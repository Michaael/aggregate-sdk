package com.tibbo.aggregate.common.expression;

import com.tibbo.aggregate.common.context.*;
import com.tibbo.aggregate.common.tests.*;

public class TestEncodeReference extends CommonsTestCase
{
  public void testSchemaVariable()
  {
    Reference ri;
    ri = new Reference();
    ri.setSchema("schema");
    ri.setEntity("variable");
    ri.setEntityType(ContextUtils.ENTITY_VARIABLE);
    assertEquals(ri.getImage(), "schema/variable$");
  }
  
  public void testEvent()
  {
    Reference ri;
    ri = new Reference();
    ri.setEntity("entity");
    ri.setEntityType(ContextUtils.ENTITY_EVENT);
    assertEquals("entity@", ri.getImage());
  }
  
  public void testAction()
  {
    Reference ri;
    ri = new Reference();
    ri.setEntity("entity");
    ri.setEntityType(ContextUtils.ENTITY_ACTION);
    assertEquals("entity!", ri.getImage());
  }
  
  public void testActionWithParams()
  {
    Reference ri;
    ri = new Reference();
    ri.setEntity("entity");
    ri.setEntityType(ContextUtils.ENTITY_ACTION);
    ri.addParameter("param");
    assertEquals("entity(\"param\")!", ri.getImage());
  }
  
  public void testVariable()
  {
    Reference ri;
    ri = new Reference();
    ri.setEntity("entity");
    ri.setEntityType(ContextUtils.ENTITY_VARIABLE);
    assertEquals("entity$", ri.getImage());
  }
  
  public void testContextVariable()
  {
    Reference ri = new Reference();
    ri.setContext("context");
    ri.setEntity("entity");
    ri.setEntityType(ContextUtils.ENTITY_VARIABLE);
    assertEquals("context:entity", ri.getImage());
  }
  
  public void testFunctionWithParameters()
  {
    Reference ri = new Reference("context", "function", new Object[] { 1, "string" });
    assertEquals("context:function(\"1\",\"string\")", ri.getImage());
  }
}
