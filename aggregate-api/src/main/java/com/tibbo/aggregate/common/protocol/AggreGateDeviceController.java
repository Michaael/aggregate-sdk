package com.tibbo.aggregate.common.protocol;

import java.io.*;
import java.util.*;

import com.tibbo.aggregate.common.communication.*;
import com.tibbo.aggregate.common.context.*;
import com.tibbo.aggregate.common.device.*;
import com.tibbo.aggregate.common.protocol.*;
import com.tibbo.aggregate.common.util.*;

public interface AggreGateDeviceController<I extends Command, O extends Command> extends RemoteConnector
{
  AggreGateDevice getDevice();
  
  void connect() throws DisconnectionException, IOException, InterruptedException, RemoteDeviceErrorException, ContextException;
  
  void disconnect() throws IOException, InterruptedException, RemoteDeviceErrorException;
  
  void login() throws ContextException;
  
  void start() throws IOException, InterruptedException, ContextException, RemoteDeviceErrorException;
  
  boolean isActive();
  
  CommandProcessorStatistics getStatistics();
  
  I sendCommand(O cmd) throws DisconnectionException, IOException, InterruptedException, RemoteDeviceErrorException, ContextException;
  
  List<ReplyMonitor<OutgoingAggreGateCommand, IncomingAggreGateCommand>> getActiveCommands();
}
