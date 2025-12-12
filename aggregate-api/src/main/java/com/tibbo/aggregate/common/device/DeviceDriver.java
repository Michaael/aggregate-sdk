package com.tibbo.aggregate.common.device;

import java.util.Date;
import java.util.List;
import java.util.Set;

import com.tibbo.aggregate.common.context.CallerController;
import com.tibbo.aggregate.common.context.Context;
import com.tibbo.aggregate.common.context.ContextException;
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
import com.tibbo.aggregate.common.plugin.AggreGatePlugin;

public interface DeviceDriver extends AggreGatePlugin
{
  // Device context control
  
  /**
   * <p>
   * Should return device connection properties as a single-record TableFormat. The format must include at least "name" field.
   *
   * <p>
   * This method is defined in AtestbstractDeviceDriver and should not be overridden in most cases.
   */
  TableFormat createConnectionPropertiesFormat();
  
  /**
   * <p>
   * This method is called once during creation of the device context (after device creation and on server startup). Driver implementation usually override it to add definitions of device
   * communication settings (e.g. IP address and port number) to the device context. Setup of synchronization settings for the whole device and individual settings is also performed at this point.
   *
   * <p>
   * Variable definitions representing device communication settings should belong to ContextUtils.GROUP_ACCESS variable group.
   */
  void setupDeviceContext(DeviceContext deviceContext) throws ContextException;
  
  /**
   * In contrast to {@link #setupDeviceContext(DeviceContext)}, this method is called only once in the end of initial device account creation. Its implementation can correct necessary default values
   * of device context variables, such as metadata reading mode or device settings caching mode. Overriding this method is not necessary for most drivers.
   */
  void configureDeviceAccount(DeviceContext deviceContext, CallerController caller) throws ContextException;
  
  /**
   * <p>
   * This method is called when any variable belonging to ContextUtils.GROUP_ACCESS variable group is changed. Driver implementations may override it to respond to device communication settings
   * changes by resetting driver internal state.
   *
   * <p>
   * Note, that in most cases overriding this method is not necessary as driver implementations should re-read device communication settings from device context in the beginning of every
   * synchronization, e.g. from startSynchronization() method.
   */
  void accessSettingUpdated(String name);
  
  /**
   * Should return implementation of a DiscoveryProvider is driver supports device discovery or null otherwise.
   */
  DiscoveryProvider createDiscoveryProvider();
  
  // Synchronization process
  
  /**
   * This method is called once before every synchronization. The synchronization is silently skipped if it returns null. Certain drivers may override this method to skip synchronization if device
   * communication settings were not yet filled in.
   *
   * @throws ContextException
   *           Should be thrown by the driver to force generating Info event in addition to skipping the synchronization. This Info event contains exception message that is supposed to describe the
   *           reason of synchronization skip, e.g. "device address not specified". In order to avoid event flooding, driver should throw an exception only if full synchronization is performed.
   */
  boolean shouldSynchronize(SynchronizationParameters parameters) throws ContextException;
  
  /**
   * Called in the beginning of every synchronization cycle. Driver implementations may read communication settings from device context at this point.
   *
   * @throws DeviceException
   *           If some device communications were performed and error occurred
   */
  void startSynchronization() throws DeviceException;
  
  /**
   * This method should return true if device uses normal connection model.
   */
  boolean isUsesConnections();
  
  /**
   * This method should return true driver has successfully established a link with the hardware.
   */
  boolean isConnected();
  
  /**
   * This method is called if isConnected() method returned false or DeviceContext.setReconnectionRequired(true) was called by the driver during previous synchronization. It should establish
   * connection with the hardware.
   *
   * @throws DeviceException
   *           If connection has failed
   */
  void connect() throws DeviceException;
  
  /**
   * This method is called when device account is being deleted, server is being stopped, or DeviceContext.setReconnectionRequired(true) was called by the driver during previous synchronization. The
   * method should break a link with the hardware and clean up any internal data related to the connection.
   *
   * @throws DeviceException
   *           If proper disconnection has failed
   */
  void disconnect() throws DeviceException;
  
  /**
   * Should return true if device driver has support for assets.
   */
  boolean isUsesAssets();
  
