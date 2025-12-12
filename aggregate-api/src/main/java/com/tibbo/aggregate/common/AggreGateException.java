package com.tibbo.aggregate.common;

public class AggreGateException extends Exception
{
  private String code;
  private String details;
  
  public AggreGateException(String message)
  {
    super(message);
  }
  
  public AggreGateException(String message, String details)
  {
    super(message);
    this.details = details;
  }
  
  public AggreGateException(Throwable cause)
  {
    super(cause.getMessage() != null ? cause.getMessage() : cause.toString(), cause);
  }
  
  public AggreGateException(String message, Throwable cause)
  {
    super(message != null ? message : String.valueOf(cause), cause);
  }
  
  public AggreGateException(String message, Throwable cause, String details)
  {
    super(message != null ? message : String.valueOf(cause), cause);
    this.details = details;
  }
  
  public String getDetails()
  {
    return details;
  }
  
  public String getCode()
  {
    return code;
  }
  
  public void setCode(String code)
  {
    this.code = code;
  }
}
