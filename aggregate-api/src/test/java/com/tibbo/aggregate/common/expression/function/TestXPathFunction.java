package com.tibbo.aggregate.common.expression.function;

import com.tibbo.aggregate.common.datatable.DataTable;
import com.tibbo.aggregate.common.expression.EvaluationException;
import com.tibbo.aggregate.common.expression.Evaluator;
import com.tibbo.aggregate.common.expression.Expression;
import com.tibbo.aggregate.common.tests.CommonsFixture;
import com.tibbo.aggregate.common.tests.CommonsTestCase;
import com.tibbo.aggregate.common.util.SyntaxErrorException;

import java.text.MessageFormat;

public class TestXPathFunction extends CommonsTestCase
{
  private static final String X_PATH_EXPRESSION_PATTERN = "xpath(\"<software_info>\" +" + System.lineSeparator()
      + "\"<software type=\\\"boot\\\" version=\\\"499-03-115\\\" date=\\\"20150119\\\" checksum=\\\"ec33\\\"/>\" +" + System.lineSeparator()
      + "\"<software type=\\\"appl\\\" version=\\\"410-18-197\\\" date=\\\"20180528\\\" checksum=\\\"81e1\\\"/>\" +" + System.lineSeparator()
      + "\"<software type=\\\"lam\\\" version=\\\"498-42-100\\\" date=\\\"20051024\\\" checksum=\\\"43f8\\\"/>\" +" + System.lineSeparator()
      + "\"<license>\" +" + System.lineSeparator()
      + "\"<key name=\\\"EXTENDED_INFO\\\" status=\\\"Offered\\\"/>\" +" + System.lineSeparator()
      + "\"<key name=\\\"WD_ATC\\\" status=\\\"Offered\\\"/>\" +" + System.lineSeparator()
      + "\"</license>\" +" + System.lineSeparator()
      + "\"</software_info>\", \"{0}\",{1})";
  
  private static final String X_PATH_FULL_NODE = "//software_info/software";
  
  private static final String X_PATH_FULL_ATTRIBUTE = "//software_info/software/@version";
  private static final String X_PATH_PART_ATTRIBUTE = "//software_info/software[1]/@version";
  
  private static final String X_PATH_RESULT_TYPE_STRING = "0";
  private static final String X_PATH_RESULT_TYPE_TABLE = "1";
  
  private static final String RESULT_FULL_NODE_STRING = "499-03-115" + System.lineSeparator() + "410-18-197" + System.lineSeparator() + "498-42-100";
  private static final String RESULT_PART_NODE_STRING = "499-03-115";
  
  private static final String FIELD_VERSION = "version";
  
  Evaluator evaluator;
  
  @Override
  protected void setUp() throws Exception
  {
    super.setUp();
    evaluator = CommonsFixture.createTestEvaluator();
  }
  
  public void testStringXpath() throws EvaluationException, SyntaxErrorException
  {
    Expression expression = new Expression(MessageFormat.format(X_PATH_EXPRESSION_PATTERN, X_PATH_FULL_ATTRIBUTE, X_PATH_RESULT_TYPE_STRING));
    String result = evaluator.evaluateToString(expression);
    assertEquals(RESULT_FULL_NODE_STRING, result);
    
    expression = new Expression(MessageFormat.format(X_PATH_EXPRESSION_PATTERN, X_PATH_PART_ATTRIBUTE, X_PATH_RESULT_TYPE_STRING));
    result = evaluator.evaluateToString(expression);
    assertEquals(RESULT_PART_NODE_STRING, result);
  }
  
  public void testTableXpath() throws EvaluationException, SyntaxErrorException
  {
    Expression expression = new Expression(MessageFormat.format(X_PATH_EXPRESSION_PATTERN, X_PATH_FULL_NODE, X_PATH_RESULT_TYPE_TABLE));
    DataTable result = evaluator.evaluateToDataTable(expression);
    assertEquals(3, (int) result.getRecordCount());
    assertTrue(result.hasField(FIELD_VERSION));
    assertEquals(result.rec().getString(FIELD_VERSION), RESULT_PART_NODE_STRING);
    
    expression = new Expression(MessageFormat.format(X_PATH_EXPRESSION_PATTERN, X_PATH_FULL_ATTRIBUTE, X_PATH_RESULT_TYPE_TABLE));
    result = evaluator.evaluateToDataTable(expression);
    assertEquals(3, (int) result.getRecordCount());
    assertTrue(result.hasField(FIELD_VERSION));
    assertEquals(result.rec().getString(FIELD_VERSION), RESULT_PART_NODE_STRING);
    
    expression = new Expression(MessageFormat.format(X_PATH_EXPRESSION_PATTERN, X_PATH_PART_ATTRIBUTE, X_PATH_RESULT_TYPE_TABLE));
    result = evaluator.evaluateToDataTable(expression);
    assertEquals(1, (int) result.getRecordCount());
    assertTrue(result.hasField(FIELD_VERSION));
    assertEquals(result.rec().getString(FIELD_VERSION), RESULT_PART_NODE_STRING);
  }
}
