package com.tibbo.aggregate.common.widget.context;

import java.beans.PropertyChangeListener;

public interface MutablePropertyChangeListener extends PropertyChangeListener
{
  void muteUpdateEvents(String variable);

  void allowUpdateEvents(String variable);
}
