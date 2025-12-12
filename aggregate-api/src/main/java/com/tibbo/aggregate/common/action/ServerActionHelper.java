package com.tibbo.aggregate.common.action;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.Log;
import com.tibbo.aggregate.common.action.command.ShowError;
import com.tibbo.aggregate.common.context.CallerController;
import com.tibbo.aggregate.common.context.Context;
import com.tibbo.aggregate.common.context.ContextException;
import com.tibbo.aggregate.common.context.ContextManager;
import com.tibbo.aggregate.common.context.VariableDefinition;
import com.tibbo.aggregate.common.datatable.DataRecord;
import com.tibbo.aggregate.common.datatable.DataTable;
import com.tibbo.aggregate.common.datatable.DataTableUtils;
import com.tibbo.aggregate.common.datatable.FieldFormat;
import com.tibbo.aggregate.common.datatable.SimpleDataTable;
import com.tibbo.aggregate.common.datatable.TableFormat;
import com.tibbo.aggregate.common.datatable.field.StringFieldFormat;
import com.tibbo.aggregate.common.expression.Evaluator;
import com.tibbo.aggregate.common.server.ServerContext;
import com.tibbo.aggregate.common.util.ErrorCollector;

public class ServerActionHelper
{
  public static final String ENV_VARIABLE_PARAMETERS = "parameters";
  
  private static final String FIELD_CONTEXT = "context";
  private static final String FIELD_SELECT = "select";
  
  private static final TableFormat CONTEXTS_FORMAT = new TableFormat();
  static
  {
    CONTEXTS_FORMAT.setUnresizable(true);
    CONTEXTS_FORMAT.addField(FieldFormat.create(FIELD_CONTEXT, FieldFormat.STRING_FIELD, Cres.get().getString("context")).setEditor(StringFieldFormat.EDITOR_CONTEXT).setReadonly(true));
    CONTEXTS_FORMAT.addField(FieldFormat.create(FIELD_SELECT, FieldFormat.BOOLEAN_FIELD, Cres.get().getString("select"), true));
  }
  
  public static Map<String, String> executeNonInteractively(Context context, String action, DataTable initialParameters, DataTable inputData, Map<String, Object> environment,
      ActionExecutionMode mode, CallerController caller) throws ContextException
  {
    Map<String, String> messages = new LinkedHashMap();
    
    ActionDefinition def = context.getActionDefinition(action, caller);
    if (def == null)
    {
      String message = MessageFormat.format(Cres.get().getString("conActNotAvailExt"), action, context.toString());
      messages.put(message, null);
      return messages;
    }
    
    Log.CONTEXT_ACTIONS.debug("Executing action '" + action + "' from context '" + context + "' non-interactively");
    
    ErrorCollector collector = new ErrorCollector();
    
    ActionIdentifier actionId = ActionUtils.initAction(context, action, new ServerActionInput(initialParameters), inputData, environment, mode, caller, collector);
    
    for (Exception error : collector.getErrors())
    {
      messages.put(error.getMessage(), error.toString());
    }
    
    if (collector.getErrors().size() > 0)
    {
      return messages; // Interrupting execution if input parameter binding evaluation has failed
    }
    
    while (true)
    {
      GenericActionCommand cmd = ActionUtils.stepAction(context, actionId, null, caller);
      
      if (cmd == null || cmd.isLast())
      {
        break;
      }
      
      Log.CONTEXT_ACTIONS.debug("Action returned command: actionId=" + actionId + ", type=" + cmd.getType() + ", title=" + cmd.getTitle());
      if (ActionUtils.CMD_SHOW_ERROR.equals(cmd.getType()))
      {
        DataRecord rec = cmd.getParameters().rec();
        String message = rec.getString(ShowError.CF_MESSAGE);
        String exception = rec.getString(ShowError.CF_EXCEPTION);
        messages.put(message, exception);
        Log.CONTEXT_ACTIONS.debug("Action returned error response: " + message + " (" + exception + ")");
      }
    }
    
    return messages;
  }
  
  public static void fillRequestCache(ActionContext actionContext, DataTable initialParameters, DataTable inputData, Map<String, Object> environment, CallerController caller, ContextManager cm,
      Context con, ErrorCollector collector) throws ContextException
  {
    if (inputData == null)
    {
      return;
    }
    
    RequestCache requestCache = actionContext.getRequestCache();
    
    if (requestCache == null)
    {
      requestCache = new RequestCache();
      
      actionContext.setRequestCache(requestCache);
    }
    
    if (inputData.getRecordCount() > 0)
    {
      DataRecord rec = inputData.rec();
      
      for (FieldFormat ff : rec.getFormat())
      {
        String requestId = ff.getName();
        
        GenericActionResponse req = buildResponse(requestId, initialParameters, rec.getDataTable(requestId), environment, caller, cm, con, collector);
        
        requestCache.getRequests().put(new RequestIdentifier(requestId), req);
      }
    }
  }
  
