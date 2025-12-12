package com.tibbo.aggregate.common.expression.function;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import com.tibbo.aggregate.common.datatable.DataRecord;
import com.tibbo.aggregate.common.datatable.DataTable;
import com.tibbo.aggregate.common.datatable.DataTableReplication;
import com.tibbo.aggregate.common.datatable.FieldFormat;
import com.tibbo.aggregate.common.datatable.SimpleDataTable;
import com.tibbo.aggregate.common.datatable.TableFormat;
import com.tibbo.aggregate.common.datatable.encoding.ClassicEncodingSettings;
import com.tibbo.aggregate.common.expression.DefaultReferenceResolver;
import com.tibbo.aggregate.common.expression.EvaluationEnvironment;
import com.tibbo.aggregate.common.expression.Evaluator;
import com.tibbo.aggregate.common.expression.Reference;
import com.tibbo.aggregate.common.expression.function.table.AddColumnsFunction;
import com.tibbo.aggregate.common.expression.function.table.AddRecordsFunction;
import com.tibbo.aggregate.common.expression.function.table.AdjustRecordLimitsFunction;
import com.tibbo.aggregate.common.expression.function.table.AggregateFunction;
import com.tibbo.aggregate.common.expression.function.table.CellFunction;
import com.tibbo.aggregate.common.expression.function.table.ClearFunction;
import com.tibbo.aggregate.common.expression.function.table.ConvertFunction;
import com.tibbo.aggregate.common.expression.function.table.CopyFunction;
import com.tibbo.aggregate.common.expression.function.table.DecodeFunction;
import com.tibbo.aggregate.common.expression.function.table.DescribeFunction;
import com.tibbo.aggregate.common.expression.function.table.DescriptionFunction;
import com.tibbo.aggregate.common.expression.function.table.DistinctFunction;
import com.tibbo.aggregate.common.expression.function.table.EncodeFunction;
import com.tibbo.aggregate.common.expression.function.table.FilterFunction;
import com.tibbo.aggregate.common.expression.function.table.GetFormatFunction;
import com.tibbo.aggregate.common.expression.function.table.GetQualityFunction;
import com.tibbo.aggregate.common.expression.function.table.GetTimestampFunction;
import com.tibbo.aggregate.common.expression.function.table.HasFieldFunction;
import com.tibbo.aggregate.common.expression.function.table.IntersectFunction;
import com.tibbo.aggregate.common.expression.function.table.JoinFunction;
import com.tibbo.aggregate.common.expression.function.table.PrintFunction;
import com.tibbo.aggregate.common.expression.function.table.RecordsFunction;
import com.tibbo.aggregate.common.expression.function.table.RemoveColumnsFunction;
import com.tibbo.aggregate.common.expression.function.table.RemoveRecordsFunction;
import com.tibbo.aggregate.common.expression.function.table.SelectFunction;
import com.tibbo.aggregate.common.expression.function.table.SetFunction;
import com.tibbo.aggregate.common.expression.function.table.SetMultipleFunction;
import com.tibbo.aggregate.common.expression.function.table.SetNestedFieldFunction;
import com.tibbo.aggregate.common.expression.function.table.SetQualityFunction;
import com.tibbo.aggregate.common.expression.function.table.SetTimestampFunction;
import com.tibbo.aggregate.common.expression.function.table.SortFunction;
import com.tibbo.aggregate.common.expression.function.table.SubtableFunction;
import com.tibbo.aggregate.common.expression.function.table.TableFunction;
import com.tibbo.aggregate.common.expression.function.table.UnionFunction;
import com.tibbo.aggregate.common.tests.CommonsFixture;
import com.tibbo.aggregate.common.tests.CommonsTestCase;

public class TestDataTableFunctions extends CommonsTestCase
{
  
  private Evaluator ev;
  
  @Override
  public void setUp() throws Exception
  {
    super.setUp();
    ev = CommonsFixture.createTestEvaluator();
  }
  
