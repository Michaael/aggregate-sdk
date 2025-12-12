package com.tibbo.aggregate.common.context.loader;

import static com.tibbo.aggregate.common.context.AbstractContext.VF_VISIBLE_CHILDREN_PROPERTY_FILTERS_PROPERTY_NAME;
import static com.tibbo.aggregate.common.context.AbstractContext.VF_VISIBLE_CHILDREN_PROPERTY_FILTERS_PROPERTY_VALUE;

import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import com.google.common.collect.ImmutableMap;
import com.tibbo.aggregate.common.context.CallerController;
import com.tibbo.aggregate.common.context.ContextManager;
import com.tibbo.aggregate.common.datatable.TableFormat;
import com.tibbo.aggregate.common.expression.Expression;
import com.tibbo.aggregate.common.expression.Reference;

public class ContextLoaders
{
  
  public static final TableFormat VFT_PROPERTY_FILTERS = new TableFormat();
  
  static
  {
    VFT_PROPERTY_FILTERS.addField("<" + VF_VISIBLE_CHILDREN_PROPERTY_FILTERS_PROPERTY_NAME + "><S>");
    VFT_PROPERTY_FILTERS.addField("<" + VF_VISIBLE_CHILDREN_PROPERTY_FILTERS_PROPERTY_VALUE + "><S>");
  }
  
  public static ContextLoader createLazyFilteredContextLoader(@Nonnull ContextManager contextManager, @Nonnull CallerController caller,
      @Nonnull String expression, @Nonnull String mask, @Nonnull Integer offset, @Nonnull Integer count)
  {
    
    ContextValidator validator = createDecoratedContextValidator(contextManager, caller, expression, mask, ImmutableMap.of(), ImmutableMap.of(), "");
    
    return new LazyContextLoader(
        new FilteredContextLoader(
            new BaseContextLoader(contextManager, caller), validator),
        offset, count);
  }
  
  public static ContextLoader createFilteredChildrenContextLoader(@Nonnull ContextManager contextManager, @Nonnull CallerController caller,
      @Nonnull String expression, @Nonnull String mask,
      @Nonnull Map<String, List<String>> propertyFilters, @Nonnull Map<String, String> globalFilters, @Nonnull String smartFilterExpression)
  {
    ContextValidator validator = new RecursiveContextValidator(contextManager, caller,
        createDecoratedContextValidator(contextManager, caller, expression, mask, propertyFilters, globalFilters, smartFilterExpression));
    
    return new FilteredContextLoader(
        new BaseContextChildrenLoader(contextManager, caller), validator);
  }
  
  private static DecoratedContextValidator createDecoratedContextValidator(@Nonnull ContextManager contextManager, @Nonnull CallerController caller,
      @Nonnull String expression, @Nonnull String mask,
      @Nonnull Map<String, List<String>> propertyFilters, @Nonnull Map<String, String> globalFilters, @Nonnull String smartFilterExpression)
  {
    return new DecoratedContextValidator(
        new ExpressionContextValidator(contextManager, caller, new Expression(expression), new Reference()),
        new MaskContextValidator(mask),
        new PropertyVisibleInfoValidator(contextManager, caller, propertyFilters),
        new GlobalVisibleInfoValidator(contextManager, caller, globalFilters),
        new SmartFilterExpressionValidator(contextManager, caller, new Expression(smartFilterExpression), new Reference()));
  }
}
