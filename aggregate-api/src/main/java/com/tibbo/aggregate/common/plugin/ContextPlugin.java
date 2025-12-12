package com.tibbo.aggregate.common.plugin;

import com.tibbo.aggregate.common.context.ContextException;
import com.tibbo.aggregate.common.context.ContextManager;
import com.tibbo.aggregate.common.server.ServerContext;

public interface ContextPlugin extends AggreGatePlugin
{
  /**
   * This method is called right after plugin creation.
   */
  void initialize() throws PluginException;
  
  /**
   * This method is called right before plugin destruction.
   */
  void deinitialize() throws PluginException;
  
  /**
   * This method is called when server context tree is fully loaded.
   * 
   * @param contextManager
   *          Server context manager
   * @throws ContextException
   *           If some context API calls from within this method have thrown an exception
   * @throws PluginException
   *           If plugin internal error occurred
   */
  void install(ContextManager<ServerContext> contextManager) throws ContextException, PluginException;
  
  /**
   * This method is called before server context tree is destroyed.
   * 
   * @param contextManager
   *          Server context manager
   * @throws ContextException
   *           If some context API calls from within this method have thrown an exception
   * @throws PluginException
   *           If plugin internal error occurred
   */
  void deinstall(ContextManager<ServerContext> contextManager) throws ContextException, PluginException;
  
  /**
   * This method is called when a new server context is created or loaded upon server startup.
   * 
   * @param context
   *          Server context
   * @throws ContextException
   *           If some context API calls from within this method have thrown an exception
   * @throws PluginException
   *           If plugin internal error occurred
   */
  void install(ServerContext context) throws ContextException, PluginException;
  
  /**
   * This method is called when a server context is destroyed or removed upon server shutdown.
   * 
   * @param context
   *          Server context
   * @throws ContextException
   *           If some context API calls from within this method have thrown an exception
   * @throws PluginException
   *           If plugin internal error occurred
   */
  void deinstall(ServerContext context) throws ContextException, PluginException;
  
  /**
   * This method is called in the very end of server startup.
   * 
   * @throws PluginException
   *           If plugin internal error occurred
   * 
   */
  public void launch() throws PluginException;
  
  /**
   * This method is called in the very beginning of server shutdown.
   * 
   * @throws PluginException
   *           If plugin internal error occurred
   * 
   */
  public void shutdown() throws PluginException;
}
