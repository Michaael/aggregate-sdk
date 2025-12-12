package com.tibbo.aggregate.common.security;

import java.util.*;
import java.util.concurrent.locks.*;

import com.tibbo.aggregate.common.util.*;

public class Permissions implements Iterable<Permission>
{
  public final static char PERMISSIONS_SEPARATOR = ',';
  
  private final List<Permission> permissions = new LinkedList();
  private final ReentrantReadWriteLock permissionsLock = new ReentrantReadWriteLock();
  
  private Map<String, LinkedList<RoleRule>> roleBasedRXPermissions = new HashMap<String, LinkedList<RoleRule>>();
  private Map<String, LinkedList<RoleRule>> roleBasedRWXPermissions = new HashMap<String, LinkedList<RoleRule>>();
  private Map<String, LinkedList<RoleRule>> roleBasedProhibitions = new HashMap<String, LinkedList<RoleRule>>();
  
  private boolean useRoleBasedPermissions;
  private boolean isWritePermissions;
  
  public Permissions()
  {
    
  }
  
  public Permissions(String data, PermissionChecker checker)
  {
    if (data == null)
    {
      data = "";
    }
    
    List<String> pd = StringUtils.split(data, Permissions.PERMISSIONS_SEPARATOR);
    
    for (String pde : pd)
    {
      String permSrc = pde.trim();
      
      if (permSrc.length() > 0)
      {
        permissions.add(new Permission(permSrc, checker));
      }
    }
  }
  
  public Permissions(String context, String type)
  {
    permissions.add(new Permission(context, type));
  }
  
  public Permissions(List<Permission> permissions, boolean isWritePermissions)
  {
    for (Permission permission : permissions)
    {
      this.permissions.add(permission);
    }
    
    this.isWritePermissions = isWritePermissions;
  }
  
  public Permissions(String data, boolean isWritePermissions)
  {
    this(data, (PermissionChecker) null);
    this.isWritePermissions = isWritePermissions;
  }
  
  public Permissions(String data)
  {
    this(data, (PermissionChecker) null);
  }
  
  public String encode()
  {
    StringBuffer enc = new StringBuffer();
    
    int i = 0;
    
    permissionsLock.readLock().lock();
    try
    {
      for (Permission perm : permissions)
      {
        if (i > 0)
        {
          enc.append(Permissions.PERMISSIONS_SEPARATOR);
        }
        
        enc.append(perm.encode());
        
        i++;
      }
    }
    finally
    {
      permissionsLock.readLock().unlock();
    }
    
    return enc.toString();
  }
  
  @Override
  public String toString()
  {
    return encode();
  }
  
  public int size()
  {
    return permissions.size();
  }
  
  public Permission get(int index)
  {
    return permissions.get(index);
  }
  
  /**
   * Note: Permissions class is not thread-safe. Don't add new Permission objects to Permissions after initialization is finished.
   */
  public Permissions add(Permission permission)
  {
    permissionsLock.writeLock().lock();
    try
    {
      permissions.add(permission);
    }
    finally
    {
      permissionsLock.writeLock().unlock();
    }
    return this;
  }
  
  public Permissions addAll(Permissions permissions)
  {
    permissionsLock.writeLock().lock();
    try
    {
      for (Permission permission : permissions)
      {
        this.permissions.add(permission);
      }
    }
    finally
    {
      permissionsLock.writeLock().unlock();
    }
    return this;
  }
  
  public boolean isWritePermissions()
  {
    return isWritePermissions;
  }
  
  public void setWritePermissions(boolean isWritePermissions)
  {
    this.isWritePermissions = isWritePermissions;
  }
  
  public Map<String, LinkedList<RoleRule>> getRoleBasedRXPermissions()
  {
    return roleBasedRXPermissions;
  }
  
  public Map<String, LinkedList<RoleRule>> getRoleBasedRWXPermissions()
  {
    return roleBasedRWXPermissions;
  }
  
  public List<Permission> getPermissions()
  {
    return permissions;
  }
  
  public void setRoleBasedRXPermissions(Map<String, LinkedList<RoleRule>> roleBasedPermissions)
  {
    this.roleBasedRXPermissions = roleBasedPermissions;
  }
  
  public void setRoleBasedRWXPermissions(Map<String, LinkedList<RoleRule>> roleBasedPermissions)
  {
    this.roleBasedRWXPermissions = roleBasedPermissions;
  }
  
  public Map<String, LinkedList<RoleRule>> getRoleBasedProhibitions()
  {
    return roleBasedProhibitions;
  }
  
  public void setRoleBasedProhibitions(Map<String, LinkedList<RoleRule>> roleBasedProhibitions)
  {
    this.roleBasedProhibitions = roleBasedProhibitions;
  }
  
  public boolean isUseRoleBasedPermissions()
  {
    return useRoleBasedPermissions;
  }
  
  public void setUseRoleBasedPermissions(boolean useRoleBasedPermissions)
  {
    this.useRoleBasedPermissions = useRoleBasedPermissions;
  }
  
  @Override
  public Iterator<Permission> iterator()
  {
    return permissions.iterator();
  }
  
  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    
    result = (prime * result) + ((permissions == null) ? 0 : permissions.hashCode());
    
    return result;
  }
  
  @Override
  public boolean equals(Object obj)
  {
    if (this == obj)
    {
      return true;
    }
    if (obj == null)
    {
      return false;
    }
    if (getClass() != obj.getClass())
    {
      return false;
    }
    Permissions other = (Permissions) obj;
    
    if (permissions == null)
    {
      if (other.permissions != null)
      {
        return false;
      }
    }
    else if (!permissions.equals(other.permissions))
    {
      return false;
    }
    return true;
  }
}
