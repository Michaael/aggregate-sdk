package com.tibbo.aggregate.common.datatable;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.tibbo.aggregate.common.tests.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestDataTableNaming extends CommonsTestCase
{
  private DataTable dataTable = null;
  
  @Override
  @BeforeEach
  protected void setUp() throws Exception
  {
    super.setUp();
    final TableFormat rf = new TableFormat();
    rf.addField("<f1><S>");
    rf.addField("<f2><S>");
    rf.addField("<b><B>");
    rf.setNamingExpression("{f1} + ' ' + ({b}?{f1}:{f2})");
    dataTable = new SimpleDataTable(rf);
  }
  
  @Override
  @AfterEach
  protected void tearDown() throws Exception
  {
    dataTable = null;
    super.tearDown();
  }
  
  @Test
  public void testGetDescription()
  {
    dataTable.addRecord().addString("field1").addString("field2");
    
    String expectedReturn = "field1 field2";
    String actualReturn = dataTable.getDescription();
    assertEquals(expectedReturn, actualReturn, "return value");
    
    dataTable.rec().setValue("b", true);
    expectedReturn = "field1 field1";
    actualReturn = dataTable.getDescription();
    assertEquals(expectedReturn, actualReturn, "return value");
  }
  
}
