package com.tibbo.aggregate.common.datatable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

import com.tibbo.aggregate.common.Log;
import com.tibbo.aggregate.common.binding.Binding;
import com.tibbo.aggregate.common.context.ContextRuntimeException;
import com.tibbo.aggregate.common.datatable.encoding.ClassicEncodingSettings;
import com.tibbo.aggregate.common.datatable.encoding.FormatCache;
import com.tibbo.aggregate.common.datatable.validator.KeyFieldsValidator;
import com.tibbo.aggregate.common.datatable.validator.RecordValidator;
import com.tibbo.aggregate.common.datatable.validator.TableExpressionValidator;
import com.tibbo.aggregate.common.datatable.validator.TableKeyFieldsValidator;
import com.tibbo.aggregate.common.datatable.validator.TableValidator;
import com.tibbo.aggregate.common.expression.Expression;
import com.tibbo.aggregate.common.expression.Reference;
import com.tibbo.aggregate.common.util.CloneUtils;
import com.tibbo.aggregate.common.util.Element;
import com.tibbo.aggregate.common.util.ElementList;
import com.tibbo.aggregate.common.util.PublicCloneable;
import com.tibbo.aggregate.common.util.StringEncodable;
import com.tibbo.aggregate.common.util.StringUtils;
import com.tibbo.aggregate.common.util.Util;

/**
 * <code>TableFormat</code> describes fields and other options of a <code>DataTable</code> or a <code>DataRecord</code>.
 */

public class TableFormat implements Iterable<FieldFormat>, Cloneable, PublicCloneable, StringEncodable
{
  public static final TableFormat EMPTY_FORMAT = new TableFormat(0, 0);
  
  public static final int DEFAULT_MIN_RECORDS = 0;
  public static final int DEFAULT_MAX_RECORDS = Integer.MAX_VALUE;
  
  private static final String ELEMENT_FLAGS = "F";
  private static final String ELEMENT_TABLE_VALIDATORS = "V";
  private static final String ELEMENT_RECORD_VALIDATORS = "R";
  private static final String ELEMENT_BINDINGS = "B";
  private static final String ELEMENT_MIN_RECORDS = "M";
  private static final String ELEMENT_MAX_RECORDS = "X";
  private static final String ELEMENT_NAMING = "N";
  
  public static final char TABLE_VALIDATOR_KEY_FIELDS = 'K';
  public static final char TABLE_VALIDATOR_EXPRESSION = 'E';
  
  public static final char RECORD_VALIDATOR_KEY_FIELDS = 'K';
  
  private static final char REORDERABLE_FLAG = 'R';
  private static final char UNRESIZEBLE_FLAG = 'U';
  private static final char BINDINGS_EDITABLE_FLAG = 'B';
  
  private List<FieldFormat> fields = new ArrayList(4); // Should be power of 2 (undocumented internal behavior of HashMap)
  
  private transient Map<String, Integer> fieldLookup = new HashMap(4);
  
  private int minRecords = DEFAULT_MIN_RECORDS;
  private int maxRecords = DEFAULT_MAX_RECORDS;
  
  private boolean reorderable;
  private boolean unresizable;
  private boolean bindingsEditable;
  
  private List<RecordValidator> recordValidators = new LinkedList();
  private List<TableValidator> tableValidators = new LinkedList();
  private List<Binding> bindings = new LinkedList();
  private Expression namingExpression;
  
  private transient boolean immutable;
  private transient Integer immutabilizerIdentityHashCode; // The identity hash code of the Data Table that made this format immutable
  
  private Integer id;
  private volatile Integer formatCacheIdentityHashCode; // Identity hash code of the FormatCache containing the format.
                                              // In case it was not cached on the local server, the parameter is null.
  
  /**
   * Constructs a default empty <code>TableFormat</code>.
   */
  public TableFormat()
  {
  }
  
  /**
   * Constructs a <code>TableFormat</code> with specified reorderable flag.
   */
  public TableFormat(boolean reorderable)
  {
    this();
    setReorderable(reorderable);
  }
  
  /**
   * Constructs a <code>TableFormat</code> with specified minimum and maximum number of records.
   */
  public TableFormat(int minRecords, int maxRecords)
  {
    this();
    setMinRecords(minRecords);
    setMaxRecords(maxRecords);
  }
  
