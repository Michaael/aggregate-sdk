package com.tibbo.aggregate.common.datatable.validator;

import java.text.*;
import java.util.*;

import com.tibbo.aggregate.common.*;
import com.tibbo.aggregate.common.datatable.*;
import com.tibbo.aggregate.common.util.*;

public class KeyFieldsValidator extends AbstractRecordValidator
{
  public KeyFieldsValidator()
  {
  }
  
  public KeyFieldsValidator(String source)
  {
  }
  
  public String encode()
  {
    return "";
  }
  
  public Character getType()
  {
    return TableFormat.RECORD_VALIDATOR_KEY_FIELDS;
  }
  
  public void validate(DataTable table, DataRecord record) throws ValidationException
  {
    List<String> keyFields = table.getFormat().getKeyFields();
    
    if (keyFields.size() == 0)
    {
      return;
    }
    
    DataTableQuery query = new DataTableQuery();
    List key = new LinkedList();
    
    for (String keyField : keyFields)
    {
      Object value = record.getValue(keyField);
      key.add(value);
      query.addCondition(new QueryCondition(keyField, value));
    }
    
    DataRecord rec = table.select(query);
    
    if (rec != null && rec != record)
    {
      throw new ValidationException(MessageFormat.format(Cres.get().getString("dtKeyFieldViolation"), key, StringUtils.print(keyFields)));
    }
  }
}
