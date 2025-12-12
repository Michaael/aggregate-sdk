package com.tibbo.aggregate.common.datatable;

import java.util.*;
import java.util.stream.*;

import com.tibbo.aggregate.common.datatable.encoding.*;
import com.tibbo.aggregate.common.util.*;

public class SortedCompositeDataTable extends AbstractUnmodifiableDataTable
{
  private final List<DataTable> sources;
  private final String fieldName;
  private final boolean ascending;
  
  private final IndexTableCache<ImmutablePair<Integer, Integer>> indexTableCache;
  
  /**
   * Constructs a <code>SortedCompositeDataTable</code> with given <code>sources</code> sorted by <code>fieldName</code> in accordance with the <code>ascending</code> parameter.
   * 
   * @param sources
   *          A list of Data Tables, each sorted by <code>fieldName</code> in accordance with the <code>ascending</code> parameter. Each source Data Table must have a <code>fieldName</code> field of
   *          the same Comparable type. NULL vales are not permitted in the <code>fieldName</code> field. If at least one of the source tables is not sorted as described above, the behavior of this
   *          Data Table is not specified.
   * @param fieldName
   *          Name of the field that each source table must be sorted by
   * @param ascending
   *          Defines whether the sort order of each source table is ascending (if true) or descending (if false)
   */
  public SortedCompositeDataTable(List<DataTable> sources, String fieldName, boolean ascending)
  {
    assertValidSources(sources, fieldName);
    
    setFormat(sources.get(0).getFormat().clone().setMaxRecords(TableFormat.DEFAULT_MAX_RECORDS));
    
    this.sources = sources;
    this.fieldName = fieldName;
    this.ascending = ascending;
    
    indexTableCache = new IndexTableCache<>(this);
  }
  
  private void assertValidSources(List<DataTable> sources, String fieldName)
  {
    if (Objects.requireNonNull(sources, "List of source data tables must not be null").size() == 0)
      throw new IllegalArgumentException("There must be at least one source table");
    
    final Iterator<DataTable> itr = sources.iterator();
    
    final TableFormat tableFormat = itr.next().getFormat();
    
    if (!tableFormat.hasField(Objects.requireNonNull(fieldName, "Field name must not be null")))
      throw new IllegalArgumentException("Every source table must have a field named " + fieldName);
    
    while (itr.hasNext())
    {
      if (!tableFormat.equals(itr.next().getFormat()))
        throw new IllegalArgumentException("All source tables must have the same format");
    }
  }
  
  @Override
  void getEncodedRecordsOrTableID(StringBuilder finalSB, ClassicEncodingSettings settings, Boolean isTransferEncode, Integer encodeLevel)
  {
    if (id != null)
      new Element(ELEMENT_ID, String.valueOf(id)).encode(finalSB, settings, isTransferEncode, encodeLevel);
  }
  
  @Override
  public DataRecord getRecord(int number)
  {
    indexTableCache.ensureNotDefinitelyInvalid(number);
    
    final List<ImmutablePair<Integer, Integer>> indexTable = indexTableCache.getIndexTable();
    
    if (number < indexTable.size())
    {
      final ImmutablePair<Integer, Integer> tableNumberAndIndex = indexTable.get(number);
      return sources.get(tableNumberAndIndex.getFirst()).getRecord(tableNumberAndIndex.getSecond());
    }
    
    return indexTableCache.iterateThroughSourceUpToNumber(number, indexTable);
  }
  
  @Override
  public boolean equals(Object obj)
  {
    if (obj == this)
    {
      return true;
    }
    
    if (!(obj instanceof SortedCompositeDataTable))
    {
      return false;
    }
    
    SortedCompositeDataTable other = (SortedCompositeDataTable) obj;
    
    if (!fieldName.equals(other.fieldName))
    {
      return false;
    }
    
    if (ascending != other.ascending)
    {
      return false;
    }
    
    if (!Util.equals(quality, other.quality))
    {
      return false;
    }
    
    if (!new HashSet<>(sources).equals(new HashSet<>(other.sources)))
    {
      return false;
    }
    
    return true;
  }
  
  @Override
  public int hashCode()
  {
    return Objects.hash(new HashSet<>(sources), fieldName, ascending, quality);
  }
  
  @Override
  public String toDefaultString()
  {
    return "Sorted composite data table with sources: " + sources;
  }
  
  @Override
  public String dataAsString(boolean showFieldNames, boolean showHiddenFields, boolean showPasswords)
  {
    throw new UnsupportedOperationException(UNSUPPORTED);
  }
  
  @Override
  public boolean isOneCellTable()
  {
    return indexTableCache.isOneCellTable();
  }
  
