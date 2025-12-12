package com.tibbo.aggregate.common.protocol;

import java.io.*;
import java.util.*;

import org.json.simple.*;

import com.tibbo.aggregate.common.*;
import com.tibbo.aggregate.common.util.*;

public class OutgoingJsonCommand extends OutgoingAggreGateCommand
{
  private final List<String> params = new LinkedList<>();
  
  public OutgoingJsonCommand()
  {
    super();
  }
  
  @Override
  public OutgoingJsonCommand addParam(String param)
  {
    if (completed)
    {
      Log.COMMANDS.error("Error, modify a complete command !");
      return this;
    }
    
    if (paramCount++ == AggreGateCommand.INDEX_ID)
      this.id = param;
    
    params.add(param);
    
    return this;
  }
  
  @Override
  public void complete()
  {
    if (completed)
      return;
    
    super.complete();
    
    try
    {
      write(JSONArray.toJSONString(params).getBytes(StringUtils.UTF8_CHARSET));
      
      params.clear();
    }
    catch (IOException ex)
    {
      Log.COMMANDS.warn(ex.getMessage(), ex);
    }
  }
  
}
