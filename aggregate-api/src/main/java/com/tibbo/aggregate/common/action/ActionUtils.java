package com.tibbo.aggregate.common.action;

import java.util.*;
import javax.swing.*;

import com.tibbo.aggregate.common.*;
import com.tibbo.aggregate.common.action.command.*;
import com.tibbo.aggregate.common.context.*;
import com.tibbo.aggregate.common.datatable.*;
import com.tibbo.aggregate.common.server.*;
import com.tibbo.aggregate.common.util.*;
import org.apache.commons.beanutils.*;

public abstract class ActionUtils
{
  public static final String CMD_SHOW_MESSAGE = "showMessage";
  public static final String CMD_CONFIRM = "confirm";
  public static final String CMD_EDIT_DATA = "editData";
  public static final String CMD_EDIT_PROPERTIES = "editProperties";
  public static final String CMD_EDIT_WIDGET = "editWidget";
  public static final String CMD_EDIT_PROCESS_CONTROL_PROGRAM = "editProcessControlProgram";
  public static final String CMD_EDIT_WORKFLOW = "editWorkflow";
  public static final String CMD_LAUNCH_WORKFLOW = "launchWorkflow";
  public static final String CMD_LAUNCH_WIDGET = "launchWidget";
  public static final String CMD_LAUNCH_PROCESS_CONTROL_PROGRAM = "launchProcessControlProgram";
  public static final String CMD_BROWSE = "browse";
  public static final String CMD_SHOW_EVENT_LOG = "showEventLog";
  public static final String CMD_SHOW_ERROR = "showError";
  public static final String CMD_EDIT_REPORT = "editReport";
  public static final String CMD_SHOW_REPORT = "showReport";
  public static final String CMD_SHOW_GUIDE = "showGuide";
  public static final String CMD_SELECT_ENTITIES = "selectEntities";
  public static final String CMD_EDIT_TEXT = "editText";
  public static final String CMD_EDIT_CODE = "editCode";
  public static final String CMD_SHOW_SYSTEM_TREE = "showSystemTree";
  public static final String CMD_SHOW_HTML_SNIPPET = "showHtmlSnippet";
  public static final String CMD_ACTIVATE_DASHBOARD = "activateDashboard";
  public static final String CMD_SHOW_DIFF = "showDiff";
  public static final String CMD_CLOSE_DASHBOARD = "closeDashboard";
  public static final String CMD_OPEN_GRID_DASHBOARD = "openGridDashboard";
  public static final String CMD_EDIT_GRID_DASHBOARD = "editGridDashboard";
  public static final String CMD_EDIT_EXPRESSION = "editExpression";
  
  public static final Map<String, Class> COMMANDS = new Hashtable();
  
  static
  {
    COMMANDS.put(CMD_SHOW_MESSAGE, ShowMessage.class);
    COMMANDS.put(CMD_CONFIRM, Confirm.class);
    COMMANDS.put(CMD_EDIT_DATA, EditData.class);
    COMMANDS.put(CMD_EDIT_PROPERTIES, EditProperties.class);
    COMMANDS.put(CMD_EDIT_WIDGET, EditTemplate.class);
    COMMANDS.put(CMD_EDIT_PROCESS_CONTROL_PROGRAM, EditTemplate.class);
    COMMANDS.put(CMD_EDIT_WORKFLOW, EditTemplate.class);
    COMMANDS.put(CMD_LAUNCH_WIDGET, LaunchWidget.class);
    COMMANDS.put(CMD_LAUNCH_PROCESS_CONTROL_PROGRAM, LaunchProcessControlProgram.class);
    COMMANDS.put(CMD_BROWSE, Browse.class);
    COMMANDS.put(CMD_SHOW_EVENT_LOG, ShowEventLog.class);
    COMMANDS.put(CMD_SHOW_ERROR, ShowError.class);
    COMMANDS.put(CMD_EDIT_REPORT, EditReport.class);
    COMMANDS.put(CMD_SHOW_REPORT, ShowReport.class);
    COMMANDS.put(CMD_SHOW_GUIDE, ShowGuide.class);
    COMMANDS.put(CMD_SELECT_ENTITIES, SelectEntities.class);
    COMMANDS.put(CMD_EDIT_TEXT, EditText.class);
    COMMANDS.put(CMD_EDIT_CODE, EditCode.class);
    COMMANDS.put(CMD_SHOW_SYSTEM_TREE, ShowSystemTree.class);
    COMMANDS.put(CMD_ACTIVATE_DASHBOARD, ActivateDashboard.class);
    COMMANDS.put(CMD_CLOSE_DASHBOARD, CloseDashboard.class);
    COMMANDS.put(CMD_OPEN_GRID_DASHBOARD, OpenGridDashboard.class);
    COMMANDS.put(CMD_EDIT_GRID_DASHBOARD, EditGridDashboard.class);
    COMMANDS.put(CMD_SHOW_HTML_SNIPPET, ShowHtmlSnippet.class);
    COMMANDS.put(CMD_SHOW_DIFF, ShowDiff.class);
    COMMANDS.put(CMD_EDIT_EXPRESSION, EditExpression.class);
  }
  