  /**
   * Constructs a <code>TableFormat</code> with a single field specified by <code>FieldFormat</code>.
   */
  public TableFormat(FieldFormat ff)
  {
    this();
    addField(ff);
  }
  
  public TableFormat(String format, ClassicEncodingSettings settings)
  {
    this(format, settings, true);
  }
  
  public TableFormat(String format, ClassicEncodingSettings settings, boolean validate)
  {
    this();
    if (format == null)
    {
      return;
    }
    
    ElementList els = StringUtils.elements(format, settings.isUseVisibleSeparators());
    
    for (Element el : els)
    {
      if (el.getName() == null)
      {
        int index = fields.size();
        FieldFormat ff = FieldFormat.create(el.getValue(), settings, validate);
        fields.add(ff);
        getFieldLookup().put(ff.getName(), index);
        continue;
      }
      if (el.getName().equals(ELEMENT_FLAGS))
      {
        String flags = el.getValue();
        setReorderable(flags.indexOf(REORDERABLE_FLAG) != -1);
        setUnresizable(flags.indexOf(UNRESIZEBLE_FLAG) != -1);
        setBindingsEditable(flags.indexOf(BINDINGS_EDITABLE_FLAG) != -1);
        continue;
      }
      if (el.getName().equals(ELEMENT_MIN_RECORDS))
      {
        minRecords = Integer.parseInt(el.getValue());
        continue;
      }
      if (el.getName().equals(ELEMENT_MAX_RECORDS))
      {
        maxRecords = Integer.parseInt(el.getValue());
        continue;
      }
      if (el.getName().equals(ELEMENT_TABLE_VALIDATORS))
      {
        createTableValidators(el.getValue(), settings);
        continue;
      }
      if (el.getName().equals(ELEMENT_RECORD_VALIDATORS))
      {
        createRecordValidators(el.getValue(), settings);
        continue;
      }
      if (el.getName().equals(ELEMENT_BINDINGS))
      {
        createBindings(el.getValue(), settings);
        continue;
      }
      if (el.getName().equals(ELEMENT_NAMING))
      {
        createNaming(el.getValue());
      }
    }
  }
  
  public TableFormat(int minRecords, int maxRecords, String fieldFormat)
  {
    this(minRecords, maxRecords);
    addField(fieldFormat);
  }
  
  public TableFormat(int minRecords, int maxRecords, FieldFormat fieldFormat)
  {
    this(minRecords, maxRecords);
    addField(fieldFormat);
  }
  
  /**
   * Adds new fields to format.
   */
  public TableFormat addFields(FieldFormat[] fieldFormats)
  {
    for (FieldFormat each : fieldFormats)
    {
      this.addField(each);
    }
    return this;
  }
  
  /**
   * Adds new fields to format.
   */
  public TableFormat addFields(List<FieldFormat> fieldFormats)
  {
    for (FieldFormat each : fieldFormats)
    {
      this.addField(each);
    }
    return this;
  }
  
  /**
   * Adds new field to format.
   */
  public TableFormat addField(FieldFormat ff)
  {
    return addField(ff, fields.size());
  }
  
  /**
   * Adds new field to format.
   */
  public TableFormat addField(String encodedFormat)
  {
    return addField(FieldFormat.create(encodedFormat));
  }
  
  /**
   * Adds new field to format.
   */
  public void addField(char type, String name) throws DataTableException
  {
    addField(type, name, fields.size());
  }
  
  /**
   * Adds new field to format.
   */
  public TableFormat addField(char type, String name, String description)
  {
    addField(FieldFormat.create(name, type, description));
    return this;
  }
  
  /**
   * Adds new field to format.
   */
  public TableFormat addField(char type, String name, String description, Object defaultValue)
  {
    addField(FieldFormat.create(name, type, description, defaultValue));
    return this;
  }
  
  /**
   * Adds new field to format.
   */
  public TableFormat addField(char type, String name, String description, Object defaultValue, String group)
  {
    addField(FieldFormat.create(name, type, description, defaultValue, group));
    return this;
  }
  
