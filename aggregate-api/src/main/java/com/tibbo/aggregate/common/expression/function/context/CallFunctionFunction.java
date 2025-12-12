package com.tibbo.aggregate.common.expression.function.context;

import java.util.Arrays;
import java.util.List;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.context.CallerController;
import com.tibbo.aggregate.common.context.Context;
import com.tibbo.aggregate.common.context.DefaultRequestController;
import com.tibbo.aggregate.common.context.FunctionDefinition;
import com.tibbo.aggregate.common.datatable.DataTable;
import com.tibbo.aggregate.common.datatable.DataTableConstruction;
import com.tibbo.aggregate.common.datatable.DataTableException;
import com.tibbo.aggregate.common.expression.EvaluationEnvironment;
import com.tibbo.aggregate.common.expression.EvaluationException;
import com.tibbo.aggregate.common.expression.Evaluator;
import com.tibbo.aggregate.common.expression.Function;
import com.tibbo.aggregate.common.expression.function.AbstractFunction;
import com.tibbo.aggregate.common.util.Pair;
import com.tibbo.aggregate.common.util.SyntaxErrorException;

public class CallFunctionFunction extends AbstractFunction
{
  protected static final int MINIMAL_COUNT = 2;
  
  protected final int minimalCount;
  
  public CallFunctionFunction()
  {
    this("callFunction", "String context, String function, Object parameter1, Object parameter2, ...", Cres.get().getString("fDescCallFunction"), MINIMAL_COUNT);
  }
  
  public CallFunctionFunction(String name, String footprint, String description, int minimalCount)
  {
    super(name, Function.GROUP_CONTEXT_RELATED, footprint, "Null",description);
    this.minimalCount = minimalCount;
  }
  
  @Override
  public Object execute(Evaluator evaluator, EvaluationEnvironment environment, Object... parameters) throws EvaluationException
  {
    checkParameters(minimalCount, false, parameters);
    String contextName = parameters[0].toString();

    Pair<Context, CallerController> contextAndCaller = resolveContext(contextName, evaluator);
    Context<?> context = contextAndCaller.getFirst();
    CallerController caller = contextAndCaller.getSecond();
    
    try
    {
      String functionName = parameters[1].toString();
      FunctionDefinition fd = context.getFunctionDefinition(functionName);
      
      List<Object> input = Arrays.asList(Arrays.copyOfRange(parameters, 2, parameters.length));
      
      DataTable inputTable = constructInputTable(input, fd, evaluator);

      DefaultRequestController request = new DefaultRequestController(evaluator);
      request.assignPinpoint(environment.obtainPinpoint());

      return context.callFunction(functionName, caller, request, inputTable);
    }
    catch (Exception ex)
    {
      throw new EvaluationException(ex);
    }
  }
  
  protected DataTable constructInputTable(List<Object> input, FunctionDefinition fd, Evaluator evaluator) throws SyntaxErrorException, DataTableException, EvaluationException
  {
    if (input.size() == 1 && input.get(0) instanceof DataTable)
    {
      DataTable tmpTable = (DataTable) input.get(0);
      if (tmpTable.getFormat().equals(fd.getInputFormat()))
      {
        return tmpTable;
      }
    }
    return DataTableConstruction.constructTable(input, fd != null ? fd.getInputFormat() : null, evaluator, null);
  }
}