  /**
   * This method should handle reading variable values and device events buffered by remote side during a disconnection period.
   * 
   * @throws ContextException
   *           If some server-side error occurred
   * @throws DeviceException
   *           If device assets reading failed (e.g. hardware error occurred)
   * @throws DisconnectionException
   *           If device connection was interrupted during metadata reading
   */
  void readBufferedData() throws ContextException, DeviceException, DisconnectionException;
  
  /**
   * This method returns the hierarchical list of assets provided by the device.
   *
   * @throws ContextException
   *           If some server-side error occurred
   * @throws DeviceException
   *           If device assets reading failed (e.g. hardware error occurred)
   * @throws DisconnectionException
   *           If device connection was interrupted during metadata reading
   */
  List<DeviceAssetDefinition> readAssets() throws ContextException, DeviceException, DisconnectionException;
  
  /**
   * This method should return a list of potentially available device variables.
   *
   * @throws ContextException
   *           If some server-side error occurred
   * @throws DeviceException
   *           If device assets reading failed (e.g. hardware error occurred)
   * @throws DisconnectionException
   *           If device connection was interrupted during metadata reading
   */
  List<DeviceEntityDescriptor> readVariableDescriptors(List<DeviceAssetDefinition> assets) throws ContextException, DeviceException, DisconnectionException;
  
  /**
   * This method should return a list of potentially available device functions.
   *
   * @throws ContextException
   *           If some server-side error occurred
   * @throws DeviceException
   *           If device assets reading failed (e.g. hardware error occurred)
   * @throws DisconnectionException
   *           If device connection was interrupted during metadata reading
   */
  List<DeviceEntityDescriptor> readFunctionDescriptors(List<DeviceAssetDefinition> assets) throws ContextException, DeviceException, DisconnectionException;
  
  /**
   * This method should return a list of potentially available device events.
   *
   * @throws ContextException
   *           If some server-side error occurred
   * @throws DeviceException
   *           If device assets reading failed (e.g. hardware error occurred)
   * @throws DisconnectionException
   *           If device connection was interrupted during metadata reading
   */
  List<DeviceEntityDescriptor> readEventDescriptors(List<DeviceAssetDefinition> assets) throws ContextException, DeviceException, DisconnectionException;
  
  /**
   * Should return true if server should cache device-side values and pass them to {@link #writeVariableValue(VariableDefinition, CallerController, DataTable, DataTable)} along with new values.
   */
  boolean isUseDeviceSideValuesCache();
  
  /**
   * <p>
   * This method should return definitions of device settings (variables). Its implementations may read device metadata or construct the definitions based on server-side device configuration.
   *
   * <p>
   * The method is called only if {@link #isUsesAssets()} method returns false.
   *
   * @param entities
   *          Interface allowing to check what variables are active and those definitions should be returned by this method.
   *          
   * @throws ContextException
   *           If some server-side error occurred
   * @throws DeviceException
   *           If device metadata reading failed (e.g. hardware error occurred)
   * @throws DisconnectionException
   *           If device connection was interrupted during metadata reading
   */
  List<VariableDefinition> readVariableDefinitions(DeviceEntities entities) throws ContextException, DeviceException, DisconnectionException;
  
  /**
   * <p>
   * This method is similar to the {@link #readVariableDefinitions(DeviceEntities)}. If should return only definitions of variables that belong to enabled assets and their children.
   *
   * <p>
   * The method is called only if {@link #isUsesAssets()} returns true.
   *
   * @param entities
   *          Interface allowing to check what variables are active and those definitions should be returned by this method.
   *          
   * @throws ContextException
   *           If some server-side error occurred
   * @throws DeviceException
   *           If device metadata reading failed (e.g. hardware error occurred)
   * @throws DisconnectionException
   *           If device connection was interrupted during metadata reading
   */
  List<VariableDefinition> readVariableDefinitions(List<DeviceAssetDefinition> assets, DeviceEntities entities) throws ContextException, DeviceException, DisconnectionException;
  
