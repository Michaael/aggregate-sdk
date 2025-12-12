package com.tibbo.aggregate.common.context;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import com.tibbo.aggregate.common.action.ActionDefinition;
import com.tibbo.aggregate.common.data.Event;
import com.tibbo.aggregate.common.datatable.DataTable;
import com.tibbo.aggregate.common.event.ContextEventListener;
import com.tibbo.aggregate.common.event.FireEventRequestController;
import com.tibbo.aggregate.common.expression.EvaluationPoint;
import com.tibbo.aggregate.common.security.Permissions;
import com.tibbo.aggregate.common.util.Pair;

/**
 * Context interface is used to provide a unified way to access any object in AggreGate. It may be some server object (e.g. alert or event filters storage), hardware device or widget component. When
 * server contexts are accessed remotely, so-called proxy contexts are created for operating server-side objects through the same interface.
 */
public interface Context<C extends Context> extends Comparable<C>, EvaluationPoint
{
  /**
   * This method is called after the context has been added to a context tree and it became aware of its full path. Note, that default implementation of this method in AbstractContext calls tree
   * methods: setupPermissions(), setupMyself() and setupChildren(). These methods should provide initialization logic in inherited classes instead of overridden setup() method.
   *
   * @param contextManager
   *          ContextManager heading current context tree
   */
  void setup(ContextManager contextManager);
  
  /**
   * This method is called when the context is being removed from context tree..
   */
  void teardown();
  
  /**
   * This method should return true if the context has already been initialized and setupMyself() finished execution. Its default implementation in AbstractContext should not be overridden.
   *
   * @return true if setupMyself() has already been completed.
   */
  boolean isSetupComplete();
  
  /**
   * This method should return true if the context status has already been initialized.
   *
   * @return true if basic context status has been initialized.
   */
  
  boolean isInitializedStatus();
  
  /**
   * This method should return true if the context has already been initialized its basic information (description, type, etc).
   *
   * @return true if basic context information has been initialized.
   */
  boolean isInitializedInfo();
  
  /**
   * This method should return true if the context has already been initialized its children.
   *
   * @return true if context children have been initialized.
   */
  boolean isInitializedChildren();
  
  /**
   * This method should return true if the context has already been initialized its variables.
   *
   * @return true if context variables have been initialized.
   */
  boolean isInitializedVariables();
  
  /**
   * This method should return true if the context has already been initialized its functions.
   *
   * @return true if context functions have been initialized.
   */
  boolean isInitializedFunctions();
  
  /**
   * This method should return true if the context has already been initialized its events.
   *
   * @return true if context events have been initialized.
   */
  boolean isInitializedEvents();
  
  /**
   * This method is called when context tree is being started after its initialization. All contexts in the tree should be available at the moment of call.
   */
  void start();
  
  /**
   * This method is called when context tree is being stopped before its de-initialization. All contexts in the tree should be available at the moment of call.
   */
  void stop();
  
  /**
   * Returns true if context was started but not yet stopped.
   */
  boolean isStarted();
  
  /**
   * Returns context name.
   */
  String getName();
  
  /**
   * Returns context path (full name).
   */
  String getPath();
  
  /**
   * Returns path of the root context. In distributed environment, returns path of the root context in remote (mounted) subtree.
   */
  String getLocalRoot(boolean withParent);
  
  /**
   * In distributed environment, returns path of the context on the server immediately connected to current server (current server's peer).
   */
  String getPeerPath();
  
  /**
   * When a certain context subtree from one server is connected to another server, this method will return the remote path of this subtree's root context. If current context doesn't have a remote
   * peer, this method returns null.
   */
  String getPeerRoot();
  
  /**
   * In distributed environment, returns path of the context on the server where it's actually defined.
   */
  String getRemotePath();
  
  /**
   * Returns path of remote server's root context within a distributed connection.
   */
  String getRemoteRoot();
  
  /**
   * In distributed environment, returns path of the primary mount context in local tree.
   */
  String getLocalPrimaryRoot();
  
