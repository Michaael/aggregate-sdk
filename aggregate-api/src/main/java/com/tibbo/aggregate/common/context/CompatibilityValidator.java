package com.tibbo.aggregate.common.context;

import com.tibbo.aggregate.common.datatable.*;

@FunctionalInterface
public interface CompatibilityValidator
{
  String needConvertVariableFormat(VariableDefinition def, DataTable table);
}
