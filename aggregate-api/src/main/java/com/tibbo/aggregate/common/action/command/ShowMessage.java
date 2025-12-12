package com.tibbo.aggregate.common.action.command;

import com.tibbo.aggregate.common.*;
import com.tibbo.aggregate.common.action.*;
import com.tibbo.aggregate.common.datatable.*;

public class ShowMessage extends GenericActionCommand
{
  public static final String CF_MESSAGE = "message";
  public static final String CF_LEVEL = "level";
  
  public static final TableFormat CFT_SHOW_MESSAGE = new TableFormat(1, 1);
  static
  {
    CFT_SHOW_MESSAGE.addField("<" + CF_MESSAGE + "><S><D="+ Cres.get().getString("message")+">");
    CFT_SHOW_MESSAGE.addField("<" + CF_LEVEL + "><I><D="+ Cres.get().getString("level")+">");
  }
  
  private String message;
  private int level;
  
  public ShowMessage()
  {
    super(ActionUtils.CMD_SHOW_MESSAGE, CFT_SHOW_MESSAGE, null);
  }
  
  public ShowMessage(String title, String message, int level)
  {
    super(ActionUtils.CMD_SHOW_MESSAGE, title);
    this.message = message;
    this.level = level;
  }
  
  public ShowMessage(String title, DataTable parameters)
  {
    super(ActionUtils.CMD_SHOW_MESSAGE, title, parameters, CFT_SHOW_MESSAGE);
  }
  
  @Override
  protected DataTable constructParameters()
  {
    return new SimpleDataTable(CFT_SHOW_MESSAGE, message, level);
  }
  
  public String getMessage()
  {
    return message;
  }
  
  public int getLevel()
  {
    return level;
  }
  
  public void setMessage(String message)
  {
    this.message = message;
  }
  
  public void setLevel(int level)
  {
    this.level = level;
  }
  
}
