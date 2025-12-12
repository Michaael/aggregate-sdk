package com.tibbo.aggregate.common.action.command;

import com.tibbo.aggregate.common.action.*;
import com.tibbo.aggregate.common.datatable.*;
import com.tibbo.aggregate.common.server.*;

public class LaunchProcessControlProgram extends LaunchWidget
{
  
  public static final TableFormat FIFT_DEBUG_PROGRAM = new TableFormat(1, 1, "<" + ProcessControlContextConstants.FIF_DEBUG_PROGRAM_PARAMETERS + "><I><A=1>");
  
  public static final TableFormat FIFT_BREAKPOINT = new TableFormat();
  
  static
  {
    FIFT_BREAKPOINT.addField(FieldFormat.create(ProcessControlContextConstants.FIF_BREAKPOINT_POSITION, FieldFormat.STRING_FIELD));
    FIFT_BREAKPOINT.addField(FieldFormat.create(ProcessControlContextConstants.FIF_BREAKPOINT_POSITION_LINE, FieldFormat.INTEGER_FIELD).setDefault(-1));
  }
  
  public LaunchProcessControlProgram()
  {
    super();
    setType(ActionUtils.CMD_LAUNCH_PROCESS_CONTROL_PROGRAM);
  }
  
  public LaunchProcessControlProgram(String title, String widgetContext, String defaultContext)
  {
    super(title, widgetContext, defaultContext);
    setType(ActionUtils.CMD_LAUNCH_PROCESS_CONTROL_PROGRAM);
  }
  
  public LaunchProcessControlProgram(String title, String widgetContext, String defaultContext, String template)
  {
    super(title, widgetContext, defaultContext, template);
    setType(ActionUtils.CMD_LAUNCH_PROCESS_CONTROL_PROGRAM);
  }
  
  public LaunchProcessControlProgram(String keyString, String title, DataTable parameters)
  {
    super(keyString, title, parameters);
    setType(ActionUtils.CMD_LAUNCH_PROCESS_CONTROL_PROGRAM);
  }

  public LaunchProcessControlProgram(String title, DataTable parameters)
  {
    super(title, parameters);
    setType(ActionUtils.CMD_LAUNCH_PROCESS_CONTROL_PROGRAM);
  }
  
}