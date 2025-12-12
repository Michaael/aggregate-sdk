package com.tibbo.aggregate.common.util;

public class WatchdogHolder {

    private volatile static MemoryWatchdog INSTANCE;

    public static MemoryWatchdog getInstance()
    {
        return getInstance(StubMemoryWatchdog.INSTANCE);
    }

    public static MemoryWatchdog getInstance(MemoryWatchdog defaultInitializer)
    {
        if (INSTANCE == null)
        {
            synchronized (WatchdogHolder.class)
            {
                if (INSTANCE == null)
                {
                    INSTANCE = defaultInitializer;
                }
            }
        }
        return INSTANCE;
    }


}
