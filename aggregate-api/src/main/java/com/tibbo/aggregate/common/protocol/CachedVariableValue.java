package com.tibbo.aggregate.common.protocol;

import java.util.*;

import com.tibbo.aggregate.common.datatable.*;

public class CachedVariableValue
{
  private Date timestamp;
  private DataTable value;
  
  public CachedVariableValue(Date timestamp, DataTable value)
  {
    this.timestamp = timestamp;
    this.value = value;
  }
  
  public Date getTimestamp()
  {
    return timestamp;
  }
  
  public void setTimestamp(Date timestamp)
  {
    this.timestamp = timestamp;
  }
  
  public DataTable getValue()
  {
    return value;
  }
  
  public void setValue(DataTable value)
  {
    this.value = value;
  }
}
