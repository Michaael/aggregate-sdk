package com.tibbo.aggregate.common.expression.function.context;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.context.CallerController;
import com.tibbo.aggregate.common.context.Context;
import com.tibbo.aggregate.common.context.ContextException;
import com.tibbo.aggregate.common.context.EventDefinition;
import com.tibbo.aggregate.common.data.Event;
import com.tibbo.aggregate.common.datatable.DataTable;
import com.tibbo.aggregate.common.datatable.DataTableConstruction;
import com.tibbo.aggregate.common.event.EventLevel;
import com.tibbo.aggregate.common.event.FireEventRequestController;
import com.tibbo.aggregate.common.expression.EvaluationEnvironment;
import com.tibbo.aggregate.common.expression.EvaluationException;
import com.tibbo.aggregate.common.expression.Evaluator;
import com.tibbo.aggregate.common.expression.Function;
import com.tibbo.aggregate.common.expression.function.AbstractFunction;
import com.tibbo.aggregate.common.util.Pair;
import com.tibbo.aggregate.common.util.Util;

public class FireEventFunction extends AbstractFunction
{
  public FireEventFunction()
  {
    super("fireEvent", Function.GROUP_CONTEXT_RELATED, "String context, String event, Integer level, Object parameter1, Object parameter2, ...", "Long", Cres.get().getString("fDescFireEvent"));
  }
  
  @Override
  public Object execute(Evaluator evaluator, EvaluationEnvironment environment, Object... parameters) throws EvaluationException
  {
    checkParameters(3, true, parameters);

    String contextPath = parameters[0].toString();
    Pair<Context, CallerController> contextAndCaller = resolveContext(contextPath, evaluator);
    Context<?> context = contextAndCaller.getFirst();
    CallerController caller = contextAndCaller.getSecond();
    
    try
    {
      String name = parameters[1].toString();
      
      EventDefinition ed = context.getEventDefinition(name);
      
      if (ed == null)
      {
        throw new ContextException(MessageFormat.format(Cres.get().getString("conEvtNotAvailExt"), name, context.getPath()));
      }
      
      Number level = Util.convertToNumber(parameters[2], true, true);
      
      if (level != null && !EventLevel.isValid(level.intValue()))
      {
        throw new EvaluationException("Invalid event level: " + level);
      }
      
      List<Object> input = Arrays.asList(Arrays.copyOfRange(parameters, 3, parameters.length));
      
      DataTable data = (input.size() == 1 && (input.get(0) instanceof DataTable))
          ? (DataTable) input.get(0)
          : DataTableConstruction.constructTable(input, ed.getFormat(), evaluator, null);
      
      Event ev = context.fireEvent(
          ed.getName(),
          (level != null) ? level.intValue() : ed.getLevel(),
          caller,
          environment.obtainPinpoint().map(FireEventRequestController::new).orElse(null),
          data);
      
      return ev != null ? ev.getId() : null;
    }
    catch (Exception ex)
    {
      throw new EvaluationException(ex);
    }
  }
}
