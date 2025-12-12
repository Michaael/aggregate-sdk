package com.tibbo.aggregate.common.widget;

import java.beans.*;

public class WPropertyChangeSupport extends PropertyChangeSupport
{
  private Object sourceBean;
  
  private static final ListenersMonitor listenersMonitor = new ListenersMonitor();
  
  public WPropertyChangeSupport(Object sourceBean)
  {
    super(sourceBean);
    
    this.sourceBean = sourceBean;
  }
  
  public void fireCollectionElementAddedEvent(String propertyName, Object addedValue, int addedIndex)
  {
    firePropertyChange(new CollectionElementAddedEvent(sourceBean, propertyName, addedValue, addedIndex));
  }
  
  public void fireCollectionElementRemovedEvent(String propertyName, Object removedValue, int removedIndex)
  {
    firePropertyChange(new CollectionElementRemovedEvent(sourceBean, propertyName, removedValue, removedIndex));
  }
  
  public void firePropertyChange(String propertyName, float oldValue, float newValue)
  {
    if (oldValue == newValue)
    {
      return;
    }
    
    firePropertyChange(propertyName, new Float(oldValue), new Float(newValue));
  }
  
  public static ListenersMonitor getListenersMonitor()
  {
    return listenersMonitor;
  }
  
  public void firePropertyChange(String propertyName, double oldValue, double newValue)
  {
    if (oldValue == newValue)
    {
      return;
    }
    
    firePropertyChange(propertyName, new Double(oldValue), new Double(newValue));
  }
}
