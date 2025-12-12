package com.tibbo.aggregate.common.expression.function.table;

import java.util.LinkedList;
import java.util.List;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.datatable.DataRecord;
import com.tibbo.aggregate.common.datatable.DataTable;
import com.tibbo.aggregate.common.datatable.DataTableReplication;
import com.tibbo.aggregate.common.datatable.DataTableUtils;
import com.tibbo.aggregate.common.datatable.FieldFormat;
import com.tibbo.aggregate.common.datatable.SimpleDataTable;
import com.tibbo.aggregate.common.datatable.TableFormat;
import com.tibbo.aggregate.common.expression.EvaluationEnvironment;
import com.tibbo.aggregate.common.expression.EvaluationException;
import com.tibbo.aggregate.common.expression.Evaluator;
import com.tibbo.aggregate.common.expression.Function;
import com.tibbo.aggregate.common.expression.function.AbstractFunction;

public class SubtableFunction extends AbstractFunction
{
  public SubtableFunction()
  {
    super("subtable", Function.GROUP_DATA_TABLE_PROCESSING, "DataTable table, Integer firstRecord, Integer recordCount, String field1, String field2, ...", "DataTable", Cres.get().getString("fDescSubtable"));
  }
  
  @Override
  public Object execute(Evaluator evaluator, EvaluationEnvironment environment, Object... parameters) throws EvaluationException
  {
    checkParameters(2, true, parameters);
    
    checkParameterType(0, parameters[0], DataTable.class);
    
    DataTable table = (DataTable) parameters[0];
    
    boolean oldVersion = parameters.length < 4;
    if (parameters.length >= 4)
    {
      oldVersion |= parameters[1] != null && !Number.class.isAssignableFrom(parameters[1].getClass()) && parameters[2] != null && !Number.class.isAssignableFrom(parameters[2].getClass());
    }
    
    if (oldVersion)
    {
      return oldSubtable(table, parameters);
    }
    else
    {
      return newSubtable(table, parameters);
    }
  }
  
  private Object newSubtable(DataTable table, Object[] parameters)
  {
    Integer firstRecord = parameters[1] != null ? ((Number) parameters[1]).intValue() : null;
    Integer recordCount = parameters[2] != null ? ((Number) parameters[2]).intValue() : null;
    
    if (firstRecord == null)
    {
      firstRecord = 0;
    }
    
    if (recordCount == null)
    {
      recordCount = table.getRecordCount();
    }
    
    List<String> fields = new LinkedList<>();
    
    for (int i = 3; i < parameters.length; i++)
    {
      fields.add(parameters[i].toString());
    }
    
    TableFormat rf = new TableFormat(Math.min(table.getFormat().getMinRecords(), recordCount), table.getFormat().getMaxRecords());
    rf.setBindings(DataTableUtils.filterBindings(table.getFormat(), fields));
    
    for (String field : fields)
    {
      FieldFormat ff = table.getFormat(field);
      if (ff != null)
      {
        rf.addField(ff);
      }
    }
    
    DataTable result = new SimpleDataTable(rf);
    
    int lastRecord = firstRecord + recordCount;
    
    for (int i = firstRecord; i < lastRecord; i++)
    {
      if (i >= table.getRecordCount())
      {
        break;
      }
      
      DataRecord rec = result.addRecord();
      
      DataTableReplication.copyRecord(table.getRecord(i), rec, true, true);
    }
    
    return result;
  }
  
  private static DataTable oldSubtable(DataTable table, Object[] parameters)
  {
    List<String> fields = new LinkedList<>();
    
    for (int i = 1; i < parameters.length; i++)
    {
      fields.add(parameters[i].toString());
    }
    
    return DataTableUtils.makeSubtable(table, fields);
  }
}