  public void testAddRecordsFunction() throws Exception
  {
    TableFormat format = new TableFormat(2, 5);
    format.addField("<f1><S><A=strval>");
    format.addField("<f2><I><A=456>");
    DataTable table = new SimpleDataTable(format, true);
    
    DataTable res = (DataTable) new AddRecordsFunction().execute(ev, null, table, "abc", 123, "def", 456);
    
    assertEquals(4, (int) res.getRecordCount());
    assertEquals("def", res.getRecord(3).getString("f1"));
  }
  
  public void testAddColumnsFunction() throws Exception
  {
    DataTable defaultTable = ev.getDefaultResolver().getDefaultTable();
    String newField = "<usage><S>";
    String exp = "{}";
    
    assertEquals(3, ev.getDefaultResolver().getDefaultTable().getFieldCount());
    
    DataTable result = (DataTable) new AddColumnsFunction().execute(ev, new EvaluationEnvironment(), defaultTable, newField, exp);
    
    assertEquals(4, result.getFieldCount());
  }
  
  public void testAdjustRecordLimitsFunction() throws Exception
  {
    DataTable defaultTable = ev.getDefaultResolver().getDefaultTable();
    
    assertEquals(5, defaultTable.getFormat().getMinRecords());
    
    DataTable result = (DataTable) new AdjustRecordLimitsFunction().execute(null, null, defaultTable, 1, 1);
    
    assertEquals(1, result.getFormat().getMinRecords());
  }
  
  public void testCellFunction() throws Exception
  {
    DataTable defaultTable = ev.getDefaultResolver().getDefaultTable();
    String expected = "test";
    
    String result = (String) new CellFunction().execute(null, null, defaultTable, "str", 0);
    
    assertEquals(expected, result);
  }
  
  public void testCellFunctionWithAdditionalParams() throws Exception
  {
    DataTable defaultTable = ev.getDefaultResolver().getDefaultTable();
    String expected = "test";
    
    String result = (String) new CellFunction().execute(null, null, defaultTable, 0, 0);
    
    assertEquals(expected, result);
  }
  
  public void testClearFunction() throws Exception
  {
    DataTable defaultTable = ev.getDefaultResolver().getDefaultTable();
    
    assertEquals(7, (int) defaultTable.getRecordCount());
    
    DataTable result = (DataTable) new ClearFunction().execute(null, null, defaultTable);
    
    assertEquals(5, (int) result.getRecordCount());
  }
  
  public void testCopyFunction() throws Exception
  {
    DataTable sourceTable = ev.getDefaultResolver().getDefaultTable();
    
    TableFormat targetFormat = new TableFormat(5, 10);
    DataTable targetTable = new SimpleDataTable(targetFormat, false);
    
    assertEquals(0, (int) targetTable.getRecordCount());
    
    new CopyFunction().execute(null, null, sourceTable, targetTable);
    
    assertEquals(7, (int) targetTable.getRecordCount());
  }
  
  public void testDescribeFunction() throws Exception
  {
    DataTable defaultTable = ev.getDefaultResolver().getDefaultTable();
    String defaultDescription = defaultTable.getFormat().getField(0).getDescription();
    
    assertEquals("Str", defaultDescription);
    
    DataTable result = (DataTable) new DescribeFunction().execute(ev, null, defaultTable, "str", "new desc");
    String resultDescription = result.getFormat().getField(0).getDescription();
    
    assertEquals("new desc", resultDescription);
  }
  
  public void testDescriptionFunction() throws Exception
  {
    DataTable defaultTable = ev.getDefaultResolver().getDefaultTable();
    
    String result = (String) new DescriptionFunction().execute(ev, null, defaultTable);
    
    assertEquals(defaultTable.getDescription(), result);
  }
  
