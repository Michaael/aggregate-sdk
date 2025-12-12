package com.tibbo.aggregate.common.datatable;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.datatable.encoding.ClassicEncodingSettings;
import com.tibbo.aggregate.common.util.CloneUtils;
import com.tibbo.aggregate.common.util.Element;
import com.tibbo.aggregate.common.util.ElementList;
import com.tibbo.aggregate.common.util.StringUtils;
import com.tibbo.aggregate.common.util.Util;

/**
 * <code>DataTable</code> is a tabular structure that consist of <code>TableFormat</code> describing table fields and other options and a number of <code>DataRecord</code> objects representing
 * individual records.
 */

public class SimpleDataTable extends AbstractDataTable implements Comparable<DataTable>, Cloneable
{
  public static final DataTable EMPTY_TABLE = new SimpleDataTable(TableFormat.EMPTY_FORMAT).makeImmutable();

  private List<DataRecord> records = new ArrayList<>();
  
  /**
   * Constructs a default <code>DataTable</code> with empty <code>TableFormat</code> and zero records.
   */
  public SimpleDataTable()
  {
  }
  
  /**
   * Constructs a <code>DataTable</code> with specified <code>TableFormat</code> and zero records.
   */
  public SimpleDataTable(TableFormat format)
  {
    setFormat(format);
  }
  
  /**
   * Constructs a <code>DataTable</code> with specified <code>TableFormat</code> and adds <code>emptyRecords</code> of empty records to it.
   */
  public SimpleDataTable(TableFormat format, int emptyRecords)
  {
    this(format);
    
    for (int i = 0; i < emptyRecords; i++)
    {
      addRecord();
    }
  }
  
  /**
   * Constructs a <code>DataTable</code> with specified <code>TableFormat</code> and adds empty records to it.
   * 
   * If <code>createEmptyRecords</code> parameter is true, a minimal number of empty records specified by <code>TableFormat</code> is added to the table.
   */
  public SimpleDataTable(TableFormat format, boolean createEmptyRecords)
  {
    this(format, createEmptyRecords ? format != null ? format.getMinRecords() : 0 : 0);
  }
  
  /**
   * Constructs a <code>DataTable</code> with one record specified by <code>record</code> parameter.
   * 
   * <code>TableFormat</code> of resulting table is taken from format of <code>record</code>.
   */
  public SimpleDataTable(DataRecord record)
  {
    this();
    addRecord(record);
  }
  
  public SimpleDataTable(TableFormat format, String dataString, ClassicEncodingSettings settings) throws DataTableException
  {
    if (dataString == null)
    {
      throw new NullPointerException("Data string is null");
    }
    
    setFormat(format);
    
    List<String> fieldNames = null;
    
    ElementList recs = StringUtils.elements(dataString, false);
    
    for (Element el : recs)
    {
      if (ELEMENT_FIELD_NAME.equals(el.getName()))
      {
        if (fieldNames == null)
        {
          fieldNames = new LinkedList<>();
        }
        fieldNames.add(el.getValue());
      }
      else if (ELEMENT_RECORD.equals(el.getName()))
      {
        addRecord(new DataRecord(getFormat(), el.getValue(), settings, true, fieldNames));
      }
      else
      {
        decodeAdvancedElement(el);
      }
    }
  }
  
  /**
   * Constructs a <code>DataTable</code> with specified <code>TableFormat</code>, adds one record and fills it with data.
   * 
   * Data to be put into the first record is specified by <code>firstRowData</code> parameter.
   */
  public SimpleDataTable(TableFormat format, Object... firstRowData)
  {
    this(format);
    if (firstRowData.length > 0)
    {
      addRecord(new DataRecord(format, firstRowData));
    }
  }
  
  /**
   * Decodes <code>DataTable</code> from string.
   */
  public SimpleDataTable(String data) throws DataTableException
  {
    this(data, true);
  }
  
  /**
   * Decodes <code>DataTable</code> from string and checks its validity.
   */
  public SimpleDataTable(String data, boolean validate) throws DataTableException
  {
    this(data, new ClassicEncodingSettings(false), validate);
  }
  
