package com.tibbo.aggregate.common.expression;

import java.awt.*;
import java.util.*;

import com.tibbo.aggregate.common.context.*;
import com.tibbo.aggregate.common.datatable.*;
import com.tibbo.aggregate.common.tests.*;
import com.tibbo.aggregate.common.util.*;

public class TestExpressionEvaluation extends CommonsTestCase
{
  
  public void testConstants() throws Exception
  {
    Evaluator ev = getCommonsFixture().createTestEvaluator();
    
    assertEquals(83l, ev.evaluate(new Expression("0123")));
    
    assertEquals(255l, ev.evaluate(new Expression("0xFF")));
    
    assertEquals(6l, ev.evaluate(new Expression("0b00000110")));
    
    assertEquals("тест", ev.evaluate(new Expression("'тест'")));
    
    assertEquals("Unescaping 1: \\ ", ev.evaluate(new Expression("\"Unescaping 1: \\\\ \"")));
    
    assertEquals("Unescaping 2: \\r\\n\\t", ev.evaluate(new Expression("\"Unescaping 2: \\\\r\\\\n\\\\t\"")));
    
    assertEquals("System chars: \r\n", ev.evaluate(new Expression("\"System chars: \\r\\n\"")));
    
    assertEquals("Unicode: \u0000\u0123\uFFFF", ev.evaluate(new Expression("\"Unicode: \\u0000\\u0123\\uFFFF\"")));
    
    // Double quotes may be escaped or not escaped within single-quoted strings
    assertEquals("\"", ev.evaluate(new Expression("'\"'")));
    assertEquals("\"", ev.evaluate(new Expression("'\\\"'")));
    
    // Single quotes must be escaped within single-quoted strings
    assertEquals("'", ev.evaluate(new Expression("'\\''")));
    try
    {
      ev.evaluate(new Expression("'''"));
      fail();
    }
    catch (Exception ex)
    {
    }
    
    // Single quotes may be escaped or not escaped within double-quoted strings
    assertEquals("'", ev.evaluate(new Expression("\"'\"")));
    assertEquals("'", ev.evaluate(new Expression("\"\\'\"")));
    
    // Double quotes must be escaped within double-quoted strings
    assertEquals("\"", ev.evaluate(new Expression("\"\\\"\"")));
    try
    {
      ev.evaluate(new Expression("\"\"\""));
      fail();
    }
    catch (Exception ex)
    {
    }
  }
  
  public void testOperators() throws Exception
  {
    Evaluator ev = getCommonsFixture().createTestEvaluator();
    
    Long lng = (Long) ev.evaluate(new Expression("1 + 1"));
    assertEquals(new Long(2), lng);
    
    assertEquals(5l, ev.evaluate(new Expression("123-118")));
    
    assertTrue(Math.abs((Double) ev.evaluate(new Expression("5-7.2")) + 2.2) < 0.000001);
    
    lng = (Long) ev.evaluate(new Expression("1 | 2"));
    assertEquals(new Long(3), lng);
    
    lng = (Long) ev.evaluate(new Expression("1 & 3"));
    assertEquals(new Long(1), lng);
    
    assertEquals(123l, ev.evaluate(new Expression("false ? {test} : 123")));
    
    assertEquals(5L, ev.evaluate(new Expression("true ? 5 : now()")));
    
    assertEquals(-32, ev.evaluate(new Expression("integer(-128) >> integer(2)")));
    assertEquals(1073741792, ev.evaluate(new Expression("integer(-128) >>> integer(2)")));
    assertEquals(160L, ev.evaluate(new Expression("5 << 5")));
    assertEquals(175L, ev.evaluate(new Expression("5629 >> 5")));
    assertEquals(5629499534213120L, ev.evaluate(new Expression("5 << 50")));
    assertEquals(5L, ev.evaluate(new Expression("5629499534213120 >> 50")));
  }
  
  public void testDefaultTypes() throws Exception
  {
    Evaluator ev = getCommonsFixture().createTestEvaluator();
    
    assertIsInstanceOf(ev.evaluate(new Expression("1"), new EvaluationEnvironment()), Long.class);
    assertIsInstanceOf(ev.evaluate(new Expression("0.1"), new EvaluationEnvironment()), Double.class);
    assertIsInstanceOf(ev.evaluate(new Expression("0.1f"), new EvaluationEnvironment()), Float.class);
  }
  
  public void testAddOperationPreservesTypes() throws Exception
  {
    Evaluator ev = getCommonsFixture().createTestEvaluator();
    
    assertIsInstanceOf(ev.evaluate(new Expression("integer(2) + integer(1)"), new EvaluationEnvironment()), Long.class);
    assertIsInstanceOf(ev.evaluate(new Expression("long(2) + integer(1)"), new EvaluationEnvironment()), Long.class);
    assertIsInstanceOf(ev.evaluate(new Expression("float(2) + 1"), new EvaluationEnvironment()), Double.class);
    assertIsInstanceOf(ev.evaluate(new Expression("double(2) + integer(1)"), new EvaluationEnvironment()), Double.class);
  }
  
