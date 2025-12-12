package com.tibbo.aggregate.common.datatable;

public class ValidationException extends DataTableException
{
  public ValidationException(String message)
  {
    super(message);
  }
  
  public ValidationException(Throwable cause)
  {
    super(cause);
  }
  
  public ValidationException(String message, Throwable cause)
  {
    super(message, cause);
  }
}
