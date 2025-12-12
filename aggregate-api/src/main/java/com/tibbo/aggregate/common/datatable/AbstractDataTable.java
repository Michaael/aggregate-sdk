package com.tibbo.aggregate.common.datatable;

import static com.google.common.collect.Lists.newArrayList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.tibbo.aggregate.common.Log;
import com.tibbo.aggregate.common.context.CallerController;
import com.tibbo.aggregate.common.context.Context;
import com.tibbo.aggregate.common.context.ContextManager;
import com.tibbo.aggregate.common.datatable.encoding.ClassicEncodingSettings;
import com.tibbo.aggregate.common.datatable.encoding.FormatCache;
import com.tibbo.aggregate.common.datatable.encoding.KnownFormatCollector;
import com.tibbo.aggregate.common.datatable.field.DataTableFieldFormat;
import com.tibbo.aggregate.common.datatable.field.DateFieldFormat;
import com.tibbo.aggregate.common.datatable.validator.FieldValidator;
import com.tibbo.aggregate.common.datatable.validator.RecordValidator;
import com.tibbo.aggregate.common.datatable.validator.TableValidator;
import com.tibbo.aggregate.common.expression.AbstractReferenceResolver;
import com.tibbo.aggregate.common.expression.DefaultReferenceResolver;
import com.tibbo.aggregate.common.expression.EvaluationEnvironment;
import com.tibbo.aggregate.common.expression.Evaluator;
import com.tibbo.aggregate.common.expression.Expression;
import com.tibbo.aggregate.common.expression.Reference;
import com.tibbo.aggregate.common.util.Element;
import com.tibbo.aggregate.common.util.ElementList;
import com.tibbo.aggregate.common.util.PublicCloneable;
import com.tibbo.aggregate.common.util.Util;

public abstract class AbstractDataTable implements DataTable, Cloneable, PublicCloneable
{
  public static final String ELEMENT_ID = "C";
  public static final TableFormat DEFAULT_FORMAT = new TableFormat();
  static final String ELEMENT_RECORD = "R";
  static final String ELEMENT_FIELD_NAME = "N";
  static final String UNSUPPORTED = "This operation is not supported";
  private static final String ELEMENT_FORMAT = "F";
  private static final String ELEMENT_FORMAT_ID = "D";
  private static final String ELEMENT_TIMESTAMP = "T";
  private static final String ELEMENT_QUALITY = "Q";
  private static final String ELEMENT_INVALIDATOR = "I";
  private static final int DEFAULT_ESTIMATE_RECORD_COUNT = 100;
  
  static
  {
    DEFAULT_FORMAT.makeImmutable(null);
  }
  
  TableFormat format = DEFAULT_FORMAT;
  
  Long id = null;
  Integer quality;
  transient Evaluator namingEvaluator;
  transient boolean immutable;
  private Date timestamp;
  private String invalidationMessage;
  
  void accomplishConstruction(ElementList elements, ClassicEncodingSettings settings, boolean validate) throws DataTableException
  {
    if (elements == null)
      return;
    
    boolean found = false;
    String encodedFormat = null;
    List<String> fieldNames = null;
    
    for (Element el : elements)
    {
      if (el.getName() != null)
      {
        if (el.getName().equals(ELEMENT_FORMAT_ID))
        {
          int formatId = Integer.valueOf(el.getValue());
          
          if (settings.getFormatCache() == null)
          {
            throw new IllegalStateException("Can't use format ID - format cache not found");
          }
          
          if (encodedFormat != null) // If format was already found in the encoded data
          {
            TableFormat format = new TableFormat(encodedFormat, settings, validate);
            settings.getFormatCache().put(formatId, format);
            continue;
          }
          
          TableFormat format = settings.getFormatCache().get(formatId);
          
          if (format == null)
          {
            throw new IllegalStateException("Format with specified ID not found in the cache: " + formatId);
          }
          
          setFormat(format);
          
          found = true;
          
          continue;
        }
        else if (el.getName().equals(ELEMENT_FORMAT))
        {
          encodedFormat = el.getValue();
          setFormat(new TableFormat(encodedFormat, settings, validate));
          found = true;
          continue;
        }
        else if (el.getName().equals(ELEMENT_RECORD))
        {
          // Using table's format if encodedFormat is not NULL (i.e. was found in the encoded data)
          TableFormat format = found ? getFormat() : (settings != null ? settings.getFormat() : null);
          
          if (format == null)
          {
            throw new IllegalStateException("Table format is neither found in encoded table nor provided by decoding environment");
          }
          
          addRecord(new DataRecord(format, el.getValue(), settings, validate, fieldNames));
          continue;
        }
        else if (el.getName().equals(ELEMENT_FIELD_NAME))
        {
          if (fieldNames == null)
          {
            fieldNames = new ArrayList<>();
          }
          fieldNames.add(el.getValue());
        }
        else if (el.getName().equals(ELEMENT_ID))
        {
          id = Long.valueOf(el.getValue());
        }
        else
        {
          decodeAdvancedElement(el);
        }
      }
    }
  }
  
