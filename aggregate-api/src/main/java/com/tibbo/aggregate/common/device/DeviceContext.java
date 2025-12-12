package com.tibbo.aggregate.common.device;

import java.util.*;
import java.util.concurrent.locks.*;

import com.tibbo.aggregate.common.context.*;
import com.tibbo.aggregate.common.datatable.*;
import com.tibbo.aggregate.common.device.sync.*;
import com.tibbo.aggregate.common.server.*;

public interface DeviceContext<C extends ServerContext> extends ServerContext<C>
{
  public static final int CONNECTION_STATUS_OFFLINE = 0;
  public static final int CONNECTION_STATUS_ONLINE = 1;
  public static final int CONNECTION_STATUS_SUSPENDED = 2;
  public static final int CONNECTION_STATUS_UNKNOWN = 3;
  public static final int CONNECTION_STATUS_MAINTENANCE = 4;
  
  public static final int SYNC_STATUS_OK = 20;
  public static final int SYNC_STATUS_WAITING = 30;
  public static final int SYNC_STATUS_ERROR = 40;
  public static final int SYNC_STATUS_UNDEFINED = 50;
  public static final int SYNC_STATUS_CONNECTING = 70;
  public static final int SYNC_STATUS_READING_METADATA = 80;
  public static final int SYNC_STATUS_SYNCHRONIZING_SETTINGS = 90;
  
  public static final int CURRENT_SYNC_STATUS_CONNECTING = 0;
  public static final int CURRENT_SYNC_STATUS_READING_METADATA = 1;
  public static final int CURRENT_SYNC_STATUS_SYNCHRONIZING_PROPERTIES = 2;
  
  public static final int SYNC_MODE_NORMAL = 0;
  public static final int SYNC_MODE_DISABLED = 1;
  public static final int SYNC_MODE_DIRECT_ACCESS = 2;
  public static final int SYNC_MODE_MASTER_VALUE = 3;
  public static final int SYNC_MODE_IGNORE_MODIFICATION_TIME = 4;
  public static final int SYNC_MODE_READ_ONLY = 5;
  public static final int SYNC_MODE_DIRECT_WRITE = 6;
  public static final int SYNC_MODE_CUSTOM = 100;
  
  public static final int DIRECTION_NONE = -1;
  public static final int DIRECTION_AUTO = 0;
  public static final int DIRECTION_DEVICE_TO_SERVER = 1;
  public static final int DIRECTION_SERVER_TO_DEVICE = 2;
  
  public static final int HISTORY_RATE_UNCHANGED = 0;
  public static final int HISTORY_RATE_NORMAL = -1;
  public static final int HISTORY_RATE_ERROR = -3;
  public static final int HISTORY_RATE_UNCHANGED_OFFLINE = -4;
  public static final int HISTORY_RATE_UNCHANGED_ERROR = -5;
  public static final int HISTORY_RATE_OFFLINE_ERROR = -6;
  public static final int HISTORY_RATE_UNCHANGED_OFFLINE_ERROR = -7;
  
  // Fetching context state
  
  /**
   * Returns caller controller with effective permissions of the user owning this device account.
   * 
   * @return Caller controller
   */
  CallerController getCallerController();
  
  /**
   * Returns device driver used by this device account.
   * 
   * @return Device driver
   */
  DeviceDriver getDriver();
  
  /**
   * This method returns string representation of device address or null if device has no address. The call is delegated to device driver.
   * 
   * @return Device address
   */
  String getAddress();
  
  /**
   * This method returns the list of device assets.
   * 
   * @return Device assets list
   */
  List<DeviceAssetDefinition> getAssets();
  
  /**
   * Returns synchronization options of a device setting variable.
   * 
   * @param variable
   *          Name of variable
   * @return Synchronization options
   */
  SettingSynchronizationOptions getSynchronizationOptions(String variable);
  
  /**
   * Returns synchronization status of a device setting variable.
   * 
   * @param variable
   *          Name of variable
   * @return Synchronization status
   */
  DeviceSettingStatus getSettingStatus(String variable);
  
  /**
   * Returns device online status. Note that online status may be reported incorrectly if device is suspended.
   * 
   * @return true if device is online, false otherwise
   */
  boolean isOnline();
  
