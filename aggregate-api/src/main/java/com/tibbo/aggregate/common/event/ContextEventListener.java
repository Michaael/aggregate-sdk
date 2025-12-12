package com.tibbo.aggregate.common.event;

import com.tibbo.aggregate.common.context.*;
import com.tibbo.aggregate.common.data.*;
import com.tibbo.aggregate.common.expression.*;

public interface ContextEventListener
{
  /**
   * Should return true if event should be handled.
   */
  boolean shouldHandle(Event ev) throws EventHandlingException;
  
  /**
   * Handles the event
   */
  void handle(Event event) throws EventHandlingException;

  void handle(Event event, EventDefinition ed) throws EventHandlingException;
  
  CallerController getCallerController();
  
  Integer getListenerCode();
  
  Expression getFilter();
  
  String getFingerprint();
  
  boolean isAsync();
  
  void setListenerCode(Integer listenerCode);
}
