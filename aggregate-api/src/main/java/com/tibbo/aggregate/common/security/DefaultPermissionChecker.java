package com.tibbo.aggregate.common.security;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.log4j.Logger;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.Log;
import com.tibbo.aggregate.common.context.CallerController;
import com.tibbo.aggregate.common.context.Context;
import com.tibbo.aggregate.common.context.ContextManager;
import com.tibbo.aggregate.common.context.ContextUtils;
import com.tibbo.aggregate.common.context.EntityDefinition;
import com.tibbo.aggregate.common.context.UncheckedCallerController;
import com.tibbo.aggregate.common.util.StringUtils;
import com.tibbo.aggregate.common.util.Util;

public abstract class DefaultPermissionChecker implements PermissionChecker
{
  public static final String NULL_PERMISSIONS = "";
  
  private PermissionType[] permissionTypes;
  private PermissionType[] rolePermissionTypes;
  
  private final Logger logger = Log.SECURITY;
  
  private final CallerController unchecked = new UncheckedCallerController();
  
  public DefaultPermissionChecker()
  {
    PermissionType nullType = new PermissionType(Integer.parseInt("00000000", 2), NULL_PERMISSIONS, Cres.get().getString("secNoPerms"));
    permissionTypes = new PermissionType[] { nullType };
    rolePermissionTypes = new PermissionType[] { nullType };
  }
  
  protected void setPermissionTypes(PermissionType[] perms)
  {
    this.permissionTypes = perms;
  }
  
  protected void setRolePermissionTypes(PermissionType[] perms)
  {
    this.rolePermissionTypes = perms;
  }
  
  public static Permissions getNullPermissions()
  {
    return new Permissions();
  }
  
  @Override
  public boolean has(CallerController caller, Permissions requiredPermissions, Context accessedContext, EntityDefinition accessedEntityDefinition)
  {
    try
    {
      if (caller == null)
      {
        return requiredPermissions.size() == 0;
      }
      
      if (!caller.isPermissionCheckingEnabled())
      {
        return true;
      }
      
      Permissions existingPermissions = caller.getPermissions();
      
      if (existingPermissions == null)
      {
        if (logger.isDebugEnabled())
        {
          logger.debug("Permission level of '" + caller + "' is 'null' and allow nothing, need " + requiredPermissions);
        }
        return false;
      }
      
      if (existingPermissions.isUseRoleBasedPermissions())
      {
        return hasWithRoles(requiredPermissions, accessedContext, accessedEntityDefinition, caller);
      }
      
      if (requiredPermissions == null || requiredPermissions.size() == 0)
      {
        return true;
      }
      
      for (Permission required : requiredPermissions)
      {
        String accessedPath = getAccessedPath(accessedContext, required);
        Integer accessedEntityType = accessedEntityDefinition != null ? accessedEntityDefinition.getEntityType() : null;
        String accessedEntityGroup = accessedEntityDefinition != null ? ContextUtils.getBaseGroup(accessedEntityDefinition.getGroup()) : null;
        String accessedEntity = accessedEntityDefinition != null ? accessedEntityDefinition.getName() : null;
        
        if (accessedPath != null)
        {
          PermissionCache cache = caller.getPermissionCache();
          String cachedLevel = cache != null ? cache.getLevel(accessedPath, accessedEntityType, accessedEntity) : null;
          String effectiveLevel;
          
          if (cachedLevel != null)
          {
            effectiveLevel = cachedLevel;
          }
          else
          {
            ContextManager cm = accessedContext != null ? accessedContext.getContextManager() : null;
            
            LevelInfo levelInfo = getLevelInfo(existingPermissions, accessedPath, accessedEntityType, accessedEntity, accessedEntityGroup, cm);
            
            Boolean hasPermissionsForEntities = levelInfo.hasPermissionsForEntities();
            effectiveLevel = levelInfo.getLevel();
            
            if (cache != null && accessedContext != null && equalsPaths(accessedPath, accessedContext)) // Caching effective permissions for real contexts only
            {
              cache.cacheLevel(accessedPath, accessedEntityType, accessedEntity, effectiveLevel, hasPermissionsForEntities);
            }
          }
          
          if (!hasNecessaryLevel(effectiveLevel, required.getLevel()))
          {
            if (logger.isDebugEnabled())
            {
              logger.debug("Permissions '" + existingPermissions + "' doesn't allow '" + requiredPermissions + "' (because effective level '" + effectiveLevel + "' in '" + accessedPath
                  + "' doesn't allow '" + required + "')");
            }
            return false;
          }
        }
        else
        {
          if (existingPermissions.size() != 1)
          {
            throw new IllegalStateException("Required permissions doesn't include context specification, so existing permissions should include exactly one element");
          }
          
          Permission existing = existingPermissions.iterator().next();
          
          if (!hasNecessaryLevel(existing.getLevel(), required.getLevel()))
          {
            if (logger.isDebugEnabled())
            {
              logger.debug("Permissions '" + existingPermissions + "' doesn't allow '" + requiredPermissions + "' (because '" + existing + "' doesn't allow '" + required + "')");
            }
            return false;
          }
        }
      }
      
      return true;
    }
    catch (Exception ex)
    {
      Log.SECURITY.error("Error checking permissions: ", ex);
      return false;
    }
  }
  
