package com.tibbo.aggregate.common.widget.component;

import java.awt.*;
import java.beans.*;
import java.util.*;

import com.tibbo.aggregate.common.datatable.*;
import com.tibbo.aggregate.common.widget.*;
import com.tibbo.aggregate.common.widget.WidgetConstants.*;
import com.tibbo.aggregate.common.widget.context.*;

abstract public class WContainer extends WAbstractComponent
{
  private int scrolling;
  private boolean scrollingAuto;
  private int layout = WidgetConstants.GRID_LAYOUT;
  private boolean showGrid = true;
  private boolean snapToGrid = false;
  private int gridStep = 10;
  private int scaling;
  private boolean maximizeScalable = false;
  private boolean useCustomScrollBars = false;
  private DataTable scrollBarProperties;
  
  public static final String V_CHILDREN = "children";
  
  private Map<WComponent, WConstraints> children = new LinkedHashMap<WComponent, WConstraints>();
  
  public WContainer(String name, String key, Class contextType, String description, String iconId, String helpId)
  {
    super(name, key, contextType, description, iconId, helpId);
    
    setBackground(Color.WHITE);
    setFocusable(false);
    this.scrollBarProperties = new DataRecord(WContainerContext.VFT_SCROLL_BAR_PROPERTIES).wrap();
  }
  
  public int getLayout()
  {
    return layout;
  }
  
  public void setLayout(int layout)
  {
    int oldLayout = this.layout;
    this.layout = layout;
    firePropertyChange("layout", oldLayout, layout);
  }
  
  public int getScrolling()
  {
    return scrolling;
  }
  
  public void setScrolling(int scrolling)
  {
    int oldScrolling = this.scrolling;
    this.scrolling = scrolling;
    firePropertyChange("scrolling", oldScrolling, scrolling);
  }
  
  public int getScaling()
  {
    return scaling;
  }
  
  public void setScaling(int scaling)
  {
    int oldScaling = this.scaling;
    this.scaling = scaling;
    firePropertyChange("scaling", oldScaling, scaling);
  }
  
  public boolean getMaximizeScalable()
  {
    return maximizeScalable;
  }
  
  public void setMaximizeScalable(boolean maximizeScalable)
  {
    boolean oldMaximizeScalable = this.maximizeScalable;
    this.maximizeScalable = maximizeScalable;
    firePropertyChange("maximize", oldMaximizeScalable, maximizeScalable);
  }
  
  public boolean isScrollingAuto()
  {
    return scrollingAuto;
  }
  
  public void setScrollingAuto(boolean scrollingAuto)
  {
    boolean oldSA = this.scrollingAuto;
    this.scrollingAuto = scrollingAuto;
    firePropertyChange("scrollingAuto", oldSA, scrollingAuto);
  }
  
  public java.util.List<WComponent> getChildren()
  {
    return new LinkedList<WComponent>(children.keySet());
  }
  
  public void add(WComponent aComponent)
  {
    add(aComponent, WidgetApiUtils.getComponentDefaultConstraints(this, getLayout()));
  }
  
  public void add(WComponent component, WConstraints cs)
  {
    setComponentConstrains(component, cs);
    component.setParent(this);
    fireCollectionElementAddedEvent(V_CHILDREN, component, 0);
  }
  
  public void setComponentConstrains(WComponent component, WConstraints cs)
  {
    if (component == null)
    {
      throw new IllegalArgumentException("Null values are not supported");
    }
    children.put(component, cs);
  }
  
  public void remove(WComponent component)
  {
    if (component == null)
      return;
    
    WConstraints cs = children.get(component);
    final WConstraints removedComponent = children.remove(component);
    if (removedComponent != null)
    {
      component.setParent(null);
      fireCollectionElementRemovedEvent(V_CHILDREN, new ComponentWithConstraints(component, cs), 0);
    }
  }
  
  public WConstraints getChildConstraints(WComponent child)
  {
    return children.get(child);
  }
  