  public void testGetFormatFunction() throws Exception
  {
    DataTable defaultTable = ev.getDefaultResolver().getDefaultTable();
    String expected = defaultTable.getFormat().encode(false);
    
    String result = (String) new GetFormatFunction().execute(ev, null, defaultTable);
    
    assertEquals(expected, result);
  }
  
  public void testGetFormatFunctionWithNullParam() throws Exception
  {
    Object nullable = new GetFormatFunction().execute(null, null, null, null);
    
    assertNull(nullable);
  }
  
  public void testHasFieldFunction() throws Exception
  {
    DataTable defaultTable = ev.getDefaultResolver().getDefaultTable();
    
    boolean isHasField = (boolean) new HasFieldFunction().execute(ev, null, defaultTable, "str");
    
    assertEquals(true, isHasField);
  }
  
  public void testPrintFunction() throws Exception
  {
    DataTable defaultTable = ev.getDefaultResolver().getDefaultTable();
    String expected = "test, second, third, fourth, fifth, sixth, seventh";
    
    String result = (String) new PrintFunction().execute(ev, null, defaultTable, "{str}", ", ");
    
    assertEquals(expected, result);
  }
  
  public void testRemoveRecordsFunction() throws Exception
  {
    DataTable defaultTable = ev.getDefaultResolver().getDefaultTable();
    
    assertEquals(7, (int) defaultTable.getRecordCount());
    
    DataTable result = (DataTable) new RemoveRecordsFunction().execute(ev, null, defaultTable, "str", "test");
    
    assertEquals(6, (int) result.getRecordCount());
  }
  
  public void testSelectFunction() throws Exception
  {
    DataTable defaultTable = ev.getDefaultResolver().getDefaultTable();
    
    String result = (String) new SelectFunction().execute(ev, null, defaultTable, "str", "str", "test");
    
    assertEquals("test", result);
  }
  
  public void testSetFunction() throws Exception
  {
    DataTable defaultTable = ev.getDefaultResolver().getDefaultTable();
    
    assertEquals("test", defaultTable.getRecord(0).getValue(0));
    
    DataTable result = (DataTable) new SetFunction().execute(ev, null, defaultTable, "str", 0, "new value");
    
    assertEquals("new value", result.getRecord(0).getValue(0));
  }
  
  public void testSubtableFunction() throws Exception
  {
    DataTable defaultTable = ev.getDefaultResolver().getDefaultTable();
    
    assertEquals(3, defaultTable.getFieldCount());
    
    DataTable result = (DataTable) new SubtableFunction().execute(ev, null, defaultTable, "str");
    
    assertEquals(1, result.getFieldCount());

    TableFormat tf = new TableFormat(3, 100);
    tf.addField("<int><I>");
    tf.addField("<str><S>");
    tf.addField("<bool><B>");

    DataTable table = new SimpleDataTable(tf);

    table.addRecord(1, "a", true);
    table.addRecord(2, "b", false);
    table.addRecord(3, "c", true);
    table.addRecord(4, "d", false);
    table.addRecord(5, "e", true);

    // Old syntax

    result = (DataTable) new SubtableFunction().execute(ev, null, table, "int", "str");

    assertEquals(2, result.getFieldCount());
    assertEquals(5, (int)result.getRecordCount());

    // The function will never try to interpret 2nd and 3rd parameters as numbers to use new syntax unless these are actually number-type values
    result = (DataTable) new SubtableFunction().execute(ev, null, table, "1", "2", "3"); // Non-existing field names

    assertEquals(0, result.getFieldCount());
    assertEquals(5, (int)result.getRecordCount());

    // New syntax

    result = (DataTable) new SubtableFunction().execute(ev, null, table, 2, 2, "int", "str");

    assertEquals(2, result.getFieldCount());
    assertEquals(2, (int)result.getRecordCount());
    assertEquals(3, (int)result.getRecord(0).getInt("int"));
    assertEquals("d", result.getRecord(1).getString("str"));

    result = (DataTable) new SubtableFunction().execute(ev, null, table, null, 2, "int", "str");

    assertEquals(2, result.getFieldCount());
    assertEquals(2, (int)result.getRecordCount());
    assertEquals(1, (int)result.getRecord(0).getInt("int"));
    assertEquals("b", result.getRecord(1).getString("str"));

    result = (DataTable) new SubtableFunction().execute(ev, null, table, 1, null, "int", "str");

    assertEquals(2, result.getFieldCount());
    assertEquals(4, (int)result.getRecordCount());
    assertEquals(2, (int)result.getRecord(0).getInt("int"));
    assertEquals("e", result.getRecord(3).getString("str"));
  }
  
