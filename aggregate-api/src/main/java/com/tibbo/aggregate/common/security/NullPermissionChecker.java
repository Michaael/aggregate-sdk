package com.tibbo.aggregate.common.security;

import java.util.*;

import com.tibbo.aggregate.common.context.*;

public class NullPermissionChecker implements PermissionChecker
{
  @Override
  public Map getPermissionLevels()
  {
    return null;
  }
  
  @Override
  public Map<String, String> getRolePermissionLevels(int entityType)
  {
    return null;
  }
  
  @Override
  public boolean has(CallerController caller, Permissions requiredPermissions, Context accessedContext, EntityDefinition accessedEntityDefinition)
  {
    return true;
  }
  
  @Override
  public boolean canSee(Permissions perms, String context, ContextManager cm)
  {
    return true;
  }
  
  @Override
  public String getLevel(Permissions permissions, String accessedContext, Integer accessedEntityType, String accessedEntity, String accessedEntityGroup, ContextManager cm)
  {
    return DefaultPermissionChecker.NULL_PERMISSIONS;
  }
  
  @Override
  public boolean isValid(String permType)
  {
    return true;
  }
  
  @Override
  public String canActivate(Permissions has, Permissions need, ContextManager cm)
  {
    return null;
  }
}
