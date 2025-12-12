package com.tibbo.aggregate.common.datatable.validator;

import com.tibbo.aggregate.common.*;
import com.tibbo.aggregate.common.context.*;
import com.tibbo.aggregate.common.datatable.*;

public class NonNullValidator extends AbstractFieldValidator
{
  private String message;
  
  public NonNullValidator()
  {
  }
  
  public NonNullValidator(String message)
  {
    if (message != null && message.length() > 0)
    {
      this.message = message;
    }
  }
  
  public boolean shouldEncode()
  {
    return true;
  }
  
  public String encode()
  {
    return "";
  }
  
  public Character getType()
  {
    return FieldFormat.VALIDATOR_NON_NULL;
  }
  
  public Object validate(Context context, ContextManager contextManager, CallerController caller, Object value) throws ValidationException
  {
    if (value == null)
    {
      throw new ValidationException(message != null ? message : Cres.get().getString("dtValueIsRequired"));
    }
    
    return value;
  }
  
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((message == null) ? 0 : message.hashCode());
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
    NonNullValidator other = (NonNullValidator) obj;
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
    return true;
  }
  
}
