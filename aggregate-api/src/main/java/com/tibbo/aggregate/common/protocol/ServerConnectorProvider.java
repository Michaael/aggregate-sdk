package com.tibbo.aggregate.common.protocol;

import com.tibbo.aggregate.common.util.*;

public interface ServerConnectorProvider
{
  RemoteConnector getConnector(String name);
}