  /**
   * Returns true if context is a remote context's proxy.
   */
  boolean isProxy();
  
  /**
   * Returns true if context has a remote peer in the distributed architecture.
   */
  boolean isDistributed();
  
  /**
   * Returns true if context is container.
   */
  boolean isContainer();
  /**
   * Returns context detailed description that includes description and path.
   */
  String toDetailedString();
  
  /**
   * Returns context description.
   */
  String getDescription();
  
  /**
   * Returns context type.
   */
  String getType();
  
  /**
   * Returns context group name of NULL if context does not belong to a group.
   */
  String getGroup();
  
  /**
   * Returns context comparison index or NULL if index is not defined.
   */
  Integer getIndex();
  
  /**
   * Returns context icon ID.
   */
  String getIconId();
  
  /**
   * Returns context status or null if status is not enabled;
   */
  ContextStatus getStatus();
  
  /**
   * Returns context manager those context tree contains this context.
   */
  ContextManager getContextManager();
  
  /**
   * Returns list of children contexts that are accessible by the specified <code>CallerController</code>.
   */
  List<C> getChildren(CallerController caller);
  
  /**
   * Returns list of children contexts.
   */
  List<C> getChildren();
  
  /**
   * Returns list of visible children contexts.
   */
  List<C> getVisibleChildren(CallerController caller);
  
  /**
   * Returns list of visible children contexts.
   */
  List<C> getVisibleChildren();
  
  /**
   * Check if the visible child of the context is available.
   */
  boolean hasVisibleChild(String name, CallerController caller) throws ContextException;

  /**
   * Returns true if context's visible children are mapped (e.g. for group and aggregation contexts).
   */
  boolean isMapped();
  
  /**
   * Returns list of mapped children contexts.
   */
  List<C> getMappedChildren(CallerController caller);
  
  /**
   * Returns list of mapped children contexts.
   */
  List<C> getMappedChildren();
  
  /**
   * Returns root context of the context tree containing this context.
   */
  C getRoot();
  
  /**
   * Returns context with the selected path.
   *
   * <code>path</code> argument may be absolute of relative to this context. This method uses provided <code>CallerController</code> for permission checking.
   */
  C get(String path, CallerController caller);
  
  /**
   * Returns context with the selected path.
   *
   * <code>path</code> argument may be absolute of relative to this context.
   *
   * Note: if this Context is a part of distributed context tree and path argument is not relative, the method will return local context matching its remote "peer" with given path. To get the local
   * context with the given path, use {@link ContextManager#get(String)} instead.
   */
  C get(String path);
  
  /**
   * Returns child of this context with the specified name.
   *
   * <code>path</code> argument may be absolute of relative to this context.
   *
   * Note: if this Context is a part of distributed context tree and path argument is not relative, the method will return local context matching its remote "peer" with given path. To get the local
   * context with the given path, use {@link ContextManager#get(String, CallerController)} instead.
   *
   * This method uses provided <code>CallerController</code> for permission checking.
   */
  C getChild(String name, CallerController caller);
  
  /**
   * Returns child of this context with the specified name.
   */
  C getChild(String name);
  
  /**
   * Check if the mapped child of this context is available.
   */
  boolean hasMappedChild(String contextName, CallerController callerController);

  /**
   * Adds new child to the current context.
   */
  void addChild(C child);
  
  /**
   * Removes child of current context.
   */
  void removeChild(C child);
  
  /**
   * Removes child with specified name.
   */
  void removeChild(String name);
  
  /**
   * Permanently destroys child of current context.
   */
  void destroyChild(C child, boolean moving);
  
  /**
   * Permanently destroys this context.
   */
  void destroy(boolean moving);
  
  /**
   * Prepare context to update.
   */
  void updatePrepare();
  
  /**
   * Moves and/or renames the context.
   */
  void move(C newParent, String newName) throws ContextException;

  /**
   * Returns parent of this context.
   */
  C getParent();
  
  void setParent(C parent);

  /**
   * Returns true if parentContext is a parent of this context or some of its parents.
   */
  boolean hasParent(C parentContext);

