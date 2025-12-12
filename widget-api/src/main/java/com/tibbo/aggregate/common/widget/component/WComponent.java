package com.tibbo.aggregate.common.widget.component;

import java.awt.*;
import java.beans.*;

import com.tibbo.aggregate.common.datatable.*;
import com.tibbo.aggregate.common.util.*;
import com.tibbo.aggregate.common.widget.*;
import com.tibbo.aggregate.common.widget.WidgetConstants.*;
import com.tibbo.aggregate.common.widget.context.*;

/**
 * <p>
 * Title: WComponent
 * </p>
 * <p>
 * Description: Basic widget component class. It is JavaBean. So it has no logic and contains only component properties and their getters and setters. This class is self describing. Properties such as
 * <code>key</code>, <code>contextType</code>, <code>description</code> and <code>descriptioniconId</code> are auxiliary and brings necessary information about component type.
 * {@link WidgetComponentFactory} class use this properties to generate component instance automatically (and {@link WComponentContext} too).
 * </p>
 * <p>
 * Fields <code>name</code>, <code>height</code> and <code>width</code> are common component properties describing component as unique unit in widget template.
 * </p>
 * <p>
 * <code>changeSupport</code> fields holds {@link WPropertyChangeSupport} instance that carries property change listeners and generates property change events. This class also has special method
 * interfacing <code>changeSupport</code> (such as {@link #addPropertyChangeListener}, {@link #fireCollectionElementAddedEvent}, {@link #fireCollectionElementRemovedEvent}, {@link #firePropertyChange}
 * ). All this provides event listeners mechanism.
 * </p>
 */
public abstract class WComponent implements Cloneable, PublicCloneable, IComponent
{
  private String key;
  
  private final Class contextType;
  
  private WPropertyChangeSupport changeSupport;
  
  private int width;
  
  private int height;
  
  private String name;
  
  private final String description;
  
  private final String iconId;
  
  public static final String V_NAME = "name";
  
  public static final String V_HEIGHT = "height";
  
  public static final String V_WIDTH = "width";
  
  public static final String V_CUSTOM_PROPERTIES = "customProperties";
  
  private boolean headless = false;
  
  private final String helpId;
  
  private WContainer parent;
  
  private DataTable customProperties;
  
  public WComponent(String name, String key, Class contextType, String description, String iconId, String helpId)
  {
    this.name = name;
    this.key = key;
    this.contextType = contextType;
    this.description = description;
    this.iconId = iconId;
    this.helpId = helpId;
    
    customProperties = new SimpleDataTable(WidgetConstants.CUSTOM_PROPERTIES_FORMAT);
  }
  
  public abstract boolean shouldSkipPropertyEncoding(String name);
  
  public String getDescription()
  {
    return description;
  }
  
  public String getIconId()
  {
    return iconId;
  }
  
  public String getHelpId()
  {
    return helpId;
  }
  
  public Class getContextType()
  {
    return contextType;
  }
  
  @Override
  public String getName()
  {
    return name;
  }
  
  @Override
  public void setName(String name)
  {
    String oldName = this.name;
    this.name = name;
    firePropertyChange(V_NAME, oldName, name);
  }
  
  public WContainer getParent()
  {
    return parent;
  }
  
  public void setParent(WContainer parent)
  {
    this.parent = parent;
  }
  
  public void firePropertyChange(String propertyName, Object oldValue, Object newValue)
  {
    WPropertyChangeSupport changeSupport = this.changeSupport;
    if (changeSupport == null || Util.equals(oldValue, newValue))
    {
      return;
    }
    changeSupport.firePropertyChange(propertyName, oldValue, newValue);
  }
  
  public void fireCollectionElementAddedEvent(String propertyName, Object addedValue, int addedIndex)
  {
    WPropertyChangeSupport changeSupport = this.changeSupport;
    if (changeSupport == null)
    {
      return;
    }
    changeSupport.fireCollectionElementAddedEvent(propertyName, addedValue, addedIndex);
  }
  
  public void fireCollectionElementRemovedEvent(String propertyName, Object removedValue, int removedIndex)
  {
    WPropertyChangeSupport changeSupport = this.changeSupport;
    if (changeSupport == null)
    {
      return;
    }
    changeSupport.fireCollectionElementRemovedEvent(propertyName, removedValue, removedIndex);
  }
  
  @Override
  public synchronized void addPropertyChangeListener(PropertyChangeListener listener)
  {
    if (listener == null)
    {
      return;
    }
    if (changeSupport == null)
    {
      changeSupport = new WPropertyChangeSupport(this);
    }
    
    for (PropertyChangeListener changeListener : changeSupport.getPropertyChangeListeners())
    {
      if (changeListener.equals(listener))
        return;
    }
    
    changeSupport.addPropertyChangeListener(listener);
  }
  
  @Override
  public synchronized void removePropertyChangeListener(PropertyChangeListener listener)
  {
    if (listener == null || changeSupport == null)
    {
      return;
    }
    
    changeSupport.removePropertyChangeListener(listener);
  }
  
