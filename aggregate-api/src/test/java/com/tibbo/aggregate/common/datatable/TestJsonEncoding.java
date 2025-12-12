package com.tibbo.aggregate.common.datatable;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.awt.*;
import java.util.Calendar;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.tibbo.aggregate.common.data.Data;
import com.tibbo.aggregate.common.datatable.encoding.JsonEncodingHelper;
import com.tibbo.aggregate.common.expression.EvaluationException;
import com.tibbo.aggregate.common.expression.Evaluator;
import com.tibbo.aggregate.common.expression.function.other.TableFromJSONFunction;
import com.tibbo.aggregate.common.tests.CommonsFixture;
import com.tibbo.aggregate.common.tests.CommonsTestCase;

public class TestJsonEncoding
{
  
  private static TableFormat format = new TableFormat(2, Integer.MAX_VALUE);
  
  static
  {
    format.addField(FieldFormat.create("A", FieldFormat.BOOLEAN_FIELD));
    format.addField(FieldFormat.create("A1", FieldFormat.INTEGER_FIELD));
    format.addField(FieldFormat.create("A2", FieldFormat.LONG_FIELD));
    format.addField(FieldFormat.create("A3", FieldFormat.FLOAT_FIELD));
    format.addField(FieldFormat.create("A4", FieldFormat.DOUBLE_FIELD));
    format.addField(FieldFormat.create("A5", FieldFormat.COLOR_FIELD));
    format.addField(FieldFormat.create("A6", FieldFormat.STRING_FIELD));
    format.addField(FieldFormat.create("A7", FieldFormat.DATE_FIELD));
    format.addField(FieldFormat.create("A8", FieldFormat.DATA_FIELD).setNullable(true));
  }
  
  private Evaluator ev;
  
    @BeforeEach
  public void setUpFixture()
  {
    ev = CommonsFixture.createTestEvaluator();
  }
  
  @Test
  public void testTableToJson()
  {
    Data data = new Data();
    data.setData("test data".getBytes());
    
    Calendar calendar = CommonsTestCase.getCalendar();
    calendar.set(2018, Calendar.JUNE, 6, 12, 0, 0);
    calendar.set(Calendar.MILLISECOND, 0);
    
    DataTable originalDataTable = new SimpleDataTable(format);
    originalDataTable.addRecord(false, 3, Long.MAX_VALUE, Float.MAX_VALUE, Double.MAX_VALUE, Color.GREEN, "string", calendar.getTime(), null);
    originalDataTable.addRecord(true, -3, Long.MIN_VALUE, Float.MIN_VALUE, Double.MIN_VALUE, Color.GREEN, "string", calendar.getTime(), data);
    
    String json = "[{\"A1\":3,\"A\":false,\"A2\":\"9223372036854775807\",\"A3\":3.4028235E38,\"A4\":1.7976931348623157E308,\"A5\":\"#00FF00FF\",\"A6\":\"string\",\"A7\":\"2018-06-06 09:00:00.000\",\"A8\":\"\"},{\"A1\":-3,\"A\":true,\"A2\":\"-9223372036854775808\",\"A3\":1.4E-45,\"A4\":4.9E-324,\"A5\":\"#00FF00FF\",\"A6\":\"string\",\"A7\":\"2018-06-06 09:00:00.000\",\"A8\":\"{\\\"id\\\": null, \\\"name\\\": null, \\\"preview\\\": \\\"null\\\", \\\"data\\\": \\\"dGVzdCBkYXRh\\\", \\\"shallow copy\\\": false}\"}]";
    
    assertEquals(json, JsonEncodingHelper.tableToJson(originalDataTable));
  }
  
