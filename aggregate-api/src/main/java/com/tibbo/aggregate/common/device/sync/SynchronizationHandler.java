package com.tibbo.aggregate.common.device.sync;

import java.util.*;

import com.tibbo.aggregate.common.*;
import com.tibbo.aggregate.common.context.*;
import com.tibbo.aggregate.common.datatable.*;
import com.tibbo.aggregate.common.device.*;

public abstract class SynchronizationHandler
{
  private static final String SERVER = Cres.get().getString("server");
  
  private SettingSynchronizationOptions synchronizationOptions;
  private DeviceContext deviceContext;
  private String variable;
  
  private VariableStatus customStatus;
  
  private boolean synchronizationEnabled = true;
  
  public SynchronizationHandler()
  {
  }
  
  public void initialize(DeviceContext deviceContext, String variable, SettingSynchronizationOptions synchronizationOptions, boolean check) throws ContextException
  {
    this.deviceContext = deviceContext;
    this.variable = variable;
    this.synchronizationOptions = synchronizationOptions;
  }
  
  public void deinitialize()
  {
    
  }
  
  public void startSynchronization() throws DeviceException, ContextException
  {
    
  }
  
  public boolean isSynchronizationEnabled()
  {
    return synchronizationEnabled;
  }
  
  protected void setSynchronizationEnabled(boolean synchronizationEnabled)
  {
    this.synchronizationEnabled = synchronizationEnabled;
  }
  
  public DataTable readFromCache(CallerController caller, RequestController request) throws ContextException
  {
    return null;
  }
  
  public void writeToCache(CallerController caller, RequestController request, DataTable value) throws ContextException
  {
  }
  
  public ValueWriter createServerWriter()
  {
    return new AbstractValueWriter(SERVER)
    {
      public void write(DataTable value, CallerController callerController, RequestController requestController) throws ContextException, DeviceException, DisconnectionException
      {
        deviceContext.setVariable(getVariable(), callerController, requestController, value);
      }
    };
  }
  
  public ValueReader createServerReader()
  {
    return new AbstractValueReader(SERVER)
    {
      public DataTable read(CallerController callerController, RequestController requestController) throws ContextException, DeviceException, DisconnectionException
      {
        DataTable value = deviceContext.getVariable(getVariable(), callerController, requestController);
        
        if (value != null && value.isImmutable())
        {
          value = value.clone();
        }
        
        return value;
      }
    };
  }
  
  public Date getServerModificationTime() throws ContextException
  {
    return deviceContext.getSettingStatus(variable).getTime();
  }
  
  public Date getDeviceModificationTime() throws ContextException, DeviceException, DisconnectionException
  {
    return deviceContext.getDriver().getVariableModificationTime(getVariable());
  }
  
  public boolean isUpdatedOnTheServer(CallerController caller) throws ContextException
  {
    return deviceContext.getSettingStatus(variable).isUpdated();
  }
  
  public int getDirectionOverride()
  {
    return DeviceContext.DIRECTION_AUTO;
  }
  
  public VariableDefinition getPersistentDefinition(VariableDefinition vd)
  {
    return vd.clone();
  }
  
  public SettingSynchronizationOptions getSynchronizationOptions()
  {
    return synchronizationOptions;
  }
  
  protected DeviceContext getDeviceContext()
  {
    return deviceContext;
  }
  
  protected String getVariable()
  {
    return variable;
  }
  
  public VariableStatus getCustomStatus()
  {
    return customStatus;
  }
  
  public void setCustomStatus(VariableStatus customStatus)
  {
    this.customStatus = customStatus;
  }
}
