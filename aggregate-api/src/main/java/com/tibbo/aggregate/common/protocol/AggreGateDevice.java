package com.tibbo.aggregate.common.protocol;

import com.tibbo.aggregate.common.util.*;

public abstract class AggreGateDevice
{
  public static final long DEFAULT_COMMAND_TIMEOUT = TimeHelper.HOUR_IN_MS;

  private String id;
  private String type;
  
  private String name;
  private String description;
  private boolean disabled;
  
  private long commandTimeout = DEFAULT_COMMAND_TIMEOUT;

  public AggreGateDevice(String id, String type)
  {
    this(id, type, id, type);
  }

  public AggreGateDevice(String id, String type, String name, String description)
  {
    this.id = id;
    this.type = type;
    this.name = name;
    this.description = description;
  }
  
  public String getName()
  {
    return name;
  }
  
  public void setName(String name)
  {
    this.name = name;
  }
  
  public void setDescription(String description)
  {
    this.description = description;
  }
  
  public String getType()
  {
    return type;
  }
  
  public String getId()
  {
    return id;
  }
  
  public String getDescription()
  {
    return description;
  }
  
  public boolean isDisabled()
  {
    return disabled;
  }
  
  public void setDisabled(boolean disabled)
  {
    this.disabled = disabled;
  }
  
  public long getCommandTimeout()
  {
    return commandTimeout;
  }
  
  public void setCommandTimeout(long commandTimeout)
  {
    this.commandTimeout = commandTimeout;
  }
  
  public String getInfo()
  {
    return type;
  }
  
  @Override
  public String toString()
  {
    String res = (getDescription() != null && getDescription().length() > 0) ? getDescription() : getType();
    return res + " (" + getInfo() + ")";
  }
  
  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((description == null) ? 0 : description.hashCode());
    result = prime * result + (disabled ? 1231 : 1237);
    result = prime * result + ((name == null) ? 0 : name.hashCode());
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
    AggreGateDevice other = (AggreGateDevice) obj;
    if (description == null)
    {
      if (other.description != null)
      {
        return false;
      }
    }
    else if (!description.equals(other.description))
    {
      return false;
    }
    if (disabled != other.disabled)
    {
      return false;
    }
    if (name == null)
    {
      if (other.name != null)
      {
        return false;
      }
    }
    else if (!name.equals(other.name))
    {
      return false;
    }
    return true;
  }
  
}
