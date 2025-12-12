package com.tibbo.aggregate.common.expression.function;

import com.tibbo.aggregate.common.expression.function.number.*;
import com.tibbo.aggregate.common.tests.*;

public class TestNumberFunctions extends CommonsTestCase
{
  public void testEqFunction() throws Exception
  {
    int num = 42;
    boolean isEqual = (boolean) new EqFunction().execute(null, null, num, 42);
    
    assertEquals(true, isEqual);
  }
  
  public void testNeFunction() throws Exception
  {
    int num = 42;
    boolean isNotEqual = (boolean) new NeFunction().execute(null, null, num, 40);
    
    assertEquals(true, isNotEqual);
  }
  
  public void testFormatNumberFunction() throws Exception
  {
    float number = 123456.789f;
    String format = "###.##";
    
    String result = (String) new FormatNumberFunction().execute(null, null, number, format);
    
    assertEquals("123456,79", result);
  }
  
  public void testFormatNumberFunctionWithEmptyParams() throws Exception
  {
    String result = (String) new FormatNumberFunction().execute(null, null, null, "");
    
    assertEquals("", result);
  }
  
  public void testGeFunction() throws Exception
  {
    GeFunction geFunction = new GeFunction();
    
    boolean numbersAreEqual = (boolean) geFunction.execute(null, null, 1, 1);
    
    assertEquals(true, numbersAreEqual);
    
    boolean firstNumGtSecondNum = (boolean) geFunction.execute(null, null, 2, 1);
    
    assertEquals(true, firstNumGtSecondNum);
  }
  
  public void testGtFunction() throws Exception
  {
    GtFunction gtFunction = new GtFunction();
    
    boolean firstNumGtSecondNum = (boolean) gtFunction.execute(null, null, 2, 1);
    
    assertEquals(true, firstNumGtSecondNum);
    
    boolean numbersAreEqual = (boolean) gtFunction.execute(null, null, 1, 1);
    
    assertEquals(false, numbersAreEqual);
  }
  
  public void testLeFunction() throws Exception
  {
    LeFunction leFunction = new LeFunction();
    
    boolean firstNumLeSecondNum = (boolean) leFunction.execute(null, null, 1, 2);
    
    assertEquals(true, firstNumLeSecondNum);
    
    boolean numbersAreEqual = (boolean) leFunction.execute(null, null, 1, 1);
    
    assertEquals(true, numbersAreEqual);
  }
  
  public void testLtFunction() throws Exception
  {
    LtFunction ltFunction = new LtFunction();
    
    boolean firstNumLeSecondNum = (boolean) ltFunction.execute(null, null, 1, 2);
    
    assertEquals(true, firstNumLeSecondNum);
    
    boolean numbersAreEqual = (boolean) ltFunction.execute(null, null, 1, 1);
    
    assertEquals(false, numbersAreEqual);
  }
}