  public static final Map<String, String> DESCRIPTIONS = new Hashtable();
  
  static
  {
    DESCRIPTIONS.put(CMD_SHOW_MESSAGE, Cres.get().getString("acUiProcShowMessage"));
    DESCRIPTIONS.put(CMD_CONFIRM, Cres.get().getString("acUiProcConfirm"));
    DESCRIPTIONS.put(CMD_EDIT_DATA, Cres.get().getString("acUiProcEditData"));
    DESCRIPTIONS.put(CMD_EDIT_PROPERTIES, Cres.get().getString("acUiProcEditProperties"));
    DESCRIPTIONS.put(CMD_LAUNCH_WIDGET, Cres.get().getString("acUiProcLaunchWidget"));
    DESCRIPTIONS.put(CMD_OPEN_GRID_DASHBOARD, Cres.get().getString("acUiProcOpenGridDashboard"));
    DESCRIPTIONS.put(CMD_EDIT_GRID_DASHBOARD, Cres.get().getString("acUiProcEditGridDashboard"));
    DESCRIPTIONS.put(CMD_BROWSE, Cres.get().getString("acUiProcBrowse"));
    DESCRIPTIONS.put(CMD_SHOW_EVENT_LOG, Cres.get().getString("acUiProcShowEventLog"));
    DESCRIPTIONS.put(CMD_SHOW_ERROR, Cres.get().getString("acUiShowError"));
    DESCRIPTIONS.put(CMD_SHOW_REPORT, Cres.get().getString("acUiProcShowReport"));
    DESCRIPTIONS.put(CMD_SHOW_GUIDE, Cres.get().getString("acUiShowGuide"));
    DESCRIPTIONS.put(CMD_SELECT_ENTITIES, Cres.get().getString("acUiProcSelectEntities"));
    DESCRIPTIONS.put(CMD_EDIT_TEXT, Cres.get().getString("acUiProcEditText"));
    DESCRIPTIONS.put(CMD_EDIT_CODE, Cres.get().getString("acUiProcEditCode"));
    DESCRIPTIONS.put(CMD_SHOW_SYSTEM_TREE, Cres.get().getString("acUiProcShowSystemTree"));
    DESCRIPTIONS.put(CMD_SHOW_DIFF, Cres.get().getString("acUiProcShowDiff"));
    DESCRIPTIONS.put(CMD_EDIT_EXPRESSION, Cres.get().getString("acUiProcEditExpression"));
  }
  
  public static final Map<String, TableFormat> FORMATS = new Hashtable();
  