  /**
   * Adds new field to format.
   */
  public TableFormat addField(char type, String name, String description, Object defaultValue, boolean nullable)
  {
    addField(FieldFormat.create(name, type, description, defaultValue, nullable));
    return this;
  }
  
  /**
   * Adds new field to format.
   */
  public TableFormat addField(char type, String name, String description, Object defaultValue, boolean nullable, String group)
  {
    addField(FieldFormat.create(name, type, description, defaultValue, nullable, group));
    return this;
  }
  
  /**
   * Adds new field to format at the specified index.
   * Note, that modifying table format of an existing table by inserting fields anywhere except appending them to
   * the end may cause <code>DataTable</code> to become invalid.
   */
  public TableFormat addField(FieldFormat ff, int index)
  {
    if (immutable)
    {
      throw new IllegalStateException("Immutable");
    }
    
    FieldFormat existing = getField(ff.getName());
    
    if (existing != null)
    {
      if (!ff.extend(existing))
      {
        throw new IllegalArgumentException("Field '" + ff.getName() + "' already exists in format");
      }
      else
      {
        return this;
      }
    }
    
    for (int i = index; i < fields.size(); i++)
    {
      String fn = fields.get(i).getName();
      
      Integer previousIndex = getFieldLookup().get(fn);
      
      if (previousIndex == null)
      {
        throw new IllegalStateException("Null lookup index for field " + i + " (" + fn + ")");
      }
      
      getFieldLookup().put(fn, previousIndex + 1);
    }
    
    fields.add(index, ff);
    
    getFieldLookup().put(ff.getName(), index);
    
    return this;
  }
  
  /**
   * Note, that modifying table format of an existing table by inserting fields anywhere except appending them to the end may cause <code>DataTable</code> to become invalid.
   */
  public TableFormat addField(char type, String name, int index) throws DataTableException
  {
    if (immutable)
    {
      throw new IllegalStateException("Immutable");
    }
    
    return addField(FieldFormat.create(name, type), index);
  }
  
  /**
   * Removes field from format.
   */
  public TableFormat removeField(String name)
  {
    if (immutable)
    {
      throw new IllegalStateException("Immutable");
    }
    
    Integer index = getFieldLookup().remove(name);
    
    if (index != null)
    {
      fields.remove(index.intValue());
      
      for (int i = index; i < fields.size(); i++)
      {
        String fn = fields.get(i).getName();
        
        getFieldLookup().put(fn, getFieldLookup().get(fn) - 1);
      }
    }
    
    return this;
  }
  
  /**
   * Renames a field.
   */
  public TableFormat renameField(String oldName, String newName)
  {
    if (immutable)
    {
      throw new IllegalStateException("Immutable");
    }
    
    FieldFormat ff = getField(oldName);
    
    if (ff == null)
    {
      return this;
    }
    
    ff.setName(newName);
    
    Integer index = getFieldLookup().remove(oldName);
    
    if (index != null)
    {
      getFieldLookup().put(newName, index);
    }
    
    return this;
  }
  
  /**
   * Returns type of field at the specified index.
   */
  public char getFieldType(int index)
  {
    return fields.get(index).getType();
  }
  
  /**
   * Returns name of field at the specified index.
   */
  public String getFieldName(int index)
  {
    return fields.get(index).getName();
  }
  
  /**
   * Returns index of field with the specified name or -1 if it does not exist.
   */
  public int getFieldIndex(String name)
  {
    Integer index = getFieldLookup().get(name);
    
    return index != null ? index : -1;
  }
  
  /**
   * Returns number of fields in table format.
   */
  public int getFieldCount()
  {
    return fields.size();
  }
  
  /**
   * Returns list of fields in table format.
   */
  public List<FieldFormat> getFields()
  {
    return immutable ? Collections.unmodifiableList(fields) : fields;
  }
  
  /**
   * Returns modifiable list of record validators.
   */
  public List<RecordValidator> getRecordValidators()
  {
    return immutable ? Collections.unmodifiableList(recordValidators) : recordValidators;
  }
  
  /**
   * Returns modifiable list of table validators.
   */
  public List<TableValidator> getTableValidators()
  {
    return immutable ? Collections.unmodifiableList(tableValidators) : tableValidators;
  }
  
