package com.tibbo.aggregate.common.expression.function;

import java.time.*;
import java.util.*;

import com.tibbo.aggregate.common.context.*;
import com.tibbo.aggregate.common.data.*;
import com.tibbo.aggregate.common.expression.*;
import com.tibbo.aggregate.common.expression.function.type.*;
import com.tibbo.aggregate.common.tests.*;

public class TestTypeConversionFunctions extends CommonsTestCase
{
  
  public static final long RELEASE_DATE = 1630443600000L;
  
  public void testLongFunction() throws Exception
  {
    Evaluator ev = CommonsFixture.createTestEvaluator();
    
    Object res = ev.evaluate(new Expression("long(123456789.0)"));
    
    assertEquals(123456789L, res);
  }
  
  public void testLongFunctionWithOneParam() throws Exception
  {
    long result = (long) new LongFunction().execute(null, null, true);
    
    assertEquals(1L, result);
  }
  
  public void testLongFunctionWithSeveralParameters() throws Exception
  {
    String radix = "16";
    long expected = 327680;
    
    long result = (long) new LongFunction().execute(null, null, "50000", radix);
    
    assertEquals(expected, result);
  }
  
  public void testBooleanFunction() throws Exception
  {
    boolean isTrue = (boolean) new BooleanFunction().convert("true");
    
    assertEquals(true, isTrue);
  }
  
  public void testDoubleFunction() throws Exception
  {
    double result = (double) new DoubleFunction().convert("42");
    
    assertEquals(42d, result);
  }
  
  public void testFloatFunction() throws Exception
  {
    float result = (float) new FloatFunction().convert("42.2");
    
    assertEquals(42.2f, result);
  }
  
  public void testIntegerFunction() throws Exception
  {
    int result = (int) new IntegerFunction().execute(null, null, "10000");
    
    assertEquals(10000, result);
  }
  
  public void testIntegerFunctionWithSeveralParameters() throws Exception
  {
    String radix = "2";
    int expected = 4;
    
    int result = (int) new IntegerFunction().execute(null, null, "100", radix);
    
    assertEquals(expected, result);
  }
  
  public void testStringFunction() throws Exception
  {
    byte[] bytes = { 65, 66, 67 };
    Data data = new Data(bytes);
    String result = (String) new StringFunction().convert(data);
    
    assertEquals("ABC", result);
  }
  
  public void testStringFunctionWithNullParam() throws Exception
  {
    Object nullable = new StringFunction().convert(null);
    
    assertNull(nullable);
  }
  
  public void testTimestampFunction() throws Exception
  {
    Date date = (Date) new TimestampFunction().convert(RELEASE_DATE);
    assertNotNull(date);
    assertEquals(new Date(RELEASE_DATE), date);
  }
  
  public void testFetchDataBlockFunction() throws Exception
  {
    Evaluator ev = CommonsFixture.createTestEvaluator();
    StubContext context = new StubContext("root");
    DefaultContextManager contextManager = new DefaultContextManager(context, true);
    ev.getDefaultResolver().setDefaultContext(context);
    ev.getDefaultResolver().setContextManager(contextManager);
    ev.getDefaultResolver().setCallerController(contextManager.getCallerController());
    
    Data result = (Data) new FetchDataBlockFunction().execute(ev, null, new Data(new byte[] { 65, 66, 67 }));
    
    assertEquals(3, result.getData().length);
  }
}
