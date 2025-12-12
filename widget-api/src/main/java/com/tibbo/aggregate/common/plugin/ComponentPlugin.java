package com.tibbo.aggregate.common.plugin;

import org.java.plugin.*;

public abstract class ComponentPlugin extends Plugin
{
  public abstract Class getSwingRenderer();
  
  public abstract Class getWComponent();
  
  public Class getEditorRendererSupport()
  {
    return null;
  }
  
  public String getId()
  {
    String id = getDescriptor().getPluginClassName();

    return id.substring(id.lastIndexOf(".") + 1, id.length());
  }
}