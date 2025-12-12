package com.tibbo.aggregate.common.datatable.field;

import java.util.*;

import com.tibbo.aggregate.common.data.*;
import com.tibbo.aggregate.common.datatable.*;
import com.tibbo.aggregate.common.datatable.encoding.*;

public class DataFieldFormat extends FieldFormat<Data>
{
  public static final String EDITOR_TEXT = "dtext";
  public static final String EDITOR_IMAGE = "image";
  public static final String EDITOR_SOUND = "sound";
  public static final String EDITOR_HEX = "hex";
  public static final String EDITOR_REPORT = "report";
  public static final String EDITOR_REFERENCE = "reference";
  
  public static final String EXTENSIONS_DESCR_FIELD = "extensionsDescr";
  public static final String MODE_FIELD = "mode";
  public static final String EXTENSIONS_FIELD = "extensions";
  public static final String EXTENSION_FIELD = "extensionsFolder";
  
  public static final String FOLDER_FIELD = "folder";
  
  public static TableFormat EXTENSIONS_FORMAT = new TableFormat();
  
  public static TableFormat DATA_EDITOR_OPTIONS_FORMAT = new TableFormat(1, 1);
  
  static
  {
    FieldFormat modeF = FieldFormat.create(MODE_FIELD, StringFieldFormat.STRING_FIELD);
    modeF.setNullable(true);
    
    FieldFormat edF = FieldFormat.create(EXTENSIONS_DESCR_FIELD, StringFieldFormat.STRING_FIELD);
    edF.setNullable(true);
    
    // Default value for 'extensions' field
    FieldFormat extF = FieldFormat.create(EXTENSION_FIELD, StringFieldFormat.STRING_FIELD);
    EXTENSIONS_FORMAT.addField(extF);
    DataTable dt = new SimpleDataTable(EXTENSIONS_FORMAT);
    
    FieldFormat<DataTable> extsF = FieldFormat.create(EXTENSIONS_FIELD, DataTableFieldFormat.DATATABLE_FIELD);
    extsF.setDefault(dt);
    extsF.setNullable(true);
    
    FieldFormat folderF = FieldFormat.create(FOLDER_FIELD, DataTableFieldFormat.STRING_FIELD);
    folderF.setNullable(true);
    
    DATA_EDITOR_OPTIONS_FORMAT.addField(modeF);
    DATA_EDITOR_OPTIONS_FORMAT.addField(edF);
    DATA_EDITOR_OPTIONS_FORMAT.addField(extsF);
    DATA_EDITOR_OPTIONS_FORMAT.addField(folderF);
  }
  
  public DataFieldFormat(String name)
  {
    super(name);
    setTransferEncode(true);
  }
  
  @Override
  public char getType()
  {
    return FieldFormat.DATA_FIELD;
  }
  
  @Override
  public Class getFieldClass()
  {
    return Data.class;
  }
  
  @Override
  public Class getFieldWrappedClass()
  {
    return Data.class;
  }
  
  @Override
  public Data getNotNullDefault()
  {
    Data data = new Data();
    data.setShallowCopy(isShallow());
    return data;
  }
  
  @Override
  public Data valueFromString(String value, ClassicEncodingSettings settings, boolean validate)
  {
    try
    {
      Data data = new Data(value);
      data.setShallowCopy(isShallow());
      return data;
    }
    catch (Exception ex)
    {
      throw new IllegalArgumentException("Invalid data block: " + ex.getMessage(), ex);
    }
  }
  
  @Override
  public String valueToString(Data value, ClassicEncodingSettings settings)
  {
    if (value == null)
    {
      return null;
    }
    
    return value.encode();
  }
  
  @Override
  public StringBuilder valueToEncodedString(Data value, ClassicEncodingSettings settings, StringBuilder sb, Integer encodeLevel)
  {
    if (value != null)
    {
      value.encode(sb, settings, isTransferEncode(), encodeLevel);
      
      return sb;
    }
    
    return super.valueToEncodedString(value, settings, sb, encodeLevel);
  }
  
  @Override
  public List<String> getSuitableEditors()
  {
    return Arrays.asList(EDITOR_LIST, EDITOR_TEXT, EDITOR_IMAGE, EDITOR_SOUND, EDITOR_HEX, EDITOR_REPORT);
  }
  
  public static String encodeTextEditorOptions(String mode)
  {
    return encodeTextEditorOptions(mode, null, null, null);
  }
  
  public static String encodeTextEditorOptions(String extensionsDescription, String folder, List<String> extensions)
  {
    return encodeTextEditorOptions(null, extensionsDescription, folder, extensions);
  }
  
  public static String encodeTextEditorOptions(String mode, String extensionsDescription, String folder, List<String> extensions)
  {
    DataTable esdt = null;
    if (extensions != null)
    {
      esdt = new SimpleDataTable(EXTENSIONS_FORMAT);
      for (String ext : extensions)
      {
        DataRecord dr = esdt.addRecord();
        dr.setValue(EXTENSION_FIELD, ext);
      }
    }
    DataTable eodt = new SimpleDataTable(DATA_EDITOR_OPTIONS_FORMAT);
    DataRecord dr = eodt.addRecord();
    dr.setValue(MODE_FIELD, mode);
    dr.setValue(FOLDER_FIELD, folder);
    dr.setValue(EXTENSIONS_DESCR_FIELD, extensionsDescription);
    dr.setValue(EXTENSIONS_FIELD, esdt);
    
    return eodt.encode();
  }
}
