package com.tibbo.aggregate.common.action;

import com.tibbo.aggregate.common.datatable.*;

public interface EditDataMerger
{
  DataTable merge(DataTable preset, DataTable original);
}