  public void testConvertFunction() throws Exception
  {
    TableFormat format = new TableFormat(2, 5);
    format.addField("<f1><S><A=strval>");
    format.addField("<f2><I><A=456>");
    DataTable table = new SimpleDataTable(format, true);
    
    DataTable res = (DataTable) new ConvertFunction().execute(ev, null, table, "<<f2><S><A=123>> <<f3><B>>", true);
    assertTrue(res.getFormat().hasField("f3"));
    assertTrue(res.getRecordCount() == 2);
    assertEquals("456", res.rec().getString("f2"));
  }
  
  public void testDistinctFunction() throws Exception
  {
    TableFormat format = new TableFormat();
    format.addField("<f1><S><A=strval>");
    format.addField("<f2><I><A=456>");
    format.setMinRecords(4);
    DataTable table = new SimpleDataTable(format);
    
    table.addRecord("1st field", 1);
    table.addRecord("2nd field", 2);
    table.addRecord("1st field", 1);
    table.addRecord("1st field", 4);
    
    DataTable res = (DataTable) new DistinctFunction().execute(ev, null, table);
    
    assertEquals(3, res.getRecordCount().intValue());
    assertEquals("1st field", res.getRecord(0).getValue(0));
    assertEquals("2nd field", res.getRecord(1).getValue(0));
    assertEquals("1st field", res.getRecord(2).getValue(0));
    assertEquals(4, res.getRecord(2).getValue(1));
    assertEquals(TableFormat.DEFAULT_MIN_RECORDS, res.getFormat().getMinRecords());
    assertEquals(TableFormat.DEFAULT_MAX_RECORDS, res.getFormat().getMaxRecords());
  }
  
  public void testEncodeAndDecodeFunctions() throws Exception
  {
    TableFormat tf = new TableFormat(2, Integer.MAX_VALUE);
    tf.addField(FieldFormat.create("A", FieldFormat.BOOLEAN_FIELD));
    tf.addField(FieldFormat.create("B", FieldFormat.INTEGER_FIELD));
    tf.addField(FieldFormat.create("C", FieldFormat.STRING_FIELD));
    tf.addField(FieldFormat.create("D", FieldFormat.STRING_FIELD));
    
    DataTable originalDataTable = new SimpleDataTable(tf);
    originalDataTable.addRecord(true, 1, "1", "33");
    originalDataTable.addRecord(true, 2, "2", "44");
    
    String encodedDataTableVisSep = (String) new EncodeFunction().execute(ev, null, originalDataTable, true);
    DataTable decodedDataTableVisSep = (DataTable) new DecodeFunction().execute(ev, null, encodedDataTableVisSep);
    
    String encodedDataTableInvisSep = (String) new EncodeFunction().execute(ev, null, originalDataTable, false);
    DataTable decodedDataTableInvisSep = (DataTable) new DecodeFunction().execute(ev, null, encodedDataTableInvisSep);
    
    assertEquals(originalDataTable, decodedDataTableVisSep);
    assertEquals(originalDataTable, decodedDataTableInvisSep);
  }
  