  /**
   * Returns maximal number of records allowed by format.
   */
  public int getMaxRecords()
  {
    return maxRecords;
  }
  
  /**
   * Returns minimal number of records allowed by format.
   */
  public int getMinRecords()
  {
    return minRecords;
  }
  
  /**
   * Returns true if format allows record reordering.
   */
  public boolean isReorderable()
  {
    return reorderable;
  }
  
  public boolean isUnresizable()
  {
    return unresizable;
  }
  
  public void setUnresizable(boolean unresizable)
  {
    if (immutable)
    {
      throw new IllegalStateException("Immutable");
    }
    
    this.unresizable = unresizable;
  }
  
  public boolean isBindingsEditable()
  {
    return bindingsEditable;
  }
  
  public void setBindingsEditable(boolean bindingsEditable)
  {
    // Bindings Editable flag only affects visual editing of the table, so there's no need to condider immutability here
    
    this.bindingsEditable = bindingsEditable;
  }
  
  /**
   * Returns modifiable list of table bindings.
   */
  public List<Binding> getBindings()
  {
    return immutable ? Collections.unmodifiableList(bindings) : bindings;
  }
  
  /**
   * Adds new binding.
   */
  public void addBinding(Binding binding)
  {
    if (immutable)
    {
      throw new IllegalStateException("Immutable");
    }
    
    bindings.add(binding);
  }
  
  /**
   * Adds new binding.
   */
  public void addBinding(Reference target, Expression expression)
  {
    addBinding(new Binding(target, expression));
  }
  
  /**
   * Adds new binding.
   */
  public void addBinding(String target, String expression)
  {
    addBinding(new Binding(new Reference(target), new Expression(expression)));
  }
  
  /**
   * Removes the binding.
   */
  public void removeBinding(Binding binding)
  {
    if (immutable)
    {
      throw new IllegalStateException("Immutable");
    }
    
    bindings.remove(binding);
  }
  
  /**
   * Set the bindings.
   */
  public void setBindings(List<Binding> in_bindings)
  {
    if (immutable)
    {
      throw new IllegalStateException("Immutable");
    }
    
    bindings = in_bindings;
  }
  
  /**
   * Returns naming expression.
   */
  public Expression getNamingExpression()
  {
    return namingExpression;
  }
  
  public String encode(boolean useVisibleSeparators)
  {
    return encode(new ClassicEncodingSettings(useVisibleSeparators));
  }
  
  public String encode(ClassicEncodingSettings settings)
  {
    return encode(settings, false, 0);
  }
  
  public String encode(ClassicEncodingSettings settings, Boolean isTransferEncode, Integer encodeLevel)
  {
    StringBuilder formatString = new StringBuilder(getFieldCount() * 7);
    
    encode(formatString, settings, isTransferEncode, encodeLevel);
    
    return formatString.toString();
  }
  
  @Override
  public StringBuilder encode(StringBuilder builder, ClassicEncodingSettings settings, Boolean isTransferEncode, Integer encodeLevel)
  {
    for (int i = 0; i < fields.size(); i++)
    {
      new Element(null, getField(i).encode(settings)).encode(builder, settings, isTransferEncode, encodeLevel);
    }
    
    if (minRecords != DEFAULT_MIN_RECORDS)
    {
      new Element(ELEMENT_MIN_RECORDS, String.valueOf(minRecords)).encode(builder, settings, isTransferEncode, encodeLevel);
    }
    
    if (maxRecords != DEFAULT_MAX_RECORDS)
    {
      new Element(ELEMENT_MAX_RECORDS, String.valueOf(maxRecords)).encode(builder, settings, isTransferEncode, encodeLevel);
    }
    
    if (tableValidators.size() > 0)
    {
      new Element(ELEMENT_TABLE_VALIDATORS, getEncodedTableValidators(settings)).encode(builder, settings, isTransferEncode, encodeLevel);
    }
    
    if (recordValidators.size() > 0)
    {
      new Element(ELEMENT_RECORD_VALIDATORS, getEncodedRecordValidators(settings)).encode(builder, settings, isTransferEncode, encodeLevel);
    }
    
    if (bindings.size() > 0)
    {
      new Element(ELEMENT_BINDINGS, getEncodedBindings(settings)).encode(builder, settings, isTransferEncode, encodeLevel);
    }
    
    if (namingExpression != null)
    {
      new Element(ELEMENT_NAMING, namingExpression.getText()).encode(builder, settings, isTransferEncode, encodeLevel);
    }
    
    encAppend(builder, ELEMENT_FLAGS, getEncodedFlags(), settings);
    
    return builder;
  }
  
