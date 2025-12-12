package com.tibbo.aggregate.common.plugin;

import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;

import com.tibbo.aggregate.common.*;
import com.tibbo.aggregate.common.context.*;
import com.tibbo.aggregate.common.security.*;
import com.tibbo.aggregate.common.util.*;
import org.java.plugin.*;
import org.java.plugin.PluginManager.*;
import org.java.plugin.registry.*;
import org.java.plugin.util.*;

public abstract class PluginDirector
{
  public final static String PLUGIN_DIRS_SEPARATOR = ",";
  
  public final static String PLUGIN_FILE_EXTENSION = ".jar";
  
  public final static String EXTENSIONS_PLUGIN_ID = "com.tibbo.aggregate.common.plugin.extensions";
  
  private PluginManager pluginManager;
  private ExtensionsPlugin extensionsPlugin;
  
  private Collection<String> allowedPlugins;
  private final Map<String, String> pluginIdMap = new LinkedHashMap();
  
  public PluginDirector(String homeDir, String additionalPluginDirs, Collection<String> allowedPlugins) throws AggreGateException
  {
    this.allowedPlugins = allowedPlugins;
    try
    {
      String pluginDirList = getPluginDirList(homeDir, additionalPluginDirs);
      
      updatePlugins();
      
      loadPlugins(pluginDirList);
      
      checkIntegrity();
      
      Plugin extentionsPlugin = pluginManager.getPlugin(PluginDirector.EXTENSIONS_PLUGIN_ID);
      if (extentionsPlugin == null)
      {
        
        throw new AggreGateException("Error loading extensions plugin (ID: '" + PluginDirector.EXTENSIONS_PLUGIN_ID + "')");
      }
      else
      {
        extensionsPlugin = (ExtensionsPlugin) extentionsPlugin;
      }
      
    }
    catch (Exception ex)
    {
      throw new AggreGateException(Cres.get().getString("pluginsErrStartingSubsystem") + ex.getMessage(), ex);
    }
  }
  
  public PluginDirector(String homeDir) throws AggreGateException
  {
    try
    {
      String pluginDirList = getPluginDirList(homeDir, null);
      
      updatePlugins();
      
      loadPlugins(pluginDirList);
    }
    catch (Exception ex)
    {
      throw new AggreGateException(Cres.get().getString("pluginsErrStartingSubsystem") + ex.getMessage(), ex);
    }
  }
  
  protected abstract void updatePlugins();
  
  public abstract ContextManager getContextManager();
  
  public abstract CallerController getCallerController();
  
  public abstract Context createGlobalConfigContext(BasePlugin plugin, Context rootContext, boolean requestReboot,
                                                    Permissions permissions, VariableDefinition... properties);
  
  public abstract Context createUserConfigContext(BasePlugin plugin, Context userContext, boolean requestReboot, VariableDefinition... properties);
  
  private void loadPlugins(String dirList) throws AggreGateException
  {
    try
    {
      Log.PLUGINS.debug("Loading all plugins from the following directories: " + dirList);
      StringTokenizer st = new StringTokenizer(dirList, PluginDirector.PLUGIN_DIRS_SEPARATOR, false);
      List<PluginLocation> pluginLocations = new LinkedList<PluginLocation>();
      while (st.hasMoreTokens())
      {
        String sourceDir = st.nextToken().trim();
        Log.PLUGINS.debug("Loading all plugins from directory: " + sourceDir);
        File folder = new File(sourceDir).getCanonicalFile();
        
        if (!folder.isDirectory())
        {
          Log.PLUGINS.warn("Plugins folder " + folder + " doesn't exist");
          continue;
        }
        
        File[] pluginFiles = folder.listFiles(new FileFilter()
        {
          @Override
          public boolean accept(final File file)
          {
            return (file.isFile() && file.getName().endsWith(PluginDirector.PLUGIN_FILE_EXTENSION));
          }
        });
        
        for (int i = 0; i < pluginFiles.length; i++)
        {
          URL manifest = new URL("jar:file:" + pluginFiles[i].getAbsolutePath() + "!/plugin.xml");
          Log.PLUGINS.debug("Located plugin " + pluginFiles[i].getName() + ", manifest file " + manifest);
          pluginLocations.add(new PluginLocationImpl(manifest, new URL("file:" + pluginFiles[i])));
        }
      }
      
      ExtendedProperties config = new ExtendedProperties();
      
      pluginManager = ObjectFactory.newInstance(config).createManager();
      
      pluginManager.publishPlugins(pluginLocations.toArray(new PluginLocationImpl[pluginLocations.size()]));
    }
    catch (Exception ex)
    {
      throw new AggreGateException(Cres.get().getString("pluginsErrLoading") + ex.getMessage(), ex);
    }
  }
  
