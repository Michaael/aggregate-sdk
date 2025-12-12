package com.tibbo.aggregate.common.context;

import java.util.*;

import com.tibbo.aggregate.common.datatable.*;
import com.tibbo.aggregate.common.security.*;

public class VariableDefinition extends AbstractEntityDefinition implements Cloneable, Comparable<VariableDefinition>
{
  public static final int HISTORY_RATE_CHANGES = -1;
  public static final int HISTORY_RATE_ALL = 0;
  
  public static final int CACHING_NONE = 0;
  public static final int CACHING_HARD = 1;
  public static final int CACHING_SOFT = 2;
  
  private TableFormat format;
  private boolean readable;
  private boolean writable;
  private boolean hidden;
  private Permissions readPermissions;
  private Permissions writePermissions;
  private String helpId;
  
  private VariableGetter getter;
  private VariableSetter setter;
  
  private boolean allowUpdateEvents;
  private Long changeEventsExpirationPeriod; // Milliseconds
  private int localCachingMode = CACHING_SOFT;
  private Long remoteCacheTime;
  private boolean addPreviousValueToVariableUpdateEvent;
  
  private Class valueClass;
  private List<CompatibilityConverter> compatibilityConverters = null;
  
  private boolean persistent = true;
  private DataTable defaultValue;
  private Integer historyRate = HISTORY_RATE_ALL;
  
  private CompatibilityValidator compatibilityValidator = null;
  
  public VariableDefinition(String name, TableFormat format, boolean readable, boolean writable)
  {
    init(name, format, readable, writable, null);
  }
  
  public VariableDefinition(String name, TableFormat format, boolean readable, boolean writable, String description)
  {
    init(name, format, readable, writable, description);
  }
  
  public VariableDefinition(String name, TableFormat format, boolean readable, boolean writable, String description, String group)
  {
    init(name, format, readable, writable, description);
    setGroup(group);
  }
  
  private void init(String name, TableFormat format, boolean readable, boolean writable, String description)
  {
    setName(name);
    
    setFormat(format);
    
    this.readable = readable;
    this.writable = writable;
    setDescription(description != null ? description : name);
  }
  
  public void setFormat(TableFormat format)
  {
    if (format != null)
    {
      format.makeImmutable(null);
    }
    
    this.format = format;
  }
  
  public void setReadable(boolean readable)
  {
    this.readable = readable;
  }
  
  public void setWritable(boolean writable)
  {
    this.writable = writable;
  }
  
  public void setHidden(boolean hidden)
  {
    this.hidden = hidden;
  }
  
  @Override
  public void setGroup(String group)
  {
    super.setGroup(group);
    if (group != null)
    {
      allowUpdateEvents = true;
    }
  }
  
  public void setReadPermissions(Permissions readPermissions)
  {
    this.readPermissions = readPermissions;
  }
  
  public void setWritePermissions(Permissions writePermissions)
  {
    this.writePermissions = writePermissions;
  }
  
  public void setSetter(VariableSetter setter)
  {
    this.setter = setter;
  }
  
  public void setGetter(VariableGetter getter)
  {
    this.getter = getter;
  }
  
  public TableFormat getFormat()
  {
    return format;
  }
  
  public boolean isReadable()
  {
    return readable;
  }
  
  public boolean isWritable()
  {
    return writable;
  }
  
  public boolean isHidden()
  {
    return hidden;
  }
  
  public Permissions getReadPermissions()
  {
    return readPermissions;
  }
  
  public Permissions getWritePermissions()
  {
    return writePermissions;
  }
  
  public VariableSetter getSetter()
  {
    return setter;
  }
  
  public VariableGetter getGetter()
  {
    return getter;
  }
  
  public String getHelpId()
  {
    return helpId;
  }
  
  public void setHelpId(String helpId)
  {
    this.helpId = helpId;
  }
  
  public Class getValueClass()
  {
    return valueClass;
  }
  
