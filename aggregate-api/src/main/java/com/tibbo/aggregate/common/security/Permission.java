package com.tibbo.aggregate.common.security;

import java.util.*;
import java.util.concurrent.locks.*;

import com.tibbo.aggregate.common.*;
import com.tibbo.aggregate.common.context.*;
import com.tibbo.aggregate.common.util.*;

public class Permission implements Cloneable
{
  public final static char PERMISSION_FIELDS_SEPARATOR = ':';
  
  public static Map<Integer, String> getUserEntitiesSelectionValues()
  {
    Map map = new TreeMap<Integer, String>();
    
    map.put(ContextUtils.ENTITY_ANY_TYPE, Cres.get().getString("all"));
    map.put(ContextUtils.ENTITY_VARIABLE, Cres.get().getString("variable"));
    map.put(ContextUtils.ENTITY_FUNCTION, Cres.get().getString("function"));
    map.put(ContextUtils.ENTITY_ACTION, Cres.get().getString("action"));
    map.put(ContextUtils.ENTITY_EVENT, Cres.get().getString("event"));
    map.put(ContextUtils.ENTITY_VARIABLE_GROUP, Cres.get().getString("variableGroup"));
    map.put(ContextUtils.ENTITY_FUNCTION_GROUP, Cres.get().getString("functionGroup"));
    map.put(ContextUtils.ENTITY_ACTION_GROUP, Cres.get().getString("actionGroup"));
    map.put(ContextUtils.ENTITY_EVENT_GROUP, Cres.get().getString("eventGroup"));
    
    return map;
  }
  
  private String context;
  private String level;
  
  private int entityType = ContextUtils.ENTITY_ANY_TYPE;
  private String entity = ContextUtils.ENTITY_ANY;
  
  private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
  
  public Permission(String data, PermissionChecker checker)
  {
    super();
    
    List<String> spd = StringUtils.split(data, Permission.PERMISSION_FIELDS_SEPARATOR);
    
    switch (spd.size())
    {
      case 1:
        setLevel(spd.get(0));
        break;
      
      case 2:
        setContext(spd.get(0));
        setLevel(spd.get(1));
        break;
      
      case 3:
        setContext(spd.get(0));
        setEntityType(Integer.parseInt(spd.get(1)));
        setLevel(spd.get(2));
        break;
      
      case 4:
        setContext(spd.get(0));
        setEntityType(Integer.parseInt(spd.get(1)));
        setEntity(spd.get(2));
        setLevel(spd.get(3));
        break;
      
      default:
        throw new IllegalArgumentException("Invalid permission: '" + data + "'");
    }
    
    if (checker != null)
    {
      if (!checker.isValid(getLevel()))
      {
        throw new IllegalArgumentException("Invalid permission type: '" + level + "'");
      }
    }
  }
  
  public Permission(String context, String level)
  {
    setContext(context);
    setLevel(level);
  }
  
  public Permission(String context, int entityType, String entity, String level)
  {
    setContext(context);
    setEntityType(entityType);
    setEntity(entity);
    setLevel(level);
  }
  
  public String encode()
  {
    String result = "";
    
    lock.readLock().lock();
    try
    {
      result = level;
      
      if (entityType > ContextUtils.ENTITY_ANY_TYPE)
      {
        if (entity != null && !entity.equals(ContextUtils.ENTITY_ANY))
        {
          result = entity + Permission.PERMISSION_FIELDS_SEPARATOR + result;
        }
        result = String.valueOf(entityType) + Permission.PERMISSION_FIELDS_SEPARATOR + result;
      }
      
      if (context != null)
      {
        result = context + Permission.PERMISSION_FIELDS_SEPARATOR + result;
      }
    }
    finally
    {
      lock.readLock().unlock();
    }
    
    return result;
  }
  
  @Override
  public Permission clone()
  {
    try
    {
      return (Permission) super.clone();
    }
    catch (CloneNotSupportedException ex)
    {
      throw new IllegalStateException(ex.getMessage(), ex);
    }
  }
  
  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((context == null) ? 0 : context.hashCode());
    result = prime * result + ((entity == null) ? 0 : entity.hashCode());
    result = prime * result + entityType;
    result = prime * result + ((level == null) ? 0 : level.hashCode());
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
    Permission other = (Permission) obj;
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
    if (entityType != other.entityType)
      return false;
    if (level == null)
    {
      if (other.level != null)
        return false;
    }
    else if (!level.equals(other.level))
      return false;
    return true;
  }
  
  @Override
  public String toString()
  {
    return encode();
  }
  
  public String getContext()
  {
    return context;
  }
  
  public String getLevel()
  {
    return level;
  }
  
  public void setContext(String entity)
  {
    lock.writeLock().lock();
    try
    {
      this.context = entity;
    }
    finally
    {
      lock.writeLock().unlock();
    }
  }
  
  public void setLevel(String level)
  {
    lock.writeLock().lock();
    try
    {
      this.level = level;
    }
    finally
    {
      lock.writeLock().unlock();
    }
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
}
