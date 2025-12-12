package com.tibbo.aggregate.common.action;

public interface Action<I extends InitialRequest, C extends ActionCommand, R extends ActionResponse>
{
  void init(ActionContext actionContext, I initialParameters);
  
  ActionResult destroy();
  
  C service(R actionRequest);
}
