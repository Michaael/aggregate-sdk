package com.tibbo.aggregate.common.tests;

import com.tibbo.aggregate.common.*;
import com.tibbo.aggregate.common.datatable.*;
import com.tibbo.aggregate.common.expression.*;

public class CommonsFixture
{
  private static boolean STARTED;
  
  public CommonsFixture()
  {
  }
  
  public CommonsFixture(Object obj)
  {
  }
  
  public void setUp() throws Exception
  {
    if (!STARTED)
    {
      Log.start();
      STARTED = true;
    }
  }
  
  public void tearDown() throws Exception
  {
  }
  
  public static Evaluator createTestEvaluator()
  {
    ReferenceResolver rr = new DefaultReferenceResolver();
    
    rr.setDefaultTable(createTestTable());
    
    Evaluator ev = new Evaluator(rr);
    
    return ev;
  }
  
  public static DataTable createTestTable()
  {
    TableFormat rf = new TableFormat(5, 10);
    
    rf.addField("<str><S><A=test>");
    rf.addField("<int><I><A=123>");
    rf.addField("<bool><B><A=1>");
    
    DataTable table = new SimpleDataTable(rf);
    
    table.addRecord("test", 123, true);
    table.addRecord("second", 222, true);
    table.addRecord("third", -111, false);
    table.addRecord("fourth", 444, false);
    table.addRecord("fifth", 666, true);
    table.addRecord("sixth", -7, true);
    table.addRecord("seventh", 7, false);
    
    return table;
    
  }
  
}