  private static GenericActionResponse buildResponse(String requestId, DataTable initialParameters, DataTable responseData, Map<String, Object> environment, CallerController caller,
      ContextManager cm, Context con, ErrorCollector collector)
  {
    GenericActionResponse req = new GenericActionResponse(responseData, false, new RequestIdentifier(requestId));
    
    if (responseData.getFormat().getBindings().size() > 0)
    {
      Evaluator evaluator = new Evaluator(cm, con, responseData, caller);
      
      if (environment != null)
      {
        for (Entry<String, Object> entry : environment.entrySet())
        {
          evaluator.getEnvironmentResolver().set(entry.getKey(), entry.getValue());
        }
      }
      
      evaluator.getEnvironmentResolver().set(ENV_VARIABLE_PARAMETERS, initialParameters);
      
      DataTableUtils.processBindings(responseData, evaluator, collector);
    }
    
    return req;
  }
  
  public static ActionIdentifier initAction(ServerContext source, String action, CallerController caller, DataTable initialParameters, DataTable inputData, Map<String, Object> environment,
                                            ActionExecutionMode mode, ErrorCollector collector, String customActionId) throws ContextException
  {
    boolean asDefault = false;

    if (action == null)
    {
      asDefault = true;

      List<ActionDefinition> actions = source.getActionDefinitions(caller);
      for (ActionDefinition ad : actions)
      {
        if (ad.isDefault())
        {
          action = ad.getName();
        }
      }
      
      if (action == null)
      {
        throw new ContextException("Default action not available in context '" + source.toDetailedString() + "'");
      }
    }
    
    ActionDefinition actionDef = source.getActionDefinition(action, caller);
    
    if (actionDef == null)
    {
      throw new ContextException("Action '" + action + "' not available in context '" + source.toDetailedString() + "'");
    }
    
    ActionIdentifier actionId = source.initAction(actionDef, new ServerActionInput(initialParameters), mode, caller, customActionId, asDefault);
    
    ActionContext actionContext = caller.getCallerData().getActionManager().getActionContext(actionId);

    actionContext.setExecutedAsDefault(asDefault);
    
    ServerActionHelper.fillRequestCache(actionContext, initialParameters, inputData, environment, caller, source.getContextManager(), source, collector);
    
    caller.getCallerData().addToActionHistory(new ActionHistoryItem(new Date(), source.getPath(), action, initialParameters));
    
    return actionId;
  }
  
  public static ActionIdentifier initActions(CallerController caller, List<ActionHolder> actions, ServerContext con) throws ContextException
  {
    ActionManager actionManager = caller.getCallerData().getActionManager();
    
    List<BatchEntry> entries = new LinkedList();
    
    for (ActionHolder holder : actions)
    {
      ServerContext context = holder.getContext();
      
      String actionName = holder.getActionName();
      
      if (actionName == null)
      {
        throw new IllegalArgumentException("Action name is null");
      }
      
      ActionDefinition actionDef = context.getActionDefinition(actionName);
      
      if (actionDef == null)
      {
        throw new ContextException("Unsupported action '" + actionName + "' for context '" + context.getPath() + "'");
      }
      
      ServerActionContext actionContext = new ServerActionContext(actionDef, context, caller);
      ServerActionInput initialParameters = new ServerActionInput(holder.getInitialParameters());
      
      fillRequestCache(actionContext, holder.getInitialParameters(), holder.getInputData(), null, caller, con.getContextManager(), con, null);
      
      caller.getCallerData().addToActionHistory(new ActionHistoryItem(new Date(), context.getPath(), actionName, holder.getInitialParameters()));
      
      entries.add(new BatchEntry(actionContext, initialParameters));
    }
    
    ActionDefinition stub = new ServerActionDefinition(null, Action.class)
    {
      @Override
      public GroupIdentifier getExecutionGroup()
      {
        return null;
      }
    };
    
    ActionIdentifier actionId = actionManager.initActions(entries, new ServerActionContext(stub, con, caller));
    return actionId;
  }
  
