package com.tibbo.aggregate.common.expression.util;

import com.tibbo.aggregate.common.*;
import com.tibbo.aggregate.common.context.*;
import com.tibbo.aggregate.common.datatable.*;

public class ContextExpressionTracer implements Tracer
{
  public static final String E_TRACE = "trace";
  
  public static final String EF_TRACE_VALUE = "value";
  public static final String EF_TRACE_MESSAGE = "message";
  
  private static final TableFormat EFT_TRACE = new TableFormat(1, 1);
  static
  {
    EFT_TRACE.addField("<" + EF_TRACE_VALUE + "><S><F=N><D=" + Cres.get().getString("value") + ">");
    EFT_TRACE.addField("<" + EF_TRACE_MESSAGE + "><S><F=N><D=" + Cres.get().getString("message") + ">");
  }
  
  private final Context context;
  
  private final String traceEventGroup;
  
  public ContextExpressionTracer(Context context, String traceEventGroup)
  {
    super();
    this.context = context;
    this.traceEventGroup = traceEventGroup;
    install();
  }
  
  public void install()
  {
    Context target = getContext();
    
    if (target == null)
    {
      return;
    }
    
    if (target.getEventDefinition(E_TRACE) != null)
    {
      return;
    }
    
    EventDefinition ed = new EventDefinition(E_TRACE, EFT_TRACE, Cres.get().getString("trace"));
    ed.setGroup(traceEventGroup);
    
    target.addEventDefinition(ed);
  }
  
  @Override
  public void trace(Object value, String message)
  {
    install();
    
    Context target = getContext();
    
    target.fireEvent(E_TRACE, value != null ? value.toString() : null, message);
  }
  
  protected Context getContext()
  {
    return context;
  }
}
