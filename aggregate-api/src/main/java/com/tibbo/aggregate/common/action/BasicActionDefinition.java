package com.tibbo.aggregate.common.action;

import java.beans.*;
import java.util.*;
import java.util.concurrent.locks.*;

import com.tibbo.aggregate.common.context.*;
import com.tibbo.aggregate.common.security.*;

public class BasicActionDefinition extends AbstractEntityDefinition implements ActionDefinition, ActionCommandList
{
  private static final String GROUP_ID_SEPARATOR = "/";
  private static final String PROPERTY_NAME = "name";
  private static final String PROPERTY_DESCRIPTION = "description";
  private static final String PROPERTY_DROP_SOURCES = "dropSources";
  private static final String PROPERTY_HELP = "help";
  private static final String PROPERTY_ACCELERATOR = "accelerator";
  private static final String PROPERTY_HIDDEN = "hidden";
  private static final String PROPERTY_ENABLED = "enabled";
  private static final String PROPERTY_GROUP_ID = "groupId";
  private static final String PROPERTY_ICON_ID = "iconId";
  private static final String PROPERTY_DEFAULT = "default";
  
  private transient PropertyChangeSupport propertyChangeListeners = new PropertyChangeSupport(this);
  
  private final Class actionClass;
  private boolean enabled = true;
  private boolean isDefault;
  private boolean hidden;
  private GroupIdentifier executionGroup;
  private KeyStroke accelerator;
  private List<ResourceMask> dropSources;
  private List<ActionCommand> commandList;
  public boolean concurrent = true;
  private Permissions permissions;
  
  private final ReentrantLock executionLock = new ReentrantLock();
  
  public BasicActionDefinition(String name)
  {
    this(name, null);
  }
  
  public BasicActionDefinition(String name, Class actionClass)
  {
    if (actionClass != null && !Action.class.isAssignableFrom(actionClass))
    {
      throw new IllegalArgumentException("Action class should implement Action interface");
    }
    
    super.setName(name);
    this.actionClass = actionClass;
  }
  
  @Override
  public GroupIdentifier getExecutionGroup()
  {
    return executionGroup;
  }
  
  @Override
  public boolean isEnabled()
  {
    return enabled;
  }
  
  @Override
  public boolean isHidden()
  {
    return hidden;
  }
  
  @Override
  public KeyStroke getAccelerator()
  {
    return accelerator;
  }
  
  @Override
  public List<ResourceMask> getDropSources()
  {
    if (dropSources != null)
    {
      return Collections.unmodifiableList(dropSources);
    }
    else
    {
      return null;
    }
  }
  
  @Override
  public void setIconId(String iconId)
  {
    String oldIconId = getIconId();
    super.setIconId(iconId);
    propertyChangeListeners.firePropertyChange(PROPERTY_ICON_ID, oldIconId, iconId);
  }
  
  @Override
  public void setHelp(String help)
  {
    String oldHelp = getHelp();
    super.setHelp(help);
    propertyChangeListeners.firePropertyChange(PROPERTY_HELP, oldHelp, help);
  }
  
  @Override
  public void setDescription(String description)
  {
    String oldDescription = getDescription();
    super.setDescription(description);
    propertyChangeListeners.firePropertyChange(PROPERTY_DESCRIPTION, oldDescription, description);
  }
  
  public void setExecutionGroup(String base)
  {
    setExecutionGroup(new GroupIdentifier(getName() + GROUP_ID_SEPARATOR + base));
  }
  
  public void setExecutionGroup(GroupIdentifier groupId)
  {
    GroupIdentifier oldGroupId = this.executionGroup;
    this.executionGroup = groupId;
    propertyChangeListeners.firePropertyChange(PROPERTY_GROUP_ID, oldGroupId, groupId);
  }
  
  @Override
  public void setEnabled(boolean enabled)
  {
    boolean oldEnabled = this.enabled;
    this.enabled = enabled;
    propertyChangeListeners.firePropertyChange(PROPERTY_ENABLED, Boolean.valueOf(oldEnabled), Boolean.valueOf(enabled));
  }
  
  public void setHidden(boolean hidden)
  {
    boolean oldHidden = this.hidden;
    this.hidden = hidden;
    propertyChangeListeners.firePropertyChange(PROPERTY_HIDDEN, Boolean.valueOf(oldHidden), Boolean.valueOf(hidden));
  }
  
  public void setAccelerator(KeyStroke accelerator)
  {
    KeyStroke oldAccelerator = this.accelerator;
    this.accelerator = accelerator;
    propertyChangeListeners.firePropertyChange(PROPERTY_ACCELERATOR, oldAccelerator, accelerator);
  }
  