  public SimpleDataTable(String data, ClassicEncodingSettings settings, boolean validate) throws DataTableException
  {
    this((data != null ? StringUtils.elements(data, settings != null && settings.isUseVisibleSeparators()) : null), settings, validate);
  }
  
  public SimpleDataTable(ElementList elements, ClassicEncodingSettings settings, boolean validate) throws DataTableException
  {
    accomplishConstruction(elements, settings, validate);
  }
  
  /**
   * Returns number of records in the table.
   */
  @Override
  public Integer getRecordCount()
  {
    return records.size();
  }
  
  private void checkOrSetFormat(DataRecord record)
  {
    if (format.getFieldCount() != 0)
    {
      if (format != record.getFormat())
      {
        String message = record.getFormat().extendMessage(format);
        if (message != null)
        {
          throw new IllegalArgumentException("Format of new record ('" + record.getFormat() + "') differs from format of data table ('" + getFormat() + "'): " + message);
        }
      }
    }
    else
    {
      ensureMutable();
      
      format = record.getFormat();
    }
  }
  
  /**
   * Sets table ID.
   */
  @Override
  public void setId(Long id)
  {
    ensureMutable();
    
    this.id = id;
  }
  
  /**
   * Adds new record to the table.
   */
  @Override
  public DataTable addRecord(DataRecord record)
  {
    checkOrSetFormat(record);
    addRecordImpl(null, record);
    return this;
  }
  
  /**
   * Adds new record to the table.
   */
  @Override
  public DataRecord addRecord(Object... fieldValues)
  {
    DataRecord rec = addRecord();
    for (Object value : fieldValues)
    {
      rec.addValue(value);
    }
    return rec;
  }
  
  /**
   * Adds new record to the table at the specified index.
   */
  @Override
  public DataTable addRecord(int index, DataRecord record)
  {
    checkOrSetFormat(record);
    addRecordImpl(index, record);
    return this;
  }
  
  /**
   * Adds new record to the table.
   */
  @Override
  public DataRecord addRecord()
  {
    if (getFormat() == null)
    {
      throw new IllegalStateException("Can't add empty record because format of data table was not set");
    }
    DataRecord record = new DataRecord(getFormat());
    addRecordImpl(null, record);
    return record;
  }
  
  private void addRecordImpl(Integer index, DataRecord record)
  {
    ensureMutable();
    
    if (getRecordCount() >= format.getMaxRecords())
    {
      throw new IllegalStateException(Cres.get().getString("dtCannotAddRecord") + "maximum number of records is reached: " + format.getMaxRecords());
    }
    
    try
    {
      validateRecord(record);
    }
    catch (ValidationException ex)
    {
      throw new IllegalStateException(ex.getMessage(), ex);
    }
    
    if (index != null)
    {
      records.add(index, record);
    }
    else
    {
      records.add(record);
    }
    
    record.setTable(this);
  }
  
  /**
   * Replaces record at the specified index.
   */
  @Override
  public DataTable setRecord(int index, DataRecord record)
  {
    ensureMutable();
    
    checkOrSetFormat(record);
    records.get(index).setTable(null);
    records.set(index, record);
    record.setTable(this);
    return this;
  }
  
  /**
   * Swaps two records.
   *
   * Both records must belong to this table, otherwise method will throw an <code>IllegalStateException</code>
   */
  @Override
  public void swapRecords(int index1, int index2)
  {
    ensureMutable();
    
    DataRecord r1 = records.get(index1);
    DataRecord r2 = records.get(index2);
    
    records.set(index1, r2);
    records.set(index2, r1);
  }
  
  /**
   * Returns list of table records.
   */
  @Override
  public List<DataRecord> getRecords()
  {
    return Collections.unmodifiableList(records);
  }
  
  /**
   * Returns record with specified index.
   */
  @Override
  public DataRecord getRecord(int number)
  {
    return records.get(number);
  }
  
  @Override
  protected DataRecord removeRecordImpl(int index)
  {
    ensureMutable();
    
    if (getRecordCount() <= format.getMinRecords())
    {
      throw new IllegalStateException("Cannot remove record: minimum number of records is reached: " + format.getMinRecords());
    }
    
    return records.remove(index);
  }
  
