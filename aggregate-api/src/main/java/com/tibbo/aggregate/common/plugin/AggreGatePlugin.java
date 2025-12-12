package com.tibbo.aggregate.common.plugin;

import com.tibbo.aggregate.common.context.*;

public interface AggreGatePlugin
{
  /**
   * Returns plugin ID
   * 
   * @return Plugin ID
   */
  String getId();
  
  /**
   * Returns plugin short ID (last "segment" of full ID).
   * 
   * @return Plugin short ID
   */
  String getShortId();
  
  /**
   * Returns plugin description
   * 
   * @return Plugin description
   */
  String getDescription();
  
  /**
   * Returns sort index used to sort plugins in the list. Plugins with zero sort index are used according to their descriptions.
   * 
   * @return Plugin sort index
   */
  int getSortIndex();
  
  /**
   * This method is called once during server startup. Its implementation will in most cases call createGlobalConfigContext() to create plugin global configuration. Another typical use is opening some
   * listening server socket to accept connections or receive network datagrams.
   * 
   * Note, that this method is called during server context tree initialization. Not all server contexts may be available at the time of its call. To access any server context during plugin startup,
   * override globalStart() method.
   * 
   * @throws PluginException
   *           If an error occurred during initialization
   */
  void globalInit(Context rootContext) throws PluginException;
  
  /**
   * This method is called once for every system user upon its creation or server startup. Its implementation will in most cases call createUserConfigContext() to create plugin user-level
   * configuration.
   * 
   * This method won't be called if user-level configuration is disabled for the user.
   * 
   * @throws PluginException
   *           If an error occurred during initialization
   */
  void userInit(Context userContext) throws PluginException;
  
  /**
   * This method is called once during server shutdown. It's usually used to terminate any threads created by the plugin and close all server sockets opened by it.
   * 
   * @throws PluginException
   *           If an error occurred during de-initialization
   */
  void globalDeinit(Context rootContext) throws PluginException;
  
  /**
   * This method is called once for every system user upon its deletion or server shutdown. The method will be called even if user-level configuration is disabled for the user.
   * 
   * @throws PluginException
   *           If an error occurred during de-initialization
   */
  void userDeinit(Context userContext) throws PluginException;
  
  /**
   * This method is called once for every plugin at the moment when server context tree is fully initialized and all contexts are available.
   * 
   * @throws PluginException
   *           If an error occurred during plugin startup
   */
  void globalStart() throws PluginException;
  
  /**
   * This method is called once for every plugin upon server shutdown.
   * 
   * @throws PluginException
   *           If an error occurred during plugin shutdown
   */
  void globalStop() throws PluginException;
  
  /**
   * This method creates and returns context containing plugin's global configuration. There is no need to keep reference to this context for future use as it may be accessed via
   * getGlobalConfigContext() method.
   * 
   * @param rootContext
   *          Server root context
   * @param requestReboot
   *          Prompt an operator to reboot server after a global plugin property change
   * @param properties
   *          List of global plugin properties
   * @return Plugin global config context
   */
  Context createGlobalConfigContext(Context rootContext, boolean requestReboot, VariableDefinition... properties);
  
  /**
   * This method creates and returns context containing plugin's user-level configuration. There is no need to keep reference to this context for future use as it may be accessed via
   * getUserConfigContext() method.
   * 
   * @param userContext
   *          Context of user to associate the configuration with
   * @param requestReboot
   *          Prompt an operator to reboot server after a global plugin property change
   * @param properties
   *          List of user-level plugin properties
   * @return Plugin user-level config context
   */
  Context createUserConfigContext(Context userContext, boolean requestReboot, VariableDefinition... properties);
  
  /**
   * Returns plugin's global configuration context
   * 
   * @return Global configuration context
   */
  Context getGlobalConfigContext();
  
  /**
   * Returns plugin's user-level configuration context or NULL if user-level configuration is disabled for the user.
   * 
   * @return User-level configuration context
   */
  Context getUserConfigContext(String username);
}