  private static void encAppend(StringBuilder buffer, String name, String value, ClassicEncodingSettings settings)
  {
    if (value != null && value.length() > 0)
    {
      buffer.append(new Element(name, value).encode(settings));
    }
  }
  
  public Map<String, Integer> getFieldLookup()
  {
    if (fieldLookup == null)
    {
      fieldLookup = new HashMap<>(getFieldCount());
      for (int i = 0; i < fields.size(); i++)
      {
        FieldFormat field = fields.get(i);
        fieldLookup.put(field.getName(), i);
      }
    }
    return fieldLookup;
  }
  
  private String getEncodedFlags()
  {
    StringBuilder buf = new StringBuilder();
    if (isReorderable())
    {
      buf.append(REORDERABLE_FLAG);
    }
    if (isUnresizable())
    {
      buf.append(UNRESIZEBLE_FLAG);
    }
    if (isBindingsEditable())
    {
      buf.append(BINDINGS_EDITABLE_FLAG);
    }
    return buf.toString();
  }
  
  public String getEncodedTableValidators(ClassicEncodingSettings settings)
  {
    StringBuilder enc = new StringBuilder();
    
    for (TableValidator tv : tableValidators)
    {
      if (tv.getType() != null)
      {
        enc.append(new Element(String.valueOf(tv.getType()), tv.encode()).encode(settings));
      }
    }
    
    return enc.toString();
  }
  
  private String getEncodedRecordValidators(ClassicEncodingSettings settings)
  {
    StringBuilder enc = new StringBuilder();
    
    for (RecordValidator rv : recordValidators)
    {
      if (rv.getType() != null)
      {
        enc.append(new Element(String.valueOf(rv.getType()), rv.encode()).encode(settings));
      }
    }
    
    return enc.toString();
  }
  
  private String getEncodedBindings(ClassicEncodingSettings settings)
  {
    StringBuilder enc = new StringBuilder();
    
    for (Binding bin : bindings)
    {
      enc.append(new Element(bin.getTarget().getImage(), bin.getExpression().getText()).encode(settings));
    }
    
    return enc.toString();
  }
  
  @Override
  public String toString()
  {
    return encode(new ClassicEncodingSettings(true));
  }
  
  /**
   * Returns format of field with specified index.
   */
  public FieldFormat getField(int index)
  {
    return fields.get(index);
  }
  
  /**
   * Returns format of field with specified name.
   */
  public FieldFormat getField(String fieldName)
  {
    int index = getFieldIndex(fieldName);
    if (index != -1)
    {
      return getField(index);
    }
    else
    {
      return null;
    }
  }
  
  /**
   * Returns true if table format contains field with specified name.
   */
  public boolean hasField(String name)
  {
    return getFieldIndex(name) != -1;
  }
  
  /**
   * Returns true if table format contains fields of the specified type.
   */
  public boolean hasFields(char type)
  {
    for (FieldFormat ff : this)
    {
      if (ff.getType() == type)
      {
        return true;
      }
    }
    
    return false;
  }
  
  /**
   * Returns true if format has read-only fields
   */
  public boolean hasReadOnlyFields()
  {
    for (FieldFormat ff : this)
    {
      if (ff.isReadonly())
      {
        return true;
      }
    }
    
    return false;
  }
  
  /**
   * Returns list of key field names.
   */
  public List<String> getKeyFields()
  {
    List<String> keyFields = new LinkedList();
    
    for (FieldFormat ff : this)
    {
      if (ff.isKeyField())
      {
        keyFields.add(ff.getName());
      }
    }
    
    return keyFields;
  }
  
  /**
   * Returns true if this format extends the <code>other</code> format.
   */
  public boolean extend(TableFormat other)
  {
    return extendMessage(other) == null;
  }
  