  /**
   * Adds variable definition to this context.
   */
  void addVariableDefinition(VariableDefinition def);
  
  /**
   * Removes variable definition from this context.
   */
  void removeVariableDefinition(String name);
  
  /**
   * Returns data of variable with specified name.
   */
  VariableData getVariableData(String name);

  void addAlias(int entityType, String aliasName, String name);

  /**
   * Returns definition of variable with specified name.
   */
  VariableDefinition getVariableDefinition(String name);
  
  /**
   * Returns definition of variable with specified name if it's accessible by caller controller.
   */
  VariableDefinition getVariableDefinition(String name, CallerController caller);
  
  /**
   * Returns list of variables available for specified <code>CallerController</code>.
   */
  List<VariableDefinition> getVariableDefinitions(CallerController caller);
  
  /**
   * Returns list of variables.
   */
  List<VariableDefinition> getVariableDefinitions();
  
  /**
   * Returns list of variables belonging to <code>group</code> that are available for specified <code>CallerController</code>.
   */
  List<VariableDefinition> getVariableDefinitions(CallerController caller, String group);
  
  /**
   * Returns list of variables belonging to <code>group</code>.
   */
  List<VariableDefinition> getVariableDefinitions(String group);
  
  /**
   * Returns list of variables.
   */
  List<VariableDefinition> getVariableDefinitions(CallerController caller, boolean includeHidden);

  /**
   * Adds function definition to this context.
   */
  void addFunctionDefinition(FunctionDefinition def);
  
  /**
   * Removes function definition from this context.
   */
  void removeFunctionDefinition(String name);
  
  /**
   * Returns data of function with specified name.
   */
  FunctionData getFunctionData(String name);
  
  /**
   * Returns definition of function with specified name.
   */
  FunctionDefinition getFunctionDefinition(String name);
  
  /**
   * Returns definition of function with specified name if it's accessible by caller controller.
   */
  FunctionDefinition getFunctionDefinition(String name, CallerController caller);
  
  /**
   * Returns list of functions available for specified <code>CallerController</code>.
   */
  List<FunctionDefinition> getFunctionDefinitions(CallerController caller);
  
  /**
   * Returns list of functions.
   */
  List<FunctionDefinition> getFunctionDefinitions();
  
  /**
   * Returns list of functions belonging to <code>group</code> that are available for specified <code>CallerController</code>.
   */
  List<FunctionDefinition> getFunctionDefinitions(CallerController caller, String group);
  
  /**
   * Returns list of functions belonging to <code>group</code>.
   */
  List<FunctionDefinition> getFunctionDefinitions(String group);
  
  /**
   * Returns list of functions.
   */
  List<FunctionDefinition> getFunctionDefinitions(CallerController caller, boolean includeHidden);
  
  /**
   * Adds event definition to this context.
   */
  void addEventDefinition(EventDefinition def);
  
  /**
   * Removes event definition from this context.
   */
  void removeEventDefinition(String name);
  
  /**
   * Returns definition of event with specified name.
   */
  EventDefinition getEventDefinition(String name);
  
  /**
   * Returns definition of event with specified name if it's accessible by caller controller.
   */
  EventDefinition getEventDefinition(String name, CallerController caller);
  
  /**
   * Returns <code>EventData</code> of event with specified name.
   */
  EventData getEventData(String name);
  
  /**
   * Returns list of events available for specified <code>CallerController</code>.
   */
  List<EventDefinition> getEventDefinitions(CallerController caller);
  
  /**
   * Returns list of events.
   */
  List<EventDefinition> getEventDefinitions();
  
  /**
   * Returns list of events belonging to <code>group</code> that are available for specified <code>CallerController</code>.
   */
  List<EventDefinition> getEventDefinitions(CallerController caller, String group);
  
  /**
   * Returns list of events belonging to <code>group</code>.
   */
  List<EventDefinition> getEventDefinitions(String group);
  
  /**
   * Returns list of events.
   */
  List<EventDefinition> getEventDefinitions(CallerController caller, boolean includeHidden);
  
