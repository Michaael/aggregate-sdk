package com.tibbo.aggregate.common.util;

import com.tibbo.aggregate.common.action.*;
import com.tibbo.aggregate.common.context.*;
import com.tibbo.aggregate.common.datatable.*;
import com.tibbo.aggregate.common.expression.*;

public interface ReferredActionExecutor
{
  public boolean executeReferredAction(Reference ref, RemoteConnector connector, Context defaultContext, DataTable parameters);
  
  public boolean executeReferredActionWithInterceptor(Reference ref, RemoteConnector connector, Context defaultContext, DataTable parameters, StepActionInterceptor interceptor);
}