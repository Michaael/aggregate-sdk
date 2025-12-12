package com.tibbo.aggregate.common.binding;

import java.util.*;

import com.tibbo.aggregate.common.expression.*;
import com.tibbo.aggregate.common.tests.*;
import com.tibbo.aggregate.common.util.*;

public class TestBindingUtils extends CommonsTestCase
{
  public void testFindReferences() throws SyntaxErrorException
  {
    String exp = "1 + {field[0]} - {other[2]}";
    
    List<Reference> refs = ExpressionUtils.findReferences(new Expression(exp));
    
    assertEquals(2, refs.size());
    assertEquals("other", refs.get(1).getField());
    
    refs = ExpressionUtils.findReferences(new Expression("{:eventsByMask('{contextMask}')}"));
    
    assertEquals(2, refs.size());
    assertEquals("contextMask", refs.get(1).getImage());
  }
}
