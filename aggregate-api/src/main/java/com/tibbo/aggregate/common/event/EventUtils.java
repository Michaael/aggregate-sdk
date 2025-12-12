package com.tibbo.aggregate.common.event;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;

import com.tibbo.aggregate.common.*;
import com.tibbo.aggregate.common.context.*;
import com.tibbo.aggregate.common.data.Event;
import com.tibbo.aggregate.common.datatable.*;
import com.tibbo.aggregate.common.util.*;

public class EventUtils
{
  public static final Color COLOR_FATAL = new Color(240, 120, 120);
  public static final Color COLOR_ERROR = new Color(240, 190, 120);
  public static final Color COLOR_WARNING = new Color(240, 240, 120);
  public static final Color COLOR_INFO = new Color(160, 240, 120);
  public static final Color COLOR_NOTICE = new Color(120, 150, 240);
  public static final Color COLOR_NONE = new Color(230, 230, 230);
  
  private static Map<Integer, Color> COLORS = new Hashtable();
  static
  {
    COLORS.put(EventLevel.NONE, EventUtils.COLOR_NONE);
    COLORS.put(EventLevel.NOTICE, EventUtils.COLOR_NOTICE);
    COLORS.put(EventLevel.INFO, EventUtils.COLOR_INFO);
    COLORS.put(EventLevel.WARNING, EventUtils.COLOR_WARNING);
    COLORS.put(EventLevel.ERROR, EventUtils.COLOR_ERROR);
    COLORS.put(EventLevel.FATAL, EventUtils.COLOR_FATAL);
  }
  
  public static final String FIELD_SEVERITY_STATS_COLOR = "color";
  public static final String FIELD_SEVERITY_STATS_NUMBER = "number";
  public static final String FIELD_SEVERITY_STATS_LEVEL = "level";
  
  private static TableFormat SEVERITY_STATS_FORMAT = new TableFormat();
  
  public static final String ENVIRONMENT_ID = "id";
  public static final String ENVIRONMENT_CONTEXT = "context";
  public static final String ENVIRONMENT_EVENT = "event";
  public static final String ENVIRONMENT_LEVEL = "level";
  public static final String ENVIRONMENT_TIME = "time";
  public static final String ENVIRONMENT_ACKNOWLEDGEMENTS = "acknowledgements";
  public static final String ENVIRONMENT_ENRICHMENTS = "enrichments";
  public static final String ENVIRONMENT_VALUE = "value";
  
  static
  {
    FieldFormat ff = FieldFormat.create("<" + FIELD_SEVERITY_STATS_LEVEL + "><I><D=" + Cres.get().getString("level") + ">");
    ff.setSelectionValues(EventLevel.getSelectionValues());
    SEVERITY_STATS_FORMAT.addField(ff);
    
    SEVERITY_STATS_FORMAT.addField(FieldFormat.create("<" + FIELD_SEVERITY_STATS_NUMBER + "><I><D=" + Cres.get().getString("efEventCount") + ">"));
    SEVERITY_STATS_FORMAT.addField(FieldFormat.create("<" + FIELD_SEVERITY_STATS_COLOR + "><C><F=HI>")); // Inline flag, will be used as row background
  }
  
  public static final String EVENT_FIELD_CONTEXT = "context";
  public static final String EVENT_FIELD_NAME = "name";
  public static final String EVENT_FIELD_CREATIONTIME = "creationtime";
  
  public static long generateEventId()
  {
    return Math.abs(ThreadLocalRandom.current().nextLong());
  }
  
  public static List<EventDefinition> getEventDefinitions(ContextManager cm, String contextMask, String eventsMask, CallerController caller)
  {
    List<EventDefinition> events = new LinkedList();
    List<Context> contexts = ContextUtils.expandMaskToContexts(contextMask, cm, caller);
    
    for (Context context : contexts)
    {
      events.addAll(getEvents(context, eventsMask, caller));
    }
    
    return events;
  }
  
  public static List<EventDefinition> getEvents(Context<Context> context, String eventsMask, CallerController caller)
  {
    List<EventDefinition> events = new LinkedList();
    
    if (eventsMask.equals(ContextUtils.ENTITY_GROUP_MASK))
    {
      for (EventDefinition ed : context.getEventDefinitions(caller))
      {
        if (ed.getGroup() != null && !ContextUtils.GROUP_SYSTEM.equals(ed.getGroup()) && !ed.isDebuggingEvaluations())
        {
          events.add(ed);
        }
      }
    }
    else
    {
      EventDefinition ed = context.getEventDefinition(eventsMask);
      if (ed != null)
      {
        events.add(ed);
      }
    }
    
    return events;
  }
  
  public static boolean matchesToMask(String eventMask, EventDefinition ed)
  {
    if (ContextUtils.ENTITY_GROUP_MASK.equals(eventMask))
    {
      return ed.getGroup() != null && !ContextUtils.GROUP_SYSTEM.equals(ed.getGroup());
    }
    
    return ed.getName().equals(eventMask);
  }
  
  // Result of this function may differ from the result of mathesToMask(String, EventDefinition)
  // because if doesn't check for the event's group name
  public static boolean matchesToMask(String eventMask, String event)
  {
    if (ContextUtils.ENTITY_GROUP_MASK.equals(eventMask))
    {
      return true;
    }
    
    return Util.equals(event, eventMask);
  }
  
  public static DataTable createSeverityStatisticsTable(int none, int notice, int info, int warning, int error, int fatal)
  {
    DataTable stats = new SimpleDataTable(SEVERITY_STATS_FORMAT);
    stats.addRecord().addInt(EventLevel.NONE).addInt(none).addColor(COLOR_NONE);
    stats.addRecord().addInt(EventLevel.NOTICE).addInt(notice).addColor(COLOR_NOTICE);
    stats.addRecord().addInt(EventLevel.INFO).addInt(info).addColor(COLOR_INFO);
    stats.addRecord().addInt(EventLevel.WARNING).addInt(warning).addColor(COLOR_WARNING);
    stats.addRecord().addInt(EventLevel.ERROR).addInt(error).addColor(COLOR_ERROR);
    stats.addRecord().addInt(EventLevel.FATAL).addInt(fatal).addColor(COLOR_FATAL);
    return stats;
  }
  
  public static Color getEventColor(int level)
  {
    return COLORS.get(level);
  }
  
  public static Map<String, Object> getEnvironment(Event ev)
  {
    if (ev == null)
    {
      return Collections.emptyMap();
    }
    
    Map<String, Object> environment = new HashMap<String, Object>();
    environment.put(ENVIRONMENT_ID, ev.getId());
    environment.put(ENVIRONMENT_CONTEXT, ev.getContext());
    environment.put(ENVIRONMENT_EVENT, ev.getName());
    environment.put(ENVIRONMENT_LEVEL, ev.getLevel());
    environment.put(ENVIRONMENT_TIME, ev.getCreationtime());
    environment.put(ENVIRONMENT_ACKNOWLEDGEMENTS, ev.getAcknowledgementsTable());
    environment.put(ENVIRONMENT_ENRICHMENTS, ev.getEnrichmentsTable());
    environment.put(ENVIRONMENT_VALUE, ev.getData());
    return environment;
  }
}
