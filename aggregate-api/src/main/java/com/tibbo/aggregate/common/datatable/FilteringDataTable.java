package com.tibbo.aggregate.common.datatable;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

import com.tibbo.aggregate.common.datatable.encoding.ClassicEncodingSettings;
import com.tibbo.aggregate.common.expression.DefaultReferenceResolver;
import com.tibbo.aggregate.common.expression.EvaluationEnvironment;
import com.tibbo.aggregate.common.expression.Evaluator;
import com.tibbo.aggregate.common.expression.Expression;
import com.tibbo.aggregate.common.expression.ReferenceResolver;
import com.tibbo.aggregate.common.util.Element;
import com.tibbo.aggregate.common.util.Util;

public class FilteringDataTable extends AbstractUnmodifiableDataTable
{
  private final DataTable source;
  private final Expression filterExpression;
  private final Evaluator localEvaluator;
  private final EvaluationEnvironment localEnvironment;

  private final IndexTableCache<Integer> indexTableCache;
  
  /* (non-Javadoc)
   * Note that <code>source</code> is made immutable when a <code>FilteringDataTable</code> is constructed.
   */
  public FilteringDataTable(DataTable source, Expression filterExpression, Evaluator evaluator,
      EvaluationEnvironment environment)
  {
    setFormat(Objects.requireNonNull(source, "Source data table must not be null").getFormat().clone()
        .setMinRecords(TableFormat.DEFAULT_MIN_RECORDS));
    
    source.makeImmutable();
    this.source = source;
    
    this.filterExpression = Objects.requireNonNull(filterExpression, "Filter expression must not be null");
    
    makeImmutable();
    
    if (evaluator == null)
    {
      final ReferenceResolver defaultResolver = new DefaultReferenceResolver();
      defaultResolver.setDefaultTable(source);
      localEvaluator = new Evaluator(defaultResolver);
    }
    else
    {
      final ReferenceResolver resolver = evaluator.getDefaultResolver();
      localEvaluator = new Evaluator(resolver.getContextManager(), resolver.getDefaultContext(), resolver.getDefaultTable(), resolver.getCallerController());
      localEvaluator.getDefaultResolver().setDefaultTable(source);
    }

    indexTableCache = new IndexTableCache<>(this);

    localEnvironment = environment != null ? environment.clone() : null;
  }
  
  public FilteringDataTable(DataTable source, Expression filterExpression)
  {
    this(source, filterExpression, null, null);
  }
  
  public FilteringDataTable(DataTable source, String filterExpression)
  {
    this(source, new Expression(filterExpression));
  }
  
  public DataTable getSource()
  {
    return source;
  }
  
  public Expression getFilterExpression()
  {
    return filterExpression;
  }
  
  @Override
  public DataRecord getRecord(int number)
  {
    indexTableCache.ensureNotDefinitelyInvalid(number);
    
    final List<Integer> indexes = indexTableCache.getIndexTable();
    
    if (number < indexes.size())
      return source.getRecord(indexes.get(number));
    
    return indexTableCache.iterateThroughSourceUpToNumber(number, indexes);
  }
  
  @Override
  public boolean equals(Object obj)
  {
    if (obj == this)
    {
      return true;
    }
    
    if (!(obj instanceof FilteringDataTable))
    {
      return false;
    }
    
    FilteringDataTable other = (FilteringDataTable) obj;
    
    if (!filterExpression.equals(other.filterExpression))
    {
      return false;
    }
    
    if (!Util.equals(quality, other.quality))
    {
      return false;
    }
    
    if (!source.equals(other.source))
    {
      return false;
    }
    
    return true;
  }
  
  @Override
  public int hashCode()
  {
    return Objects.hash(source, filterExpression, quality);
  }
  
  @Override
  void getEncodedRecordsOrTableID(StringBuilder finalSB, ClassicEncodingSettings settings, Boolean isTransferEncode, Integer encodeLevel)
  {
    if (id != null)
      new Element(ELEMENT_ID, String.valueOf(id)).encode(finalSB, settings, isTransferEncode, encodeLevel);
  }
  
