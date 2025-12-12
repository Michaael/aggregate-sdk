package com.tibbo.aggregate.common.tests;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class JavaTests
{
  @Test
  public void testNullToInt()
  {
    class C
    {
      public int m(int x)
      {
        return x + 1;
      }
    }
    C c = new C();
    Integer i = null;
    assertThrows(NullPointerException.class, () -> {
      c.m(i);
    });
  }
}