  void decodeAdvancedElement(Element el)
  {
    if (el.getName().equals(ELEMENT_TIMESTAMP))
    {
      if (el.getValue().length() > 4 && el.getValue().charAt(4) == '-')
      {
        timestamp = DateFieldFormat.dateFromString(el.getValue());
      }
      else
      {
        // TODO: For timestamps encoded using wrong slow code, remove in v6
        Util.convertToDate(el.getValue(), false, false);
      }
    }
    else if (el.getName().equals(ELEMENT_QUALITY))
    {
      quality = Util.convertToNumber(el.getValue(), false, false).intValue();
    }
    else if (el.getName().equals(ELEMENT_INVALIDATOR))
    {
      invalidationMessage = el.getValue();
    }
  }
  
  /**
   * Returns number of fields in the table.
   */
  @Override
  public int getFieldCount()
  {
    return format.getFieldCount();
  }
  
  /**
   * Returns format of the table.
   */
  @Override
  public TableFormat getFormat()
  {
    return format;
  }
  
  /**
   * Returns format of field with specified index.
   */
  @Override
  public FieldFormat getFormat(int field)
  {
    return getFormat().getField(field);
  }
  
  /**
   * Returns format of field with specified name.
   */
  @Override
  public FieldFormat getFormat(String name)
  {
    return getFormat().getField(name);
  }
  
  /**
   * Sets new format for the table.
   *
   * Note, that resulting table is not checked for validity. Format of existing records may be incompatible with new format of table.
   */
  @Override
  public DataTable setFormat(TableFormat format)
  {
    if (format != null)
    {
      ensureMutable();
      
      format.makeImmutable(this);
      
      this.format = format;
    }
    return this;
  }
  
  @Override
  public DataTable addRecords(List<DataRecord> records)
  {
    records.forEach(this::addRecord);
    return this;
  }
  
  @Override
  public boolean applyCachedFormat(Optional<FormatCache> formatCacheOpt)
  {
    if (format == null)
    {
      return false;
    }
    
    if (formatCacheOpt.isPresent())
    {
      applyCachedFormatRecursively(this, this::getFormat, this::setFormat, formatCacheOpt.get());
      return true;
    }
    return false;
  }
  
  private void applyCachedFormatRecursively(DataTable source, Supplier<TableFormat> formatGetter, Consumer<TableFormat> formatSetter, FormatCache formatCache)
  {
    TableFormat tableFormat = formatGetter.get();
    
    tableFormat.applyCachedFormat(formatCache, formatSetter);
    
    List<String> fieldsToBeCached = newArrayList();
    
    for (FieldFormat fieldFormat : tableFormat.getFields())
    {
      if (fieldFormat.getType() != FieldFormat.DATATABLE_FIELD)
      {
        continue;
      }
      DataTableFieldFormat dataTableFieldFormat = (DataTableFieldFormat) fieldFormat;
      if (dataTableFieldFormat.getDefaultValue() == null)
      {
        continue;
      }
      if (DEFAULT_FORMAT != dataTableFieldFormat.getDefaultValue().getFormat())
      {
        fieldsToBeCached.add(fieldFormat.getName());
      }
    }
    
    for (DataRecord dataRecord : source)
    {
      for (String tableField : fieldsToBeCached)
      {
        if (!dataRecord.hasField(tableField))
        {
          continue;
        }
        DataTable innerTable = dataRecord.getDataTable(tableField);
        if (innerTable != null)
        {
          applyCachedFormatRecursively(innerTable, innerTable::getFormat, innerTable::setFormat, formatCache);
        }
      }
    }
  }
  
