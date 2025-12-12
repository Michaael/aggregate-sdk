package com.tibbo.aggregate.common.binding;

import static com.tibbo.aggregate.common.structure.OriginKind.EXPRESSION;
import static com.tibbo.aggregate.common.structure.OriginKind.REFERENCE;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.WeakHashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import javax.annotation.Nullable;

import com.google.common.annotations.VisibleForTesting;
import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.Log;
import com.tibbo.aggregate.common.expression.EvaluationEnvironment;
import com.tibbo.aggregate.common.expression.EvaluationException;
import com.tibbo.aggregate.common.expression.Evaluator;
import com.tibbo.aggregate.common.expression.Reference;
import com.tibbo.aggregate.common.expression.ReferenceResolver;
import com.tibbo.aggregate.common.structure.Pinpoint;
import com.tibbo.aggregate.common.util.SyntaxErrorException;
import com.tibbo.aggregate.common.util.Util;

public class DefaultBindingProcessor implements BindingProcessor
{
  private final BindingProvider provider;

  private final Evaluator evaluator;
  
  private Timer timer;
  
  private TimerFactory timerFactory;
  
  private ExecutorService executionService;
  
  private boolean disableStartupConcurrency;
  
  private boolean shareTimer;
  
  private boolean shareConcurrency;
  
  private final List<ReferenceListener> listeners = new LinkedList<>();
  
  private final List<TimerTask> timerTasks = new LinkedList();
  
  private final Set<Future> tasks = Collections.newSetFromMap(Collections.synchronizedMap(new WeakHashMap()));
  
  private boolean stopped;
  
  private boolean enabled = true;
  
  private final CountDownLatch bindingProcessorLatch;

  private final AtomicInteger unevaluatedStartupBindingsCount = new AtomicInteger();

  private int initialStartupBindingsCount;

  private long evaluationStartTime;

  /**
   * An optional callback that can be called by the processor upon finishing of startup bindings evaluation.<ol>
   *     <li>{@code Integer} parameter is number of executed bindings</li>
   *     <li>{@code Long} parameter is milliseconds passed from the beginning of the evaluation</li>
   * </ol>
   */
  @Nullable
  private BiConsumer<Integer, Long> startupBindingsEvaluatedCallback;
  
  public DefaultBindingProcessor(BindingProvider provider, Evaluator evaluator)
  {
    this.provider = provider;
    this.evaluator = evaluator;
    bindingProcessorLatch = new CountDownLatch(1);
  }
  
  public DefaultBindingProcessor(BindingProvider provider, Evaluator evaluator, ExecutorService executionService)
  {
    this(provider, evaluator);
    this.executionService = executionService;
  }
  
  public DefaultBindingProcessor(BindingProvider provider, Evaluator evaluator, Timer timer, ExecutorService executionService)
  {
    this(provider, evaluator);
    this.executionService = executionService;
    this.timer = timer;
    shareTimer = true;
    shareConcurrency = true;
  }
  
  public DefaultBindingProcessor(BindingProvider provider, Evaluator evaluator, TimerFactory timerFactory, ExecutorService executionService)
  {
    this(provider, evaluator);
    this.executionService = executionService;
    this.timerFactory = timerFactory;
    shareTimer = true;
    shareConcurrency = true;
  }
  
  @Override
  public boolean start()
  {
    start(executionService != null && !disableStartupConcurrency);
    
    return !stopped;
  }
  
  protected void start(final boolean concurrentProcessing)
  {
    if (stopped)
    {
      throw new IllegalStateException("Cannot reuse binding processor");
    }
    
    Callable task = () -> {
      startImpl(concurrentProcessing);
      bindingProcessorLatch.countDown();
      return null;
    };
    
    if (!concurrentProcessing)
    {
      try
      {
        task.call();
      }
      catch (Exception ex)
      {
        throw new IllegalStateException(ex.getMessage(), ex);
      }
    }
    else
    {
      submit(task);
    }
  }
  
