package com.tibbo.aggregate.common.dao;

import com.tibbo.aggregate.common.*;

public class DaoException extends AggreGateException
{
  public DaoException(String message)
  {
    super(message);
  }
  
  public DaoException(Throwable cause)
  {
    super(cause);
  }
  
  public DaoException(String message, Throwable cause)
  {
    super(message, cause);
  }
}
