package com.tibbo.aggregate.common.datatable.validator;

import com.tibbo.aggregate.common.context.*;
import com.tibbo.aggregate.common.datatable.*;
import com.tibbo.aggregate.common.util.*;

public interface FieldValidator<T> extends Cloneable, PublicCloneable
{
  boolean shouldEncode();
  
  Character getType();
  
  String encode();
  
  T validate(T value) throws ValidationException;
  
  T validate(Context context, ContextManager contextManager, CallerController caller, T value) throws ValidationException;
}
