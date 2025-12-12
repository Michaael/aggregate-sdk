package com.tibbo.aggregate.common.security;

import com.tibbo.aggregate.common.*;

public class ServerPermissionChecker extends DefaultPermissionChecker
{
  public static final String OBSERVER_PERMISSIONS = "observer";
  public static final String OPERATOR_PERMISSIONS = "operator";
  public static final String MANAGER_PERMISSIONS = "manager";
  public static final String ENGINEER_PERMISSIONS = "engineer";
  public static final String ADMIN_PERMISSIONS = "admin";
  
  public static final String RWX_PERMISSIONS = "rwx";
  public static final String R_X_PERMISSIONS = "r_x";
  
  public static final int RWX_LEVEL = 3;
  public static final int R_X_LEVEL = 1;
  
  public ServerPermissionChecker()
  {
    PermissionType nullType = new PermissionType(Integer.parseInt("00000000", 2), NULL_PERMISSIONS, Cres.get().getString("secNoPerms"));
    PermissionType observerType = new PermissionType(Integer.parseInt("00000001", 2), OBSERVER_PERMISSIONS, Cres.get().getString("secObserverPerms"));
    PermissionType operatorType = new PermissionType(Integer.parseInt("00000011", 2), OPERATOR_PERMISSIONS, Cres.get().getString("secOperatorPerms"));
    PermissionType managerType = new PermissionType(Integer.parseInt("00000111", 2), MANAGER_PERMISSIONS, Cres.get().getString("secManagerPerms"));
    PermissionType engineerType = new PermissionType(Integer.parseInt("00001111", 2), ENGINEER_PERMISSIONS, Cres.get().getString("secEngineerPerms"));
    PermissionType adminType = new PermissionType(Integer.parseInt("00011111", 2), ADMIN_PERMISSIONS, Cres.get().getString("secAdminPerms"));
    setPermissionTypes(new PermissionType[] { nullType, observerType, operatorType, managerType, engineerType, adminType });
    
    PermissionType r_xType = new PermissionType(Integer.parseInt("00000001", 2), R_X_PERMISSIONS, R_X_PERMISSIONS);
    PermissionType rwxType = new PermissionType(Integer.parseInt("00000011", 2), RWX_PERMISSIONS, RWX_PERMISSIONS);
    setRolePermissionTypes(new PermissionType[] { nullType, r_xType, rwxType });
    
  }
  
  public static Permissions getObserverPermissions()
  {
    return new Permissions(null, OBSERVER_PERMISSIONS);
  }
  
  public static Permissions getOperatorPermissions()
  {
    return new Permissions(null, OPERATOR_PERMISSIONS);
  }
  
  public static Permissions getManagerPermissions()
  {
    return new Permissions(null, MANAGER_PERMISSIONS);
  }
  
  public static Permissions getEngineerPermissions()
  {
    return new Permissions(null, ENGINEER_PERMISSIONS);
  }
  
  public static Permissions getAdminPermissions()
  {
    return new Permissions(null, ADMIN_PERMISSIONS);
  }
}
