package com.tibbo.aggregate.common.security;

import com.tibbo.aggregate.common.context.*;

public class RoleEntity
{
  private int entityType = ContextUtils.ENTITY_ANY_TYPE;
  private String entity = ContextUtils.ENTITY_ANY;
  
  public RoleEntity(int entityType, String entity)
  {
    this.entityType = entityType;
    this.entity = entity;
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
  
  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((entity == null) ? 0 : entity.hashCode());
    result = prime * result + entityType;
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
    RoleEntity other = (RoleEntity) obj;
    if (entity == null)
    {
      if (other.entity != null)
        return false;
    }
    else if (!entity.equals(other.entity))
      return false;
    if (entityType != other.entityType)
      return false;
    return true;
  }
  
}