  static
  {
    FORMATS.put(CMD_SHOW_MESSAGE, ShowMessage.CFT_SHOW_MESSAGE);
    FORMATS.put(CMD_CONFIRM, Confirm.CFT_CONFIRM);
    FORMATS.put(CMD_EDIT_DATA, EditData.CFT_EDIT_DATA);
    FORMATS.put(CMD_EDIT_PROPERTIES, EditProperties.CFT_EDIT_PROPERTIES);
    FORMATS.put(CMD_EDIT_WIDGET, EditTemplate.CFT_EDIT_TEMPLATE);
    FORMATS.put(CMD_EDIT_PROCESS_CONTROL_PROGRAM, EditTemplate.CFT_EDIT_TEMPLATE);
    FORMATS.put(CMD_EDIT_WORKFLOW, EditTemplate.CFT_EDIT_TEMPLATE);
    FORMATS.put(CMD_LAUNCH_WIDGET, LaunchWidget.CFT_LAUNCH_WIDGET);
    FORMATS.put(CMD_LAUNCH_PROCESS_CONTROL_PROGRAM, LaunchProcessControlProgram.CFT_LAUNCH_WIDGET);
    FORMATS.put(CMD_BROWSE, Browse.CFT_BROWSE);
    FORMATS.put(CMD_SHOW_EVENT_LOG, ShowEventLog.CFT_SHOW_EVENT_LOG);
    FORMATS.put(CMD_SHOW_ERROR, ShowError.CFT_SHOW_ERROR);
    FORMATS.put(CMD_EDIT_REPORT, EditReport.CFT_EDIT_REPORT);
    FORMATS.put(CMD_SHOW_REPORT, ShowReport.CFT_SHOW_REPORT);
    FORMATS.put(CMD_SHOW_GUIDE, ShowGuide.CFT_SHOW_GUIDE);
    FORMATS.put(CMD_SELECT_ENTITIES, SelectEntities.CFT_SELECT_ENTITIES);
    FORMATS.put(CMD_EDIT_TEXT, EditText.CFT_EDIT_TEXT);
    FORMATS.put(CMD_EDIT_CODE, EditCode.CFT_EDIT_CODE);
    FORMATS.put(CMD_SHOW_SYSTEM_TREE, ShowSystemTree.CFT_SHOW_SYSTEM_TREE);
    FORMATS.put(CMD_ACTIVATE_DASHBOARD, ActivateDashboard.CFT_ACTIVATE_DASHBOARD);
    FORMATS.put(CMD_CLOSE_DASHBOARD, CloseDashboard.CFT_CLOSE_DASHBOARD);
    FORMATS.put(CMD_OPEN_GRID_DASHBOARD, GridDashboardActionCommand.CFT_GRID_DASHBOARD);
    FORMATS.put(CMD_EDIT_GRID_DASHBOARD, GridDashboardActionCommand.CFT_GRID_DASHBOARD);
    FORMATS.put(CMD_SHOW_HTML_SNIPPET, ShowHtmlSnippet.CFT_SHOW_HTML_SNIPPET);
    FORMATS.put(CMD_SHOW_DIFF, ShowDiff.CFT_SHOW_DIFF);
    FORMATS.put(CMD_EDIT_EXPRESSION, TableFormat.EMPTY_FORMAT);
  }
  
  public static final int INDEX_HIGHEST = 400;
  public static final int INDEX_VERY_HIGH = 300;
  public static final int INDEX_HIGH = 200;
  public static final int INDEX_HIGHER = 100;
  public static final int INDEX_NORMAL = 0;
  public static final int INDEX_LOWER = -100;
  public static final int INDEX_LOW = -200;
  public static final int INDEX_VERY_LOW = -300;
  public static final int INDEX_LOWEST = -400;
  
  public static final int DELTA_HIGHEST = 40;
  public static final int DELTA_VERY_HIGH = 30;
  public static final int DELTA_HIGH = 20;
  public static final int DELTA_HIGHER = 10;
  public static final int DELTA_LOWER = -10;
  public static final int DELTA_LOW = -20;
  public static final int DELTA_VERY_LOW = -30;
  public static final int DELTA_LOWEST = -400;
  
  public static final String RESPONSE_OK = "ok";
  public static final String RESPONSE_SAVED = "saved";
  public static final String RESPONSE_CLOSED = "closed";
  public static final String RESPONSE_ERROR = "error";
  
  public static final int YES_NO_OPTION = JOptionPane.YES_NO_OPTION;
  public static final int YES_NO_CANCEL_OPTION = JOptionPane.YES_NO_CANCEL_OPTION;
  public static final int OK_CANCEL_OPTION = JOptionPane.OK_CANCEL_OPTION;
  
  public static final int YES_OPTION = JOptionPane.YES_OPTION;
  public static final int NO_OPTION = JOptionPane.NO_OPTION;
  public static final int CANCEL_OPTION = JOptionPane.CANCEL_OPTION;
  public static final int OK_OPTION = JOptionPane.OK_OPTION;
  public static final int CLOSED_OPTION = JOptionPane.CLOSED_OPTION;
  
  public static final int ERROR_MESSAGE = JOptionPane.ERROR_MESSAGE;
  public static final int INFORMATION_MESSAGE = JOptionPane.INFORMATION_MESSAGE;
  public static final int WARNING_MESSAGE = JOptionPane.WARNING_MESSAGE;
  public static final int QUESTION_MESSAGE = JOptionPane.QUESTION_MESSAGE;
  public static final int PLAIN_MESSAGE = JOptionPane.PLAIN_MESSAGE;
  
  public static final String FIELD_ACTION_TARGET_CONTEXT = "targetContext";
  public static final String FIELD_ACTION_FROM_CONTEXT = "fromContext";
  public static final String FIELD_ACTION_EXECUTION_PARAMETERS = "executionParameters";
  
  public static final TableFormat FORMAT_NORMAL_ACTION = new TableFormat(1, 1);
  
