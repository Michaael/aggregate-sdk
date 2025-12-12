package com.tibbo.aggregate.common.datatable.encoding;

import com.tibbo.aggregate.common.Log;
import com.tibbo.aggregate.common.datatable.TableFormat;

/*
* Format cache that belongs to the current server. Formats are coming to the cache and receive their indexes.
*
**/
public class LocalFormatCache extends AbstractFormatCache
{
  private int currentId = 0;
  
  public LocalFormatCache(String name) {
    super(name);
  }
  
  protected Integer add(TableFormat format)
  {
    if (format == null)
    {
      throw new IllegalArgumentException("Format is NULL");
    }
    cacheLock.writeLock().lock();
    try
    {
      int id = currentId++;
      
      addImpl(format, id);
      
      if (!format.isImmutable())
      {
        Log.PROTOCOL_CACHING.warn("Cached mutable format as #" + id + ": " + format, new Exception());
      }
      
      if (Log.PROTOCOL_CACHING.isDebugEnabled())
      {
        Log.PROTOCOL_CACHING.debug("Cache '" + getName() + "' cached format as #" + id + ": " + format);
      }
      
      return id;
    }
    finally
    {
      cacheLock.writeLock().unlock();
    }
  }
}
