package com.tibbo.aggregate.common.agent;

import java.util.concurrent.*;

import com.tibbo.aggregate.common.context.*;
import com.tibbo.aggregate.common.data.*;
import com.tibbo.aggregate.common.datatable.*;
import com.tibbo.aggregate.common.datatable.encoding.*;
import com.tibbo.aggregate.common.event.*;
import com.tibbo.aggregate.common.protocol.*;
import com.tibbo.aggregate.common.util.*;

public class AgentImplementationController extends DefaultClientController
{
  public AgentImplementationController(BlockingChannel dataChannel, ContextManager contextManager, ExecutorService commandExecutionService, FormatCache formatCache,
      DataTableRegistry dataTableRegistry, Integer maxEventQueueLength)
  {
    super(dataChannel, contextManager, commandExecutionService, formatCache, dataTableRegistry, maxEventQueueLength);
  }
  
  @Override
  protected OutgoingAggreGateCommand processMessageOperation(IncomingAggreGateCommand cmd, OutgoingAggreGateCommand ans) throws SyntaxErrorException, ContextException
  {
    ans = super.processMessageOperation(cmd, ans);
    
    String context = cmd.getParameter(AggreGateCommand.INDEX_OPERATION_CONTEXT).getString();
    
    Context con = getContext(context);
    
    if (con != null)
    {
      addNormalListener(con.getPath(), AbstractContext.E_UPDATED, getDefaultEventListener());
    }
    return ans;
  }
  
  @Override
  public boolean controllerShouldHandle(Event ev, ContextEventListener listener) throws EventHandlingException
  {
    return true;
  }
  
  @Override
  protected ClassicEncodingSettings createClassicEncodingSettings(boolean useFormatCache)
  {
    ClassicEncodingSettings encodingSettings = super.createClassicEncodingSettings(useFormatCache);
    encodingSettings.setEncodeFormat(!useFormatCache);
    return encodingSettings;
  }
  
  public KnownFormatCollector getKnownFormatCollector()
  {
    return this.knownFormatCollector;
  }
}
