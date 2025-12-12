package com.tibbo.aggregate.common.device;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.tibbo.aggregate.common.context.CallerController;
import com.tibbo.aggregate.common.context.Context;
import com.tibbo.aggregate.common.context.ContextException;
import com.tibbo.aggregate.common.context.ContextUtils;
import com.tibbo.aggregate.common.context.EntityDefinition;
import com.tibbo.aggregate.common.context.EventDefinition;
import com.tibbo.aggregate.common.context.FunctionDefinition;
import com.tibbo.aggregate.common.context.VariableDefinition;
import com.tibbo.aggregate.common.context.VariableStatus;
import com.tibbo.aggregate.common.data.Event;
import com.tibbo.aggregate.common.datatable.DataTable;
import com.tibbo.aggregate.common.datatable.TableFormat;
import com.tibbo.aggregate.common.device.sync.SynchronizationParameters;
import com.tibbo.aggregate.common.discovery.DiscoveryProvider;
import com.tibbo.aggregate.common.event.Acknowledgement;
import com.tibbo.aggregate.common.expression.Expression;
import com.tibbo.aggregate.common.plugin.BasePlugin;

public abstract class AbstractDeviceDriver extends BasePlugin implements DeviceDriver
{
  private DeviceContext deviceContext;
  
  private boolean connected;
  
  private String protocol;
  
  private final TableFormat connectionPropertiesFormat;
  
  public AbstractDeviceDriver(String description, TableFormat connectionPropertiesFormat)
  {
    super(description);
    this.connectionPropertiesFormat = connectionPropertiesFormat;
  }
  
  public AbstractDeviceDriver(String description, String protocol, TableFormat connectionProperties)
  {
    this(description, connectionProperties);
    this.protocol = protocol;
  }
  
  @Override
  public String getPrimaryAddress()
  {
    return null;
  }
  
  @Override
  public Set<String> getAddresses()
  {
    final String primaryAddress = getPrimaryAddress();
    return primaryAddress != null ? Collections.singleton(primaryAddress) : Collections.emptySet();
  }
  
  @Override
  public String getStatus()
  {
    return null;
  }
  
  @Override
  public List<Expression> getStatusExpressions(CallerController aCallerController)
  {
    return new LinkedList();
  }
  
  @Override
  public void setupDeviceContext(DeviceContext deviceContext) throws ContextException
  {
    this.deviceContext = deviceContext;
  }
  
  @Override
  public void configureDeviceAccount(DeviceContext deviceContext, CallerController caller) throws ContextException
  {
    
  }
  
  @Override
  public void accessSettingUpdated(String name)
  {
    // Do nothing
  }
  
  @Override
  public boolean shouldSynchronize(SynchronizationParameters parameters) throws ContextException
  {
    return true;
  }
  
  @Override
  public void startSynchronization() throws DeviceException
  {
    // Do nothing
  }
  
  @Override
  public boolean isUsesConnections()
  {
    return true;
  }
  
  @Override
  public boolean isConnected()
  {
    return connected;
  }
  
  @Override
  public void connect() throws DeviceException
  {
    setConnected(true);
  }
  
  @Override
  public boolean isUsesAssets()
  {
    return false;
  }
  
  @Override
  public List<DeviceAssetDefinition> readAssets() throws ContextException, DeviceException, DisconnectionException
  {
    return new LinkedList();
  }
  
  @Override
  public List<DeviceEntityDescriptor> readVariableDescriptors(List<DeviceAssetDefinition> assets) throws ContextException, DeviceException, DisconnectionException
  {
    List<VariableDefinition> definitions = this.readVariableDefinitions(assets, new DeviceEntityDescriptorList());
    
    List<DeviceEntityDescriptor> results = new LinkedList();
    for (VariableDefinition each : definitions)
    {
      DeviceEntityDescriptor variableDescriptor = new DeviceEntityDescriptor(each.getName(), each.getDescription(), each.getGroup(), false);
      results.add(variableDescriptor);
    }
    return results;
  }
  
  @Override
  public List<DeviceEntityDescriptor> readFunctionDescriptors(List<DeviceAssetDefinition> assets) throws ContextException, DeviceException, DisconnectionException
  {
    return null;
  }
  
  @Override
  public List<DeviceEntityDescriptor> readEventDescriptors(List<DeviceAssetDefinition> assets) throws ContextException, DeviceException, DisconnectionException
  {
    return null;
  }
  
  @Override
  public boolean isUseDeviceSideValuesCache()
  {
    return false;
  }
  
  @Override
  public List<VariableDefinition> readVariableDefinitions(DeviceEntities entities) throws ContextException, DeviceException, DisconnectionException
  {
    return null;
  }
  
