package com.tibbo.aggregate.common.security;

public class LevelInfo
{
  private Permission permission;
  private String level;
  private boolean hasPermissionsForEntities;
  private int permissionIndex;
  
  public LevelInfo(String level, boolean hasEntityLevelPermissions, int recordIndex)
  {
    super();
    this.level = level;
    this.hasPermissionsForEntities = hasEntityLevelPermissions;
    this.permissionIndex = recordIndex;
  }
  
  public LevelInfo(Permission permission, boolean hasPermissionsForEntities, int permissionIndex)
  {
    super();
    this.permission = permission;
    this.level = permission.getLevel() != null ? permission.getLevel() : DefaultPermissionChecker.NULL_PERMISSIONS;
    this.hasPermissionsForEntities = hasPermissionsForEntities;
    this.permissionIndex = permissionIndex;
  }
  
  public Permission getPermission()
  {
    return permission;
  }
  
  public String getLevel()
  {
    return level;
  }
  
  public boolean hasPermissionsForEntities()
  {
    return hasPermissionsForEntities;
  }
  
  public int getPermissionIndex()
  {
    return permissionIndex;
  }
}