  @Override
  public void removeRecordsByIds(Collection<String> ids)
  {
    ensureMutable();
    records = records.stream().filter(it -> !ids.contains(it.getId())).collect(Collectors.toList());
  }
  
  /**
   * Removes all records equal to the rec parameter from the table.
   */
  @Override
  public void removeRecords(DataRecord rec)
  {
    for (int i = records.size() - 1; i >= 0; i--)
    {
      if (Util.equals(rec, records.get(i)))
      {
        removeRecordImpl(i);
      }
    }
  }
  
  /**
   * Moves specified record to position specified by <code>index</code> argument.
   * 
   * <code>record<code> must belong to this table, otherwise method will throw an <code>IllegalStateException</code>
   */
  @Override
  public void reorderRecord(DataRecord record, int index)
  {
    ensureMutable();
    
    int oi = records.indexOf(record);
    
    if (oi == -1)
    {
      throw new IllegalStateException("Record is not from this table");
    }
    
    if (records.remove(record))
    {
      records.add(index - (oi < index ? 1 : 0), record);
    }
  }
  
  @Override
  public boolean equals(Object obj)
  {
    if (obj == null)
    {
      return false;
    }
    
    if (!(obj instanceof SimpleDataTable))
    {
      return false;
    }
    
    SimpleDataTable other = (SimpleDataTable) obj;
    
    if (!format.equals(other.getFormat()))
    {
      return false;
    }
    
    if (!getRecordCount().equals(other.getRecordCount()))
    {
      return false;
    }
    
    if (!Util.equals(quality, other.quality))
    {
      return false;
    }
    
    for (int i = 0; i < getRecordCount(); i++)
    {
      if (!getRecord(i).equals(other.getRecord(i)))
      {
        return false;
      }
    }
    
    return true;
  }
  
  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((format == null) ? 0 : format.hashCode());
    result = prime * result + ((records == null) ? 0 : records.hashCode());
    result = prime * result + ((quality == null) ? 0 : quality.hashCode());
    return result;
  }
  
  @Override
  void getEncodedRecordsOrTableID(StringBuilder finalSB, ClassicEncodingSettings settings, Boolean isTransferEncode, Integer encodeLevel)
  {
    for (int i = 0; i < getRecordCount(); i++)
    {
      new Element(ELEMENT_RECORD, getRecord(i)).encode(finalSB, settings, isTransferEncode, encodeLevel);
    }
  }
  
  @Override
  int getEstimateDataSize()
  {
    return getFieldCount() * getRecordCount() * 3 + getFieldCount() * 7;
  }
  
  @Override
  public String toDefaultString()
  {
    if (getRecordCount() == 1)
    {
      return dataAsString();
    }
    else
    {
      return MessageFormat.format(Cres.get().getString("dtTable"), getRecordCount());
    }
  }
  
  @Override
  public String dataAsString(boolean showFieldNames, boolean showHiddenFields, boolean showPasswords)
  {
    StringBuffer res = new StringBuffer();
    
    String recordSeparator = getFieldCount() > 1 ? " | " : ", ";
    
    for (int i = 0; i < getRecordCount(); i++)
    {
      if (i > 0)
      {
        res.append(recordSeparator);
      }
      
      DataRecord rec = getRecord(i);
      
      res.append(rec.dataAsString(showFieldNames, showHiddenFields, showPasswords));
    }
    
    return res.toString();
  }
  
  /**
   * Returns true if table has exactly one record and one field.
   */
  @Override
  public boolean isOneCellTable()
  {
    return getFieldCount() == 1 && getRecordCount() == 1;
  }
  
  @Override
  public String conformMessage(TableFormat rf)
  {
    if (getRecordCount() < rf.getMinRecords())
    {
      return "Number of records too small: need " + rf.getMinRecords() + " or more, found " + getRecordCount();
    }
    
    if (getRecordCount() > rf.getMaxRecords())
    {
      return "Number of records too big: need " + rf.getMaxRecords() + " or less, found " + getRecordCount();
    }
    
    return getFormat().extendMessage(rf);
  }
  
  @Override
  public Integer findIndex(DataTableQuery query)
  {
    for (int i = 0; i < this.getRecordCount(); i++)
    {
      boolean meet = true;
      
      DataRecord rec = this.getRecord(i);
      
      for (QueryCondition cond : query.getConditions())
      {
        if (!rec.meetToCondition(cond))
        {
          meet = false;
        }
      }
      
      if (meet)
      {
        return i;
      }
    }
    
    return null;
  }
  
  @Override
  public void sort(final DataTableSorter sorter)
  {
    ensureMutable();
    
    Collections.sort(records, new Comparator<DataRecord>()
    {
      @Override
      public int compare(DataRecord r1, DataRecord r2)
      {
        for (SortOrder order : sorter)
        {
          Object v1 = r1.getValue(order.getField());
          Object v2 = r2.getValue(order.getField());
          
          if (v1 == null && v2 != null)
          {
            return order.isAscending() ? Integer.MIN_VALUE : Integer.MAX_VALUE;
          }
          
          if (v2 == null && v1 != null)
          {
            return order.isAscending() ? Integer.MAX_VALUE : Integer.MIN_VALUE;
          }
          
          if (v1 instanceof Comparable && v2 instanceof Comparable)
          {
            int res = ((Comparable) v1).compareTo(v2);
            if (res != 0)
            {
              return order.isAscending() ? res : -res;
            }
          }
        }
        
        return 0;
      }
    });
  }
  
  @Override
  public void sort(Comparator<DataRecord> comparator)
  {
    ensureMutable();
    
    Collections.sort(records, comparator);
  }
  
  @Override
  public Object get()
  {
    return getRecord(0).getValue(0);
  }
  
  // This method should be called only by Data Table Editors! It creates a mutable copy of format in every record.
  @Override
  public void splitFormat()
  {
    for (DataRecord rec : records)
    {
      rec.cloneFormatFromTable();
    }
  }
  
  @Override
  public void joinFormats()
  {
    for (DataRecord rec : records)
    {
      rec.setFormat(this.getFormat());
    }
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
  
  @Override
  public DataTable clone()
  {
    SimpleDataTable cl = (SimpleDataTable) super.clone();
    
    cl.records = (List) CloneUtils.deepClone(records);
    
    for (DataRecord rec : cl.records)
    {
      rec.setTable(cl);
    }
    
    cl.namingEvaluator = null;
    
    cl.immutable = false;
    
    return cl;
  }
  
  @Override
  public int compareTo(DataTable other)
  {
    return dataAsString().compareTo(other.dataAsString());
  }
  
  @Override
  public DataTable makeImmutable()
  {
    if (immutable)
    {
      return this;
    }
    
    immutable = true;
    
    format.makeImmutable(this);
    
    makeSubtablesImmutable();
    
    return this;
  }
  
  private void makeSubtablesImmutable()
  {
    if (getRecordCount() == 0)
      return;
    
    List<Integer> dataTableFields = new ArrayList<>();
    
    for (int i = 0; i < format.getFieldCount(); i++)
    {
      final FieldFormat field = format.getField(i);
      
      if (field.getType() == FieldFormat.DATATABLE_FIELD)
        dataTableFields.add(i);
    }
    
    if (dataTableFields.isEmpty())
      return;
    
    for (DataRecord record : records)
    {
      for (Integer index : dataTableFields)
      {
        final DataTable dataTable = record.getDataTable(index);
        if (dataTable != null)
          dataTable.makeImmutable();
      }
    }
  }
  
  @Override
  public boolean isSimple()
  {
    return true;
  }
  
  @Override
  public Stream<DataRecord> stream()
  {
    return records.stream();
  }
  
  private class Iter implements Iterator<DataRecord>
  {
    private final Iterator<DataRecord> recsIter;
    private DataRecord rec;
    
    Iter()
    {
      recsIter = records.iterator();
    }
    
    Iter(int index)
    {
      recsIter = records.listIterator(index);
    }
    
    @Override
    public boolean hasNext()
    {
      return recsIter.hasNext();
    }
    
    @Override
    public DataRecord next()
    {
      rec = recsIter.next();
      return rec;
    }
    
    @Override
    public void remove()
    {
      ensureMutable();
      
      recsIter.remove();
      
      if (rec != null)
      {
        rec.setTable(null);
      }
    }
  }
}
