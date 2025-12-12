package com.tibbo.aggregate.common.context.loader;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.tibbo.aggregate.common.context.DefaultContextManager;
import com.tibbo.aggregate.common.expression.Expression;
import com.tibbo.aggregate.common.expression.Reference;
import com.tibbo.aggregate.common.tests.StubContext;

public class TestContextValidator
{
  
  private final DefaultContextManager contextManager;

  public TestContextValidator()
  {
    StubContext context = new StubContext("root");
    context.setDescription("rootContext");
    StubContext child = new StubContext("child");
    child.setDescription("annoyingChild");
    StubContext grandChild = new StubContext("grandChild");
    grandChild.setDescription("42");
    child.addChild(grandChild);
    context.addChild(child);
    
    contextManager = new DefaultContextManager(context, true);
  }
  
  @Test
  public void testMaskContextValidator()
  {
    assertTrue(new MaskContextValidator("users.*.models.modelX").validate("users.admin.models.modelX"));
    assertFalse(new MaskContextValidator("users.*.models.modelY").validate("users.admin.models.modelX"));
    assertFalse(new MaskContextValidator("users.test.models.modelX").validate("users.admin.models.modelX"));
  }
  
  @Test
  public void testExpressionContextValidator()
  {
    assertTrue(new ExpressionContextValidator(contextManager, contextManager.getCallerController(), new Expression("true"), new Reference()).validate("child.grandChild"));
    assertFalse(new ExpressionContextValidator(contextManager, contextManager.getCallerController(), new Expression("2 == 3"), new Reference()).validate("child.grandChild"));
  }

  @Test
  public void testDecoratedContextValidator()
  {
    assertTrue(new DecoratedContextValidator(
        new ExpressionContextValidator(contextManager, contextManager.getCallerController(), new Expression("true"), new Reference()),
        new MaskContextValidator("*.grandChild")).validate("child.grandChild"));

    assertTrue(new DecoratedContextValidator(
        new ExpressionContextValidator(contextManager, contextManager.getCallerController(), new Expression("true"), new Reference()),
        new MaskContextValidator("child")).validate("child"));

    assertFalse(new DecoratedContextValidator(
            new ExpressionContextValidator(contextManager, contextManager.getCallerController(), new Expression("false"), new Reference()),
            new MaskContextValidator("child")).validate("child"));

    assertFalse(new DecoratedContextValidator(
            new ExpressionContextValidator(contextManager, contextManager.getCallerController(), new Expression("true"), new Reference()),
            new MaskContextValidator("child")).validate("child.foundling"));

  }
  
  @Test
  public void testRecursiveContextValidator()
  {
    assertTrue(new RecursiveContextValidator(contextManager, contextManager.getCallerController(), new DecoratedContextValidator(
            new ExpressionContextValidator(contextManager, contextManager.getCallerController(), new Expression("true"), new Reference()),
            new MaskContextValidator("*.grandChild"))).validate(""));
  }

  @Test
  public void testPropertyVisibleInfoValidator()
  {
    assertTrue(
    new PropertyVisibleInfoValidator(contextManager, contextManager.getCallerController(),
            prepareFilterMap("name", ImmutableList.of("child", "grand")))
            .validate("child.grandChild"));

    assertFalse(
            new PropertyVisibleInfoValidator(contextManager, contextManager.getCallerController(),
                    prepareFilterMap("name", ImmutableList.of("child", "step")))
                    .validate("child.grandChild"));

    assertTrue(
            new PropertyVisibleInfoValidator(contextManager, contextManager.getCallerController(),
                    prepareFilterMap("name", ImmutableList.of("child", "grand,step")))
                    .validate("child.grandChild"));
  }

  @Test
  public void testGlobalVisibleInfoValidator()
  {
    assertTrue(
            new GlobalVisibleInfoValidator(contextManager, contextManager.getCallerController(),
                    ImmutableMap.of("name", "42", "description", "42"))
                    .validate("child.grandChild"));

    assertTrue(
            new GlobalVisibleInfoValidator(contextManager, contextManager.getCallerController(),
                    ImmutableMap.of("name", "grand", "description", "grand"))
                    .validate("child.grandChild"));

    assertFalse(
            new GlobalVisibleInfoValidator(contextManager, contextManager.getCallerController(),
                    ImmutableMap.of("name", "43", "description", "3"))
                    .validate("child.grandChild"));
  }

  private Map<String, List<String>> prepareFilterMap(String prop, List<String> values) {
    return ImmutableMap.<String, List<String>>builder()
            .put(prop, values)
            .build();
  }

}