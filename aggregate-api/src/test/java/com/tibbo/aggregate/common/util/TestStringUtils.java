package com.tibbo.aggregate.common.util;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.*;

import com.tibbo.aggregate.common.tests.*;

public class TestStringUtils extends CommonsTestCase
{
  public void testSplit()
  {
    String str = "this+is++a+test+";
    char ch = '+';
    List<String> res = StringUtils.split(str, ch);
    assertEquals(6, res.size());
    assertEquals("is", res.get(1));
    assertEquals("", res.get(2));
    assertEquals("test", res.get(4));
    assertEquals("", res.get(5));
    
    str = "name:S";
    ch = ':';
    res = StringUtils.split(str, ch);
    assertEquals(2, res.size());
    assertEquals("S", res.get(1));
    
    str = "::";
    ch = ':';
    res = StringUtils.split(str, ch);
    assertEquals(3, res.size());
    assertEquals("", res.get(1));
    
    str = "A^21-09-2006,15:35:07";
    ch = '^';
    res = StringUtils.split(str, ch);
    assertEquals(2, res.size());
    assertEquals("21-09-2006,15:35:07", res.get(1));
  }
  
  public void testElements()
  {
    String s = "<x=2><z=3><x><k=sss><n=<x=3>><r=<aa=<xx><bb>>>";
    
    ElementList res = StringUtils.elements(s, true);
    
    assertEquals(6, res.size());
    assertEquals(null, res.get(2).getName());
    assertEquals("sss", res.getElement("k").getValue());
    assertEquals("x", res.get(2).getValue());
    assertEquals("<x=3>", res.get(4).getValue());
    assertEquals("<aa=<xx><bb>>", res.get(5).getValue());
    
    s = "<{ackRequired#enabled}={notifyOwner}>";
    
    res = StringUtils.elements(s, true);
    assertEquals(1, res.size());
    assertEquals("{ackRequired#enabled}", res.get(0).getName());
    assertEquals("{notifyOwner}", res.get(0).getValue());
    
    s = "<{v1}={v2} != 5>";
    res = StringUtils.elements(s, true);
    assertEquals("{v1}", res.get(0).getName());
    assertEquals("{v2} != 5", res.get(0).getValue());
  }
  
  public void testStringBuilder()
  {
    StringBuilder sb = new StringBuilder(10);
    assertEquals(sb.capacity(), 10);
    
    sb.append("123456789");
    assertEquals(sb.capacity(), 10);
    
    sb.append("0");
    assertEquals(sb.capacity(), 10);
    
    sb.append("1");
    assertEquals(sb.capacity(), 22);
  }
  
  public void testHexStringConversions()
  {
    String source = "My source string 12345=-!+Русские буквы";
    
    String toHex = StringUtils.toHexString(source.getBytes(), " ");
    String fromHex = StringUtils.fromHexString(toHex, " ");
    
    assertEquals(fromHex, source);
    
    toHex = StringUtils.toHexString(source.getBytes(), ";");
    fromHex = StringUtils.fromHexString(toHex, " ");
    
    assertNotEquals(fromHex, source);
  }
}
