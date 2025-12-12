package com.tibbo.aggregate.common.context;

import java.lang.ref.*;

import com.tibbo.aggregate.common.datatable.*;
import com.tibbo.aggregate.common.event.*;
import com.tibbo.aggregate.common.expression.*;
import com.tibbo.aggregate.common.security.*;
import com.tibbo.aggregate.common.server.*;

public class EventDefinition extends AbstractEntityDefinition implements Cloneable, Comparable<EventDefinition>
{
  public static int CONCURRENCY_SYNCHRONOUS = 0;
  public static int CONCURRENCY_SEQUENTIAL = 1;
  public static int CONCURRENCY_CONCURRENT = 2;
  
  private TableFormat format;
  private boolean hidden;
  private Permissions permissions;
  private long expirationPeriod; // Milliseconds, 0 for non-persistent
  private int level;
  private Permissions firePermissions;
  private int queueLength = EventData.UNDISPATCHED_EVENTS_QUEUE_LENGTH;
  
  private int concurrency = CONCURRENCY_SEQUENTIAL;
  
  private PersistenceOptions persistenceOptions = new PersistenceOptions();
  private Integer memoryStorageSize;
  private boolean sessionBound = false;
  private String fingerprintExpression;
  private SoftReference<Expression> cachedFingerprintExpression;
  
  public EventDefinition(String name, TableFormat format)
  {
    init(name, format, null);
  }
  
  public EventDefinition(String name, TableFormat format, String description)
  {
    init(name, format, description);
  }
  
  public EventDefinition(String name, TableFormat format, String description, String group)
  {
    init(name, format, description);
    setGroup(group);
  }
  
  private void init(String name, TableFormat format, String description)
  {
    setName(name);
    
    setFormat(format);
    
    setDescription(description);
  }
  
  public void setFormat(TableFormat format)
  {
    if (format != null)
    {
      format.makeImmutable(null);
    }
    
    this.format = format;
  }
  
  public void setHidden(boolean hidden)
  {
    this.hidden = hidden;
  }
  
  public void setPermissions(Permissions permissions)
  {
    this.permissions = permissions;
  }
  
  public void setExpirationPeriod(long expirationPeriod)
  {
    this.expirationPeriod = expirationPeriod;
  }
  
  public void setLevel(int level)
  {
    this.level = level;
  }
  
  public TableFormat getFormat()
  {
    return format;
  }
  
  public boolean isHidden()
  {
    return hidden;
  }
  
  public Permissions getPermissions()
  {
    return permissions;
  }
  
  public long getExpirationPeriod()
  {
    return expirationPeriod;
  }
  
  public int getLevel()
  {
    return level;
  }
  
  public Permissions getFirePermissions()
  {
    return firePermissions;
  }
  
  public void setFirePermissions(Permissions firePermissions)
  {
    this.firePermissions = firePermissions;
  }
  
  public PersistenceOptions getPersistenceOptions()
  {
    return persistenceOptions;
  }
  
  public Integer getMemoryStorageSize()
  {
    return memoryStorageSize;
  }
  
  public void setMemoryStorageSize(Integer memoryStorageSize)
  {
    this.memoryStorageSize = memoryStorageSize;
  }
  
  public int getConcurrency()
  {
    return concurrency;
  }
  
  public void setConcurrency(int concurrency)
  {
    this.concurrency = concurrency;
  }
  
  public void setSessionBound(boolean sessionBound)
  {
    this.sessionBound = sessionBound;
  }
  
  public boolean isSessionBound()
  {
    return sessionBound;
  }
  
  @Override
  public EventDefinition clone()
  {
    try
    {
      EventDefinition clone = (EventDefinition) super.clone();
      
      clone.persistenceOptions = persistenceOptions.clone();
      
      return clone;
    }
    catch (CloneNotSupportedException ex)
    {
      throw new IllegalStateException(ex.getMessage(), ex);
    }
  }
  
