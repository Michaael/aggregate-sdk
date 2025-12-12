package com.tibbo.aggregate.common.context.loader;

import java.util.List;

import com.tibbo.aggregate.common.context.Context;
import com.tibbo.aggregate.common.context.ContextException;
import com.tibbo.aggregate.common.datatable.DataTable;

public interface LoadedContexts
{
  LoadedContexts getPage(int offset, int count) throws ContextException;
  
  DataTable asDataTable();
  
  int getTotalCount();
  
  List<Context> getContexts();
}
