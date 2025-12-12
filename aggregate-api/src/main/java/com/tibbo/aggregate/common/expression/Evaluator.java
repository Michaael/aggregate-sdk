package com.tibbo.aggregate.common.expression;

import static java.lang.String.format;

import java.awt.Color;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicLong;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.context.CallerController;
import com.tibbo.aggregate.common.context.Context;
import com.tibbo.aggregate.common.context.ContextException;
import com.tibbo.aggregate.common.context.ContextManager;
import com.tibbo.aggregate.common.data.Data;
import com.tibbo.aggregate.common.datatable.DataTable;
import com.tibbo.aggregate.common.datatable.DataTableUtils;
import com.tibbo.aggregate.common.expression.parser.ASTStart;
import com.tibbo.aggregate.common.expression.util.Tracer;
import com.tibbo.aggregate.common.structure.Pinpoint;
import com.tibbo.aggregate.common.util.DateUtils;
import com.tibbo.aggregate.common.util.SyntaxErrorException;
import com.tibbo.aggregate.common.util.Util;

public class Evaluator
{
  /**
   * The key in the {@link #resolvers} map that is used to hold the default reference resolver. This is the most
   * convenient key for that resolver because it has no schema and corresponding references'
   * {@link Reference#getSchema() getSchema()} method naturally returns {@code null}.
   */
  static final String DEFAULT_RESOLVER_KEY = null;

  private static final String ENVIRONMENT_PREVIOUS = "previous";
  private static final String ENVIRONMENT_COUNT = "count";
  
  private final EnvironmentReferenceResolver environmentResolver = new LocalEnvironmentResolver();
  
  private final Map<String, ReferenceResolver> resolvers = new HashMap<>();
  
  private final Map<String, Function> customFunctions = new LinkedHashMap<>();
  
  private Tracer tracer;
  
  private long count = 0;
  private Object previousResult;
  private boolean keepPreviousResult = false;
  private boolean loggerRequest;
  
  public Evaluator(ContextManager cm, CallerController caller)
  {
    this(cm, null, null, caller);
  }
  
  public Evaluator(ContextManager cm, CallerController caller, boolean loggerRequest)
  {
    this(cm, null, null, caller);
    this.loggerRequest = loggerRequest;
  }
  
  public Evaluator(DataTable defaultTable)
  {
    this(null, null, defaultTable, null);
  }
  
  public Evaluator(ContextManager cm, Context defaultContext, DataTable defaultTable, CallerController caller)
  {
    DefaultReferenceResolver resolver = new DefaultReferenceResolver();
    resolver.setContextManager(cm);
    resolver.setCallerController(caller);
    resolver.setDefaultContext(defaultContext);
    resolver.setDefaultTable(defaultTable);
    init(resolver);
  }
  
  public Evaluator(ReferenceResolver resolver)
  {
    init(resolver);
  }

  private void init(ReferenceResolver defaultResolver)
  {
    defaultResolver.setEvaluator(this);
    
    resolvers.put(DEFAULT_RESOLVER_KEY, defaultResolver);
    
    setResolver(Reference.SCHEMA_ENVIRONMENT, environmentResolver);
  }
  
  public Object evaluate(Expression expression) throws SyntaxErrorException, EvaluationException
  {
    return evaluate(expression, null);
  }
  
  public Object evaluate(Expression expression, EvaluationEnvironment environment) throws SyntaxErrorException, EvaluationException
  {
    return evaluate(expression, environment, null, null, false);
  }
  
  public Object evaluate(Expression expression, EvaluationPoint point, Reference holder) throws SyntaxErrorException, EvaluationException
  {
    return evaluate(expression, null, point, holder, false);
  }
  
  public Object evaluate(Expression expression, EvaluationEnvironment environment, EvaluationPoint point, Reference holder) throws SyntaxErrorException, EvaluationException
  {
    return evaluate(expression, environment, point, holder, false);
  }
  
  public AttributedObject evaluateAttributed(Expression expression) throws SyntaxErrorException, EvaluationException
  {
    return evaluateAttributed(expression, null);
  }
  
