package com.tibbo.aggregate.common.event;

public class ContextEventListenerInfo
{
  private final ContextEventListener listener;
  private final boolean weak;
  
  public ContextEventListenerInfo(ContextEventListener listener, boolean weak)
  {
    super();
    this.listener = listener;
    this.weak = weak;
  }
  
  public ContextEventListener getListener()
  {
    return listener;
  }
  
  public boolean isWeak()
  {
    return weak;
  }
}