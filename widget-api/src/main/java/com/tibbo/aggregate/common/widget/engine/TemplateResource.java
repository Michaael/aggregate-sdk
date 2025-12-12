package com.tibbo.aggregate.common.widget.engine;

/**
 * Description: Tree node for TemplateResourcesTreeModel
 */
public class TemplateResource
{
  private String ID;
  
  private TemplateResource parent;
  
  public TemplateResource(String ID, TemplateResource parent)
  {
    this.ID = ID;
    this.parent = parent;
  }
  
  public void setID(String ID)
  {
    this.ID = ID;
  }
  
  public void setParent(TemplateResource parent)
  {
    this.parent = parent;
  }
  
  public String getID()
  {
    return ID;
  }
  
  public TemplateResource getParent()
  {
    return parent;
  }
  
  @Override
  public String toString()
  {
    return getID();
  }
}
