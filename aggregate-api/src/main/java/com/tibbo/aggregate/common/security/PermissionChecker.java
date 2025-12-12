package com.tibbo.aggregate.common.security;

import java.util.*;

import com.tibbo.aggregate.common.context.*;

/**
 * The <code>PermissionChecker</code> is responsible for granting/denying access of various callers to different contexts.
 */
public interface PermissionChecker
{
  /**
   * Returns a map of all permission levels supported by the checker. Each level is defined by a name string and description string.
   */
  Map<Object, String> getPermissionLevels();
  
  /**
   * Returns a map of all supported permission levels for the specified entity type. Each level is defined by a name and a description.
   */
  Map<String, String> getRolePermissionLevels(int entityType);
  
  /**
   * Returns true if permission level {@code permissionLevel} is supported by the checker.
   */
  boolean isValid(String permissionLevel);
  
  /**
   * Returns true if {@code caller} is allowed to access an entity those permissions are {@code requiredPermissions}.
   */
  boolean has(CallerController caller, Permissions requiredPermissions, Context accessedContext, EntityDefinition accessedEntityDefinition);
  
  /**
   * Returns the effective permission level of the calling party (those permissions are identified by {@code permissions}) for the entity.
   */
  String getLevel(Permissions permissions, String accessedContext, Integer accessedEntityType, String accessedEntity, String accessedEntityGroup, ContextManager cm);
  
  /**
   * Returns true if the calling party (those permissions are identified by {@code perms}) can see {@code context} among children of its parent context because it has non-null permissions for one or
   * more direct/nested children of {@code context}.
   */
  boolean canSee(Permissions perms, String context, ContextManager cm);
  
  /**
   * Checks whether the calling party (those permissions are identified by {@code has}) can set permissions of some other party to {@code need}.
   */
  String canActivate(Permissions has, Permissions need, ContextManager cm);
}
