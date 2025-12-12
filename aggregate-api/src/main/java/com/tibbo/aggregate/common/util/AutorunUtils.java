package com.tibbo.aggregate.common.util;

import com.tibbo.aggregate.common.Log;
import com.tibbo.aggregate.common.context.*;
import com.tibbo.aggregate.common.datatable.DataRecord;
import com.tibbo.aggregate.common.datatable.DataTable;
import com.tibbo.aggregate.common.server.LoginServerContextConstants;
import com.tibbo.aggregate.common.server.RootContextConstants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AutorunUtils
{
  public static List<Context> getAutorunContexts(Context rootContext, CallerController callerController, ContextManager contextManager, String username)
  {
    FunctionDefinition fd = rootContext.getFunctionDefinition(LoginServerContextConstants.F_UPDATE_AUTORUN, callerController);
    if (fd != null)
    {
      try
      {
        DataTable contexts = rootContext.callFunction(LoginServerContextConstants.F_UPDATE_AUTORUN, callerController);
        List<Context> result = new ArrayList<>();
        for (DataRecord record : contexts)
        {
          result.add(rootContext.get(record.getString(LoginServerContextConstants.V_PATH), callerController));
        }
        if (!result.isEmpty())
          return result;
      }
      catch (ContextException e)
      {
        Log.SYSTEMTREE.warn(e.getMessage(), e);
      }
    }
    return Collections.singletonList(contextManager.get(ContextUtils.autorunActionsContextPath(username)));
  }
}
