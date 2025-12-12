package com.tibbo.aggregate.common.datatable.converter;

import java.util.Arrays;
import java.util.LinkedHashSet;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.binding.Binding;
import com.tibbo.aggregate.common.datatable.DataRecord;
import com.tibbo.aggregate.common.datatable.DataTableBindingProvider;
import com.tibbo.aggregate.common.datatable.DataTableConversion;
import com.tibbo.aggregate.common.datatable.DataTableException;
import com.tibbo.aggregate.common.datatable.FieldFormat;
import com.tibbo.aggregate.common.datatable.TableFormat;

public abstract class AbstractFormatConverter<T> implements FormatConverter<T>
{
  public static final String VF_IS_NULL = "isNull";
  public static final LinkedHashSet<String> FIELDS_TO_SKIP = new LinkedHashSet<String>(Arrays.asList(VF_IS_NULL));
  
  /**
   * Adds null-value trigger to format. Must be called after adding all fields thus it adds a disable binding to all previous added fields
   * 
   * @param format
   *          Format
   * @return Format with null value trigger
   */
  public static TableFormat deriveNullable(TableFormat format)
  {
    format = format.clone();
    for (FieldFormat fieldFormat : format.getFields())
      addDisabledBinding(format, fieldFormat);
    format.addField(FieldFormat.create("<" + VF_IS_NULL + "><B><D=" + Cres.get().getString("wNullValue") + "><A=0>"), 0);
    return format;
  }
  
  public static void addFiledToNullableFormat(TableFormat format, FieldFormat field)
  {
    format.addField(field);
    addDisabledBinding(format, field);
  }
  
  public static void removeFiledFromNullableFormat(TableFormat format, String fieldName)
  {
    for (Binding binding : format.getBindings())
    {
      if (fieldName.equals(binding.getTarget().getField()))
        format.removeBinding(binding);
    }
    format.removeField(fieldName);
  }
  
  private static void addDisabledBinding(TableFormat format, FieldFormat field)
  {
    format.addBinding(field.getName() + "#" + DataTableBindingProvider.PROPERTY_ENABLED, "!{" + VF_IS_NULL + "}");
  }
  
  private Class<T> valueClass;
  private TableFormat format;
  
  public AbstractFormatConverter(Class valueClass, TableFormat format)
  {
    super();
    this.valueClass = valueClass;
    this.format = format;
  }
  
  public AbstractFormatConverter(Class valueClass)
  {
    super();
    this.valueClass = valueClass;
  }
  
  @Override
  public Class<T> getValueClass()
  {
    return valueClass;
  }
  
  public void setValueClass(Class<T> valueClass)
  {
    this.valueClass = valueClass;
  }
  
  @Override
  public TableFormat getFormat()
  {
    return format;
  }
  
  @Override
  public FieldFormat createFieldFormat(String name)
  {
    return DataTableConversion.createTableField(name, format);
  }
  
  @Override
  public T instantiate(DataRecord source) throws InstantiationException
  {
    try
    {
      return convertToBean(source.wrap(), null);
    }
    catch (Exception ex)
    {
      throw new IllegalStateException(ex);
    }
  }
  
  @Override
  public T clone(T value, boolean useConversion)
  {
    if (useConversion)
    {
      Object fieldValue = convertToTable(value);
      return convertToBean(fieldValue, null);
    }
    else
    {
      return null;
    }
  }
  
  @Override
  public Object convertToTable(T value)
  {
    return convertToTable(value, null);
  }
  
  protected static Object simpleToTable(Object value, TableFormat format)
  {
    try
    {
      return DataTableConversion.beanToRecord(value, format, true, false, FIELDS_TO_SKIP).wrap();
    }
    catch (DataTableException ex)
    {
      throw new IllegalStateException(ex);
    }
  }
}
