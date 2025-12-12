package com.tibbo.aggregate.common.server;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import com.tibbo.aggregate.common.action.ActionDefinition;
import com.tibbo.aggregate.common.action.ActionExecutionMode;
import com.tibbo.aggregate.common.action.ActionIdentifier;
import com.tibbo.aggregate.common.action.ServerActionInput;
import com.tibbo.aggregate.common.context.CallerController;
import com.tibbo.aggregate.common.context.Context;
import com.tibbo.aggregate.common.context.ContextException;
import com.tibbo.aggregate.common.context.EntityDefinition;
import com.tibbo.aggregate.common.context.VariableDefinition;
import com.tibbo.aggregate.common.data.Event;
import com.tibbo.aggregate.common.datatable.DataTable;
import com.tibbo.aggregate.common.event.Acknowledgement;
import com.tibbo.aggregate.common.event.Enrichment;
import com.tibbo.aggregate.common.security.Permissions;

public interface ServerContext<C extends ServerContext> extends Context<C>
{
  /**
   * Add new visible child to the context.
   * 
   * @param path
   *          Full name of the context to add as visible child of the current one.
   */
  void addVisibleChild(String path);
  
  /**
   * Remove visible child from the context.
   * 
   * @param path
   *          Full name of the visible child to remove.
   */
  void removeVisibleChild(String path);
  
  /**
   * Returns true if the context has a visible child with specified path.
   * 
   * @param path
   *          Path to check.
   */
  boolean hasVisibleChild(String path);
  
  /**
   * Returns default value of the variable. If it's not directly specified in the variable definition, an empty data table in the variable's format is created and returned.
   * 
   * @param vd
   *          Definition of the variable those default value to return
   * @return Default value of the variable
   */
  DataTable getDefaultValue(VariableDefinition vd);
  
  /**
   * Permanently deletes variable value from the database. This method should be used only before variable definition is going to be removed from the context.
   * 
   * @param name
   *          Name of variable those value should be removed.
   */
  void removeVariableValue(String name);
  
  // The following methods should not be called via public API
  
  ActionIdentifier initAction(ActionDefinition def, ServerActionInput actionInput, ActionExecutionMode mode, CallerController caller, String customActionId, boolean asDefault) throws ContextException;
  
  boolean checkPermissions(Permissions needPermissions, CallerController caller, Context accessedContext, EntityDefinition accessedEntityDefinition);
  
  Collection<String> getMembers(boolean includeSubgroups, CallerController cc);
  
  Collection<String> getMembers(boolean includeSubgroups, boolean detectNestedLoop, CallerController cc);
  
  void addedToGroup(GroupContext groupContext, CallerController caller);
  
  void removedFromGroup(GroupContext groupContext, CallerController caller);
  
  Set<GroupContext> getGroups();
  
  void alertActivated(Event alert, Integer type);
  
  void alertDeactivated(Event alert);
  
  boolean shouldBeHidden(CallerController callerController);
  
  void setIndex(Integer index);
  
  void createDefaultStatisticsChannels(VariableDefinition vd);
  
  String getShortDescription();
  
  void eventAcknowledged(Event ev, Acknowledgement acknowledgement) throws ContextException;
  
  void eventEnriched(Event ev, Enrichment en) throws ContextException;

  default Collection<Context> getDependentContexts(CallerController callerController) throws ContextException
  {
    return Collections.emptyList();
  }
  
  default Map<Context, Collection<String>> getDependentVariables(CallerController callerController) throws ContextException
  {
    return Collections.emptyMap();
  }
}