  protected void startImpl(boolean concurrentProcessing)
  {
    final Map<Binding, EvaluationOptions> bindings = provider.createBindings();
    initBindings(bindings, concurrentProcessing);
    
    if (stopped)
    {
      return;
    }
    
    provider.start();
  }
  
  @Override
  public synchronized void stop()
  {
    bindingProcessorLatch.countDown();
    
    stopped = true;
    
    if (shareTimer)
    {
      synchronized (timerTasks)
      {
        for (TimerTask task : timerTasks)
        {
          task.cancel();
        }
      }
    }
    else
    {
      if (timer != null)
      {
        timer.cancel();
      }
    }
    
    if (shareConcurrency)
    {
      for (Future future : tasks)
      {
        future.cancel(true);
      }
    }
    else
    {
      if (executionService != null)
      {
        synchronized (executionService)
        {
          executionService.shutdownNow();
        }
        
        try
        {
          if (!executionService.awaitTermination(10000, TimeUnit.MILLISECONDS))
          {
            Log.BINDINGS.warn("Execution service didn't terminate in time");
          }
        }
        catch (InterruptedException ex)
        {
          Log.BINDINGS.debug("Interrupted during execution service termination");
          executionService.shutdown();
        }
      }
    }
    
    synchronized (listeners)
    {
      for (ReferenceListener listener : listeners)
      {
        provider.removeReferenceListener(listener);
      }
    }
    
    provider.stop();
  }

  @VisibleForTesting
  public void evaluateBindingExpression(int method, Binding binding, EvaluationOptions options)
          throws EvaluationException, SyntaxErrorException, BindingException
  {
    evaluateBindingExpression(method, binding, options, new EvaluationEnvironment(), null, null);
  }
  
  protected void evaluateBindingExpression(int method, Binding binding, EvaluationOptions options,
                                           EvaluationEnvironment evaluationEnvironment, Reference cause, ChangeCache cache)
      throws EvaluationException, SyntaxErrorException, BindingException
  {
    ReferenceResolver customDefaultReferenceResolver = options.getCustomDefaultReferenceResolver();
    // If options contain a custom resolver, then it should be added either to the environment
    if (customDefaultReferenceResolver != null)
    {
      evaluationEnvironment.addCustomDefaultResolver(customDefaultReferenceResolver);
    }

    if (checkCondition(options, evaluationEnvironment, evaluator))
    {

      options.obtainPinpoint().ifPresent(pinpoint -> {                // to make further execution observable
        evaluationEnvironment.assignPinpoint(pinpoint.withOriginField(Bindings.FIELD_EXPRESSION, EXPRESSION));
      });

      Pinpoint targetPinpoint = options.obtainPinpoint()
          .map(pinpoint -> pinpoint.withOriginField(Bindings.FIELD_TARGET, REFERENCE))
          .orElse(null);

      Object result = evaluator.evaluate(binding.getExpression(), evaluationEnvironment);
      provider.processExecution(method, binding, options, cause, result);
      writeReference(method, binding, cause, result, cache, targetPinpoint);
    }
    else if (method == EvaluationOptions.STARTUP)
    {
      Log.BINDINGS.debug("Condition '" + options.getCondition() + "' is false for binding: " + binding);
    }
  }
  
