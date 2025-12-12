package com.tibbo.aggregate.common.datatable.validator;

import com.tibbo.aggregate.common.*;
import com.tibbo.aggregate.common.datatable.*;
import com.tibbo.aggregate.common.expression.*;
import org.apache.commons.net.util.Base64;

public class TableExpressionValidator extends AbstractTableValidator
{
  private final Expression expression;
  
  public TableExpressionValidator(String expression)
  {
    expression = updateIfNeeded(expression);
    this.expression = new Expression(expression);
  }
  
  private String updateIfNeeded(String expression)
  {
    if (Base64.isArrayByteBase64(expression.getBytes()))
    {
      return new String(Base64.decodeBase64(expression.getBytes()));
    }
    return expression;
  }
  
  @Override
  public Character getType()
  {
    return TableFormat.TABLE_VALIDATOR_EXPRESSION;
  }
  
  @Override
  public String encode()
  {
    return Base64.encodeBase64String(expression.getText().getBytes());
  }
  
  @Override
  public void validate(DataTable table) throws ValidationException
  {
    Evaluator evaluator = new Evaluator(table);
    
    try
    {
      Object result = evaluator.evaluate(expression);
      
      if (result != null)
      {
        throw new ValidationException(result.toString());
      }
    }
    catch (ValidationException ex)
    {
      throw ex;
    }
    catch (Exception ex)
    {
      Log.DATATABLE.warn("Error evaluating data table validator's expression '" + expression + "': " + ex.getMessage(), ex);
    }
  }
}
