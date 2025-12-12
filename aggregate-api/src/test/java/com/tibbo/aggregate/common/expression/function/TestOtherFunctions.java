package com.tibbo.aggregate.common.expression.function;

import com.tibbo.aggregate.common.context.DefaultContextManager;
import com.tibbo.aggregate.common.context.UncheckedCallerController;
import com.tibbo.aggregate.common.datatable.DataTable;
import com.tibbo.aggregate.common.datatable.FieldFormat;
import com.tibbo.aggregate.common.datatable.SimpleDataTable;
import com.tibbo.aggregate.common.datatable.TableFormat;
import com.tibbo.aggregate.common.expression.EvaluationEnvironment;
import com.tibbo.aggregate.common.expression.EvaluationException;
import com.tibbo.aggregate.common.expression.Evaluator;
import com.tibbo.aggregate.common.expression.Expression;
import com.tibbo.aggregate.common.expression.Function;
import com.tibbo.aggregate.common.expression.function.other.AndFunction;
import com.tibbo.aggregate.common.expression.function.other.CaseFunction;
import com.tibbo.aggregate.common.expression.function.other.EvaluateFunction;
import com.tibbo.aggregate.common.expression.function.other.FailFunction;
import com.tibbo.aggregate.common.expression.function.other.IfFunction;
import com.tibbo.aggregate.common.expression.function.other.LoginFunction;
import com.tibbo.aggregate.common.expression.function.other.OrFunction;
import com.tibbo.aggregate.common.expression.function.other.TableFromCSVFunction;
import com.tibbo.aggregate.common.expression.function.other.TraceFunction;
import com.tibbo.aggregate.common.expression.function.other.UserFunction;
import com.tibbo.aggregate.common.expression.function.other.XPathFunction;
import com.tibbo.aggregate.common.expression.function.string.FormatFunction;
import com.tibbo.aggregate.common.expression.function.table.AggregateFunction;
import com.tibbo.aggregate.common.tests.CommonsFixture;
import com.tibbo.aggregate.common.tests.CommonsTestCase;
import com.tibbo.aggregate.common.tests.StubContext;
import com.tibbo.aggregate.common.util.StringUtils;
import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.util.Date;

public class TestOtherFunctions extends CommonsTestCase
{
  
  private Evaluator ev;
  
  @Override
  public void setUp() throws Exception
  {
    super.setUp();
    ev = CommonsFixture.createTestEvaluator();
  }
  
  public void testStoreAndLoadFunctions() throws Exception
  {
    Object res = ev.evaluate(new Expression("st('var', 11 + 22 + 33) > 50 ? ld('var') : -1"), new EvaluationEnvironment());
    
    assertEquals(66L, res);
    
    res = ev.evaluate(new Expression("st(\"list\", 123) != null ? ld(\"list\") : null"), new EvaluationEnvironment());
    
    assertEquals(123L, res);
  }
  
  public void testCatchFunction() throws Exception
  {
    Object res = ev.evaluate(new Expression("catch({.:invalidPathContext}, \"error\")"), new EvaluationEnvironment());
    
    assertEquals("error", res);
    
    res = ev.evaluate(new Expression("catch(1/0)"), new EvaluationEnvironment());
    assertEquals("/ by zero", res);
    
    res = ev.evaluate(new Expression("evaluate(\"'result: ' + string(catch(1/0, 'error'))\")"), new EvaluationEnvironment());
    
    assertEquals("result: error", res);
    
    res = ev.evaluate(new Expression("evaluate(\"'result: ' + string(catch(1/0))\")"), new EvaluationEnvironment());
    
    assertEquals("result: / by zero", res);
  }
  
  public void testCatchWithAggregateFunction() throws Exception
  {
    Evaluator ev = CommonsFixture.createTestEvaluator();
    
    TableFormat format = new TableFormat();
    format.addField("<str><S>");
    DataTable table = new SimpleDataTable(format);
    table.addRecord("0");
    table.addRecord("1");
    table.addRecord("q");
    table.addRecord("3");
    
    String exp1 = "{env/previous} + catch(evaluate(\"string(integer(string({str})))\"), \"err\")";
    
    Object res = new AggregateFunction().execute(ev, null, table, exp1, "start");
    
    assertEquals("start01err3", res);
  }
  