  /**
   * Gets variable from context and returns its value. The value is immutable and will generate {@link IllegalStateException} on every change.
   */
  DataTable getVariable(String name, CallerController caller, RequestController request) throws ContextException;
  
  /**
   * Gets variable from context and returns its value. The value is immutable and will generate {@link IllegalStateException} on every change.
   */
  DataTable getVariable(String name, CallerController caller) throws ContextException;
  
  /**
   * Gets variable from context and returns its value. The value is immutable and will generate {@link IllegalStateException} on every change.
   */
  DataTable getVariable(String name) throws ContextException;
  
  /**
   * Gets variable from context and returns its value. The value is mutable. Method has lower performance comparing to 'getVariable'.
   */
  DataTable getVariableClone(String name, CallerController caller) throws ContextException;
  
  /**
   * Returns value of variable as bean or list of beans.
   */
  Object getVariableObject(String name, CallerController caller);
  
  /**
   * Sets context variable to specified <code>value</code>.
   */
  void setVariable(String name, CallerController caller, DataTable value) throws ContextException;
  
  /**
   * Sets context variable to specified <code>value</code>.
   */
  void setVariable(String name, CallerController caller, RequestController request, DataTable value) throws ContextException;
  
  /**
   * Sets context variable to specified <code>value</code>.
   */
  void setVariable(String name, CallerController caller, Object... value) throws ContextException;
  
  /**
   * Sets context variable to specified <code>value</code>.
   */
  void setVariable(String name, DataTable value) throws ContextException;
  
  /**
   * Sets context variable to specified <code>value</code>.
   */
  void setVariable(String name, Object... value) throws ContextException;
  
  /**
   * Gets variable, updates field value in the first record, and sets variable.
   */
  boolean setVariableField(String variable, String field, Object value, CallerController cc) throws ContextException;
  
  /**
   * Gets variable, updates field value in the specified record, and sets variable.
   */
  default boolean setVariableField(String variable, String field, int record, Object value, CallerController cc) throws ContextException
  {
    return setVariableField(variable, field, record, value, cc, null);
  }

  boolean setVariableField(String variable, String field, int record, Object value, CallerController cc,
      @Nullable RequestController request) throws ContextException;

  /**
   * Gets variable, updates field value in the records for those value of compareField equals compareValue, and sets variable.
   */
  void setVariableField(String variable, String field, Object value, String compareField, Object compareValue, CallerController cc) throws ContextException;
  
  /**
   * Executes context function with specified <code>parameters</code> and returns its output.
   */
  DataTable callFunction(String name, CallerController caller, DataTable parameters) throws ContextException;
  
  /**
   * Executes context function with specified <code>parameters</code> and returns its output.
   */
  DataTable callFunction(String name, DataTable parameters) throws ContextException;
  
  /**
   * Executes context function with specified <code>parameters</code> and returns its output.
   */
  DataTable callFunction(String name) throws ContextException;
  
  /**
   * Executes context function with specified <code>parameters</code> and returns its output.
   */
  DataTable callFunction(String name, CallerController caller, RequestController request, DataTable parameters) throws ContextException;
  
  /**
   * Executes context function with specified <code>parameters</code> and returns its output.
   */
  DataTable callFunction(String name, CallerController caller, Object... parameters) throws ContextException;
  
  /**
   * Executes context function with specified <code>parameters</code> and returns its output.
   */
  DataTable callFunction(String name, Object... parameters) throws ContextException;
  
  /**
   * Executes context function with specified <code>parameters</code> and returns its output.
   */
  DataTable callFunction(String name, CallerController caller) throws ContextException;
  
  /**
   * Fires context event.
   *
   * @return Event object or null if event was suppressed by context.
   */
  Event fireEvent(String name);
  
  /**
   * Fires context event.
   *
   * @return Event object or null if event was suppressed by context.
   */
  Event fireEvent(String name, CallerController caller);
  
  /**
   * Fires context event.
   *
   * @return Event object or null if event was suppressed by context.
   */
  Event fireEvent(String name, DataTable data);
  
