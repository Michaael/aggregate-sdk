package com.tibbo.aggregate.common.datatable.validator;

import com.tibbo.aggregate.common.datatable.*;

import com.tibbo.aggregate.common.util.*;

public interface RecordValidator extends Cloneable, PublicCloneable
{
  Character getType();
  
  String encode();
  
  void validate(DataTable table, DataRecord record) throws ValidationException;
}
