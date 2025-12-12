package com.tibbo.aggregate.common.context;

import java.util.List;
import java.util.Map;

import com.tibbo.aggregate.common.action.ActionHistoryItem;
import com.tibbo.aggregate.common.action.ActionManager;
import com.tibbo.aggregate.common.data.Event;
import com.tibbo.aggregate.common.datatable.DataTable;
import com.tibbo.aggregate.common.datatable.encoding.KnownFormatCollector;
import com.tibbo.aggregate.common.event.EventSortDirective;
import com.tibbo.aggregate.common.event.EventsFilter;
import com.tibbo.aggregate.common.view.StorageSession;

public interface CallerData
{
  void storeEventHistory(int listenerHashCode, List<Event> history);

  List<Event> getEventHistory(int listenerHashCode);

  Map<Integer, List<Event>> getEventHistory();

  List<EventSortDirective> getEventSortDirectives(int listenerHashCode);

  void storeEventSortDirectives(int listenerHashCode, List<EventSortDirective> sortDirectives);

  void cleanup();

  Map<Integer, EventsFilter> getHistoryEventFilters();

  Map<Integer, EventsFilter> getRealtimeEventFilters();

  boolean isDataIdAvailable(Long id);

  void makeDataIdAvailable(Long id);

  Map<String, String> getContextPasswords();

  ActionManager getActionManager();

  KnownFormatCollector getKnownFormatCollector();
  
  void addToActionHistory(ActionHistoryItem item);

  List<ActionHistoryItem> getActionHistory();

  void setActionManager(ActionManager actionManager);

  void registerViewSession(long id, StorageSession session);

  StorageSession getViewSession(long id);
  
  DataTable addToLocalRegistry(Long id, DataTable table);
}
