package com.tibbo.aggregate.common.context;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;

import javax.annotation.Nullable;

import com.tibbo.aggregate.common.data.Event;
import com.tibbo.aggregate.common.datatable.encoding.FormatCache;
import com.tibbo.aggregate.common.event.ContextEventListener;
import com.tibbo.aggregate.common.event.FireEventRequestController;
import com.tibbo.aggregate.common.plugin.PluginDirector;

public interface ContextManager<T extends Context>
{
  /**
   * Returns true if context manager startup was completed
   */
  boolean isStarted();
  
  /**
   * Starts event dispatcher thread
   */
  void start();
  
  /**
   * Calls stop() and then start()
   */
  void restart();
  
  /**
   * 1. Stops the event dispatcher<br>
   * 2. Calls stop() of root context
   */
  void stop();
  
  /**
   * Get root context.
   * 
   * @return Root context of the managed context tree
   */
  T getRoot();
  
  /**
   * Get specified context using specified CallerController for permission checking.
   * 
   * Note: getting contexts via context manager should not be used in distributed environment. Use {@link Context#get(String, CallerController)} method of a "reference" context instead to ensure
   * proper paths conversion.
   * 
   * @param caller
   *          CallerController used for permission checking
   * @param contextName
   *          Context full name
   * @return Requested context or null if this context not exist or not available with current permissions
   */
  T get(String contextName, CallerController caller);
  
  /**
   * Get specifies context without CallerController. Internally calls get(null, contextName);
   * 
   * Note: getting contexts via context manager should not be used in distributed environment. Use {@link Context#get(String)} method of a "reference" context instead to ensure proper paths
   * conversion.
   * 
   * @param contextName
   *          Context full name
   * @return Requested context or null if this context not exist or not available with current permissions
   */
  T get(String contextName);
  
  /**
   * Adds event listener to specified event to every context satisfying context mask.
   */
  void addMaskEventListener(String mask, String event, ContextEventListener listener);
  
  /**
   * Adds event listener to specified event to every context satisfying context mask.
   */
  void addMaskEventListener(String mask, String event, ContextEventListener listener, boolean weak);
  
  /**
   * Removes event listener of event 'event' from every context satisfying event mask.
   */
  void removeMaskEventListener(String mask, String event, ContextEventListener listener);
  
  /**
   * Called when new context is added to the context manager
   */
  void contextAdded(T con);
  
  /**
   * Called when context is removed from the context manager
   */
  void contextRemoved(T con);
  
  /**
   * Called when context basic info is changed
   */
  void contextInfoChanged(T con);
  
  /**
   * Called when new variable definition is added to a context
   */
  void variableAdded(T con, VariableDefinition vd);
  
  /**
   * Called when variable definition is removed from a context
   */
  void variableRemoved(T con, VariableDefinition vd);
  
  /**
   * Called when new function definition is added to a context
   */
  void functionAdded(T con, FunctionDefinition fd);
  
  /**
   * Called when function definition is removed from a context
   */
  void functionRemoved(T con, FunctionDefinition fd);
  
  /**
   * Called when new event definition is added to a context
   */
  void eventAdded(T con, EventDefinition ed);
  
  /**
   * Called when event definition is removed from a context
   */
  void eventRemoved(T con, EventDefinition ed);
  
  /**
   * Called when event if fired in one of the contexts in the tree
   */
  void queue(EventData ed, Event ev, FireEventRequestController request);

  /**
   * Returns context manager's task execution service
   */
  ExecutorService getExecutorService();
  
  /**
   * Returns the plugin director or null if a context manager is not the server context manager.
   */
  <P extends PluginDirector> P getPluginDirector();
  
  /**
   * Returns caller controller used by context manager for internal operations. This controller is unsafe since it doesn't perform any permission checking.
   */
  CallerController getCallerController();
  
  /**
   * Returns current length of event queue.
   */
  int getEventQueueLength();

  /**
   * Returns number of events received since server start.
   */
  long getEventsScheduled();

  /**
   * Returns number of events processed since server start.
   */
  long getEventsProcessed();

  /**
   * Returns pending event count per context
   */
  Map<String,Long> getEventQueueStatistics();

  /**
   * @return format cache associated with the context. This may be the server-wide cache, a local one or even nothing
   * (denoted by an empty optional) in case of cache absence.
   * @apiNote <strong>Caution!</strong> Since this method relates to context manager which in turn defaults to
   * the server-wide format cache, it is not recommended to use this method directly because it may return a cache of
   * wrong scope. Instead, consider using {@link AbstractContext#obtainFormatCache()} which delegates to certain
   * contexts and thus provides a more reliable way to pick the correct cache.
   */
  default Optional<FormatCache> getFormatCache()
  {
    return Optional.empty();
  }

  /**
   * The scope of the context manager is for observability purposes only. It helps to reliably distinguish likely-named
   * contexts belonging to different scopes of a low-code application, e.g. the root context of the system tree (named
   * "") and the context of the root panel of a web dashboard (also named "").
   *
   * @return this manager's scope to ease tracing of its contexts or null if the scope is not defined (yet)
   * @implNote The format and the content of the scope are implementation specific. For example, a dashboard context
   *     manager may return the dashboard's context path. The default implementation returns an empty string.
   */
  @Nullable
  default String getScope() {
    return "";
  }
}
