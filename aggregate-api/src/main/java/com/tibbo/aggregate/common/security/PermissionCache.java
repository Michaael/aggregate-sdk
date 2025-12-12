package com.tibbo.aggregate.common.security;

import java.util.*;
import java.util.concurrent.*;

import com.tibbo.aggregate.common.context.*;

public class PermissionCache
{
  private final Map<String, String> levelByContext = new ConcurrentHashMap(16, 0.75f, 16); // Levels for contexts without permissions for entities
  private final Map<String, String> levelByEntity = new ConcurrentHashMap(16, 0.75f, 16); // Levels for contexts with permissions for entities
  
  private final Map<String, Integer> roleBasedLevelByContext = new ConcurrentHashMap(16, 0.75f, 16); // Levels for contexts without permissions for entities
  private final Map<String, Integer> roleBasedLevelByEntity = new ConcurrentHashMap(16, 0.75f, 16); // Levels for contexts with permissions for entities
  
  public String getLevel(String accessedPath, Integer accessedEntityType, String accessedEntity)
  {
    String level = levelByContext.get(accessedPath);
    
    if (level != null)
    {
      return level;
    }
    
    return levelByEntity.get(createCacheId(accessedPath, accessedEntityType, accessedEntity));
  }
  
  public void cacheLevel(String accessedPath, Integer accessedEntityType, String accessedEntity, String level, boolean hasPermissionsForEntities)
  {
    if (hasPermissionsForEntities)
    {
      levelByEntity.put(createCacheId(accessedPath, accessedEntityType, accessedEntity), level);
    }
    else
    {
      levelByContext.put(accessedPath, level);
    }
  }
  
  public Integer getRoleBasedLevel(String accessedPath, EntityDefinition accessedEntityDefinition)
  {
    Integer accessedEntityType = null;
    String accessedEntity = null;
    
    if (accessedEntityDefinition != null)
    {
      accessedEntityType = accessedEntityDefinition.getEntityType();
      accessedEntity = accessedEntityDefinition.getName();
    }
    
    Integer level = roleBasedLevelByContext.get(accessedPath);
    
    if (level != null)
    {
      return level;
    }
    
    return roleBasedLevelByEntity.get(createCacheId(accessedPath, accessedEntityType, accessedEntity));
  }
  
  public void cacheRoleBasedLevel(String accessedPath, Integer accessedEntityType, String accessedEntity, Integer level, boolean hasPermissionsForEntities)
  {
    if (hasPermissionsForEntities)
    {
      roleBasedLevelByEntity.put(createCacheId(accessedPath, accessedEntityType, accessedEntity), level);
    }
    else
    {
      roleBasedLevelByContext.put(accessedPath, level);
    }
  }
  
  private String createCacheId(String accessedPath, Integer accessedEntityType, String accessedEntity)
  {
    String cacheId = accessedPath;
    
    if (accessedEntityType != null && !accessedEntityType.equals(ContextUtils.ENTITY_ANY_TYPE))
    {
      cacheId = cacheId + Permission.PERMISSION_FIELDS_SEPARATOR + accessedEntityType;
      
      if (accessedEntity != null && !accessedEntity.equals(ContextUtils.ENTITY_ANY))
      {
        cacheId = cacheId + Permission.PERMISSION_FIELDS_SEPARATOR + accessedEntity;
      }
    }
    
    return cacheId;
  }

  public void removeContext(String path)
  {
    levelByContext.remove(path);

    for(Iterator<String> iter = levelByEntity.keySet().iterator(); iter.hasNext();)
    {
      String cacheId = iter.next();
      if (cacheId.startsWith(path + Permission.PERMISSION_FIELDS_SEPARATOR))
      {
        iter.remove();
      }
    }

    roleBasedLevelByContext.remove(path);

    for(Iterator<String> iter = roleBasedLevelByEntity.keySet().iterator(); iter.hasNext();)
    {
      String cacheId = iter.next();
      if (cacheId.startsWith(path + Permission.PERMISSION_FIELDS_SEPARATOR))
      {
        iter.remove();
      }
    }
  }

  public Map<String, String> getLevelByContext()
  {
    return levelByContext;
  }
  
  public Map<String, String> getLevelByEntity()
  {
    return levelByEntity;
  }
  
  public Map<String, Integer> getRoleBasedlevelByContext()
  {
    return roleBasedLevelByContext;
  }
  
  public Map<String, Integer> getRoleBasedlevelByEntity()
  {
    return roleBasedLevelByEntity;
  }
  
}
