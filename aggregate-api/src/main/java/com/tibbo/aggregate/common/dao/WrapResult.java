package com.tibbo.aggregate.common.dao;

import com.tibbo.aggregate.common.datatable.*;

public class WrapResult
{
  private Integer fullSize;
  private DataTable data;
  
  public WrapResult(Integer fullSize, DataTable data)
  {
    super();
    this.fullSize = fullSize;
    this.data = data;
  }
  
  public Integer getFullSize()
  {
    return fullSize;
  }
  
  public void setFullSize(Integer fullSize)
  {
    this.fullSize = fullSize;
  }
  
  public DataTable getData()
  {
    return data;
  }
  
  public void setData(DataTable data)
  {
    this.data = data;
  }
  
}
