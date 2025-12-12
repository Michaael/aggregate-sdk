package com.tibbo.aggregate.common.widget.context;

/*Legacy property change listener to preserve widgets functionality. It has global mute flag.*/
class ComponentPropertyChangeListener extends AbstractComponentPropertyChangeListener implements MutablePropertyChangeListener
{
  private volatile boolean allowUpdateEvents = true;
  
  public ComponentPropertyChangeListener(WContext wContext)
  {
    super(wContext);
  }
  
  @Override
  protected boolean allowToFireUpdateEvent(String variable)
  {
    return allowUpdateEvents;
  }
  
  public void muteUpdateEvents(String variable)
  {
    allowUpdateEvents = false;
  }
  
  public void allowUpdateEvents(String variable)
  {
    allowUpdateEvents = true;
  }
}
