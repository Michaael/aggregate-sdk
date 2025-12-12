package com.tibbo.aggregate.common.datatable;

import com.tibbo.aggregate.common.datatable.encoding.*;
import com.tibbo.aggregate.common.datatable.validator.*;
import com.tibbo.aggregate.common.tests.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestDataRecord extends CommonsTestCase
{
  private DataRecord rec = null;
  private DataRecord rec2 = null;
  
  @Override
  protected void setUp() throws Exception
  {
    super.setUp();
    rec = new DataRecord();
  }
  
  @Override
  protected void tearDown() throws Exception
  {
    rec = null;
    super.tearDown();
  }
  
  private TableFormat createFormat(String format)
  {
    return new TableFormat(format, new ClassicEncodingSettings(true));
  }
  
  @Test
  public void testDataRecord() throws Exception
  {
    rec = new DataRecord(createFormat("<<str><S>> <<int><I>> <<bool><B>> <<long><L>>"));
    assertEquals(rec.getString("str"), "");
    assertEquals(rec.getInt("int"), new Integer(0));
    assertEquals(rec.getBoolean("bool"), Boolean.valueOf(false));
    assertEquals(rec.getLong("long"), new Long(0));
    
    try
    {
      rec.addString("ok");
      rec.addString("failure");
      assertTrue(false);
    }
    catch (Exception ex)
    {
    }
    
    try
    {
      rec.getBoolean("int");
      assertTrue(false);
    }
    catch (Exception ex1)
    {
    }
    
    rec.setValue("long", new Long(80));
    assertEquals(rec.getLong("long"), new Long(80));
    assertEquals(rec.getValue("int"), new Integer(0));
    
    rec = new DataRecord(createFormat("<<str><S><F=N>> <<int><I><F=N>> <<bool><B><F=N>> <<long><L><F=N>>"));
    assertEquals(null, rec.getString("str"));
    assertEquals(null, rec.getValue("int"));
    assertEquals(null, rec.getValue("bool"));
    assertEquals(null, rec.getValue("long"));
    
    rec = new DataRecord(createFormat("<<str><S><F=N><A=duck>> <<int><I><F=N><A=11>> <<bool><B><F=N><A=1>> <<long><L><F=N><A=123>>"));
    assertEquals("duck", rec.getString("str"));
    assertEquals(new Integer(11), rec.getValue("int"));
    assertEquals(Boolean.valueOf(true), rec.getValue("bool"));
    assertEquals(new Long(123), rec.getValue("long"));
    
    rec2 = new DataRecord(createFormat("<<str><S><F=N><A=duck>> <<int><I><F=N><A=11>> <<bool><B><F=N><A=1>> <<long><L><F=N><A=123>>"));
    
    assertEquals(rec, rec2);
    
    rec2 = new DataRecord(createFormat("<<str><S><F=N><A=duck>> <<int><I><F=N><A=11>> <<bool><B><F=N><A=1>>"));
    
    assertFalse(rec.equals(rec2));
    
    FieldFormat ff1 = FieldFormat.create("test", 'I');
    TableFormat rf1 = new TableFormat(ff1);
    rec = new DataRecord(rf1);
    rec.addInt(0);
    
    rf1 = createFormat("<<str><S><F=N><A=duck>> <<st2><S><F=N>> <<int><I><F=N><A=11>> <<bool><B><F=N><A=1>>");
    rec = new DataRecord(rf1, "xx1", null, 4, true);
    
    assertEquals("xx1", rec.getString("str"));
    assertEquals(null, rec.getString("st2"));
    assertEquals(4, rec.getValue("int"));
    assertEquals(Boolean.valueOf(true), rec.getBoolean("bool"));
    
    TableFormat rf3 = createFormat("<<str><S><F=N><A=duck>> <<int><I><F=N><A=11>> <<bool><B><F=N><A=1>>");
    rec = new DataRecord(rf3);
    
    rec.setValue("int", null);
    assertEquals(null, rec.getValue("int"));
    
    rec.setValue("bool", true);
    assertEquals(true, rec.getValue("bool"));
    
    rec = new DataRecord(createFormat("<<str><S><F=N><A=duck>> <<int><I><F=N><A=11>> <<bool><B><F=N><A=1>>"));
    rec.setValue("bool", true);
    assertEquals(Boolean.valueOf(true), rec.getBoolean("bool"));
    
    ff1 = FieldFormat.create("<val><I>");
    ff1.getValidators().add(new LimitsValidator(10, 20));
    
    rec = new DataRecord(new TableFormat(ff1));
    
    rec.setValue(0, 15);
    
    try
    {
      rec.setValue(0, 5);
      assertTrue(false);
    }
    catch (Exception ex2)
    {
    }
    
    try
    {
      rec.setValue(0, 25);
      assertTrue(false);
    }
    catch (Exception ex2)
    {
    }
  }
  
  @Test
  public void testSelect() throws Exception
  {
    final TableFormat rf = new TableFormat();
    rf.addField("<str><S>");
    rf.addField("<int><I>");
    rf.addField("<val><S>");
    DataTable t = new SimpleDataTable(rf);
    
    t.addRecord().addString("line1").addInt(1).addValue("test1");
    t.addRecord().addString("line2").addInt(2).addValue("test2");
    t.addRecord().addString("line3").addInt(3).addValue("test3");
    t.addRecord().addString("line3").addInt(4).addValue("test4");
    
    assertEquals("test2", t.select("str", "line2").getString("val"));
    assertEquals("test4", t.select(new DataTableQuery(new QueryCondition("str", "line3"), new QueryCondition("int", 4))).getString("val"));
  }
  
  @Test
  public void testHashCodeDataRecord()
  {
    TableFormat format1 = new TableFormat();
    format1.addField("<st><S><F=K>");
    format1.addField("<val><I>");
    
    DataTable table1 = new SimpleDataTable(format1);
    table1.addRecord().addString("line1").addInt(11);
    
    TableFormat format2 = new TableFormat();
    format2.addField("<st><S><F=K>");
    format2.addField("<val><I>");
    
    DataTable table2 = new SimpleDataTable(format2);
    table2.addRecord().addString("line1").addInt(11);
    
    assertEquals(true, table1.getRecord(0).hashCode() == table2.getRecord(0).hashCode());
  }
  
  @Test
  public void testEqualsDataRecord()
  {
    TableFormat format1 = new TableFormat();
    format1.addField("<st><S><F=K>");
    format1.addField("<val><I>");
    
    DataTable table1 = new SimpleDataTable(format1);
    table1.addRecord().addString("line1").addInt(11);
    
    TableFormat format2 = new TableFormat();
    format2.addField("<st><S><F=K>");
    format2.addField("<val><I>");
    
    DataTable table2 = new SimpleDataTable(format2);
    table2.addRecord().addString("line1").addInt(11);
    
    assertEquals(true, table1.getRecord(0).equals(table2.getRecord(0)));
  }
  
}
