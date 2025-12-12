package examples.api;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.tibbo.aggregate.common.Log;
import com.tibbo.aggregate.common.context.Context;
import com.tibbo.aggregate.common.context.ContextException;
import com.tibbo.aggregate.common.context.ContextManager;
import com.tibbo.aggregate.common.context.ContextUtils;
import com.tibbo.aggregate.common.context.DefaultContextEventListener;
import com.tibbo.aggregate.common.context.VariableDefinition;
import com.tibbo.aggregate.common.data.Event;
import com.tibbo.aggregate.common.datatable.DataRecord;
import com.tibbo.aggregate.common.datatable.DataTable;
import com.tibbo.aggregate.common.datatable.FieldFormat;
import com.tibbo.aggregate.common.datatable.TableFormat;
import com.tibbo.aggregate.common.device.DeviceContext;
import com.tibbo.aggregate.common.event.EventLevel;
import com.tibbo.aggregate.common.protocol.RemoteServer;
import com.tibbo.aggregate.common.protocol.RemoteServerController;
import com.tibbo.aggregate.common.server.ServerContextConstants;

/**
 * This example illustrates creation and management of device accounts, modification of device settings, execution of device operations and asynchronous receiving of device events.
 */
public class ManageDevices
{
  public static void main(String[] args)
  {
    try
    {
      // Enabling logging
      Log.start();

      // Provide correct server address/port and name/password of server user to log in as
      // This user must have "admin" permission level for the "users" context and its children for the successful execution of this example
      final String username = "admin";
      final String password = "admin";
      RemoteServer rls = new RemoteServer("localhost", RemoteServer.DEFAULT_PORT, username, password);

      // Creating server controller
      RemoteServerController rlc = new RemoteServerController(rls, true);

      // Connecting to the server
      rlc.connect();

      // Authentication/authorization
      rlc.login();

      // Getting context manager
      ContextManager cm = rlc.getContextManager();

      listDeviceAccounts(username, cm);

      final String deviceName = "virtualDevice";

      final Context deviceContext = createDeviceAccount(username, deviceName, "Virtual Device", "com.tibbo.linkserver.plugin.device.virtual", cm);

      // At this point we can set up device communication settings, such as IP address, port, etc.
      // This is done by changing correspondent variables of the device context
      // The virtual device doesn't have them, so we skip this step

      awaitDeviceSynchronization(deviceContext);

      listDeviceSettings(deviceContext);

      changeDeviceSettings(deviceContext);

      listenerForDeviceEvents(deviceContext);

      executeDeviceOperation(deviceContext);

      deleteDeviceAccount(deviceContext);

      // Disconnecting from the server
      rlc.disconnect();
    }
    catch (Exception ex)
    {
      Log.TEST.error("Failed to manage devices", ex);
    }
  }

  public static void executeDeviceOperation(final Context deviceContext) throws ContextException
  {
    // Let's execute a "generateEvent" function (operation) provided by the device
    // The below call will implicitly fill function input data table
    // This call should result to event "event1" generation
    // The event should be received by the listener that was added earlier, and the listener should dump function parameters specified below
    deviceContext.callFunction("generateEvent", "event1", EventLevel.INFO, "OurTestString", 11223344);
  }

  public static void listenerForDeviceEvents(final Context deviceContext)
  {
    // Let's now start listening for device events
    // Out event listener will just dump event data
    // Virtual device provides test event called "event" that is triggered by calling "generateEvent" function

    // Creating the listener
    final DefaultContextEventListener deviceEventListener = new DefaultContextEventListener()
    {
      public void handle(Event event) {
        Log.TEST.info("Received device event with data: " + event.getData().toString());
      }
    };

    deviceContext.addEventListener("event1", deviceEventListener);
  }

  private static void changeDeviceSettings(final Context deviceContext) throws ContextException
  {
    // Changing scalar (one-cell) device setting
    deviceContext.setVariableField("int", "int", 12345, null);

    // Changing tabular device setting

    // Reading variable value
    DataTable table = deviceContext.getVariable("table");

    // Adding new record
    // This call will implicitly fill first two fields in the record
    table.addRecord("Test String", 1234);

    // Adding another record with explicitly specified fields
    DataRecord record = table.addRecord();
    record.setValue("string", "Another String");
    record.setValue("int", 5678);

    // Writing variable value
    deviceContext.setVariable("table", table);
  }

  public static void deleteDeviceAccount(final Context deviceContext) throws ContextException
  {
    // Deleting device account by calling "delete" function from the parent container context and providing name of the device to delete
    // The call will implicitly fill function input data table
    deviceContext.getParent().callFunction("delete", deviceContext.getName());
  }

