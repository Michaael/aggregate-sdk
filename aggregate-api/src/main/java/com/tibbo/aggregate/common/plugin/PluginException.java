package com.tibbo.aggregate.common.plugin;

import com.tibbo.aggregate.common.*;


public class PluginException extends AggreGateException
{
  public PluginException(String message)
  {
    super(message);
  }

  public PluginException(Throwable cause)
  {
    super(cause);
  }

  public PluginException(String message, Throwable cause)
  {
    super(message, cause);
  }
}
