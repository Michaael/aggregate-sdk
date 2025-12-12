package com.tibbo.aggregate.common.expression.function;

import com.tibbo.aggregate.common.expression.*;
import com.tibbo.aggregate.common.tests.*;

public class TestNumericFunctions extends CommonsTestCase
{
  public void testStoreAndLoadFunctions() throws Exception
  {
    Evaluator ev = getCommonsFixture().createTestEvaluator();
    
    Number res = (Number) new JavaMethodFunction("java.lang.Math","sin", "max", "", "", "").execute(ev, null, 3.2, 5.6);
    
    assertEquals(5.6d, res.doubleValue());
    
    res = (Number) new JavaMethodFunction("java.lang.Math","sin", "sin", "", "", "").execute(ev, null, 2.0);
    
    assertTrue(res instanceof Double);
  }
  
}
