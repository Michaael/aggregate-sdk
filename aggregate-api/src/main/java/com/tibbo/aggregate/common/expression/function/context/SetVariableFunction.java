package com.tibbo.aggregate.common.expression.function.context;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.context.CallerController;
import com.tibbo.aggregate.common.context.Context;
import com.tibbo.aggregate.common.context.ContextException;
import com.tibbo.aggregate.common.context.DefaultRequestController;
import com.tibbo.aggregate.common.context.VariableDefinition;
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

public class SetVariableFunction extends AbstractFunction
{
  private static final int MINIMAL_COUNT = 2;
  
  protected final int minimalCount;
  
  public SetVariableFunction(String name, String footprint, String description, int minimalCount)
  {
    super(name, Function.GROUP_CONTEXT_RELATED, footprint, "Null", description);
    this.minimalCount = minimalCount;
  }
  
  public SetVariableFunction()
  {
    this("setVariable", "String context, String variable, Object parameter1, Object parameter2, ...", Cres.get().getString("fDescSetVariable"), MINIMAL_COUNT);
  }
  
  @Override
  public Object execute(Evaluator evaluator, EvaluationEnvironment environment, Object... parameters) throws EvaluationException
  {
    checkParameters(minimalCount, false, parameters);

    String contextPath = parameters[0].toString();
    Pair<Context, CallerController> contextAndCaller = resolveContext(contextPath, evaluator);
    Context<?> context = contextAndCaller.getFirst();
    CallerController caller = contextAndCaller.getSecond();
    
    try
    {
      String name = parameters[1].toString();
      
      VariableDefinition vd = context.getVariableDefinition(parameters[1].toString(), caller);
      
      if (vd == null)
      {
        throw new ContextException(MessageFormat.format(Cres.get().getString("conVarNotAvailExt"), name, context.getPath()));
      }
  
      DataTable valueTable = getValueTable(evaluator, vd, parameters);

      DefaultRequestController request = new DefaultRequestController(evaluator);
      request.assignPinpoint(environment.obtainPinpoint());

      context.setVariable(parameters[1].toString(), caller, request, valueTable);
      
      return null;
    }
    catch (Exception ex)
    {
      throw new EvaluationException(ex);
    }
  }
  
  protected DataTable getValueTable(Evaluator evaluator, VariableDefinition vd, Object[] parameters) throws SyntaxErrorException, EvaluationException, DataTableException
  {
    List<Object> input = Arrays.asList(Arrays.copyOfRange(parameters, 2, parameters.length));
    
    return (input.size() == 1 && (input.get(0) instanceof DataTable)) 
            ? (DataTable) input.get(0) 
            : DataTableConstruction.constructTable(input, vd.getFormat(), evaluator, null);
  }
}
