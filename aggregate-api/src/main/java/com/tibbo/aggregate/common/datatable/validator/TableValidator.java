package com.tibbo.aggregate.common.datatable.validator;

import com.tibbo.aggregate.common.datatable.*;

import com.tibbo.aggregate.common.util.*;

public interface TableValidator extends Cloneable, PublicCloneable
{
  Character getType();
  
  String encode();
  
  void validate(DataTable table) throws ValidationException;
}
