package com.tibbo.aggregate.common.expression.function.context;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.context.CallerController;
import com.tibbo.aggregate.common.context.Context;
import com.tibbo.aggregate.common.context.ContextException;
import com.tibbo.aggregate.common.context.VariableDefinition;
import com.tibbo.aggregate.common.expression.EvaluationEnvironment;
import com.tibbo.aggregate.common.expression.EvaluationException;
import com.tibbo.aggregate.common.expression.Evaluator;
import com.tibbo.aggregate.common.expression.Function;
import com.tibbo.aggregate.common.expression.function.AbstractFunction;
import com.tibbo.aggregate.common.util.Pair;
import com.tibbo.aggregate.common.util.Util;

public class SetVariableRecordFunction extends AbstractFunction
{
  public SetVariableRecordFunction()
  {
    super("setVariableRecord", Function.GROUP_CONTEXT_RELATED, "String context, String variable, Integer record, Object parameter1, Object parameter2, ...", "Null", Cres.get().getString("fDescSetVariableRecord"));
  }
  
  @Override
  public Object execute(Evaluator evaluator, EvaluationEnvironment environment, Object... parameters) throws EvaluationException
  {
    checkParameters(4, true, parameters);

    String contextPath = parameters[0].toString();
    Pair<Context, CallerController> contextAndCaller = resolveContext(contextPath, evaluator);
    Context<?> context = contextAndCaller.getFirst();
    CallerController caller = contextAndCaller.getSecond();
    
    try
    {
      VariableDefinition vd = context.getVariableDefinition(parameters[1].toString(), caller);
      
      if (vd == null)
      {
        throw new ContextException(MessageFormat.format(Cres.get().getString("conVarNotAvailExt"), parameters[1].toString(), context.getPath()));
      }
      
      List<Object> input = Arrays.asList(Arrays.copyOfRange(parameters, 3, parameters.length));
      
      int row = Util.convertToNumber(parameters[2], true, false).intValue();
      
      for (int i = 0; i < vd.getFormat().getFieldCount() && i < input.size(); i++)
      {
        context.setVariableField(parameters[1].toString(), vd.getFormat().getFieldName(i), row, input.get(i), caller);
      }
      
      return null;
    }
    catch (Exception ex)
    {
      throw new EvaluationException(ex);
    }
  }
}