  public void testSubtractOperationPreservesTypes() throws Exception
  {
    Evaluator ev = getCommonsFixture().createTestEvaluator();
    
    assertIsInstanceOf(ev.evaluate(new Expression("integer(2) - integer(1)"), new EvaluationEnvironment()), Long.class);
    assertIsInstanceOf(ev.evaluate(new Expression("long(2) - integer(1)"), new EvaluationEnvironment()), Long.class);
    assertIsInstanceOf(ev.evaluate(new Expression("float(2) - 1"), new EvaluationEnvironment()), Double.class);
    assertIsInstanceOf(ev.evaluate(new Expression("double(2) - 1"), new EvaluationEnvironment()), Double.class);
  }
  
  public void testMulOperationsPreservesTypes() throws Exception
  {
    Evaluator ev = getCommonsFixture().createTestEvaluator();
    
    assertIsInstanceOf(ev.evaluate(new Expression("integer(2) * integer(1)"), new EvaluationEnvironment()), Long.class);
    assertIsInstanceOf(ev.evaluate(new Expression("integer(2) * long(1)"), new EvaluationEnvironment()), Long.class);
    assertIsInstanceOf(ev.evaluate(new Expression("float(2) * long(1)"), new EvaluationEnvironment()), Double.class);
    assertIsInstanceOf(ev.evaluate(new Expression("2 * double(1)"), new EvaluationEnvironment()), Double.class);
  }
  
  public void testDivOperationPreservesTypes() throws Exception
  {
    Evaluator ev = getCommonsFixture().createTestEvaluator();
    
    assertIsInstanceOf(ev.evaluate(new Expression("integer(2) / integer(1)"), new EvaluationEnvironment()), Long.class);
    assertIsInstanceOf(ev.evaluate(new Expression("integer(2) / long(1)"), new EvaluationEnvironment()), Long.class);
    assertIsInstanceOf(ev.evaluate(new Expression("float(2) / integer(1)"), new EvaluationEnvironment()), Double.class);
    assertIsInstanceOf(ev.evaluate(new Expression("double(2) / long(1)"), new EvaluationEnvironment()), Double.class);
  }
  
  protected void assertIsInstanceOf(Object anObject, Class expectedClass)
  {
    Class clazz = anObject.getClass();
    assertTrue("Got " + clazz + " instead of " + expectedClass, clazz.equals(expectedClass));
  }
  
  public void testPrecedence() throws Exception
  {
    Evaluator ev = getCommonsFixture().createTestEvaluator();
    
    assertEquals(true, ev.evaluate(new Expression("true && !false")));
    
    assertEquals(true, ev.evaluate(new Expression("5 > 3 && 1 < 2")));
    
    assertEquals(true, ev.evaluate(new Expression("(1 + 1 == 2) ? (5 > 3 && 1 < 2) : (5 == 6)")));
    
    assertEquals(true, ev.evaluate(new Expression("1 + 2 << 3 * 4 == (1 + 2) << (3 * 4)")));
  }
  
  public void testReferenceResolving() throws Exception
  {
    Evaluator ev = getCommonsFixture().createTestEvaluator();
    
    assertEquals(444, ev.evaluate(new Expression("{int[3]}")));
    
    assertEquals("444", ev.evaluate(new Expression("string({int[3]})")));
    
    assertEquals(7, ev.evaluate(new Expression("{#records}")));
  }
  
  public void testMathFunctions() throws Exception
  {
    Evaluator ev = getCommonsFixture().createTestEvaluator();
    
    int dbl = (int) ev.evaluate(new Expression("abs(-123)"));
    assertEquals(123, dbl);
    
    Double lng = (Double) ev.evaluate(new Expression("max(4, 7)"));
    assertEquals(7d, lng);
    
    assertTrue((Double) ev.evaluate(new Expression("abs(-123.456)")) - 123.456 < 0.0001);
    
    assertEquals(123, ev.evaluate(new Expression("abs(-123)")));
    
    assertEquals(true, ev.evaluate(new Expression("lt(4, 5)")));
    assertEquals(true, ev.evaluate(new Expression("ge(5, 5)")));
    assertEquals(false, ev.evaluate(new Expression("ge(4, 5)")));
  }
  
  public void testStringFunctions() throws Exception
  {
    Evaluator ev = getCommonsFixture().createTestEvaluator();
    
    assertEquals("bcd", ev.evaluate(new Expression("substring('abcdef', 1, 4)")));
    
    assertEquals("4", ev.evaluate(new Expression("length('test')")).toString());
    
    assertEquals("-1", ev.evaluate(new Expression("index('test', 'a')")).toString());
    
    assertEquals("1", ev.evaluate(new Expression("index('test', 'e')")).toString());
  }
  
