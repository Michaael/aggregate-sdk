package com.tibbo.aggregate.common.datatable.validator;

import com.tibbo.aggregate.common.datatable.*;
import com.tibbo.aggregate.common.util.*;

public abstract class AbstractFieldValidator implements FieldValidator
{
  public AbstractFieldValidator()
  {
    
  }
  
  public AbstractFieldValidator(String source)
  {
    
  }
  
  @Override
  public boolean shouldEncode()
  {
    return false;
  }
  
  @Override
  public String encode()
  {
    return "";
  }
  
  @Override
  public Object validate(Object value) throws ValidationException
  {
    return validate(null, null, null, value);
  }
  
  @Override
  public boolean equals(Object obj)
  {
    if (obj == null)
    {
      return false;
    }
    
    if (!(obj instanceof FieldValidator))
    {
      return false;
    }
    
    FieldValidator other = (FieldValidator) obj;
    
    if (!Util.equals(getType(), other.getType()))
    {
      return false;
    }
    
    if (!Util.equals(shouldEncode(), other.shouldEncode()))
    {
      return false;
    }
    
    if (!Util.equals(encode(), other.encode()))
    {
      return false;
    }
    
    return true;
  }
  
  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    
    if (getType() != null)
      result = prime * result + getType();
    
    result = prime * result + encode().hashCode();
    result = prime * result + (shouldEncode() ? 1231 : 1237);
    
    return result;
  }
  
  @Override
  public Character getType()
  {
    return null;
  }
  
  @Override
  public Object clone()
  {
    try
    {
      return super.clone();
    }
    catch (CloneNotSupportedException ex)
    {
      throw new IllegalStateException(ex);
    }
  }
}
