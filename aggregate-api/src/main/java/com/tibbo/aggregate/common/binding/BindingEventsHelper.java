package com.tibbo.aggregate.common.binding;

import com.tibbo.aggregate.common.*;
import com.tibbo.aggregate.common.context.*;
import com.tibbo.aggregate.common.datatable.*;
import com.tibbo.aggregate.common.datatable.field.*;
import com.tibbo.aggregate.common.expression.*;
import com.tibbo.aggregate.common.util.*;

public class BindingEventsHelper
{
  public static final String E_BINDING_ERROR = "bindingError";
  public static final String E_BINDING_EXECUTION = "bindingExecution";
  
  public static final String EF_BINDING_CONTEXT = "context";
  public static final String EF_BINDING_TARGET = "target";
  public static final String EF_BINDING_EXPRESSION = "expression";
  public static final String EF_BINDING_ACTIVATOR = "activator";
  public static final String EF_BINDING_EXECUTION = "execution";
  public static final String EF_BINDING_CONDITION = "condition";
  public static final String EF_BINDING_VALUE = "value";
  public static final String EF_BINDING_CAUSE = "cause";
  public static final String EF_BINDING_ERROR = "error";
  public static final String EF_BINDING_ERROR_STACK = "stack";
  
  public static final TableFormat EFT_BINDING_EXECUTION = new TableFormat(1, 1);
  static
  {
    EFT_BINDING_EXECUTION.addField(FieldFormat.create("<" + EF_BINDING_TARGET + "><S><D=" + Cres.get().getString("target") + "><F=N><E=" + StringFieldFormat.EDITOR_CONTEXT + ">"));
    EFT_BINDING_EXECUTION.addField(FieldFormat.create("<" + EF_BINDING_EXPRESSION + "><S><F=N><D=" + Cres.get().getString("expression") + ">"));
    EFT_BINDING_EXECUTION.addField(FieldFormat.create("<" + EF_BINDING_VALUE + "><S><F=N><D=" + Cres.get().getString("value") + ">"));
    EFT_BINDING_EXECUTION.addField(FieldFormat.create("<" + EF_BINDING_ACTIVATOR + "><S><F=N><D=" + Cres.get().getString("wActivatorDescr") + ">"));
    EFT_BINDING_EXECUTION.addField(FieldFormat.create("<" + EF_BINDING_CONDITION + "><S><F=N><D=" + Cres.get().getString("condition") + ">"));
    EFT_BINDING_EXECUTION.addField(FieldFormat.create("<" + EF_BINDING_EXECUTION + "><S><D=" + Cres.get().getString("wExecution") + ">"));
    EFT_BINDING_EXECUTION.addField(FieldFormat.create("<" + EF_BINDING_CAUSE + "><S><F=N><D=" + Cres.get().getString("cause") + ">"));
  }
  
  public static final TableFormat EFT_BINDING_EXECUTION_EXT = EFT_BINDING_EXECUTION.clone();
  static
  {
    EFT_BINDING_EXECUTION_EXT.addField(FieldFormat.create("<" + EF_BINDING_CONTEXT + "><S><D=" + Cres.get().getString("context") + "><F=N><E=" + StringFieldFormat.EDITOR_CONTEXT + ">"), 0);
  }
  
  public static final TableFormat EFT_BINDING_ERROR = EFT_BINDING_EXECUTION.clone();
  static
  {
    EFT_BINDING_ERROR.removeField(EF_BINDING_VALUE);
    EFT_BINDING_ERROR.removeField(EF_BINDING_CONDITION);
    EFT_BINDING_ERROR.addField(FieldFormat.create("<" + EF_BINDING_ERROR + "><S><D=" + Cres.get().getString("error") + "><E=" + StringFieldFormat.EDITOR_TEXT_AREA + ">"));
    EFT_BINDING_ERROR.addField(FieldFormat.create("<" + EF_BINDING_ERROR_STACK + "><T><D=" + Cres.get().getString("stack") + ">"));
  }
  
  public static final TableFormat EFT_BINDING_ERROR_EXT = EFT_BINDING_ERROR.clone();
  static
  {
    EFT_BINDING_ERROR_EXT.addField(FieldFormat.create("<" + EF_BINDING_CONTEXT + "><S><D=" + Cres.get().getString("context") + "><F=N>"), 0);
  }
  
  public static DataTable createBindingErrorEventData(Context con, Binding binding, int method, String activator, Exception error)
  {
    DataTable dt = new SimpleDataTable(con == null ? EFT_BINDING_ERROR : EFT_BINDING_ERROR_EXT);
    DataRecord record = dt.addRecord();
    if (con != null)
    {
      record.setValue(EF_BINDING_CONTEXT, con.getPath());
    }
    if (binding != null)
    {
      record.setValue(EF_BINDING_TARGET, binding.getTarget().toString());
      record.setValue(EF_BINDING_EXPRESSION, binding.getExpression().toString());
    }
    if (error != null)
    {
      record.setValue(EF_BINDING_ERROR, error.getMessage());
      
      DataTable stackTable = ThreadUtils.createStackTraceTable(error.getStackTrace());
      record.setValue(EF_BINDING_ERROR_STACK, stackTable);
    }
    
    record.setValue(EF_BINDING_ACTIVATOR, activator);
    record.setValue(EF_BINDING_EXECUTION, executionTypeDescription(method));
    
    return dt;
  }
  
  public static DataTable createBindingExecutionEventData(Context con, int method, Binding binding, EvaluationOptions options, Reference cause, Object result)
  {
    final DataRecord data = new DataRecord(con == null ? EFT_BINDING_EXECUTION : EFT_BINDING_EXECUTION_EXT);
    
    if (con != null)
    {
      data.setValue(EF_BINDING_CONTEXT, con.getPath());
    }
    data.setValue(EF_BINDING_TARGET, binding.getTarget().toString());
    data.setValue(EF_BINDING_EXPRESSION, binding.getExpression().toString());
    if (options.getActivator() != null)
    {
      data.setValue(EF_BINDING_ACTIVATOR, options.getActivator().toString());
    }
    if (options.getCondition() != null)
    {
      data.setValue(EF_BINDING_CONDITION, options.getCondition().toString());
    }
    if (result != null)
    {
      data.setValue(EF_BINDING_VALUE, result.toString());
    }
    if (cause != null)
    {
      data.setValue(EF_BINDING_CAUSE, cause.toString());
    }
    
    data.setValue(EF_BINDING_EXECUTION, executionTypeDescription(method));
    
    return data.wrap();
  }
  
  private static String executionTypeDescription(int method)
  {
    String exec;
    if (method == EvaluationOptions.STARTUP)
    {
      exec = Cres.get().getString("wOnStartup");
    }
    else if (method == EvaluationOptions.EVENT)
    {
      exec = Cres.get().getString("wOnEvent");
    }
    else
    {
      exec = Cres.get().getString("wPeriodically");
    }
    return exec;
  }
  
}
