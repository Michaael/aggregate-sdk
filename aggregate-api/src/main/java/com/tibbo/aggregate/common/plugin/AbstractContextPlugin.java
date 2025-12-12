package com.tibbo.aggregate.common.plugin;

import com.tibbo.aggregate.common.context.ContextException;
import com.tibbo.aggregate.common.context.ContextManager;
import com.tibbo.aggregate.common.server.ServerContext;

public abstract class AbstractContextPlugin extends BasePlugin implements ContextPlugin
{
  public AbstractContextPlugin()
  {
    super();
  }
  
  public AbstractContextPlugin(String name)
  {
    super(name);
  }
  
  public void initialize() throws PluginException
  {
  }
  
  public void deinitialize() throws PluginException
  {
  }
  
  public void install(ContextManager<ServerContext> cm) throws ContextException, PluginException
  {
  }
  
  public void deinstall(ContextManager<ServerContext> cm) throws ContextException, PluginException
  {
  }
  
  public void install(ServerContext context) throws ContextException, PluginException
  {
  }
  
  public void deinstall(ServerContext context) throws ContextException, PluginException
  {
  }
  
  public void launch() throws PluginException
  {
  }
  
  public void shutdown() throws PluginException
  {
  }
}
