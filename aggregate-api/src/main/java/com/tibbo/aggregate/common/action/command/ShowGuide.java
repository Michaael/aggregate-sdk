package com.tibbo.aggregate.common.action.command;

import com.tibbo.aggregate.common.action.*;
import com.tibbo.aggregate.common.datatable.*;

public class ShowGuide extends GenericActionCommand
{
  public static final String CF_INVOKER_CONTEXT = "invokerContext";
  public static final String CF_MACRO_NAME = "macroName";
  
  public static final TableFormat CFT_SHOW_GUIDE = new TableFormat(1, 1);
  static
  {
    CFT_SHOW_GUIDE.addField("<" + CF_INVOKER_CONTEXT + "><S><F=N>");
    CFT_SHOW_GUIDE.addField("<" + CF_MACRO_NAME + "><S><F=N>");
  }
  
  private String invokerContext;
  private String macroName;
  
  public ShowGuide()
  {
    super(ActionUtils.CMD_SHOW_GUIDE, CFT_SHOW_GUIDE, null);
  }
  
  public ShowGuide(String title, String invokerContext, String macroName)
  {
    super(ActionUtils.CMD_SHOW_GUIDE, title);
    this.invokerContext = invokerContext;
    this.macroName = macroName;
  }
  
  public ShowGuide(String title, DataTable parameters)
  {
    super(ActionUtils.CMD_SHOW_GUIDE, title, parameters, CFT_SHOW_GUIDE);
  }
  
  @Override
  protected DataTable constructParameters()
  {
    return new SimpleDataTable(CFT_SHOW_GUIDE, invokerContext, macroName);
  }
  
  public String getInvokerContext()
  {
    return invokerContext;
  }
  
  public void setInvokerContext(String invokerContext)
  {
    this.invokerContext = invokerContext;
  }
  
  public String getMacroName()
  {
    return macroName;
  }
  
  public void setMacroName(String macroName)
  {
    this.macroName = macroName;
  }
  
}
