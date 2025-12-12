package com.tibbo.aggregate.common.widget;

import com.tibbo.aggregate.common.widget.component.*;

public class WidgetApiUtils
{
  public static WComponent getComponentByName(WidgetTemplate widget, String name)
  {
    return widget.getComponent(name);
  }
  
  public static WConstraints getComponentDefaultConstraints(WComponent comp, int layout)
  {
    if (layout == WidgetConstants.ABSOLUTE_LAYOUT)
    {
      return new WAbsoluteConstraints();
    }
    else if (layout == WidgetConstants.GRID_LAYOUT)
    {
      return comp.getDefaultGridConstrains();
    }
    
    return null;
  }
  
  /**
   * Returns container component which contains provided component
   * 
   * @param child
   *          component that parent is being searched
   * @param widget
   *          Widget
   * @return WContainer object containing provided component
   */
  public static WContainer getComponentParent(WComponent child, WidgetTemplate widget)
  {
    return searchParentContainer(child, widget.getRootPanel());
  }
  
  /**
   * Searches <code>component</code> among given containers children. If not found tries to search among child containers.
   * 
   * @param component
   *          Component
   * @param container
   *          Container
   * @return Parent Container
   */
  public static WContainer searchParentContainer(WComponent component, WContainer container)
  {
    return component.getParent();
  }
}
