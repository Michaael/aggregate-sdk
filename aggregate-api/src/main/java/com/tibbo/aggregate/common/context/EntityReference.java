package com.tibbo.aggregate.common.context;

import java.io.*;

public class EntityReference implements Serializable, Cloneable, Comparable<EntityReference>
{
  private String context;
  private String entity;
  private String serverID;
  
  public EntityReference()
  {
  }
  
  public EntityReference(String context, String entity)
  {
    this.context = context;
    this.entity = entity;
  }
  
  public EntityReference(String context, String entity, String serverID)
  {
    this(context, entity);
    this.serverID = serverID;
  }
  
  public String getContext()
  {
    return context;
  }
  
  public String getEntity()
  {
    return entity;
  }
  
  // For compatibility with LS properties table mapping
  public String getProperty()
  {
    return entity;
  }
  
  public void setContext(String context)
  {
    this.context = context;
  }
  
  public void setEntity(String entity)
  {
    this.entity = entity;
  }
  
  // For compatibility with LS properties table mapping
  public void setProperty(String property)
  {
    this.entity = property;
  }
  
  public String getServerID()
  {
    return serverID;
  }
  
  public void setServerID(String serverID)
  {
    this.serverID = serverID;
  }
  
  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((context == null) ? 0 : context.hashCode());
    result = prime * result + ((entity == null) ? 0 : entity.hashCode());
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
    EntityReference other = (EntityReference) obj;
    if (context == null)
    {
      if (other.context != null)
      return false;
    }
    else if (!context.equals(other.context))
      return false;
    if (entity == null)
    {
      if (other.entity != null)
      return false;
    }
    else if (!entity.equals(other.entity))
      return false;
    return true;
  }
  
  public int compareTo(EntityReference ref)
  {
    return toString().compareTo(ref.toString());
  }
  
  @Override
  public String toString()
  {
    return context + ":" + entity;
  }
  
  @Override
  public EntityReference clone()
  {
    try
    {
      return (EntityReference) super.clone();
    }
    catch (CloneNotSupportedException ex)
    {
      throw new IllegalStateException(ex.getMessage(), ex);
    }
  }
}
