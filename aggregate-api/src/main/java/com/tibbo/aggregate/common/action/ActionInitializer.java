package com.tibbo.aggregate.common.action;

import java.util.*;

import com.tibbo.aggregate.common.context.*;
import com.tibbo.aggregate.common.datatable.*;
import com.tibbo.aggregate.common.util.*;

public interface ActionInitializer
{
  ActionIdentifier initAction(Context context, String actionName, ServerActionInput initialParameters, DataTable inputData, Map<String, Object> environment, ActionExecutionMode mode,
      CallerController callerController, ErrorCollector collector) throws ContextException;
}
