package com.tibbo.aggregate.common.context;

import java.util.Date;
import java.util.Map;

import com.tibbo.aggregate.common.datatable.DataTable;
import com.tibbo.aggregate.common.security.PermissionCache;
import com.tibbo.aggregate.common.security.Permissions;

public interface CallerController {
  String getUsername();
  
  String getInheritedUsername();
  
  String getEffectiveUsername();
  
  String getLogin();
  
  boolean isPermissionCheckingEnabled();
  
  Permissions getPermissions();
  
  PermissionCache getPermissionCache();
  
  boolean isLoggedIn();
  
  void login(String username, String inheritedUsername, Permissions permissons) throws ContextException;
  
  void logout();
  
  boolean isHeadless();
  
  Type getType();
  
  String getAddress();
  
  Date getCreationTime();
  
  CallerData getCallerData();

  // Values can be of string or boolean
  Map<String, Object> getProperties();
  
  void sendFeedback(int level, String message);
  
  Long getSessionIdCounter();
  
  String getSessionId();
  
  Long getTokenExpirationPeriod();
  
  Context lookup(String path);
  
  void cache(String path, Context context);
  
  void addLockedContext(Context context);
  
  void removeLockedContext(Context context);
  
  void unlockAllContexts();
  
  DataTable createLockedContextsTable();
  
  boolean isWeb();
  
  boolean isConnectionTerminatable();
  
  enum Type
  {
    DESKTOP, WEB_SOCKET, WEB_WIDGET_PLAYER, WEB_SERVICE, CONSUMER, WEB, REST
  }
  
}