  /**
   * <p>
   * This method should return definitions of device operations (functions). Its implementations may read device metadata or construct the definitions based on server-side device configuration.
   *
   * <p>
   * The method is called only if {@link #isUsesAssets()} method returns false.
   *
   * @param entities
   *          Interface allowing to check what functions are active and those definitions should be returned by this method.
   *          
   * @throws ContextException
   *           If some server-side error occurred
   * @throws DeviceException
   *           If device metadata reading failed (e.g. hardware error occurred)
   * @throws DisconnectionException
   *           If device connection was interrupted during metadata reading
   */
  List<FunctionDefinition> readFunctionDefinitions(DeviceEntities entities) throws ContextException, DeviceException, DisconnectionException;
  
  /**
   * <p>
   * This method is similar to the {@link #readFunctionDefinitions(DeviceEntities)}. If should return only definitions of functions that belong to enabled assets and their children.
   *
   * <p>
   * The method is called only if {@link #isUsesAssets()} returns true.
   *
   * @param entities
   *          Interface allowing to check what functions are active and those definitions should be returned by this method.
   *          
   * @throws ContextException
   *           If some server-side error occurred
   * @throws DeviceException
   *           If device metadata reading failed (e.g. hardware error occurred)
   * @throws DisconnectionException
   *           If device connection was interrupted during metadata reading
   */
  List<FunctionDefinition> readFunctionDefinitions(List<DeviceAssetDefinition> assets, DeviceEntities entities) throws ContextException, DeviceException, DisconnectionException;
  
  /**
   * <p>
   * This method should return definitions of events that may be generated by the device. Its implementations may read device metadata or construct the definitions based on server-side device
   * configuration.
   *
   * <p>
   * Note, that instances of these events may be generated by the driver asynchronously at any time after at least one device synchronization has finished. This is performed by calling
   * DeviceContext.fireEvent().
   *
   * <p>
   * The method is called only if {@link #isUsesAssets()} method returns false.
   *
   * @param entities
   *          Interface allowing to check what events are active and those definitions should be returned by this method.
   *          
   * @throws ContextException
   *           If some server-side error occurred
   * @throws DeviceException
   *           If device metadata reading failed (e.g. hardware error occurred)
   * @throws DisconnectionException
   *           If device connection was interrupted during metadata reading
   */
  List<EventDefinition> readEventDefinitions(DeviceEntities entities) throws ContextException, DeviceException, DisconnectionException;
  
  /**
   * <p>
   * This method is similar to the {@link #readEventDefinitions(DeviceEntities)}. If should return only definitions of events that belong to enabled assets and their children.
   *
   * <p>
   * The method is called only if {@link #isUsesAssets()} returns true.
   *
   * @param entities
   *          Interface allowing to check what events are active and those definitions should be returned by this method.
   *          
   * @throws ContextException
   *           If some server-side error occurred
   * @throws DeviceException
   *           If device metadata reading failed (e.g. hardware error occurred)
   * @throws DisconnectionException
   *           If device connection was interrupted during metadata reading
   */
  List<EventDefinition> readEventDefinitions(List<DeviceAssetDefinition> assets, DeviceEntities entities) throws ContextException, DeviceException, DisconnectionException;
  
  /**
   * Implementation of this method should read value of device setting pointed by the argument, convert it to the form of Data Table and return it.
   *
   * @throws ContextException
   *           If conversion of device value to the Data Table has occurred
   * @throws DeviceException
   *           If reading of device setting has failed (e.g. hardware error occurred)
   * @throws DisconnectionException
   *           If device connection was lost during operation
   */
  DataTable readVariableValue(VariableDefinition vd, CallerController caller) throws ContextException, DeviceException, DisconnectionException;
  
  /**
   * Implementation of this method should write server-side value of device setting into a hardware. This method will be called only if device setting definition is writable (e.g server-side changes
   * are allowed.
   *
   * @param deviceValue
   *          Previous value of setting received from device by calling {@link #readVariableValue(VariableDefinition, CallerController)}. Will be NULL if server was restarted since previous read
   *          operation or {@link #isUseDeviceSideValuesCache()} returns false;
   *          
   * @throws ContextException
   *           If conversion of the Data Table to a device value has occurred
   * @throws DeviceException
   *           If writing of device setting has failed (e.g. hardware error occurred)
   * @throws DisconnectionException
   *           If device connection was lost during operation
   */
  void writeVariableValue(VariableDefinition vd, CallerController caller, DataTable value, DataTable deviceValue) throws ContextException, DeviceException, DisconnectionException;
  
