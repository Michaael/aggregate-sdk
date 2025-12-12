package com.tibbo.aggregate.common.query;

import com.tibbo.aggregate.common.datatable.*;

public class FieldDescriptor
{
  private final String fieldName;
  private final String fieldDescription;
  private final TypeMapper.Type type;
  private FieldFormat format;
  private final String columnName;
  
  public FieldDescriptor(String columnName, String name, String description, TypeMapper.Type type)
  {
    super();
    this.columnName = columnName;
    this.fieldName = name;
    this.fieldDescription = description;
    this.type = type;
  }
  
  public String getColumnName()
  {
    return columnName;
  }
  
  public String getFieldName()
  {
    return fieldName;
  }
  
  public String getFieldDescription()
  {
    return fieldDescription;
  }
  
  public TypeMapper.Type getType()
  {
    return type;
  }
  
  public FieldFormat getFormat()
  {
    return format;
  }
  
  public void setFormat(FieldFormat format)
  {
    this.format = format;
  }
  
  @Override
  public String toString()
  {
    return "FieldDescriptor [name=" + fieldName + ", description=" + fieldDescription + ", type=" + type + "]";
  }
}
