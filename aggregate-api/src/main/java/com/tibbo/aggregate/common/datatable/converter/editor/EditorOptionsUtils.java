package com.tibbo.aggregate.common.datatable.converter.editor;

import java.util.*;

import com.tibbo.aggregate.common.*;
import com.tibbo.aggregate.common.context.*;
import com.tibbo.aggregate.common.datatable.*;
import com.tibbo.aggregate.common.util.*;

public class EditorOptionsUtils
{
  private static LinkedList<AbstractEditorOptionsConverter> CONVERTERS = new LinkedList();
  static
  {
    CONVERTERS.add(new ExpressionConverter());
    CONVERTERS.add(new ReferenceConverter());
    CONVERTERS.add(new ContextMaskConverter());
    CONVERTERS.add(new InstanceConverter());
    CONVERTERS.add(new ForeignInstanceConverter());
    CONVERTERS.add(new FormatValueConverter());
    CONVERTERS.add(new DateRangeConverter());
  }
  
  private static EditorOptionsConverter getConverter(String type, String editor)
  {
    for (EditorOptionsConverter converter : CONVERTERS)
    {
      if (converter.isSupportingEditor(editor) && converter.isSupportingType(type))
        return converter;
    }
    
    Log.CONVERTER.debug("Not found converter for editor: " + editor + " and type: " + type);
    return null;
  }
  
  public static String convertToString(DataRecord fdata)
  {
    Log.CONVERTER.debug("Starting convertToString, fdata: " + fdata);
    
    String type = fdata.getString(DataTableBuilding.FIELD_FIELDS_FORMAT_TYPE);
    String editor = fdata.getString(DataTableBuilding.FIELD_FIELDS_FORMAT_EDITOR);
    DataTable options = fdata.getDataTable(DataTableBuilding.FIELD_FIELDS_FORMAT_EDITOR_OPTIONS);
    if (options == null)
      return null;
    
    if (options.getRecordCount() == 0)
      return "";
    
    EditorOptionsConverter converter = getConverter(type, editor);
    
    if (converter != null)
      return converter.convertToString(options);
    
    if (options.rec().hasField(DataTableBuilding.FIELD_EDITOR_OPTIONS_SIMPLE_FORMAT_OPTIONS))
    {
      String eo = options.rec().getString(DataTableBuilding.FIELD_EDITOR_OPTIONS_SIMPLE_FORMAT_OPTIONS);
      return StringUtils.isEmpty(eo) ? null : eo;
    }
    
    return null;
  }
  
  public static DataTable createEditorOptionsTable(String type, String editor, String editorOptions)
  {
    Log.CONVERTER.debug("Starting convertToDataTable, type: " + type + ", editor: " + editor);
    
    EditorOptionsConverter converter = getConverter(type, editor);
    
    if (converter != null)
    {
      DataTable table = new SimpleDataTable(converter.getFormat());
      
      if (editorOptions != null)
      {
        try
        {
          DataTable eot = new SimpleDataTable(editorOptions);
          DataTableReplication.copy(eot, table);
        }
        catch (DataTableException ex)
        {
          throw new ContextRuntimeException(ex.getMessage(), ex);
        }
      }
      
      return table;
    }
    
    return new SimpleDataTable(DataTableBuilding.EDITOR_OPTIONS_SIMPLE_FORMAT, editorOptions != null ? editorOptions : new String());
  }
  
  public static DataTable createEditorOptionsTable(String fieldType, String editor)
  {
    return createEditorOptionsTable(fieldType, editor, null);
  }
  
  public static boolean hasConverter(String type, String editor)
  {
    return getConverter(type, editor) != null ? true : false;
  }
  
}
