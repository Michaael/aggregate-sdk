package com.tibbo.aggregate.common.dao;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.tibbo.aggregate.common.context.CallerController;
import com.tibbo.aggregate.common.context.Context;
import com.tibbo.aggregate.common.context.ContextManager;
import com.tibbo.aggregate.common.context.EntityList;
import com.tibbo.aggregate.common.context.EventDefinition;
import com.tibbo.aggregate.common.data.Event;
import com.tibbo.aggregate.common.datatable.DataTable;
import com.tibbo.aggregate.common.event.EventWriter;
import com.tibbo.aggregate.common.event.EventWriterConfiguration;
import com.tibbo.aggregate.common.event.EventsFilter;

public interface EventDao extends Dao
{
  /**
   * Register a new event storage and create it if necessary.
   *
   * @param storage
   *          A storage to register
   * @param update
   *          Requests storage schema update
   * @throws DaoException
   *           If registration or creation has failed
   */
  void createStorage(EventStorage storage, boolean update) throws DaoException;

  /**
   * Remove an event storage.
   *
   * @param storage
   *          A storage to remove
   * @throws DaoException
   *           If removal has failed
   */
  void dropStorage(EventStorage storage) throws DaoException;

  /**
   * Persistently save a single event.
   * 
   * @param ed
   *          A definition of the event
   * @param event
   *          An event to be saved persistently
   * @throws DaoException
   *           If saving has failed
   */
  void postEvent(EventDefinition ed, Event event) throws DaoException;
  
  /**
   * Persistently save several events.
   * 
   * @param storage
   *          A storage to save events to
   * @param events
   *          List of events to be saved persistently
   * @throws DaoException
   *           If saving has failed
   */
  void postEvents(EventStorage storage, EventDefinition ed, List<Event> events) throws DaoException;
  
  /**
   * Load selected events from the storage.
   * 
   * @param cm
   *          Context manager of the context tree the events are defined in
   * @param caller
   *          Caller controller used for event filtering
   * @param eventList
   *          List of events to load
   * @param filter
   *          A filter to be applied to events after loading
   * @param startDate
   *          Only events that occurred after this date will be loaded
   * @param endDate
   *          Only events that occurred before this date will be loaded
   * @param maxResults
   *          Maximum number of events to load
   * @param sortBy
   *          Name of field that will be used to compare events while sorting
   * @param sortAscending
   *          True if events should be sorted in ascending order and false otherwise
   * @param additionalCriteria
   *          Additional storage-level filtering criteria
   * @return An iterator of loaded events
   * @throws DaoException
   *           If loading has failed
   */
  Iterator<Event> getEvents(ContextManager cm,
                            CallerController caller,
                            EntityList eventList,
                            EventsFilter filter,
                            Date startDate,
                            Date endDate,
                            Integer maxResults,
                            String sortBy,
                            boolean sortAscending,
                            Object... additionalCriteria)
          throws DaoException;
  
  /**
   * Causes event DAO to delete all expired events from all storages.
   * 
   * @return Number of events deleted
   * @throws DaoException
   *           If cleaning has failed
   */
  int removeExpiredEvents() throws DaoException;
  
  /**
   * Load a single event.
   * 
   * @param context
   *          Context the event has occurred in
   * @param event
   *          Definition of the event
   * @param id
   *          Unique ID of the event
   * @return An instance of loaded event or null if event does not exist
   * @throws DaoException
   *           If loading has failed
   */
  Event getEvent(Context context, EventDefinition event, long id) throws DaoException;
  
  /**
   * Delete a single persistent event.
   * 
   * @param context
   *          Context the event has occurred in
   * @param name
   *          Name of the event
   * @param id
   *          Unique ID of the event
   * @throws DaoException
   *           If removal has failed
   */
  void deleteEvent(String context, String name, long id) throws DaoException;
  
  /**
   * Delete all persistent events occurred in a certain context.
   *
   * @param context Context the events has occurred in
   * @return Number of events deleted
   * @throws DaoException If removal has failed
   */
  long deleteEvents(String context) throws DaoException;
  
  /**
   * Delete all persistent events occurred in a certain context.
   *
   * @param context   Context the events has occurred in
   * @param name      Name of the event
   * @param startDate Only events that occurred after this date will be loaded
   * @param endDate   Only events that occurred before this date will be loaded
   * @return Number of events deleted
   * @throws DaoException If removal has failed
   */
  long deleteEvents(String context, String name, Date startDate, Date endDate) throws DaoException;
  
  /**
   * Update a previously saved persistent event.
   * 
   * @param event
   *          An updated instance of the event
   * @param def
   *          Definition of the event
   * @throws DaoException
   *           If updating has failed
   */
  void updateEvent(Event event, EventDefinition def) throws DaoException;
  
  /**
   * Reassociate persistent events with a new context path.
   *
   * @param oldContext Context path of the events to reassociate
   * @param newContext Context path the events will be associated with
   * @return Number of events moved
   * @throws DaoException If reassociation has failed
   */
  long moveEvents(String oldContext, String newContext) throws DaoException;
  
  /**
   * Create a custom event writer.
   * 
   * @param config
   *          Configuration of the writes
   * @return An instance of custom event writer of null if creating a writer with provided configuration is not possible
   * @throws DaoException
   *           If writer creation has failed
   */
  EventWriter createEventWriter(EventWriterConfiguration config) throws DaoException;

  /**
   * Returns usage statistics table.
   */
  DataTable getStatistics();
}