  private boolean hasWithRoles(Permissions requiredPermissions, Context accessedContext, EntityDefinition accessedEntityDefinition, CallerController caller)
  {
    PermissionCache cache = caller.getPermissionCache();
    Integer cachedLevel = cache != null ? cache.getRoleBasedLevel(accessedContext.getPath(), accessedEntityDefinition) : null;
    Integer effectiveLevel;
    
    if (cachedLevel != null)
    {
      effectiveLevel = cachedLevel;
    }
    else
    {
      effectiveLevel = findAndCacheRule(requiredPermissions, accessedContext, accessedEntityDefinition, caller).getPermission();
    }
    
    if (effectiveLevel == ServerPermissionChecker.RWX_LEVEL)
      return true;
    
    boolean isVariableWriteAttempt = requiredPermissions.isWritePermissions();
    
    if (effectiveLevel == ServerPermissionChecker.R_X_LEVEL && !isVariableWriteAttempt)
      return true;
    
    return false;
  }
  
  private RoleRule findAndCacheRule(Permissions requiredPermissions, Context accessedContext, EntityDefinition accessedEntityDefinition, CallerController caller)
  {
    Permissions existingPermissions = caller.getPermissions();
    PermissionCache cache = caller.getPermissionCache();
    
    Map<String, LinkedList<RoleRule>> roleBasedRXPermissions = existingPermissions.getRoleBasedRXPermissions();
    Map<String, LinkedList<RoleRule>> roleBasedRWXPermissions = existingPermissions.getRoleBasedRWXPermissions();
    Map<String, LinkedList<RoleRule>> roleBasedProhibitions = existingPermissions.getRoleBasedProhibitions();
    
    String accessedPath = accessedContext.getPath();
    
    Integer accessedEntityType = null;
    String accessedEntity = null;
    
    if (accessedEntityDefinition != null)
    {
      accessedEntityType = accessedEntityDefinition.getEntityType();
      accessedEntity = accessedEntityDefinition.getName();
    }
    
    RoleRuleInfo ruleForProhibitionMap = findRuleForAccessedObject(roleBasedProhibitions, accessedContext, accessedEntityDefinition);
    boolean hasRulesForEntities = ruleForProhibitionMap.hasRulesForEntities();
    
    if (ruleForProhibitionMap.ruleFound())
    {
      int level = ruleForProhibitionMap.getRoleRule().getPermission();
      cache.cacheRoleBasedLevel(accessedPath, accessedEntityType, accessedEntity, level, hasRulesForEntities);
      return ruleForProhibitionMap.getRoleRule();
    }
    
    RoleRuleInfo ruleForRWXPermissionMap = findRuleForAccessedObject(roleBasedRWXPermissions, accessedContext, accessedEntityDefinition);
    hasRulesForEntities |= ruleForRWXPermissionMap.hasRulesForEntities();
    
    if (ruleForRWXPermissionMap.ruleFound())
    {
      int level = ruleForRWXPermissionMap.getRoleRule().getPermission();
      cache.cacheRoleBasedLevel(accessedPath, accessedEntityType, accessedEntity, level, hasRulesForEntities);
      return ruleForRWXPermissionMap.getRoleRule();
    }
    
    RoleRuleInfo ruleForRXPermissionMap = findRuleForAccessedObject(roleBasedRXPermissions, accessedContext, accessedEntityDefinition);
    hasRulesForEntities |= ruleForRXPermissionMap.hasRulesForEntities();
    
    if (ruleForRXPermissionMap.ruleFound())
    {
      int level = ruleForRXPermissionMap.getRoleRule().getPermission();
      cache.cacheRoleBasedLevel(accessedPath, accessedEntityType, accessedEntity, level, hasRulesForEntities);
      return ruleForRXPermissionMap.getRoleRule();
    }
    
    return new RoleRule();
  }
  