  private String getPluginDirList(String homeDir, String additionalPluginDirs) throws AggreGateException, IOException
  {
    Log.CORE.debug("Starting plugins subsystem");
    
    String mainPluginsDir = homeDir + Constants.PLUGINS_SUBDIR;
    
    Log.PLUGINS.debug("Main plugins directory: " + mainPluginsDir);
    
    File[] pluginDirs = new File(mainPluginsDir).listFiles(new FileFilter()
    {
      @Override
      public boolean accept(final File file)
      {
        return file.isDirectory();
      }
    });
    
    String pluginDirList = "";
    int num = 0;
    
    if (pluginDirs != null)
    {
      for (File dir : pluginDirs)
      {
        pluginDirList += (num > 0 ? PluginDirector.PLUGIN_DIRS_SEPARATOR : "") + dir.getCanonicalPath();
        num++;
      }
    }
    
    if (additionalPluginDirs != null)
    {
      pluginDirList += PluginDirector.PLUGIN_DIRS_SEPARATOR + additionalPluginDirs;
    }
    
    return pluginDirList;
    
  }
  
  protected void checkIntegrity() throws AggreGateException
  {
    updateDeprecatedPrerequisites(); // backward compatibility
    
    IntegrityCheckReport integrityCheckReport = pluginManager.getRegistry().checkIntegrity(pluginManager.getPathResolver());
    Log.PLUGINS.debug("Plugin integrity check complete: errors - " + integrityCheckReport.countErrors() + ", warnings - " + integrityCheckReport.countWarnings());
    if (integrityCheckReport.countErrors() != 0)
    {
      Log.PLUGINS.warn(integrityCheckReportToString(integrityCheckReport));
      throw new AggreGateException(Cres.get().getString("pluginsIntegrityCheckFailed"));
    }
  }
  
  private String integrityCheckReportToString(IntegrityCheckReport report)
  {
    StringBuffer buf = new StringBuffer();
    buf.append("Plugin integrity check report\n");
    for (Iterator it = report.getItems().iterator(); it.hasNext();)
    {
      IntegrityCheckReport.ReportItem item = (IntegrityCheckReport.ReportItem) it.next();
      
      if (item.getCode() != IntegrityCheckReport.Error.NO_ERROR)
      {
        buf.append("\tSeverity=").append(item.getSeverity()).append(", code=").append(item.getCode()).append(", message=").append(item.getMessage()).append(", source=").append(item.getSource())
            .append("\n");
      }
    }
    return buf.toString();
  }
  
  public void stop() throws AggreGateException
  {
    try
    {
      pluginManager.shutdown();
      System.gc();
    }
    catch (Exception ex)
    {
      Log.PLUGINS.warn("Error stopping plugins subsystem", ex);
    }
  }
  
  protected String convertId(String id)
  {
    if (pluginIdMap.containsKey(id))
    {
      return pluginIdMap.get(id);
    }
    
    return id;
  }
  
  public Plugin getExistingPlugin(String id)
  {
    id = convertId(id);
    
    if (!isPluginAllowed(id))
    {
      throw new IllegalArgumentException(MessageFormat.format(Cres.get().getString("pluginNotAvail"), id));
    }
    
    try
    {
      Plugin plugin = pluginManager.getPlugin(id);
      plugin.getClass().getClassLoader().loadClass(plugin.getDescriptor().getPluginClassName());
      return plugin;
    }
    catch (Throwable ex)
    {
      throw new IllegalArgumentException(MessageFormat.format(Cres.get().getString("pluginNotAvail"), id) + ex.getMessage(), ex);
    }
  }
  
