package com.tibbo.aggregate.common.server;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.datatable.FieldFormat;
import com.tibbo.aggregate.common.datatable.TableFormat;
import com.tibbo.aggregate.common.datatable.validator.TableKeyFieldsValidator;

public class WorkflowContextConstants
{
  
  public static final String V_ARGUMENTS = "arguments";
  
  public static final String V_FIXED_ARGUMENTS = "fixedArguments";
  public static final String A_EDIT = "editTemplate";
  
  public static final String F_EXECUTE = "execute";
  
  public static final String F_ANALYZE = "analyze";
  public static final String FIF_EXECUTE_ACTION_DEFAULT_TABLE = "defaultDataTable";
  public static final String FIF_EXECUTE_ACTION = "workflowExecutionParameters";
  
  public static final String A_LAUNCH = "launch";
  
  public static final String V_THREAD_POOL_STATUS = "threadPoolStatus";
  
  public static final String FIF_THREAD_POOL_ACTIVE_COUNT = "activeCount";
  public static final String FIF_THREAD_POOL_COMPLETED_COUNT = "completedCount";
  public static final String FIF_THREAD_POOL_TOTAL_COUNT = "totalCount";
  public static final String FIF_THREAD_POOL_CORE_SIZE = "coreSize";
  public static final String FIF_THREAD_POOL_LARGEST_SIZE = "largestSize";
  public static final String FIF_THREAD_POOL_MAXIMUM_SIZE = "maximumSize";
  public static final String FIF_THREAD_POOL_QUEUE_LENGTH = "queueLength";
  
  public static final String E_WORKFLOW_EXECUTION = "workflowExecution";
  public static final String EF_WORKFLOW_EXECUTION_ELEMENT = "workflowExecutionElement";
  public static final String EF_WORKFLOW_EXECUTION_INPUT_VALUE = "workflowExecutionInputValue";
  public static final String E_WORKFLOW_EXECUTION_ERROR = "workflowError";
  public static final String EF_EXECUTION_ERROR_ELEMENT = "target";
  public static final String EF_EXECUTION_ERROR = "error";
  public static final String EF_EXECUTION_ERROR_STACK = "stack";
  public static final String VF_TEMPLATE = "template";
  
  public static final TableFormat FOFT_GET_COMPONENTS = new TableFormat();
  
  public static final String F_GET_COMPONENTS = "getComponents";
  
  public static final String FOF_COMPONENTS_KEY = "key";
  public static final String FOF_COMPONENTS_DESCRIPTION = "description";
  public static final String FOF_COMPONENTS_GROUP = "group";
  public static final String FOF_COMPONENTS_ICON = "icon";
  public static final String FOF_COMPONENTS_HELP_ID = "helpId";
  public static final String FOF_COMPONENTS_VISIBLE = "visible";
  public static final String FOF_COMPONENTS_VARIABLE_DEFINITIONS = "variableDefinitions";
  public static final String FOF_COMPONENTS_EVENT_DEFINITIONS = "eventDefinitions";
  public static final String FOF_COMPONENTS_FUNCTION_DEFINITIONS = "functionDefinitions";
  public static final String FOF_COMPONENTS_ACTION_DEFINITIONS = "actionDefinitions";
  
  public static final String V_COMPONENTS = "components";
  
  public static final String F_ELEMENT_PARAMETERS = "elementParameters";
  
  public static final TableFormat FOFT_ANALYZE = new TableFormat();
  public static final String FOF_ANALYZE_COMPONENT_NAME = "componentName";
  public static final String FOF_ANALYZE_MESSAGE = "message";
  public static final String FOF_ANALYZE_TYPE_MESSAGE = "typeMessage";
  public static final String FIELD_NAME = "name";
  public static final String FIELD_TYPE = "type";
  public static final String FIELD_VARIABLES = "variables";
  public static TableFormat WORKFLOW_ELEMENT_FORMAT = new TableFormat(true);
  
  static
  {
    FieldFormat ff = FieldFormat.create(FOF_ANALYZE_COMPONENT_NAME, FieldFormat.STRING_FIELD);
    FOFT_ANALYZE.addField(ff);
    
    ff = FieldFormat.create(FOF_ANALYZE_MESSAGE, FieldFormat.STRING_FIELD);
    FOFT_ANALYZE.addField(ff);
    
    ff = FieldFormat.create(FOF_ANALYZE_TYPE_MESSAGE, FieldFormat.STRING_FIELD);
    FOFT_ANALYZE.addField(ff);
  }
  
