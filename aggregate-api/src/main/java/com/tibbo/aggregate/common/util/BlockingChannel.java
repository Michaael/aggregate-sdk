package com.tibbo.aggregate.common.util;

import java.io.*;
import java.nio.channels.*;

public interface BlockingChannel extends ByteChannel
{
  public boolean isUsesCompression();
  
  public void setUsesCompression(boolean usesCompression);
  
  public void flush() throws IOException;
  
  public String getChannelAddress();
}
