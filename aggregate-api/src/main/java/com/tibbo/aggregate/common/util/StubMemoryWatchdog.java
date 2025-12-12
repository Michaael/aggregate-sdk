package com.tibbo.aggregate.common.util;

public class StubMemoryWatchdog implements MemoryWatchdog
{
  public static final MemoryWatchdog INSTANCE = new StubMemoryWatchdog();
  
  private StubMemoryWatchdog()
  {
  }
  
  @Override
  public void awaitForEnoughMemory()
  {
  }
  
  @Override
  public boolean isEnoughMemory()
  {
    return true;
  }
}