  private RoleRuleInfo findRuleForAccessedObject(Map<String, LinkedList<RoleRule>> roleBasedMap, Context accessedContext, EntityDefinition accessedEntityDefinition)
  {
    String accessedPath = accessedContext.getPath();
    String accessedСontextType = accessedContext.getType();
    
    Integer accessedEntityType = null;
    String accessedEntityGroup = null;
    String accessedEntity = null;
    
    RoleRuleInfo roleRuleInfo = new RoleRuleInfo();
    
    if (accessedEntityDefinition != null)
    {
      accessedEntityType = accessedEntityDefinition.getEntityType();
      accessedEntityGroup = accessedEntityDefinition.getGroup();
      accessedEntity = accessedEntityDefinition.getName();
    }
    
    for (String mask : roleBasedMap.keySet())
    {
      for (String allowedPath : getAllowedPaths(mask, accessedContext.getContextManager()))
      {
        if (!ContextUtils.matchesToMask(allowedPath, accessedPath, false, false))
        {
          continue;
        }
        
        LinkedList<RoleRule> rules = roleBasedMap.get(mask);
        
        RoleRuleInfo foundRule = findRuleForAccessedObject(rules, accessedСontextType, accessedEntityType, accessedEntity, accessedEntityGroup);
        
        if (foundRule.hasRulesForEntities())
          roleRuleInfo.setHasRulesForEntities(true);
        
        if (foundRule.ruleFound())
        {
          roleRuleInfo.setRoleRule(foundRule.getRoleRule());
          roleRuleInfo.setCorrespondingMask(mask);
          return roleRuleInfo;
        }
      }
    }
    return roleRuleInfo;
  }
  
  private RoleRuleInfo findRuleForAccessedObject(LinkedList<RoleRule> rules, String accessedСontextType, Integer accessedEntityType, String accessedEntity, String accessedEntityGroup)
  {
    RoleRuleInfo foundRuleInfo = new RoleRuleInfo();
    
    for (RoleRule rule : rules)
    {
      if (!rule.getContextType().equals(accessedСontextType) && !ContextUtils.CONTEXT_TYPE_ANY.equals(accessedСontextType))
        continue;
      
      if (rule.getEntityType() != ContextUtils.ENTITY_ANY_TYPE)
        foundRuleInfo.setHasRulesForEntities(true);
      
      if (!checkEntity(rule.getEntityType(), rule.getEntity(), accessedEntityType, accessedEntity, accessedEntityGroup))
        continue;
      
      if (checkExceptions(rule.getRuleExceptions(), accessedEntityType, accessedEntity, accessedEntityGroup))
      {
        foundRuleInfo.setRoleRule(rule);
        return foundRuleInfo;
      }
    }
    
    return foundRuleInfo;
    
  }
  
