package com.tibbo.aggregate.common.expression;

import com.tibbo.aggregate.common.context.*;
import com.tibbo.aggregate.common.tests.*;

public class TestDecodeReference extends CommonsTestCase
{
  public void testField() throws Exception
  {
    Reference ri = new Reference("field");
    assertEquals("field", ri.getField());
  }
  
  public void testFieldRow() throws Exception
  {
    Reference ri = new Reference("field[3]");
    assertEquals("field", ri.getField());
    assert (ri.getRow() == 3);
  }
  
  public void testComplex()
  {
    Reference ri = new Reference("schema/server^users.admin:func(\"par1\", \"par2\", '{fref} + 1', null)$field[2]#property");
    
    assertEquals("schema", ri.getSchema());
    assertEquals("server", ri.getServer());
    assertEquals("users.admin", ri.getContext());
    assertEquals("func", ri.getEntity());
    
    assertEquals(4, ri.getParameters().size());
    assertEquals("par1", ri.getParameters().get(0));
    assertTrue(ri.getParameters().get(2) instanceof Expression);
    assertEquals("null", ri.getParameters().get(3).toString());
    
    assertEquals("field", ri.getField());
    assertEquals(2, (int) ri.getRow());
    assertEquals("property", ri.getProperty());
  }
  
  public void testContextEvent()
  {
    Reference ri;
    ri = new Reference("users.admin.devices.dev1:shutdown@");
    assertEquals("shutdown", ri.getEntity());
    assertEquals(ContextUtils.ENTITY_EVENT, ri.getEntityType());
  }
  
  public void testContextAction()
  {
    Reference ri;
    ri = new Reference("users.admin.devices.dev1:action!");
    assertEquals("action", ri.getEntity());
    assertEquals(ContextUtils.ENTITY_ACTION, ri.getEntityType());
  }
  
  public void testContextActionWithParams()
  {
    Reference ri;
    ri = new Reference("users.admin.devices.dev1:action(\"param\", 1)!");
    assertEquals("action", ri.getEntity());
    assertEquals(2, ri.getParameters().size());
    assertEquals("param", ri.getParameters().get(0));
    assertEquals(ContextUtils.ENTITY_ACTION, ri.getEntityType());
  }
  
  public void testContextActionWithField()
  {
    Reference ri;
    ri = new Reference("users.admin.devices.dev1:action!$field");
    assertEquals("action", ri.getEntity());
    assertEquals(ContextUtils.ENTITY_ACTION, ri.getEntityType());
    assertEquals("field", ri.getField());
  }
  
  public void testContext()
  {
    Reference ri = new Reference(".:");
    assertEquals(".", ri.getContext());
  }
  
  public void testContextEntityProperty()
  {
    Reference ri;
    ri = new Reference(".:#property");
    assertNull(ri.getEntity());
    assertEquals(".", ri.getContext());
    assertEquals("property", ri.getProperty());
  }
  
  public void testEntityProperty()
  {
    Reference ri;
    ri = new Reference("entity$#property");
    assertEquals("entity", ri.getEntity());
    assertEquals("property", ri.getProperty());
  }
  
  public void testTableProperty()
  {
    Reference ri;
    ri = new Reference("#records");
    assertNull(ri.getEntity());
    assertNull(ri.getField());
    assertEquals("records", ri.getProperty());
  }
  
  public void testFunctionWithQuotedParameters()
  {
    Reference ref = new Reference("check(\"\\\"\" + {invokerContext} + \"\\\"!=null\")");
    assertEquals("check", ref.getEntity());
  }
  
  public void testFunctionWithEmptyParameters()
  {
    Reference ri;
    ri = new Reference("test()");
    assertEquals("test", ri.getEntity());
    assertEquals(null, ri.getField());
  }
  
  public void testFunction()
  {
    Reference ri;
    ri = new Reference("test('true')");
    assertEquals("test", ri.getEntity());
    assertEquals(null, ri.getField());
  }
  
  public void testShemaWithContext()
  {
    Reference ri;
    ri = new Reference("test/Label1:");
    assertEquals(null, ri.getEntity());
  }
  
  public void testFunctionWithExpressionParameters()
  {
    Reference ri;
    ri = new Reference(":eventsByMask(exp1, {expref}, null)");
    assertEquals(3, ri.getParameters().size());
    assertEquals(Expression.class, ri.getParameters().get(0).getClass());
    assertEquals(Expression.class, ri.getParameters().get(1).getClass());
    assertEquals(Expression.class, ri.getParameters().get(2).getClass());
  }
  
  public void testFunctionWithDifferentParameters()
  {
    Reference ri;
    ri = new Reference(":eventsByMask('{contextMask}', {expr}, null, \"str\", \"xx\\\"yy\\'zz\")");
    assertEquals(5, ri.getParameters().size());
    assertEquals("", ri.getContext());
    assertEquals(Expression.class, ri.getParameters().get(0).getClass());
    assertTrue(ri.getParameters().get(0).toString().equals("{contextMask}"));
    assertEquals(Expression.class, ri.getParameters().get(1).getClass());
    assertTrue(ri.getParameters().get(1).toString().equals("{expr}"));
    assertEquals(Expression.class, ri.getParameters().get(2).getClass());
    assertTrue(ri.getParameters().get(2).toString().equals("null"));
    assertEquals(String.class, ri.getParameters().get(3).getClass());
    assertTrue(ri.getParameters().get(3).toString().equals("str"));
    assertTrue(ri.getParameters().get(4).toString().equals("xx\"yy'zz"));
  }
  
  public void testEvent()
  {
    Reference ri;
    ri = new Reference("entity@$");
    assertEquals("entity", ri.getEntity());
    assertEquals(ContextUtils.ENTITY_EVENT, ri.getEntityType());
    assertNull(ri.getField());
  }
  
  public void testContextWithEntity()
  {
    Reference ri;
    ri = new Reference("context:entity");
    assertEquals("context", ri.getContext());
    assertEquals("entity", ri.getEntity());
    assertEquals(null, ri.getField());
  }
  
  public void testEntityWithoutField()
  {
    Reference ri;
    ri = new Reference("entity$");
    assertEquals("entity", ri.getEntity());
    assertNull(ri.getField());
  }
  
  public void testMultipleFields()
  {
    Reference ri;
    ri = new Reference("context.path:entity$fieldA[3].fieldB[5].fieldC[7]");
    assert (ri.getFields().size() == 3);
    assertEquals("fieldA", ri.getFields().get(0).getFirst());
    assert (ri.getFields().get(2).getSecond() == 7);
  }
  
}
