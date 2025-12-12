package com.tibbo.aggregate.common.util;

import java.lang.reflect.*;

import javax.swing.*;

public abstract class AggreGateSwingWorker<T> implements Runnable
{
  private T value;
  
  public AggreGateSwingWorker()
  {
    super();
  }
  
  public T start()
  {
    if (SwingUtilities.isEventDispatchThread())
    {
      run();
    }
    else
    {
      SwingUtilities.invokeLater(AggreGateSwingWorker.this);
    }
    
    return value;
  }
  
  public T execute()
  {
    try
    {
      return executeWithExceptions();
    }
    catch (InvocationTargetException ex)
    {
      throw new IllegalStateException(ex.getCause().getMessage(), ex.getCause());
    }
    catch (Exception ex)
    {
      throw new IllegalStateException(ex.getMessage(), ex);
    }
  }
  
  public T executeWithExceptions() throws InterruptedException, InvocationTargetException
  {
    if (SwingUtilities.isEventDispatchThread())
    {
      run();
    }
    else
    {
      SwingUtilities.invokeAndWait(AggreGateSwingWorker.this);
    }
    
    return value;
  }
  
  public abstract void run();
  
  protected void set(T value)
  {
    this.value = value;
  }
}
