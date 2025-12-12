package com.tibbo.aggregate.common.action.command;

import com.tibbo.aggregate.common.action.*;
import com.tibbo.aggregate.common.datatable.*;

public class EditGridDashboard extends GridDashboardActionCommand
{
  
  public EditGridDashboard(String contextPath, String defaultContext)
  {
    super(ActionUtils.CMD_EDIT_GRID_DASHBOARD, null, contextPath, defaultContext, null);
  }
  
  public EditGridDashboard(String title, DataTable parameters)
  {
    super(ActionUtils.CMD_EDIT_GRID_DASHBOARD, title, parameters);
  }
  
  public EditGridDashboard()
  {
    super(ActionUtils.CMD_EDIT_GRID_DASHBOARD, null, null, null, null);
  }
}
