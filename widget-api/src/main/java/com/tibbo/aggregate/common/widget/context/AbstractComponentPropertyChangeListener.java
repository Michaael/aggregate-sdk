package com.tibbo.aggregate.common.widget.context;

import java.beans.PropertyChangeEvent;

import com.tibbo.aggregate.common.Log;
import com.tibbo.aggregate.common.context.AbstractContext;
import com.tibbo.aggregate.common.context.ContextException;
import com.tibbo.aggregate.common.context.VariableDefinition;
import com.tibbo.aggregate.common.event.FireEventRequestController;
import com.tibbo.aggregate.common.widget.component.WComponent;

/*Web property change listener to mute only specific variables.*/
public abstract class AbstractComponentPropertyChangeListener implements MutablePropertyChangeListener
{
  
  private final WContext wContext;
  
  public AbstractComponentPropertyChangeListener(WContext wContext)
  {
    this.wContext = wContext;
  }
  
  @Override
  public void propertyChange(PropertyChangeEvent evt)
  {
    wContext.componentPropertyChanged(evt);
    String prop = evt.getPropertyName();
    
    boolean canFireUpdates = wContext.getEventDefinition(AbstractContext.E_UPDATED) != null;
    
    VariableDefinition vd = wContext.getVariableDefinition(prop);
    if (canFireUpdates && vd != null && allowToFireUpdateEvent(prop))
    {
      try
      {
        wContext.fireEvent(AbstractContext.E_UPDATED, new FireEventRequestController(wContext), prop, wContext.getVariable(prop));
      }
      catch (ContextException ex)
      {
        Log.WIDGETS.error("Failed to fire event 'updated' for property '" + prop + "' in component: " + wContext.getComponent().getName() + " :" + ex.getMessage(), ex);
      }
    }
    
    if (evt.getPropertyName().equals(WComponent.V_NAME))
    {
      wContext.setContextName((String) evt.getNewValue());
    }
  }

  protected abstract boolean allowToFireUpdateEvent(String variable);

}