  public Plugin createNewPlugin(String id)
  {
    id = convertId(id);
    
    if (!isPluginAllowed(id))
    {
      throw new IllegalArgumentException(MessageFormat.format(Cres.get().getString("pluginNotAvail"), id));
    }
    
    BasePlugin plugin = (BasePlugin) getExistingPlugin(id);
    
    try
    {
      Log.PLUGINS.debug("Creating new instance of plugin " + id);
      
      BasePlugin instance = plugin.getClass().newInstance();
      
      instance.setId(plugin.getId());
      instance.setDescription(plugin.getDescription());
      instance.setPluginDirector(this);
      
      return instance;
    }
    catch (Exception ex)
    {
      throw new IllegalArgumentException(ex.getMessage(), ex);
    }
  }
  
  protected boolean isPluginAllowed(String id)
  {
    return (allowedPlugins == null || allowedPlugins.contains(id));
  }
  
  public PluginManager getPluginManager()
  {
    return pluginManager;
  }
  
  public ClassLoader getPluginClassLoader(String id) throws PluginLifecycleException
  {
    Plugin plugin = pluginManager.getPlugin(id);
    
    if (plugin == null)
    {
      return null;
    }
    
    return pluginManager.getPluginClassLoader(plugin.getDescriptor());
  }

  public boolean isPluginAvailable(String id) {
    id = convertId(id);
    if (!pluginManager.getRegistry().isPluginDescriptorAvailable(id)) {
      return false;
    }

    PluginDescriptor pd = pluginManager.getRegistry().getPluginDescriptor(id);

    return isPluginAllowed(id) && !pluginManager.isBadPlugin(pd) && pluginManager.isPluginEnabled(pd);
  }
  
  protected ExtensionsPlugin getExtensionsPlugin()
  {
    return extensionsPlugin;
  }
  
  public Map<String, String> getPluginIdMap()
  {
    return pluginIdMap;
  }
  
  private class PluginLocationImpl implements PluginLocation
  {
    private final URL manifest;
    private final URL plugin;
    
    public PluginLocationImpl(URL manifest, URL plugin)
    {
      this.manifest = manifest;
      this.plugin = plugin;
    }
    
    @Override
    public URL getManifestLocation()
    {
      return manifest;
    }
    
    @Override
    public URL getContextLocation()
    {
      return plugin;
    }
  }
  
  private void updateDeprecatedPrerequisites()
  {
    // TODO remove this method in version 6 (or later)
    
    // This method updates values from <requires></requires> block of the plugin descriptors.
    // This is necessary for backward compatibility.
    // In particular for packs generated by Resource Packs context before its renaming to Applications.
    
    String resourcePacksId = "com.tibbo.linkserver.plugin.context.resource-packs";
    String applicationsId = "com.tibbo.linkserver.plugin.context.applications";
    
    Map<String, String> oldToNewIdMap = Collections.singletonMap(resourcePacksId, applicationsId);
    try
    {
      for (PluginDescriptor descriptor : pluginManager.getRegistry().getPluginDescriptors())
      {
        Map<String, PluginPrerequisite> prerequisites = (Map<String, PluginPrerequisite>) ReflectUtils.getPrivateField(descriptor, "pluginPrerequisites");
        Map<String, PluginPrerequisite> oldPrerequisiteMap = new HashMap();
        
        for (String oldPrerequisiteId : oldToNewIdMap.keySet())
        {
          if (prerequisites.containsKey(oldPrerequisiteId))
          {
            oldPrerequisiteMap.put(oldPrerequisiteId, prerequisites.get(oldPrerequisiteId));
          }
        }
        
        for (String oldPrerequisiteId : oldPrerequisiteMap.keySet())
        {
          String newPrerequisiteId = oldToNewIdMap.get(oldPrerequisiteId);
          PluginPrerequisite oldPrerequisite = prerequisites.get(oldPrerequisiteId);
          
          Object model = ReflectUtils.getPrivateField(oldPrerequisite, "model");
          
          ReflectUtils.setPrivateField(model, "pluginId", newPrerequisiteId);
          ReflectUtils.setPrivateField(model, "id", "prerequisite:" + newPrerequisiteId);
          
          prerequisites.put(newPrerequisiteId, oldPrerequisite);
          prerequisites.remove(oldPrerequisiteId);
        }
      }
    }
    catch (Exception ex)
    {
      Log.PLUGINS.warn("Updating deprecated prerequisites failed", ex);
    }
  }
}
