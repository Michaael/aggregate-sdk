package com.tibbo.aggregate.common.datatable.encoding;

import com.tibbo.aggregate.common.datatable.*;

public class EncodingSettings
{
  private boolean encodeFormat = true;
  
  private TableFormat format;
  
  public EncodingSettings(boolean encodeFormat, TableFormat format)
  {
    this.encodeFormat = encodeFormat;
    this.format = format;
  }
  
  public TableFormat getFormat()
  {
    return format;
  }
  
  public void setFormat(TableFormat format)
  {
    this.format = format;
  }
  
  public boolean isEncodeFormat()
  {
    return encodeFormat;
  }
  
  public void setEncodeFormat(boolean encodeFormat)
  {
    this.encodeFormat = encodeFormat;
  }
}