  @Test
  public void testTableWithInnerTableToJson()
  {
    TableFormat tf = new TableFormat(2, Integer.MAX_VALUE);
    tf.addField(FieldFormat.create("A", FieldFormat.BOOLEAN_FIELD));
    tf.addField(FieldFormat.create("D", FieldFormat.DATATABLE_FIELD));
    
    DataTable inner = new SimpleDataTable(format);
    
    Calendar calendar = CommonsTestCase.getCalendar();
    calendar.set(2018, Calendar.JUNE, 6, 12, 0, 0);
    calendar.set(Calendar.MILLISECOND, 0);
    
    inner.addRecord(false, 3, Long.MAX_VALUE, Float.MAX_VALUE, Double.MAX_VALUE, Color.GREEN, "string", calendar.getTime(), null);
    
    DataTable originalDataTable = new SimpleDataTable(tf);
    originalDataTable.addRecord(true, inner);
    originalDataTable.addRecord(false);
    
    String json = "[{\"A\":true,\"D\":[{\"A1\":3,\"A\":false,\"A2\":\"9223372036854775807\"," + "\"A3\":3.4028235E38,\"A4\":1.7976931348623157E308,\"A5\":\"#00FF00FF\","
        + "\"A6\":\"string\",\"A7\":\"2018-06-06 09:00:00.000\",\"A8\":\"\"}]},{\"A\":false,\"D\":[]}]";
    
    assertEquals(json, JsonEncodingHelper.tableToJson(originalDataTable));
  }
  