  public String extendMessage(TableFormat other)
  {
    if (this == other)
    {
      return null;
    }
    
    if (equals(other))
    {
      return null;
    }
    
    if (!isReorderable() && other.isReorderable())
    {
      return "Different reorderable flags: need " + isReorderable() + ", found " + other.isReorderable();
    }
    
    if (!isUnresizable() && other.isUnresizable())
    {
      return "Different unresizable flags: need " + isUnresizable() + ", found " + other.isUnresizable();
    }
    
    if (!Util.equals(getNamingExpression(), other.getNamingExpression()))
    {
      return "Different naming expressions: need " + getNamingExpression() + ", found " + other.getNamingExpression();
    }
    
    for (Binding otherBinding : other.getBindings())
    {
      if (!getBindings().contains(otherBinding))
      {
        return "Different bindings: need " + getBindings() + ", found " + other.getBindings();
      }
    }
    
    for (FieldFormat otherFormat : other)
    {
      FieldFormat ownFormat = getField(otherFormat.getName());
      
      if (ownFormat == null)
      {
        if (otherFormat.isOptional())
        {
          continue;
        }
        else
        {
          return "Required field doesn't exist: " + otherFormat.getName();
        }
      }
      
      String fieldExtendMessage = ownFormat.extendMessage(otherFormat);
      
      if (fieldExtendMessage != null)
      {
        return "Incorrect format of field '" + otherFormat.getName() + "': " + fieldExtendMessage;
      }
    }
    
    return null;
  }
  
  /**
   * Adds new table validator to the format.
   */
  public void addTableValidator(TableValidator tv)
  {
    if (immutable)
    {
      throw new IllegalStateException("Immutable");
    }
    
    tableValidators.add(tv);
  }
  
  /**
   * Adds new record validator to the format.
   */
  public void addRecordValidator(RecordValidator rv)
  {
    if (immutable)
    {
      throw new IllegalStateException("Immutable");
    }
    
    recordValidators.add(rv);
  }
  
  public void createTableValidators(String source, ClassicEncodingSettings settings)
  {
    if (source == null || source.length() == 0)
    {
      return;
    }
    
    ElementList validatorsData = StringUtils.elements(source, settings.isUseVisibleSeparators());
    
    for (Element el : validatorsData)
    {
      char validatorType = el.getName().charAt(0);
      String validatorParams = el.getValue();
      
      switch (validatorType)
      {
        case TABLE_VALIDATOR_KEY_FIELDS:
          addTableValidator(new TableKeyFieldsValidator(validatorParams));
          break;
        
        case TABLE_VALIDATOR_EXPRESSION:
          addTableValidator(new TableExpressionValidator(validatorParams));
          break;
      }
    }
  }
  
  private void createRecordValidators(String source, ClassicEncodingSettings settings)
  {
    if (source == null || source.length() == 0)
    {
      return;
    }
    
    ElementList validatorsData = StringUtils.elements(source, settings.isUseVisibleSeparators());
    
    for (Element el : validatorsData)
    {
      char validatorType = el.getName().charAt(0);
      String validatorParams = el.getValue();

      if (validatorType == RECORD_VALIDATOR_KEY_FIELDS)
      {
        addRecordValidator(new KeyFieldsValidator(validatorParams));
      }
    }
  }
  
  private void createBindings(String source, ClassicEncodingSettings settings)
  {
    if (source == null || source.length() == 0)
    {
      return;
    }
    
    ElementList bindingsData = StringUtils.elements(source, settings.isUseVisibleSeparators());
    
    for (Element el : bindingsData)
    {
      bindings.add(new Binding(new Reference(el.getName()), new Expression(el.getValue())));
    }
  }
  
  private void createNaming(String source)
  {
    if (source == null || source.length() == 0)
    {
      return;
    }
    
    namingExpression = new Expression(source);
  }
  
  @Override
  public Iterator<FieldFormat> iterator()
  {
    return fields.iterator();
  }
  
  public boolean isReplicated()
  {
    for (FieldFormat ff : this)
    {
      if (!ff.isNotReplicated())
      {
        return true;
      }
    }
    
    return false;
  }
  
