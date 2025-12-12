package com.tibbo.aggregate.common.view;

import com.tibbo.aggregate.common.datatable.*;

public abstract class StorageSession
{
  private String table;
  
  private String view;
  private DataTable viewColumns;
  
  abstract public void close();
  
  public String getTable()
  {
    return table;
  }
  
  public void setTable(String table)
  {
    this.table = table;
  }
  
  public String getView()
  {
    return view;
  }
  
  public void setView(String view)
  {
    this.view = view;
  }
  
  public DataTable getViewColumns()
  {
    return viewColumns;
  }
  
  public void setViewColumns(DataTable viewColumns)
  {
    this.viewColumns = viewColumns;
  }
}