  public void setValueClass(Class valueClass)
  {
    this.valueClass = valueClass;
  }
  
  public Long getChangeEventsExpirationPeriod()
  {
    return changeEventsExpirationPeriod;
  }
  
  /**
   * Sets duration of update events storage (in milliseconds). Null duration disables update events persistent storage.
   */
  public void setChangeEventsExpirationPeriod(Long changeEventsExpirationPeriod)
  {
    this.changeEventsExpirationPeriod = changeEventsExpirationPeriod;
  }
  
  public boolean isLocalCachingEnabled()
  {
    return localCachingMode != CACHING_NONE;
  }
  
  public int getLocalCachingMode()
  {
    return localCachingMode;
  }
  
  public void setLocalCachingMode(int value)
  {
    this.localCachingMode = value;
  }
  
  public Long getRemoteCacheTime()
  {
    return remoteCacheTime;
  }
  
  public void setRemoteCacheTime(Long remoteCacheTime)
  {
    this.remoteCacheTime = remoteCacheTime;
  }
  
  public Boolean isAddPreviousValueToVariableUpdateEvent()
  {
    return addPreviousValueToVariableUpdateEvent;
  }
  
  public void setAddPreviousValueToVariableUpdateEvent(boolean addPreviousValueToVariableUpdateEvent)
  {
    this.addPreviousValueToVariableUpdateEvent = addPreviousValueToVariableUpdateEvent;
  }
  
  public DataTable getDefaultValue()
  {
    return defaultValue;
  }
  
  public void setDefaultValue(DataTable defaultValue)
  {
    this.defaultValue = defaultValue;
  }
  
  public boolean isPersistent()
  {
    return persistent;
  }
  
  public void setPersistent(boolean persistent)
  {
    this.persistent = persistent;
  }
  
  public boolean isAllowUpdateEvents()
  {
    return allowUpdateEvents;
  }
  
  public void setAllowUpdateEvents(boolean allowUpdateEvents)
  {
    this.allowUpdateEvents = allowUpdateEvents;
  }
  
  public Integer getHistoryRate()
  {
    return historyRate;
  }
  
  public void setHistoryRate(Integer historyRate)
  {
    this.historyRate = historyRate;
  }
  
  public boolean storeChangesOnlyInHistory()
  {
    return HISTORY_RATE_CHANGES == historyRate;
  }
  
  public void addCompatibilityConverter(CompatibilityConverter converter)
  {
    if (compatibilityConverters == null)
    {
      compatibilityConverters = new LinkedList();
    }
    
    compatibilityConverters.add(converter);
  }
  
  public List<CompatibilityConverter> getCompatibilityConverters()
  {
    return compatibilityConverters;
  }
  
  public CompatibilityValidator getCompatibilityValidator()
  {
    return compatibilityValidator;
  }
  
  public void setCompatibilityValidator(CompatibilityValidator compatibilityValidator)
  {
    this.compatibilityValidator = compatibilityValidator;
  }
  
  @Override
  public VariableDefinition clone()
  {
    try
    {
      return (VariableDefinition) super.clone();
    }
    catch (CloneNotSupportedException ex)
    {
      throw new IllegalStateException(ex.getMessage(), ex);
    }
  }
  
  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((getDescription() == null) ? 0 : getDescription().hashCode());
    result = prime * result + ((format == null) ? 0 : format.hashCode());
    result = prime * result + ((getGroup() == null) ? 0 : getGroup().hashCode());
    result = prime * result + ((getHelp() == null) ? 0 : getHelp().hashCode());
    result = prime * result + (hidden ? 1231 : 1237);
    result = prime * result + ((getIconId() == null) ? 0 : getIconId().hashCode());
    result = prime * result + ((helpId == null) ? 0 : helpId.hashCode());
    result = prime * result + ((getIndex() == null) ? 0 : getIndex().hashCode());
    result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
    result = prime * result + (readable ? 1231 : 1237);
    result = prime * result + (writable ? 1231 : 1237);
    result = prime * result + ((changeEventsExpirationPeriod == null) ? 0 : changeEventsExpirationPeriod.hashCode());
    result = prime * result + ((readPermissions == null) ? 0 : readPermissions.hashCode());
    result = prime * result + ((writePermissions == null) ? 0 : writePermissions.hashCode());
    result = prime * result + (persistent ? 1231 : 1237);
    result = prime * result + ((historyRate == null) ? 0 : historyRate.hashCode());
    
