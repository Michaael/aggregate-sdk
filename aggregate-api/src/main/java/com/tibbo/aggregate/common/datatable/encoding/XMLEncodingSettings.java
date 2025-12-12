package com.tibbo.aggregate.common.datatable.encoding;

import java.util.*;

import com.tibbo.aggregate.common.datatable.*;

public class XMLEncodingSettings extends EncodingSettings
{
  private boolean getFormatFromDefaultValue;
  
  private FieldFormat fieldFormat;
  
  private ResourceBundle bundle;
  
  /**
   * If false encoded DOM will not contain field tags for fields with default value
   */
  private boolean allFields;
  
  public XMLEncodingSettings(boolean encodeFormat)
  {
    this(encodeFormat, null);
  }
  
  public XMLEncodingSettings(boolean encodeFormat, TableFormat format)
  {
    super(encodeFormat, format);
  }
  
  public XMLEncodingSettings(boolean encodeFormat, TableFormat format, boolean getFormatFromDefaultValue, FieldFormat fieldFormat)
  {
    this(encodeFormat, format, getFormatFromDefaultValue, fieldFormat, null);
  }
  
  public XMLEncodingSettings(boolean encodeFormat, TableFormat format, boolean getFormatFromDefaultValue, FieldFormat fieldFormat, ResourceBundle bundle)
  {
    super(encodeFormat, format);
    this.getFormatFromDefaultValue = getFormatFromDefaultValue;
    this.fieldFormat = fieldFormat;
    this.bundle = bundle;
  }
  
  public void setFieldFormat(FieldFormat fieldFormat)
  {
    this.fieldFormat = fieldFormat;
  }
  
  public FieldFormat getFieldFormat()
  {
    return fieldFormat;
  }
  
  public boolean isGetFormatFromDefaultValue()
  {
    return getFormatFromDefaultValue;
  }
  
  public void setGetFormatFromDefaultValue(boolean getFormatFromDefaultValue)
  {
    this.getFormatFromDefaultValue = getFormatFromDefaultValue;
  }
  
  public ResourceBundle getBundle()
  {
    return bundle;
  }
  
  public void setBundle(ResourceBundle bundle)
  {
    this.bundle = bundle;
  }
  
  public boolean isAllFields()
  {
    return allFields;
  }
  
  public void setAllFields(boolean allFields)
  {
    this.allFields = allFields;
  }
}
