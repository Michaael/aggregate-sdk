package com.tibbo.aggregate.common.expression;

import java.util.*;

import com.tibbo.aggregate.common.tests.*;

public class TestExpressionUtils extends CommonsTestCase
{
  
  public void testSyntaxValidation() throws Exception
  {
    String str = "cell({:executeQuery(\"SELECT SUM(data." + "processList" + "$hrSWRunPerfMem) as value FROM users.admin.devices.localhost:" + "processList" + " as data WHERE " + "processList"
        + "$hrSWRunName = 'svchost.exe' AND " + "processList" + "$hrSWRunPath = 'C:\\\\Windows\\\\system32\\\\'\" )}, \"value\") > 3600";
    
    ExpressionUtils.validateSyntax(new Expression(str), false);
  }
  
  public void testFunctionParameters() throws Exception
  {
    String params = "\"constant\", 'expression', unquoted_expression";
    List<Object> res = ExpressionUtils.getFunctionParameters(params, true);
    assertEquals(3, res.size());
    assertEquals("constant", res.get(0));
    assertEquals(new Expression("expression"), res.get(1));
    assertEquals(new Expression("unquoted_expression"), res.get(2));
  }
  
  public void testEscaping() throws Exception
  {
    String params = "\"SELECT COUNT(*) as value FROM users.admin.devices.lh:processList as data     WHERE       processList$hrSWRunName = 'csrss.exe' AND processList$hrSWRunPath = 'C:\\\\Windows\\\\system32\\\\' \"";
    List<Object> res = ExpressionUtils.getFunctionParameters(params, false);
    assertEquals(1, res.size());
    assertEquals(
        "SELECT COUNT(*) as value FROM users.admin.devices.lh:processList as data     WHERE       processList$hrSWRunName = 'csrss.exe' AND processList$hrSWRunPath = 'C:\\Windows\\system32\\' ",
        res.get(0));
  }
  
}
