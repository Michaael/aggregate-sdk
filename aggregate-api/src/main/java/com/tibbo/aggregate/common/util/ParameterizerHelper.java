package com.tibbo.aggregate.common.util;

import java.util.*;

import org.apache.log4j.*;

import com.tibbo.aggregate.common.*;
import com.tibbo.aggregate.common.binding.*;
import com.tibbo.aggregate.common.context.*;
import com.tibbo.aggregate.common.datatable.*;
import com.tibbo.aggregate.common.datatable.encoding.*;
import com.tibbo.aggregate.common.datatable.field.*;
import com.tibbo.aggregate.common.server.*;

public class ParameterizerHelper
{
  public static final String FIELD_PARAMETERIZER = "parameterizer";
  public static final String FIELD_PARAMETERIZED = "parameterized";
  
  public static final String FIELD_PARAMETERIZER_FORMAT_FORMAT = "format";
  public static final String FIELD_PARAMETERIZER_FORMAT_PARAMETERIZED_EXSPRESSION = "parameterizedExspression";
  
  public static final TableFormat PARAMETERIZER_FORMAT = new TableFormat(1, 1);
  static
  {
    FieldFormat ff = FieldFormat.create(FIELD_PARAMETERIZER_FORMAT_FORMAT, FieldFormat.DATATABLE_FIELD, Cres.get().getString("format")).setHelp(Cres.get().getString("parameterizerFormatHelp"));
    PARAMETERIZER_FORMAT.addField(ff);
    
    ff = FieldFormat.create(FIELD_PARAMETERIZER_FORMAT_PARAMETERIZED_EXSPRESSION, FieldFormat.STRING_FIELD, Cres.get().getString("parameterizedExpression")).setEditor(StringFieldFormat.EDITOR_TEXT)
        .setEditorOptions(StringFieldFormat.TEXT_EDITOR_MODE_XML).setHelp(Cres.get().getString("parameterizerExpressionHelp"));
    PARAMETERIZER_FORMAT.addField(ff);
  }
  
  public static final DataTable createDataTable(String format, String expression)
  {
    TableFormat tableFormat = new TableFormat(format, new ClassicEncodingSettings(true));
    return createDataTable(tableFormat, expression);
  }
  
  public static final DataTable createDataTable(TableFormat tableFormat, String expression)
  {
    DataRecord rec = new DataRecord(DataTableBuilding.TABLE_FORMAT);
    rec.addInt(0);
    rec.addInt(Integer.MAX_VALUE);
    rec.addDataTable(DataTableBuilding.formatToFieldsTable(tableFormat, false, new ClassicEncodingSettings(true)));
    rec.addBoolean(false);
    rec.addBoolean(false);
    
    List<Binding> bindingsLst = tableFormat.getBindings();
    if (bindingsLst != null)
    {
      DataTable bindings = new SimpleDataTable(DataTableBuilding.BINDINGS_FORMAT);
      for (Binding binding : bindingsLst)
      {
        DataRecord bindRec = new DataRecord(DataTableBuilding.BINDINGS_FORMAT);
        bindRec.addValue(binding.getTarget());
        bindRec.addValue(binding.getExpression());
        bindings.addRecord(bindRec);
      }
      
      rec.addDataTable(bindings);
    }
    
    DataTable parameterizer = new SimpleDataTable(ParameterizerHelper.PARAMETERIZER_FORMAT);
    parameterizer.addRecord(rec.wrap(), expression);
    return parameterizer;
  }
  
  public static TableFormat getParametersTableFormat(Context context, CallerController caller, Logger logger)
  {
    TableFormat tableFormat = null;
    try
    {
      DataTable childInfo = context.getVariable(EditableChildContextConstants.V_CHILD_INFO, caller);
      DataTable parameterizer = childInfo.rec().getDataTable(FIELD_PARAMETERIZER);
      DataTable parametrizerFormatTable = parameterizer.rec().getDataTable(FIELD_PARAMETERIZER_FORMAT_FORMAT);
      tableFormat = DataTableBuilding.createTableFormat(parametrizerFormatTable, new ClassicEncodingSettings(true), true);
    }
    catch (ContextException ex)
    {
      logger.error(ex.getMessage(), ex);
    }
    
    return tableFormat;
  }
  
  public static boolean isParameterizedContext(Context context, CallerController caller, Logger logger)
  {
    boolean isParameterized = false;
    try
    {
      DataTable childInfo = context.getVariable(EditableChildContextConstants.V_CHILD_INFO, caller);
      isParameterized = childInfo.rec().getBoolean(FIELD_PARAMETERIZED);
    }
    catch (ContextException ex)
    {
      logger.error(ex.getMessage(), ex);
    }
    
    return isParameterized;
  }
}
