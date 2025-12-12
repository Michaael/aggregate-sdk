package com.tibbo.aggregate.common.event;

import com.tibbo.aggregate.common.data.*;

public interface EventWriter
{
  boolean shouldWrite(Event ev);
  
  void write(Event ev) throws EventHandlingException;
  
  void terminate();
}
