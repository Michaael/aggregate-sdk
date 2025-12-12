package com.tibbo.aggregate.common.device.sync;

import java.util.Set;

public interface SynchronizationParameters
{
  SynchronizationParameters NULL = createDefaultSynchronizationParameters();
  
  boolean isConnectOnly();
  
  void addVariable(String variable);
  
  void removeVariable(String variable);
  
  boolean hasMoreVariables();

  String getId();
  
  boolean hasVariable(String variable);
  
  Set<String> getVariables();
  
  boolean isShouldPersistStatus();
  
  boolean isShouldReadMetadata();
  
  boolean isShouldUseExtendedStatus();
  
  boolean isFull();
  
  Integer getDirectionOverride();
  
  static SynchronizationParameters createReinitializerSynchronizationParameters()
  {
    return new ReinitializerSynchronizationParameters();
  }
  
  static SynchronizationParameters createDefaultSynchronizationParameters()
  {
    return new DefaultSynchronizationParameters();
  }
  
  static SynchronizationParameters createDefaultSynchronizationParameters(boolean useExtendedStatus)
  {
    return new DefaultSynchronizationParameters(useExtendedStatus);
  }
  
  static VariablesSynchronizationParameters ofVariable(String variableName)
  {
    return new VariablesSynchronizationParameters(variableName);
  }
  
  static VariablesSynchronizationParameters ofVariable(String variableName, int directionDeviceToServer)
  {
    return new VariablesSynchronizationParameters(variableName, directionDeviceToServer);
  }
}
