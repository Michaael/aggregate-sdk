package com.tibbo.aggregate.common.expression;

import java.awt.*;

import com.tibbo.aggregate.common.expression.function.AbstractFunction;
import com.tibbo.aggregate.common.tests.CommonsFixture;
import com.tibbo.aggregate.common.tests.CommonsTestCase;

public class TestExpressions extends CommonsTestCase
{
  
  private Evaluator ev;
  
  public void setUp() throws Exception
  {
    super.setUp();
    ev = CommonsFixture.createTestEvaluator();
  }
  
  public void testGeExpressionEqualsCondition() throws Exception
  {
    boolean isTrue = (boolean) ev.evaluate(new Expression("1 >= 1"));
    
    assertEquals(true, isTrue);
  }
  
  public void testGeExpressionLargerCondition() throws Exception
  {
    boolean isTrue = (boolean) ev.evaluate(new Expression("4 >= 2"));
    
    assertEquals(true, isTrue);
  }
  
  public void testGeExpressionFalseCondition() throws Exception
  {
    boolean isFalse = (boolean) ev.evaluate(new Expression("4 >= 42"));
    
    assertEquals(false, isFalse);
  }
  
  public void testGeExpressionStringCondition() throws Exception
  {
    boolean isFalse = (boolean) ev.evaluate(new Expression("\"a\" >= \"b\""));
    
    assertEquals(false, isFalse);
  }
  
  public void testGeExpressionDoubleCondition() throws Exception
  {
    boolean isTrue = (boolean) ev.evaluate(new Expression("2.1 > 1.1"));
    
    assertEquals(true, isTrue);
  }
  
  public void testGtExpressionTrueCondition() throws Exception
  {
    boolean isTrue = (boolean) ev.evaluate(new Expression("2 > 1"));
    
    assertEquals(true, isTrue);
  }
  
  public void testGtExpressionFalseCondition() throws Exception
  {
    boolean isFalse = (boolean) ev.evaluate(new Expression("1 > 1"));
    
    assertEquals(false, isFalse);
  }
  
  public void testGtExpressionDoubleCondition() throws Exception
  {
    boolean isFalse = (boolean) ev.evaluate(new Expression("0.1 > 1.1"));
    
    assertEquals(false, isFalse);
  }
  
  public void testGtExpressionStringCondition() throws Exception
  {
    boolean isTrue = (boolean) ev.evaluate(new Expression("\"bb\" > \"aa\""));
    
    assertEquals(true, isTrue);
  }
  
  public void testLeExpressionTrueCondition() throws Exception
  {
    boolean isTrue = (boolean) ev.evaluate(new Expression(" 1 <= 1"));
    
    assertEquals(true, isTrue);
  }
  
  public void testLeExpressionFalseCondition() throws Exception
  {
    boolean isFalse = (boolean) ev.evaluate(new Expression("4 <= 2"));
    
    assertEquals(false, isFalse);
  }
  
  public void testLeExpressionDoubleCondition() throws Exception
  {
    boolean isTrue = (boolean) ev.evaluate(new Expression("5.5 <= 6.6"));
    
    assertEquals(true, isTrue);
  }
  
  public void testLeExpressionStringCondition() throws Exception
  {
    boolean isFalse = (boolean) ev.evaluate(new Expression("\"worlds\" <= \"world\""));
    
    assertEquals(false, isFalse);
  }
  
  public void testLtExpressionTrueCondition() throws Exception
  {
    boolean isTrue = (boolean) ev.evaluate(new Expression("1 < 2"));
    
    assertEquals(true, isTrue);
  }
  
  public void testLtExpressionFalseCondition() throws Exception
  {
    boolean isFalse = (boolean) ev.evaluate(new Expression(" 42 < 24"));
    
    assertEquals(false, isFalse);
  }
  
  public void testLtExpressionDoubleCondition() throws Exception
  {
    boolean isTrue = (boolean) ev.evaluate(new Expression("4.47 < 5.559587"));
    
    assertEquals(true, isTrue);
  }
  