  /**
   * Returns table ID.
   */
  @Override
  public Long getId()
  {
    return id;
  }
  
  @Override
  public boolean hasField(String field)
  {
    return format.hasField(field);
  }
  
  @Override
  public void validate(Context context, ContextManager contextManager, CallerController caller) throws DataTableException
  {
    if (isInvalid())
    {
      throw new ValidationException(invalidationMessage);
    }
    for (TableValidator tv : getFormat().getTableValidators())
    {
      tv.validate(this);
    }
    for (DataRecord rec : this)
    {
      for (RecordValidator rv : getFormat().getRecordValidators())
      {
        rv.validate(this, rec);
      }
      for (FieldFormat ff : getFormat())
      {
        List<FieldValidator> fvs = ff.getValidators();
        for (FieldValidator fv : fvs)
        {
          try
          {
            fv.validate(context, contextManager, caller, rec.getValue(ff.getName()));
          }
          catch (ValidationException ex)
          {
            throw new ValidationException("Error validating value of field '" + ff + "': " + ex.getMessage(), ex);
          }
        }
      }
    }
    for (FieldFormat ff : getFormat())
    {
      if (ff.getType() == FieldFormat.DATATABLE_FIELD)
      {
        for (DataRecord rec : this)
        {
          DataTable nested = rec.getDataTable(ff.getName());
          if (nested != null)
          {
            nested.validate(context, contextManager, caller);
          }
        }
      }
    }
  }
  
  @Override
  public void validateRecord(DataRecord record) throws ValidationException
  {
    for (RecordValidator rv : getFormat().getRecordValidators())
    {
      rv.validate(this, record);
    }
  }
  
  void ensureMutable()
  {
    if (immutable)
    {
      if (Log.DATATABLE.isDebugEnabled())
      {
        Log.DATATABLE.warn("Attempt to change immutable table", new IllegalStateException());
      }
      else
      {
        Log.DATATABLE.warn("Attempt to change immutable table");
      }
      throw new IllegalStateException("Immutable");
    }
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
    
    DataRecord r1 = getRecord(index1);
    DataRecord r2 = getRecord(index2);
    
    setRecord(index1, r2);
    setRecord(index2, r1);
  }
  
  /**
   * Returns list of table records.
   */
  @Override
  public List<DataRecord> getRecords()
  {
    throw new UnsupportedOperationException(UNSUPPORTED);
  }
  
  @Override
  public boolean isInvalid()
  {
    return invalidationMessage != null;
  }
  
  @Override
  public String getInvalidationMessage()
  {
    return invalidationMessage;
  }
  
  @Override
  public void setInvalidationMessage(String invalidationMessage)
  {
    ensureMutable();
    
    this.invalidationMessage = invalidationMessage;
  }
  
  @Override
  public Date getTimestamp()
  {
    return timestamp;
  }
  
  @Override
  public void setTimestamp(Date timestamp)
  {
    ensureMutable();
    
    this.timestamp = timestamp;
  }
  
  @Override
  public Integer getQuality()
  {
    return quality;
  }
  
  @Override
  public void setQuality(Integer quality)
  {
    ensureMutable();
    
    this.quality = quality;
  }
  
  @Override
  public Object getValue()
  {
    return this;
  }
  
  @Override
  public DataRecord getRecordById(String id)
  {
    if (id == null)
    {
      return null;
    }
    
    for (DataRecord rec : this)
    {
      if (rec.getId() != null && rec.getId().equals(id))
      {
        return rec;
      }
    }
    
    return null;
  }
  
