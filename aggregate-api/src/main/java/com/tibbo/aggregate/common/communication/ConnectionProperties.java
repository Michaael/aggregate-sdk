package com.tibbo.aggregate.common.communication;

public interface ConnectionProperties
{
  public static final int MODE_TCP = 0;
  public static final int MODE_UDP = 1;
  public static final int MODE_SERIAL = 2;
  
  public int getMode();
  
  public String getAddress();
  
  public int getPort();
  
  public String getPortName();
  
  public int getBaudRate();
  
  public int getFlowControlIn();
  
  public int getFlowControlOut();
  
  public int getDatabits();
  
  public int getStopbits();
  
  public int getParity();
  
  public long getTimeout();
  
}