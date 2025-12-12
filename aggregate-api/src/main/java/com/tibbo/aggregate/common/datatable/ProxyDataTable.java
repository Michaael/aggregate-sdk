package com.tibbo.aggregate.common.datatable;

import java.lang.ref.*;
import java.util.*;

import com.tibbo.aggregate.common.*;
import com.tibbo.aggregate.common.context.*;
import com.tibbo.aggregate.common.datatable.encoding.*;
import com.tibbo.aggregate.common.protocol.*;
import com.tibbo.aggregate.common.util.*;
import com.tibbo.aggregate.common.view.*;

public class ProxyDataTable extends AbstractUnmodifiableDataTable
{
  private static final int DEFAULT_BATCH_SIZE = 100;
  
  private final Cache cache;
  private Context functionContext;
  
  public ProxyDataTable(ElementList elements, ClassicEncodingSettings settings, boolean validate, AggreGateDeviceController controller) throws DataTableException
  {
    accomplishConstruction(elements, settings, validate);
    
    if (id == null)
      throw new IllegalStateException("Table id must not be null");
    
    if (controller != null)
    {
      functionContext = controller.getContextManager().getRoot();
      
      if (functionContext == null)
        throw new IllegalStateException("functionContext must not be null");
    }
    
    cache = new Cache();
    
    makeImmutable();
  }
  
  @Override
  public void setId(Long id)
  {
    throw new UnsupportedOperationException(UNSUPPORTED);
  }
  
  @Override
  public void splitFormat()
  {
  }
  
  /**
   * Returns record with specified index.
   */
  
  @Override
  public DataRecord getRecord(int number)
  {
    final DataRecord record = cache.get(number);
    return record != null ? record : loadRecord(number);
  }
  
  private DataRecord loadRecord(int number)
  {
    final DataTable parameters = new SimpleDataTable(StorageHelper.FIFT_STORAGE_GET, id, number, DEFAULT_BATCH_SIZE);
    final DataTable result;
    try
    {
      result = functionContext.callFunction(StorageHelper.F_STORAGE_GET, parameters);
    }
    catch (ContextException exc)
    {
      throw new AggreGateRuntimeException("An error occurred when calling function: ", exc);
    }
    
    final Integer size = result.rec().getInt(StorageHelper.FOF_STORAGE_GET_SIZE);
    if (size == null)
      throw new IndexOutOfBoundsException("Record index is out of bounds");
    
    fillCache(number, result.rec().getDataTable(StorageHelper.FOF_STORAGE_GET_DATA));
    
    return result.rec().getDataTable(StorageHelper.FOF_STORAGE_GET_DATA).rec();
  }
  
  private void fillCache(int fromIndex, DataTable data)
  {
    int currentIndex = fromIndex;
    for (DataRecord record : data)
    {
      cache.put(currentIndex++, record);
    }
  }
  
  @Override
  public boolean equals(Object obj)
  {
    if (obj == this)
    {
      return true;
    }
    
    if (!(obj instanceof ProxyDataTable))
    {
      return false;
    }
    
    ProxyDataTable other = (ProxyDataTable) obj;
    
    if (!id.equals(other.getId()))
    {
      return false;
    }
    
    if (!format.equals(other.getFormat()))
    {
      return false;
    }
    
    if (!Util.equals(quality, other.quality))
    {
      return false;
    }
    
    return true;
  }
  
  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    result = prime * result + ((format == null) ? 0 : format.hashCode());
    result = prime * result + ((quality == null) ? 0 : quality.hashCode());
    return result;
  }
  
  @Override
  void getEncodedRecordsOrTableID(StringBuilder finalSB, ClassicEncodingSettings settings, Boolean isTransferEncode, Integer encodeLevel)
  {
    throw new UnsupportedOperationException(UNSUPPORTED);
  }
  
  @Override
  public String toDefaultString()
  {
    return "Proxy data table with format: " + format.toString();
  }
  
  @Override
  public String dataAsString(boolean showFieldNames, boolean showHiddenFields, boolean showPasswords)
  {
    throw new UnsupportedOperationException(UNSUPPORTED);
  }
  
  /**
   * Returns true if table has exactly one record and one field.
   */
  @Override
  public boolean isOneCellTable()
  {
    if (getFieldCount() != 1)
      return false;
    
    boolean zerothRecordExists = true;
    try
    {
      rec();
    }
    catch (IndexOutOfBoundsException exc)
    {
      zerothRecordExists = false;
    }
    
    if (!zerothRecordExists)
      return false;
    
    try
    {
      getRecord(1);
    }
    catch (IndexOutOfBoundsException exc)
    {
      return true;
    }
    
    return false;
  }
  
  @Override
  public Integer findIndex(DataTableQuery query)
  {
    int index = 0;
    for (DataRecord record : this)
    {
      boolean meet = true;
      
      for (QueryCondition cond : query.getConditions())
      {
        if (!record.meetToCondition(cond))
        {
          meet = false;
        }
      }
      
      if (meet)
      {
        return index;
      }
      
      index++;
    }
    
    return null;
  }
  
  @Override
  public Iterator<DataRecord> iterator()
  {
    return new Iter();
  }
  
  @Override
  public Iterator<DataRecord> iterator(int index)
  {
    return new Iter(index);
  }
  
  private class Iter implements Iterator<DataRecord>
  {
    private int index;
    private DataRecord nextRecord;
    private boolean isNextRecordSet;
    
    Iter()
    {
      index = 0;
    }
    
    Iter(int index)
    {
      this.index = index;
    }
    
    @Override
    public boolean hasNext()
    {
      if (isNextRecordSet || setNextRecord())
      {
        return true;
      }
      
      setRecordCount(index);
      return false;
    }
    
    @Override
    public DataRecord next()
    {
      if (!isNextRecordSet && !setNextRecord())
        throw new NoSuchElementException();
      
      isNextRecordSet = false;
      return nextRecord;
    }
    
    private boolean setNextRecord()
    {
      try
      {
        nextRecord = getRecord(index);
        isNextRecordSet = true;
        index++;
        return true;
      }
      catch (IndexOutOfBoundsException exc)
      {
        return false;
      }
    }
  }
  
  private static class Cache
  {
    private final Map<Integer, SoftReference<DataRecord>> map = new HashMap<>(DEFAULT_BATCH_SIZE);
    
    private DataRecord get(Integer key)
    {
      final SoftReference<DataRecord> softRef = map.get(key);
      
      if (softRef == null)
        return null;
      
      return softRef.get();
    }
    
    private DataRecord put(Integer key, DataRecord value)
    {
      final SoftReference<DataRecord> softRef = map.put(key, new SoftReference<>(value));
      
      if (softRef == null)
        return null;
      
      final DataRecord oldValue = softRef.get();
      softRef.clear();
      
      return oldValue;
    }
    
    @SuppressWarnings("unused")
    private DataRecord remove(Integer key)
    {
      final SoftReference<DataRecord> softRef = map.remove(key);
      
      if (softRef == null)
        return null;
      
      final DataRecord oldValue = softRef.get();
      softRef.clear();
      
      return oldValue;
    }
  }
  
  public Context getFunctionContext()
  {
    return functionContext;
  }
  
  public void setFunctionContext(Context functionContext)
  {
    this.functionContext = functionContext;
  }
  
  @Override
  public void close()
  {
    try
    {
      functionContext.callFunction(StorageHelper.F_STORAGE_CLOSE, new SimpleDataTable(StorageHelper.FIFT_STORAGE_CLOSE, id));
    }
    catch (ContextException exc)
    {
      throw new AggreGateRuntimeException("An error occurred when calling function: ", exc);
    }
  }
}