  public void testFilter() throws Exception
  {
    ev.getDefaultResolver().setDefaultTable(null);
    
    DataTable res = (DataTable) new FilterFunction().execute(ev, null, CommonsFixture.createTestTable(), "{int} > 100");
    
    assertEquals(4, (int) res.getRecordCount());
    assertEquals(3, res.getFieldCount());
    assertEquals(444, res.getRecord(2).getValue("int"));
  }
  
  public void testIntersectFunction() throws Exception
  {
    ev.getDefaultResolver().setDefaultTable(null);
    DataTable sourceTable = CommonsFixture.createTestTable();
    DataTable sampleTable = CommonsFixture.createTestTable();
    
    DataRecord removableRec = new DataRecord(sampleTable.getFormat());
    DataTableReplication.copyRecord(sampleTable.getRecord(1), removableRec);
    sampleTable.removeRecord(1);
    sampleTable.removeRecord(2);
    
    DataTable res = (DataTable) new IntersectFunction().execute(ev, null, sourceTable, "str", sampleTable, "str");
    assertNull(res.findIndex(removableRec));
    assertEquals(5, res.getRecordCount().intValue());
    assertEquals(7, sourceTable.getRecordCount().intValue());
    
    sourceTable.addRecord(removableRec);
    res = (DataTable) new IntersectFunction().execute(ev, null, sourceTable, "str", sampleTable, "str", true);
    assertEquals(0, res.findIndex(removableRec).intValue());
    assertEquals(3, res.getRecordCount().intValue());
    
    TableFormat fixedSizeFormat = sourceTable.getFormat().clone();
    fixedSizeFormat.setMinRecords(sourceTable.getRecordCount());
    fixedSizeFormat.setMaxRecords(sourceTable.getRecordCount());
    sourceTable.setFormat(fixedSizeFormat);
    
    res = (DataTable) new IntersectFunction().execute(ev, null, sourceTable, "str", sampleTable, "str");
    assertEquals(TableFormat.DEFAULT_MIN_RECORDS, res.getFormat().getMinRecords());
    assertEquals(TableFormat.DEFAULT_MAX_RECORDS, res.getFormat().getMaxRecords());
  }
  
  public void testRecords() throws Exception
  {
    Integer res = (Integer) new RecordsFunction().execute(CommonsFixture.createTestEvaluator(), null, (Object) CommonsFixture.createTestTable());
    
    assertEquals(7, res.intValue());
  }
  
  public void testRemoveColumnsFunction() throws Exception
  {
    TableFormat format = new TableFormat(2, 5);
    format.addField("<f1><S><A=strval>");
    format.addField("<f2><I><A=456>");
    
    DataTable res = (DataTable) new RemoveColumnsFunction().execute(ev, null, new SimpleDataTable(format, true), "f1");
    
    assertEquals(2, (int) res.getRecordCount());
    assertEquals(1, res.getFieldCount());
    assertEquals(456, res.getRecord(1).getValue(0));
  }
  
  public void testSortFunction() throws Exception
  {
    DataTable res = (DataTable) new SortFunction().execute(ev, null, CommonsFixture.createTestTable(), "int", true);
    
    assertEquals(-111, res.getRecord(0).getValue("int"));
    assertEquals("fifth", res.getRecord(res.getRecordCount() - 1).getValue("str"));
    
    DataTable table = new SimpleDataTable(new TableFormat("<<value><I><F=N>>", new ClassicEncodingSettings(true)));
    table.addRecord(9);
    table.addRecord(5);
    table.addRecord(8);
    table.addRecord((Object) null);
    table.addRecord(4);
    table.addRecord(9);
    table.addRecord(3);
    
    res = (DataTable) new SortFunction().execute(ev, null, table, "value", false);
    
    assertEquals(9, res.getRecord(0).getValue("value"));
    assertEquals(9, res.getRecord(1).getValue("value"));
    assertEquals(8, res.getRecord(2).getValue("value"));
    assertEquals(5, res.getRecord(3).getValue("value"));
    assertEquals(4, res.getRecord(4).getValue("value"));
    assertEquals(3, res.getRecord(5).getValue("value"));
    assertEquals(null, res.getRecord(6).getValue("value"));
    
  }
  
