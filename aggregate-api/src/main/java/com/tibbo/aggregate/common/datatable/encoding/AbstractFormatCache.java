package com.tibbo.aggregate.common.datatable.encoding;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.tibbo.aggregate.common.Log;
import com.tibbo.aggregate.common.datatable.TableFormat;

public abstract class AbstractFormatCache implements FormatCache
{
  protected final Map<Integer, TableFormat> cache = new HashMap<>(100, 0.75f);
  
  protected final Map<TableFormat, Integer> reverse = new HashMap<>(100, 0.75f);
  
  protected final ReentrantReadWriteLock cacheLock = new ReentrantReadWriteLock();
  
  private final String name;
  
  public AbstractFormatCache(String name)
  {
    this.name = name;
  }
  
  public String getName()
  {
    return name;
  }
  
  public int getSize()
  {
    return cache.size();
  }
  
  public TableFormat get(int id)
  {
    TableFormat result;
    
    cacheLock.readLock().lock();
    try
    {
      result = cache.get(id);
    }
    finally
    {
      cacheLock.readLock().unlock();
    }
    
    return result;
  }
  
  protected TableFormat addImpl(TableFormat format, int id)
  {
    if (format.isImmutable())
    {
      format.setId(id);
    }
    
    format.setFormatCacheIdentityHashCode(System.identityHashCode(this));
    
    reverse.put(format, id);
    
    return cache.put(id, format);
  }
  
  public Integer addIfNotExists(TableFormat format)
  {
    Integer formatId = obtainId(format);
    
    if (formatId == null)
    {
      cacheLock.writeLock().lock();
      try
      {
        formatId = obtainId(format);
        
        if (formatId == null)
        {
          formatId = add(format);
        }
      }
      finally
      {
        cacheLock.writeLock().unlock();
      }
    }
    
    return formatId;
  }
  
  protected abstract Integer add(TableFormat tableFormat);

  protected Integer obtainId(TableFormat format)
  {
    Integer idFromFormat;
    Integer idFromCache;

    cacheLock.readLock().lock();
    try
    {
      idFromCache = reverse.get(format);
      idFromFormat = format.getId();
    }
    finally
    {
      cacheLock.readLock().unlock();
    }

    if (Objects.equals(idFromFormat, idFromCache))
    {
      return idFromFormat;    // this is the mainstream case that should take place most frequently
    }

    // All the following is a set of attempts to properly handle various deviations
    //noinspection ConstantConditions (idFromCache != null is always true but left explicit for clarity)
    if (idFromFormat == null && idFromCache != null)
    {
      // Finding a format without ID in the cache means that the format wasn't cached before but happened to be equal
      // to a one already cached. This is logically the same as being cached in usual way.
      return idFromCache;
    }

    // Coming here means that the format has some ID but there is no corresponding cache entry.
    boolean attachedToAnotherCache = format.isAttachedToAnotherCache(this);
    // This can be OK if the format is cached somewhere else:
    if (attachedToAnotherCache)
    {
      return idFromFormat;
    }
    // But also can be a sign of serious inconsistency if the format should have been cached here but somehow wasn't
    throw new IllegalStateException(String.format("The following table format must be stored in current cache (%s, " +
            "identityHashCode=%d) with ID=%d but was not actually: %s", this.getClass().getSimpleName(),
            System.identityHashCode(this), idFromFormat, format));
  }
  
  public TableFormat getCachedVersion(TableFormat format)
  {
    if (format == null)
    {
      return null;
    }
    
    cacheLock.readLock().lock();
    try
    {
      Integer id = obtainId(format);
      
      return id != null ? cache.get(id) : format;
    }
    finally
    {
      cacheLock.readLock().unlock();
    }
  }
  
  public void clear()
  {
    cacheLock.writeLock().lock();
    try
    {
      cache.clear();
      reverse.clear();
    }
    finally
    {
      cacheLock.writeLock().unlock();
    }
  }

  public void put(int id, TableFormat format)
  {
    cacheLock.writeLock().lock();
    try
    {

      if (format == null)
      {
        throw new IllegalArgumentException("Format is NULL");
      }

      TableFormat tableFormat = addImpl(format, id);

      if (Log.PROTOCOL_CACHING.isDebugEnabled())
      {
        if (tableFormat != null)
        {
          Log.PROTOCOL_CACHING.debug("Cache '" + getName() + "' override format as #" + id + ": " + format);
        }
      }
    }
    finally
    {
      cacheLock.writeLock().unlock();
    }
  }
}
