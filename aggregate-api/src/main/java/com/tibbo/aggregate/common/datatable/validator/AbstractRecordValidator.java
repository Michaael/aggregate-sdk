package com.tibbo.aggregate.common.datatable.validator;

import com.tibbo.aggregate.common.util.*;

public abstract class AbstractRecordValidator implements RecordValidator
{
  
  @Override
  public boolean equals(Object obj)
  {
    if (obj == null)
    {
      return false;
    }
    
    if (!(obj instanceof RecordValidator))
    {
      return false;
    }
    
    RecordValidator other = (RecordValidator) obj;
    
    if (!Util.equals(getType(), other.getType()))
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
    
    return result;
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
