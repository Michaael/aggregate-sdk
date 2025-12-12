package com.tibbo.aggregate.common.context;

import com.tibbo.aggregate.common.datatable.*;

public interface VariableSetter
{
  boolean set(Context con, VariableDefinition def, CallerController caller, RequestController request, DataTable value) throws ContextException;
}