  /**
   * Fires context event.
   *
   * @return Event object or null if event was suppressed by context.
   */
  Event fireEvent(String name, CallerController caller, DataTable data);
  
  /**
   * Fires context event.
   *
   * @return Event object or null if event was suppressed by context.
   */
  Event fireEvent(String name, int level, DataTable data);
  
  /**
   * Fires context event.
   *
   * @return Event object or null if event was suppressed by context.
   */
  Event fireEvent(String name, int level, CallerController caller, DataTable data);

  /**
   * Fires context event.
   *
   * @return Event object or null if event was suppressed by context.
   */
  Event fireEvent(String name, int level, CallerController caller, FireEventRequestController request, DataTable data);

  /**
   * Fires context event.
   *
   * @return Event object or null if event was suppressed by context.
   */
  Event fireEvent(String name, Object... data);
  
  Event fireEvent(String name, FireEventRequestController request, Object... data);

  /**
   * Fires context event.
   *
   * @return Event object or null if event was suppressed by context.
   */
  Event fireEvent(String name, DataTable data, int level, Long id, Date creationtime, Integer listener, CallerController caller, FireEventRequestController request);
  
  /**
   * Add a new action definition to the context.
   *
   * @param def
   *          ActionDefinition to add
   */
  void addActionDefinition(ActionDefinition def);
  
  /**
   * Remove an action definition from the context.
   *
   * @param name
   *          Name of action to remove
   */
  void removeActionDefinition(String name);
  
  /**
   * Returns action definition by name.
   *
   * @param name
   *          Name of action
   */
  ActionDefinition getActionDefinition(String name);
  
  /**
   * Returns action definition by name.
   *
   * @param name
   *          Name of action
   * @param caller
   *          Caller controller
   */
  ActionDefinition getActionDefinition(String name, CallerController caller);
  
  /**
   * Returns default action definition or NULL if there is no default action or it's not available to the caller.
   *
   * @param caller
   *          Caller controller
   */
  ActionDefinition getDefaultActionDefinition(CallerController caller);
  
  /**
   * Returns action definitions.
   */
  List<ActionDefinition> getActionDefinitions();
  
  /**
   * Returns action definitions that are accessible for the caller.
   *
   * @param caller
   *          Caller controller
   */
  List<ActionDefinition> getActionDefinitions(CallerController caller);
  
  /**
   * Returns action definitions.
   */
  List<ActionDefinition> getActionDefinitions(CallerController caller, boolean includeHidden);
  
  /**
   * Returns context permissions.
   */
  Permissions getPermissions();
  
  /**
   * Returns permissions required to access children of this context.
   */
  Permissions getChildrenViewPermissions();
  
  /**
   * Adds listener of event with specified name.
   */
  boolean addEventListener(String name, ContextEventListener listener);
  
  /**
   * Adds listener of event with specified name. This method allows to add auto-cleaned listeners by setting weak flag to true.
   */
  boolean addEventListener(String name, ContextEventListener listener, boolean weak);
  
  /**
   * Removes listener of event with specified name.
   */
  boolean removeEventListener(String name, ContextEventListener listener);
  
  /**
   * Returns in-memory event history.
   */
  List<Event> getEventHistory(String name);
  
  /**
   * Accepts context visitor, i.e. calls visitor.visit(this).
   */
  void accept(ContextVisitor visitor) throws ContextException;
  
  /**
   * Check if an installation is permitted.
   */
  boolean isInstallationAllowed(String installableItemName);

  List<VariableDefinition> updateVariableDefinitions(Map<String, VariableDefinition> variableDefinitionMap, String baseGroup, boolean skipRemoval, boolean onDestroy, Object owner);

  List<FunctionDefinition> updateFunctionDefinitions(Map<String, Pair<FunctionDefinition, Boolean>> functionDefinitionMap, String baseGroup, boolean skipRemoval, Object owner);

  List<EventDefinition> updateEventDefinitions(Map<String, EventDefinition> eventDefinitionMap, String baseGroup, boolean skipRemoval, Object owner);
}
