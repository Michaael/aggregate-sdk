package com.tibbo.aggregate.common.datatable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.*;

import com.tibbo.aggregate.common.tests.*;
import com.tibbo.aggregate.common.util.*;
import org.junit.jupiter.api.Test;

public class TestDataTableUtils extends CommonsTestCase
{
  
  @Test
  public void testCopyWithKeysFieldsAndSort()
  {
    final TableFormat rf1 = new TableFormat();
    rf1.addField("<st><S><F=K>");
    rf1.addField("<val><I>");
    DataTable master = new SimpleDataTable(rf1);
    
    master.addRecord().addString("line1").addInt(11);
    master.addRecord().addString("line2").addInt(33);
    master.addRecord().addString("line3").addInt(22);
    master.addRecord().addString("line4").addInt(44);
    
    final TableFormat rf2 = new TableFormat(0, 10);
    rf2.addField("<st><S><F=K>");
    rf2.addField("<va><I>");
    rf2.addField("<test><I><A=555>");
    DataTable slave = new SimpleDataTable(rf2);
    
    slave.addRecord().addString("line4").addInt(11).addInt(111);
    slave.addRecord().addString("line3").addInt(22).addInt(222);
    slave.addRecord().addString("line2").addInt(11).addInt(111);
    slave.addRecord().addString("line1").addInt(22).addInt(222);
    
    DataTableReplication.copy(master, slave, false, true, true);
    
    assertEquals(4, (int) master.getRecordCount());
    assertEquals(4, (int) slave.getRecordCount());
    
    assertEquals("line4", slave.getRecord(0).getString("st"));
    assertEquals("line3", slave.getRecord(1).getString("st"));
    assertEquals("line2", slave.getRecord(2).getString("st"));
    assertEquals("line1", slave.getRecord(3).getString("st"));
  }
  
  @Test
  public void testCopyWithoutKeysFields()
  {
    final TableFormat rf = new TableFormat();
    rf.addField("<st><S>");
    rf.addField("<val><I>");
    DataTable master = new SimpleDataTable(rf);
    
    master.addRecord().addString("line1").addInt(11);
    master.addRecord().addString("line2").addInt(22);
    master.addRecord().addString("line3").addInt(33);
    master.addRecord().addString("line4").addInt(44);
    
    final TableFormat rf2 = new TableFormat(0, 10);
    rf2.addField("<st><S>");
    rf2.addField("<val><I>");
    rf2.addField("<test><I><A=555>");
    DataTable slave = new SimpleDataTable(rf2);
    
    slave.addRecord().addString("xxx1").addInt(111).addInt(111);
    slave.addRecord().addString("xxx2").addInt(222).addInt(222);
    slave.addRecord().addString("xxx3").addInt(333).addInt(333);
    
    DataTableReplication.copy(master, slave);
    
    assertEquals(4, (int) master.getRecordCount());
    assertEquals(4, (int) slave.getRecordCount());
    
    assertEquals("line1", slave.getRecord(0).getString("st"));
    assertEquals(555, (int) slave.getRecord(3).getInt("test"));
    
    assertEquals("line2", master.getRecord(1).getString("st"));
  }
  
  @Test
  public void testCopyWithKeysFields()
  {
    final TableFormat rf1 = new TableFormat();
    rf1.addField("<st><S><F=K>");
    rf1.addField("<val><I>");
    DataTable master = new SimpleDataTable(rf1);
    
    master.addRecord().addString("line1").addInt(11).addString("1");
    master.addRecord().addString("line2").addInt(22).addString("1");
    master.addRecord().addString("line3").addInt(33).addString("1");
    master.addRecord().addString("line4").addInt(44).addString("1");
    
    final TableFormat rf2 = new TableFormat(0, 10);
    rf2.addField("<st><S><F=K>");
    rf2.addField("<val><I>");
    rf2.addField("<test><I><A=555>");
    DataTable slave = new SimpleDataTable(rf2);
    
    slave.addRecord().addString("xxx1").addInt(111).addInt(111);
    slave.addRecord().addString("xxx2").addInt(222).addInt(222);
    slave.addRecord().addString("line1").addInt(333).addInt(333);
    
    DataTableReplication.copy(master, slave);
    
    assertEquals(4, (int) master.getRecordCount());
    assertEquals(4, (int) slave.getRecordCount());
    
    assertEquals(555, (int) slave.select("st", "line3").getInt("test"));
    assertEquals(333, (int) slave.select("st", "line1").getInt("test"));
  }
  
  @Test
  public void testCopyWithMaxRecordsReached()
  {
    final TableFormat rf = new TableFormat();
    rf.addField("<st><S>");
    rf.addField("<val><I>");
    DataTable master = new SimpleDataTable(rf);
    
    master.addRecord().addString("line1").addInt(11);
    master.addRecord().addString("line2").addInt(22);
    master.addRecord().addString("line3").addInt(33);
    master.addRecord().addString("line4").addInt(44);
    
    final TableFormat rf2 = new TableFormat(0, 3);
    rf2.addField("<st><S>");
    rf2.addField("<val><I>");
    rf2.addField("<test><I><A=555>");
    DataTable slave = new SimpleDataTable(rf2);
    
    slave.addRecord().addString("xxx1").addInt(111).addInt(111);
    slave.addRecord().addString("xxx2").addInt(222).addInt(222);
    slave.addRecord().addString("xxx3").addInt(333).addInt(333);
    
    Set<String> errors = DataTableReplication.copy(master, slave);
    
    assertEquals(3, (int) slave.getRecordCount());
    
    assertTrue(errors.size() == 1);
  }
  
  @Test
  public void testDefaultValuesInitializationForMissingRecordFields()
  {
    TableFormat f = WindowLocation.FORMAT.clone();
    
    f.removeField(WindowLocation.FIELD_CLOSABLE);
    
    WindowLocation wl = new WindowLocation(new DataRecord(f));
    
    assertTrue(wl.isClosable());
  }
  
  @Test
  public void testCopyWithBindings()
  {
    final TableFormat rf = new TableFormat();
    rf.addField("<st><T>");
    DataTable master = new SimpleDataTable(rf);
    
    final TableFormat rf2 = new TableFormat();
    rf2.addField("<innermaster><S><A=master>");
    rf2.addBinding("innermaster", "\"innermaster\"");
    DataTable dt = new SimpleDataTable(rf2, true);
    dt.addRecord().setValue(0, "testMaster");
    master.addRecord().setValue(0, dt);
    
    DataTable slaveWithoutRecords = new SimpleDataTable(rf);
    final TableFormat rf3 = rf2.clone();
    rf3.setBindings(new ArrayList<>());
    DataTable dt3 = new SimpleDataTable(rf3);
    dt3.addRecord().setValue(0, "testSlave");
    
    DataTable slaveWithRecords = slaveWithoutRecords.clone();
    slaveWithRecords.addRecord().setValue(0, dt3);
    
    DataTableReplication.copy(master, slaveWithoutRecords, false, true, true);
    
    assertEquals(slaveWithoutRecords.rec().getDataTable(0).getFormat().getBindings().size(), 1);
    
    DataTableReplication.copy(master, slaveWithRecords, false, true, true);
    
    assertEquals(slaveWithRecords.rec().getDataTable(0).getFormat().getBindings().size(), 0);
  }
  
}