  public void testDateFunctions() throws Exception
  {
    Evaluator ev = getCommonsFixture().createTestEvaluator();
    
    assertEquals(5l, ev.evaluate(new Expression("dateDiff(date(2011, 0, 30, 12, 0, 0), date(2011, 1, 4, 12, 0, 0), \"day\")")));
    
    assertTrue(3580000l < (long) ev.evaluate(new Expression("time(dateAdd(date(2011, 11, 30, 23, 55, 55), 1, \"hour\")) - time(date(2011, 11, 30, 23, 55, 55))")));
  }
  
  public void testDatesAndTimezones() throws Exception
  {
    Evaluator ev = getCommonsFixture().createTestEvaluator();
    Object actual = ev.evaluate(new Expression("date(2016, 5, 1, 0, 0, 0, \"Europe/Moscow\")"));
    
    GregorianCalendar calendar = new GregorianCalendar(TimeZone.getTimeZone("Europe/Moscow"));
    calendar.set(2016, Calendar.JUNE, 1, 0, 0, 0);
    calendar.set(Calendar.MILLISECOND, 0);

    assertEquals(calendar.getTime(), actual);
  }
  
  public void testColorFunctions() throws Exception
  {
    Evaluator ev = getCommonsFixture().createTestEvaluator();
    
    assertEquals(new Color(50, 100, 150), ev.evaluate(new Expression("color(50, 100, 150)")));
    
    assertTrue(((Color) ev.evaluate(new Expression("brighter(color(100, 100, 100))"))).getRed() > 101);
  }
  
  public void testOtherFunctions() throws Exception
  {
    Evaluator ev = getCommonsFixture().createTestEvaluator();
    
    assertEquals(3, ev.evaluate(new Expression("integer(3.141592)")));
  }
  
  public void testComplexExpressions() throws Exception
  {
    Evaluator ev = getCommonsFixture().createTestEvaluator();
    
    assertEquals("test\ntest", ev.evaluate(new Expression("'test\\ntest'")));
    
    assertEquals(false, ev.evaluate(new Expression("false && {test}")));
    
    assertEquals(true, ev.evaluate(new Expression("true || {test}")));
    
    assertEquals("xxx", ev.evaluate(new Expression("((length('abcde') == 5) && (length('test') == 4)) ? 'xxx' : 'yyy'")).toString());
  }
  
  public void testComments() throws Exception
  {
    Evaluator ev = getCommonsFixture().createTestEvaluator();
    
    assertEquals(15L, ev.evaluate(new Expression("/*start comment*/1 + /*%^//$internal comment*&#*/ 2 + //end line comment\n 3 + 4/*end comment*/\n+5//abracadabra")));
    
    assertEquals("/*not a comment*/", ev.evaluate(new Expression("'/*not a comment*/'/*a comment*/")));
  }
  
  public void testCommentsOnly() throws Exception
  {
    Evaluator ev = getCommonsFixture().createTestEvaluator();
    
    Object result = ev.evaluate(new Expression("//2+2"));
    assertNull(result);
    
    result = ev.evaluate(new Expression("/*{.:}*/"));
    assertNull(result);
    
    result = ev.evaluate(new Expression("//comment1 /*comment2*/"));
    assertNull(result);
  }
  
  public void testAttributedEvaluation() throws Exception
  {
    Date timestamp = new Date();
    
    Evaluator ev = getCommonsFixture().createTestEvaluator();
    
    ev.getDefaultResolver().getDefaultTable().setQuality(Quality.GOOD_NON_SPECIFIC);
    ev.getDefaultResolver().getDefaultTable().setTimestamp(timestamp);
    
    AttributedObject result = ev.evaluateAttributed(new Expression("({int} * 2) + 1"));
    
    assertEquals(result.getQuality(), (Object) Quality.GOOD_NON_SPECIFIC);
    
  }
  
  public void testBinding() throws Exception
  {
    Evaluator ev = new Evaluator(new DefaultReferenceResolver());
    
    StubContext stubContext = new StubContext("context");
    
    TableFormat tf = new TableFormat(FieldFormat.create("test", FieldFormat.INTEGER_FIELD));
    tf.addBinding("test#choices", "table('<<value><I>><<description><S>>', 1, 'one', 2, 'two',3 , 'three')");
    tf.addBinding("test#choices", "table('<<value><I>><<description><S>>', 1, 'one_1', 2, 'two_2',3 , 'three_3')");
    stubContext.addVariableDefinition(new VariableDefinition("test", tf, true, true));
    
    stubContext.setVariable("test", new SimpleDataTable(tf, (Object) 2));
    
    ev.getDefaultResolver().setDefaultContext(stubContext);
    
    Object result = ev.evaluate(new Expression("{.:test$test#svdesc}"));
    
    assertEquals("two_2", result);
  }
}
