package com.tibbo.aggregate.common.datatable.converter.editor;

import java.util.*;

import com.tibbo.aggregate.common.datatable.*;

public abstract class AbstractEditorOptionsConverter implements EditorOptionsConverter
{
  // See DataTableUtils.getEditorSelectionValues()
  protected final LinkedList<String> editors = new LinkedList<>();
  
  // See FieldFormat.getTypeSelectionValues()
  protected final LinkedList<String> types = new LinkedList<>();
  
  /* (non-Javadoc)
   * @see com.tibbo.aggregate.common.datatable.converter.editor.EditorOptionsConverter#convertToString(com.tibbo.aggregate.common.datatable.DataTable)
   */
  @Override
  public abstract String convertToString(DataTable options);
  
  /* (non-Javadoc)
   * @see com.tibbo.aggregate.common.datatable.converter.editor.EditorOptionsConverter#getFormat()
   */
  @Override
  public abstract TableFormat getFormat();
  
  /* (non-Javadoc)
   * @see com.tibbo.aggregate.common.datatable.converter.editor.EditorOptionsConverter#isSupportingEditor(java.lang.String)
   */
  @Override
  public boolean isSupportingEditor(String editor)
  {
    return editors.contains(editor);
  }
  
  /* (non-Javadoc)
   * @see com.tibbo.aggregate.common.datatable.converter.editor.EditorOptionsConverter#isSupportingType(java.lang.String)
   */
  @Override
  public boolean isSupportingType(String type)
  {
    return types.contains(type);
  }
  
}
