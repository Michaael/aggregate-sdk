package com.tibbo.aggregate.common.communication;

import com.tibbo.aggregate.common.*;
import com.tibbo.aggregate.common.datatable.*;
import com.tibbo.aggregate.common.datatable.field.*;
import com.tibbo.aggregate.common.util.*;

public class CommandProcessorStatistics
{
  public static final TableFormat FORMAT = new TableFormat(1, 1);
  static
  {
    FORMAT.addField("<connectionTime><L><F=N><D=" + Cres.get().getString("commConnectionTime") + "><H=" + Cres.get().getString("statisticsServerConnectionHelpTime") + "><E="
        + LongFieldFormat.EDITOR_PERIOD + "><O=" + LongFieldFormat.encodePeriodEditorOptions(TimeHelper.SECOND, TimeHelper.DAY) + ">");
    FORMAT.addField("<commandCount><L><D=" + Cres.get().getString("commCommandCount") + "><H=" + Cres.get().getString("statisticsServerConnectionHelpCommands") + ">");
    FORMAT.addField("<eventCount><L><D=" + Cres.get().getString("commEventCount") + "><H=" + Cres.get().getString("statisticsServerConnectionHelpEvents") + ">");
    FORMAT.addField("<averageResponseTime><L><D=" + Cres.get().getString("commAvgResponseTime") + "><H=" + Cres.get().getString("statisticsServerConnectionHelpResponceTime") + "><E="
        + LongFieldFormat.EDITOR_PERIOD + "><O=" + LongFieldFormat.encodePeriodEditorOptions(TimeHelper.MILLISECOND, TimeHelper.MILLISECOND) + ">");
    FORMAT.addField("<incomingTraffic><L><D=" + Cres.get().getString("commIncomingTraffic") + "><H=" + Cres.get().getString("statisticsServerConnectionHelpIncoming") + "><E="
        + LongFieldFormat.EDITOR_BYTES + ">");
    FORMAT.addField("<outgoingTraffic><L><D=" + Cres.get().getString("commOutgoingTraffic") + "><H=" + Cres.get().getString("statisticsServerConnectionHelpOutgoing") + "><E="
        + LongFieldFormat.EDITOR_BYTES + ">");
    FORMAT.addField("<unrepliedCommandCount><L><D=" + Cres.get().getString("commUnrepliedCommandCount") + "><H=" + Cres.get().getString("statisticsServerConnectionHelpUnreplied") + ">");
  }
  
  private Long startTime;
  private long commandCount;
  private long eventCount;
  private float averageResponseTime;
  private long outgoingTraffic;
  private long incomingTraffic;
  private long unrepliedCommandCount;
  
  public void updateOnAsyncCommand(Command command)
  {
    eventCount++;
    
    incomingTraffic += command.size();
  }
  
  public void updateOnSyncCommand(ReplyMonitor<? extends Command, ? extends Command> monitor)
  {
    if (commandCount == 0)
    {
      startTime = System.currentTimeMillis();
    }
    
    commandCount++;
    
    averageResponseTime = (averageResponseTime * (new Float(commandCount) - 1) + new Float(System.currentTimeMillis() - monitor.getStartTime())) / new Float(commandCount);
    
    outgoingTraffic += monitor.getCommand().size();
    
    if (monitor.getReply() != null)
    {
      incomingTraffic += monitor.getReply().size();
    }
    else
    {
      unrepliedCommandCount++;
    }
  }
  
  public DataTable toDataTable()
  {
    try
    {
      return DataTableConversion.beanToTable(this, FORMAT);
    }
    catch (DataTableException ex)
    {
      throw new IllegalStateException(ex.getMessage(), ex);
    }
  }
  
  public Long getStartTime()
  {
    return startTime;
  }
  
  public Long getConnectionTime()
  {
    return startTime != null ? (System.currentTimeMillis() - startTime) : null;
  }
  
  public long getCommandCount()
  {
    return commandCount;
  }
  
  public long getEventCount()
  {
    return eventCount;
  }
  
  public long getAverageResponseTime()
  {
    return new Float(averageResponseTime).longValue();
  }
  
  public long getOutgoingTraffic()
  {
    return outgoingTraffic;
  }
  
  public long getIncomingTraffic()
  {
    return incomingTraffic;
  }
  
  public long getUnrepliedCommandCount()
  {
    return unrepliedCommandCount;
  }
}
