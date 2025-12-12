package com.tibbo.aggregate.common.context.loader;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;

import org.junit.jupiter.api.Test;

import com.google.common.collect.Maps;
import com.tibbo.aggregate.common.context.Context;
import com.tibbo.aggregate.common.context.ContextException;
import com.tibbo.aggregate.common.context.DefaultContextManager;
import com.tibbo.aggregate.common.expression.DefaultReferenceResolver;
import com.tibbo.aggregate.common.expression.Evaluator;
import com.tibbo.aggregate.common.expression.Expression;
import com.tibbo.aggregate.common.expression.Reference;
import com.tibbo.aggregate.common.tests.StubContext;

public class TestContextLoader
{
  private final DefaultContextManager contextManager;
  private final Evaluator evaluator;

  public TestContextLoader()
  {

    contextManager = new DefaultContextManager(prepareTestContextTree(), true);

    evaluator = new Evaluator(new DefaultReferenceResolver());
    evaluator.getDefaultResolver().setContextManager(contextManager);
  }

  /**
   * root c1 c11 c111 c112 c12 c13 c14 c141 c2 c21 c22 c3 c31
   */
  private Context prepareTestContextTree()
  {
    StubContext root = new StubContext("");
    Map<String, Context> children = prepareChildNodes(root, "c1", "c2", "c3");
    Map<String, Context> c1Children = prepareChildNodes(children.get("c1"), "c11", "c12", "c13", "c14");
    prepareChildNodes(children.get("c2"), "c21", "c22");
    prepareChildNodes(children.get("c3"), "c31");
    prepareChildNodes(c1Children.get("c11"), "c111", "c112");
    prepareChildNodes(c1Children.get("c14"), "c141");
    return root;
  }

  private Map<String, Context> prepareChildNodes(Context context, String... children)
  {
    Map<String, Context> childrenContexts = Maps.newHashMap();
    for (String child : children)
    {
      StubContext childContext = new StubContext(child);
      context.addChild(childContext);
      childrenContexts.put(child, childContext);
    }
    return childrenContexts;
  }

  @Test
  public void testFilteredBaseContextLoader() throws ContextException
  {
    LoadedContexts filteredContextLoader = createFilteredContextsLoader("true", "*").load("");
    assertEquals(14, filteredContextLoader.getTotalCount());

    filteredContextLoader = createFilteredContextsLoader("true", "c2.*").load("");
    assertEquals(2, filteredContextLoader.getTotalCount());

    filteredContextLoader = createFilteredContextsLoader("true", "c1.*").load("");
    assertEquals(7, filteredContextLoader.getTotalCount());
  }

  @Test
  public void testFilteredContextChildrenLoader() throws ContextException
  {
    LoadedContexts filteredContextLoader = createRecursiveFilteredChildrenContextsLoader("dc() == \"c2\"", "*").load("");
    assertEquals(1, filteredContextLoader.getTotalCount());

    filteredContextLoader = createRecursiveFilteredChildrenContextsLoader("true", "c1.*").load("c1");
    assertEquals(4, filteredContextLoader.getTotalCount());

    filteredContextLoader = createRecursiveFilteredChildrenContextsLoader("true", "c1.*.c111").load("c1");
    assertEquals(1, filteredContextLoader.getTotalCount());
  }

  @Test
  public void testIllegalInput() throws ContextException
  {
    org.junit.jupiter.api.Assertions.assertThrows(ContextException.class, () -> {
      new LazyContextLoader(createRecursiveFilteredChildrenContextsLoader("true", "*"), 4, 20).load("c1");
    });
  }

  @Test
  public void testLazyContextLoader() throws ContextException
  {
    LoadedContexts loaded = new LazyContextLoader(createRecursiveFilteredChildrenContextsLoader("true", "*"), 0, 2).load("c1");
    assertEquals(4, loaded.getTotalCount());
    assertEquals(2, loaded.asDataTable().rec().getDataTable(0).getRecordCount().intValue());

    loaded = new LazyContextLoader(createFilteredContextsLoader("true", "*"), 0, 2).load("c1");
    assertEquals(8, loaded.getTotalCount());
    assertEquals(2, loaded.asDataTable().rec().getDataTable(0).getRecordCount().intValue());
  }

  private FilteredContextLoader createRecursiveFilteredChildrenContextsLoader(String expression, String mask)
  {
    return new FilteredContextLoader(new BaseContextChildrenLoader(contextManager, contextManager.getCallerController()),
            createRecursiveContextValidator(expression, mask));
  }

  private FilteredContextLoader createFilteredContextsLoader(String expression, String mask)
  {
    return new FilteredContextLoader(new BaseContextLoader(contextManager, contextManager.getCallerController()),
            createDecoratedContextValidator(expression, mask));
  }

  private ContextValidator createRecursiveContextValidator(String expression, String mask)
  {
    return new RecursiveContextValidator(
            contextManager, contextManager.getCallerController(), createDecoratedContextValidator(expression, mask));
  }

  private DecoratedContextValidator createDecoratedContextValidator(String expression, String mask)
  {
    return new DecoratedContextValidator(
            new ExpressionContextValidator(contextManager, contextManager.getCallerController(), new Expression(expression), new Reference()),
            new MaskContextValidator(mask));
  }
}