package com.tibbo.aggregate.common.communication;

import java.io.*;

public abstract class AbstractCommandParser<C extends Command> implements CommandParser<C>
{
  private static final int DEFAULT_SIZE = 1024;
  
  private ByteArrayOutputStream data = new ByteArrayOutputStream(DEFAULT_SIZE);
  
  private CommandParserListener listener;
  
  public AbstractCommandParser()
  {
    super();
  }
  
  @Override
  public void reset()
  {
    if (data != null)
      data.reset();
  }
  
  public byte[] getData()
  {
    return data.toByteArray();
  }
  
  public byte[] clearData()
  {
    byte[] dataArray = data.toByteArray();
    data = new ByteArrayOutputStream(DEFAULT_SIZE);
    return dataArray;
  }
  
  @Override
  public void addData(int dataByte)
  {
    data.write(dataByte);
  }
  
  @Override
  public void setListener(CommandParserListener listener)
  {
    this.listener = listener;
  }
  
  protected CommandParserListener getListener()
  {
    return listener;
  }
  
  @Override
  public String toString()
  {
    return data.toString();
  }
}
