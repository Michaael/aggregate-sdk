package com.tibbo.aggregate.common.context.loader;

import static com.tibbo.aggregate.common.context.AbstractContext.FOFT_VISIBLE_CHILDREN;
import static com.tibbo.aggregate.common.context.AbstractContext.VFT_VISIBLE_CHILDREN;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.tibbo.aggregate.common.context.Context;
import com.tibbo.aggregate.common.context.ContextException;
import com.tibbo.aggregate.common.datatable.DataTable;
import com.tibbo.aggregate.common.datatable.SimpleDataTable;

@Immutable
public class PageableLoadedContexts implements LoadedContexts
{
  
  private final List<Context> contexts;
  
  private final int count;
  
  public PageableLoadedContexts(@Nonnull List<Context> contexts, int count)
  {
    this.contexts = contexts;
    this.count = count;
  }
  
  @Override
  public LoadedContexts getPage(int offset, int count) throws ContextException
  {
    List<Context> result = new ArrayList<>();
    if (this.count == 0)
    {
      return new PageableLoadedContexts(result, this.count);
    }
    int lastIndex = Math.min(offset + count, this.count);
    
    if (offset > this.count - 1)
    {
      throw new ContextException("Offset cannot be greater then total count!");
    }
    
    for (int i = offset; i < lastIndex; i++)
    {
      result.add(contexts.get(i));
    }
    return new PageableLoadedContexts(result, this.count);
  }
  
  @Override
  public DataTable asDataTable()
  {
    DataTable children = new SimpleDataTable(VFT_VISIBLE_CHILDREN);
    
    for (Context visibleChild : contexts)
    {
      children.addRecord(visibleChild.getPath());
    }
    
    DataTable result = new SimpleDataTable(FOFT_VISIBLE_CHILDREN);
    result.addRecord(children, count);
    return result;
  }
  
  @Override
  public int getTotalCount()
  {
    return count;
  }
  
  @Override
  public List<Context> getContexts()
  {
    return contexts;
  }
}
