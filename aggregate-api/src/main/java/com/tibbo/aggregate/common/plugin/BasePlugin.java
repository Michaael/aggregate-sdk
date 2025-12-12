package com.tibbo.aggregate.common.plugin;

import com.tibbo.aggregate.common.*;
import com.tibbo.aggregate.common.context.*;
import com.tibbo.aggregate.common.security.*;
import org.java.plugin.*;

public abstract class BasePlugin extends Plugin implements AggreGatePlugin, Comparable<AggreGatePlugin>
{
  public static final int INDEX_HIGHEST = 400;
  public static final int INDEX_VERY_HIGH = 300;
  public static final int INDEX_HIGH = 200;
  public static final int INDEX_HIGHER = 100;
  public static final int INDEX_NORMAL = 0;
  public static final int INDEX_LOWER = -100;
  public static final int INDEX_LOW = -200;
  public static final int INDEX_VERY_LOW = -300;
  public static final int INDEX_LOWEST = -400;
  
  private PluginDirector pluginDirector;
  
  private String id;
  private String description;
  
  private int index = INDEX_NORMAL;
  
  public BasePlugin()
  {
    super();
  }
  
  public BasePlugin(String description)
  {
    this();
    this.description = description;
  }
  
  @Override
  public String getId()
  {
    return id != null ? id : super.getDescriptor().getId();
  }
  
  public void setId(String id)
  {
    this.id = id;
  }
  
  @Override
  public String getShortId()
  {
    String id = getId();
    
    int index = id.lastIndexOf(".");
    
    return index != -1 ? id.substring(index + 1) : id;
  }
  
  @Override
  public String getDescription()
  {
    return description != null ? description : (getDescriptor().getDocumentation() != null ? getDescriptor().getDocumentation().getText() : null);
  }
  
  public void setDescription(String description)
  {
    this.description = description;
  }
  
  public PluginDirector getPluginDirector()
  {
    return pluginDirector;
  }
  
  public void setPluginDirector(PluginDirector pluginDirector)
  {
    this.pluginDirector = pluginDirector;
  }
  
  public Context createGlobalConfigContext(Context rootContext, boolean requestReboot, VariableDefinition... properties)
  {
    return pluginDirector.createGlobalConfigContext(this, rootContext, requestReboot,
        ServerPermissionChecker.getAdminPermissions(), properties);
  }
  
  public Context createGlobalConfigContext(Context rootContext, boolean requestReboot, Permissions permissions,
                                           VariableDefinition... properties)
  {
    return pluginDirector.createGlobalConfigContext(this, rootContext, requestReboot, permissions, properties);
  }
  
  public Context createUserConfigContext(Context userContext, boolean requestReboot, VariableDefinition... properties)
  {
    return pluginDirector.createUserConfigContext(this, userContext, requestReboot, properties);
  }
  
  @Override
  public Context getGlobalConfigContext()
  {
    return pluginDirector.getContextManager().get(ContextUtils.pluginGlobalConfigContextPath(getId()), pluginDirector.getCallerController());
  }
  
  @Override
  public Context getUserConfigContext(String username)
  {
    return pluginDirector.getContextManager().get(ContextUtils.pluginUserConfigContextPath(username, getId()), pluginDirector.getCallerController());
  }
  
  @Override
  public int getSortIndex()
  {
    return index;
  }
  
  protected void setIndex(int index)
  {
    this.index = index;
  }
  
  @Override
  public int compareTo(AggreGatePlugin other)
  {
    if (index != other.getSortIndex())
    {
      return other.getSortIndex() - index;
    }
    else
    {
      return getId().compareTo(other.getId());
    }
  }
  
  @Override
  protected void doStart() throws Exception
  {
    Log.PLUGINS.debug("Starting plugin: " + getDescription() + " (" + getId() + ")");
  }
  
  @Override
  protected void doStop() throws Exception
  {
    Log.PLUGINS.debug("Stopping plugin: " + getDescription() + " (" + getId() + ")");
  }
  
  @Override
  public void globalInit(Context rootContext) throws PluginException
  {
  }
  
  @Override
  public void globalDeinit(Context rootContext) throws PluginException
  {
  }
  
  @Override
  public void userInit(Context userContext) throws PluginException
  {
  }
  
  @Override
  public void userDeinit(Context userContext) throws PluginException
  {
  }
  
  @Override
  public void globalStart() throws PluginException
  {
    
  }
  
  @Override
  public void globalStop() throws PluginException
  {
    
  }
}
