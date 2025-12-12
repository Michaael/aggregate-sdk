package com.tibbo.aggregate.common.widget;

import java.beans.*;

/**
 * This event should be passed to <code>collectionElementAdded</code> method of <code>CollectionChangeListener</code>
 * 
 *@see CollectionChangeListener
 */
public class CollectionElementAddedEvent extends PropertyChangeEvent
{
  private final int addedIndex;
  
  public CollectionElementAddedEvent(Object source, String propertyName, Object addedValue, int addedIndex)
  {
    super(source, propertyName, null, addedValue);
    
    this.addedIndex = addedIndex;
  }
  
  public Object getAddedValue()
  {
    return getNewValue();
  }
  
  public int getAddedIndex()
  {
    return addedIndex;
  }
}