  public boolean isReadonly()
  {
    for (FieldFormat ff : this)
    {
      if (!ff.isReadonly())
      {
        return false;
      }
    }
    
    return true;
  }
  
  public boolean isGrouped()
  {
    for (FieldFormat ff : this)
    {
      if (ff.getGroup() != null)
      {
        return true;
      }
    }
    
    return false;
  }
  
  public boolean isAdvanced()
  {
    for (FieldFormat ff : this)
    {
      if (ff.isAdvanced())
      {
        return true;
      }
    }
    
    return false;
  }
  
  public boolean isSingleRecord()
  {
    return minRecords == 1 && maxRecords == 1;
  }
  
  public boolean isSingleField()
  {
    return getFieldCount() == 1;
  }
  
  public boolean isSingleCell()
  {
    return isSingleRecord() && isSingleField();
  }
  
  public boolean isEmpty()
  {
    return minRecords == 0 && maxRecords == 0 && getFieldCount() == 0;
  }
  
  public boolean isImmutable()
  {
    return immutable;
  }
  
  /**
   * Resets allowed number of records to defaults.
   */
  
  public TableFormat resetAllowedRecords()
  {
    if (immutable)
    {
      throw new IllegalStateException("Immutable");
    }
    
    this.minRecords = DEFAULT_MIN_RECORDS;
    this.maxRecords = DEFAULT_MAX_RECORDS;
    
    return this;
  }
  
  /**
   * Sets maximum allowed number of records.
   */
  public TableFormat setMaxRecords(int maxRecords)
  {
    if (immutable)
    {
      throw new IllegalStateException("Immutable");
    }
    
    this.maxRecords = maxRecords;
    
    return this;
  }
  
  /**
   * Sets minimum allowed number of records.
   */
  public TableFormat setMinRecords(int minRecords)
  {
    if (immutable)
    {
      throw new IllegalStateException("Immutable");
    }
    
    this.minRecords = minRecords;
    
    return this;
  }
  
  void fixRecords(DataTable table)
  {
    if (immutable && !Objects.equals(immutabilizerIdentityHashCode, System.identityHashCode(table)))
    {
      throw new IllegalStateException("Format was not made immutable by this table");
    }
    
    minRecords = table.getRecordCount();
    maxRecords = table.getRecordCount();
  }
  
  /**
   * Sets reorderable flag that allows table record reordering.
   */
  public TableFormat setReorderable(boolean reorderable)
  {
    if (immutable)
    {
      throw new IllegalStateException("Immutable");
    }
    
    this.reorderable = reorderable;
    return this;
  }
  
  /**
   * Sets naming expression.
   */
  public TableFormat setNamingExpression(Expression namingExpression)
  {
    if (immutable)
    {
      throw new IllegalStateException("Immutable");
    }
    
    this.namingExpression = namingExpression;
    return this;
  }
  
  /**
   * Sets naming expression.
   */
  public TableFormat setNamingExpression(String namingExpression)
  {
    setNamingExpression(new Expression(namingExpression));
    return this;
  }
  
  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    
    result = prime * result + maxRecords;
    result = prime * result + minRecords;
    result = prime * result + ((fields == null) ? 0 : fields.hashCode());
    result = prime * result + ((namingExpression == null) ? 0 : namingExpression.hashCode());
    result = prime * result + ((recordValidators == null) ? 0 : recordValidators.hashCode());
    result = prime * result + ((tableValidators == null) ? 0 : tableValidators.hashCode());
    result = prime * result + (reorderable ? 1231 : 1237);
    result = prime * result + (unresizable ? 1231 : 1237);
    result = prime * result + ((bindings == null) ? 0 : bindings.hashCode());
    
