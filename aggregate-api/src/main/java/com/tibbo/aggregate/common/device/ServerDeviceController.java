package com.tibbo.aggregate.common.device;

import com.tibbo.aggregate.common.context.*;

public interface ServerDeviceController
{
  public static final String TYPE_GENERIC = "generic";
  
  String getType();
  
  String getTypeName();
  
  void install(DeviceContext deviceContext) throws ContextException;
  
  void uninstall() throws ContextException;
  
  void start();
  
  void stop();
  
  String getDeviceIconId();
  
  String getGroupName();
}