  @Test
  public void testTableFromJSONFunction() throws Exception
  {
    String simpleJSON = "{\"coord\":{\"lon\":38.11,\"lat\":50.2},\"weather\":[{\"id\":801,\"main\":\"Clouds\",\"description\":\"облачно\","
        + "\"icon\":\"02d\"}],\"base\":\"cmc stations\",\"main\":{\"temp\":9.1,\"pressure\":1027.23,\"humidity\":53,\"temp_min\":9.1,\"temp_max\":9.1,"
        + "\"sea_level\":1048.6,\"grnd_level\":1027.23},\"wind\":{\"speed\":1.97,\"deg\":27.5007},\"clouds\":{\"all\":24},\"dt\":1444991331,"
        + "\"sys\":{\"message\":0.0036,\"country\":\"RU\",\"sunrise\":1444967469,\"sunset\":1445006053},\"id\":477192,\"name\":\"Valuyki\",\"cod\":200, \"bool\":true}";
    
    Object res = new TableFromJSONFunction().execute(ev, null, simpleJSON);
    DataTable dataTable = (DataTable) res;
    
    assertEquals(12, dataTable.getFieldCount());
    
    for (int i = 0; i < dataTable.getFieldCount(); i++)
    {
      assertNotNull(dataTable.rec().getValue(i));
    }
    
    assertEquals(1, (int) dataTable.rec().getDataTable("weather").getRecordCount());
    
    String nullValueJSON = "{\"null\": null}";
    res = new TableFromJSONFunction().execute(ev, null, nullValueJSON);
    dataTable = (DataTable) res;
    assertEquals(1, dataTable.getFieldCount());
    assertNull(dataTable.rec().getValue(0));
    
    simpleJSON = "{        \"cis\" : [{        \"ucmdbId\" : \"43ad626bf9d2af05b72a67277bcfe3b1\",        \"type\" : \"nt\",        \"properties\" :    {       \"name\" : \"win2008r2ee\",       \"root_class\" : \"nt\"     }      }, {        \"ucmdbId\" : \"46124847af5f20269ea8aa949aade1ca\",        \"type\" : \"unix\",        \"properties\" :    {       \"host_isvirtual\" : true,       \"host_osinstalltype\" : \"Red Hat Enterprise Linux Server\",       \"discovered_model\" : \"VMware Virtual Platform\",       \"discovered_os_name\" : \"Red Hat(Linux)\",       \"primary_dns_name\" : \"host-linux-oa.factor.edu\",       \"name\" : \"host-linux-oa\",       \"discovered_os_version\" : \"7.1\",       \"root_class\" : \"unix\",       \"os_architecture\" : \"64-bit\"     }      }, {        \"ucmdbId\" : \"4dbc2336d95eb48f9a6075a0a514d32c\",        \"type\" : \"nt\",        \"properties\" :    {       \"host_isvirtual\" : true,       \"host_osinstalltype\" : \"Server Enterprise\",       \"discovered_model\" : \"VMware Virtual Platform\",       \"discovered_os_name\" : \"Windows 2008 R2\",       \"primary_dns_name\" : \"soib-agt-w2008.factor.edu\",       \"name\" : \"soib-agt-w2008\",       \"discovered_os_version\" : \"6.1.7601\",       \"root_class\" : \"nt\",       \"os_architecture\" : \"64-bit\"     }      }, {        \"ucmdbId\" : \"4f682706c7ebff93b4e00bc2bb621b14\",        \"type\" : \"nt\",        \"properties\" :    {       \"name\" : \"ada\",       \"root_class\" : \"nt\"     }      } ],      \"relations\" : null    }";
    res = new TableFromJSONFunction().execute(ev, null, simpleJSON);
    dataTable = (DataTable) res;
    
    assertEquals(2, dataTable.getFieldCount());
    
    DataTable cis = dataTable.rec().getDataTable("cis");
    for (int i = 0; i < cis.getRecordCount(); i++)
    {
      
      assertNotNull(cis.getRecord(i));
      assertEquals(1, cis.getRecord(i).getFieldCount());
      DataTable val = cis.getRecord(i).getDataTable("value");
      assertEquals(3, val.rec().getFieldCount());
      for (int j = 0; j < val.rec().getFieldCount(); j++)
        assertNotNull(val.rec().getValue(j));
    }
    
    String jsonArrays = "{ \"idHardware\":{ \"node\": \"kas-azk18\", \"id\": \"kas-azk18_7408081\", \"internalId\": 7408081 }, \"hardware\": \"tank\", \"number\": 4, \"comment\": null, \"gas\": null, \"gasName\": null, \"idChildrenHardware\": [] }";
    
    res = new TableFromJSONFunction().execute(ev, null, jsonArrays);
    dataTable = (DataTable) res;
    
    assertEquals(7, dataTable.getFieldCount());
    
    DataTable hardware = dataTable.rec().getDataTable("idChildrenHardware");
    assertEquals(0, (int) hardware.getRecordCount());
    
    String jsonOuterArray = "[1,2,3]";
    
    res = new TableFromJSONFunction().execute(ev, null, jsonOuterArray);
    dataTable = (DataTable) res;
    
    assertEquals(3, (int) dataTable.getRecordCount());
    
    // Testing JSON arrays where the first element is null
    String jsonArrayWithTwoAtomicElements = "{ \"cis\": [ null, true ] }";
    
    res = new TableFromJSONFunction().execute(ev, null, jsonArrayWithTwoAtomicElements);
    dataTable = (DataTable) res;
    
    assertEquals(2, (int) dataTable.rec().getDataTable("cis").getRecordCount());
    assertEquals(FieldFormat.BOOLEAN_FIELD, dataTable.rec().getDataTable("cis").getFormat(0).getType());
    assertEquals(Boolean.TRUE, dataTable.rec().getDataTable("cis").getRecord(1).getBoolean(0));
    
    String jsonArrayWithTwoJSONObjects = "{ \"cis\": [ { \"prop\": null }, { \"prop\": null } ] }";
    
    assertEquals(2, (int) dataTable.rec().getDataTable("cis").getRecordCount());
    res = new TableFromJSONFunction().execute(ev, null, jsonArrayWithTwoJSONObjects);
    dataTable = (DataTable) res;
    
    assertEquals(2, (int) dataTable.rec().getDataTable("cis").getRecordCount());
    assertEquals(FieldFormat.STRING_FIELD, dataTable.rec().getDataTable("cis").rec().getDataTable(0).getFormat("prop").getType());
    assertEquals(null, dataTable.rec().getDataTable("cis").getRecord(1).getDataTable(0).rec().getBoolean("prop"));
    
    String jsonArrayWithTwoJSONObjects2 = "{ \"cis\": [ { \"prop\": null }, { \"prop\": true } ] }";
    
    assertEquals(2, (int) dataTable.rec().getDataTable("cis").getRecordCount());
    res = new TableFromJSONFunction().execute(ev, null, jsonArrayWithTwoJSONObjects2);
    dataTable = (DataTable) res;
    
    assertEquals(2, (int) dataTable.rec().getDataTable("cis").getRecordCount());
    assertEquals(FieldFormat.BOOLEAN_FIELD, dataTable.rec().getDataTable("cis").rec().getDataTable(0).getFormat("prop").getType());
    assertEquals(Boolean.TRUE, dataTable.rec().getDataTable("cis").getRecord(1).getDataTable(0).rec().getBoolean("prop"));
    
    String jsonArrayWithTwoJSONObjects3 = "{ \"cis\": [ { \"properties\": null }, { \"properties\": { \"host_isvirtual\": true } } ] }";
    
    res = new TableFromJSONFunction().execute(ev, null, jsonArrayWithTwoJSONObjects3);
    dataTable = (DataTable) res;
    
    assertEquals(2, (int) dataTable.rec().getDataTable("cis").getRecordCount());
    
    String jsonArrayWithTwoJSONObjects4 = "{ \"cis\": [ { \"properties\": { \"host_isvirtual\": true }}, { \"properties\": { \"host_isvirtual\": false} } ] }";
    
    res = new TableFromJSONFunction().execute(ev, null, jsonArrayWithTwoJSONObjects4);
    dataTable = (DataTable) res;
    
    assertEquals(2, (int) dataTable.rec().getDataTable("cis").getRecordCount());
    
    // Testing JSON arrays with convertUnequalFieldTypesToString parameter
    String jsonArrayWithUnequalFieldTypes5 = "[{\"num\":48, \"test\":5}, {\"num\":654, \"test\":\"ff\"}]";
    
    res = new TableFromJSONFunction().execute(ev, null, jsonArrayWithUnequalFieldTypes5, true);
    dataTable = (DataTable) res;
    assertEquals(FieldFormat.LONG_FIELD, dataTable.rec().getDataTable(0).getFormat().getField("num").getType());
    assertEquals(FieldFormat.STRING_FIELD, dataTable.rec().getDataTable(0).getFormat().getField("test").getType());
    
    try
    {
      new TableFromJSONFunction().execute(ev, null, jsonArrayWithUnequalFieldTypes5, false);
      fail("An EvaluationException must be thrown");
    }
    catch (EvaluationException e)
    {
      assertTrue(!e.getMessage().isEmpty());
    }
    
    res = new TableFromJSONFunction().execute(ev, null, "{\"values\": [1, 2.2, 4.4, 5.5]}", true);
    dataTable = (DataTable) res;
    assertEquals(FieldFormat.STRING_FIELD, dataTable.rec().getDataTable(0).getFormat().getField("value").getType());
    
    res = new TableFromJSONFunction().execute(ev, null, "{\"values\": [1, 2.2, 4.4, 5.5]}", false);
    dataTable = (DataTable) res;
    assertEquals(FieldFormat.LONG_FIELD, dataTable.rec().getDataTable(0).getFormat().getField("value").getType());
  }
  
  @Test
  public void testTableFromJSONFunctionWithJSONArray() throws Exception
  {
    JSONObject objectOne = new JSONObject();
    objectOne.put("str", "STR");
    objectOne.put("two", "TWO");
    JSONObject objectTwo = new JSONObject();
    objectTwo.put("str2", "STR2");
    objectTwo.put("two", "TWO2");
    
    JSONObject objectThree = new JSONObject();
    objectThree.put("str3", "STR3");
    JSONArray jsonArray = new JSONArray();
    jsonArray.add(objectThree);
    
    JSONArray array = new JSONArray();
    array.add(objectOne);
    array.add(objectTwo);
    array.add(jsonArray);
    
    DataTable result = (DataTable) new TableFromJSONFunction().execute(ev, null, array);
    
    assertEquals(3, (int) result.getRecordCount());
  }
  
}