  protected void initBinding(Binding binding, EvaluationOptions options)
  {
    try
    {
      if (stopped)
      {
        return;
      }
      
      Log.BINDINGS.debug("Initializing binding: " + binding + ", evaluation options: " + options);
      
      if ((options.getPattern() & EvaluationOptions.EVENT) != 0)
      {
        if (options.getActivator() != null)
        {
          try
          {
            addReferenceListener(binding, options, options.getActivator());
          }
          catch (Exception ex)
          {
            provider.processError(binding, EvaluationOptions.EVENT, options.getActivator(), ex);
          }
        }
        else
        {
          List<Reference> identifiers = provider.getReferences(binding);
          List<Reference> added = new LinkedList<>();
          
          for (Reference ref : identifiers)
          {
            try
            {
              if (!added.contains(ref))
              {
                addReferenceListener(binding, options, ref);
                added.add(ref);
              }
              else
              {
                Log.BINDINGS.debug("Adding reference listener was skipped; reference: " + ref + "; binding: " + binding);
              }
            }
            catch (Exception ex)
            {
              provider.processError(binding, EvaluationOptions.EVENT, ref, ex);
            }
          }
        }
      }
      
      if ((options.getPattern() & EvaluationOptions.PERIODIC) != 0 && options.getPeriod() > 0)
      {
        ensureTimer();
        if (!stopped)
        {
          final EvaluationTimerTask task = new EvaluationTimerTask(binding, options);
          timer.schedule(task, options.getPeriod(), options.getPeriod());
          if (shareTimer)
          {
            // timerTasks list manipulation is always synchronized using processor as a mutex
            synchronized (this)
            {
              timerTasks.add(task);
            }
          }
        }
      }
    }
    catch (Exception ex)
    {
      Log.BINDINGS.warn("Error initializing binding: " + binding, ex);
    }
  }


  private void initBindings(final Map<Binding, EvaluationOptions> bindings, final boolean concurrentProcessing)
  {
    for (Binding bin : bindings.keySet())
    {
      initBinding(bin, bindings.get(bin));
    }
    
    evaluateStartupBindings(bindings, concurrentProcessing);
  }
  
  private void evaluateStartupBindings(Map<Binding, EvaluationOptions> bindings, boolean concurrentProcessing)
  {
    if (stopped || !enabled)
    {
      return;
    }

    evaluationStartTime = System.currentTimeMillis();
    initialStartupBindingsCount = countStartupBindings(bindings);
    if (initialStartupBindingsCount == 0)
    {
      Log.BINDINGS.debug("No startup bindings found for current processor");
      if (startupBindingsEvaluatedCallback != null)
      {
        long evaluationTime = System.currentTimeMillis() - evaluationStartTime;
        startupBindingsEvaluatedCallback.accept(initialStartupBindingsCount, evaluationTime);
      }
      return;
    }

    unevaluatedStartupBindingsCount.set(initialStartupBindingsCount);

    for (final Binding binding : bindings.keySet())
    {
      final EvaluationOptions options = bindings.get(binding);

      if (enabled && (options.getPattern() & EvaluationOptions.STARTUP) != 0)
      {
        if (executionService != null && concurrentProcessing)
        {
          try
          {
            submit(() -> {
              evaluateStartupBinding(binding, options);
              return null;
            });
          }
          catch (Exception ex)
          {
            provider.processError(binding, EvaluationOptions.STARTUP, null,
                    new BindingException(Cres.get().getString("binBindingQueueOverflow"), ex));
          }
        }
        else
        {
          evaluateStartupBinding(binding, options);
        }
      }
    }
  }

  private int countStartupBindings(Map<Binding, EvaluationOptions> bindings)
  {
    int count = 0;
    for (final Binding binding : bindings.keySet())
    {
      if ((bindings.get(binding).getPattern() & EvaluationOptions.STARTUP) != 0)
        count++;
    }
    return count;
  }

  private void evaluateStartupBinding(Binding binding, EvaluationOptions options)
  {
    try
    {
      evaluateBindingExpression(EvaluationOptions.STARTUP, binding, options);
    }
    catch (Exception ex)
    {
      provider.processError(binding, EvaluationOptions.STARTUP, null, ex);
    }
    finally
    {
      int current = unevaluatedStartupBindingsCount.decrementAndGet();
      if (current == 0 && startupBindingsEvaluatedCallback != null)
      {
        try
        {
          long evaluationTime = System.currentTimeMillis() - evaluationStartTime;
          startupBindingsEvaluatedCallback.accept(initialStartupBindingsCount, evaluationTime);
        }
        catch (Exception e)
        {
          provider.processError(binding, EvaluationOptions.STARTUP, null, e);
        }
      }
    }
  }
  
  protected void ensureTimer()
  {
    if (timer == null)
    {
      if (timerFactory != null)
      {
        timer = timerFactory.createTimer();
      }
      else
      {
        timer = new Timer("Timer/DefaultBindingProcessor");
      }
    }
  }
  