  public AttributedObject evaluateAttributed(Expression expression, EvaluationEnvironment environment) throws SyntaxErrorException, EvaluationException
  {
    Object result = evaluate(expression, environment, null, null, true);
    
    return ExpressionUtils.toAttributed(result);
  }
  
  private Object evaluate(Expression expression, EvaluationEnvironment environment, EvaluationPoint point, Reference holder, boolean attributed) throws SyntaxErrorException, EvaluationException
  {
    try
    {
      if ((expression == null) || (expression.getText().isEmpty()))
      {
        return null;
      }
      
      if (environment == null)
      {
        environment = new EvaluationEnvironment();
      }
      
      if (point != null)
      {
        environment.setDebug(point.isDebuggingEvaluations());
      }

      if (expression.obtainPinpoint().isPresent())
      {
        Pinpoint expressionPinpoint = expression.obtainPinpoint().get();
        if (environment.obtainPinpoint().isPresent())
        {
          throw new IllegalStateException(format("Environment '%s' already has pinpoint '%s' but attempted to have " +
              "another one '%s' from expression '%s'", environment, environment.obtainPinpoint().get(),
              expressionPinpoint, expression));
        }
        environment.assignPinpoint(expressionPinpoint);
      }

      ASTStart root;
      
      synchronized (expression)
      {
        root = expression.getRootNode();
        
        if (root == null)
        {
          root = ExpressionUtils.parse(expression, true);
          EvaluationStatistics.onExpressionParsed();
          expression.setRootNode(root);
        }
      }
      
      DefaultEvaluatingVisitor visitor = new DefaultEvaluatingVisitor(this);
      
      root.jjtAccept(visitor, environment);
      Object result = visitor.getResult();
      
      if (!attributed && result instanceof AttributedObject)
      {
        result = ((AttributedObject) result).getValue();
      }
      
      if (keepPreviousResult)
      {
        previousResult = result;
      }
      
      if (point != null)
      {
        point.processEvaluation(this, expression, holder, result, environment.getRootNode());
      }
      EvaluationStatistics.onExpressionEvaluated();
      return result;
    }
    catch (SyntaxErrorException ex)
    {
      if (point != null)
      {
        point.processEvaluationError(this, expression, holder, ex, environment.getRootNode());
      }
      
      throw ex;
    }
    catch (Exception ex)
    {
      if (point != null)
      {
        point.processEvaluationError(this, expression, holder, ex, environment.getRootNode());
      }
      EvaluationStatistics.onErrorGenerated();
      throw new EvaluationException(MessageFormat.format(Cres.get().getString("exprErrEvaluatingExpr"), expression) + ex.getMessage(), ex);
    }
    finally
    {
      count++;
    }
  }
  
  public String evaluateToString(Expression expression) throws SyntaxErrorException, EvaluationException
  {
    return evaluateToString(expression, null, null);
  }
  
  public String evaluateToString(Expression expression, EvaluationPoint point, Reference holder) throws SyntaxErrorException, EvaluationException
  {
    Object result = evaluate(expression, null, point, holder);
    
    return result != null ? result.toString() : "";
  }
  
  public Data evaluateToData(Expression expression) throws SyntaxErrorException, EvaluationException
  {
    Object result = evaluate(expression);
    
    return result != null ? (Data) result : null;
  }
  
  public String evaluateToStringOrNull(Expression expression) throws SyntaxErrorException, EvaluationException
  {
    Object result = evaluate(expression);
    
    return result != null ? result.toString() : null;
  }
  
  public Boolean evaluateToBoolean(Expression expression, EvaluationEnvironment environment) throws SyntaxErrorException, EvaluationException
  {
    Object result = evaluate(expression, environment);
    return Util.convertToBoolean(result, true, false);
  }

  public Boolean evaluateToBoolean(Expression expression) throws SyntaxErrorException, EvaluationException
  {
    return evaluateToBoolean(expression, null, null);
  }

  public Boolean evaluateToBoolean(Expression expression, EvaluationPoint point, Reference holder) throws SyntaxErrorException, EvaluationException
  {
    Object result = evaluate(expression, point, holder);
    return Util.convertToBoolean(result, true, false);
  }
  
