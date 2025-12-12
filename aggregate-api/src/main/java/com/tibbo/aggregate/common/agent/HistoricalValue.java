package com.tibbo.aggregate.common.agent;

import java.util.*;

import com.tibbo.aggregate.common.datatable.*;

public class HistoricalValue
{
  private String variable;
  private Date timestamp;
  private DataTable value;
  
  public HistoricalValue(String variable, Date timestamp, DataTable value)
  {
    this.variable = variable;
    this.timestamp = timestamp;
    this.value = value;
  }
  
  public String getVariable()
  {
    return variable;
  }
  
  public void setVariable(String variable)
  {
    this.variable = variable;
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
