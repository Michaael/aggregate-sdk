package com.tibbo.aggregate.common.expression.function;

import java.nio.charset.*;

import com.tibbo.aggregate.common.data.Data;
import com.tibbo.aggregate.common.datatable.*;
import com.tibbo.aggregate.common.expression.*;
import com.tibbo.aggregate.common.expression.function.other.*;
import com.tibbo.aggregate.common.expression.function.string.*;
import com.tibbo.aggregate.common.tests.*;

public class TestStringFunctions extends CommonsTestCase
{
  public void testGroupsFunction() throws Exception
  {
    Evaluator ev = getCommonsFixture().createTestEvaluator();
  
    Object res = new GroupsFunction().execute(ev, null, "Testing123Testing", "^[a-zA-Z]+([0-9]+).*");
    
    assertEquals("123", res);
  }
  
  public void testCharacterFunction() throws Exception
  {
    Evaluator ev = getCommonsFixture().createTestEvaluator();
    
    Object res = new CharacterFunction("isDigit").execute(ev, null, "5blabla");
    assertTrue((boolean) res);
    
    res = new CharacterFunction("isDigit").execute(ev, null, "blabla");
    assertFalse((boolean) res);
  }
  
  public void testSplitFunction() throws Exception
  {
    String data = "It's a simple string";
    String regex = " ";
    DataTable result = (DataTable) new SplitFunction().execute(null, null, data, regex);
    
    assertNotNull(result);
    assertEquals(4, (int) result.getRecordCount());
  }
  
  public void testUrlDecodeFunction() throws Exception
  {
    String url = "https%3A%2F%2Fmywebsite%2Fdocs%2Fenglish%2Fsite%2Fmybook.do%3Frequest_type";
    String expected = "https://mywebsite/docs/english/site/mybook.do?request_type";
    String result = (String) new UrlDecodeFunction().execute(null, null, url, StandardCharsets.UTF_8);
    
    assertEquals(expected, result);
  }
  
  public void testUrlEncodeFunction() throws Exception
  {
    String url = "https://mywebsite/docs/english/site/mybook.do?request_type";
    String expected = "https%3A%2F%2Fmywebsite%2Fdocs%2Fenglish%2Fsite%2Fmybook.do%3Frequest_type";
    String result = (String) new UrlEncodeFunction().execute(null, null, url, StandardCharsets.UTF_8);
    
    assertEquals(expected, result);
  }
  
  public void testDataBlocFunction() throws Exception
  {
    String dataForEncode = "String for encode";
    String expected = "0/\u001A/\u001A/-1/17/String for encode";
    String result = ((Data) new DataBlockFunction().execute(null, null, dataForEncode, StandardCharsets.UTF_8)).encode();
    
    assertEquals(expected, result);
  }
  
}
