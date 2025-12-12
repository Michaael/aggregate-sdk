package com.tibbo.aggregate.common.device;

import com.tibbo.aggregate.common.context.*;

public class SynchronousRequestController extends DefaultRequestController
{
  public SynchronousRequestController(Long lockTimeout)
  {
    super(lockTimeout);
  }
}