  public void testTableFunction() throws Exception
  {
    DataTable res = (DataTable) new TableFunction().execute(ev, null, "<<str><S>><<int><I>>", "s11", 11, "s22", 22);
    
    assertEquals(2, (int) res.getRecordCount());
    assertEquals(2, res.getFieldCount());
    assertEquals("s11", res.getRecord(0).getValue("str"));
    assertEquals(22, res.getRecord(1).getValue("int"));
  }
  
  public void testTableFunctionWithEmptyParam() throws Exception
  {
    DataTable result = (DataTable) new TableFunction().execute(null, null);
    
    assertEquals(new SimpleDataTable(), result);
  }
  
  public void testAggregateFunction() throws Exception
  {
    TableFormat format = new TableFormat();
    format.addField("<num><I>");
    DataTable table = new SimpleDataTable(format);
    table.addRecord(6);
    table.addRecord(8);
    table.addRecord(3);
    table.addRecord(7);
    
    String exp = "({env/previous} * {#" + DefaultReferenceResolver.ROW + "} + {num}) / ({#" + DefaultReferenceResolver.ROW + "} + 1)";
    
    Object res = new AggregateFunction().execute(ev, null, table, exp, 0f);
    
    assertEquals(6.0d, res);
  }

  public void testAggregateFunctionForTable() throws Exception
  {
    TableFormat format = new TableFormat();
    format.addField("<num><I>");
    DataTable table = new SimpleDataTable(format);
    table.addRecord(1);
    table.addRecord(2);
    table.addRecord(3);
    table.addRecord(4);
    table.addRecord(5);
    table.addRecord(6);
    table.addRecord(7);
    table.addRecord(8);
    table.addRecord(9);

    String exp = "({env/previous} + {num})";

    EvaluationEnvironment ee = new EvaluationEnvironment();
    ee.setCause(new Reference("records$[3]"));

    Object res = new AggregateFunction().execute(ev, ee, table, exp, 0f);

    assertEquals(45.0d, res);
  }
  
  public void testSetMultipleFunction() throws Exception
  {
    ev.getEnvironmentResolver().set("test", true);
    
    Map<String, Object> env = new LinkedHashMap<>();
    env.put("test2", true);
    EvaluationEnvironment ee = new EvaluationEnvironment(env);
    
    TableFormat format = new TableFormat();
    format.addField("<num><I>");
    DataTable table = new SimpleDataTable(format);
    table.addRecord(6);
    
    String condition = "{env/test} == true";
    
    DataTable res = (DataTable) new SetMultipleFunction().execute(ev, ee, table, "num", 111, condition);
    assertEquals(111, res.get());
  }
  
  public void testSetMultipleFunctionWhenSettingDataTableField() throws Exception
  {
    TableFormat format = new TableFormat();
    format.addField("<num><I>");
    format.addField("<table><T>");
    DataTable table = new SimpleDataTable(format, 1);
    
    TableFormat tf = new TableFormat("<<1><S>>", new ClassicEncodingSettings(true));
    DataTable dt = new SimpleDataTable(tf);
    dt.addRecord().setValue(0, 1);
    
    DataTable expected = new SimpleDataTable(format);
    expected.addRecord().setValue(1, dt);
    
    DataTable res = (DataTable) new SetMultipleFunction().execute(ev, null, table, "table", dt);
    assertEquals(expected, res);
  }
  