  @Override
  public List<VariableDefinition> readVariableDefinitions(List<DeviceAssetDefinition> groups, DeviceEntities entities) throws ContextException, DeviceException, DisconnectionException
  {
    return readVariableDefinitions(null);
  }
  
  @Override
  public List<FunctionDefinition> readFunctionDefinitions(DeviceEntities entities) throws ContextException, DeviceException, DisconnectionException
  {
    return null;
  }
  
  @Override
  public List<FunctionDefinition> readFunctionDefinitions(List<DeviceAssetDefinition> groups, DeviceEntities entities) throws ContextException, DeviceException, DisconnectionException
  {
    return null;
  }
  
  @Override
  public List<EventDefinition> readEventDefinitions(DeviceEntities entities) throws ContextException, DeviceException, DisconnectionException
  {
    return null;
  }
  
  @Override
  public List<EventDefinition> readEventDefinitions(List<DeviceAssetDefinition> groups, DeviceEntities entities) throws ContextException, DeviceException, DisconnectionException
  {
    return null;
  }
  
  @Override
  public DataTable readVariableValue(VariableDefinition vd, CallerController caller) throws ContextException, DeviceException, DisconnectionException
  {
    return null;
  }
  
  @Override
  public void writeVariableValue(VariableDefinition vd, CallerController caller, DataTable value, DataTable deviceValue) throws ContextException, DeviceException, DisconnectionException
  {
  }
  
  @Override
  public DataTable executeFunction(FunctionDefinition fd, CallerController caller, DataTable parameters) throws ContextException, DeviceException, DisconnectionException
  {
    throw new UnsupportedOperationException();
  }
  
  @Override
  public Date getVariableModificationTime(String name) throws DeviceException, DisconnectionException
  {
    return null;
  }
  
  @Override
  public void updateVariableModificationTime(String name, Date value) throws DeviceException, DisconnectionException
  {
    
  }
  
  @Override
  public VariableStatus getCustomVariableStatus(String name) throws DeviceException, DisconnectionException
  {
    return null;
  }
  
  @Override
  public void disconnect() throws DeviceException
  {
    setConnected(false);
  }
  
  @Override
  public void finishSynchronization() throws DeviceException, DisconnectionException
  {
    
  }
  
  public void setConnected(boolean connected)
  {
    this.connected = connected;
  }
  
  public DeviceContext getDeviceContext()
  {
    return deviceContext;
  }
  
  @Override
  public DiscoveryProvider createDiscoveryProvider()
  {
    return null;
  }
  
  @Override
  public String getProtocol()
  {
    return protocol;
  }
  
  public TableFormat getConnectionPropertiesFormat()
  {
    return connectionPropertiesFormat;
  }
  
  @Override
  public TableFormat createConnectionPropertiesFormat()
  {
    TableFormat format = getConnectionPropertiesFormat();
    
    if (format == null)
    {
      format = new TableFormat(1, 1);
    }
    else
    {
      format = format.clone();
    }
    
    // Adding to the beginning of table
    format.addField(DeviceUtils.DESCRIPTION_FIELD, 0);
    format.addField(DeviceUtils.NAME_FIELD, 0);
    
    return format;
  }
  
  public List<DeviceEntityDescriptor> definitionsToDescriptors(List<? extends EntityDefinition> definitions, boolean activeByDefault)
  {
    List<DeviceEntityDescriptor> res = new LinkedList();
    
    for (EntityDefinition def : definitions)
    {
      res.add(new DeviceEntityDescriptor(def.getName(), def.getDescription(), ContextUtils.getVisualGroup(def.getGroup()), activeByDefault));
    }
    
    return res;
  }
  
  @Override
  public VariableDefinition discoverVariable(String name, Object helper)
  {
    return null;
  }
  
  @Override
  public FunctionDefinition discoverFunction(String name, Object helper)
  {
    return null;
  }
  
  @Override
  public EventDefinition discoverEvent(String name, Object helper)
  {
    return null;
  }
  
  @Override
  public void acklowledgeEvent(Event ev, Acknowledgement acknowledgement) throws ContextException, DeviceException
  {
    // Do nothing by default
  }
  
  @Override
  public boolean runFullSynchronizationOnStartup()
  {
    return false;
  }
  
  @Override
  public void deviceDestroyed(boolean moving)
  {
  }
  
  @Override
  public void deviceMoved(Context newParent, String newName) throws ContextException
  {
  }
  
  @Override
  public void readBufferedData() throws ContextException, DeviceException, DisconnectionException
  {
  }
}
