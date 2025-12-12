package com.tibbo.aggregate.common.event;

import javax.annotation.Nullable;

import com.tibbo.aggregate.common.context.DefaultRequestController;
import com.tibbo.aggregate.common.data.Event;
import com.tibbo.aggregate.common.structure.Pinpoint;

public class FireEventRequestController extends DefaultRequestController
{
  private Long customExpirationPeriod;
  
  private boolean ignoreStorageErrors;

  private boolean suppressIfNotEnoughMemory;

  private String serverID = null;

  public FireEventRequestController()
  {
    super();
  }

  public FireEventRequestController(@Nullable Pinpoint pinpoint)
  {
    assignPinpoint(pinpoint);
  }

  public FireEventRequestController(Object originatorObject){
    super(originatorObject);
  }

  public FireEventRequestController(Long customExpirationPeriod)
  {
    super();
    this.customExpirationPeriod = customExpirationPeriod;
  }
  
  public FireEventRequestController(boolean ignoreStorageErrors)
  {
    super();
    this.ignoreStorageErrors = ignoreStorageErrors;
  }
  
  public Long getCustomExpirationPeriod()
  {
    return customExpirationPeriod;
  }
  
  public void setCustomExpirationPeriod(Long customExpirationPeriod)
  {
    this.customExpirationPeriod = customExpirationPeriod;
  }
  
  public boolean isIgnoreStorageErrors()
  {
    return ignoreStorageErrors;
  }
  
  public void setIgnoreStorageErrors(boolean ignoreStorageErrors)
  {
    this.ignoreStorageErrors = ignoreStorageErrors;
  }
  
  public Event process(Event event)
  {
    return event;
  }

  public boolean isSuppressIfNotEnoughMemory()
  {
    return suppressIfNotEnoughMemory;
  }

  public void setSuppressIfNotEnoughMemory(boolean value)
  {
    this.suppressIfNotEnoughMemory = value;
  }

  public String getServerId()
  {
    return serverID;
  }

  public void setServerId(String serverId)
  {
    this.serverID = serverId;
  }
}
