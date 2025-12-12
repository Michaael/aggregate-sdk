package com.tibbo.aggregate.common.discovery;

import java.util.*;

public interface DiscoverableService
{
  public List<DiscoveryResultItem> check(DiscoverableServiceDefinition definition, String address, long timeout, int retriesCount);
}