  @Override
  public Integer findIndex(DataTableQuery query)
  {
    final Iterator<DataRecord> itr = iterator();
    int i = 0;
    
    while (itr.hasNext())
    {
      boolean meet = true;
      
      final DataRecord rec = itr.next();
      
      for (QueryCondition cond : query.getConditions())
        if (!rec.meetToCondition(cond))
          meet = false;
        
      if (meet)
        return i;
      
      ++i;
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
  
  private final class Iter implements Iterator<DataRecord>
  {
    private final List<ImmutablePair<Integer, Integer>> indexTable = indexTableCache.getIndexTable();
    private final List<Iterator<DataRecord>> sourceIterators = sources.stream().map(DataTable::iterator).collect(Collectors.toList());
    private final TopRecords topRecords = new TopRecords(sourceIterators);
    
    private DataRecord nextRecord;
    private boolean isNextRecordSet;
    private int cursor;
    
    Iter()
    {
      cursor = -1;
    }
    
    Iter(int index)
    {
      if (index > indexTable.size())
      {
        cursor = indexTable.size() - 1;
        
        while (hasNext() && cursor < index)
        {
          next();
        }
        
        if (cursor != index)
          throw new NoSuchElementException("There is no element with index #" + index + " in this composite data table");
      }
      else
      {
        cursor = index - 1;
        
        for (int i = 0; i <= cursor; i++)
          topRecords.pop();
      }
    }
    
    @Override
    public boolean hasNext()
    {
      if (cursor + 1 < indexTable.size())
        return true;
      
      if (isNextRecordSet || setNextRecord())
        return true;
      
      indexTableCache.setSoftIndexTable(indexTable);
      
      setRecordCount(cursor + 1);
      return false;
    }
    
    @Override
    public DataRecord next()
    {
      if (cursor + 1 < indexTable.size())
      {
        final ImmutablePair<Integer, Integer> tableNumberAndIndex = indexTable.get(++cursor);
        topRecords.pop();
        return sources.get(tableNumberAndIndex.getFirst()).getRecord(tableNumberAndIndex.getSecond());
      }
      
      if (!isNextRecordSet && !setNextRecord())
        throw new NoSuchElementException();
      
      isNextRecordSet = false;
      return nextRecord;
    }
    
    private boolean setNextRecord()
    {
      final TopRecord topRecord = topRecords.pop();
      
      if (topRecord == null)
        return false;
      
      nextRecord = topRecord.getRecord();
      isNextRecordSet = true;
      cursor++;
      
      indexTable.add(new ImmutablePair<>(topRecord.getTableNumber(), topRecord.getIndex()));
      
      return true;
    }
    
    private final class TopRecords
    {
      private final List<Iterator<DataRecord>> sourceIterators;
      
      private final int size = sources.size();
      private final DataRecord[] records = new DataRecord[size];
      private final Object[] values = new Object[size];
      private final Integer[] indexes = new Integer[size];
      
      TopRecords(List<Iterator<DataRecord>> sourceIterators)
      {
        this.sourceIterators = sourceIterators;
        
        initializeValuesAndIndexes();
      }
      
      private void initializeValuesAndIndexes()
      {
        int tableNumber = 0;
        for (Iterator<DataRecord> itr : sourceIterators)
        {
          if (!itr.hasNext())
          {
            ++tableNumber;
            continue;
          }
          
          final DataRecord record = itr.next();
          
          final Object value = record.getValue(fieldName);
          if (!(value instanceof Comparable))
            throw new IllegalArgumentException("The type of field named '" + fieldName + "' is not comparable");
          
          indexes[tableNumber] = 0;
          records[tableNumber] = record;
          values[tableNumber++] = value;
        }
      }
      
      TopRecord pop()
      {
        final Integer tableNumber = findExtremum(ascending ? Extremum.MIN : Extremum.MAX);
        
        if (tableNumber == null)
          return null;
        
        final DataRecord record = records[tableNumber];
        final int index = indexes[tableNumber];
        
        final TopRecord result = new TopRecord(tableNumber, index, record);
        
        final Iterator<DataRecord> itr = sourceIterators.get(tableNumber);
        final DataRecord newRecord = itr.hasNext() ? itr.next() : null;
        final Object newValue = newRecord != null ? newRecord.getValue(fieldName) : null;
        final Integer newIndex = newValue != null ? index + 1 : null;
        
        records[tableNumber] = newRecord;
        values[tableNumber] = newValue;
        indexes[tableNumber] = newIndex;
        
        return result;
      }
      
      private Integer findExtremum(Extremum extremum)
      {
        Object extremumValue = null;
        int extremumIndex = 0;
        
        for (int i = 0; i < values.length; i++)
        {
          final Object value = values[i];
          if (value != null)
          {
            extremumValue = value;
            extremumIndex = i;
            break;
          }
        }
        
        if (extremumValue == null)
          return null;
        
        for (int i = extremumIndex + 1; i < values.length; i++)
        {
          final Object value = values[i];
          
          if (value == null)
            continue;
          
          final boolean condition;
          condition = getCondition(extremum, extremumValue, (Comparable) value);
          
          if (condition)
          {
            extremumValue = value;
            extremumIndex = i;
          }
        }
        
        return extremumIndex;
      }
      
      private boolean getCondition(Extremum extremum, Object extremumValue, Comparable value)
      {
        boolean condition;
        switch (extremum)
        {
          case MAX:
            condition = value.compareTo(extremumValue) > 0;
            break;
          
          case MIN:
            condition = value.compareTo(extremumValue) < 0;
            break;
          
          default:
            throw new IllegalArgumentException("Unknown extremum type: " + extremum);
        }
        return condition;
      }
    }
  }
  
  private enum Extremum
  {
    MIN,
    MAX
  }
  
  private static final class TopRecord
  {
    private final int tableNumber;
    private final int index;
    private final DataRecord record;
    
    public TopRecord(int tableNumber, int index, DataRecord record)
    {
      this.tableNumber = tableNumber;
      this.index = index;
      this.record = record;
    }
    
    public int getTableNumber()
    {
      return tableNumber;
    }
    
    public int getIndex()
    {
      return index;
    }
    
    public DataRecord getRecord()
    {
      return record;
    }
  }
}
