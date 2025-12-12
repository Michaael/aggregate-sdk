package com.tibbo.aggregate.common.device.sync;

import com.tibbo.aggregate.common.device.DeviceContext;
import com.tibbo.aggregate.common.expression.Expression;

public class SettingSynchronizationOptions implements Cloneable
{
  private int mode = DeviceContext.SYNC_MODE_NORMAL;
  
  private Long updateHistoryStorageTime; // Ms
  private Long syncPeriod;
  private int historyRate;
  private String filter;
  private String master;
  private String condition;
  private boolean deliverUpdates;
  private boolean addPreviousValueToVariableUpdateEvent;
  
  private Expression filterExpression;
  private Expression conditionExpression;
  private SynchronizationHandler synchronizationHandler;
  
  // Transient counter
  private int synchronizationsCounter;
  
  // Enabled flag of the setting (to handle in DefaultDeviceContext:readVariableDescriptors())
  private boolean enabled = true;
  
  public SettingSynchronizationOptions()
  {
    super();
    deliverUpdates = false;
  }
  
  public SettingSynchronizationOptions(Long syncPeriod)
  {
    this();
    this.syncPeriod = syncPeriod;
  }
  
  public SettingSynchronizationOptions(Long updateHistoryStorageTime, Long syncPeriod)
  {
    this(syncPeriod);
    this.updateHistoryStorageTime = updateHistoryStorageTime;
  }
  
  public SettingSynchronizationOptions(Long updateHistoryStorageTime, Long syncPeriod, int historyRate)
  {
    this(updateHistoryStorageTime, syncPeriod);
    this.historyRate = historyRate;
  }
  
  public int getMode()
  {
    return mode;
  }
  
  public void setMode(int mode)
  {
    this.mode = mode;
  }

  public boolean isAddPreviousValueToVariableUpdateEvent()
  {
    return addPreviousValueToVariableUpdateEvent;
  }
  
  public void setAddPreviousValueToVariableUpdateEvent(boolean addPreviousValueToVariableUpdateEvent)
  {
    this.addPreviousValueToVariableUpdateEvent = addPreviousValueToVariableUpdateEvent;
  }
  
  public Long getSyncPeriod()
  {
    return syncPeriod;
  }
  
  public void setSyncPeriod(Long syncPeriod)
  {
    this.syncPeriod = syncPeriod;
  }
  
  public String getMaster()
  {
    return master;
  }
  
  public void setMaster(String master)
  {
    this.master = master;
  }
  
  public Long getUpdateHistoryStorageTime()
  {
    return updateHistoryStorageTime;
  }
  
  public void setUpdateHistoryStorageTime(Long updateHistoryStorageTime)
  {
    this.updateHistoryStorageTime = updateHistoryStorageTime;
  }
  
  public int getHistoryRate()
  {
    return historyRate;
  }
  
  public void setHistoryRate(int historyRate)
  {
    this.historyRate = historyRate;
  }
  
  public String getFilter()
  {
    return filter;
  }
  
  public Expression getFilterExpression()
  {
    if (filterExpression == null && filter != null && filter.length() > 0)
    {
      filterExpression = new Expression(filter);
    }
    return filterExpression;
  }
  
  public void setFilter(String filter)
  {
    this.filter = filter;
    filterExpression = null;
  }
  
  public String getCondition()
  {
    return condition;
  }
  
  public Expression getConditionExpression()
  {
    if (conditionExpression == null && condition != null && condition.length() > 0)
    {
      conditionExpression = new Expression(condition);
    }
    return conditionExpression;
  }
  
  public void setCondition(String condition)
  {
    this.condition = condition;
    conditionExpression = null;
  }
  
  public SynchronizationHandler getSynchronizationHandler()
  {
    return synchronizationHandler;
  }
  
  public void setSynchronizationHandler(SynchronizationHandler synchronizationHandler)
  {
    this.synchronizationHandler = synchronizationHandler;
  }
  
  public int getSynchronizationsCounter()
  {
    return synchronizationsCounter;
  }
  
  public void incrementSynchronizationsCounter()
  {
    synchronizationsCounter++;
  }
  
  public boolean getDeliverUpdates()
  {
    return deliverUpdates;
  }
  
  public void setDeliverUpdates(boolean deliverUpdates)
  {
    this.deliverUpdates = deliverUpdates;
  }
  
  public boolean isEnabled()
  {
    return enabled;
  }
  
  public void setEnabled(boolean enabled)
  {
    this.enabled = enabled;
  }
  
  @Override
  public SettingSynchronizationOptions clone()
  {
    try
    {
      SettingSynchronizationOptions clone = (SettingSynchronizationOptions) super.clone();
      clone.setSynchronizationHandler(null);
      return clone;
    }
    catch (CloneNotSupportedException ex)
    {
      throw new IllegalStateException(ex.getMessage(), ex);
    }
  }
  
  public boolean allowsChangesOnly()
  {
    int hr = getHistoryRate();
    
    return hr == DeviceContext.HISTORY_RATE_NORMAL || hr == DeviceContext.HISTORY_RATE_ERROR || hr == DeviceContext.HISTORY_RATE_OFFLINE_ERROR;
  }
  
  public boolean allowsErroneousValues()
  {
    int hr = getHistoryRate();
    
    return hr == DeviceContext.HISTORY_RATE_ERROR || hr == DeviceContext.HISTORY_RATE_OFFLINE_ERROR || hr == DeviceContext.HISTORY_RATE_UNCHANGED_ERROR
        || hr == DeviceContext.HISTORY_RATE_UNCHANGED_OFFLINE_ERROR;
  }
  
  public boolean allowsOfflineValues()
  {
    int hr = getHistoryRate();
    
    return hr == DeviceContext.HISTORY_RATE_OFFLINE_ERROR || hr == DeviceContext.HISTORY_RATE_UNCHANGED_OFFLINE || hr == DeviceContext.HISTORY_RATE_UNCHANGED_OFFLINE_ERROR;
  }
  
  public boolean hasCustomStatus()
  {
    if (synchronizationHandler != null)
    {
      return synchronizationHandler.getCustomStatus() != null;
    }
    return false;
  }
}
