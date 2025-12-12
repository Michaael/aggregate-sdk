package com.tibbo.aggregate.common.discovery;

import java.util.*;

import com.tibbo.aggregate.common.context.*;
import com.tibbo.aggregate.common.datatable.*;
import com.tibbo.aggregate.common.device.*;

public interface DiscoveryProvider
{
  String getName();
  
  String getDescription();
  
  String getDriver();
  
  Collection<VariableDefinition> getDiscoverySettingsVariableDefinitions();
  
  DataTable getDiscoverySettings(String settingsVariable, ContextManager cm, CallerController cc);
  
  List<DiscoverableServiceDefinition> getAvailableServices();
  
  DeviceContext createDevice(String username, String deviceName, String description, String address, CallerController caller) throws ContextException;
  
  DiscoverableServiceDefinition getDiscoverableServiceDefinition(String name);
  
  void configureService(String name, boolean enable, DeviceContext deviceContext, DataTable parameters, CallerController callerController) throws ContextException;
  
}
