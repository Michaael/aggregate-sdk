package com.tibbo.aggregate.common.expression.function.string;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.expression.EvaluationEnvironment;
import com.tibbo.aggregate.common.expression.EvaluationException;
import com.tibbo.aggregate.common.expression.Evaluator;
import com.tibbo.aggregate.common.expression.Function;
import com.tibbo.aggregate.common.expression.function.AbstractFunction;

public class UrlDecodeFunction extends AbstractFunction
{
  public UrlDecodeFunction()
  {
    super("urlDecode", Function.GROUP_STRING_PROCESSING, "String string, String encoding", "String", Cres.get().getString("fDescUrlDecode"));
  }
  
  @Override
  public Object execute(Evaluator evaluator, EvaluationEnvironment environment, Object... parameters) throws EvaluationException
  {
    checkParameters(2, false, parameters);
    
    String source = parameters[0].toString();
    String encoding = parameters[1].toString();
    
    try
    {
      return URLDecoder.decode(source, encoding);
    }
    catch (UnsupportedEncodingException ex)
    {
      throw new EvaluationException(ex.getMessage(), ex);
    }
  }
}
