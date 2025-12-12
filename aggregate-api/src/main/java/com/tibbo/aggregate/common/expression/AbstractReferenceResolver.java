package com.tibbo.aggregate.common.expression;

import com.tibbo.aggregate.common.context.*;
import com.tibbo.aggregate.common.datatable.*;

public abstract class AbstractReferenceResolver implements ReferenceResolver
{
  private Evaluator evaluator;
  private ContextManager contextManager;
  private Context defaultContext;
  private DataTable defaultTable;
  private Integer defaultRow;
  private CallerController callerController;
  
  public CallerController getCallerController()
  {
    return callerController;
  }
  
  public Context getDefaultContext()
  {
    return defaultContext;
  }
  
  public ContextManager getContextManager()
  {
    return contextManager;
  }
  
  public Integer getDefaultRow()
  {
    return defaultRow;
  }
  
  public DataTable getDefaultTable()
  {
    return defaultTable;
  }
  
  public Evaluator getEvaluator()
  {
    return evaluator;
  }
  
  public void addContextManager(String schema, ContextManager cm)
  {
  }
  
  public void setCallerController(CallerController callerController)
  {
    this.callerController = callerController;
  }
  
  public void setDefaultContext(Context defaultContext)
  {
    this.defaultContext = defaultContext;
  }
  
  public void setContextManager(ContextManager contextManager)
  {
    this.contextManager = contextManager;
  }
  
  public void setDefaultRow(Integer defaultRow)
  {
    this.defaultRow = defaultRow;
  }
  
  public void setDefaultTable(DataTable defaultTable)
  {
    this.defaultTable = defaultTable;
  }
  
  public void setEvaluator(Evaluator evaluator)
  {
    this.evaluator = evaluator;
  }
}
