package com.tibbo.aggregate.common.datatable.converter.editor;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.datatable.DataTable;
import com.tibbo.aggregate.common.datatable.FieldFormat;
import com.tibbo.aggregate.common.datatable.TableFormat;
import com.tibbo.aggregate.common.datatable.validator.RegexValidator;

public class FormatValueConverter extends AbstractEditorOptionsConverter
{
  
  public static final String FIELD_FORMAT_MASK = "formatMask";
  
  public static final TableFormat FORMAT_VALUE_OPTIONS_FORMAT = new TableFormat(1, 1);
  
  static
  {
    FieldFormat<Object> ff = FieldFormat.create(FIELD_FORMAT_MASK, FieldFormat.STRING_FIELD, Cres.get().getString("formatMask"))
            .setNullable(false);
    ff.addValidator(new RegexValidator("^#*[^#]?#+[^#]?#*$", Cres.get().getString("dtInvalidFormatMask")));

    FORMAT_VALUE_OPTIONS_FORMAT.addField(ff);
  }
  
  public FormatValueConverter()
  {
    editors.add(FieldFormat.EDITOR_FORMAT_STRING);
    types.add(String.valueOf(FieldFormat.INTEGER_FIELD));
    types.add(String.valueOf(FieldFormat.LONG_FIELD));
    types.add(String.valueOf(FieldFormat.FLOAT_FIELD));
    types.add(String.valueOf(FieldFormat.DOUBLE_FIELD));
  }
  
  @Override
  public String convertToString(DataTable options)
  {
    return options.clone().encode(false);
  }
  
  @Override
  public TableFormat getFormat()
  {
    return FORMAT_VALUE_OPTIONS_FORMAT;
  }
}