  public void testLtExpressionStringCondition() throws Exception
  {
    boolean isTrue = (boolean) ev.evaluate(new Expression("\"abc\" < \"bca\""));
    
    assertEquals(true, isTrue);
  }
  
  public void testNullCondition() throws Exception
  {
    boolean isFalse = (boolean) ev.evaluate(new Expression("null > null"));
    
    assertEquals(false, isFalse);
  }
  
  public void testNullConditionOfFirstVariable() throws Exception
  {
    boolean isTrue = (boolean) ev.evaluate(new Expression("null < 1"));
    
    assertEquals(true, isTrue);
  }
  
  public void testNullConditionOfSecondVariable() throws Exception
  {
    boolean isTrue = (boolean) ev.evaluate(new Expression("24 <= null"));
    
    assertEquals(true, isTrue);
  }
  
  public void testFloatingPointCondition() throws Exception
  {
    boolean isTrue = (boolean) ev.evaluate(new Expression("float(2.3) >= 2.1"));
    
    assertEquals(true, isTrue);
  }
  
  public void testRegexMatchExpression() throws Exception
  {
    boolean isFalse = (boolean) ev.evaluate(new Expression("\"abXXXX\" ~= \"^abc.*\""));
    
    assertEquals(false, isFalse);
  }
  
  public void testRegexMatchWithNullParamExpression() throws Exception
  {
    String expected = "^abc.*";
    String result = (String) ev.evaluate(new Expression("null ~= \"^abc.*\""));
    
    assertEquals(expected, result);
  }

  public void testRegexMatchWithExactMatchExpression() throws Exception
  {
    Boolean result = (Boolean) ev.evaluate(new Expression(" 'Example' ~= 'Exam' "));

    assertFalse(result);
  }
  
  public void testAddExpression() throws Exception
  {
    String expected = "test123";
    String result = (String) ev.evaluate(new Expression("\"test\" + 123"));
    
    assertEquals(expected, result);
  }
  
  public void testAddNullParamExpression() throws Exception
  {
    Object nullable = ev.evaluate(new Expression("123 + null"));
    
    assertNull(nullable);
  }
  
  public void testEqExpression() throws Exception
  {
    boolean isTrue = (boolean) ev.evaluate(new Expression(" 1 == 1"));
    
    assertEquals(true, isTrue);
  }
  
  public void testFalseExpression() throws Exception
  {
    boolean isFalse = (boolean) ev.evaluate(new Expression("false"));
    
    assertEquals(false, isFalse);
  }
  
  public void testTrueExpression() throws Exception
  {
    boolean isTrue = (boolean) ev.evaluate(new Expression("true"));
    
    assertEquals(true, isTrue);
  }
  
  public void testNullExpression() throws Exception
  {
    Object nullable = ev.evaluate(new Expression("null"));
    
    assertNull(nullable);
  }
  
  public void testFunctionExpression() throws Exception
  {
    String expected = "mile";
    String result = (String) ev.evaluate(new Expression("substring(\"smiles\", 1, 5)"));
    
    assertEquals(expected, result);
  }
  
  public void testCustomFunctionExpression() throws Exception
  {
    ev.registerCustomFunction("funcBlueColor", new AbstractFunction("funcBlueColor", Function.GROUP_COLOR_PROCESSING,
        "Integer red, Integer green, Integer blue", "Color", "")
    {
      @Override
      public Object execute(Evaluator evaluator, EvaluationEnvironment environment, Object... parameters) throws EvaluationException
      {
        return Color.BLUE;
      }
    });
    Color expected = Color.BLUE;
    Color result = (Color) ev.evaluate(new Expression("funcBlueColor()"));
    
    assertEquals(expected, result);
  }
  
  public void testBitwiseAndExpression() throws Exception
  {
    long expected = 0;
    long result = (long) ev.evaluate(new Expression("1 & 0"));
    
    assertEquals(expected, result);
  }
  
