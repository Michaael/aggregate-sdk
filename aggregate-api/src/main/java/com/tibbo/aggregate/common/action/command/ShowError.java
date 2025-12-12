package com.tibbo.aggregate.common.action.command;

import java.io.*;

import com.tibbo.aggregate.common.*;
import com.tibbo.aggregate.common.action.*;
import com.tibbo.aggregate.common.datatable.*;

public class ShowError extends GenericActionCommand
{
  public static final String CF_LEVEL = "level";
  public static final String CF_MESSAGE = "message";
  public static final String CF_EXCEPTION = "exception";
  
  public static final TableFormat CFT_SHOW_ERROR = new TableFormat(1, 1);
  static
  {
    CFT_SHOW_ERROR.addField("<" + CF_LEVEL + "><I><D="+ Cres.get().getString("level")+">");
    CFT_SHOW_ERROR.addField("<" + CF_MESSAGE + "><S><F=N><D="+ Cres.get().getString("message")+">");
    CFT_SHOW_ERROR.addField("<" + CF_EXCEPTION + "><S><F=N><D="+ Cres.get().getString("exception")+">");
  }
  
  private Throwable exception;
  private int level;
  private String message;
  
  public ShowError()
  {
    super(ActionUtils.CMD_SHOW_ERROR, CFT_SHOW_ERROR, null);
  }
  
  public ShowError(String title, String message, int level, Throwable exception)
  {
    super(ActionUtils.CMD_SHOW_ERROR, title);
    this.message = message;
    this.level = level;
    this.exception = exception;
  }

  public ShowError(String title, DataTable parameters)
  {
    super(ActionUtils.CMD_SHOW_ERROR, title, parameters);
  }
  
  @Override
  protected DataTable constructParameters()
  {
    DataTable t = new SimpleDataTable(CFT_SHOW_ERROR);
    
    StringWriter exTrace = new StringWriter();
    PrintWriter pw = new PrintWriter(exTrace);
    exception.printStackTrace(pw);
    
    String finalDetails = exTrace.toString();
    
    t.addRecord().addInt(level).addString(message).addString(finalDetails);
    
    return t;
  }
  
  public int getLevel()
  {
    return level;
  }
  
  public void setLevel(int level)
  {
    this.level = level;
  }
  
  public String getMessage()
  {
    return message;
  }
  
  public void setMessage(String message)
  {
    this.message = message;
  }
  
  public Throwable getException()
  {
    return exception;
  }
  
  public void setException(Throwable exception)
  {
    this.exception = exception;
  }
}