  public void testCatchWithAggregateAndAddRecordFunctions() throws Exception
  {
    Evaluator ev = CommonsFixture.createTestEvaluator();
    
    String exp = "aggregate(table('<<val><I>>', '2', '4', '1', '0'), \"addRecords({env/previous}, catch(2 + 100/integer(cell(dt(), 'val', {#row})), 10))\", table('<<val><I>>'))";
    
    DataTable res = (DataTable) ev.evaluate(new Expression(exp), new EvaluationEnvironment());
    
    assertEquals(4, (int) res.getRecordCount());
    assertEquals(52, res.getRecord(0).getInt(0).intValue());
    assertEquals(27, res.getRecord(1).getInt(0).intValue());
    assertEquals(102, res.getRecord(2).getInt(0).intValue());
    assertEquals(10, res.getRecord(3).getInt(0).intValue());
  }
  
  public void testCatchWithAggregateAndAddRecordFunctions_Hard() throws Exception
  {
    Evaluator ev = CommonsFixture.createTestEvaluator();
    
    String expCatch = "catch(2 + 100/(integer(cell(dt(), 'val', {#row}))), 10)";
    String expCatch2 = "(" + expCatch + ") > 100 ? (" + expCatch + ") : (" + expCatch + ")";
    String exp = "aggregate(table('<<val><I>>', '2', '4', '1', '0'), \"addRecords({env/previous}, " + expCatch2 + " )\", table('<<val><I>>'))";
    
    DataTable res = (DataTable) ev.evaluate(new Expression(exp), new EvaluationEnvironment());
    
    assertEquals(4, (int) res.getRecordCount());
    assertEquals(52, res.getRecord(0).getInt(0).intValue());
    assertEquals(27, res.getRecord(1).getInt(0).intValue());
    assertEquals(102, res.getRecord(2).getInt(0).intValue());
    assertEquals(10, res.getRecord(3).getInt(0).intValue());
  }
  
  public void testXPathFunction1() throws Exception
  {
    String[] values = { "273" };
    
    String par1 = String.format("<val1>%1$s</val1>", values[0]);
    String par2 = "val1";
    boolean par3 = false;
    
    Object res = new XPathFunction().execute(ev, new EvaluationEnvironment(), par1, par2, par3);
    
    assertEquals(res, par1 + System.lineSeparator());
  }
  
  public void testXPathFunction2() throws Exception
  {
    String[] values = { "0000", "566", "0.000", "-273.000" };
    
    String par1 = String.format("<response><val1>%1$s</val1><val2>%2$s</val2><val3>%3$s</val3><val4>%4$s</val4></response>", values[0], values[1], values[2], values[3]);
    String par2 = "response";
    boolean par3 = true;
    
    DataTable res = (DataTable) new XPathFunction().execute(ev, new EvaluationEnvironment(), par1, par2, par3);
    DataTable childrenTable = res.rec().getDataTable(XPathFunction.FIELD_NODE_CHILDREN);
    Object val1 = childrenTable.getRecord(0).getValue(XPathFunction.FIELD_NODE_CONTENT);
    Object val2 = childrenTable.getRecord(1).getValue(XPathFunction.FIELD_NODE_CONTENT);
    Object val3 = childrenTable.getRecord(2).getValue(XPathFunction.FIELD_NODE_CONTENT);
    Object val4 = childrenTable.getRecord(3).getValue(XPathFunction.FIELD_NODE_CONTENT);
    
    assertEquals(val1, values[0]);
    assertEquals(val2, values[1]);
    assertEquals(val3, values[2]);
    assertEquals(val4, values[3]);
  }
  