    return result;
  }
  
  @Override
  public boolean equals(Object obj)
  {
    if (this == obj)
    {
      return true;
    }
    if (obj == null)
    {
      return false;
    }
    if (getClass() != obj.getClass())
    {
      return false;
    }
    VariableDefinition other = (VariableDefinition) obj;
    if (getDescription() == null)
    {
      if (other.getDescription() != null)
      {
        return false;
      }
    }
    else if (!getDescription().equals(other.getDescription()))
    {
      return false;
    }
    if (format == null)
    {
      if (other.format != null)
      {
        return false;
      }
    }
    else if (!format.equals(other.format))
    {
      return false;
    }
    if (getGroup() == null)
    {
      if (other.getGroup() != null)
      {
        return false;
      }
    }
    else if (!getGroup().equals(other.getGroup()))
    {
      return false;
    }
    if (getHelp() == null)
    {
      if (other.getHelp() != null)
      {
        return false;
      }
    }
    else if (!getHelp().equals(other.getHelp()))
    {
      return false;
    }
    if (hidden != other.hidden)
    {
      return false;
    }
    if (getIconId() == null)
    {
      if (other.getIconId() != null)
      {
        return false;
      }
    }
    else if (!getIconId().equals(other.getIconId()))
    {
      return false;
    }
    if (helpId == null)
    {
      if (other.helpId != null)
      {
        return false;
      }
    }
    else if (!helpId.equals(other.helpId))
    {
      return false;
    }
    if (getIndex() == null)
    {
      if (other.getIndex() != null)
      {
        return false;
      }
    }
    else if (!getIndex().equals(other.getIndex()))
    {
      return false;
    }
    if (getName() == null)
    {
      if (other.getName() != null)
      {
        return false;
      }
    }
    else if (!getName().equals(other.getName()))
    {
      return false;
    }
    if (changeEventsExpirationPeriod == null)
    {
      if (other.changeEventsExpirationPeriod != null)
      {
        return false;
      }
    }
    else if (!changeEventsExpirationPeriod.equals(other.changeEventsExpirationPeriod))
    {
      return false;
    }
    if (readable != other.readable)
    {
      return false;
    }
    if (writable != other.writable)
    {
      return false;
    }
    if (readPermissions == null)
    {
      if (other.readPermissions != null)
      {
        return false;
      }
    }
    else if (!readPermissions.equals(other.readPermissions))
    {
      return false;
    }
    if (writePermissions == null)
    {
      if (other.writePermissions != null)
      {
        return false;
      }
    }
    else if (!writePermissions.equals(other.writePermissions))
    {
      return false;
    }
    if (persistent != other.persistent)
    {
      return false;
    }
    if (historyRate == null)
    {
      if (other.historyRate != null)
      {
        return false;
      }
    }
    else if (!historyRate.equals(other.historyRate))
    {
      return false;
    }
    
    return true;
  }
  
  @Override
  public int compareTo(VariableDefinition d)
  {
    if (getIndex() != null || d.getIndex() != null)
    {
      Integer my = getIndex() != null ? getIndex() : new Integer(0);
      Integer other = d.getIndex() != null ? d.getIndex() : new Integer(0);
      return other.compareTo(my);
    }
    
    return 0;
  }
  
  @Override
  public Integer getEntityType()
  {
    return ContextUtils.ENTITY_VARIABLE;
  }
}