  public void testBitwiseAndExpressionWithIntegerVariables() throws Exception
  {
    int expected = 0;
    int result = (int) ev.evaluate(new Expression("integer(0) & integer(1)"));
    
    assertEquals(expected, result);
  }
  
  public void testBitwiseAndExpressionWithNullVariable() throws Exception
  {
    Object nullable = ev.evaluate(new Expression("null & 1"));
    
    assertNull(nullable);
  }
  
  public void testBitwiseNotExpression() throws Exception
  {
    long expected = -1;
    long result = (long) ev.evaluate(new Expression("~0x00000000"));
    
    assertEquals(expected, result);
  }
  
  public void testBitwiseNotExpressionWithIntegerVariable() throws Exception
  {
    int expected = -1;
    int result = (int) ev.evaluate(new Expression("~integer(0)"));
    
    assertEquals(expected, result);
  }
  
  public void testBitwiseNotExpressionWithNullVariable() throws Exception
  {
    Object nullable = ev.evaluate(new Expression("~null"));
    
    assertNull(nullable);
  }
  
  public void testBitwiseOrExpression() throws Exception
  {
    long expected = 61;
    long result = (long) ev.evaluate(new Expression("60|13"));
    
    assertEquals(expected, result);
  }
  
  public void testBitwiseOrExpressionWithIntegerVariables() throws Exception
  {
    int expected = 61;
    int result = (int) ev.evaluate(new Expression("integer(60)|integer(13)"));
    
    assertEquals(expected, result);
  }
  
  public void testBitwiseOrExpressionWithNullVariable() throws Exception
  {
    Object nullable = ev.evaluate(new Expression("null|13"));
    
    assertNull(nullable);
  }
  
  public void testBitwiseXorExpression() throws Exception
  {
    long expected = 49;
    long result = (long) ev.evaluate(new Expression("60^13"));
    
    assertEquals(expected, result);
  }
  
  public void testBitwiseXorExpressionWithIntegerVariables() throws Exception
  {
    int expected = 49;
    int result = (int) ev.evaluate(new Expression("integer(60)^integer(13)"));
    
    assertEquals(expected, result);
  }
  
  public void testBitwiseExpressionWithNullVariable() throws Exception
  {
    Object nullable = ev.evaluate(new Expression("60^null"));
    
    assertNull(nullable);
  }
  
  public void testLeftShiftExpression() throws Exception
  {
    long expected = 4;
    long result = (long) ev.evaluate(new Expression("2 << 1"));
    
    assertEquals(expected, result);
  }
  
  public void testLeftShiftIntExpression() throws Exception
  {
    int expected = 4;
    int result = (int) ev.evaluate(new Expression("integer(2) << integer(1)"));
    
    assertEquals(expected, result);
  }
  
  public void testLeftShiftExpressionWithNullVariable() throws Exception
  {
    Object nullable = ev.evaluate(new Expression("2 << null"));
    
    assertNull(nullable);
  }
  
  public void testRightShiftExpression() throws Exception
  {
    long expected = 1;
    long result = (long) ev.evaluate(new Expression("2 >> 1"));
    
    assertEquals(expected, result);
  }
  
  public void testRightShiftExpressionWithNullVariable() throws Exception
  {
    Object nullable = ev.evaluate(new Expression("null >> 1"));
    
    assertNull(nullable);
  }
  
  public void testUnsignedRightShiftExpression() throws Exception
  {
    long expected = 15;
    long result = (long) ev.evaluate(new Expression("60>>>2"));
    
    assertEquals(expected, result);
  }
  
  public void testUnsignedRightShiftExpressionWithNullVariable() throws Exception
  {
    Object nullable = ev.evaluate(new Expression("60>>>null"));
    
    assertNull(nullable);
  }
  
  public void testLogicalAndExpression() throws Exception
  {
    boolean isTrue = (boolean) ev.evaluate(new Expression("1 < 2 && 2 == 2"));
    
    assertEquals(true, isTrue);
  }
  
