package com.tibbo.aggregate.common.security;

import com.tibbo.aggregate.common.Log;
import com.tibbo.aggregate.common.context.*;
import com.tibbo.aggregate.common.datatable.DataRecord;
import com.tibbo.aggregate.common.datatable.DataTable;
import com.tibbo.aggregate.common.server.RootContextConstants;

public class AuthUtils
{
  public static String getCorrectUsername(String username, ContextManager contextManager, CallerController callerController)
  {
    if (contextManager == null || callerController == null)
      return username;
    
    Context rootContext = contextManager.getRoot();
    FunctionDefinition fd = rootContext.getFunctionDefinition(RootContextConstants.F_SESSION_GET);
    if (fd == null)
      return username;
    
    DataRecord parameters = new DataRecord(fd.getInputFormat()).addString(RootContextConstants.V_SESSION_USER_NAME);
    try
    {
      DataTable result = rootContext.callFunction(fd.getName(), callerController, parameters.wrap());
      if (result != null && result.getRecordCount() > 0)
      {
        username = result.rec().getString(RootContextConstants.V_SESSION_DEFAULT_VALUE);
      }
    }
    catch (ContextException e)
    {
      Log.SECURITY.warn(e.getMessage(), e);
    }
    
    return username;
  }
}