  public static VariableDefinition selectVariable(String description, Context context, ServerActionCommandProcessor processor, CallerController caller, String iconId, String helpId)
  {
    List<VariableDefinition> vars = context.getVariableDefinitions(caller);
    
    for (Iterator<VariableDefinition> iter = vars.iterator(); iter.hasNext();)
    {
      VariableDefinition vd = iter.next();
      
      if (vd.getGroup() == null)
      {
        iter.remove();
      }
    }
    
    if (vars.isEmpty())
    {
      processor.showMessage(description, Cres.get().getString("acNoVariables"));
      return null;
    }
    
    if (vars.size() == 1)
    {
      return vars.get(0);
    }
    
    final String variableNameField = "variable";
    
    FieldFormat ff = FieldFormat.create(variableNameField, FieldFormat.STRING_FIELD);
    ff.setDescription(Cres.get().getString("variable"));
    ff.setEditor(FieldFormat.EDITOR_LIST);
    Map<Object, String> selValues = new LinkedHashMap();
    for (VariableDefinition var : vars)
    {
      selValues.put(var.getName(), var.toString());
    }
    ff.setSelectionValues(selValues);
    ff.setExtendableSelectionValues(false);
    TableFormat rf = new TableFormat(ff);
    rf.setMinRecords(1);
    rf.setMaxRecords(1);
    
    DataTable table = new SimpleDataTable(rf, true);
    
    table = processor.editData(description, table, iconId, helpId, null, null);
    
    if (table == null)
    {
      return null;
    }
    
    return context.getVariableDefinition(table.rec().getString(variableNameField));
  }
  
  public static FieldFormat selectField(String description, TableFormat format, ServerActionCommandProcessor processor, CallerController caller, String iconId, String helpId)
  {
    if (format == null)
    {
      return null;
    }
    
    List<FieldFormat> fields = new LinkedList();
    
    for (FieldFormat ff : format)
    {
      if (ff.isHidden())
      {
        continue;
      }
      
      fields.add(ff);
    }
    
    if (fields.isEmpty())
    {
      processor.showMessage(description, Cres.get().getString("acNoFields"));
      return null;
    }
    
    if (fields.size() == 1)
    {
      return fields.get(0);
    }
    
    final String fieldNameField = "field";
    
    FieldFormat ff = FieldFormat.create(fieldNameField, FieldFormat.STRING_FIELD);
    ff.setDescription(Cres.get().getString("field"));
    ff.setEditor(FieldFormat.EDITOR_LIST);
    Map<Object, String> selValues = new LinkedHashMap();
    for (FieldFormat cff : fields)
    {
      selValues.put(cff.getName(), cff.toString());
    }
    ff.setSelectionValues(selValues);
    ff.setExtendableSelectionValues(false);
    TableFormat rf = new TableFormat(ff);
    rf.setMinRecords(1);
    rf.setMaxRecords(1);
    
    DataTable table = new SimpleDataTable(rf, true);
    
    table = processor.editData(description, table, iconId, helpId, null, null);
    
    if (table == null)
    {
      return null;
    }
    
    return format.getField(table.rec().getString(fieldNameField));
  }
  
  public static Integer selectOption(String description, Map<Integer, String> options, Integer defaultOption, ServerActionCommandProcessor processor)
  {
    final String fieldName = "option";
    
    FieldFormat ff = FieldFormat.create(fieldName, FieldFormat.INTEGER_FIELD);
    ff.setDescription(description);
    ff.setEditor(FieldFormat.EDITOR_LIST);
    ff.setSelectionValues(options);
    if (defaultOption != null)
    {
      ff.setDefault(defaultOption);
    }
    
    TableFormat rf = new TableFormat(ff);
    rf.setMinRecords(1);
    rf.setMaxRecords(1);
    
    DataTable table = new SimpleDataTable(rf, true);
    
    table = processor.editData(description, table, null);
    
    if (table == null)
    {
      return null;
    }
    
    return table.rec().getInt(fieldName);
  }
  
  public static Collection<Context> selectContexts(String description, Collection<Context> contexts, ContextManager cm, CallerController cc, ServerActionCommandProcessor processor)
  {
    
    DataTable data = new SimpleDataTable(CONTEXTS_FORMAT);
    
    for (Context con : contexts)
    {
      data.addRecord(con.getPath(), true);
    }
    
    data = processor.editData(description, data, null);
    
    if (data == null)
    {
      return null;
    }
    
    Collection<Context> res = new LinkedHashSet();
    
    for (DataRecord rec : data)
    {
      if (rec.getBoolean(FIELD_SELECT))
      {
        Context con = cm.get(rec.getString(FIELD_CONTEXT), cc);
        if (con != null)
        {
          res.add(con);
        }
      }
    }
    
    return res;
  }
  
  public static DataRecord getEntityRelatedModeRecord(ServerActionInput parameters)
  {
    DataTable ininitialParameters = parameters.getData();
    
    if (ininitialParameters != null && ininitialParameters.getFormat().hasField(EntityRelatedActions.FIELD_RECORD)) // Entity related mode was used
    {
      DataTable record = ininitialParameters.rec().getDataTable(EntityRelatedActions.FIELD_RECORD);
      
      if (record != null) // Action called for specific record
      {
        return record.rec();
      }
    }
    
    return null;
  }
}
