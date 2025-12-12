package com.tibbo.aggregate.common.protocol;

import com.tibbo.aggregate.common.util.*;

public abstract class AggreGateNetworkDevice extends AggreGateDevice
{
  public static final String DEFAULT_ADDRESS = "localhost";
  
  public static final long DEFAULT_CONNECTION_TIMEOUT = 20 * TimeHelper.SECOND_IN_MS;
  
  private String address;
  private int port;
  
  private long connectionTimeout = DEFAULT_CONNECTION_TIMEOUT;
  
  public AggreGateNetworkDevice(String id, String type, String address, int port)
  {
    super(id, type);
    this.address = address;
    this.port = port;
  }
  
  public String getAddress()
  {
    return address;
  }
  
  public int getPort()
  {
    return port;
  }
  
  public void setPort(int port)
  {
    this.port = port;
  }
  
  public void setAddress(String address)
  {
    this.address = address;
  }
  
  public long getConnectionTimeout()
  {
    return connectionTimeout;
  }
  
  public void setConnectionTimeout(long connectionTimeout)
  {
    this.connectionTimeout = connectionTimeout;
  }
  
  @Override
  public String getInfo()
  {
    return address + ":" + port;
  }
  
  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((address == null) ? 0 : address.hashCode());
    result = prime * result + port;
    return result;
  }
  
  @Override
  public boolean equals(Object obj)
  {
    if (this == obj)
    {
      return true;
    }
    if (!super.equals(obj))
    {
      return false;
    }
    if (getClass() != obj.getClass())
    {
      return false;
    }
    AggreGateNetworkDevice other = (AggreGateNetworkDevice) obj;
    if (address == null)
    {
      if (other.address != null)
      {
        return false;
      }
    }
    else if (!address.equals(other.address))
    {
      return false;
    }
    if (port != other.port)
    {
      return false;
    }
    return true;
  }
}