  @Override
  public String toDefaultString()
  {
    return "Filtering data table with source: " + source + " filtered using filter: " + filterExpression;
  }
  
  @Override
  public String dataAsString(boolean showFieldNames, boolean showHiddenFields, boolean showPasswords)
  {
    throw new UnsupportedOperationException(UNSUPPORTED);
  }

  @Override
  public boolean isOneCellTable() {
    return indexTableCache.isOneCellTable();
  }

  @Override
  public Integer findIndex(DataTableQuery query)
  {
    final Integer foundIndexInSource = source.findIndex(query);
    
    if (foundIndexInSource == null || !passing(foundIndexInSource))
      return null;
    
    final List<Integer> indexes = indexTableCache.getIndexTable();
    int foundIndexInThisDataTable = indexes.indexOf(foundIndexInSource);
    
    if (foundIndexInThisDataTable != -1)
      return foundIndexInThisDataTable;
    
    if (indexTableCache.isIndexTableComplete(indexes, getRecordCount()))
      return null;
    
    final DataRecord recordToFind = source.getRecord(foundIndexInSource);
    int currentMaxIndexInThisDataTable = indexes.size() - 1;
    final Iterator<DataRecord> iter = currentMaxIndexInThisDataTable != -1 ? iterator(currentMaxIndexInThisDataTable + 1) : iterator();
    while (iter.hasNext())
    {
      final DataRecord record = iter.next();
      currentMaxIndexInThisDataTable++;
      if (record.equals(recordToFind))
        return currentMaxIndexInThisDataTable;
    }
    throw new RuntimeException("This should not have happened");
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

  private boolean passing(int recordIndex)
  {
    localEvaluator.getDefaultResolver().setDefaultRow(recordIndex);
    
    try
    {
      return localEvaluator.evaluateToBoolean(filterExpression, localEnvironment);
    }
    catch (Exception exc)
    {
      throw new RuntimeException(exc);
    }
  }
  
  private final class Iter implements Iterator<DataRecord>
  {
    private final List<Integer> indexes = indexTableCache.getIndexTable();
    private int nextIndexInSource = indexes.size() != 0 ? indexes.get(indexes.size() - 1) + 1 : 0;
    private final Iterator<DataRecord> recsIter = indexes.size() != 0 ? source.iterator(nextIndexInSource) : source.iterator();
    private DataRecord nextRecord;
    private boolean isNextRecordSet;
    private int cursor;
    
    Iter()
    {
      cursor = -1;
    }
    
    Iter(int index)
    {
      if (index > indexes.size())
      {
        cursor = indexes.size() - 1;
        
        while (hasNext() && cursor < index)
        {
          next();
        }
        
        if (cursor != index)
          throw new NoSuchElementException("There is no element with index #" + index + " in this filtering data table");
        
      }
      else
      {
        cursor = index - 1;
      }
    }
    
    @Override
    public boolean hasNext()
    {
      if (cursor + 1 < indexes.size())
        return true;
      
      if (isNextRecordSet || setNextRecord())
        return true;
      
      indexTableCache.setSoftIndexTable(indexes);
      setRecordCount(cursor + 1);
      return false;
    }
    
    @Override
    public DataRecord next()
    {
      if (cursor + 1 < indexes.size())
        return source.getRecord(indexes.get(++cursor));
      
      if (!isNextRecordSet && !setNextRecord())
        throw new NoSuchElementException();
      
      isNextRecordSet = false;
      return nextRecord;
    }
    
    private boolean setNextRecord()
    {
      while (recsIter.hasNext())
      {
        final DataRecord nextRecordInSource = recsIter.next();
        if (passing(nextIndexInSource))
        {
          nextRecord = nextRecordInSource;
          isNextRecordSet = true;
          indexes.add(nextIndexInSource++);
          cursor++;
          return true;
        }
        else
        {
          nextIndexInSource++;
        }
      }
      return false;
    }
  }
}