  public void testCSVFunction() throws Exception
  {
    // format is not specified
    
    String aCsvWithHeader = "col1;col2\n" + "val11;val12\n" + "val21;val22\n";
    
    Object res = new TableFromCSVFunction().execute(ev, null, aCsvWithHeader, "names", ";");
    DataTable dataTable = (DataTable) res;
    
    assertEquals(2, dataTable.getFieldCount());
    assertTrue(dataTable.hasField("col1"));
    assertTrue(dataTable.hasField("col2"));
    
    assertEquals("val11", dataTable.getRecord(0).getValue("col1"));
    assertEquals("val12", dataTable.getRecord(0).getValue("col2"));
    assertEquals("val21", dataTable.getRecord(1).getValue("col1"));
    assertEquals("val22", dataTable.getRecord(1).getValue("col2"));
    
    res = new TableFromCSVFunction().execute(ev, null, aCsvWithHeader, "descriptions", ";");
    dataTable = (DataTable) res;
    
    assertEquals(2, dataTable.getFieldCount());
    
    assertEquals("col1", dataTable.getFormat("0").getDescription());
    assertEquals("col2", dataTable.getFormat("1").getDescription());
    
    assertEquals("val11", dataTable.getRecord(0).getValue("0"));
    assertEquals("val22", dataTable.getRecord(1).getValue("1"));
    
    // format is specified
    
    String format = "<<col_1><I>><<col_2><F>>";
    String aCsvWithoutHeader = "111,111.1\n" + "222,222.2\n";
    
    res = new TableFromCSVFunction().execute(ev, null, aCsvWithoutHeader, "none", ",", format);
    dataTable = (DataTable) res;
    
    assertEquals(2, dataTable.getFieldCount());
    
    assertEquals(111, dataTable.getRecord(0).getValue("col_1"));
    assertTrue(Math.abs(111.1 - (Float) dataTable.getRecord(0).getValue("col_2")) < 0.00001);
    assertEquals(222, dataTable.getRecord(1).getValue("col_1"));
    assertTrue(Math.abs(222.2 - (Float) dataTable.getRecord(1).getValue("col_2")) < 0.00001);
    
    res = new TableFromCSVFunction().execute(ev, null, aCsvWithoutHeader, "skip", ",", format);
    dataTable = (DataTable) res;
    
    assertEquals(2, dataTable.getFieldCount());
    
    assertEquals(222, dataTable.getRecord(0).getValue("col_1"));
    assertTrue(Math.abs(222.2 - (Float) dataTable.getRecord(0).getValue("col_2")) < 0.00001);
    
    // test for the case when a character used as the delimiter has to be escaped (format is not specified)
    
    aCsvWithHeader = "col1\\;col111;col2\n" + "val11\\;val111;val12\n" + "val21;val22\n";
    
    res = new TableFromCSVFunction().execute(ev, null, aCsvWithHeader, "names", ";");
    dataTable = (DataTable) res;
    
    assertEquals(2, dataTable.getFieldCount());
    assertTrue(dataTable.hasField("col1;col111"));
    assertTrue(dataTable.hasField("col2"));
    
    assertEquals("val11;val111", dataTable.getRecord(0).getValue("col1;col111"));
    
    // test qualifier, escapeMod, comment
    
    String qFormat = "<<col_1><S>> <<col_2><S>>";
    String aCSVWithParam = "!ddddd\n" + "aaa,\"bb\\\"bb\\\"bb\"\n" + "\"!ee\",fff!fff\n";
    res = new TableFromCSVFunction().execute(ev, null, aCSVWithParam, "none", ",", qFormat, "\"", 2, "!");
    dataTable = (DataTable) res;
    
    assertEquals(2, dataTable.getFieldCount());
    assertTrue(dataTable.hasField("col_1"));
    assertEquals("aaa", dataTable.getRecord(0).getValue("col_1"));
    assertEquals("bb\"bb\"bb", dataTable.getRecord(0).getValue("col_2"));
    assertEquals("!ee", dataTable.getRecord(1).getValue("col_1"));
    assertEquals("fff!fff", dataTable.getRecord(1).getValue("col_2"));
  }
  
  public void testCSVFunctionWithExportFromFileWithBOM() throws Exception
  {
    InputStream is = getClass().getResourceAsStream("/data/test_bom_file.csv");
    assertNotNull(is);
    String dataWithHeader = IOUtils.toString(is, StringUtils.UTF8_CHARSET);
    
    Object res = new TableFromCSVFunction().execute(ev, null, dataWithHeader, "names", ",");
    DataTable dataTable = (DataTable) res;
    
    /*
     * Note: BOM should not be added to the beginning of a first column name.
     * @see <a href="https://www.rgagnon.com/javadetails/java-handle-utf8-file-with-bom.html">Java ignores BOM</a>
     */
    assertTrue(dataTable.hasField("col1"));
  }
  
  public void testEvaluateFunction() throws Exception
  {
    Object result = new EvaluateFunction().execute(ev, new EvaluationEnvironment(), "dt()");
    
    assertEquals(ev.getDefaultResolver().getDefaultTable(), result);
  }
  
  public void testEvaluateFunctionWithAdditionalParams() throws Exception
  {
    StubContext context = new StubContext("root");
    context.addChild(new StubContext("child"));
    DataTable defaultTable = ev.getDefaultResolver().getDefaultTable();
    DefaultContextManager contextManager = new DefaultContextManager(context, true);
    ev.getDefaultResolver().setContextManager(contextManager);
    
    DataTable result = (DataTable) new EvaluateFunction().execute(ev, new EvaluationEnvironment(), "dt()", "", defaultTable, 0);
    
    assertEquals(defaultTable, result);
  }
  
