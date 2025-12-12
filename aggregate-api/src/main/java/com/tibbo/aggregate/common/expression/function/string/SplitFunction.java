package com.tibbo.aggregate.common.expression.function.string;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.datatable.DataTable;
import com.tibbo.aggregate.common.datatable.FieldFormat;
import com.tibbo.aggregate.common.datatable.SimpleDataTable;
import com.tibbo.aggregate.common.expression.EvaluationEnvironment;
import com.tibbo.aggregate.common.expression.EvaluationException;
import com.tibbo.aggregate.common.expression.Evaluator;
import com.tibbo.aggregate.common.expression.Function;
import com.tibbo.aggregate.common.expression.function.AbstractFunction;
import com.tibbo.aggregate.common.util.Util;

public class SplitFunction extends AbstractFunction
{
  public SplitFunction()
  {
    super("split", Function.GROUP_STRING_PROCESSING, "String string, String regex[, String fieldName [, Integer limit ]]", "DataTable", Cres.get().getString("fDescSplit"));
  }
  
  @Override
  public Object execute(Evaluator evaluator, EvaluationEnvironment environment, Object... parameters) throws EvaluationException
  {
    checkParameters(2, false, parameters);
    
    String source = parameters[0].toString();
    String regexp = parameters[1].toString();
    
    String fieldName = parameters.length > 2 ? parameters[2].toString() : "element";
    
    Integer limit = parameters.length > 3 ? Util.convertToNumber(parameters[3], true, false).intValue() : 0;
    
    String[] data = source.split(regexp, limit);
    
    FieldFormat ff = FieldFormat.create(fieldName, FieldFormat.STRING_FIELD);
    
    DataTable res = new SimpleDataTable(ff.wrap());
    
    for (int i = 0; i < data.length; i++)
    {
      String element = data[i];
      res.addRecord(element);
    }
    
    return res;
  }
}
