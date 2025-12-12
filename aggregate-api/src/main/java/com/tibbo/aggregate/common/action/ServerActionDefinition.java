package com.tibbo.aggregate.common.action;

import java.util.*;

public class ServerActionDefinition extends BasicActionDefinition
{
  private Integer index = ActionUtils.INDEX_NORMAL;
  private boolean headless;
  
  public ServerActionDefinition(String name)
  {
    super(name);
  }
  
  public ServerActionDefinition(String name, String description)
  {
    this(name);
    setDescription(description);
  }
  
  public ServerActionDefinition(String name, Class actionClass)
  {
    super(name, actionClass);
  }
  
  public ServerActionDefinition(String name, String description, Class actionClass)
  {
    this(name, actionClass);
    setDescription(description);
  }
  
  @Override
  public int compareTo(Object o)
  {
    if (o instanceof ServerActionDefinition)
    {
      return ((ServerActionDefinition) o).index - index;
    }
    
    return 0;
  }
  
  @Override
  public String toString()
  {
    return getDescription() != null ? getDescription() : getName();
  }
  
  public void setAcceptedContextTypes(String... types)
  {
    LinkedList<ResourceMask> masks = new LinkedList();
    for (String type : types)
    {
      if (type != null)
      {
        masks.add(new TreeMask(type));
      }
    }
    
    setDropSources(masks);
  }
  
  public Integer getIndex()
  {
    return index;
  }
  
  @Override
  public boolean isHeadless()
  {
    return headless;
  }
  
  public void setIndex(int index)
  {
    this.index = index;
  }
  
  public void setHeadless(boolean headless)
  {
    this.headless = headless;
  }
}
