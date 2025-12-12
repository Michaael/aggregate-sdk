package com.tibbo.aggregate.common.action.command;

import com.tibbo.aggregate.common.action.*;
import com.tibbo.aggregate.common.datatable.*;
import com.tibbo.aggregate.common.util.*;

public class OpenGridDashboard extends GridDashboardActionCommand
{
  
  public OpenGridDashboard(String contextPath, String defaultContext, WebWindowLocation location)
  {
    super(ActionUtils.CMD_OPEN_GRID_DASHBOARD, null, contextPath, defaultContext, location);

    setTitle(evaluateTitle(contextPath));
  }
  
  public OpenGridDashboard(String title, DataTable parameters)
  {
    super(ActionUtils.CMD_OPEN_GRID_DASHBOARD, title, parameters);
  }
  
  public OpenGridDashboard()
  {
    super(ActionUtils.CMD_OPEN_GRID_DASHBOARD, null, null, null, null);
  }

  protected String evaluateTitle(String contextPath) {
    int last = 0;
    if (contextPath != null && (last = contextPath.lastIndexOf(".")) != -1) {
      return contextPath.substring(last + 1);
    }

    return contextPath;
  }
}
