package com.tibbo.aggregate.common.device.sync;

public class ConnectOnlySynchronization extends AbstractSynchronizationParameters
{
  public ConnectOnlySynchronization()
  {
    this(null);
  }

  public ConnectOnlySynchronization(String id)
  {
    super(id, true, true, false);
  }
  
  @Override
  public boolean isConnectOnly()
  {
    return true;
  }
}
