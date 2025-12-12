package com.tibbo.aggregate.common.widget;

import java.beans.*;

/**
 * This class wraps <code>CollectionListenerAdapter</code> to invoke its specific methods (<code>collectionElementAdded</code> and <code>collectionElementAdded</code> ). This method inherit
 * <code>PropertyChangeListener</code> and suitable instead of it.
 * 
 * @see CollectionChangeListener
 */
public class CollectionListenerAdapter implements PropertyChangeListener
{
  private final CollectionChangeListener listener;
  
  public CollectionListenerAdapter(CollectionChangeListener listener)
  {
    this.listener = listener;
  }
  
  public void propertyChange(PropertyChangeEvent evt)
  {
    if (evt instanceof CollectionElementAddedEvent)
    {
      listener.collectionElementAdded((CollectionElementAddedEvent) evt);
    }
    else if (evt instanceof CollectionElementRemovedEvent)
    {
      listener.collectionElementRemoved((CollectionElementRemovedEvent) evt);
    }
  }
  
  public CollectionChangeListener getListener()
  {
    return listener;
  }
}
