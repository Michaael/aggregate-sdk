package com.tibbo.aggregate.common.util;

import java.util.*;

import com.tibbo.aggregate.common.tests.*;

public class TestStringWrapper extends CommonsTestCase
{
  @SuppressWarnings("unlikely-arg-type")
  public void testEquals()
  {
    StringWrapper v1 = StringWrapper.valueOf("12345");
    StringWrapper v2 = StringWrapper.valueOf("12345");
    
    assertEquals(v1, v2);
    
    StringWrapper v3 = StringWrapper.valueOf("012345");
    assertFalse(v1.equals(v3));
    
    StringWrapper v4 = StringWrapper.valueOf("12345", 0, 5);
    assertTrue(v1.equals(v4));
    assertTrue(v1.equals("12345"));
    
    StringWrapper v5 = StringWrapper.valueOf("12345", 0, 5);
    assertTrue(v5.equals(v1));
    assertTrue(v5.equals("12345"));
    
    StringWrapper v6 = StringWrapper.valueOf("0123456", 1, 6);
    assertTrue(v6.equals(v1));
    assertTrue(v6.equals("12345"));
    assertTrue(v5.equals(v6));
    assertTrue(v6.equals(v1));
    assertTrue(v1.equals(v6));
    
    assertEquals(v1.getString(), "12345");
    assertEquals(v6.getString(), "12345");
    
    assertFalse(v6.equals("123456"));
  }
  
  public void testLength()
  {
    StringWrapper v1 = StringWrapper.valueOf("12345");
    StringWrapper v2 = StringWrapper.valueOf("12345", 0, 5);
    StringWrapper v3 = StringWrapper.valueOf("12345", 1, 4);
    
    assertEquals(v1, v2);
    assertEquals(v1.length(), 5);
    assertEquals(v2.length(), 5);
    assertEquals(v3.length(), 3);
  }
  
  public void testCharAt()
  {
    StringWrapper v1 = StringWrapper.valueOf("12345");
    StringWrapper v2 = StringWrapper.valueOf("12345", 1, 5);
    StringWrapper v3 = StringWrapper.valueOf("12345", 0, 5);
    
    assertEquals(v1.charAt(0), '1');
    assertEquals(v1.charAt(4), '5');
    assertEquals(v2.charAt(0), '2');
    assertEquals(v3.charAt(0), '1');
  }
  
  public void testSplit()
  {
    List<StringWrapper> list = StringWrapper.split("12\n34\n56\n78\n90", '\n');
    
    assertEquals(list.size(), 5);
    assertEquals(list.get(0), "12");
  }
  
}