  private static void listDeviceSettings(final Context deviceContext) throws ContextException
  {
    // Listing values of all device settings

    // Getting all variables in the group "remote". These are device setting variables.
    List<VariableDefinition> settings = deviceContext.getVariableDefinitions(ContextUtils.GROUP_REMOTE);

    for (VariableDefinition vd : settings)
    {
      DataTable value = deviceContext.getVariable(vd.getName());

      Log.TEST.info("Device setting '" + vd.toDetailedString() + "' has value: " + value.toString());
    }
  }

  private static void awaitDeviceSynchronization(final Context deviceContext) throws ContextException, InterruptedException
  {
    // First let's wait until the device gets synchronized with the server

    // Device status that we're waiting for
    final int statusSynchronized = DeviceContext.SYNC_STATUS_OK + DeviceContext.CONNECTION_STATUS_ONLINE;

    // Getting current status of the device
    // It's available in "status" field of "contextStatus" variable
    int currentStatus = deviceContext.getVariable(ServerContextConstants.V_CONTEXT_STATUS).rec().getInt(ServerContextConstants.VF_CONTEXT_STATUS_STATUS);

    // If the device isn't yet synchronized, we'll need to listen to its status change events until it gets synchronized
    // This is done by adding event listener for "contextStatusChanged" event
    // The listener will signal about awaited status using CountDownLatch

    // Creating the latch
    final CountDownLatch latch = new CountDownLatch(1);

    // Creating the listener
    final DefaultContextEventListener statusChangeListener = new DefaultContextEventListener()
    {
      public void handle(Event event) {
        if (event.getData().rec().getInt(ServerContextConstants.EF_CONTEXT_STATUS_CHANGED_STATUS) == statusSynchronized)
        {
          // Signal that correct status has been detected
          latch.countDown();

          // Remove the listener
          deviceContext.removeEventListener(ServerContextConstants.E_CONTEXT_STATUS_CHANGED, this);
        }
      }
    };

    // Adding the listener
    deviceContext.addEventListener(ServerContextConstants.E_CONTEXT_STATUS_CHANGED, statusChangeListener);

    // If current status is not what we are waiting for, let's wait for the sync to be completed
    if (currentStatus != statusSynchronized && !latch.await(10, TimeUnit.SECONDS))
    {
      throw new IllegalStateException("Device didn't synchronize with the server in time");
    }
  }

  public static Context createDeviceAccount(String username, String name, String description, String driverId, ContextManager cm) throws ContextException
  {
    // Creating new device account

    // Getting user's devices container context
    String devicesContextPath = ContextUtils.devicesContextPath(username);
    Context adminDevicesContext = cm.get(devicesContextPath);

    // Calling "add" function to create new Virtual Device and providing driver ID, device name, and description
    // Driver IDs may be found in Device Drivers section of the manual
    // This call will implicitly fill in function input data table
    adminDevicesContext.callFunction("add", driverId, name, description);

    // Returning context of the newly created device
    String deviceContextPath = ContextUtils.deviceContextPath(username, name);
    return cm.get(deviceContextPath);
  }

  private static void listDeviceAccounts(String username, ContextManager cm) throws ContextException
  {
    // Listing all device accounts available under current user's account

    // Getting all contexts matching to the mask
    String mask = ContextUtils.deviceContextPath(username, ContextUtils.CONTEXT_GROUP_MASK); // Will result to users.admin.devices.*
    List<Context> deviceContexts = ContextUtils.expandMaskToContexts(mask, cm);

    for (Context deviceContext : deviceContexts)
    {
      // Getting device driver ID/description
      // Driver ID is contained in the "driver" field of "status" variable
      String driverId = deviceContext.getVariable("status").rec().getString("driver");

      // Driver description is description of the selection value of the "driver" field
      // To get it, we get definition of the "status" variable first
      VariableDefinition statusVariableDefinition = deviceContext.getVariableDefinition("status");

      // Then its format
      TableFormat statusVariableFormat = statusVariableDefinition.getFormat();

      // Then format of "driver" field
      FieldFormat driverFieldFormat = statusVariableFormat.getField("driver");

      // Then driver selection values
      Map<Object, String> driverSelectionValues = driverFieldFormat.getSelectionValues();

      // And finally we get description of the driver ID
      String driverDescription = driverSelectionValues.get(driverId);

      Log.TEST.info("Found device account: " + deviceContext.toDetailedString() + " (" + driverId + "/" + driverDescription + ")");

    }
  }
}
