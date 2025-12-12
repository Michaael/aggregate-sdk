package com.tibbo.aggregate.common.widget;

import java.beans.*;

/**
 * This event should be passed to <code>collectionElementRemoved</code> method of <code>CollectionChangeListener</code>
 * 
 *@see CollectionChangeListener
 */
public class CollectionElementRemovedEvent extends PropertyChangeEvent
{
  private final int removedIndex;
  
  public CollectionElementRemovedEvent(Object source, String propertyName, Object removedValue, int removedIndex)
  {
    super(source, propertyName, removedValue, null);
    
    this.removedIndex = removedIndex;
  }
  
  public Object getRemovedValue()
  {
    return getOldValue();
  }
  
  public int getRemovedIndex()
  {
    return removedIndex;
  }
}