  public void testUnionFunction() throws Exception
  {
    TableFormat leftFormat = new TableFormat();
    leftFormat.addField(FieldFormat.create("A", FieldFormat.BOOLEAN_FIELD));
    leftFormat.addField(FieldFormat.create("B", FieldFormat.INTEGER_FIELD));
    leftFormat.addField(FieldFormat.create("C", FieldFormat.STRING_FIELD));
    DataTable leftTable = new SimpleDataTable(leftFormat);
    leftTable.addRecord(true, 1, "1");
    leftTable.addRecord(true, 2, "2");
    
    TableFormat rightFormat = new TableFormat(2, 2);
    rightFormat.addField(FieldFormat.create("B", FieldFormat.INTEGER_FIELD));
    rightFormat.addField(FieldFormat.create("C", FieldFormat.STRING_FIELD));
    rightFormat.addField(FieldFormat.create("D", FieldFormat.STRING_FIELD));
    DataTable rightTable = new SimpleDataTable(rightFormat);
    rightTable.addRecord(3, "3", "33");
    rightTable.addRecord(4, "4", "44");
    
    TableFormat leftResultFormat = new TableFormat(2, Integer.MAX_VALUE);
    leftResultFormat.addField(FieldFormat.create("A", FieldFormat.BOOLEAN_FIELD));
    leftResultFormat.addField(FieldFormat.create("B", FieldFormat.INTEGER_FIELD));
    leftResultFormat.addField(FieldFormat.create("C", FieldFormat.STRING_FIELD));
    leftResultFormat.addField(FieldFormat.create("D", FieldFormat.STRING_FIELD));
    
    DataTable leftResultDataTable = new SimpleDataTable(leftResultFormat);
    leftResultDataTable.addRecord(true, 1, "1");
    leftResultDataTable.addRecord(true, 2, "2");
    leftResultDataTable.addRecord(false, 3, "3", "33");
    leftResultDataTable.addRecord(false, 4, "4", "44");
    
    TableFormat rightResultFormat = new TableFormat(2, Integer.MAX_VALUE);
    rightResultFormat.addField(FieldFormat.create("B", FieldFormat.INTEGER_FIELD));
    rightResultFormat.addField(FieldFormat.create("C", FieldFormat.STRING_FIELD));
    rightResultFormat.addField(FieldFormat.create("D", FieldFormat.STRING_FIELD));
    rightResultFormat.addField(FieldFormat.create("A", FieldFormat.BOOLEAN_FIELD));
    
    DataTable rightResultDataTable = new SimpleDataTable(rightResultFormat);
    rightResultDataTable.addRecord(3, "3", "33", false);
    rightResultDataTable.addRecord(4, "4", "44", false);
    rightResultDataTable.addRecord(1, "1", "", true);
    rightResultDataTable.addRecord(2, "2", "", true);
    
    DataTable calculateDataTable = (DataTable) new UnionFunction().execute(ev, null, leftTable, rightTable);
    assertEquals(4, (int) calculateDataTable.getRecordCount());
    
    calculateDataTable = (DataTable) new UnionFunction().execute(ev, null, calculateDataTable, rightTable);
    assertEquals(6, (int) calculateDataTable.getRecordCount());
    assertTrue(calculateDataTable.hasField("C"));
  }
  
