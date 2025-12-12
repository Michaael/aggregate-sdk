package com.tibbo.aggregate.common.action;

import java.util.*;

import com.tibbo.aggregate.common.*;
import com.tibbo.aggregate.common.datatable.*;

public class ActionHistoryItem extends AggreGateBean
{
  public static final TableFormat FORMAT = new TableFormat();
  static
  {
    FORMAT.addField("<time><D><D=" + Cres.get().getString("timestamp") + ">");
    FORMAT.addField("<mask><S><D=" + Cres.get().getString("conContextMask") + ">");
    FORMAT.addField("<action><S><D=" + Cres.get().getString("action") + ">");
    FORMAT.addField("<input><T><F=N><D=" + Cres.get().getString("parameters") + ">");
  }
  
  private Date time;
  private String mask;
  private String action;
  private DataTable input;
  
  public ActionHistoryItem()
  {
    super(FORMAT);
  }
  
  public ActionHistoryItem(DataRecord data)
  {
    super(FORMAT, data);
  }
  
  public ActionHistoryItem(Date time, String mask, String action, DataTable input)
  {
    this();
    this.time = time;
    this.mask = mask;
    this.action = action;
    this.input = input;
  }
  
  public Date getTime()
  {
    return time;
  }
  
  public void setTime(Date time)
  {
    this.time = time;
  }
  
  public String getMask()
  {
    return mask;
  }
  
  public void setMask(String mask)
  {
    this.mask = mask;
  }
  
  public String getAction()
  {
    return action;
  }
  
  public void setAction(String action)
  {
    this.action = action;
  }
  
  public DataTable getInput()
  {
    return input;
  }
  
  public void setInput(DataTable input)
  {
    this.input = input;
  }
}
