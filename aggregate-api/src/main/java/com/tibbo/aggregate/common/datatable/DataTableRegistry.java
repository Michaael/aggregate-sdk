package com.tibbo.aggregate.common.datatable;

import java.lang.ref.*;
import java.util.*;
import java.util.concurrent.locks.*;

import com.tibbo.aggregate.common.*;

public class DataTableRegistry
{
  private long currentId = 0L;
  
  private final Map<Long, SoftReference<DataTable>> dataTables = new WeakHashMap<>(100);
  
  private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
  
  public long add(DataTable table)
  {
    lock.writeLock().lock();
    try
    {
      if (table == null)
      {
        throw new IllegalArgumentException("Data table is null");
      }
      
      long id = currentId++;
      
      table.setId(id);
      dataTables.put(id, new SoftReference<>(table));
      
      if (Log.DATATABLE.isDebugEnabled())
      {
        if (!table.isImmutable())
        {
          Log.DATATABLE.debug("Added mutable table '" + table + "' to the data table registry as #" + id);
        }
        else
        {
          Log.DATATABLE.debug("Added table '" + table + "' to the data table registry as #" + id);
        }
      }
      
      return id;
    }
    finally
    {
      lock.writeLock().unlock();
    }
  }
  
  public DataTable get(Long id)
  {
    final DataTable result;
    final SoftReference<DataTable> reference;
    
    lock.readLock().lock();
    try
    {
      // Оптимизация: проверяем существование записи перед обращением к get()
      reference = dataTables.get(id);
      if (reference == null)
      {
        throw new IllegalStateException("No data table with id #" + id + " was found in the data table registry");
      }
      result = reference.get();
    }
    finally
    {
      lock.readLock().unlock();
    }
    
    if (result == null)
    {
      // Таблица была удалена GC, удаляем запись из реестра
      close(id);
      throw new IllegalStateException("Data table with id #" + id + " was garbage collected");
    }
    
    return result;
  }
  
  public void close(Long id)
  {
    try
    {
      lock.writeLock().lock();
      dataTables.remove(id);
    }
    finally
    {
      lock.writeLock().unlock();
    }
  }
}
