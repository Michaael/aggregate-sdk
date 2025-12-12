package com.tibbo.aggregate.common.datatable;

import com.tibbo.aggregate.common.context.*;
import com.tibbo.aggregate.common.data.*;
import com.tibbo.aggregate.common.datatable.encoding.*;
import com.tibbo.aggregate.common.expression.*;
import com.tibbo.aggregate.common.tests.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestDataTable extends CommonsTestCase
{
  private DataTable dt = null;
  
  @Override
  protected void setUp() throws Exception
  {
    super.setUp();
    dt = new SimpleDataTable();
  }
  
  @Override
  protected void tearDown() throws Exception
  {
    dt = null;
    super.tearDown();
  }
  
  private TableFormat createFormat(String format)
  {
    return new TableFormat(format, new ClassicEncodingSettings(true));
  }
  
  public void testDataTable() throws ContextException
  {
    TableFormat format = createFormat("<<val><I>> <<str><S>> <<bool><B>>");
    final int emptyRecords = 20;
    dt = new SimpleDataTable(format, emptyRecords);
    
    assertEquals(emptyRecords, (int) dt.getRecordCount());
    assertEquals(3, dt.getFieldCount());
    
    // Test datatable type field
    
    DataTable tbl = new SimpleDataTable(createFormat("<<int><I>> <<str><S>> <<bool><B>>"), 5, "test", true);
    
    DataRecord rec = new DataRecord(FieldFormat.create("<tbl><T>").wrap());
    rec.addDataTable(tbl);
    
    DataTable t1 = new SimpleDataTable(rec);
    t1 = new SimpleDataTable(t1.encode(new ClassicEncodingSettings(false)));
    
    t1 = t1.rec().getDataTable("tbl");
    
    assertEquals(1, (int) t1.getRecordCount());
    assertEquals(3, t1.getFieldCount());
    assertEquals(5, (int) t1.rec().getInt("int"));
    assertEquals("test", t1.rec().getString("str"));
    assertEquals(Boolean.TRUE, t1.rec().getBoolean("bool"));
  }
  
  public void testClone()
  {
    TableFormat format = FieldFormat.create("<s1><S><F=N><A=default><D=Test><S=<desc=default><desc2=val2>><V=<L=1 10>>").wrap();
    dt = new SimpleDataTable(format, 5);
    
    assertEquals(2, dt.getFormat().getField("s1").getSelectionValues().size());
    
    DataTable cl = dt.clone();
    
    assertTrue(format == cl.getFormat());
    
    assertEquals(2, cl.getFormat().getField("s1").getSelectionValues().size());
    
    dt.getRecord(2).setValue("s1", "val2");
    
    assertEquals("default", cl.getRecord(2).getString("s1"));
    
  }
  
  public void testParameterizedReportThatWorksIncorrectly()
  {
    TableFormat format = createFormat("<<devices><T><A=<F=<<devicePath><S><A=><D=Device Path>>><R=<aaa>><R=<bbb>>>>");
    // "<<devices><T><A=<F=<<devicePath><S><D=Device Path>>><R=<aaa>><R=<bbb>>>>");
    DataTable result = new SimpleDataTable(format, 1);
    DataTable devices = result.rec().getDataTable("devices");
    assertEquals(2, (int) devices.getRecordCount());
    assertEquals("aaa", devices.getRecord(0).getString("devicePath"));
    assertEquals("bbb", devices.getRecord(1).getString("devicePath"));
  }
  
  public void testInnerTableRecordsAreFilledProperly() throws ContextException
  {
    TableFormat innerFormat = new TableFormat();
    innerFormat.addField(FieldFormat.STRING_FIELD, "devicePath", "Device Path");
    DataTable defaultInnerTable = new SimpleDataTable(innerFormat);
    defaultInnerTable.addRecord("aaa");
    defaultInnerTable.addRecord("bbb");
    
    TableFormat outerFormat = new TableFormat();
    outerFormat.addField(FieldFormat.DATATABLE_FIELD, "devices");
    outerFormat.getField(0).setDefault(defaultInnerTable);
    
    String encodedFormat = outerFormat.encode(true);
    assertEquals("<<devices><T><A=<F=<<devicePath><S><A=><D=Device Path>>><R=<aaa>><R=<bbb>>>>", encodedFormat);
    
    final TableFormat decodedFormat = createFormat(encodedFormat);
    DataTable innerDefaultTable = (DataTable) decodedFormat.getField("devices").getDefaultValue();
    assertEquals(2, (int) innerDefaultTable.getRecordCount());
    assertEquals("aaa", innerDefaultTable.getRecord(0).getString("devicePath"));
    
    DataTable result = new SimpleDataTable(decodedFormat, 1);
    DataTable devices = result.rec().getDataTable("devices");
    assertEquals(2, (int) devices.getRecordCount());
    assertEquals("aaa", devices.getRecord(0).getString("devicePath"));
    assertEquals("bbb", devices.getRecord(1).getString("devicePath"));
  }
  
  public void testDataTableEncodingLigth() throws Exception
  {
    TableFormat tf = FieldFormat.create("DATATABLE_FIELD", FieldFormat.DATATABLE_FIELD).wrap();
    
    DataTable table = new SimpleDataTable(tf);
    DataRecord record = table.addRecord();
    DataTable table2 = table.clone();
    record.setValue(0, table2);
    DataTable table3 = table.clone();
    table2.getRecord(0).setValue(0, table3);
    
    String e = table.encode();
    System.out.println(TransferEncodingHelper.encode(e));
    // e =
    // "FDATATABLE_FIELDTAFR%<F%=%<%<DATATABLE_FIELD%>%<T%>%<A%=%<F%=%>%>%>%>%<R%=%<%%<F%%=%%<%%<DATATABLE_FIELD%%>%%<T%%>%%<A%%=%%<F%%=%%>%%>%%>%%>%%<R%%=%%<%%%%<F%%%%=%%%%<%%%%<DATATABLE_FIELD%%%%>%%%%<T%%%%>%%%%<A%%%%=%%%%<F%%%%=%%%%>%%%%>%%%%>%%%%>%%%%<R%%%%=%%%%<%%%%%%%%<F%%%%%%%%=%%%%%%%%>%%%%>%%%%>%%>%%>%>%>";
    
    DataTable r = new SimpleDataTable(e);
    System.out.println("test: " + e);
    String res = TransferEncodingHelper
        .encode("<F=<<DATATABLE_FIELD><T><A=<F=>>>><R=<<F=<<DATATABLE_FIELD><T><A=<F=>>>><R=<<F=<<DATATABLE_FIELD><T><A=<F=>>>><R=<<F=<<DATATABLE_FIELD><T><A=<F=>>>><R=<<F=>>>>>>>>>", 1);
    assertEquals(table, r);
  }
  
  public void testTransfer()
  {
    String data = "FDATATABLE_FIELDTAF";
    String res = TransferEncodingHelper.encode(data);
    String res2 = TransferEncodingHelper.encode(res);
    String res3 = TransferEncodingHelper.encode(res2);

    String res5 = TransferEncodingHelper.encode(data, 1);
    String res6 = TransferEncodingHelper.encode(data, 2);
    String res7 = TransferEncodingHelper.encode(data, 3);

    assertEquals(res, res5);
    assertEquals(res2, res6);
    assertEquals(res3, res7);
  }
  
  public void testDataTableEncoding() throws Exception
  {
    TableFormat tf = FieldFormat.create("STRING_FIELD", FieldFormat.STRING_FIELD).wrap();
    tf.addField(FieldFormat.create("BOOLEAN_FIELD", FieldFormat.BOOLEAN_FIELD));
    tf.addField(FieldFormat.create("DATA_FIELD", FieldFormat.DATA_FIELD));
    tf.addField(FieldFormat.create("DATE_FIELD", FieldFormat.DATE_FIELD));
    tf.addField(FieldFormat.create("DATATABLE_FIELD", FieldFormat.DATATABLE_FIELD));
    
    Data dataF = new Data();
    dataF.setData("test data % %% %%%".getBytes());
    
    DataTable table = new SimpleDataTable(tf);
    DataRecord record = table.addRecord();
    record.setValue(0, "test data % %% %%%");
    record.setValue(1, true);
    record.setValue(2, dataF);
    record.setValue(3, new Date());
    
    DataTable table2 = table.clone();
    record.setValue(4, table2);
    
    DataTable table3 = table.clone();
    
    table2.getRecord(0).setValue(4, table3);
    
    String e = table.encode();
    
    DataTable r = new SimpleDataTable(e);
    
    // e =
    // " F STRING_FIELD S A BOOLEAN_FIELD B A 0 DATA_FIELD A A 0///-1/-1/ DATE_FIELD D A 2000-02-01 12:00:00.000 DATATABLE_FIELD T A F R test data %% %%%% %%%%%% 1 0///-1/18/test data %% %%%%
    // %%%%%% 2016-07-07 13:27:50.551 %<F%=%<%<STRING_FIELD%>%<S%>%<A%=%>%>%<%<BOOLEAN_FIELD%>%<B%>%<A%=0%>%>%<%<DATA_FIELD%>%<A%>%<A%=0///-1/-1/%>%>%<%<DATE_FIELD%>%<D%>%<A%=2000-02-01
    // 12:00:00.000%>%>%<%<DATATABLE_FIELD%>%<T%>%<A%=%<F%=%>%>%>%>%<R%=%<test data %%%% %%%%%%%% %%%%%%%%%%%%%>%<1%>%<0///-1/18/test data %%%% %%%%%%%% %%%%%%%%%%%%%>%<2016-07-07
    // 13:27:50.551%>%<%%%<F%%%=%%%<%%%<STRING_FIELD%%%>%%%<S%%%>%%%<A%%%=%%%>%%%>%%%<%%%<BOOLEAN_FIELD%%%>%%%<B%%%>%%%<A%%%=0%%%>%%%>%%%<%%%<DATA_FIELD%%%>%%%<A%%%>%%%<A%%%=0///-1/-1/%%%>%%%>%%%<%%%<DATE_FIELD%%%>%%%<D%%%>%%%<A%%%=2000-02-01
    // 12:00:00.000%%%>%%%>%%%<%%%<DATATABLE_FIELD%%%>%%%<T%%%>%%%<A%%%=%%%<F%%%=%%%>%%%>%%%>%%%>%<R%=%<test data %%%%%%%% %%%%%%%%%%%%%%%% %%%%%%%%%%%%%%%%%%%%%%%%%>%<1%>%<0///-1/18/test data
    // %%%%%%%% %%%%%%%%%%%%%%%% %%%%%%%%%%%%%%%%%%%%%%%%%>%<2016-07-07
    // 13:27:50.551%>%<%%%%%%%<F%%%%%%%=%%%%%%%<%%%%%%%<STRING_FIELD%%%%%%%>%%%%%%%<S%%%%%%%>%%%%%%%<A%%%%%%%=%%%%%%%>%%%%%%%>%%%%%%%<%%%%%%%<BOOLEAN_FIELD%%%%%%%>%%%%%%%<B%%%%%%%>%%%%%%%<A%%%%%%%=0%%%%%%%>%%%%%%%>%%%%%%%<%%%%%%%<DATA_FIELD%%%%%%%>%%%%%%%<A%%%%%%%>%%%%%%%<A%%%%%%%=0///-1/-1/%%%%%%%>%%%%%%%>%%%%%%%<%%%%%%%<DATE_FIELD%%%%%%%>%%%%%%%<D%%%%%%%>%%%%%%%<A%%%%%%%=2000-02-01
    // 12:00:00.000%%%%%%%>%%%%%%%>%%%%%%%<%%%%%%%<DATATABLE_FIELD%%%%%%%>%%%%%%%<T%%%%%%%>%%%%%%%<A%%%%%%%=%%%%%%%<F%%%%%%%=%%%%%%%>%%%%%%%>%%%%%%%>%%%%%%%>%<R%=%<test data %%%%%%%%%%%%%%%%
    // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%>%<1%>%<0///-1/18/test data %%%%%%%%%%%%%%%% %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%>%<2016-07-07 13:27:50.551%>%<%%%%%%%%%%%%%%%<F%%%%%%%%%%%%%%%=%%%%%%%%%%%%%%%>%>%>%>%>%>%> ";
    
    // r = new SimpleDataTable(e);
    
    assertEquals(table, r);
  }
  
  public void testEncodingOfNestedTableWithoutDefaultValue() throws Exception
  {
    final TableFormat tableFormat = FieldFormat.create("DATATABLE_FIELD", FieldFormat.DATATABLE_FIELD).wrap();
    final DataTable mainTable = new SimpleDataTable(tableFormat);
    
    final TableFormat nestedTableFormat = FieldFormat.create("STRING_FIELD", FieldFormat.STRING_FIELD).wrap();
    final DataTable nestedTable1 = new SimpleDataTable(nestedTableFormat);
    final DataRecord nestedRecord11 = nestedTable1.addRecord();
    nestedRecord11.addString("aaa1");
    final DataRecord nestedRecord12 = nestedTable1.addRecord();
    nestedRecord12.addString("bbb1");
    
    final DataTable nestedTable2 = new SimpleDataTable(nestedTableFormat);
    final DataRecord nestedRecord21 = nestedTable2.addRecord();
    nestedRecord21.addString("aaa2");
    final DataRecord nestedRecord22 = nestedTable2.addRecord();
    nestedRecord22.addString("bbb2");
    
    DataRecord record1 = mainTable.addRecord();
    record1.addDataTable(nestedTable1);
    DataRecord record2 = mainTable.addRecord();
    record2.addDataTable(nestedTable2);
    
    String encoded = mainTable.encode(true);
    
    DataTable decoded = new SimpleDataTable(encoded, new ClassicEncodingSettings(ExpressionUtils.useVisibleSeparators(encoded)), true);
    
    assertEquals(mainTable, decoded);
  }
  
  public void testEncodingOfNestedTableWithDefaultValue() throws Exception
  {
    final TableFormat tableFormat = new TableFormat(1, 1);
    
    final FieldFormat ff = FieldFormat.create("DATATABLE_FIELD", FieldFormat.DATATABLE_FIELD);
    
    final TableFormat nestedTableFormat = FieldFormat.create("STRING_FIELD", FieldFormat.STRING_FIELD).wrap();
    final DataTable nestedTable = new SimpleDataTable(nestedTableFormat);
    final DataRecord nestedRecord = nestedTable.addRecord();
    nestedRecord.addString("aaa1");
    
    ff.setDefault(nestedTable);
    
    tableFormat.addField(ff);
    
    final DataTable mainTable = new SimpleDataTable(tableFormat);
    
    final DataTable nestedTable2 = new SimpleDataTable(nestedTableFormat);
    final DataRecord nestedRecord21 = nestedTable2.addRecord();
    nestedRecord21.addString("aaa2");
    final DataRecord nestedRecord22 = nestedTable2.addRecord();
    nestedRecord22.addString("bbb2");
    
    DataRecord record1 = mainTable.addRecord();
    record1.addDataTable(nestedTable2);
    
    String encoded = mainTable.encode(true);
    
    DataTable decoded = new SimpleDataTable(encoded, new ClassicEncodingSettings(ExpressionUtils.useVisibleSeparators(encoded)), true);
    
    assertEquals(mainTable, decoded);
  }
  
  public void testPutDataTableInItself()
  {
    TableFormat tf = new TableFormat(1, 1);
    tf.addField("<str><S>");
    tf.addField("<int><I>");
    tf.addField("<float><F>");
    tf.addField("<datatable><T>");
    tf.addField("<date><D>");
    DataTable dt = new SimpleDataTable(tf, true);
    dt.rec().setValue(0, "str");
    dt.rec().setValue(1, 1);
    dt.rec().setValue(2, 2);
    dt.rec().setValue(4, new Date());
    DataTable expected = dt.clone();
    dt.rec().setValue(3, dt);
    
    assertEquals(expected, dt.rec().getValue("datatable"));
  }
  
  public void testEquals()
  {
    TableFormat tf = createFormat("<<val><I>>");
    DataTable dt1 = new SimpleDataTable();
    DataTable dt2 = new SimpleDataTable();
    
    dt1.addRecord(155);
    dt2.addRecord(155);
    assertEquals(dt1, dt2);
    
    dt1.setQuality(192);
    dt1.setTimestamp(new Date());
    dt2.setQuality(192);
    dt2.setTimestamp(null);
    assertEquals(dt1, dt2);
    
    dt1.setQuality(192);
    dt2.setQuality(0);
    assertNotEquals(dt1, dt2);
    
    dt1.setQuality(192);
    dt2.setQuality(null);
    assertNotEquals(dt1, dt2);
    
    dt1.setQuality(null);
    dt2.setQuality(192);
    assertNotEquals(dt1, dt2);
  }
}
