package com.tibbo.aggregate.common.communication;

import java.io.IOException;

import com.tibbo.aggregate.common.device.DisconnectionException;
import com.tibbo.aggregate.common.protocol.ProtocolVersion;
import com.tibbo.aggregate.common.util.BlockingChannel;

public interface CommandWriter<C extends Command>
{
  default void write(C command, BlockingChannel channel) throws IOException, DisconnectionException
  {
    write(command, channel, false);
  }

  void write(C command, BlockingChannel channel, boolean needsFlushing) throws IOException, DisconnectionException;

  void setVersion(ProtocolVersion version);
  
  void setVersionAfterNextWrite(ProtocolVersion version);
}
