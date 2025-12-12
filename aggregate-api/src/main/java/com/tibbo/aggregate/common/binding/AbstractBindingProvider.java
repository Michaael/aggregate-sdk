package com.tibbo.aggregate.common.binding;

import com.tibbo.aggregate.common.*;
import com.tibbo.aggregate.common.device.*;
import com.tibbo.aggregate.common.expression.*;
import com.tibbo.aggregate.common.util.*;
import org.apache.commons.lang3.exception.*;
import org.apache.log4j.*;

import java.text.*;
import java.util.*;

public abstract class AbstractBindingProvider<T> implements BindingProvider<T>
{
  final static Logger LOGGER = Log.BINDINGS;
  
  private ErrorCollector errorCollector;
  
  public AbstractBindingProvider()
  {
    super();
  }
  
  public AbstractBindingProvider(ErrorCollector errorCollector)
  {
    super();
    this.errorCollector = errorCollector;
  }
  
  @Override
  public void processExecution(int method, Binding binding, EvaluationOptions options, Reference cause, Object result)
  {
    if (LOGGER.isDebugEnabled())
    {
      LOGGER.debug(buildExecutionMessage(method, binding, options, cause, result));
    }
  }
  
  protected String buildExecutionMessage(int method, Binding binding, EvaluationOptions options, Reference cause, Object result)
  {
    String res = "";
    switch (method)
    {
      case EvaluationOptions.STARTUP:
        res = "Evaluating '" + binding.getExpression() + "' on startup and writing result (" + result + ") into '" + binding.getTarget() + "'";
        break;
      case EvaluationOptions.EVENT:
        res = "Change of '" + cause + "' caused evaluation of '" + binding.getExpression() + "' and writing result (" + result + ") into '" + binding.getTarget() + "'";
        break;
      case EvaluationOptions.PERIODIC:
        res = "Periodical evaluation of '" + binding.getExpression() + "' caused writing result (" + result + ") into '" + binding.getTarget() + "'";
        break;
    }
    return res;
  }
  
  protected String buildErrorMessage(Binding binding, int method, Reference cause, Exception error)
  {
    return MessageFormat.format(Cres.get().getString("binBindingError"), binding) + error.getMessage();
  }
  
  @Override
  public void processError(Binding binding, int method, Reference cause, Exception error)
  {
    String message = buildErrorMessage(binding, method, cause, error);
    
    if (errorCollector != null)
    {
      errorCollector.addError(new BindingException(message, error));
    }
    else
    {
      boolean disconnected = ExceptionUtils.indexOfType(error, DisconnectionException.class) != -1;
      LOGGER.log(disconnected ? Level.DEBUG : Level.ERROR, message, error);
    }
  }
  
  public List<Reference> getReferences(Binding binding) throws BindingException
  {
    try
    {
      return ExpressionUtils.findReferences(binding.getExpression());
    }
    catch (SyntaxErrorException ex)
    {
      throw new BindingException(ex.getMessage(), ex);
    }
  }
}
