package com.tibbo.aggregate.common.widget;

import java.util.*;

/**
 * Special event listener interface for {@link java.util.Collection} properties. It is useful when some entry were added to Collection or removed. It is still same Collection object and usual
 * {@link java.beans.PropertyChangeListener} unsuitable.
 * 
 */
public interface CollectionChangeListener extends EventListener
{
  void collectionElementAdded(CollectionElementAddedEvent event);
  
  void collectionElementRemoved(CollectionElementRemovedEvent event);
}
