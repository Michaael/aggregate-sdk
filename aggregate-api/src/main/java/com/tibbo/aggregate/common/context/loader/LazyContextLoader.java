package com.tibbo.aggregate.common.context.loader;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.tibbo.aggregate.common.context.ContextException;

@Immutable
public class LazyContextLoader implements ContextLoader
{
  private final ContextLoader contextLoader;
  private final int offset;
  private final int count;
  
  public LazyContextLoader(@Nonnull ContextLoader contextLoader, int offset, int count)
  {
    this.contextLoader = contextLoader;
    this.offset = offset;
    this.count = count;
  }
  
  @Override
  public LoadedContexts load(String parent) throws ContextException
  {
      return contextLoader.load(parent).getPage(offset, count);
  }
}
