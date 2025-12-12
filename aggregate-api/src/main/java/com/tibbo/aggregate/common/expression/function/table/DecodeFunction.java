package com.tibbo.aggregate.common.expression.function.table;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.datatable.SimpleDataTable;
import com.tibbo.aggregate.common.datatable.encoding.ClassicEncodingSettings;
import com.tibbo.aggregate.common.expression.EvaluationEnvironment;
import com.tibbo.aggregate.common.expression.EvaluationException;
import com.tibbo.aggregate.common.expression.Evaluator;
import com.tibbo.aggregate.common.expression.ExpressionUtils;
import com.tibbo.aggregate.common.expression.Function;
import com.tibbo.aggregate.common.expression.function.AbstractFunction;

public class DecodeFunction extends AbstractFunction
{
  public DecodeFunction()
  {
    super("decode", Function.GROUP_DATA_TABLE_PROCESSING, "String stringToDecode", "DataTable", Cres.get().getString("fDescDecode"));
  }
  
  @Override
  public Object execute(Evaluator evaluator, EvaluationEnvironment environment, Object... parameters) throws EvaluationException
  {
    checkParameters(1, false, parameters);
    
    checkParameterType(0, parameters[0], String.class);
    
    final String stringToDecode = parameters[0].toString();
    
    try
    {
      return new SimpleDataTable(stringToDecode, new ClassicEncodingSettings(ExpressionUtils.useVisibleSeparators(stringToDecode)), true);
    }
    catch (Exception exc)
    {
      throw new EvaluationException(exc.getMessage(), exc);
    }
  }
}
