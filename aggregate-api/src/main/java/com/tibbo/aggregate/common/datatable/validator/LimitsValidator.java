package com.tibbo.aggregate.common.datatable.validator;

import com.tibbo.aggregate.common.*;
import com.tibbo.aggregate.common.context.*;
import com.tibbo.aggregate.common.data.*;
import com.tibbo.aggregate.common.datatable.*;
import com.tibbo.aggregate.common.util.*;

import java.text.*;
import java.util.*;

public class LimitsValidator extends AbstractFieldValidator
{
  private static final char MIN_MAX_SEPARATOR = ' ';
  
  private Comparable min;
  private Comparable max;
  
  public LimitsValidator(FieldFormat fieldFormat, String source)
  {
    List<String> minMax = StringUtils.split(source, MIN_MAX_SEPARATOR);
    
    if (fieldFormat.getType() == FieldFormat.DATA_FIELD || fieldFormat.getType() == FieldFormat.STRING_FIELD)
    {
      if (minMax.size() > 1)
      {
        min = new Integer(minMax.get(0));
        max = new Integer(minMax.get(1));
      }
      else
      {
        max = new Integer(minMax.get(0));
      }
    }
    else
    {
      if (minMax.size() > 1)
      {
        min = (Comparable) fieldFormat.valueFromString(minMax.get(0));
        max = (Comparable) fieldFormat.valueFromString(minMax.get(1));
      }
      else
      {
        max = (Comparable) fieldFormat.valueFromString(minMax.get(0));
      }
    }
  }
  
  public LimitsValidator(Comparable min, Comparable max)
  {
    setLimits(min, max);
  }
  
  protected void setLimits(Comparable min, Comparable max)
  {
    if (min != null && max != null && !min.getClass().equals(max.getClass()))
      Log.DATATABLE.error("'min' and 'max' Limits Validator parameters should be the same type", new Throwable());
    
    this.min = min;
    this.max = max;
  }
  
  @Override
  public boolean shouldEncode()
  {
    return true;
  }
  
  @Override
  public Character getType()
  {
    return FieldFormat.VALIDATOR_LIMITS;
  }
  
  public Comparable getMin()
  {
    return min;
  }
  
  public Comparable getMax()
  {
    return max;
  }
  
  @Override
  public String encode()
  {
    return min != null ? min.toString() + (max != null ? MIN_MAX_SEPARATOR + max.toString() : "") : (max != null ? max.toString() : "");
  }
  
  public Object validate(Context context, ContextManager contextManager, CallerController caller, Object value) throws ValidationException
  {
    if (value == null)
    {
      return value;
    }
    
    if (value instanceof Data)
    {
      Data data = (Data) value;
      
      if (data.getData() != null)
      {
        Comparable size = data.getData().length;
        compare(size, null, null);
      }
    }
    else if (value instanceof String)
    {
      compare(value.toString().length(), Cres.get().getString("dtValueTooShort"), Cres.get().getString("dtValueTooLong"));
    }
    else
    {
      if (!(value instanceof Comparable))
      {
        throw new ValidationException("Value not comparable: " + value);
      }
      
      Comparable cv = (Comparable) value;
      
      compare(cv, null, null);
    }
    
    return value;
  }
  
  private void compare(Comparable cv, String smallMessage, String bigMessage) throws ValidationException
  {
    if (min != null)
    {
      if (cv.compareTo(min) < 0)
      {
        throw new ValidationException(MessageFormat.format(smallMessage != null ? smallMessage : Cres.get().getString("dtValueTooSmall"), cv, min));
      }
    }
    
    if (cv.compareTo(max) > 0)
    {
      throw new ValidationException(MessageFormat.format(bigMessage != null ? bigMessage : Cres.get().getString("dtValueTooBig"), cv, max));
    }
  }
  
  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((max == null) ? 0 : max.hashCode());
    result = prime * result + ((min == null) ? 0 : min.hashCode());
    return result;
  }
  
  @Override
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
    LimitsValidator other = (LimitsValidator) obj;
    if (max == null)
    {
      if (other.max != null)
      {
        return false;
      }
    }
    else if (!max.equals(other.max))
    {
      return false;
    }
    if (min == null)
    {
      if (other.min != null)
      {
        return false;
      }
    }
    else if (!min.equals(other.min))
    {
      return false;
    }
    return true;
  }
}