  private boolean checkExceptions(HashSet<RoleEntity> exceptions, Integer accessedEntityType, String accessedEntity, String accessedEntityGroup)
  {
    for (RoleEntity exception : exceptions)
    {
      if (checkEntity(exception.getEntityType(), exception.getEntity(), accessedEntityType, accessedEntity, accessedEntityGroup))
        return false;
    }
    
    return true;
  }
  
  private boolean checkLevel(int level, boolean isVariableWriteAttempt)
  {
    if (isVariableWriteAttempt)
      return level == ServerPermissionChecker.RWX_LEVEL;
    
    return true;
  }
  
  private boolean checkEntity(int entityType, String entity, Integer accessedEntityType, String accessedEntity, String accessedEntityGroup)
  {
    if (entityType == ContextUtils.ENTITY_ANY_TYPE)
      return true;
    
    if (!Objects.equals(entityType, accessedEntityType))
    {
      boolean isContextAccess = accessedEntityType == null;
      
      if (isContextAccess)
      {
        // access to the context if there is access to all variables
        boolean hasVariableAccess = entityType == ContextUtils.ENTITY_VARIABLE || entityType == ContextUtils.ENTITY_VARIABLE_GROUP;
        boolean hasAccessToAllVariables = hasVariableAccess && ContextUtils.ENTITY_ANY.equals(entity);
        
        return hasAccessToAllVariables;
      }
    }
    else if (ContextUtils.ENTITY_ANY.equals(entity) || Objects.deepEquals(accessedEntity, entity))
      return true;
    
    boolean entityTypeMatchByGroup = accessedEntityType != null && accessedEntityType + ContextUtils.ENTITY_GROUP_SHIFT == entityType;
    boolean groupMatch = ContextUtils.ENTITY_ANY.equals(entity) || Objects.equals(accessedEntityGroup, entity) || Objects.equals(ContextUtils.getBaseGroup(accessedEntityGroup), entity);
    
    return entityTypeMatchByGroup && groupMatch;
  }
  
  private boolean equalsPaths(String accessedPath, Context accessedContext)
  {
    return Util.equals(accessedPath, accessedContext.getPath());
  }

  private String getAccessedPath(Context accessedContext, Permission permission)
  {
    if (permission.getContext() != null)
    {
      return permission.getContext();
    }
    else
    {
      return accessedContext != null ? accessedContext.getPath() : null;
    }
  }
  
  @Override
  public String getLevel(Permissions permissions, String accessedContext, Integer accessedEntityType, String accessedEntity, String accessedEntityGroup, ContextManager cm) throws SecurityException
  {
    return getLevelInfo(permissions, accessedContext, accessedEntityType, accessedEntity, accessedEntityGroup, cm).getLevel();
  }
  
  public LevelInfo getLevelInfo(Permissions existingPermissions, String accessedContext, Integer accessedEntityType, String accessedEntity, String accessedEntityGroup, ContextManager cm)
      throws SecurityException
  {
    boolean hasPermissionsForEntities = false;
    Integer permissionIndex = 0;
    
    try
    {
      if (existingPermissions == null)
      {
        return new LevelInfo(NULL_PERMISSIONS, hasPermissionsForEntities, permissionIndex);
      }
      
      for (Permission permission : existingPermissions)
      {
        if (permission.getContext() == null)
        {
          return new LevelInfo(permission, hasPermissionsForEntities, permissionIndex);
        }
        
        for (String allowedPath : getAllowedPaths(permission.getContext(), cm))
        {
          if(!permissionMatchesToMask(allowedPath, accessedContext))
          {
            continue;
          }
          
          int allowedEntityType = permission.getEntityType();
          
          if (allowedEntityType == ContextUtils.ENTITY_ANY_TYPE)
          {
            return new LevelInfo(permission, hasPermissionsForEntities, permissionIndex);
          }
          
          hasPermissionsForEntities = true;
          
          String allowedEntity = permission.getEntity();
          
          boolean entityTypeMatch = accessedEntityType != null && accessedEntityType == allowedEntityType;
          boolean entityMatch = ContextUtils.ENTITY_ANY.equals(allowedEntity) || Objects.equals(accessedEntity, allowedEntity);
          
          if (entityTypeMatch && entityMatch)
          {
            return new LevelInfo(permission, hasPermissionsForEntities, permissionIndex);
          }
          
          boolean entityTypeMatchByGroup = accessedEntityType != null && accessedEntityType + ContextUtils.ENTITY_GROUP_SHIFT == allowedEntityType;
          boolean groupMatch = ContextUtils.ENTITY_ANY.equals(allowedEntity) || Objects.equals(accessedEntityGroup, allowedEntity);
          
          if (entityTypeMatchByGroup && groupMatch)
          {
            return new LevelInfo(permission, hasPermissionsForEntities, permissionIndex);
          }
        }
        permissionIndex++;
      }
      
      return new LevelInfo(NULL_PERMISSIONS, hasPermissionsForEntities, permissionIndex);
    }
    catch (Exception ex)
    {
      throw new SecurityException("Error getting permission type of '" + existingPermissions + "' in '" + accessedContext + "': ", ex);
    }
  }
  