  public void testJoinFunction() throws Exception
  {
    TableFormat leftFormat = new TableFormat();
    leftFormat.addField(FieldFormat.create("A", FieldFormat.BOOLEAN_FIELD));
    leftFormat.addField(FieldFormat.create("B", FieldFormat.INTEGER_FIELD));
    leftFormat.addField(FieldFormat.create("C", FieldFormat.STRING_FIELD));
    DataTable leftTable = new SimpleDataTable(leftFormat);
    leftTable.addRecord(true, 1, "1");
    leftTable.addRecord(true, 2, "2");
    
    TableFormat rightFormat = new TableFormat(2, 2);
    rightFormat.addField(FieldFormat.create("B", FieldFormat.INTEGER_FIELD));
    rightFormat.addField(FieldFormat.create("C", FieldFormat.STRING_FIELD));
    rightFormat.addField(FieldFormat.create("D", FieldFormat.STRING_FIELD));
    DataTable rightTable = new SimpleDataTable(rightFormat);
    rightTable.addRecord(3, "3", "33");
    rightTable.addRecord(4, "4", "44");
    
    TableFormat leftResultFormat = new TableFormat(2, Integer.MAX_VALUE);
    leftResultFormat.addField(FieldFormat.create("A", FieldFormat.BOOLEAN_FIELD));
    leftResultFormat.addField(FieldFormat.create("B", FieldFormat.INTEGER_FIELD));
    leftResultFormat.addField(FieldFormat.create("C", FieldFormat.STRING_FIELD));
    leftResultFormat.addField(FieldFormat.create("D", FieldFormat.STRING_FIELD));
    
    DataTable leftResultDataTable = new SimpleDataTable(leftResultFormat);
    leftResultDataTable.addRecord(true, 1, "1", "33");
    leftResultDataTable.addRecord(true, 2, "2", "44");
    
    TableFormat rightResultFormat = new TableFormat(2, Integer.MAX_VALUE);
    rightResultFormat.addField(FieldFormat.create("B", FieldFormat.INTEGER_FIELD));
    rightResultFormat.addField(FieldFormat.create("C", FieldFormat.STRING_FIELD));
    rightResultFormat.addField(FieldFormat.create("D", FieldFormat.STRING_FIELD));
    rightResultFormat.addField(FieldFormat.create("A", FieldFormat.BOOLEAN_FIELD));
    
    DataTable rightResultDataTable = new SimpleDataTable(rightResultFormat);
    rightResultDataTable.addRecord(3, "3", "33", true);
    rightResultDataTable.addRecord(4, "4", "44", true);
    
    DataTable calculateDataTable = (DataTable) new JoinFunction().execute(ev, null, leftTable, rightTable);
    assertEquals(leftResultDataTable, calculateDataTable);
    
    calculateDataTable = (DataTable) new JoinFunction().execute(ev, null, rightTable, leftTable);
    assertEquals(rightResultDataTable, calculateDataTable);
  }
  
  public void testTimestampAndQualityFunctions() throws Exception
  {
    DataTable table = new SimpleDataTable();
    
    Date timestamp = new Date(System.currentTimeMillis());
    int quality = 123;
    
    new SetTimestampFunction().execute(ev, null, table, timestamp);
    new SetQualityFunction().execute(ev, null, table, quality);
    
    assertEquals(timestamp, new GetTimestampFunction().execute(ev, null, table));
    assertEquals(quality, new GetQualityFunction().execute(ev, null, table));
    
  }
  
  public void testSetNestedFieldFunction() throws Exception
  {
    TableFormat format = new TableFormat();
    format.addField(FieldFormat.create("I", FieldFormat.INTEGER_FIELD));
    format.addField(FieldFormat.create("T", FieldFormat.DATATABLE_FIELD));
    
    DataTable level1 = new SimpleDataTable(format);
    DataTable level2 = new SimpleDataTable(format);
    DataTable level3 = new SimpleDataTable(format);
    level1.addRecord(1, level2);
    level2.addRecord(1, level3);
    level3.addRecord(1, new SimpleDataTable());
    
    DataTable res = (DataTable) new SetNestedFieldFunction().execute(ev, null, level1, 2, "I", 0);
    assertEquals(2, (int) res.rec().getInt("I"));
    
    res = (DataTable) new SetNestedFieldFunction().execute(ev, null, level1, 3, "T", 0, "I", 0);
    assertEquals(3, (int) res.rec().getDataTable("T").rec().getInt("I"));
    
    res = (DataTable) new SetNestedFieldFunction().execute(ev, null, level1, 4, "T", 0, "T", 0, "I", 0);
    assertEquals(4, (int) res.rec().getDataTable("T").rec().getDataTable("T").rec().getInt("I"));
  }
  
}
