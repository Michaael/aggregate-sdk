package com.tibbo.aggregate.common.discovery;

import com.tibbo.aggregate.common.datatable.*;

public class DiscoveryResultItem
{
  private DiscoverableServiceDefinition serviceDefinition;
  private DeviceRecommendation deviceRecommendation;
  private DataRecord parameters;
  
  public DiscoveryResultItem(DiscoverableServiceDefinition aDiscoverableServiceDefinition, DataRecord parametersDataRecord)
  {
    super();
    serviceDefinition = aDiscoverableServiceDefinition;
    setParameters(parametersDataRecord);
  }
  
  public DiscoveryResultItem(DiscoverableServiceDefinition aDiscoverableServiceDefinition, DataRecord parametersDataRecord, DeviceRecommendation aDeviceRecommendation)
  {
    this(aDiscoverableServiceDefinition, parametersDataRecord);
    deviceRecommendation = aDeviceRecommendation;
  }
  
  public DiscoverableServiceDefinition getServiceDefinition()
  {
    return serviceDefinition;
  }
  
  public DataRecord getParameters()
  {
    return parameters;
  }
  
  public void setParameters(DataRecord parametersDataRecord)
  {
    parameters = parametersDataRecord;
  }
  
  public String getDeviceName()
  {
    return deviceRecommendation == null ? null : deviceRecommendation.getName();
  }
  
  public String getDeviceDescription()
  {
    return deviceRecommendation == null ? null : deviceRecommendation.getDescription();
  }
  
  public DiscoveryProvider getDiscoveryProvider()
  {
    return serviceDefinition.getDiscoveryProvider();
  }
  
  public String getServiceName()
  {
    return serviceDefinition.getName();
  }
  
  public String getServiceDescription()
  {
    return serviceDefinition.getDescription();
  }
  
  public boolean isEnabledByDefault()
  {
    return serviceDefinition.isEnabledByDefault();
  }
  
  public DataTable getParametersTable()
  {
    return parameters.wrap();
  }
  
  public DeviceRecommendation getDeviceRecommendation()
  {
    return deviceRecommendation;
  }
  
  @Override
  public String toString()
  {
    return "DiscoveryResultItem [recommendation= " + deviceRecommendation + ", parameters= " + parameters + "]";
  }
}
