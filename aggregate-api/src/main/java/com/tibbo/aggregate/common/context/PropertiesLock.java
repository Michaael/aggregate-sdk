package com.tibbo.aggregate.common.context;

import java.text.*;
import java.util.*;
import java.util.concurrent.locks.*;

import com.tibbo.aggregate.common.*;

public class PropertiesLock
{
  private final Context context;
  private final Set<String> lockingPropertiesEditorUUIDs = new HashSet<>();
  private final ReadWriteLock rwLock = new ReentrantReadWriteLock();
  
  private CallerController lockedBy;
  
  public PropertiesLock(Context context)
  {
    this.context = context;
  }
  
  public String lockedBy()
  {
    rwLock.readLock().lock();
    try
    {
      return lockedBy != null ? getLockOwnerName(lockedBy) : null;
    }
    finally
    {
      rwLock.readLock().unlock();
    }
  }
  
  public String lock(CallerController caller, String propertiesEditorUUID) throws ContextException
  {
    Objects.requireNonNull(caller, "Caller must not be null");
    Objects.requireNonNull(propertiesEditorUUID, "Properties Editor UUID must not be null");
    
    rwLock.writeLock().lock();
    try
    {
      if (lockedBy != null && !lockedBy.equals(caller))
        throw new ContextException(getLockedMessage());
      
      lockingPropertiesEditorUUIDs.add(propertiesEditorUUID);
      
      if (lockedBy != null && lockedBy.equals(caller))
        return getLockOwnerName(lockedBy);
      
      Log.CONTEXT.debug("Lock for context '" + context.getPath() + "' was acquired by: '" + caller + "'");
      
      lockedBy = caller;
      
      caller.addLockedContext(context);
      
      return getLockOwnerName(lockedBy);
    }
    finally
    {
      rwLock.writeLock().unlock();
    }
  }
  
  private String getLockedMessage()
  {
    return MessageFormat.format(Cres.get().getString("conPropLockErrLocked"), context.getPath(), lockedBy);
  }
  
  public boolean unlock(CallerController caller, String propertiesEditorUUID) throws ContextException
  {
    Objects.requireNonNull(caller, "Caller must not be null");
    Objects.requireNonNull(propertiesEditorUUID, "Properties Editor UUID must not be null");
    
    rwLock.writeLock().lock();
    try
    {
      if (!caller.equals(lockedBy))
        throw new ContextException(getLockedMessage());
      
      lockingPropertiesEditorUUIDs.remove(propertiesEditorUUID);
      
      if (!lockingPropertiesEditorUUIDs.isEmpty())
        return false;
      
      Log.CONTEXT.debug("Lock for context '" + context.getPath() + "' held by '" + lockedBy + "' was released");
      lockedBy = null;
      
      caller.removeLockedContext(context);
      
      return true;
    }
    finally
    {
      rwLock.writeLock().unlock();
    }
  }
  
  public void breakLock()
  {
    rwLock.writeLock().lock();
    try
    {
      if (lockedBy != null)
      {
        Log.CONTEXT.warn("Lock for context '" + context.getPath() + "' held by '" + lockedBy + "' was forced");
        
        lockedBy.removeLockedContext(context);
      }
      
      lockedBy = null;
      lockingPropertiesEditorUUIDs.clear();
    }
    finally
    {
      rwLock.writeLock().unlock();
    }
  }
  
  private String getLockOwnerName(CallerController caller)
  {
    return caller.getEffectiveUsername();
  }
}