  public void childPropertyChanged(PropertyChangeEvent evt)
  {
  }
  
  public void prepareToAcceptChild(WComponent component)
  {
  }
  
  public WComponent getChildWithGridPosition(int x, int y)
  {
    for (Map.Entry<WComponent, WConstraints> entry : children.entrySet())
    {
      WConstraints componentConstraints = entry.getValue();
      if (componentConstraints instanceof WGridConstraints)
      {
        WGridConstraints gridConstraints = (WGridConstraints) componentConstraints;
        if (gridConstraints.getGridx() == x && gridConstraints.getGridy() == y)
          return entry.getKey();
      }
    }
    
    return null;
  }
  
  public boolean isShowGrid()
  {
    return showGrid;
  }
  
  public void setShowGrid(boolean showGrid)
  {
    firePropertyChange("showGrid", this.showGrid, this.showGrid = showGrid);
  }
  
  public int getGridStep()
  {
    return gridStep;
  }
  
  public void setGridStep(int gridStep)
  {
    firePropertyChange("gridStep", this.gridStep, this.gridStep = gridStep);
  }
  
  public boolean isSnapToGrid()
  {
    return snapToGrid;
  }
  
  public void setSnapToGrid(boolean snapToGrid)
  {
    firePropertyChange("snapToGrid", this.snapToGrid, this.snapToGrid = snapToGrid);
  }
  
  public static class ComponentWithConstraints
  {
    private WComponent component;
    
    private WConstraints constraints;
    
    public WComponent getComponent()
    {
      return component;
    }
    
    public void setComponent(WComponent component)
    {
      this.component = component;
    }
    
    public WConstraints getConstraints()
    {
      return constraints;
    }
    
    public void setConstraints(WConstraints constraints)
    {
      this.constraints = constraints;
    }
    
    public ComponentWithConstraints(WComponent component, WConstraints constraints)
    {
      super();
      this.component = component;
      this.constraints = constraints;
    }
  }
  
  @Override
  public ComponentGroup getComponentGroup()
  {
    return ComponentGroup.CONTAINER;
  }
  
  @Override
  public boolean isContainer()
  {
    return true;
  }
  
  @Override
  public WGridConstraints getDefaultGridConstrains()
  {
    final WGridConstraints result = new WGridConstraints(0, 0, true, true);
    result.setGridheight(1);
    result.setGridwidth(1);
    result.setWeightx(1);
    result.setWeighty(1);
    return result;
  }
  
  @Override
  public WContainer clone()
  {
    WContainer clone = (WContainer) super.clone();
    
    clone.children = new LinkedHashMap<WComponent, WConstraints>();
    for (WComponent child : children.keySet())
    {
      WConstraints ncs = null;
      WConstraints constraints = children.get(child);
      if (constraints != null)
      {
        ncs = constraints.clone();
      }
      WComponent childClone = child.clone();
      childClone.setParent(clone);
      clone.children.put(childClone, ncs);
    }
    
    return clone;
  }
  
  public boolean isAcceptedChildComponentKey(String key)
  {
    return true;
  }
  
  public boolean isUseCustomScrollBars()
  {
    return useCustomScrollBars;
  }
  
  public void setUseCustomScrollBars(boolean useCustomScrollBars)
  {
    boolean old = this.useCustomScrollBars;
    this.useCustomScrollBars = useCustomScrollBars;
    firePropertyChange(WContainerContext.V_USE_CUSTOM_SCROLL_BARS, old, this.useCustomScrollBars);
  }
  
  public DataTable getScrollBarProperties()
  {
    return scrollBarProperties;
  }
  
  public void setScrollBarProperties(DataTable scrollBarProperties)
  {
    DataTable old = this.scrollBarProperties;
    this.scrollBarProperties = scrollBarProperties;
    firePropertyChange(WContainerContext.V_SCROLL_BAR_PROPERTIES, old, this.scrollBarProperties);
  }
}
