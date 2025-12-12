package com.tibbo.aggregate.common.protocol;

import java.io.*;
import java.text.*;
import java.util.*;

import com.tibbo.aggregate.common.*;
import com.tibbo.aggregate.common.context.*;
import com.tibbo.aggregate.common.device.*;
import com.tibbo.aggregate.common.event.*;
import com.tibbo.aggregate.common.util.*;

public class RemoteContextManager extends DefaultContextManager<ProxyContext>
{
  private boolean initialized = false;
  private boolean initializing = false;
  
  private final AbstractAggreGateDeviceController controller;

  private final Map<String, List<Runnable>> deferredTasks = Collections.synchronizedMap(new HashMap<String, List<Runnable>>());

  public RemoteContextManager(AbstractAggreGateDeviceController controller, boolean async, int eventQueueLength)
  {
    super(async, eventQueueLength, null);
    this.controller = controller;
  }
  
  protected void initialize() throws ContextException, DisconnectionException, IOException, InterruptedException, RemoteDeviceErrorException
  {
    if (initialized || initializing)
    {
      return;
    }
    
    initializing = true;
    
    try
    {
      controller.connect();
    }
    finally
    {
      initializing = false;
    }
    
    initialized = true;
  }
  
  @Override
  public void stop()
  {
    initialized = false;
    
    super.stop();
  }
  
  public AbstractAggreGateDeviceController getController()
  {
    return controller;
  }
  
  protected void sendAddListener(String context, String event, ContextEventListener listener)
  {
    ProtocolVersion protocolVersion = getController().getProtocolVersion();
    if (protocolVersion == null || protocolVersion.ordinal() < ProtocolVersion.V3.ordinal())
    {
      return;
    }
    
    try
    {
      String filterText = listener.getFilter() != null ? listener.getFilter().getText() : null;
      final String fingerprint = listener.getFingerprint();
      getController().sendCommandAndCheckReplyCode(getController().getCommandBuilder().addEventListenerOperation(context, event, listener.getListenerCode(), filterText, fingerprint));
    }
    catch (Exception ex)
    {
      String msg = MessageFormat.format(Cres.get().getString("conErrAddingListener"), event, context);
      throw new IllegalStateException(msg + ": " + ex.getMessage(), ex);
    }
  }
  
  protected void sendRemoveListener(String context, String event, ContextEventListener listener)
  {
    ProtocolVersion protocolVersion = getController().getProtocolVersion();
    if (protocolVersion == null || protocolVersion.ordinal() < ProtocolVersion.V3.ordinal())
    {
      return;
    }
    
    try
    {
      String filter = listener.getFilter() != null ? listener.getFilter().getText() : null;
      String fingerprint = listener.getFingerprint();
      getController().sendCommandAndCheckReplyCode(getController().getCommandBuilder().removeEventListenerOperation(context, event, listener.getListenerCode(), filter, fingerprint));
    }
    catch (Exception ex)
    {
      String msg = MessageFormat.format(Cres.get().getString("conErrRemovingListener"), event, context);
      throw new IllegalStateException(msg + ": " + ex.getMessage(), ex);
    }
  }
  
  @Override
  protected void addListenerToContext(ProxyContext con, String event, ContextEventListener listener, boolean mask, boolean weak)
  {
    con.addEventListener(event, listener, false, !mask); // Don't sent remote command if adding as mask listener
  }
  
  @Override
  protected void removeListenerFromContext(ProxyContext con, String event, ContextEventListener listener, boolean mask)
  {
    con.removeEventListener(event, listener, !mask); // Don't sent remote command if adding as mask listener
  }
  
  @Override
  public void addMaskEventListener(String mask, String event, ContextEventListener listener)
  {
    super.addMaskEventListener(mask, event, listener);
    
    sendAddListener(mask, event, listener);
  }
  
  @Override
  public void removeMaskEventListener(String mask, String event, ContextEventListener listener)
  {
    super.removeMaskEventListener(mask, event, listener);
    
    sendRemoveListener(mask, event, listener);
  }
  
  @Override
  public void contextRemoved(ProxyContext con)
  {
    // We don't store listeners from removed contexts because server do that itself
  }

  public ProxyContext createContexts(String path)
  {
    ProxyContext cur = getRoot();
    List<String> names = StringUtils.split(path, ContextUtils.CONTEXT_NAME_SEPARATOR.charAt(0));
    for (String name : names)
    {
      ProxyContext child = (ProxyContext)cur.getChild(name);

      if (child == null)
      {
        child = cur.createChildContextProxy(name);
        cur.addChild(child);
      }

      cur = child;
    }

    return cur;
  }

  public void addDeferredTask(String path, Runnable task)
  {
    List<Runnable> tasks = deferredTasks.get(path);

    if (tasks == null)
    {
      tasks = new ArrayList<>();
    }

    tasks.add(task);

    deferredTasks.put(path, tasks);
  }

  public void executeDeferredTasks(String path)
  {
    if (deferredTasks.containsKey(path))
    {
      for (Runnable task : deferredTasks.get(path))
      {
        task.run();
      }

      deferredTasks.remove(path);
    }
  }
}