  public void testLogicalOrExpression() throws Exception
  {
    boolean isTrue = (boolean) ev.evaluate(new Expression("1 > 2 || 1 == 1"));
    
    assertEquals(true, isTrue);
  }
  
  public void testNeExpression() throws Exception
  {
    boolean isTrue = (boolean) ev.evaluate(new Expression("true != false"));
    
    assertEquals(true, isTrue);
  }
  
  public void testDivExpression() throws Exception
  {
    double expected = 10.05;
    double result = (double) ev.evaluate(new Expression("100.5 / 10"));
    
    assertEquals(expected, result);
  }
  
  public void testDivExpressionWithNullVariable() throws Exception
  {
    Object nullable = ev.evaluate(new Expression("null/12"));
    
    assertNull(nullable);
  }
  
  public void testSubtractExpression() throws Exception
  {
    long expected = 42;
    long result = (long) ev.evaluate(new Expression("43 - 1"));
    
    assertEquals(expected, result);
  }
  
  public void testSubtractExpressionWithNullVariable() throws Exception
  {
    Object nullable = ev.evaluate(new Expression("1 - null"));
    
    assertNull(nullable);
  }
  
  public void testMulExpression() throws Exception
  {
    long expected = 42;
    long result = (long) ev.evaluate(new Expression("42 * 1"));
    
    assertEquals(expected, result);
  }
  
  public void testMulExpressionWithNullVariable() throws Exception
  {
    Object nullable = ev.evaluate(new Expression("null * 134"));
    
    assertNull(nullable);
  }
  
  public void testModExpression() throws Exception
  {
    long expected = 4;
    long result = (long) ev.evaluate(new Expression("24.42 % 10"));
    
    assertEquals(expected, result);
  }
  
  public void testModExpression2() throws Exception
  {
    long expected = 4;
    long result = (long) ev.evaluate(new Expression("24.92 % 10"));
    
    assertEquals(expected, result);
  }

  public void testFloatExpression() throws Exception
  {
    assertEquals(3.4028235e38f, ev.evaluate(new Expression("3.4028235E38f")));
    assertEquals(3.4028235e38f, ev.evaluate(new Expression("3.4028235e38f")));
   // assertEquals(3.4028235e38f, ev.evaluate(new Expression("3.4028235E38F")));
   // assertEquals(3.4028235e38f, ev.evaluate(new Expression("3.4028235e38F")));
    assertEquals(3.4028235e38, ev.evaluate(new Expression("3.4028235E38")));
    assertEquals(3.4028235e38, ev.evaluate(new Expression("3.4028235e38")));
    assertEquals(3.4028235e38, ev.evaluate(new Expression("3.4028235E38")));
    assertEquals(3.4028235e38, ev.evaluate(new Expression("3.4028235e38")));

    assertEquals(1.17549435e-38f, ev.evaluate(new Expression("1.17549435E-38f")));
    //assertEquals(1.17549435e-38f, ev.evaluate(new Expression("1.17549435E-38F")));
    assertEquals(1.17549435e-38, ev.evaluate(new Expression("1.17549435E-38")));


    assertEquals(2.8056470606563002E38, ev.evaluate(new Expression("1.4028235E38f + 1.4028235E38f")));
  }

  public void testLongExpression() throws Exception
  {
    assertEquals(5354135431843451853L, ev.evaluate(new Expression("5354135431843451853")));
  }

  
  public void testModeExpressionWithNullVariable() throws Exception
  {
    Object nullable = ev.evaluate(new Expression("null % 0"));
    
    assertNull(nullable);
  }
  
  public void testNotExpression() throws Exception
  {
    boolean isFalse = (boolean) ev.evaluate(new Expression("!(1 > 0)"));
    
    assertEquals(false, isFalse);
  }
  
  public void testEmptyExpression() throws Exception
  {
    Object nullable = ev.evaluate(new Expression(""));
    
    assertNull(nullable);
  }
}
