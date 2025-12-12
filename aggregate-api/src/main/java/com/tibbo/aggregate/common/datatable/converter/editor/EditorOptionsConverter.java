package com.tibbo.aggregate.common.datatable.converter.editor;

import com.tibbo.aggregate.common.datatable.*;

public interface EditorOptionsConverter
{
  public String convertToString(DataTable options);
  
  public TableFormat getFormat();
  
  public boolean isSupportingEditor(String editor);
  
  public boolean isSupportingType(String type);
}