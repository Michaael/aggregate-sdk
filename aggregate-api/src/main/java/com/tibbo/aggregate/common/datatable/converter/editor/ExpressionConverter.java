package com.tibbo.aggregate.common.datatable.converter.editor;

import com.tibbo.aggregate.common.datatable.*;
import com.tibbo.aggregate.common.datatable.field.*;
import com.tibbo.aggregate.common.expression.*;

import java.util.*;

public class ExpressionConverter extends AbstractEditorOptionsConverter
{
  public static final String FIELD_DEFAULT_CONTEXT = StringFieldFormat.FIELD_DEFAULT_CONTEXT;
  public static final String FIELD_DEFAULT_TABLE = StringFieldFormat.FIELD_DEFAULT_TABLE;
  public static final String FIELD_REFERENCES = StringFieldFormat.FIELD_REFERENCES;
  
  public static final String FIELD_ADDITIONAL_REFERENCES_DESCRIPTION = StringFieldFormat.FIELD_ADDITIONAL_REFERENCES_DESCRIPTION;
  public static final String FIELD_ADDITIONAL_REFERENCES_REFERENCE = StringFieldFormat.FIELD_ADDITIONAL_REFERENCES_REFERENCE;
  
  public static TableFormat FORMAT = StringFieldFormat.EXPRESSION_BUILDER_OPTIONS_FORMAT;
  public static TableFormat REFERENCES_FORMAT = StringFieldFormat.ADDITIONAL_REFERENCES_FORMAT;
  
  public ExpressionConverter()
  {
    editors.add(StringFieldFormat.EDITOR_EXPRESSION);
    types.add(String.valueOf(FieldFormat.STRING_FIELD));
  }
  
  @Override
  public String convertToString(DataTable options)
  {
    Map<Reference, String> additionalReferences = new LinkedHashMap();
    
    String defaultContext = options.rec().getString(FIELD_DEFAULT_CONTEXT);
    DataTable defaultTable = options.rec().getDataTable(FIELD_DEFAULT_TABLE);
    DataTable references = options.rec().getDataTable(FIELD_REFERENCES);
    
    if (references != null)
    {
      for (DataRecord rec : references)
        additionalReferences.put(new Reference(rec.getString(FIELD_ADDITIONAL_REFERENCES_REFERENCE)), rec.getString(FIELD_ADDITIONAL_REFERENCES_DESCRIPTION));
    }
    
    return StringFieldFormat.encodeExpressionEditorOptions(defaultContext,defaultTable,additionalReferences);
  }
  
  @Override
  public TableFormat getFormat()
  {
    return FORMAT;
  }
}
