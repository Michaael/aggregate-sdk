package com.tibbo.aggregate.common.communication;

import com.tibbo.aggregate.common.protocol.*;
import com.tibbo.aggregate.common.tests.*;

public class TestCommandProcessorStatistics extends CommonsTestCase
{
  public void testStatistics() throws Exception
  {
    CommandProcessorStatistics s = new CommandProcessorStatistics();
    
    Command out = ProtocolCommandBuilder.createCommand(false);
    out.write(123);
    out.write(456);
    
    Command in = ProtocolCommandBuilder.createCommand(false);
    in.write(789);
    
    ReplyMonitor<Command, Command> rm = new ReplyMonitor(out);
    rm.setReply(in);
    
    Thread.currentThread().sleep(15);
    
    s.updateOnSyncCommand(rm);
    s.updateOnSyncCommand(rm);
    s.updateOnSyncCommand(rm);
    
    assertEquals(3, s.getCommandCount());
    
    float rt = s.getAverageResponseTime();
    assertTrue(rt > 10f);
    assertTrue(rt < 1000f);
    
    assertEquals(6, s.getOutgoingTraffic());
    assertEquals(3, s.getIncomingTraffic());
  }
}
