package com.tibbo.aggregate.common.util;

public interface MemoryWatchdog {

    void awaitForEnoughMemory();

    boolean isEnoughMemory();
}
