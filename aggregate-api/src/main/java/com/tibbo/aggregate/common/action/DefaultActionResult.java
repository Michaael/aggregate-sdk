package com.tibbo.aggregate.common.action;

import com.tibbo.aggregate.common.datatable.*;

public class DefaultActionResult implements ActionResult
{
  private boolean successful = true;
  
  public DefaultActionResult()
  {
    super();
  }
  
  public DefaultActionResult(boolean successful)
  {
    super();
    this.successful = successful;
  }

  @Override
  public DataTable getResult() {
    return null;
  }

  public boolean isSuccessful()
  {
    return successful;
  }
  
  public void setSuccessful(boolean successful)
  {
    this.successful = successful;
  }
}
