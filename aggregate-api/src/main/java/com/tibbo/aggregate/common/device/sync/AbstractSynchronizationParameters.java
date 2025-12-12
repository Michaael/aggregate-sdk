package com.tibbo.aggregate.common.device.sync;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

import com.tibbo.aggregate.common.device.DeviceContext;
import com.tibbo.aggregate.common.util.StringUtils;

public abstract class AbstractSynchronizationParameters implements SynchronizationParameters
{
  public final String id;
  
  public final boolean shouldReadMetadata;
  
  public final boolean shouldPersistStatus;
  
  public final boolean shouldUseExtendedStatus;
  
  public final Integer directionOverride;
  
  public final Set<String> variables = new LinkedHashSet<>();
  
  public AbstractSynchronizationParameters(String id, boolean shouldReadMetadata, boolean shouldPersistStatus, boolean shouldUseExtendedStatus)
  {
    this(id, shouldReadMetadata, shouldPersistStatus, shouldUseExtendedStatus, DeviceContext.DIRECTION_AUTO);
  }
  
  public AbstractSynchronizationParameters(String id, boolean shouldReadMetadata, boolean shouldPersistStatus, boolean shouldUseExtendedStatus, Integer directionOverride)
  {
    this.id = id;
    this.shouldReadMetadata = shouldReadMetadata;
    this.shouldPersistStatus = shouldPersistStatus;
    this.shouldUseExtendedStatus = shouldUseExtendedStatus;
    this.directionOverride = directionOverride;
  }
  
  @Override
  public boolean isShouldReadMetadata()
  {
    return shouldReadMetadata;
  }
  
  @Override
  public boolean isShouldPersistStatus()
  {
    return shouldPersistStatus;
  }
  
  @Override
  public boolean isShouldUseExtendedStatus()
  {
    return shouldUseExtendedStatus;
  }
  
  @Override
  public boolean isConnectOnly()
  {
    return false;
  }
  
  @Override
  public void addVariable(String variable)
  {
    variables.add(variable);
  }
  
  @Override
  public void removeVariable(String variable)
  {
    variables.remove(variable);
  }
  
  @Override
  public boolean hasMoreVariables()
  {
    return !variables.isEmpty();
  }
  
  @Override
  public boolean hasVariable(String variable)
  {
    return variables.contains(variable);
  }
  
  @Override
  public Set<String> getVariables()
  {
    return Collections.unmodifiableSet(variables);
  }
  
  @Override
  public boolean isFull()
  {
    return variables.isEmpty();
  }
  
  @Override
  public String getId()
  {
    return id;
  }
  
  @Override
  public Integer getDirectionOverride()
  {
    return directionOverride;
  }

  @Override
  public String toString()
  {
    return "read metadata: " + (shouldReadMetadata ? "yes" : "no") + ", use extended status: " + (shouldUseExtendedStatus ? "yes" : "no") + ", settings: " + StringUtils.print(variables);
  }
  
  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + (isConnectOnly() ? 1231 : 1237);
    result = prime * result + directionOverride;
    result = prime * result + variables.hashCode();
    result = prime * result + (shouldPersistStatus ? 1231 : 1237);
    result = prime * result + (shouldReadMetadata ? 1231 : 1237);
    result = prime * result + (shouldUseExtendedStatus ? 1231 : 1237);
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
    SynchronizationParameters other = (SynchronizationParameters) obj;
    if (isConnectOnly() != other.isConnectOnly())
      return false;
    if (Objects.equals(directionOverride, other.getDirectionOverride()))
      return false;
    if (!variables.equals(other.getVariables()))
      return false;
    if (shouldPersistStatus != other.isShouldPersistStatus())
      return false;
    if (shouldReadMetadata != other.isShouldReadMetadata())
      return false;
    return shouldUseExtendedStatus == other.isShouldUseExtendedStatus();
  }
}
