package com.tibbo.aggregate.common.context;

import com.tibbo.aggregate.common.data.*;

public class QueuedEvent
{
  private final EventData eventData;
  private final Event event;
  
  public QueuedEvent(EventData ed, Event ev)
  {
    this.eventData = ed;
    this.event = ev;
  }
  
  public void dispatch()
  {
    eventData.dispatch(event);
  }
  
  public Event getEvent()
  {
    return event;
  }
  
  public EventData getEventData()
  {
    return eventData;
  }
}