  @Override
  public void removeRecordsByIds(Collection<String> ids)
  {
    throw new UnsupportedOperationException(UNSUPPORTED);
  }
  
  /**
   * Removes record with specified index from the table.
   */
  @Override
  public DataRecord removeRecord(int index)
  {
    return removeRecordImpl(index);
  }
  
  protected abstract DataRecord removeRecordImpl(int index);
  
  @Override
  public String getEncodedData(ClassicEncodingSettings settings)
  {
    return getEncodedData(new StringBuilder(), settings, false, 1).toString();
  }
  
  @Override
  public StringBuilder getEncodedData(StringBuilder finalSB, ClassicEncodingSettings settings, Boolean isTransferEncode, Integer encodeLevel)
  {
    boolean encodeFieldNames = settings == null || settings.isEncodeFieldNames();
    
    if (encodeFieldNames)
    {
      for (int i = 0; i < format.getFieldCount(); i++)
      {
        new Element(ELEMENT_FIELD_NAME, format.getField(i).getName()).encode(finalSB, settings, isTransferEncode, encodeLevel);
      }
    }
    
    getEncodedRecordsOrTableID(finalSB, settings, isTransferEncode, encodeLevel);
    
    if (quality != null)
    {
      new Element(ELEMENT_QUALITY, String.valueOf(quality)).encode(finalSB, settings, isTransferEncode, encodeLevel);
    }
    
    if (timestamp != null)
    {
      new Element(ELEMENT_TIMESTAMP, DateFieldFormat.dateToString(timestamp)).encode(finalSB, settings, isTransferEncode, encodeLevel);
    }
    
    return finalSB;
  }
  
  abstract void getEncodedRecordsOrTableID(StringBuilder finalSB, ClassicEncodingSettings settings, Boolean isTransferEncode, Integer encodeLevel);
  
  @Override
  public String encode()
  {
    return encode(new ClassicEncodingSettings(false));
  }
  
  @Override
  public String encode(boolean useVisibleSeparators)
  {
    return encode(new ClassicEncodingSettings(useVisibleSeparators));
  }
  
  @Override
  public String encode(ClassicEncodingSettings settings)
  {
    return encode(new StringBuilder(getEstimateDataSize()), settings, false, 0).toString();
  }
  
  @Override
  public StringBuilder encode(StringBuilder finalSB, ClassicEncodingSettings settings, Boolean isTransferEncode, Integer encodeLevel)
  {
    if (finalSB.length() + getEstimateDataSize() > finalSB.capacity())
    {
      finalSB.ensureCapacity(finalSB.capacity() + getEstimateDataSize());
    }
    
    Integer formatId = null;
    
    boolean formatWasInserted = false;
    
    boolean needToInsertFormat = settings != null && settings.isEncodeFormat();
    
    boolean isKnown = false;
    
    KnownFormatCollector collector = settings != null ? settings.getKnownFormatCollector() : null;
    
    if (needToInsertFormat)
    {
      if (getFormat().getFieldCount() > 0 && settings.getFormatCache() != null)
      {
        formatId = settings.getFormatCache().addIfNotExists(getFormat());
        
        if (collector != null)
        {
          needToInsertFormat = false;
          
          if (collector.isKnown(formatId) && collector.isMarked(formatId))
          {
            // Format is known - inserting ID only
            new Element(ELEMENT_FORMAT_ID, formatId.toString()).encode(finalSB, settings, isTransferEncode, encodeLevel);
            
            isKnown = true;
          }
          else
          {
            boolean oldEncodeFormat = settings.isEncodeFormat();
            try
            {
              settings.setEncodeFormat(true);
              
              // Format is not known - inserting both format and ID
              new Element(ELEMENT_FORMAT, getFormat()).encode(finalSB, settings, isTransferEncode, encodeLevel);
              new Element(ELEMENT_FORMAT_ID, formatId.toString()).encode(finalSB, settings, isTransferEncode, encodeLevel);
              
              formatWasInserted = true;
            }
            finally
            {
              settings.setEncodeFormat(oldEncodeFormat);
            }
          }
        }
      }
      
      if (needToInsertFormat)
      {
        boolean oldEncodeFormat = settings.isEncodeFormat();
        try
        {
          settings.setEncodeFormat(true);
          
          new Element(ELEMENT_FORMAT, getFormat()).encode(finalSB, settings, isTransferEncode, encodeLevel);
          
          formatWasInserted = true;
        }
        finally
        {
          settings.setEncodeFormat(oldEncodeFormat);
        }
      }
    }
    
    Boolean oldEncodeFormat = settings != null ? settings.isEncodeFormat() : null;
    try
    {
      if (settings != null && formatWasInserted)
      {
        settings.setEncodeFormat(false);
      }
      
      getEncodedData(finalSB, settings, isTransferEncode, encodeLevel + 1);
    }
    finally
    {
      if (oldEncodeFormat != null)
      {
        settings.setEncodeFormat(oldEncodeFormat);
      }
    }
    
    if (isInvalid())
    {
      new Element(ELEMENT_FORMAT, invalidationMessage).encode(finalSB, settings, isTransferEncode, encodeLevel);
    }
    
    if (!isKnown && formatId != null && collector != null)
    {
      // Marking format as known
      collector.makeKnown(formatId, true);
    }
    
    return finalSB;
  }
  
