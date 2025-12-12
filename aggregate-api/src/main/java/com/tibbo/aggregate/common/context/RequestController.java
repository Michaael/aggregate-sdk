package com.tibbo.aggregate.common.context;

import com.tibbo.aggregate.common.expression.Evaluator;
import com.tibbo.aggregate.common.structure.PinpointAware;

public interface RequestController extends PinpointAware
{
  Originator getOriginator();
  
  Long getLockTimeout();
  
  Evaluator getEvaluator();
  
  String getQueue();
  
  boolean isReplyRequired();
  
  boolean isLoggerRequest();

}