  public int getHeight()
  {
    return height;
  }
  
  public int getWidth()
  {
    return width;
  }
  
  @Override
  public String toString()
  {
    return "Widget Component: '" + name + "' (" + super.toString() + ")";
  }
  
  public void setWidth(int width)
  {
    if (width < 0)
    {
      throw new IllegalArgumentException("Wrong value for width: " + Integer.toString(width));
    }
    
    int oldWidth = this.width;
    this.width = width;
    firePropertyChange(V_WIDTH, oldWidth, width);
  }
  
  public void setHeight(int height)
  {
    if (height < 0)
    {
      throw new IllegalArgumentException("Wrong value for height: " + Integer.toString(height));
    }
    
    int oldHeight = this.height;
    this.height = height;
    firePropertyChange(V_HEIGHT, oldHeight, height);
  }
  
  public void setSize(Point size)
  {
    setWidth(size.x);
    setHeight(size.y);
  }
  
  public void setSizeFiringHeightChange(int w, int h)
  {
    width = w;
    setHeight(h);
  }
  
  public boolean isResizeProportionally()
  {
    return false;
  }
  
  @Override
  public String getKey()
  {
    return key;
  }
  
  protected void setKey(String key)
  {
    this.key = key;
  }
  
  public boolean isContainer()
  {
    return false;
  }
  
  public boolean isDependentContainer()
  {
    return false;
  }
  
  public boolean isDependentContainerHolder()
  {
    return false;
  }
  
  public boolean isBasic()
  {
    return true;
  }
  
  @Override
  public WComponent clone()
  {
    try
    {
      WComponent clone = (WComponent) super.clone();
      
      clone.changeSupport = null;
      
      if (customProperties != null)
      {
        clone.customProperties = customProperties.clone();
      }
      
      return clone;
    }
    catch (CloneNotSupportedException ex)
    {
      throw new IllegalStateException(ex.getMessage(), ex);
    }
  }
  
  public void setHeadless(boolean v)
  {
    headless = v;
  }
  
  public boolean isHeadless()
  {
    return headless;
  }
  
  public String treeSortProperty()
  {
    return "name";
  }
  
  public boolean greaterThan(WComponent other)
  {
    return other != null && (getName().compareToIgnoreCase(other.getName()) > 0);
  }
  
  public ComponentGroup getComponentGroup()
  {
    return ComponentGroup.CONTENT;
  }
  
  public WGridConstraints getDefaultGridConstrains()
  {
    WGridConstraints result = new WGridConstraints();
    result.setFill(WGridConstraints.FILL_NONE);
    result.setGridx(0);
    result.setGridy(0);
    result.setGridheight(1);
    result.setGridwidth(1);
    result.setWeightx(1);
    result.setWeighty(1);
    return result;
  }
  
  @Override
  public Object getProperty(String name)
  {
    throw new UnsupportedOperationException();
  }
  
  @Override
  public boolean hasProperty(String name)
  {
    throw new UnsupportedOperationException();
  }
  
  @Override
  public void setProperty(String name, String value)
  {
    throw new UnsupportedOperationException();
  }
  
  @Override
  public DataTable getCustomProperty(String name)
  {
    DataRecord rec = customProperties.select(WidgetConstants.F_NAME, name);
    if (rec != null)
    {
      return rec.getDataTable(WidgetConstants.F_VALUE).clone();
    }
    
    return null;
  }
  
  @Override
  public void setCustomProperty(String name, DataTable newDataTable)
  {
    DataRecord rec = customProperties.select(WidgetConstants.F_NAME, name);
    if (rec != null)
    {
      final DataTable o = rec.getDataTable(WidgetConstants.F_VALUE);
      rec.setValue(WidgetConstants.F_VALUE, newDataTable);
      firePropertyChange(name, o, newDataTable);
    }
  }
  
  @Override
  public boolean hasCustomProperty(String name)
  {
    return getCustomProperties().select(WidgetConstants.F_NAME, name) != null;
  }
  
  @Override
  public void setOrCreateCustomProperty(String name, DataTable newDataTable, String description, String help)
  {
    DataRecord rec = customProperties.select(WidgetConstants.F_NAME, name);
    if (rec != null)
    {
      setCustomProperty(name, newDataTable);
    }
    else
    {
      if (description == null)
      {
        description = name;
      }
      
      customProperties.addRecord(name, newDataTable, description, help);
      
      firePropertyChange(name, null, newDataTable);
    }
  }
  
  @Override
  public DataTable removeCustomProperty(String name)
  {
    DataRecord rec = customProperties.select(WidgetConstants.F_NAME, name);
    if (rec != null)
    {
      customProperties.removeRecords(rec);
    }
    return rec.getDataTable(WidgetConstants.F_VALUE);
  }
  
  @Override
  public DataTable getCustomProperties()
  {
    return customProperties;
  }
  
  @Override
  public void setCustomProperties(DataTable value)
  {
    DataTable old = customProperties;
    customProperties = value;
    firePropertyChange(V_CUSTOM_PROPERTIES, old, value);
  }
}
