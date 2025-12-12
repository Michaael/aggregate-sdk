package com.tibbo.aggregate.common.communication;

import java.util.concurrent.*;
import java.util.concurrent.TimeUnit;

import com.tibbo.aggregate.common.util.*;

public class SendersPool
{
  private static ThreadPoolExecutor SENDERS_POOL = new ThreadPoolExecutor(0, 200, 60L, TimeUnit.SECONDS, new SynchronousQueue(), new NamedThreadFactory("AsyncCommandSender"));
  static
  {
    SENDERS_POOL.allowCoreThreadTimeOut(true);
  }
  
  public static ThreadPoolExecutor get()
  {
    return SENDERS_POOL;
  }
}
