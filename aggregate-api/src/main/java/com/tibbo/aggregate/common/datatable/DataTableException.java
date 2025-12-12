package com.tibbo.aggregate.common.datatable;

import com.tibbo.aggregate.common.context.*;

public class DataTableException extends ContextException
{
  public DataTableException(String message)
  {
    super(message);
  }
  
  public DataTableException(Throwable cause)
  {
    super(cause);
  }
  
  public DataTableException(String message, Throwable cause)
  {
    super(message, cause);
  }
}
