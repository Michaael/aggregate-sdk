package com.tibbo.aggregate.common.server;

public interface ProcessControlContextConstants
{
  int STATUS_ENABLED = 1;
  int STATUS_DISABLED = 2;
  
  String V_RUNTIME_DATA = "runtimeData";
  String V_TEMPLATE = "template";
  String V_STANDARD_HINT = "standardHint";
  String V_CUSTOM_HINT = "customHint";
  String V_GENERATED_CODE = "generatedCode";
  String E_ERRORS_ANALYZE = "errorAnalyze";
  
  String F_VALUE = "value";
  String F_PROPERTY = "property";
  String F_ANALYZE = "analyze";
  String F_FIND_HINT = "findHint";
  String F_GET_GPIO_VARIABLES = "getGpioVariables";
  String F_GET_ANALOG_VARIABLES = "getAnalogVariables";
  String F_BREAKPOINT = "breakPoint";
  String F_DEBUG_PROGRAM = "debugProgram";
  String F_EXECUTE = "execute";
  String F_EXECUTE_FUNCTION = "executeFunction";
  String F_META_POU = "metaPou";
  String F_GET_TOKENS = "getTokens";
  
  String E_CHANGE_VARIABLES = "changeVariables";
  String E_CHANGE_PERIOD = "changePeriod";
  String E_CHANGE_POSITION = "changePosition";
  String E_DEBUG_FINISH = "debugFinish";
  
  String A_DEBUG = "debug";
  String A_EDIT_PROGRAM = "editProgram";
  
  String VF_POU_NAME = "name";
  String VF_POU_DESCRIPTION = "description";
  String VF_POU_TEMPLATE = "template";
  String VF_IMPLEMENT_LANGUAGE = "implementLanguage";
  String VF_POU_TYPE = "pouType";
  String VF_POU_IMPORTS = "imports";
  String VF_POU_DEVICE_CONTEXT = "deviceContext";
  String VF_POU_DEVICE_WATCHDOG = "watchdog";
  String VF_POU_DEVICE_WATCHDOG_TIME = "watchdogTime";
  String VF_POU_DEVICE_WATCHDOG_SENSITIVITY = "watchdogSensitivity";
  String VF_POU_TASK = "task";
  String VF_POU_INTERVAL = "interval";
  String VF_POU_EXECUTION_TYPE = "executionType";
  String VF_POU_AUTORUN = "autorun";
  
  String FIF_ANALYZE_TEMPLATE = "widgetTemplate";
  String FIF_ANALYZE_IMPORTS = "imports";
  String FOF_ANALYZE_PROBLEM_TEXT = "problemText";
  String FOF_ANALYZE_OBJECT = "object";
  String FOF_ANALYZE_POSITION = "positionText";
  String FOF_ANALYZE_PROBLEM_START_POSITION_COL = "problemStartPositionColumn";
  String FOF_ANALYZE_PROBLEM_START_POSITION_LINE = "problemStartPositionLine";
  String FOF_ANALYZE_PROBLEM_END_POSITION_COL = "problemEndPositionColumn";
  String FOF_ANALYZE_PROBLEM_END_POSITION_LINE = "problemEndPositionLine";
  String FOF_ANALYZE_NAME = "name";
  String FOF_ANALYZE_PROBLEMS = "problems";
  
  String FIF_GET_HINT_PARAM_TYPE = "getHintInputObjType";
  String FIF_GET_HINT_PARAM_NAME = "getHintInputObjName";
  String FOF_GET_HINT_NAME = "hintName";
  String FOF_GET_HINT_PASS = "hintPass";
  String FOF_GET_HINT_EXPRESSION = "hintExpression";
  String FOF_GET_HINT_TYPE = "hintType";
  
  String FOF_GENERATED_CODE = "generatedCode";
  
  String FOF_GET_GPIO_VARIABLES_NAME = "gpioVariablesName";
  String FOF_GET_ANALOG_VARIABLES_NAME = "analogVariablesName";
  
  String FIF_BREAKPOINT_POSITION = "positionText";
  String FIF_BREAKPOINT_POSITION_LINE = "positionLine";
  
  String FIF_DEBUG_PROGRAM_PARAMETERS = "programParameters";
  
  String FIF_META_POU_NAME = "name";
  
  String FOF_META_POU_TYPE = "type";
  String FOF_META_POU_IO = "io";
  String FOF_META_POU_IO_NAME = "name";
  String FOF_META_POU_IO_TYPE = "type";
  
  String FOF_GET_TOKENS_NAME = "name";
  String FOF_GET_TOKENS_TYPE = "type";
  String FOF_GET_TOKENS_EXPRESSION = "expression";
  
  String EF_CHANGE_POSITION_DEBUGGER_POSITION = "positionText";
  String EF_CHANGE_POSITION_DEBUGGER_POSITION_LINE = "positionLine";
  
  String EF_CHANGE_PERIOD_TIME = "time";
  
  String EF_CHANGE_VARIABLES_SCOPE = "scope";
  String EF_CHANGE_VARIABLES_NAME = "name";
  String EF_CHANGE_VARIABLES_TYPE = "type";
  String EF_CHANGE_VARIABLES_VALUE = "value";
  
  int DEBUGGER_RESUME = 0;
  int DEBUGGER_TERMINATE = 1;
  int DEBUGGER_STEP_OVER = 2;
  int DEBUGGER_GET_VARS = 3;
  int DEBUGGER_DEBUG_FINISH = 4;
  
  int INPUT_SECTION = 0;
  int IN_OUT_SECTION = 1;
  int OUTPUT_SECTION = 2;
  
  int POU_FUNCTION = 0;
  int POU_FUNCTION_BLOCK = 1;
  int POU_PROGRAM = 2;
  
  int TOKEN_COLOR_BLUE = 6;
  
  String ANALYZE_ENV_PROBLEMS = "envProblems";
  String ANALYZE_BODY_PROBLEMS = "bodyProblems";
  
  String BODY_CODE_EDITOR = "bodyEditor";
  String ACTION_CODE_EDITOR = "actionEditor";
  String VAR_CODE_EDITOR = "variablesEditor";
  String GENERATED_CODE_EDITOR = "generatedCodeEditor";
  int ENV_PROBLEMS = 0;
  int BODY_PROBLEMS = 1;
}