  int getEstimateDataSize()
  {
    final Integer knownRecordCount = getRecordCount();
    final int recordCount = knownRecordCount != null ? knownRecordCount : DEFAULT_ESTIMATE_RECORD_COUNT;
    return getFieldCount() * recordCount * 3 + getFieldCount() * 7;
  }
  
  @Override
  public String toString()
  {
    if (getNamingExpression() != null && !getNamingExpression().getText().isEmpty())
    {
      return getDescription();
    }
    else
    {
      return toDefaultString();
    }
  }
  
  /**
   * Returns human-readable description of the table.
   */
  @Override
  public String getDescription()
  {
    Expression namingExpression = getNamingExpression();
    
    if (namingExpression == null || getRecordCount() == 0)
    {
      return toDefaultString();
    }
    
    Evaluator evaluator = ensureEvaluator();
    
    Object name;
    try
    {
      name = evaluator.evaluate(namingExpression);
    }
    catch (Exception ex)
    {
      Log.CORE.info("Error evaluating naming expression of table '" + toDefaultString() + "'", ex);
      return toDefaultString();
    }
    
    return name == null ? null : name.toString();
  }
  
  private Evaluator ensureEvaluator()
  {
    if (namingEvaluator == null)
    {
      DefaultReferenceResolver defaultResolver = new DefaultReferenceResolver();
      defaultResolver.setDefaultTable(this);
      
      namingEvaluator = new Evaluator(defaultResolver);
      
      namingEvaluator.setResolver(Reference.SCHEMA_ENVIRONMENT, new DataTableReferenceResolver());
    }
    
    return namingEvaluator;
  }
  
  private Expression getNamingExpression()
  {
    return format == null ? null : format.getNamingExpression();
  }
  
  @Override
  public void fixRecords()
  {
    getFormat().fixRecords(this);
  }
  
  @Override
  public String dataAsString()
  {
    return dataAsString(true, false, false);
  }
  
  @Override
  public String dataAsString(boolean showFieldNames, boolean showHiddenFields)
  {
    return dataAsString(showFieldNames, showHiddenFields, false);
  }
  
  @Override
  public boolean conform(TableFormat rf)
  {
    return conformMessage(rf) == null;
  }
  
  @Override
  public String conformMessage(TableFormat rf)
  {
    if (getRecordCount() != null && getRecordCount() < rf.getMinRecords())
    {
      return "Number of records too small: need " + rf.getMinRecords() + " or more, found " + getRecordCount();
    }
    
    if (getRecordCount() != null && getRecordCount() > rf.getMaxRecords())
    {
      return "Number of records too big: need " + rf.getMaxRecords() + " or less, found " + getRecordCount();
    }
    
    return getFormat().extendMessage(rf);
  }
  