  public Boolean evaluateToBooleanOrNull(Expression expression) throws SyntaxErrorException, EvaluationException
  {
    return evaluateToBoolean(expression, true, true, null, null);
  }
  
  public Boolean evaluateToBoolean(Expression expression, boolean validate, boolean allowNull, EvaluationPoint point, Reference holder) throws SyntaxErrorException, EvaluationException
  {
    Object result = evaluate(expression, point, holder);
    return Util.convertToBoolean(result, validate, allowNull);
  }
  
  public Number evaluateToNumber(Expression expression, EvaluationPoint point, Reference holder) throws SyntaxErrorException, EvaluationException
  {
    return evaluateToNumber(expression, false, false, point, holder);
  }
  
  public Number evaluateToNumber(Expression expression) throws SyntaxErrorException, EvaluationException
  {
    return evaluateToNumber(expression, false, false);
  }
  
  public Number evaluateToNumber(Expression expression, boolean validate, boolean allowNull) throws SyntaxErrorException, EvaluationException
  {
    return evaluateToNumber(expression, validate, allowNull, null, null);
  }
  
  public Number evaluateToNumber(Expression expression, boolean validate, boolean allowNull, EvaluationPoint point, Reference holder) throws SyntaxErrorException, EvaluationException
  {
    Object result = evaluate(expression, point, holder);
    
    return Util.convertToNumber(result, validate, allowNull);
  }
  
  public Comparable evaluateToComparable(Expression expression) throws SyntaxErrorException, EvaluationException
  {
    Object result = evaluate(expression);
    return result instanceof Comparable ? (Comparable) result : result.toString();
  }
  
  public DataTable evaluateToDataTable(Expression expression) throws SyntaxErrorException, EvaluationException
  {
    return evaluateToDataTable(expression, new EvaluationEnvironment());
  }
  
  public DataTable evaluateToDataTable(Expression expression, EvaluationEnvironment environment) throws SyntaxErrorException, EvaluationException
  {
    AttributedObject result = evaluateAttributed(expression, environment);
    
    if (result.getValue() != null && result.getValue() instanceof DataTable)
    {
      return (DataTable) result.getValue();
    }
    
    DataTable data = DataTableUtils.wrapToTable(Collections.singletonList(result.getValue()));
    
    ExpressionUtils.copyAttributes(result, data);
    
    return data;
  }
  
  public Date evaluateToDate(Expression expression) throws SyntaxErrorException, EvaluationException
  {
    Object result = evaluate(expression);
    
    if (result == null)
    {
      return null;
    }
    
    if (result instanceof Date)
    {
      return (Date) result;
    }
    
    if (result instanceof Number)
    {
      return new Date(((Number) result).longValue());
    }
    
    try
    {
      return DateUtils.createDateFormatter().parse(result.toString());
    }
    catch (ParseException ex)
    {
      throw new EvaluationException(ex.getMessage(), ex);
    }
  }
  
  public Color evaluateToColor(Expression expression) throws SyntaxErrorException, EvaluationException
  {
    return evaluateToColor(expression, null, null);
  }
  
  public Color evaluateToColor(Expression expression, EvaluationPoint point, Reference holder) throws SyntaxErrorException, EvaluationException
  {
    Object result = evaluate(expression, point, holder);
    
    if (result == null)
    {
      return null;
    }
    
    if (result instanceof Color)
    {
      return (Color) result;
    }
    
    return new Color(Util.convertToNumber(result, false, false).intValue());
  }
  
  public ReferenceResolver getResolver(String schema)
  {
    return resolvers.get(schema);
  }
  
  public ReferenceResolver getDefaultResolver()
  {
    return resolvers.get(DEFAULT_RESOLVER_KEY);
  }
  
  public void setResolver(String schema, ReferenceResolver resolver)
  {
    resolvers.put(schema, resolver);
  }
  
  public Map<String, ReferenceResolver> getResolvers()
  {
    return resolvers;
  }
  
  public Object getPreviousResult()
  {
    return previousResult;
  }
  