  private void addReferenceListener(Binding binding, EvaluationOptions options, Reference reference) throws BindingException
  {
    if (stopped)
    {
      return;
    }
    
    if (Reference.SCHEMA_ENVIRONMENT.equals(reference.getSchema()))
      return;
    
    ReferenceListener<?> listener = new BindingReferenceListener(binding, options);
    provider.addReferenceListener(reference, listener);
    
    synchronized (listeners)
    {
      listeners.add(listener);
    }
  }
  
  private void writeReference(int method, Binding binding, Reference cause, Object value, ChangeCache cache,
      Pinpoint pinpoint) throws BindingException
  {
    if (stopped || !enabled)
    {
      return;
    }
    
    provider.writeReference(method, binding, cause, value, cache, pinpoint);
  }
  
  protected boolean checkCondition(EvaluationOptions options, EvaluationEnvironment evaluationEnvironment, Evaluator evaluator) throws BindingException
  {
    try
    {
      boolean conditionIsAbsent = (options == null)
          || (options.getCondition() == null)
          || (options.getCondition().getText() == null)
          || options.getCondition().getText().isEmpty();
      
      if (conditionIsAbsent)
      {
        return true;
      }

      options.obtainPinpoint().ifPresent(pinpoint -> {                // to make further execution observable
        evaluationEnvironment.assignPinpoint(pinpoint.withOriginField(Bindings.FIELD_CONDITION, EXPRESSION));
      });

      Object condition = evaluator.evaluate(options.getCondition(), evaluationEnvironment);

      return condition != null ? Util.convertToBoolean(condition, true, false) : true;
    }
    catch (Exception e)
    {
      throw new BindingException(e.getMessage(), e);
    }
    finally
    {
      evaluationEnvironment.removePinpoint();    // to allow env re-using for expression evaluation
    }
  }
  
  public boolean isStopped()
  {
    return stopped;
  }
  
  public void setStartupBindingsEvaluatedCallback(@Nullable BiConsumer<Integer, Long> startupBindingsEvaluatedCallback)
  {
    this.startupBindingsEvaluatedCallback = startupBindingsEvaluatedCallback;
  }
  
  private class EvaluationTimerTask extends TimerTask
  {
    private final Binding binding;
    private final EvaluationOptions options;
    
    public EvaluationTimerTask(Binding binding, EvaluationOptions options)
    {
      this.binding = binding;
      this.options = options;
    }
    
    @Override
    public void run()
    {
      Callable task = () -> {
        executeEvaluationTask();
        return null;
      };
      
      try
      {
        submit(task);
      }
      catch (Throwable ex)
      {
        provider.processError(binding, EvaluationOptions.PERIODIC, null, new BindingException(Cres.get().getString("binBindingQueueOverflow"), ex));
      }
    }
    
    public void executeEvaluationTask()
    {
      if (stopped || !enabled)
      {
        return;
      }
      try
      {
        evaluateBindingExpression(EvaluationOptions.PERIODIC, binding, options);
      }
      catch (Exception ex)
      {
        provider.processError(binding, EvaluationOptions.PERIODIC, null, ex);
      }
    }
  }
  
  private class BindingReferenceListener implements ReferenceListener<Object>
  {
    private final Binding binding;
    
    private Object content;
    
    private final EvaluationOptions options;
    
    public BindingReferenceListener(Binding binding, EvaluationOptions options)
    {
      this.binding = binding;
      this.options = options;
    }
    
    @Override
    public void referenceChanged(final Reference cause, final Map<String, Object> environment, ChangeCache cache) throws BindingException
    {
      referenceChanged(cause, environment, cache, null);
    }
    
    @Override
    public void referenceChanged(final Reference cause, final Map<String, Object> environment, ChangeCache cache, boolean asynchronousProcessing) throws BindingException
    {
      referenceChanged(cause, environment, cache, Boolean.valueOf(asynchronousProcessing));
    }
    
