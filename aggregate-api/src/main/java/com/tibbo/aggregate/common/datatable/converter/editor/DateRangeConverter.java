package com.tibbo.aggregate.common.datatable.converter.editor;

import com.google.common.collect.ImmutableMap;
import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.datatable.DataTable;
import com.tibbo.aggregate.common.datatable.FieldFormat;
import com.tibbo.aggregate.common.datatable.TableFormat;
import com.tibbo.aggregate.common.datatable.field.DataTableFieldFormat;
import com.tibbo.aggregate.common.datatable.field.DateFieldFormat;

public class DateRangeConverter extends AbstractEditorOptionsConverter
{
  public static final String DATE_FIELD_EDITOR = "dateFieldEditor";
  
  public static TableFormat FORMAT = new TableFormat();
  
  static
  {
    FieldFormat<String> ff = FieldFormat.create(DATE_FIELD_EDITOR, FieldFormat.STRING_FIELD, Cres.get().getString("dtEditorViewer"));
    ff.setSelectionValues(ImmutableMap.of(DateFieldFormat.EDITOR_DATE, Cres.get().getString("date"), DateFieldFormat.EDITOR_TIME, Cres.get().getString("time")));
    ff.setNullable(true);
    FORMAT.addField(ff);
  }
  
  public DateRangeConverter()
  {
    editors.add(DataTableFieldFormat.EDITOR_DATE_RANGE);
    types.add(String.valueOf(FieldFormat.DATATABLE_FIELD));
  }
  
  @Override
  public String convertToString(DataTable options)
  {
    return options.rec().getString(DATE_FIELD_EDITOR);
  }
  
  @Override
  public TableFormat getFormat()
  {
    return FORMAT;
  }
}
