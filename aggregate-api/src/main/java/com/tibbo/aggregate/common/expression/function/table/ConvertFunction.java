package com.tibbo.aggregate.common.expression.function.table;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.datatable.DataTable;
import com.tibbo.aggregate.common.datatable.DataTableReplication;
import com.tibbo.aggregate.common.datatable.SimpleDataTable;
import com.tibbo.aggregate.common.datatable.TableFormat;
import com.tibbo.aggregate.common.datatable.encoding.ClassicEncodingSettings;
import com.tibbo.aggregate.common.expression.EvaluationEnvironment;
import com.tibbo.aggregate.common.expression.EvaluationException;
import com.tibbo.aggregate.common.expression.Evaluator;
import com.tibbo.aggregate.common.expression.Function;
import com.tibbo.aggregate.common.expression.function.AbstractFunction;
import com.tibbo.aggregate.common.util.Util;

public class ConvertFunction extends AbstractFunction
{
  public ConvertFunction()
  {
    super("convert", Function.GROUP_DATA_TABLE_PROCESSING, "DataTable table, String format", "DataTable", Cres.get().getString("fDescConvert"));
  }
  
  @Override
  public Object execute(Evaluator evaluator, EvaluationEnvironment environment, Object... parameters) throws EvaluationException
  {
    checkParameters(2, false, parameters);
    
    checkParameterType(0, parameters[0], DataTable.class);
    
    DataTable source = (DataTable) parameters[0];
    
    String formatSource = parameters[1].toString();
    
    if (formatSource == null)
    {
      return source;
    }
    
    Boolean useVisibleSeparators = true;
    
    if (parameters.length > 2)
    {
      useVisibleSeparators = Util.convertToBoolean(parameters[2], false, false);
    }
    
    TableFormat format = new TableFormat(formatSource, new ClassicEncodingSettings(useVisibleSeparators), true);
    
    DataTable target = new SimpleDataTable(format, true);
    
    if (source != null)
    {
      DataTableReplication.copy(source, target, true, true, true);
    }
    
    return target;
  }
}
