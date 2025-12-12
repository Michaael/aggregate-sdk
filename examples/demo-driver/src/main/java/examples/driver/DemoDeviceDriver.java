package examples.driver;

import java.util.Collections;
import java.util.List;

import com.tibbo.aggregate.common.context.CallerController;
import com.tibbo.aggregate.common.context.ContextException;
import com.tibbo.aggregate.common.context.ContextUtils;
import com.tibbo.aggregate.common.context.EventDefinition;
import com.tibbo.aggregate.common.context.FunctionDefinition;
import com.tibbo.aggregate.common.context.VariableDefinition;
import com.tibbo.aggregate.common.datatable.DataTable;
import com.tibbo.aggregate.common.datatable.FieldFormat;
import com.tibbo.aggregate.common.datatable.SimpleDataTable;
import com.tibbo.aggregate.common.datatable.TableFormat;
import com.tibbo.aggregate.common.device.AbstractDeviceDriver;
import com.tibbo.aggregate.common.device.DeviceContext;
import com.tibbo.aggregate.common.device.DeviceEntities;
import com.tibbo.aggregate.common.device.DeviceException;
import com.tibbo.aggregate.common.device.DisconnectionException;
import com.tibbo.aggregate.common.util.TimeHelper;

/**
 * This demo device driver illustrates the usage of device-provided settings, operations and events. 
 * It doesn't communicate with any real hardware, all variables, functions and events are pre-defined.
 */
@SuppressWarnings("RedundantThrows")
public class DemoDeviceDriver extends AbstractDeviceDriver
{
  private String demoSettingValue = "Demo Setting Value";

  public DemoDeviceDriver()
  {
    super("Demo", null);
  }

  public void setupDeviceContext(DeviceContext deviceContext) throws ContextException
  {
    // Must call super here to store deviceContext reference in the AbstractDeviceDriver
    super.setupDeviceContext(deviceContext);

    // Default synchronization period for the demo device will be 30 seconds
    deviceContext.setDefaultSynchronizationPeriod(30 * TimeHelper.SECOND_IN_MS);

    // Device type is used to disable grouped operations between diverse devices
    deviceContext.setDeviceType("demo");
  }

  public List<VariableDefinition> readVariableDefinitions(DeviceEntities entities) throws ContextException, DeviceException, DisconnectionException
  {
    // Creating String field format
    FieldFormat ff = FieldFormat.create("demoSettingField", FieldFormat.STRING_FIELD);

    // Creating single-cell (scalar) setting
    TableFormat format = new TableFormat(1, 1, ff);

    // Creating variable (setting) definition. Note that variable group should not be changed.
    VariableDefinition vd = new VariableDefinition("demoSetting", format, true, true, "Demo Setting", ContextUtils.GROUP_REMOTE);

    return Collections.singletonList(vd);
  }

  public List<FunctionDefinition> readFunctionDefinitions(DeviceEntities entities) throws ContextException, DeviceException, DisconnectionException
  {
    // Creating function input format (scalar, integer)
    FieldFormat iff = FieldFormat.create("demoOperationInputField", FieldFormat.INTEGER_FIELD);
    TableFormat inputFormat = new TableFormat(1, 1, iff);

    // Creating function output format (scalar, string)
    FieldFormat off = FieldFormat.create("demoOperationOutputField", FieldFormat.STRING_FIELD);
    TableFormat outputFormat = new TableFormat(1, 1, off);

    // Creating function (operation) definition. Note that function group should not be changed.
    FunctionDefinition fd = new FunctionDefinition("demoOperation", inputFormat, outputFormat, "Demo Operation", ContextUtils.GROUP_DEFAULT);

    return Collections.singletonList(fd);
  }

  public List<EventDefinition> readEventDefinitions(DeviceEntities entities) throws ContextException, DeviceException, DisconnectionException
  {
    // Creating event data format (scalar, string)
    FieldFormat ff = FieldFormat.create("demoEventField", FieldFormat.STRING_FIELD);
    TableFormat format = new TableFormat(1, 1, ff);

    // Creating event definition
    EventDefinition ed = new EventDefinition("demoEvent", format, "Demo Event", ContextUtils.GROUP_DEFAULT);

    return Collections.singletonList(ed);
  }

  public DataTable readVariableValue(VariableDefinition vd, CallerController caller) throws ContextException, DeviceException, DisconnectionException
  {
    // React, if our demo setting is being read
    if (vd.getName().equals("demoSetting"))
    {
      // This code will fill the only cell (1st record, 1st field) of demo setting Data Table with demoSettingValue
      return new SimpleDataTable(vd.getFormat(), demoSettingValue);
    }

    // Should never happen
    throw new IllegalArgumentException("Unknown demo device setting: " + vd.getName());
  }

  public void writeVariableValue(VariableDefinition vd, CallerController caller, DataTable value, DataTable deviceValue) 
          throws ContextException, DeviceException, DisconnectionException
  {
    // React, if our demo setting is being written
    if (vd.getName().equals("demoSetting"))
    {
      // Storing demoSettingField from the 1st record of Data Table
      demoSettingValue = value.rec().getString("demoSettingField");
      return;
    }

    // Should never happen
    throw new IllegalArgumentException("Unknown demo device setting: " + vd.getName());
  }

  public DataTable executeFunction(FunctionDefinition fd, CallerController caller, DataTable parameters) throws ContextException, DeviceException, DisconnectionException
  {
    // React, if our demo operation is being executed
    if (fd.getName().equals("demoOperation"))
    {
      // Getting integer input parameter
      int inputParameter = parameters.rec().getInt("demoOperationInputField");

      // Creating and returning output
      return new SimpleDataTable(fd.getOutputFormat(), "Value of input parameter multiplied by two: " + inputParameter * 2);
    }

    throw new IllegalArgumentException("Unknown demo device operation: " + fd.getName());
  }

  public void finishSynchronization() throws DeviceException
  {
    // We can asynchronously generate device events at any time
    // To simplify the demo, we generate one demo event at the end of every synchronization
    getDeviceContext().fireEvent("demoEvent", "Demo Event Data: " + Math.random());
  }

  @SuppressWarnings("unused")   // for testing purposes
  public String getDemoSettingValue()
  {
    return demoSettingValue;
  }
}