   private boolean permissionMatchesToMask(String allowedPath, String accessedContext)
  {
    return ContextUtils.matchesToMask(allowedPath, accessedContext, true, false);
  }

  @Override
  public boolean canSee(Permissions permissions, String context, ContextManager cm)
  {
    try
    {
      if (permissions == null)
      {
        return false;
      }
      
      for (Permission permission : permissions)
      {
        if (permission.getLevel().equals(NULL_PERMISSIONS))
        {
          continue;
        }
        
        if (permission.getContext() == null)
        {
          return true;
        }
        
        List<String> allowedPaths = getAllowedPaths(permission.getContext(), cm);
        
        for (String allowedPath : allowedPaths)
        {
          if (ContextUtils.matchesToMask(allowedPath, context, false, false))
          {
            return false;
          }
          
          if (ContextUtils.matchesToMask(allowedPath, context, false, true))
          {
            return true;
          }
        }
      }
      
      return false;
    }
    catch (Exception ex)
    {
      Log.SECURITY.error("Error checking permissions: ", ex);
      return false;
    }
  }
  
  public boolean hasNecessaryLevel(String existingLevel, String requiredLevel)
  {
    int existingLevelPattern = findPattern(existingLevel);
    int requiredLevelPattern = findPattern(requiredLevel);
    
    if ((requiredLevelPattern & existingLevelPattern) != requiredLevelPattern)
    {
      return false;
    }
    
    return true;
  }
  
  private int findPattern(String level) throws SecurityException
  {
    for (int i = 0; i < permissionTypes.length; i++)
    {
      if (permissionTypes[i].getName().equals(level))
      {
        return permissionTypes[i].getPattern();
      }
    }
    throw new SecurityException("Permission level '" + level + "' not found");
  }
  
  @Override
  public boolean isValid(String level)
  {
    try
    {
      for (int i = 0; i < permissionTypes.length; i++)
      {
        if (permissionTypes[i].getName().equals(level))
        {
          return true;
        }
      }
      return false;
    }
    catch (Exception ex)
    {
      return false;
    }
  }
  
  @Override
  public Map<Object, String> getPermissionLevels()
  {
    Map<Object, String> pm = new LinkedHashMap();
    
    for (int i = 0; i < permissionTypes.length; i++)
    {
      pm.put(permissionTypes[i].getName(), permissionTypes[i].getDescription());
    }
    
    return pm;
  }
  
