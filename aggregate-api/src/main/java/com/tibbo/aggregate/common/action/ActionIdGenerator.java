package com.tibbo.aggregate.common.action;

import java.util.UUID;

public class ActionIdGenerator
{
  public ActionIdGenerator()
  {
    super();
  }
  
  public ActionIdentifier generate(Action action)
  {
    String id = action.getClass().getSimpleName() + "@" + System.identityHashCode(action) + ":" + UUID.randomUUID();

    return new ActionIdentifier(id);
  }
}
