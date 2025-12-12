package com.tibbo.aggregate.common.util;

import com.tibbo.aggregate.common.*;
import com.tibbo.aggregate.common.datatable.*;

public class ThreadUtils
{
  public static final TableFormat FORMAT_STACK = new TableFormat();
  static
  {
    FORMAT_STACK.addField("<class><S><D=" + Cres.get().getString("class") + ">");
    FORMAT_STACK.addField("<method><S><D=" + Cres.get().getString("method") + ">");
    FORMAT_STACK.addField("<file><S><F=N><D=" + Cres.get().getString("file") + ">");
    FORMAT_STACK.addField("<line><I><F=N><D=" + Cres.get().getString("line") + ">");
  }
  
  public static DataTable createStackTraceTable(StackTraceElement[] elements)
  {
    DataTable stack = new SimpleDataTable(FORMAT_STACK);
    for (int j = 0; j < elements.length; j++)
    {
      StackTraceElement el = elements[j];
      stack.addRecord().addString(el.getClassName()).addString(el.getMethodName()).addString(el.getFileName()).addInt(el.getLineNumber());
    }
    return stack;
  }
}
