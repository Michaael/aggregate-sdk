package com.tibbo.aggregate.common.plugin;

import java.net.*;
import java.util.*;

import com.tibbo.aggregate.common.*;
import com.tibbo.aggregate.common.context.*;
import com.tibbo.aggregate.common.security.*;
import org.java.plugin.*;
import org.java.plugin.registry.*;

public class ComponentPluginDirector extends PluginDirector
{
  private final static String EXTENSION_POINT_COMPONENT = "component";
  
  private final static String EXTENSIONS_PLUGIN_DESCRIPTOR = "plugin.xml";
  
  public ComponentPluginDirector(String homeDir) throws AggreGateException
  {
    super(homeDir);
    try
    {
      getPluginManager().getRegistry().register(new URL[] { this.getClass().getResource(EXTENSIONS_PLUGIN_DESCRIPTOR) });
      checkIntegrity();
    }
    catch (ManifestProcessingException ex)
    {
      Log.WIDGETS.error("Error creating plugin director: " + ex.getMessage(), ex);
    }
  }
  
  public Collection getPlugins(String extensionPoint)
  {
    LinkedList res = new LinkedList();
    for (Iterator<Extension> it = getExtensionPoint(extensionPoint).getConnectedExtensions().iterator(); it.hasNext();)
    {
      Extension ext = it.next();
      String id = ext.getDeclaringPluginDescriptor().getId();
      
      if (!isPluginAllowed(id))
      {
        continue;
      }
      
      Plugin plugin = getExistingPlugin(id);
      
      if (ComponentPlugin.class.isAssignableFrom(plugin.getClass()))
      {
        res.add(plugin);
      }
    }
    
    return res;
  }
  
  @Override
  protected void updatePlugins()
  {
    
  }
  
  protected ExtensionPoint getExtensionPoint(String name)
  {
    return getPluginManager().getRegistry().getExtensionPoint(EXTENSIONS_PLUGIN_ID, name);
  }
  
  public Collection<ComponentPlugin> getComponentPlugins()
  {
    return getPlugins(EXTENSION_POINT_COMPONENT);
  }
  
  @Override
  public ContextManager getContextManager()
  {
    return null;
  }
  
  @Override
  public CallerController getCallerController()
  {
    return null;
  }
  
  @Override
  public Context createGlobalConfigContext(BasePlugin plugin, Context rootContext, boolean requestReboot,
                                           Permissions permissions, VariableDefinition... properties)
  {
    return null;
  }
  
  @Override
  public Context createUserConfigContext(BasePlugin plugin, Context userContext, boolean requestReboot, VariableDefinition... properties)
  {
    return null;
  }
}
