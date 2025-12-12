package com.tibbo.aggregate.common.action;

import com.tibbo.aggregate.common.context.CallerController;
import com.tibbo.aggregate.common.context.Context;
import com.tibbo.aggregate.common.context.ContextException;

public interface LaunchDashboardAction
{

  Context getDashboardContext();
  boolean isDefaultActionSubstitutor(Context definingContext, CallerController caller) throws ContextException;
}
