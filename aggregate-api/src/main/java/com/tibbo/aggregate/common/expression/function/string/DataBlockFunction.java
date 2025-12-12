package com.tibbo.aggregate.common.expression.function.string;

import java.io.UnsupportedEncodingException;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.data.Data;
import com.tibbo.aggregate.common.expression.EvaluationEnvironment;
import com.tibbo.aggregate.common.expression.EvaluationException;
import com.tibbo.aggregate.common.expression.Evaluator;
import com.tibbo.aggregate.common.expression.Function;
import com.tibbo.aggregate.common.expression.function.AbstractFunction;

public class DataBlockFunction extends AbstractFunction
{
  public DataBlockFunction()
  {
    super("dataBlock", Function.GROUP_TYPE_CONVERSION, "Object value[, String charset[, String name]]", "String", Cres.get().getString("fDescDataBlock"));
  }
  
  @Override
  public Object execute(Evaluator evaluator, EvaluationEnvironment environment, Object... parameters) throws EvaluationException
  {
    checkParameters(1, false, parameters);
    
    final String source = parameters[0].toString();
    String encoding = (parameters.length > 1) ? parameters[1].toString() : "";
    
    Data data = new Data();
    byte[] bytes;
    try
    {
      bytes = encoding.isEmpty() ? source.getBytes() : source.getBytes(encoding);
    }
    catch (UnsupportedEncodingException ex)
    {
      throw new EvaluationException(ex.getMessage(), ex);
    }
    
    data.setData(bytes);

    if (parameters.length > 2 && parameters[2] != null)
    {
      data.setName(parameters[2].toString());
    }

    return data;
  }
}