  static
  {
    FieldFormat ff = FieldFormat.create(FOF_COMPONENTS_KEY, FieldFormat.STRING_FIELD);
    ff.setKeyField(true);
    FOFT_GET_COMPONENTS.addField(ff);
    
    ff = FieldFormat.create(FOF_COMPONENTS_DESCRIPTION, FieldFormat.STRING_FIELD);
    FOFT_GET_COMPONENTS.addField(ff);
    
    ff = FieldFormat.create(FOF_COMPONENTS_GROUP, FieldFormat.STRING_FIELD);
    FOFT_GET_COMPONENTS.addField(ff);
    
    ff = FieldFormat.create(FOF_COMPONENTS_ICON, FieldFormat.STRING_FIELD);
    FOFT_GET_COMPONENTS.addField(ff);
    
    ff = FieldFormat.create(FOF_COMPONENTS_HELP_ID, FieldFormat.STRING_FIELD);
    FOFT_GET_COMPONENTS.addField(ff);
    
    ff = FieldFormat.create(FOF_COMPONENTS_VISIBLE, FieldFormat.BOOLEAN_FIELD);
    FOFT_GET_COMPONENTS.addField(ff);
    
    ff = FieldFormat.create(FOF_COMPONENTS_VARIABLE_DEFINITIONS, FieldFormat.DATATABLE_FIELD);
    FOFT_GET_COMPONENTS.addField(ff);
    
    ff = FieldFormat.create(FOF_COMPONENTS_EVENT_DEFINITIONS, FieldFormat.DATATABLE_FIELD);
    FOFT_GET_COMPONENTS.addField(ff);
    
    ff = FieldFormat.create(FOF_COMPONENTS_FUNCTION_DEFINITIONS, FieldFormat.DATATABLE_FIELD);
    FOFT_GET_COMPONENTS.addField(ff);
    
    ff = FieldFormat.create(FOF_COMPONENTS_ACTION_DEFINITIONS, FieldFormat.DATATABLE_FIELD);
    FOFT_GET_COMPONENTS.addField(ff);
  }
  
  static
  {
    FieldFormat ff = FieldFormat.create("<" + FIELD_NAME + "><S><D=" + Cres.get().getString("name") + ">");
    ff.setKeyField(true);
    ff.setHelp(Cres.get().getString("conNameChangeWarning"));
    // TODO Validators are needed, but now they do not allow to save the root context.
    // ff.getValidators().add(ValidatorHelper.NAME_LENGTH_VALIDATOR);
    // ff.getValidators().add(ValidatorHelper.NAME_SYNTAX_VALIDATOR);
    WORKFLOW_ELEMENT_FORMAT.addField(ff);
    
    ff = FieldFormat.create(FIELD_TYPE, FieldFormat.STRING_FIELD);
    Map<String, String> values = Arrays.stream(ELEMENTS.values()).map(it -> it.id).collect(Collectors.toMap(Function.identity(), Function.identity()));
    ff.setSelectionValues(values);
    WORKFLOW_ELEMENT_FORMAT.addField(ff);
    
    ff = FieldFormat.create("<" + FIELD_VARIABLES + "><T><D=" + Cres.get().getString("parameters") + ">");
    WORKFLOW_ELEMENT_FORMAT.addField(ff);
    
    String ref = FIELD_VARIABLES;
    String exp = "{.:" + F_ELEMENT_PARAMETERS + "('{" + FIELD_NAME + "}', '{" + FIELD_TYPE + "}','{" + FIELD_VARIABLES + "}')}";
    WORKFLOW_ELEMENT_FORMAT.addBinding(ref, exp);
    
    WORKFLOW_ELEMENT_FORMAT.addTableValidator(new TableKeyFieldsValidator());
  }
  
  public enum ELEMENTS
  {
    WORKFLOW_TIMER("workflowTimer"),
    WORKFLOW_EVENT("workflowEvent"),
    WORKFLOW_STARTUP("workflowStartup"),
    WORKFLOW_FUNCTION("workflowFunction"),
    WORKFLOW_ACTION("workflowAction"),
    WORKFLOW_EXPRESSION("workflowExpression"),
    WORKFLOW_IF("workflowIf"),
    WORKFLOW_FORK("workflowFork"),
    WORKFLOW_UI_PROCEDURE("workflowUiProcedure"),
    WORKFLOW_CATCH("workflowCatch"),
    WORKFLOW_EXIT("workflowExit"),
    WORKFLOW_SWITCH("workflowSwitch"),
    WORKFLOW_SCRIPT("workflowScript"),
    
    WORKFLOW_CONNECTOR(WidgetContextConstants.COMPONENT_CONNECTOR, true),
    
    WORKFLOW_ROOT_PANEL(WidgetContextConstants.COMPONENT_ROOT_PANEL, true);
    
    private final String id;
    
    private final boolean isAdditionalElement;
    
    ELEMENTS(String id)
    {
      this.id = id;
      this.isAdditionalElement = false;
    }
    
    ELEMENTS(String id, boolean isAdditionalElement)
    {
      this.id = id;
      this.isAdditionalElement = isAdditionalElement;
    }
    
    public static ELEMENTS getElementById(String type)
    {
      for (ELEMENTS ELEMENTS : ELEMENTS.values())
      {
        if (ELEMENTS.id.equals(type))
          return ELEMENTS;
      }
      throw new IllegalStateException("unknown type: " + type);
    }
    
    public String getId()
    {
      return this.id;
    }
    
    public boolean isAdditionalElement()
    {
      return isAdditionalElement;
    }
  }
}
