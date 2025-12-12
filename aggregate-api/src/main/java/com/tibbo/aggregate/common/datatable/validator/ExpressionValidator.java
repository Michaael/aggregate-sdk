package com.tibbo.aggregate.common.datatable.validator;

import java.text.*;
import java.util.regex.*;

import com.tibbo.aggregate.common.*;
import com.tibbo.aggregate.common.context.*;
import com.tibbo.aggregate.common.datatable.*;
import com.tibbo.aggregate.common.expression.*;
import com.tibbo.aggregate.common.util.*;

public class ExpressionValidator extends AbstractFieldValidator
{
  private static final String SEPARATOR = "^^";
  private static final String SEPARATOR_REGEX = "\\^\\^";
  
  private String expression;
  private String message;
  
  public ExpressionValidator(String source)
  {
    String[] parts = source.split(SEPARATOR_REGEX);
    
    expression = parts[0];
    
    if (parts.length > 1)
    {
      message = parts[1];
    }
  }
  
  public ExpressionValidator(String expression, String message)
  {
    this.expression = expression;
    this.message = message;
  }
  
  public boolean shouldEncode()
  {
    return true;
  }
  
  public String encode()
  {
    return expression + (message != null ? SEPARATOR + message : "");
  }
  
  public Character getType()
  {
    return FieldFormat.VALIDATOR_EXPRESSION;
  }
  
  public Boolean evaluateExpression(Context context, ContextManager contextManager, CallerController caller, Object value) throws ValidationException
  {
    Boolean result = false;
    final Evaluator evaluator = new Evaluator(contextManager, context, null, caller);
    evaluator.getEnvironmentResolver().getEnvironment().put("value", value);
    
    if (expression != null && !expression.isEmpty())
    {
      try
      {
        result = evaluator.evaluateToBooleanOrNull(new Expression(expression));
        if (result == null)
        {
          result = false;
        }
      }
      catch (SyntaxErrorException ex)
      {
        throw new ValidationException(ex.getMessage(), ex);
      }
      catch (EvaluationException ex)
      {
        throw new ValidationException(ex.getMessage(), ex);
      }
    }
    
    return result;
  }
  
  public Object validate(Context context, ContextManager contextManager, CallerController caller, Object value) throws ValidationException
  {
    Boolean evaluationResult = false;
    try
    {
      evaluationResult = evaluateExpression(context, contextManager, caller, value);
      if (!evaluationResult)
      {
        throw new ValidationException(message != null ? message : MessageFormat.format(Cres.get().getString("dtValueDoesNotMatchExpression"), value, expression));
      }
    }
    catch (PatternSyntaxException ex)
    {
      throw new ValidationException(ex.getMessage(), ex);
    }
    
    return value;
  }
  
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((message == null) ? 0 : message.hashCode());
    result = prime * result + ((expression == null) ? 0 : expression.hashCode());
    return result;
  }
  
  public boolean equals(Object obj)
  {
    if (this == obj)
    {
      return true;
    }
    if (!super.equals(obj))
    {
      return false;
    }
    if (getClass() != obj.getClass())
    {
      return false;
    }
    ExpressionValidator other = (ExpressionValidator) obj;
    if (message == null)
    {
      if (other.message != null)
      {
        return false;
      }
    }
    else if (!message.equals(other.message))
    {
      return false;
    }
    if (expression == null)
    {
      if (other.expression != null)
      {
        return false;
      }
    }
    else if (!expression.equals(other.expression))
    {
      return false;
    }
    return true;
  }
}
