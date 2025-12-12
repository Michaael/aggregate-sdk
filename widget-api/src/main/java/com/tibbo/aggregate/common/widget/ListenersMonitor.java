package com.tibbo.aggregate.common.widget;

import java.beans.*;
import java.util.*;

import com.tibbo.aggregate.common.*;
import com.tibbo.aggregate.common.widget.component.*;

public class ListenersMonitor
{
  private final Map<WComponent, List<PropertyChangeListener>> listenersMap = new LinkedHashMap<WComponent, List<PropertyChangeListener>>();
  
  private String attentionOn = null;
  
  private final Map<PropertyChangeListener, String> places = new LinkedHashMap<PropertyChangeListener, String>();
  
  public void listenerAdded(WComponent component, PropertyChangeListener listener, String place)
  {
    ensureComponentList(component);
    listenersMap.get(component).add(listener);
    places.put(listener, place);
    if (attentionOn == null || attentionOn.equals(component.getName()))
      Log.GUIBUILDER.debug("+ component '" + component.getName() + "'(" + component.hashCode() + ") got listener: " + listener.hashCode());
  }
  
  public String getAttentionOn()
  {
    return attentionOn;
  }
  
  public void setAttentionOn(String attentionOn)
  {
    this.attentionOn = attentionOn;
  }
  
  public void ensureComponentList(WComponent component)
  {
    if (!listenersMap.containsKey(component))
    {
      listenersMap.put(component, new LinkedList<PropertyChangeListener>());
    }
  }
  
  public void listenerRemoved(WComponent component, PropertyChangeListener listener)
  {
    if (!listenersMap.containsKey(component))
    {
      throw new IllegalStateException("Unknown component '" + component.getName() + "' in listeners monitor");
    }
    listenersMap.get(component).remove(listener);
    if (attentionOn == null || attentionOn.equals(component.getName()))
      Log.GUIBUILDER.debug("- component '" + component.getName() + "'(" + component.hashCode() + ") removed listener: " + listener.hashCode());
  }
  
  public void reportRemained()
  {
    for (WComponent c : listenersMap.keySet())
    {
      List<PropertyChangeListener> list = listenersMap.get(c);
      if (list.size() > 0)
      {
        Log.GUIBUILDER.warn("Component '" + c.getName() + "'(" + c.hashCode() + ") has " + list.size() + " listeners remained");
      }
      for (PropertyChangeListener item : list)
      {
        Log.GUIBUILDER.warn("-- listener " + item.hashCode() + " from: " + places.get(item));
      }
    }
  }
}