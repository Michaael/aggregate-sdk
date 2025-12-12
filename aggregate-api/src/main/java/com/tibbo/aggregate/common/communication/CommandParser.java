package com.tibbo.aggregate.common.communication;

import java.io.*;

import com.tibbo.aggregate.common.device.*;
import com.tibbo.aggregate.common.util.*;

public interface CommandParser<C extends Command>
{
  void addData(int dataByte);
  
  C readCommand() throws IOException, DisconnectionException, SyntaxErrorException;
  
  void reset();
  
  void setListener(CommandParserListener listener);
}