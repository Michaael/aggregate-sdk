package com.tibbo.aggregate.common.device.sync;

public class DefaultSynchronizationParameters extends AbstractSynchronizationParameters
{
  
  public DefaultSynchronizationParameters()
  {
    this(false);
  }
  
  public DefaultSynchronizationParameters(boolean useExtendedStatus)
  {
    super(null, true, true, useExtendedStatus);
  }
}
