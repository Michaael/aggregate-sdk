package com.tibbo.aggregate.common.datatable.validator;

import java.text.*;
import java.util.*;

import com.tibbo.aggregate.common.*;
import com.tibbo.aggregate.common.datatable.*;
import com.tibbo.aggregate.common.util.*;

public class TableKeyFieldsValidator extends AbstractTableValidator
{
  public TableKeyFieldsValidator()
  {
    super();
  }
  
  public TableKeyFieldsValidator(String source)
  {
  }
  
  public String encode()
  {
    return "";
  }
  
  public Character getType()
  {
    return TableFormat.TABLE_VALIDATOR_KEY_FIELDS;
  }
  
  public void validate(DataTable table) throws ValidationException
  {
    List<String> keyFields = table.getFormat().getKeyFields();
    
    if (keyFields.size() == 0)
    {
      return;
    }
    Set<String> uniqueSet = new HashSet<>();
    for (DataRecord record : table)
    {
      int oldSize = uniqueSet.size();
      StringBuilder keys = new StringBuilder();
      
      for (String keyField : keyFields)
      {
        Object value = record.getValue(keyField);
        keys.append(value);
      }
      uniqueSet.add(keys.toString());
      if (oldSize == uniqueSet.size())
      {
        throw new ValidationException(MessageFormat.format(Cres.get().getString("dtKeyFieldViolation"), keys, StringUtils.print(keyFields)));
      }
    }
  }
}
