package com.tibbo.aggregate.common.action;

import com.tibbo.aggregate.common.action.command.*;

import java.util.*;

public class ActionCommandRegistry
{
  private static Map<String, GenericActionCommand> COMMANDS = new HashMap<String, GenericActionCommand>();
  
  static
  {
    register(new ActivateDashboard());
    register(new Browse());
    register(new Confirm());
    register(new EditCode());
    register(new EditData());
    register(new EditProperties());
    register(new EditReport());
    register(new EditText());
    register(new EditTemplate(ActionUtils.CMD_EDIT_WIDGET));
    register(new LaunchWidget());
    register(new EditTemplate(ActionUtils.CMD_EDIT_PROCESS_CONTROL_PROGRAM));
    register(new LaunchProcessControlProgram());
    register(new EditTemplate(ActionUtils.CMD_EDIT_WORKFLOW));
    register(new SelectEntities());
    register(new ShowDiff());
    register(new ShowError());
    register(new ShowEventLog());
    register(new ShowGuide());
    register(new ShowMessage());
    register(new ShowReport());
    register(new ShowHtmlSnippet());
    register(new ShowSystemTree());
    register(new CloseDashboard());
    register(new OpenGridDashboard());
    register(new EditGridDashboard());
    register(new EditExpression());
  }
  
  public static GenericActionCommand getCommand(String type)
  {
    return COMMANDS.get(type);
  }
  
  private static void register(GenericActionCommand command)
  {
    COMMANDS.put(command.getType(), command);
  }
  
}
