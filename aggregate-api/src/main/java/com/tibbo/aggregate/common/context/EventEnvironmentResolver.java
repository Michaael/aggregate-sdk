package com.tibbo.aggregate.common.context;

import java.util.*;

import com.tibbo.aggregate.common.data.*;
import com.tibbo.aggregate.common.datatable.*;
import com.tibbo.aggregate.common.event.*;
import com.tibbo.aggregate.common.expression.*;
import com.tibbo.aggregate.common.util.*;

public class EventEnvironmentResolver extends AbstractReferenceResolver
{
  private EnvironmentReferenceResolver resolver;
  
  private Event ev;
  
  public EventEnvironmentResolver(EnvironmentReferenceResolver environmentResolver, Event ev)
  {
    this.resolver = environmentResolver;
    this.ev = ev;
  }
  
  @Override
  public Object resolveReference(Reference ref, EvaluationEnvironment environment) throws SyntaxErrorException, EvaluationException, ContextException
  {
    if (ref.getField() != null)
    {
      switch (ref.getField())
      {
        case EventUtils.ENVIRONMENT_ID:
          return ev.getId();
          
        case EventUtils.ENVIRONMENT_CONTEXT:
          return ev.getContext();
          
        case EventUtils.ENVIRONMENT_EVENT:
          return ev.getName();
          
        case EventUtils.ENVIRONMENT_LEVEL:
          return ev.getLevel();
          
        case EventUtils.ENVIRONMENT_TIME:
          return ev.getCreationtime();
          
        case EventUtils.ENVIRONMENT_ACKNOWLEDGEMENTS:
          return ev.getAcknowledgementsTable();
          
        case EventUtils.ENVIRONMENT_ENRICHMENTS:
          return ev.getEnrichmentsTable();
          
        case EventUtils.ENVIRONMENT_VALUE:
          return ev.getData();
      }
    }
    
    return resolver.resolveReference(ref, environment);
  }
  
  @Override
  public CallerController getCallerController()
  {
    return resolver.getCallerController();
  }
  
  @Override
  public Context getDefaultContext()
  {
    return resolver.getDefaultContext();
  }
  
  @Override
  public ContextManager getContextManager()
  {
    return resolver.getContextManager();
  }
  
  @Override
  public Integer getDefaultRow()
  {
    return resolver.getDefaultRow();
  }
  
  @Override
  public DataTable getDefaultTable()
  {
    return resolver.getDefaultTable();
  }
  
  @Override
  public Evaluator getEvaluator()
  {
    return resolver.getEvaluator();
  }
  
  @Override
  public void addContextManager(String schema, ContextManager cm)
  {
    resolver.addContextManager(schema, cm);
  }
  
  @Override
  public void setCallerController(CallerController callerController)
  {
    resolver.setCallerController(callerController);
  }
  
  @Override
  public void setDefaultContext(Context defaultContext)
  {
    resolver.setDefaultContext(defaultContext);
  }
  
  @Override
  public void setContextManager(ContextManager contextManager)
  {
    resolver.setContextManager(contextManager);
  }
  
  @Override
  public void setDefaultRow(Integer defaultRow)
  {
    resolver.setDefaultRow(defaultRow);
  }
  
  @Override
  public void setDefaultTable(DataTable defaultTable)
  {
    resolver.setDefaultTable(defaultTable);
  }
  
  public void set(String variable, Object value)
  {
    resolver.set(variable, value);
  }
  
  @Override
  public void setEvaluator(Evaluator evaluator)
  {
    resolver.setEvaluator(evaluator);
  }
  
  public Object get(String variable)
  {
    return resolver.get(variable);
  }
  
  public void setEnvironment(Map<String, Object> environment)
  {
    resolver.setEnvironment(environment);
  }
  
  public Map<String, Object> getEnvironment()
  {
    return resolver.getEnvironment();
  }
  
}