  static
  {
    FORMAT_NORMAL_ACTION.addField("<" + FIELD_ACTION_EXECUTION_PARAMETERS + "><T><F=N>");
  }
  
  public static final TableFormat FORMAT_DND_ACTION = new TableFormat(1, 1);
  
  static
  {
    FORMAT_DND_ACTION.addField("<" + FIELD_ACTION_FROM_CONTEXT + "><S>");
  }
  
  public static final TableFormat FORMAT_PROPAGATED_ACTION = new TableFormat(1, 1);
  
  static
  {
    FORMAT_PROPAGATED_ACTION.addField("<" + FIELD_ACTION_TARGET_CONTEXT + "><S>");
  }
  
  private static ActionInitializer ACTION_INITIALIZER = new DefaultActionInitializer();
  
  public static void setActionInitializer(ActionInitializer initializer)
  {
    ACTION_INITIALIZER = initializer;
  }
  
  public static GenericActionCommand createActionCommand(String type, String title, DataTable parameters)
  {
    Class clazz = getActionCommandClass(type);
    
    try
    {
      return (GenericActionCommand) ConstructorUtils.invokeConstructor(clazz, new Object[] { title, parameters });
    }
    catch (Exception ex)
    {
      throw new IllegalStateException("Error creating action command of type '" + type + "': " + ex.getMessage(), ex);
    }
  }
  
  public static TableFormat getActionCommandFormat(String type)
  {
    Class clazz = getActionCommandClass(type);
    
    try
    {
      GenericActionCommand cmd = (GenericActionCommand) clazz.newInstance();
      
      return cmd.getParameters().getFormat();
    }
    catch (Exception ex)
    {
      throw new IllegalStateException("Error creating action command of type '" + type + "': " + ex.getMessage(), ex);
    }
  }
  
  private static Class getActionCommandClass(String type)
  {
    Class clazz = COMMANDS.get(type);
    
    if (clazz == null)
    {
      throw new IllegalArgumentException("Unknown action command type: " + type);
    }
    
    return clazz;
  }
  
  public static void checkResponseCode(String result) throws IllegalArgumentException
  {
    if (!RESPONSE_SAVED.equals(result) && !RESPONSE_CLOSED.equals(result) && !RESPONSE_ERROR.equals(result))
    {
      throw new IllegalArgumentException("Illegal response result: " + result);
    }
  }
  
  public static DataTable createDndActionParameters(Context acceptedContext)
  {
    return createDndActionParameters(acceptedContext.getPath());
  }
  
  public static DataTable createDndActionParameters(String accepterContextPath)
  {
    DataTable paramsEntry = new SimpleDataTable(FORMAT_DND_ACTION);
    paramsEntry.addRecord().addString(accepterContextPath);
    return paramsEntry;
  }
  
  public static ServerActionInput createActionInput(DataTable executionParameters)
  {
    return new ServerActionInput(new SimpleDataTable(FORMAT_NORMAL_ACTION, executionParameters));
  }
  
  public static ActionIdentifier initAction(Context context, String actionName, ServerActionInput initialParameters, DataTable inputData, ActionExecutionMode mode, CallerController callerController)
      throws ContextException
  {
    return initAction(context, actionName, initialParameters, inputData, null, mode, callerController, null);
  }
  
  public static ActionIdentifier initAction(Context context, String actionName, ServerActionInput initialParameters, DataTable inputData, Map<String, Object> environment, ActionExecutionMode mode,
      CallerController callerController, ErrorCollector collector) throws ContextException
  {
    return ACTION_INITIALIZER.initAction(context, actionName, initialParameters, inputData, environment, mode, callerController, collector);
  }
  
  public static GenericActionCommand stepAction(Context context, ActionIdentifier actionId, GenericActionResponse actionResponse, CallerController callerController) throws ContextException
  {
    long startTime = System.currentTimeMillis();
    
    DataTable res = context.callFunction(ServerContextConstants.F_STEP_ACTION, callerController, actionId.toString(), ProtocolHandler.actionResponseToDataTable(actionResponse));
    GenericActionCommand rc = ProtocolHandler.actionCommandFromDataTable(res.rec().getDataTable(ServerContextConstants.FOF_STEP_ACTION_ACTION_COMMAND));
    
    if (Log.CONTEXT_ACTIONS.isDebugEnabled())
    {
      Log.CONTEXT_ACTIONS.debug("Action step that returned UI command '" + rc + "' completed in " + (System.currentTimeMillis() - startTime) + " ms");
    }
    
    return rc;
  }
}
