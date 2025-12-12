package com.tibbo.aggregate.common.action;

public interface StepActionInterceptor
{
  void interceptActionResponse(GenericActionResponse actionResponse);
  
  void afterLastStep();
}