  // Changing context state
  
  /**
   * Sets default synchronization period for the device account. This method is often called from DeviceDriver.setupDeviceContext().
   * 
   * The default period will be used only for newly created device accounts. This method will have no effect if default synchronization period has already been changed for an account.
   * 
   * @param period
   *          New default synchronization period
   */
  void setDefaultSynchronizationPeriod(long period);
  
  /**
   * Sets default status expression for the device account. This method is often called from DeviceDriver.setupDeviceContext().
   * 
   * The default status expression will be used only for newly created device accounts. This method will have no effect if default status expression has already been changed for an account.
   * 
   * @param expression
   *          New default status expression
   */
  void setDefaultStatusExpression(String expression);
  
  /**
   * Sets default color expression for the device account. This method is often called from DeviceDriver.setupDeviceContext().
   * 
   * The default color expression will be used only for newly created device accounts. This method will have no effect if default color expression has already been changed for an account.
   * 
   * @param expression
   *          New default color expression
   */
  void setDefaultColorExpression(String expression);
  
  /**
   * Sets default latitude expression for the device account. This method is often called from DeviceDriver.setupDeviceContext().
   * 
   * The default latitude expression will be used only for newly created device accounts. This method will have no effect if default latitude expression has already been changed for an account.
   * 
   * @param expression
   *          New default latitude expression
   */
  void setDefaultLatitudeExpression(String expression);
  
  /**
   * Sets default longitude expression for the device account. This method is often called from DeviceDriver.setupDeviceContext().
   * 
   * The default longitude expression will be used only for newly created device accounts. This method will have no effect if default longitude expression has already been changed for an account.
   * 
   * @param expression
   *          New default longitude expression
   */
  void setDefaultLongitudeExpression(String expression);
  
  /**
   * Sets default device setting cache mode. This method is often called from DeviceDriver.setupDeviceContext().
   * 
   * The default mode will be used only for newly created device accounts. This method will have no effect if default mode has already been changed for an account.
   * 
   * @param mode
   *          Cache mode ({@link com.tibbo.aggregate.common.device.GenericPropertiesConstants#CACHE_DATABASE} = Database,
   *          {@link com.tibbo.aggregate.common.device.GenericPropertiesConstants#CACHE_MEMORY} = Memory)
   */
  void setDefaultCacheMode(int mode);
  
  /**
   * Sets default event storage period for the device account. This method is often called from DeviceDriver.setupDeviceContext().
   * 
   * The default period will be used only for newly created device accounts. This method will have no effect if default event storage period has already been changed for an account.
   * 
   * @param defaultEventStoragePeriod
   *          New default event storage period
   */
  void setDefaultEventStoragePeriod(long defaultEventStoragePeriod);
  
  /**
   * Sets default synchronization options for a device setting variable. This call will modify system-wide list of default synchronization options that may be accessed via server global settings.
   * 
   * The default synchronization options will be used only for newly created device accounts. This method will have no effect if default setting synchronization options have already been changed for
   * an account.
   * 
   * @param variable
   *          Name of variable
   * @param options
   *          Default synchronization options
   */
  void setDefaultSynchronizationOptions(String variable, SettingSynchronizationOptions options);
  
  /**
   * Sets default synchronization options for a device setting variable. This call can modify system-wide or local (account-wide) list of default synchronization options.
   * 
   * @param variable
   *          Name of variable
   * @param local
   *          Whether to modify system-wide or local default synchronization options list
   * @param options
   *          Default synchronization options
   */
  void setDefaultSynchronizationOptions(String variable, boolean local, SettingSynchronizationOptions options);
  
  /**
   * Sets custom synchronization handler for a device setting variable.
   * 
   * @param variable
   *          Name of variable
   * @param handler
   *          Synchronization handler
   * @param forceCustomSyncMode
   *          Switch setting to a custom synchronization mode if true
   */
  void setCustomSynchronizationHandler(String variable, SynchronizationHandler handler, boolean forceCustomSyncMode);
  
