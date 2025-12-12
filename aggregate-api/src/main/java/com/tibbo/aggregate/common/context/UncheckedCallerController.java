package com.tibbo.aggregate.common.context;

import com.tibbo.aggregate.common.data.*;
import com.tibbo.aggregate.common.event.*;
import com.tibbo.aggregate.common.security.*;

public class UncheckedCallerController extends AbstractCallerController
{
  private final Permissions permissions = DefaultPermissionChecker.getNullPermissions();
  
  public UncheckedCallerController()
  {
    super(null);
  }
  
  public UncheckedCallerController(String username)
  {
    super(null);
    setUsername(username);
  }
  
  public UncheckedCallerController(CallerData callerData)
  {
    super(callerData);
  }
  
  @Override
  public Permissions getPermissions()
  {
    return permissions;
  }
  
  @Override
  public boolean isPermissionCheckingEnabled()
  {
    return false;
  }
  
  @Override
  public boolean isLoggedIn()
  {
    return true;
  }
  
  @Override
  public boolean isHeadless()
  {
    return true;
  }
  
  public void handleContextEvent(Event event) throws EventHandlingException
  {
    throw new UnsupportedOperationException();
  }
}
