package com.tibbo.aggregate.common.security;

import java.util.*;

import com.tibbo.aggregate.common.context.*;

public class RoleRule implements Cloneable
{
  private String contextType;
  private int entityType = ContextUtils.ENTITY_ANY_TYPE;
  private String entity = ContextUtils.ENTITY_ANY;
  private HashSet<RoleEntity> ruleExceptions = new LinkedHashSet<>();
  private int permission;
  
  public String getContextType()
  {
    return contextType;
  }
  
  public void setContextType(String contextType)
  {
    this.contextType = contextType;
  }
  
  public int getEntityType()
  {
    return entityType;
  }
  
  public void setEntityType(int entityType)
  {
    this.entityType = entityType;
  }
  
  public String getEntity()
  {
    return entity;
  }
  
  public void setEntity(String entity)
  {
    this.entity = entity;
  }
  
  public int getPermission()
  {
    return permission;
  }
  
  public void setPermission(int permission)
  {
    this.permission = permission;
  }
  
  public HashSet<RoleEntity> getRuleExceptions()
  {
    return ruleExceptions;
  }
  
  public void setRuleExceptions(HashSet<RoleEntity> ruleExceptions)
  {
    this.ruleExceptions = ruleExceptions;
  }
  
  public void addRuleException(int entityType, String entity)
  {
    ruleExceptions.add(new RoleEntity(entityType, entity));
  }
  
  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((contextType == null) ? 0 : contextType.hashCode());
    result = prime * result + ((entity == null) ? 0 : entity.hashCode());
    result = prime * result + entityType;
    result = prime * result + permission;
    result = prime * result + ((ruleExceptions == null) ? 0 : ruleExceptions.hashCode());
    return result;
  }
  
  @Override
  public boolean equals(Object obj)
  {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    RoleRule other = (RoleRule) obj;
    if (contextType == null)
    {
      if (other.contextType != null)
        return false;
    }
    else if (!contextType.equals(other.contextType))
      return false;
    if (entity == null)
    {
      if (other.entity != null)
        return false;
    }
    else if (!entity.equals(other.entity))
      return false;
    if (entityType != other.entityType)
      return false;
    if (permission != other.permission)
      return false;
    if (ruleExceptions == null)
    {
      if (other.ruleExceptions != null)
        return false;
    }
    else if (!ruleExceptions.equals(other.ruleExceptions))
      return false;
    return true;
  }
  
  @Override
  public String toString()
  {
    return contextType + " '" + entityType + "'" + " '" + entity + "' " + permission;
  }
}