  @Override
  public int compareTo(EventDefinition d)
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
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((getDescription() == null) ? 0 : getDescription().hashCode());
    result = prime * result + (int) (expirationPeriod ^ (expirationPeriod >>> 32));
    result = prime * result + ((format == null) ? 0 : format.hashCode());
    result = prime * result + ((getGroup() == null) ? 0 : getGroup().hashCode());
    result = prime * result + ((getHelp() == null) ? 0 : getHelp().hashCode());
    result = prime * result + (hidden ? 1231 : 1237);
    result = prime * result + ((getIconId() == null) ? 0 : getIconId().hashCode());
    result = prime * result + ((getIndex() == null) ? 0 : getIndex().hashCode());
    result = prime * result + level;
    result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
    result = prime * result + ((permissions == null) ? 0 : permissions.hashCode());
    result = prime * result + ((firePermissions == null) ? 0 : firePermissions.hashCode());
    result = prime * result + (sessionBound ? 1231 : 1237);
    result = prime * result + ((fingerprintExpression == null) ? 0 : fingerprintExpression.hashCode());
    return result;
  }
  
  @Override
  public boolean equals(Object obj)
  {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    EventDefinition other = (EventDefinition) obj;
    if (getDescription() == null)
    {
      if (other.getDescription() != null)
        return false;
    }
    else if (!getDescription().equals(other.getDescription()))
      return false;
    if (expirationPeriod != other.expirationPeriod)
      return false;
    if (format == null)
    {
      if (other.format != null)
        return false;
    }
    else if (!format.equals(other.format))
      return false;
    if (getGroup() == null)
    {
      if (other.getGroup() != null)
        return false;
    }
    else if (!getGroup().equals(other.getGroup()))
      return false;
    if (getHelp() == null)
    {
      if (other.getHelp() != null)
        return false;
    }
    else if (!getHelp().equals(other.getHelp()))
      return false;
    if (hidden != other.hidden)
      return false;
    if (getIconId() == null)
    {
      if (other.getIconId() != null)
        return false;
    }
    else if (!getIconId().equals(other.getIconId()))
      return false;
    if (getIndex() == null)
    {
      if (other.getIndex() != null)
        return false;
    }
    else if (!getIndex().equals(other.getIndex()))
      return false;
    if (level != other.level)
      return false;
    if (getName() == null)
    {
      if (other.getName() != null)
        return false;
    }
    else if (!getName().equals(other.getName()))
      return false;
    if (permissions == null)
    {
      if (other.permissions != null)
        return false;
    }
    else if (!permissions.equals(other.permissions))
      return false;
    if (firePermissions == null)
    {
      if (other.firePermissions != null)
        return false;
    }
    else if (!firePermissions.equals(other.firePermissions))
      return false;
    else if ((sessionBound != other.sessionBound))
      return false;
    if (fingerprintExpression == null)
    {
      return other.fingerprintExpression == null;
    }
    else
      return fingerprintExpression.equals(other.fingerprintExpression);
  }
  
  public String getFingerprintExpression()
  {
    return fingerprintExpression;
  }
  
  public void setFingerprintExpression(String expression)
  {
    fingerprintExpression = expression;
    
    synchronized (this)
    {
      cachedFingerprintExpression = null;
    }
  }
  
  private Expression cacheFingerprintExpression()
  {
    synchronized (this)
    {
      Expression expression = cachedFingerprintExpression != null ? cachedFingerprintExpression.get() : null;
      
      if (expression == null)
      {
        expression = new Expression(fingerprintExpression);
        cachedFingerprintExpression = new SoftReference<>(expression);
      }
      
      return expression;
    }
  }
  
  public Expression getCachedFingerprintExpression()
  {
    Expression expression = cachedFingerprintExpression != null ? cachedFingerprintExpression.get() : null;
    
    return expression == null ? cacheFingerprintExpression() : expression;
  }
  
  @Override
  public Integer getEntityType()
  {
    return ContextUtils.ENTITY_EVENT;
  }
  
  public int getQueueLength()
  {
    return queueLength;
  }
  
  public void setQueueLength(int queueLength)
  {
    this.queueLength = queueLength;
  }

  public boolean isDebuggingEvaluations()
  {
    return ServerContextConstants.E_EVALUATION.equals(getName());
  }
}
