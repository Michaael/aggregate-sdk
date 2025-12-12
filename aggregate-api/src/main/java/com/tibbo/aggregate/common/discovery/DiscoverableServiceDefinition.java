package com.tibbo.aggregate.common.discovery;

import java.util.List;

import com.tibbo.aggregate.common.Log;
import com.tibbo.aggregate.common.datatable.DataTable;
import com.tibbo.aggregate.common.device.ServerDeviceController;

public abstract class DiscoverableServiceDefinition implements Cloneable
{
  private DiscoveryProvider discoveryProvider;
  private String name;
  private String description;
  
  /**
   * In most cases, every record in this table defines an instance of connection and authentication "credentials".
   * 
   * Some services, such as TCP port checker, use the whole table for a single check.
   */
  private DataTable connectionOptions;
  
  private long discoveryTimeout;
  private int discoveryRetries;
  
  private boolean useService = true;
  private boolean isEnabledByDefault;
  
  public DiscoverableServiceDefinition(DiscoveryProvider discoveryProvider, String name, String description, DataTable connectionOptions, int defaultDiscoveryTimeout, int defaultDiscoveryRetries,
      boolean isEnabledByDefault)
  {
    this.discoveryProvider = discoveryProvider;
    this.name = name;
    this.description = description;
    setConnectionOptions(connectionOptions);
    setDiscoveryTimeout(defaultDiscoveryTimeout);
    setDiscoveryRetries(defaultDiscoveryRetries);
    this.isEnabledByDefault = isEnabledByDefault;
  }
  
  public abstract DiscoverableService createServiceInstance();
  
  public String getName()
  {
    return name;
  }
  
  public DataTable getConnectionOptions()
  {
    return connectionOptions;
  }
  
  public void setConnectionOptions(DataTable connectionOptions)
  {
    this.connectionOptions = connectionOptions;
  }
  
  public String getDescription()
  {
    return description;
  }
  
  public long getDiscoveryTimeout()
  {
    return discoveryTimeout;
  }
  
  public void setDiscoveryTimeout(long defaultDiscoveryTimeoutInt)
  {
    discoveryTimeout = defaultDiscoveryTimeoutInt;
  }
  
  public int getDiscoveryRetries()
  {
    return discoveryRetries;
  }
  
  public void setDiscoveryRetries(int defaultDiscoveryRetries)
  {
    discoveryRetries = defaultDiscoveryRetries;
  }
  
  public DiscoveryProvider getDiscoveryProvider()
  {
    return discoveryProvider;
  }
  
  public String deviceType(String addressStrings)
  {
    return ServerDeviceController.TYPE_GENERIC;
  }
  
  public Integer priority()
  {
    return 0;
  }
  
  public DiscoverableServiceDefinition clone()
  {
    try
    {
      return (DiscoverableServiceDefinition) super.clone();
    }
    catch (CloneNotSupportedException ex)
    {
      Log.CORE.error("Unable to clone DiscoverableServiceDescription", ex);
      return null;
    }
  }
  
  public void setUseService(Boolean useService)
  {
    this.useService = useService;
  }
  
  public boolean isUseService()
  {
    return useService;
  }
  
  public boolean isEnabledByDefault()
  {
    return isEnabledByDefault;
  }
  
  public List<DiscoveryResultItem> check(String addressString, long timeoutMilliseconds, int triesCountInt)
  {
    return createServiceInstance().check(this, addressString, timeoutMilliseconds, triesCountInt);
  }
  
  @Override
  public String toString()
  {
    return "DiscoverableServiceDefinition " + name + " [" + discoveryProvider + ", " + ", timeout=" + discoveryTimeout + ", retries=" + discoveryRetries + ", options=" + connectionOptions + "]";
  }
}