    private void referenceChanged(final Reference cause, final Map<String, Object> environment, final ChangeCache cache, Boolean asynchronousProcessing) throws BindingException
    {
      if (stopped || !enabled)
      {
        return;
      }
      
      boolean async = executionService != null && (asynchronousProcessing == null || asynchronousProcessing);
      
      if (async)
      {
        Callable task = () -> {
          try
          {
            processReferenceChange(cause, environment, cache);
          }
          catch (BindingException ex)
          {
            provider.processError(binding, EvaluationOptions.EVENT, cause, ex);
          }

          return null;
        };
        
        try
        {
          submit(task);
        }
        catch (Exception ex)
        {
          provider.processError(binding, EvaluationOptions.EVENT, null, new BindingException(Cres.get().getString("binBindingQueueOverflow"), ex));
        }
      }
      else
      {
        processReferenceChange(cause, environment, cache);
      }
    }
    
    private void processReferenceChange(Reference cause, Map<String, Object> environment, ChangeCache cache) throws BindingException
    {
      if (stopped || !enabled)
      {
        return;
      }
      
      EvaluationEnvironment evaluationEnvironment = new EvaluationEnvironment(cause, environment);
      try
      {
        evaluateBindingExpression(EvaluationOptions.EVENT, binding, options, evaluationEnvironment, cause, cache);
      }
      catch (Exception ex)
      {
        throw new BindingException(ex.getMessage(), ex);
      }
    }
    
    @Override
    public Binding getBinding()
    {
      return binding;
    }
    
    @Override
    public void setContent(Object content)
    {
      this.content = content;
    }
    
    @Override
    public Object getContent()
    {
      return content;
    }
    
    @Override
    public BindingProcessor getBindingProcessor()
    {
      return DefaultBindingProcessor.this;
    }
    
    @Override
    public EvaluationOptions getEvaluationOptions()
    {
      return options;
    }
    
    @Override
    public String toString()
    {
      return "[binding: " + binding + ", options: " + options + "]";
    }
  }
  
  @Override
  public void submit(Callable task)
  {
    if (stopped || !enabled)
      return;
    
    if (executionService != null)
    {
      Future future;
      synchronized (executionService)
      {
        if (executionService.isShutdown())
        {
          return;
        }
        future = executionService.submit(task);
      }
      
      if (shareConcurrency)
      {
        tasks.add(future);
      }
    }
    else
    {
      try
      {
        task.call();
      }
      catch (Exception ex)
      {
        throw new IllegalStateException(ex.getMessage(), ex);
      }
    }
  }
  
  public void setExecutionService(ExecutorService service)
  {
    if (executionService != null)
    {
      executionService.shutdown();
      try
      {
        executionService.awaitTermination(10000, TimeUnit.MILLISECONDS);
      }
      catch (InterruptedException e)
      {
        Log.BINDINGS.warn("Error terminating bindings execution service", e);
      }
    }
    
    executionService = service;
  }
  
  public boolean isEnabled()
  {
    return enabled;
  }
  
  @Override
  public void setEnabled(boolean enabled)
  {
    this.enabled = enabled;
  }
  
  @Override
  public ExecutorService getExecutorService()
  {
    return executionService;
  }
  
  public BindingProvider getProvider()
  {
    return provider;
  }
  
  public Evaluator getEvaluator()
  {
    return evaluator;
  }
  
  @Override
  public Timer getTimer()
  {
    return timer;
  }
  
  public boolean isDisableStartupConcurrency()
  {
    return disableStartupConcurrency;
  }

  public void setDisableStartupConcurrency(boolean disableStartupConcurrency)
  {
    this.disableStartupConcurrency = disableStartupConcurrency;
  }
  
  public boolean awaitBindingsProcessorStart(long millis)
  {
    try
    {
      return bindingProcessorLatch.await(millis, TimeUnit.MILLISECONDS);
    }
    catch (InterruptedException ex)
    {
      Log.BINDINGS.error("Couldn't await CountDownLatch bindingsProcessor", ex);
      return false;
    }
  }
}
