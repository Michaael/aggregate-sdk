package com.tibbo.aggregate.common.context;

import com.tibbo.aggregate.common.datatable.*;

public interface VariableGetter
{
  DataTable get(Context con, VariableDefinition def, CallerController caller, RequestController request) throws ContextException;
}
