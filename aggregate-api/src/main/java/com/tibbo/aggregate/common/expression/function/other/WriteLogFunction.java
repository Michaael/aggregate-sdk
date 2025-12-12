package com.tibbo.aggregate.common.expression.function.other;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.spi.StandardLevel;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.expression.EvaluationEnvironment;
import com.tibbo.aggregate.common.expression.EvaluationException;
import com.tibbo.aggregate.common.expression.Evaluator;
import com.tibbo.aggregate.common.expression.Function;
import com.tibbo.aggregate.common.expression.function.AbstractFunction;

public class WriteLogFunction extends AbstractFunction
{
  
  private static final String PARAMS_FOOTPRINT = "Integer level, String category, Object value";
  private static final String AG_PREFIX = "ag.";
  
  public WriteLogFunction()
  {
    super("writeLog", Function.GROUP_SYSTEM, PARAMS_FOOTPRINT, "Object", Cres.get().getString("fDescWriteLog"));
  }
  
  @Override
  public Object execute(Evaluator evaluator, EvaluationEnvironment environment, Object... parameters) throws EvaluationException
  {
    checkParameters(3, true, parameters);
    
    try
    {
      StandardLevel standardLevel = StandardLevel.getStandardLevel(Integer.parseInt(parameters[0].toString()));
      Level level = Level.getLevel(standardLevel.toString());
      Logger logger = LogManager.getLogger(AG_PREFIX + parameters[1]);
      
      Object value = parameters[2];
      
      logger.log(level, value);
      
      return value;
    }
    catch (NumberFormatException e)
    {
      throw new EvaluationException("Logging level should be numeric.");
    }
  }
}
