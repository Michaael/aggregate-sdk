package com.tibbo.aggregate.common.datatable;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.tibbo.aggregate.common.expression.*;
import com.tibbo.aggregate.common.tests.*;
import com.tibbo.aggregate.common.util.*;

public class TestDataTableBindings extends CommonsTestCase
{
  
  private static final String VF_TEST_STR = "str";
  private static final String VF_TEST_INT = "int";
  private static final String VF_TEST_FLOAT = "float";
  private static final String VF_TEST_DATATABLE = "datatable";
  private static final String VF_TEST_DATE = "date";
  
  private static final TableFormat VFT_TEST = new TableFormat(1, 1);
  static
  {
    VFT_TEST.addField("<" + VF_TEST_STR + "><S>");
    VFT_TEST.addField("<" + VF_TEST_INT + "><I>");
    VFT_TEST.addField("<" + VF_TEST_FLOAT + "><F>");
    VFT_TEST.addField("<" + VF_TEST_DATATABLE + "><T>");
    VFT_TEST.addField("<" + VF_TEST_DATE + "><D>");
  }
  
  public void testTwoSimpleBindings()
  {
    TableFormat tf = VFT_TEST.clone();
    tf.addBinding(VF_TEST_INT, "123");
    tf.addBinding(VF_TEST_STR, "{" + VF_TEST_INT + "} == 123 ? \"ok\" : \"no\"");
    DataTable dt = new SimpleDataTable(tf, 1);
    Evaluator evaluator = new Evaluator(new SimpleDataTable());
    ErrorCollector errorCollector = new ErrorCollector();
    DataTable res = DataTableUtils.processBindings(dt, evaluator, errorCollector);
    
    DataTable expected = new SimpleDataTable(tf, 1);
    expected.rec().setValue(VF_TEST_INT, 123);
    expected.rec().setValue(VF_TEST_STR, "ok");
    
    assertEquals(expected, res);
    assertEquals(new ErrorCollector(), errorCollector);
  }
  
  public void testBindingWithError()
  {
    TableFormat tf = VFT_TEST.clone();
    tf.addBinding(VF_TEST_STR, "{" + VF_TEST_INT);
    DataTable dt = new SimpleDataTable(tf, 1);
    Evaluator evaluator = new Evaluator(new SimpleDataTable());
    ErrorCollector errorCollector = new ErrorCollector();
    DataTable res = DataTableUtils.processBindings(dt, evaluator, errorCollector);
    
    DataTable expected = new SimpleDataTable(tf, 1);
    
    assertEquals(expected, res);
    assertEquals(1, errorCollector.getErrors().size());
  }
}