  public void testFormatFunction() throws Exception
  {
    String pattern = "%1$tm %1$te,%1$tY";
    Date date = new Date(1521705584);
    String expected = "01 18,1970";
    
    String result = (String) new FormatFunction().execute(ev, null, pattern, date);
    
    assertEquals(expected, result);
  }
  
  public void testTraceFunction() throws Exception
  {
    String value = "Trace value";
    
    String result = (String) new TraceFunction().execute(ev, null, value);
    
    assertEquals(value, result);
  }
  
  public void testUserFunction() throws Exception
  {
    String username = "Test username";
    ev.getDefaultResolver().setCallerController(new UncheckedCallerController(username));
    
    String result = (String) new UserFunction().execute(ev, null);
    
    assertEquals(username, result);
  }
  
  public void testJavaConstructorFunction() throws Exception
  {
    Date date = (Date) new JavaConstructorFunction("date", Date.class.getName(), Function.GROUP_DATE_TIME_PROCESSING, "Date", "")
        .execute(null, null);
    
    assertNotNull(date);
  }
  
  public void testLoginFunction() throws Exception
  {
    String username = "Test username";
    ev.getDefaultResolver().setCallerController(new UncheckedCallerController(username));
    
    String result = (String) new LoginFunction().execute(ev, null);
    
    assertEquals(username, result);
  }
  
  public void testExpressionEditorOptions() throws Exception
  {
    StubContext root = new StubContext("root");
    root.addChild(new StubContext("child"));
    DefaultContextManager contextManager = new DefaultContextManager(root, true);
    
    ev.getDefaultResolver().setContextManager(contextManager);
    ev.getDefaultResolver().setCallerController(contextManager.getCallerController());
    ev.getDefaultResolver().setDefaultContext(root);
    
    TableFormat tf = new TableFormat(1, 1);
    tf.addField(FieldFormat.create("<int><I>"));
    tf.addField(FieldFormat.create("<reference><S>"));
    tf.addField(FieldFormat.create("<description><S><D=description>"));
    DataTable table = new SimpleDataTable(tf, true);
    
    ExpressionEditorOptionsFunction function = new ExpressionEditorOptionsFunction();
    String result = (String) function.execute(ev, null, "child", "str", "1", table);
    
    assertNotNull(result);
    assertFalse(result.isEmpty());
  }
  
  public void testCaseFunction() throws Exception
  {
    assertEquals(1, new CaseFunction().execute(ev, null, 1, false, 2));
    assertEquals(2, new CaseFunction().execute(ev, null, 1, true, 2));
    assertEquals(4, new CaseFunction().execute(ev, null, 1, false, 2, false, 3, true, 4));
    assertEquals(3, new CaseFunction().execute(ev, null, 1, false, 2, true, 3, true)); // Incomplete condition/result pair ignored
    
  }
  
  public void testLogicalFunctions() throws Exception
  {
    assertEquals(true, new AndFunction().execute(ev, null, true));
    assertEquals(false, new AndFunction().execute(ev, null, false));
    assertEquals(false, new AndFunction().execute(ev, null, true, true, false));
    assertEquals(false, new AndFunction().execute(ev, null, true, true, false, true, true));
    assertEquals(true, new AndFunction().execute(ev, null, true, true, true, true, true));
    
    assertEquals(true, new OrFunction().execute(ev, null, true));
    assertEquals(false, new OrFunction().execute(ev, null, false));
    assertEquals(true, new OrFunction().execute(ev, null, false, true, false));
    assertEquals(true, new OrFunction().execute(ev, null, true, true, false, true, true));
    assertEquals(false, new OrFunction().execute(ev, null, false, false, false, false, false));
    
    assertEquals(1, new IfFunction().execute(ev, null, true, 1, 2));
    assertEquals(2, new IfFunction().execute(ev, null, false, 1, 2));
  }
  
  public void testFailFunction() throws Exception
  {
    try
    {
      assertEquals(null, new FailFunction().execute(ev, null, true, "Test"));
      throw new Exception("fail() didn't generate an exception");
    }
    catch (EvaluationException e)
    {
      assertTrue(e.getMessage().contains("Test"));
    }

    assertEquals(null, new FailFunction().execute(ev, null, false, "Test"));
    assertEquals(123, new FailFunction().execute(ev, null, false, "Test", 123));
  }
}
