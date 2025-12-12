package com.tibbo.aggregate.common.action;

import com.tibbo.aggregate.common.datatable.*;

public interface ActionResult
{
  boolean isSuccessful();
  DataTable getResult();
}