  public void setPreviousResult(Object previousResult)
  {
    keepPreviousResult = true;
    this.previousResult = previousResult;
  }
  
  public void registerCustomFunction(String name, Function impl)
  {
    if (AbstractEvaluatingVisitor.DEFAULT_FUNCTIONS.containsKey(name) || customFunctions.containsKey(name))
    {
      throw new IllegalArgumentException("Function already registered:" + name);
    }
    
    customFunctions.put(name, impl);
  }
  
  public Map<String, Function> getAllFunctions()
  {
    Map<String, Function> res = new LinkedHashMap<>(AbstractEvaluatingVisitor.DEFAULT_FUNCTIONS);
    res.putAll(customFunctions);
    return res;
  }
  
  public Function getCustomFunction(String name)
  {
    return customFunctions.get(name);
  }
  
  public EnvironmentReferenceResolver getEnvironmentResolver()
  {
    return environmentResolver;
  }
  
  public Tracer getTracer()
  {
    return tracer;
  }
  
  public void setTracer(Tracer tracer)
  {
    this.tracer = tracer;
  }
  
  public void restorePreviousResult(Object previousResult)
  {
    this.previousResult = previousResult;
  }
  
  public boolean isLoggerRequest()
  {
    return loggerRequest;
  }
  
  private class LocalEnvironmentResolver extends EnvironmentReferenceResolver
  {
    @Override
    public Object resolveReference(Reference ref, EvaluationEnvironment environment) throws SyntaxErrorException, ContextException, EvaluationException
    {
      if (Util.equals(ENVIRONMENT_PREVIOUS, ref.getField()))
      {
        if (keepPreviousResult)
        {
          return previousResult;
        }
        else
        {
          throw new EvaluationException("Previous result was not keep because the were no references to 'count'");
        }
      }
      
      if (Util.equals(ENVIRONMENT_COUNT, ref.getField()))
      {
        keepPreviousResult = true;
        return count;
      }
      
      return super.resolveReference(ref, environment);
    }
  }
  
  public void setDefaultTable(DataTable oldValue)
  {
    this.getDefaultResolver().setDefaultTable(oldValue);
  }
  
  public void setDefaultContext(Context aContext)
  {
    this.getDefaultResolver().setDefaultContext(aContext);
  }
  
  public ExecutorService getExecutorService()
  {
    ReferenceResolver defaultResolver = getDefaultResolver();
    if (defaultResolver != null)
    {
      ContextManager contextManager = defaultResolver.getContextManager();
      if (contextManager != null)
      {
        return contextManager.getExecutorService();
      }
    }
    return null;
  }
  
  public static class EvaluationStatistics
  {
    
    public static final AtomicLong EXPRESSIONS_PARSED = new AtomicLong(0L);
    public static final AtomicLong EXPRESSIONS_EVALUATED = new AtomicLong(0L);
    public static final AtomicLong ERRORS_GENERATED = new AtomicLong(0L);
    public static final AtomicLong ERRORS_CAUGHT = new AtomicLong(0L);
    public static final AtomicLong REFERENCES_PROCESSED = new AtomicLong(0L);
    public static final Map<String, AtomicLong> FUNCTIONS_CALLED = new ConcurrentHashMap<>();
    
    public static void onReferenceProcessed()
    {
      REFERENCES_PROCESSED.incrementAndGet();
    }
    
    public static void onErrorCatch()
    {
      ERRORS_CAUGHT.incrementAndGet();
      ERRORS_GENERATED.incrementAndGet();
    }
    
    public static void onFunctionCall(String description)
    {
      FUNCTIONS_CALLED.compute(description, (name, counter) -> {
        if (counter == null)
        {
          counter = new AtomicLong(1L);
        }
        else
        {
          counter.incrementAndGet();
        }
        return counter;
      });
    }
    
    public static void onExpressionEvaluated()
    {
      EXPRESSIONS_EVALUATED.incrementAndGet();
    }
    
    public static void onErrorGenerated()
    {
      ERRORS_GENERATED.incrementAndGet();
    }
    
    public static void onExpressionParsed()
    {
      EXPRESSIONS_PARSED.incrementAndGet();
    }
  }
}
