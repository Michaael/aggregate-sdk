package examples.driver;

import com.tibbo.aggregate.common.context.EventDefinition;
import com.tibbo.aggregate.common.context.FunctionDefinition;
import com.tibbo.aggregate.common.context.VariableDefinition;
import com.tibbo.aggregate.common.datatable.DataTable;
import com.tibbo.aggregate.common.datatable.FieldFormat;
import com.tibbo.aggregate.common.datatable.SimpleDataTable;
import com.tibbo.aggregate.common.datatable.TableFormat;
import com.tibbo.aggregate.common.examples.AbstractTestExamples;

public class DemoDeviceDriverTest extends AbstractTestExamples
{
  private static final String DEMO_SETTING_FIELD = "demoSettingField";
  private static final String DEMO_OPERATION_INPUT_FIELD = "demoOperationInputField";
  private static final String DEMO_SETTING = "demoSetting";
  private static final String DEMO_OPERATION = "demoOperation";
  private static final String DEMO_EVENT = "demoEvent";

  private DemoDeviceDriver mockDriver;

  @Override
  protected void setUp() throws Exception
  {
    super.setUp();
    mockDriver = new DemoDeviceDriver();
  }

  public void testReadVariableDefinitions() throws Exception
  {
    VariableDefinition vd = mockDriver.readVariableDefinitions(null).get(0);

    assertNotNull(vd);
    assertEquals(DEMO_SETTING, vd.getName());
  }

  public void testReadFunctionDefinition() throws Exception
  {
    FunctionDefinition fd = mockDriver.readFunctionDefinitions(null).get(0);

    assertNotNull(fd);
    assertEquals(DEMO_OPERATION, fd.getName());
  }

  public void testReadEventDefinition() throws Exception
  {
    EventDefinition ed = mockDriver.readEventDefinitions(null).get(0);

    assertNotNull(ed);
    assertEquals(DEMO_EVENT, ed.getName());
  }

  public void testReadVariableValue() throws Exception
  {
    DataTable dt = mockDriver.readVariableValue(mockDriver.readVariableDefinitions(null).get(0), null);

    assertNotNull(dt);
    assertEquals(mockDriver.getDemoSettingValue(), dt.rec().getString(DEMO_SETTING_FIELD));
  }

  public void testWriteVariableValue() throws Exception
  {
    VariableDefinition vd = mockDriver.readVariableDefinitions(null).get(0);
    DataTable dt = mockDriver.readVariableValue(vd, null);
    String newValue = "new value";
    dt.rec().setValueSmart(DEMO_SETTING_FIELD, newValue);
    mockDriver.writeVariableValue(vd, null, dt, null);
    dt = mockDriver.readVariableValue(vd, null);

    assertEquals(newValue, dt.rec().getString(DEMO_SETTING_FIELD));
  }

  public void testExecuteFunction() throws Exception
  {
    FunctionDefinition fd = mockDriver.readFunctionDefinitions(null).get(0);
    FieldFormat iff = FieldFormat.create(DEMO_OPERATION_INPUT_FIELD, FieldFormat.INTEGER_FIELD);
    TableFormat inputFormat = new TableFormat(1, 1, iff);
    DataTable params = new SimpleDataTable(inputFormat, true);
    params.rec().setValue(DEMO_OPERATION_INPUT_FIELD, 10);
    DataTable dt = mockDriver.executeFunction(fd, null, params);
    String expected = "Value of input parameter multiplied by two: 20";

    assertNotNull(dt);
    assertEquals(expected, dt.rec().getString("demoOperationOutputField"));
  }

}