  public void setDropSources(List<ResourceMask> dropSources)
  {
    if (dropSources == null)
    {
      throw new NullPointerException();
    }
    
    for (ResourceMask resourceMask : dropSources)
    {
      if (resourceMask == null)
      {
        throw new NullPointerException();
      }
    }
    
    List<ResourceMask> oldDropSources = this.dropSources;
    this.dropSources = dropSources;
    propertyChangeListeners.firePropertyChange(PROPERTY_DROP_SOURCES, oldDropSources, dropSources);
  }
  
  @Override
  public void setName(String name)
  {
    String oldName = getName();
    super.setName(name);
    propertyChangeListeners.firePropertyChange(PROPERTY_NAME, oldName, name);
  }
  
  @Override
  public boolean isDefault()
  {
    return isDefault;
  }
  
  @Override
  public void setDefault(boolean isDefault)
  {
    boolean oldIsDefault = this.isDefault;
    this.isDefault = isDefault;
    propertyChangeListeners.firePropertyChange(PROPERTY_DEFAULT, oldIsDefault, isDefault);
  }
  
  @Override
  public boolean isConcurrent()
  {
    return concurrent;
  }
  
  public void setConcurrent(boolean allowConcurrentExecution)
  {
    this.concurrent = allowConcurrentExecution;
  }
  
  public void addDropSource(ResourceMask resourceMask)
  {
    if (resourceMask == null)
    {
      throw new NullPointerException();
    }
    
    dropSources.add(resourceMask);
  }
  
  @Override
  public Action instantiate()
  {
    if (actionClass == null)
    {
      throw new IllegalArgumentException("Redirection to actions of proxy contexts is not supported");
    }
    
    Action action = null;
    
    try
    {
      action = (Action) actionClass.newInstance();
    }
    catch (Exception ex)
    {
      throw new RuntimeException(ex);
    }
    
    return action;
  }
  
  /**
   * List of ActionCommands that can be requested by the source action.
   */
  @Override
  public List<ActionCommand> getCommands()
  {
    if (commandList == null)
    {
      commandList = new LinkedList();
      registerCommands();
    }
    
    return Collections.unmodifiableList(commandList);
  }
  
  /**
   * Override this method to call registerCommand() only inside body of this method. This guarantees necessary synchronization to be performed
   */
  protected void registerCommands()
  {
  }
  
  /**
   * Action may register several parameters (or states) designated by the RequestIdentifier The action passes through these states sequentially with no repeat. If the action repeats it's request a
   * caller may suppose that the action want to reenter invalid data and in automatic mode just stop execution Note this if the action should be able to run in both interactive and silent mode.
   * Interactive mode will re-request the user to enter a parameter but silent won't. So the same RequestIdentifier should describe semantically different steps of an action.
   *
   * The preferred way to distinguish the action request id from other types of action is to add action group id followed by a '.' (dot) symbol with the step name, ie. getGroupId() + ".inputParams"
   *
   * Call this method only inside of the body registerParameters() to perform synchronization!
   */
  protected synchronized void registerCommand(ActionCommand cmd)
  {
    if (cmd == null)
    {
      throw new NullPointerException("cmd is null");
    }
    
    // To prevent changes of internal command structure
    cmd = cmd.clone();
    
    if (cmd.getRequestId() == null)
    {
      throw new IllegalArgumentException("Request ID is NULL");
    }
    
    if (commandList == null)
    {
      commandList = new LinkedList();
    }
    
    for (ActionCommand listCommand : commandList)
    {
      if (cmd.getRequestId().equals(listCommand.getRequestId()))
      {
        if (cmd.equals(listCommand))
        {
          return;
        }
        
        throw new IllegalArgumentException("Command has already been registered: " + cmd.getRequestId());
      }
    }
    
    commandList.add(cmd);
  }
  
  @SuppressWarnings("unlikely-arg-type")
  protected synchronized void unregisterCommand(String id)
  {
    if (commandList == null)
    {
      return;
    }
    
    for (Iterator<ActionCommand> iterator = commandList.iterator(); iterator.hasNext();)
    {
      ActionCommand listCommand = iterator.next();
      
      if (id.equals(listCommand.getRequestId()))
      {
        iterator.remove();
      }
    }
  }
  
  @Override
  public ReentrantLock getExecutionLock()
  {
    return executionLock;
  }
  
  public synchronized void removePropertyChangeListener(PropertyChangeListener l)
  {
    propertyChangeListeners.removePropertyChangeListener(l);
  }
  
  public synchronized void addPropertyChangeListener(PropertyChangeListener l)
  {
    propertyChangeListeners.addPropertyChangeListener(l);
  }
  
  @Override
  public boolean isHeadless()
  {
    return false;
  }
  
  @Override
  public Permissions getPermissions()
  {
    return permissions;
  }
  
  public void setPermissions(Permissions permissions)
  {
    this.permissions = permissions;
  }
  
  @Override
  public int compareTo(Object o)
  {
    return 0; // Not implemented
  }
  
  @Override
  public Integer getEntityType()
  {
    return ContextUtils.ENTITY_ACTION;
  }
}
