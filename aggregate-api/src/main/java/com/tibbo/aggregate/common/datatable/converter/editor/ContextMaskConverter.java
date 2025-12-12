package com.tibbo.aggregate.common.datatable.converter.editor;

import java.util.*;

import com.tibbo.aggregate.common.*;
import com.tibbo.aggregate.common.datatable.*;
import com.tibbo.aggregate.common.datatable.field.*;

public class ContextMaskConverter extends AbstractEditorOptionsConverter
{
  public static final String FIELD_ROOT_CONTEXT = "rootContext";
  public static final String FIELD_CONTEXT_TYPES = "contextTypes";
  public static final String FIELD_CONTEXT_MASKS = "contextMasks";
  public static final String FIELD_CONTEXT_TYPE = "contextType";
  public static final String FIELD_CONTEXT_MASK = "contextMask";
  
  private static final TableFormat TYPE_FORMAT = new TableFormat(0, Integer.MAX_VALUE);
  static
  {
    TYPE_FORMAT.addField(FieldFormat.create(FIELD_CONTEXT_TYPE, FieldFormat.STRING_FIELD, Cres.get().getString("conContextType")));
  }
  
  private static final TableFormat MASK_FORMAT = new TableFormat(0, Integer.MAX_VALUE);
  static
  {
    MASK_FORMAT.addField(FieldFormat.create(FIELD_CONTEXT_MASK, FieldFormat.STRING_FIELD, Cres.get().getString("conContextMask")));
  }
  
  public static final TableFormat FORMAT = new TableFormat(1, 1);
  static
  {
    FORMAT.addField(FieldFormat.create(FIELD_ROOT_CONTEXT, FieldFormat.STRING_FIELD, Cres.get().getString("wRoot")).setEditor(StringFieldFormat.EDITOR_CONTEXT).setNullable(true));
    FORMAT.addField(FieldFormat.create(FIELD_CONTEXT_TYPES, FieldFormat.DATATABLE_FIELD, Cres.get().getString("conContextTypes")).setDefault(new SimpleDataTable(TYPE_FORMAT)));
    FORMAT.addField(FieldFormat.create(FIELD_CONTEXT_MASKS, FieldFormat.DATATABLE_FIELD, Cres.get().getString("conContextMasks")).setDefault(new SimpleDataTable(MASK_FORMAT)));
  }
  
  public ContextMaskConverter()
  {
    editors.add(StringFieldFormat.EDITOR_CONTEXT);
    editors.add(StringFieldFormat.EDITOR_CONTEXT_MASK);
    types.add(String.valueOf(FieldFormat.STRING_FIELD));
  }
  
  @Override
  public String convertToString(DataTable options)
  {
    return options.encode();
  }
  
  @Override
  public TableFormat getFormat()
  {
    return FORMAT;
  }
  
  public static class Options extends AggreGateBean
  {
    public String rootContext;
    public List<String> contextTypes;
    public List<String> contextMasks;
    
    public Options(DataRecord data)
    {
      super(FORMAT, data);
    }
    
    public Options()
    {
      super(FORMAT);
    }
    
    public String getRootContext()
    {
      return rootContext;
    }
    
    public void setRootContext(String rootContext)
    {
      this.rootContext = rootContext;
    }
    
    public List<String> getContextTypes()
    {
      return contextTypes;
    }
    
    public void setContextTypes(List<String> contextTypes)
    {
      this.contextTypes = contextTypes;
    }
    
    public List<String> getContextMasks()
    {
      return contextMasks;
    }
    
    public void setContextMasks(List<String> contextMasks)
    {
      this.contextMasks = contextMasks;
    }
  }
  
}