    if (result < 0)
    {
      result = Integer.MAX_VALUE + result;
    }
    return result;
  }
  
  @Override
  public boolean equals(Object obj)
  {
    if (this == obj)
    {
      return true;
    }
    if (obj == null)
    {
      return false;
    }
    if (getClass() != obj.getClass())
    {
      return false;
    }
    TableFormat other = (TableFormat) obj;
    if (maxRecords != other.maxRecords)
    {
      return false;
    }
    if (minRecords != other.minRecords)
    {
      return false;
    }
    if (fields == null)
    {
      if (other.fields != null)
      {
        return false;
      }
    }
    
    else if (!fields.equals(other.fields))
    {
      return false;
    }
    if (namingExpression == null)
    {
      if (other.namingExpression != null)
      {
        return false;
      }
    }
    else if (!namingExpression.equals(other.namingExpression))
    {
      return false;
    }
    if (recordValidators == null)
    {
      if (other.recordValidators != null)
      {
        return false;
      }
    }
    else if (!recordValidators.equals(other.recordValidators))
    {
      return false;
    }
    if (tableValidators == null)
    {
      if (other.tableValidators != null)
      {
        return false;
      }
    }
    else if (!tableValidators.equals(other.tableValidators))
    {
      return false;
    }
    if (reorderable != other.reorderable)
    {
      return false;
    }
    if (unresizable != other.unresizable)
    {
      return false;
    }
    if (bindingsEditable != other.bindingsEditable)
    {
      return false;
    }
    if (bindings == null)
    {
      return other.bindings == null;
    }
    else
      return bindings.equals(other.bindings);
  }
  
  @Override
  public TableFormat clone()
  {
    TableFormat cl;
    
    try
    {
      cl = (TableFormat) super.clone();
    }
    catch (CloneNotSupportedException ex)
    {
      throw new IllegalStateException(ex.getMessage(), ex);
    }
    
    cl.fields = (List) CloneUtils.deepClone(fields);
    cl.fieldLookup = (Map) CloneUtils.deepClone(getFieldLookup());
    cl.recordValidators = (List) CloneUtils.deepClone(recordValidators);
    cl.tableValidators = (List) CloneUtils.deepClone(tableValidators);
    cl.bindings = (List) CloneUtils.deepClone(bindings);
    
    cl.id = null; // Need to clear ID to avoid conflicts in format cache
    cl.immutable = false;
    
    return cl;
  }
  
  public void makeImmutable(DataTable immutabilizer)
  {
    if (immutable)
    {
      return;
    }
    
    immutable = true;
    
    immutabilizerIdentityHashCode = System.identityHashCode(immutabilizer);
    
    for (FieldFormat ff : fields)
    {
      ff.makeImmutable();
    }
  }
  
  public Integer getId()
  {
    return id;
  }
  
  public void setId(Integer id)
  {
    if (!immutable)
    {
      throw new ContextRuntimeException("Cannot set ID of non-immutable format");
    }
    
    if (this.id != null && !this.id.equals(id))
    {
      throw new ContextRuntimeException("Format already has ID " + this.id + ", new ID " + id);
    }
    
    this.id = id;
  }
  
  public void applyCachedFormat(FormatCache formatCache, Consumer<TableFormat> formatSetter)
  {
    if (isAttachedToAnotherCache(formatCache))
    {
      // Format is belonged to another cache. Cannot be cached.
      return;
    }
    Integer cachedFormatId;
    try
    {
      cachedFormatId = formatCache.addIfNotExists(this);
    }
    catch (UnsupportedOperationException e)
    {
      Log.PROTOCOL_CACHING.debug("Illegal attempt to add format: '" + this + "' into the cache: '" + formatCache.getName() + "'");
      return;
    }
    
    TableFormat cachedFormat = formatCache.get(cachedFormatId);
    if (!cachedFormat.equals(this))
    {
      Log.PROTOCOL_CACHING.fatal("Expected format: '" + this + "' is differ from the cached one. " +
          "Cached id: '" + cachedFormatId + "' , Cached Format: '" + cachedFormat + "'");
      return;
    }
    if (cachedFormat != this) // this happens when the format have already been cached previously
    {
      formatSetter.accept(cachedFormat);
    }
  }
  
  public void setFormatCacheIdentityHashCode(int formatCacheIdentityHashCode)
  {
    this.formatCacheIdentityHashCode = formatCacheIdentityHashCode;
  }
  
  public boolean isAttachedToAnotherCache(FormatCache formatCache)
  {
    return formatCacheIdentityHashCode != null && formatCacheIdentityHashCode != System.identityHashCode(formatCache);
  }

  public boolean isCachedLocally()
  {
    return formatCacheIdentityHashCode != null;
  }
}
