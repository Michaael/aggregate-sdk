package com.tibbo.aggregate.common.context.loader;

import com.tibbo.aggregate.common.context.ContextException;

public interface ContextLoader
{
  LoadedContexts load(String parent) throws ContextException;
}
