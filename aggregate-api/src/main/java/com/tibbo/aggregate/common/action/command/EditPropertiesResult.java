package com.tibbo.aggregate.common.action.command;

import java.util.*;

import com.tibbo.aggregate.common.action.*;
import com.tibbo.aggregate.common.action.command.*;
import com.tibbo.aggregate.common.datatable.*;

public class EditPropertiesResult extends DefaultActionResult
{
  private final String code;
  private final List<String> changedProperties;
  
  public EditPropertiesResult(String code)
  {
    this(code, new LinkedList());
  }
  
  public EditPropertiesResult(String code, List<String> changedProperties)
  {
    this.code = code;
    this.changedProperties = changedProperties;
  }
  
  public String getCode()
  {
    return code;
  }
  
  public List<String> getChangedProperties()
  {
    return changedProperties;
  }
  
  public static EditPropertiesResult parse(GenericActionResponse resp)
  {
    DataTable ps = resp.getParameters();
    
    if (ps == null || ps.getRecordCount() == 0)
    {
      return new EditPropertiesResult(ActionUtils.RESPONSE_CLOSED);
    }
    
    String result = ps.rec().getString(EditProperties.RF_EDIT_PROPERTIES_RESULT);
    
    ActionUtils.checkResponseCode(result);
    
    List<String> savedProperties = new LinkedList();
    
    for (DataRecord rec : ps.rec().getDataTable(EditProperties.RF_EDIT_PROPERTIES_CHANGED_PROPERTIES))
    {
      savedProperties.add(rec.getString(EditProperties.FIELD_PROPERTIES_PROPERTY));
    }
    
    return new EditPropertiesResult(result, savedProperties);
  }
  
}