  /* (non-Javadoc)
   * Note that the result of the query is supposed to fit in a List
   */
  @Override
  public List<DataRecord> selectAll(DataTableQuery query)
  {
    List<DataRecord> r = new ArrayList<>();
    
    for (DataRecord rec : this)
    {
      boolean meet = true;
      
      for (QueryCondition cond : query.getConditions())
      {
        if (!rec.meetToCondition(cond))
        {
          meet = false;
        }
      }
      
      if (meet)
      {
        r.add(rec);
      }
    }
    
    return r;
  }
  
  @Override
  public DataRecord select(DataTableQuery query)
  {
    for (DataRecord rec : this)
    {
      boolean meet = true;
      
      for (QueryCondition cond : query.getConditions())
      {
        if (!rec.meetToCondition(cond))
        {
          meet = false;
        }
      }
      
      if (meet)
      {
        return rec;
      }
    }
    
    return null;
  }
  
  @Override
  public DataRecord select(String field, Object value)
  {
    return select(new DataTableQuery(new QueryCondition(field, value)));
  }
  
  @Override
  public Integer findIndex(String field, Object value)
  {
    return findIndex(new DataTableQuery(new QueryCondition(field, value)));
  }
  
  /**
   * Returns the index of the first occurrence of <code>record</code> in this Data Table, or null if this Data Table does not contain the specified record.
   */
  @Override
  public Integer findIndex(DataRecord record)
  {
    int index = 0;
    for (DataRecord currentRecord : this)
    {
      if (currentRecord.equals(record))
        return index;
      
      index++;
    }
    return null;
  }
  
  @Override
  public void sort(String field, boolean ascending)
  {
    sort(new DataTableSorter(new SortOrder(field, ascending)));
  }
  
  /**
   * Returns first record of the table.
   */
  @Override
  public DataRecord rec()
  {
    return getRecord(0);
  }
  
  /**
   * Returns value of first field in first record of the table.
   */
  @Override
  public Object get()
  {
    return rec().getValue(0);
  }
  
  // This method should be called only by Data Table Editors! It creates a mutable copy of format in every record.
  @Override
  public void splitFormat()
  {
    for (DataRecord rec : this)
    {
      rec.cloneFormatFromTable();
    }
  }
  
  @Override
  public void joinFormats()
  {
    for (DataRecord rec : this)
    {
      rec.setFormat(this.getFormat());
    }
  }
  
  @Override
  public DataTable clone()
  {
    try
    {
      return (DataTable) super.clone();
    }
    catch (CloneNotSupportedException ex)
    {
      throw new IllegalStateException(ex.getMessage(), ex);
    }
  }
  
  @Override
  public void append(DataTable src)
  {
    for (DataRecord rec : src)
    {
      this.addRecord(rec);
    }
  }
  
  @Override
  public DataTable cloneIfImmutable()
  {
    if (isImmutable())
    {
      return this.clone();
    }
    
    return this;
  }
  
  @Override
  public boolean isImmutable()
  {
    return immutable;
  }
  
  SimpleDataTable toSimpleDataTable()
  {
    final SimpleDataTable simpleDataTable = new SimpleDataTable(format);
    
    for (DataRecord record : this)
    {
      simpleDataTable.addRecord(record.clone());
    }
    
    if (getTimestamp() != null)
      simpleDataTable.setTimestamp(getTimestamp());
    
    if (getQuality() != null)
      simpleDataTable.setQuality(getQuality());
    
    return simpleDataTable;
  }
  
  @Override
  public void close()
  {
  }
  
  @Override
  public Stream<DataRecord> stream()
  {
    return StreamSupport.stream(spliterator(), false);
  }
  
  private class DataTableReferenceResolver extends AbstractReferenceResolver
  {
    @Override
    public Object resolveReference(Reference ref, EvaluationEnvironment resolvingEnvironment)
    {
      if (DataTableUtils.NAMING_ENVIRONMENT_SHORT_DATA.equals(ref.getField()))
      {
        return dataAsString(false, false);
      }
      
      if (DataTableUtils.NAMING_ENVIRONMENT_FULL_DATA.equals(ref.getField()))
      {
        return dataAsString(true, false);
      }
      
      return null;
    }
  }
}