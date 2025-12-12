package com.tibbo.aggregate.common.context;

import com.tibbo.aggregate.common.datatable.*;

public interface FunctionImplementation
{
  DataTable execute(Context con, FunctionDefinition def, CallerController caller, RequestController request, DataTable parameters) throws ContextException;
}