  /**
   * This method should implement the call of the device-side operation (function). It should decode parameters Data Table to a device native format, pass this input to the device and call requested
   * operation. Opeartion output should be encoded in the form of Data Table and returned by this method
   *
   * @throws ContextException
   *           If server-side error occurred (e.g. conversion of input/output between Data Tables and device native format)
   * @throws DeviceException
   *           If device-side error occurred
   * @throws DisconnectionException
   *           If device connection was lost during operation
   */
  DataTable executeFunction(FunctionDefinition fd, CallerController caller, DataTable parameters) throws ContextException, DeviceException, DisconnectionException;
  
  /**
   * This method should return timestamp of device setting last modification time as reported by the hardware. If modification-time-based synchronization is not supported, the method should return
   * null.
   *
   * @throws DeviceException
   *           If device-side error occurred
   * @throws DisconnectionException
   *           If device connection was lost during operation
   */
  Date getVariableModificationTime(String name) throws DeviceException, DisconnectionException;
  
  /**
   * This method should update device-side modification time of a setting pointed by name argument or do nothing if modification-time-based synchronization is not supported.
   *
   * @throws DeviceException
   *           If device-side error occurred
   * @throws DisconnectionException
   *           If device connection was lost during operation
   */
  void updateVariableModificationTime(String name, Date value) throws DeviceException, DisconnectionException;
  
  /**
   * This method allows the driver to report custom variable status to the system core. It should return null if no custom status should be used.
   */
  VariableStatus getCustomVariableStatus(String name) throws DeviceException, DisconnectionException;
  
  /**
   * This method is called in the end of every synchronization. Its implementations may perform some cleanup.
   *
   * @throws DeviceException
   *           If some device communications were performed and error occurred
   * @throws DeviceException
   *           If some device communications were performed and disconnection occurred
   */
  void finishSynchronization() throws DeviceException, DisconnectionException;
  
  // Driver info and status
  
  /**
   * This method should return string representation of device address or null if device has no address. The address will be used by device discovery module to find existing devices.
   *
   * @return Device address
   */
  String getPrimaryAddress();
  
  /**
   * Returns the list of device addresses. For example, for a network device this list can contain device host name and IPv4/IPv6 addresses of devices' network interfaces.
   *
   * @return Set of device addresses
   */
  Set<String> getAddresses();
  
  /**
   * Returns device protocol description. This method should return null, if device protocol name matches driver description.
   *
   * @return Device protocol description
   */
  String getProtocol();
  
  /**
   * Returns current status of device and/or driver or null if detailed status is not available.
   *
   * @return Textual description of device/driver status
   */
  String getStatus();
  
  /**
   * Implementations of this method should provide list of expression that will be used to show device status on dynamic maps. The expressions may include relative references to variables/functions of
   * device context.
   *
   * @return The list of device status expressions
   */
  List<Expression> getStatusExpressions(CallerController caller);
  
  /**
   * Defines whether full synchronization of the device should occur on every server restart. Returns false by default. In this case, full synchronization is only performed if full synchronization
   * period elapsed since the previous full synchronization, in other cases connect-only synchronization takes place.
   */
  boolean runFullSynchronizationOnStartup();
  
  /**
   * Tries to discover device variable that was not found during automatic metadata reading.
   *
   * @return Variable definition or null if nothing was found
   */
  VariableDefinition discoverVariable(String name, Object helper);
  
  /**
   * Tries to discover device variable that was not found during automatic metadata reading.
   *
   * @return Variable definition or null if nothing was found
   */
  FunctionDefinition discoverFunction(String name, Object helper);
  
  /**
   * Tries to discover device event that was not found during automatic metadata reading.
   *
   * @return Event definition or null if nothing was found
   */
  EventDefinition discoverEvent(String name, Object helper);
  
  /**
   * Can be used to register an event acknowledgement in a device.
   */
  void acklowledgeEvent(Event ev, Acknowledgement acknowledgement) throws ContextException, DeviceException;
  
  /**
   * This method is called when the device is removed.
   */
  void deviceDestroyed(boolean moving);
  
  /**
   * This method is called when the device is moved.
   */
  void deviceMoved(Context newParent, String newName) throws ContextException;
  
  default Context createDeviceContext(DeviceDriver driver, Context userContext, String cname)
  {
    return null;
  }
}