  @Override
  public Map<String, String> getRolePermissionLevels(int entityType)
  {
    Map<String, String> pm = new LinkedHashMap();
    
    pm.put(rolePermissionTypes[0].getName(), Cres.get().getString("secNoPerms"));
    
    if (entityType == ContextUtils.ENTITY_ACTION_GROUP || entityType == ContextUtils.ENTITY_ACTION || entityType == ContextUtils.ENTITY_FUNCTION_GROUP || entityType == ContextUtils.ENTITY_FUNCTION)
      pm.put(rolePermissionTypes[1].getName(), Cres.get().getString("secExecute"));
    else if (entityType == ContextUtils.ENTITY_VARIABLE_GROUP || entityType == ContextUtils.ENTITY_VARIABLE)
    {
      pm.put(rolePermissionTypes[1].getName(), Cres.get().getString("secRead"));
      pm.put(rolePermissionTypes[2].getName(), Cres.get().getString("secWrite"));
    }
    else if (entityType == ContextUtils.ENTITY_EVENT_GROUP || entityType == ContextUtils.ENTITY_EVENT)
      pm.put(rolePermissionTypes[1].getName(), Cres.get().getString("secRead"));
    else if (entityType == ContextUtils.ENTITY_ANY_TYPE)
    {
      pm.put(rolePermissionTypes[1].getName(), Cres.get().getString("secRead") + "/" + Cres.get().getString("secExecute"));
      pm.put(rolePermissionTypes[2].getName(), Cres.get().getString("secAll"));
    }

    return pm;
  }
  
  @Override
  public String canActivate(Permissions existingPermissions, Permissions requiredPermissions, ContextManager cm)
  {
    String canNotSetPermissionsMessage = "Cannot set permissions for '%s' to '%s' because your own permission level for '%s' is '%s'";
    
    // Checking required permissions levels
    for (Permission requiredPermission : requiredPermissions)
    {
      String context = requiredPermission.getContext();
      int entityType = requiredPermission.getEntityType();
      String entity = requiredPermission.getEntity();
      
      String existingPermissionLevel = getLevel(existingPermissions, context, entityType, entity, null, cm);
      String requiredPermissionLevel = getLevel(requiredPermissions, context, entityType, entity, null, cm);
      
      if (!hasNecessaryLevel(existingPermissionLevel, requiredPermissionLevel))
      {
        String requiredEntityInfo = StringUtils.removeSuffix(requiredPermission.encode(), Permission.PERMISSION_FIELDS_SEPARATOR + requiredPermissionLevel);
        
        return String.format(canNotSetPermissionsMessage, requiredEntityInfo, requiredPermissionLevel, requiredEntityInfo, existingPermissionLevel);
      }
    }
    
    // Checking existing permissions for special restrictions
    for (Permission existingPermission : existingPermissions)
    {
      String context = existingPermission.getContext();
      int entityType = existingPermission.getEntityType();
      String entity = existingPermission.getEntity();
      
      String requiredPermissionLevel = getLevel(requiredPermissions, context, entityType, entity, null, cm);
      String existingPermissionLevel = getLevel(existingPermissions, context, entityType, entity, null, cm);
      
      if (!hasNecessaryLevel(existingPermissionLevel, requiredPermissionLevel))
      {
        String requiredEntityInfo = StringUtils.removeSuffix(existingPermission.encode(), Permission.PERMISSION_FIELDS_SEPARATOR + existingPermissionLevel);
        
        return String.format(canNotSetPermissionsMessage, requiredEntityInfo, requiredPermissionLevel, requiredEntityInfo, existingPermissionLevel);
      }
    }
    
    return null;
  }
  
  protected List<String> getAllowedPaths(String context, ContextManager cm)
  {
    if (cm != null && context.endsWith(ContextUtils.CONTEXT_NAME_SEPARATOR + ContextUtils.CONTEXT_GROUP_MASK))
    {
      String truncated = context.substring(0, context.length() - 2);
      
      Context<Context> groupContext = cm.get(truncated, unchecked);
      
      if (groupContext != null && groupContext.isMapped())
      {
        List<String> allowedPaths = new LinkedList();
        for (Context mappedChild : groupContext.getMappedChildren(unchecked))
        {
          allowedPaths.add(mappedChild.getPath());
        }
        return allowedPaths;
      }
    }
    
    return Collections.singletonList(context);
  }
}
