package com.tibbo.aggregate.common.expression;

import java.util.*;

import com.tibbo.aggregate.common.*;
import com.tibbo.aggregate.common.context.*;
import com.tibbo.aggregate.common.util.*;

public class EnvironmentReferenceResolver extends AbstractReferenceResolver
{
  private final Map<String, Object> environment = new HashMap();
  
  public EnvironmentReferenceResolver()
  {
  }
  
  @Override
  public Object resolveReference(Reference ref, EvaluationEnvironment resolvingEnvironment) throws SyntaxErrorException, ContextException, EvaluationException
  {
    if (!Reference.SCHEMA_ENVIRONMENT.equals(ref.getSchema()) || ref.getServer() != null || ref.getContext() != null || ref.getEntity() != null || ref.getProperty() != null || ref.getRow() != null)
    {
      throw new EvaluationException("Unexpected reference syntax: " + ref);
    }
    
    final String variable = ref.getField();
    
    if (resolvingEnvironment != null && resolvingEnvironment.getEnvironment() != null && resolvingEnvironment.getEnvironment().containsKey(variable))
    {
      return resolvingEnvironment.getEnvironment().get(variable);
    }
    
    if (environment == null)
    {
      throw new ContextException(Cres.get().getString("exprEnvNotDefined"));
    }
    
    if (!environment.containsKey(variable))
    {
      throw new ContextException(Cres.get().getString("exprEnvVarNotFound") + variable);
    }
    
    return environment.get(variable);
  }
  
  public void set(String variable, Object value)
  {
    environment.put(variable, value);
  }
  
  public Object get(String variable)
  {
    return environment.get(variable);
  }
  
  public void setEnvironment(Map<String, Object> environment)
  {
    this.environment.putAll(environment);
  }
  
  public Map<String, Object> getEnvironment()
  {
    return environment;
  }
}
