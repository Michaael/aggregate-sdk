package com.tibbo.aggregate.common.action;

import java.util.*;
import java.util.concurrent.locks.*;

import com.tibbo.aggregate.common.context.*;
import com.tibbo.aggregate.common.security.*;

public interface ActionDefinition extends Comparable, EntityDefinition
{
  /**
   * Returns true if action execution is allowed.
   */
  boolean isEnabled();
  
  /**
   * Enables/disables action.
   */
  void setEnabled(boolean enabled);
  
  /**
   * Returns true if action is a default action in context.
   */
  boolean isDefault();
  
  /**
   * Sets default flag for the action.
   */
  void setDefault(boolean b);
  
  /**
   * Returns true if action supports non-interactive execution.
   */
  boolean isHeadless();
  
  /**
   * Returns true if action is hidden.
   */
  boolean isHidden();
  
  /**
   * Returns key stroke used to initiate the action in any UI.
   */
  KeyStroke getAccelerator();
  
  /**
   * Returns the drop source context masks.
   */
  List<ResourceMask> getDropSources();
  
  /**
   * Returns permissions required to execute action.
   */
  Permissions getPermissions();
  
  /**
   * Some category of actions may be applied to groups of objects. To make system know what actions to group the definition has an execution group. If action can't be grouped, execution group should
   * be null.
   */
  GroupIdentifier getExecutionGroup();
  
  /**
   * Creates and return an instance of the action
   */
  Action instantiate();
  
  /**
   * If false, parallel execution of several action instances is not allowed
   */
  boolean isConcurrent();
  
  /**
   * Returns the execution lock
   */
  ReentrantLock getExecutionLock();
}
