package com.tibbo.aggregate.common.view;

import com.tibbo.aggregate.common.*;

public class StorageException extends AggreGateException
{
  
  public StorageException(String message)
  {
    super(message);
  }
  
  public StorageException(String message, String details)
  {
    super(message, details);
  }
  
  public StorageException(Throwable cause)
  {
    super(cause);
  }
  
  public StorageException(String message, Throwable cause)
  {
    super(message, cause);
  }
  
  public StorageException(String message, Throwable cause, String details)
  {
    super(message, cause, details);
  }
  
}
