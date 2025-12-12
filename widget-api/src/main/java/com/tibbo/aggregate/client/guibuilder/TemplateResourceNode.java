package com.tibbo.aggregate.client.guibuilder;

import java.util.*;

import javax.swing.tree.*;

import com.tibbo.aggregate.common.widget.*;
import com.tibbo.aggregate.common.widget.component.*;
import com.tibbo.aggregate.common.widget.engine.*;

public class TemplateResourceNode implements TreeNode
{
  public static String BRACKETS_OPEN = " (";
  
  public static String BRACKETS_CLOSE = ")";
  
  private final TemplateResource resource;
  
  private TemplateResourceNode parent;
  
  final List<TemplateResourceNode> children = new LinkedList<TemplateResourceNode>();
  
  private final String key;
  
  private final AggreGateIDE guiBuilder;
  
  /*  
   * Widget-API: this class is moved here from TemplateResourcesTreeModel.
   */
  
  public TemplateResourceNode(AggreGateIDE guiBuilder, TemplateResourceNode parent, TemplateResource resource, String key)
  {
    this.resource = resource;
    this.parent = parent;
    this.key = key;
    this.guiBuilder = guiBuilder;
  }
  
  public String getComponentNameNote()
  {
    String name = "";
    // Button Groups node has no component. We set description manually.
    if (key.equals(WidgetConstants.COMPONENT_BUTTON_GROUPS))
    {
      name = Res.get().getString("gbButtonGroups");
    }
    else
    {
      name = guiBuilder.getWComponent(key).getDescription();
    }
    if (name.length() == 0)
    {
      return "";
    }
    return BRACKETS_OPEN + name + BRACKETS_CLOSE;
  }
  
  @Override
  public String toString()
  {
    return resource.getID() + getComponentNameNote();
  }
  
  public void addChild(int pos, TemplateResourceNode newNode)
  {
    synchronized (children)
    {
      children.add(pos, newNode);
    }
    newNode.getResource().setParent(this.getResource());
    newNode.setParent(this);
    guiBuilder.getDataModel().addResource(newNode.getResource());
  }
  
  @Override
  public TreeNode getChildAt(int childIndex)
  {
    return children.get(childIndex);
  }
  
  @Override
  public int getChildCount()
  {
    return children.size();
  }
  
  @Override
  public TreeNode getParent()
  {
    return parent;
  }
  
  @Override
  public int getIndex(TreeNode node)
  {
    return children.indexOf(node);
  }
  
  @Override
  public boolean getAllowsChildren()
  {
    if (resource.getID().equals(WidgetConstants.BUTTON_GROUPS_RESOURCE_ID))
    {
      return true;
    }
    WComponent c = guiBuilder.getWComponent(key);
    return c != null && c.isContainer();
  }
  
  @Override
  public boolean isLeaf()
  {
    if (resource.getID().equals(WidgetConstants.BUTTON_GROUPS_RESOURCE_ID))
    {
      return false;
    }
    
    return !getAllowsChildren();
  }
  
  @Override
  public Enumeration children()
  {
    return Collections.enumeration(children);
  }
  
  public boolean isRoot()
  {
    return parent == null;
  }
  
  public void setParent(TemplateResourceNode parent)
  {
    this.parent = parent;
    resource.setParent(parent.getResource());
  }
  
  public TemplateResource getResource()
  {
    return resource;
  }
  
  /**
   * Sets new resource and node ID
   * 
   * @param ID
   *          String
   */
  public void setID(String ID)
  {
    if (!resource.getID().equals(ID))
    {
      resource.setID(ID);
    }
  }
  
  public void removeChild(TemplateResourceNode childNode)
  {
    children.remove(childNode);
    guiBuilder.getDataModel().removeResource(childNode.getResource().getID());
  }
  
  public String getKey()
  {
    return key;
  }
  
  public List<TemplateResourceNode> getChildren()
  {
    return children;
  }
}
