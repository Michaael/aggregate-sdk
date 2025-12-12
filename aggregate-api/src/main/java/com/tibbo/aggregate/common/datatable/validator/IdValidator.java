package com.tibbo.aggregate.common.datatable.validator;

import com.tibbo.aggregate.common.context.*;
import com.tibbo.aggregate.common.datatable.*;
import com.tibbo.aggregate.common.util.*;

public class IdValidator extends AbstractFieldValidator
{
  public IdValidator()
  {
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
    return FieldFormat.VALIDATOR_ID;
  }
  
  public Object validate(Context context, ContextManager contextManager, CallerController caller, Object value) throws ValidationException
  {
    if (value != null && value instanceof String)
    {
      return Util.descriptionToName(value.toString());
    }
    
    return value;
  }
}
