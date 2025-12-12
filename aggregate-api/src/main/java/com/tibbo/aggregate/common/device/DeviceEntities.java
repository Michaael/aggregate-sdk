package com.tibbo.aggregate.common.device;

import java.util.Map;

public interface DeviceEntities
{
  boolean isActive(String entity);
  
  Map<String, DeviceEntityDescriptor> getDescriptors();
  
  boolean hasSpecificEntities();
}
