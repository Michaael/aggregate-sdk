package com.tibbo.aggregate.common.expression.function.table;

import java.util.LinkedHashMap;
import java.util.Map;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.datatable.DataRecord;
import com.tibbo.aggregate.common.datatable.DataTable;
import com.tibbo.aggregate.common.datatable.FieldFormat;
import com.tibbo.aggregate.common.datatable.TableFormat;
import com.tibbo.aggregate.common.expression.EvaluationEnvironment;
import com.tibbo.aggregate.common.expression.EvaluationException;
import com.tibbo.aggregate.common.expression.Evaluator;
import com.tibbo.aggregate.common.expression.Expression;

public class AddColumnsExFunction extends AddColumnsFunction
{
  public AddColumnsExFunction()
  {
    super("addColumnsEx", "DataTable table, DataTable columns, boolean concurrent", Cres.get().getString("fDescAddColumnsEx"));
  }
  
  @Override
  public Object execute(Evaluator evaluator, EvaluationEnvironment environment, Object... parameters) throws EvaluationException
  {
    checkParameters(3, true, parameters);
    checkParameterType(0, parameters[0], DataTable.class);
    
    DataTable table = ((DataTable) parameters[0]).clone();
    
    checkParameterType(0, parameters[1], DataTable.class);
    DataTable columns = ((DataTable) parameters[1]).clone();
    checkParameterType(0, parameters[2], Boolean.class);
    boolean concurrent = (boolean) parameters[2];
    
    TableFormat clone = table.getFormat().clone();
    
    Map<FieldFormat, Expression> newFields = new LinkedHashMap<>();
    
    for (DataRecord dataRecord : columns)
    {
      FieldFormat ff = FieldFormat.create(dataRecord.getValue(0).toString());
      newFields.put(ff, new Expression(dataRecord.getValue(1).toString()));
      clone.addField(ff);
    }
    
    return processAddColumns(evaluator, environment, table, clone, newFields, concurrent);
  }
}