  /**
   * Removes custom synchronization handler of a device setting variable.
   * 
   * If variable was in custom synchronization mode, the mode is changed to normal.
   * 
   * @param variable
   *          Name of variable
   */
  void removeCustomSynchronizationHandler(String variable);
  
  /**
   * Sets the "secondary" type of device context. Full type will be device.deviceType, where "device" is the base type.
   * 
   * This method should be called from {@link com.tibbo.aggregate.common.device.DeviceDriver#setupDeviceContext(DeviceContext)}.
   * 
   * @param deviceType
   *          Device type string
   * @throws ContextException
   *           If device type change fails since device controller of a new device type cannot be applied to current device context
   */
  void setDeviceType(String deviceType) throws ContextException;
  
  /**
   * Sets an access setting reinitializer in the device context. The reinitializer is supposed to react to a changed device access setting change. However, in most cases the system just reconnects to
   * the device and resynchronizes it after access setting change, so no custom reinitializers are necessary.
   * 
   * @param variable
   *          Name of access setting variable those changes should be tracked
   * @param reinitializer
   *          The reinitializer
   */
  void setAccessSettingReinitializer(String variable, AccessSettingReinizializer reinitializer);
  
  // Controlling context
  
  /**
   * Should be called by device driver to request reconnection to the hardware in the beginning of the next synchronization cycle.
   */
  void requestReconnection();
  
  /**
   * Should be called by the device driver to request full synchronization. The synchronization will be started right after the ongoing synchronization completes if it's currently running.
   */
  void requestSynchronization();
  
  /**
   * Same as {@link #requestSynchronization()}, but allows to temporarily enable extended connection status.
   */
  void requestSynchronization(boolean useExtendedStatus);
  
  /**
   * Same as {@link #requestSynchronization()}, but allows to specify custom synchronization options.
   */
  void requestSynchronization(SynchronizationParameters parameters);
  
  /**
   * Should be called by the driver to force re-reading of device assets during next synchronization.
   */
  void requestAssetsUpdate();
  
  /**
   * Should be called by the device driver once new setting's value was asynchronously received from the hardware. This method updates settings cache.
   */
  void asyncVariableUpdate(String variable, DataTable value) throws DisconnectionException, ContextException, DeviceException;
  
  /**
   * Should be called by the device driver once a new historical value of a device setting was received from hardware. This method stores value in the server database and updates associated
   * statistical channels.
   */
  void processHistoricalValue(String variable, Date timestamp, DataTable value) throws ContextException;
  
  /**
   * Returns synchronization lock.
   */
  ReentrantLock getSynchronizationLock();
  
  /**
   * Executes a synchronous synchronization using provided parameters.
   * 
   * Warning: the synchronization lock must be obtained before the call to this method and released later on.
   */
  SynchronizationResult executeSynchronization(SynchronizationParameters parameters);
  
  /**
   * Sets new synchronization status for the device. This method must be called by the driver in the end of synchronization only for devices those
   * {@link com.tibbo.aggregate.common.device.DeviceDriver#isUsesConnections()} method returns false. The method is normally called from
   * {@link com.tibbo.aggregate.common.device.DeviceDriver#finishSynchronization()}.
   * 
   * @param online
   *          New connection status of the device
   */
  void setOnline(boolean online);
  
  /**
   * Forces the device driver to discover a new variable and adds it to context if found.
   * 
   * @return Definition of new variable if added or null otherwise
   */
  public VariableDefinition discoverDeviceVariable(final String name, int timeout, final Object helper) throws ContextException;
  
  /**
   * Forces the device driver to discover a new function and adds it to context if found.
   * 
   * @return Definition of new function if added or null otherwise
   */
  public FunctionDefinition discoverDeviceFunction(final String name, int timeout, final Object helper) throws ContextException;
  
  /**
   * Forces the device driver to discover a new event and adds it to context if found.
   * 
   * @return Definition of new event if added or null otherwise
   */
  public EventDefinition discoverDeviceEvent(final String name, int timeout, final Object helper) throws ContextException;
  
  /**
   * Allows the server to use predefined quality codes for device settings. Otherwise the driver should set quality codes.
   */
  public void setUsePredefinedQualityCodes(boolean value);
}
