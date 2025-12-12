package com.tibbo.aggregate.common.context;

import com.tibbo.aggregate.common.datatable.*;

@FunctionalInterface
public interface CompatibilityConverter
{
  DataTable convert(DataTable oldValue, DataTable newValue);
}
