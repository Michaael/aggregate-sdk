package com.tibbo.aggregate.common.datatable;

import java.awt.*;
import java.text.*;
import java.util.*;
import java.util.List;

import com.tibbo.aggregate.common.*;
import com.tibbo.aggregate.common.data.*;
import com.tibbo.aggregate.common.datatable.encoding.*;
import com.tibbo.aggregate.common.datatable.field.*;
import com.tibbo.aggregate.common.util.*;

/**
 * <code>DataRecord</code> is a single record of <code>DataTable</code>. When created separately from a table, <code>DataRecord</code> may have its own <code>TableFormat</code> describing record
 * fields and other options.
 */

public class DataRecord implements Cloneable, PublicCloneable, StringEncodable
{
  private static final int INITIAL_DATA_SIZE = 4; // Should be power of 2 (undocumented internal behavior of HashMap)
  
  private static final String ELEMENT_ID = "I";
  
  private Map<String, Object> data;
  private TableFormat format = new TableFormat();
  private String id = null;
  
  private transient DataTable table;
  
  /**
   * Constructs a default <code>DataRecord</code> with empty <code>TableFormat</code>.
   */
  public DataRecord()
  {
    data = new HashMap<String, Object>(INITIAL_DATA_SIZE);
  }
  
  /**
   * Constructs a <code>DataRecord</code> with specified <code>TableFormat</code>.
   */
  public DataRecord(TableFormat tableFormat)
  {
    data = new HashMap<String, Object>(tableFormat != null ? tableFormat.getFieldCount() : INITIAL_DATA_SIZE);
    if (tableFormat != null)
    {
      tableFormat.makeImmutable(null);
      format = tableFormat;
    }
  }
  
  /**
   * Constructs a <code>DataRecord</code> with specified <code>TableFormat</code> and fills it with data field-by-field.
   */
  public DataRecord(TableFormat tableFormat, Object... data)
  {
    this(tableFormat);
    for (Object param : data)
    {
      addValue(param);
    }
  }
  
  public DataRecord(TableFormat tableFormat, String dataString, ClassicEncodingSettings settings, boolean validate, List<String> fieldNamesInData) throws DataTableException
  {
    this(tableFormat);
    setData(dataString, settings, validate, fieldNamesInData);
  }
  
  public DataRecord(TableFormat tableFormat, String dataString) throws DataTableException
  {
    this(tableFormat, dataString, new ClassicEncodingSettings(false), true, null);
  }
  
  private void setData(String dataString, ClassicEncodingSettings settings, boolean validate, List<String> fieldNamesInData) throws DataTableException
  {
    ElementList recs = null;
    try
    {
      recs = StringUtils.elements(dataString, settings.isUseVisibleSeparators());
    }
    catch (Exception | OutOfMemoryError ex)
    {
      Log.DATATABLE.warn(ex.getMessage());
    }
    
    if (recs == null)
      return;
    
    int i = 0;
    for (Element el : recs)
    {
      if (el.getName() != null)
      {
        if (el.getName().equals(ELEMENT_ID))
        {
          setId(el.getValue());
        }
        else
        {
          // This code exists for compatibility reason only
          FieldFormat ff = format.getField(el.getName());
          if (ff != null)
          {
            setValue(el.getName(), ff.valueFromEncodedString(el.getValue(), settings, validate), validate);
          }
        }
      }
      else
      {
        if (fieldNamesInData != null && fieldNamesInData.size() > i)
        {
          String fieldName = fieldNamesInData.get(i);
          if (getFormat().hasField(fieldName))
          {
            Object value = format.getField(fieldName).valueFromEncodedString(el.getValue(), settings, validate);
            setValue(fieldName, value, validate);
          }
        }
        else if (i < format.getFieldCount())
        {
          Object value = format.getField(i).valueFromEncodedString(el.getValue(), settings, validate);
          setValue(i, value, validate);
        }
        i++;
      }
    }
  }
  
  /**
   * Returns number of fields in the record.
   */
  public int getFieldCount()
  {
    if (format == null)
    {
      return 0;
    }
    else
    {
      return format.getFieldCount();
    }
  }
  
  /**
   * Returns format of the record.
   */
  public TableFormat getFormat()
  {
    return format;
  }
  
  /**
   * Returns format of field with specified index.
   */
  public FieldFormat getFormat(int index)
  {
    return format.getField(index);
  }
  
  /**
   * Returns format of the specified field.
   */
  public FieldFormat getFormat(String name)
  {
    return format.getField(name);
  }
  
  public String getId()
  {
    return id;
  }
  
  public DataTable getTable()
  {
    return table;
  }
  
  public String encode(boolean useVisibleSeparators)
  {
    return encode(new ClassicEncodingSettings(useVisibleSeparators));
  }
  
  public String encode(ClassicEncodingSettings settings)
  {
    return encode(new StringBuilder(), settings, false, 0).toString();
  }
  
  @Override
  public StringBuilder encode(StringBuilder sb, ClassicEncodingSettings settings, Boolean isTransferEncode, Integer encodeLevel)
  {
    if (getId() != null)
    {
      new Element(ELEMENT_ID, getId()).encode(sb, settings, isTransferEncode, encodeLevel);
    }
    
    for (int i = 0; i < format.getFieldCount(); i++)
    {
      FieldFormat ff = format.getField(i);
      
      Object value = getValue(ff);
      
      new Element(null, ff, value).encode(sb, settings, isTransferEncode, encodeLevel);
    }
    
    return sb;
  }
  
  private boolean checkNumberOfDataFieldsSet(Object value)
  {
    if (data.size() >= format.getFieldCount())
    {
      Log.DATATABLE.warn("Can't add data to data record since all data fields defined by format are already set: " + value, new Exception());
      
      return false;
    }
    
    return true;
  }
  
  /**
   * Adds new Integer to the record.
   */
  public DataRecord addInt(Integer val)
  {
    if (!checkNumberOfDataFieldsSet(val))
    {
      return this;
    }
    
    return setValue(data.size(), val);
  }
  
  /**
   * Adds new String to the record.
   */
  public DataRecord addString(String val)
  {
    if (!checkNumberOfDataFieldsSet(val))
    {
      return this;
    }
    
    return setValue(data.size(), val);
  }
  
  /**
   * Adds new Boolean to the record.
   */
  public DataRecord addBoolean(Boolean val)
  {
    if (!checkNumberOfDataFieldsSet(val))
    {
      return this;
    }
    
    return setValue(data.size(), val);
  }
  
  /**
   * Adds new Long to the record.
   */
  public DataRecord addLong(Long val)
  {
    if (!checkNumberOfDataFieldsSet(val))
    {
      return this;
    }
    
    return setValue(data.size(), val);
  }
  
  /**
   * Adds new Float to the record.
   */
  public DataRecord addFloat(Float val)
  {
    if (!checkNumberOfDataFieldsSet(val))
    {
      return this;
    }
    
    return setValue(data.size(), val);
  }
  
  /**
   * Adds new Double to the record.
   */
  public DataRecord addDouble(Double val)
  {
    if (!checkNumberOfDataFieldsSet(val))
    {
      return this;
    }
    
    return setValue(data.size(), val);
  }
  
  /**
   * Adds new Date to the record.
   */
  public DataRecord addDate(Date val)
  {
    if (!checkNumberOfDataFieldsSet(val))
    {
      return this;
    }
    
    return setValue(data.size(), val);
  }
  
  /**
   * Adds new DataTable to the record.
   */
  public DataRecord addDataTable(DataTable val)
  {
    if (!checkNumberOfDataFieldsSet(val))
    {
      return this;
    }
    
    return setValue(data.size(), val);
  }
  
  /**
   * Adds new Color to the record.
   */
  public DataRecord addColor(Color val)
  {
    if (!checkNumberOfDataFieldsSet(val))
    {
      return this;
    }
    
    return setValue(data.size(), val);
  }
  
  /**
   * Adds new Data to the record.
   */
  public DataRecord addData(Data val)
  {
    if (!checkNumberOfDataFieldsSet(val))
    {
      return this;
    }
    
    return setValue(data.size(), val);
  }
  
  /**
   * Sets value of field with specified index to <code>value</code>.
   */
  public DataRecord setValue(int index, Object value)
  {
    return setValue(index, value, true);
  }
  
  public DataRecord setValue(int index, Object value, boolean validate)
  {
    ensureMutable();
    
    DataTable recordTable = getTable();
    boolean isRecursive = value != null && value == recordTable;
    if (isRecursive)
    {
      value = recordTable.clone();
    }
    
    FieldFormat ff = getFormat().getField(index);
    
    try
    {
      value = ff.checkAndConvertValue(value, validate);
    }
    catch (ValidationException ex)
    {
      throw new IllegalArgumentException(MessageFormat.format(Cres.get().getString("dtIllegalFieldValue"), value, ff.toDetailedString()) + ex.getMessage(), ex);
    }
    
    Object oldValue = data.get(ff.getName());
    
    try
    {
      data.put(ff.getName(), value);
      if (table != null)
      {
        table.validateRecord(this);
      }
    }
    catch (ValidationException ex1)
    {
      data.put(ff.getName(), oldValue);
      throw new IllegalArgumentException(ex1.getMessage(), ex1);
    }
    
    return this;
  }
  
  /**
   * Sets value of field with specified name to <code>value</code>.
   */
  public DataRecord setValue(String name, Object value)
  {
    return setValue(findIndex(name), value, true);
  }
  
  /**
   * Sets value of field with specified name to <code>value</code>.
   */
  public DataRecord setValue(String name, Object value, boolean validate)
  {
    return setValue(findIndex(name), value, validate);
  }
  
  /**
   * Sets value of field with specified index to <code>value</code>.
   */
  public DataRecord setValueSmart(int index, Object value)
  {
    FieldFormat ff = getFormat(index);
    return setValueSmart(ff.getName(), value);
  }
  
  /**
   * Sets value of field with specified name to <code>value</code>.
   */
  public DataRecord setValueSmart(String name, Object value)
  {
    FieldFormat ff = getFormat().getField(name);
    
    if (ff == null)
    {
      throw new IllegalArgumentException(MessageFormat.format(Cres.get().getString("dtFieldNotFound"), name) + ": " + dataAsString(true, true));
    }
    
    if (value == null || ff.getFieldClass().isAssignableFrom(value.getClass()) || ff.getFieldWrappedClass().isAssignableFrom(value.getClass()))
    {
      return setValue(ff.getName(), value);
    }
    else
    {
      String stringValue = value.toString();
      try
      {
        return setValue(ff.getName(), ff.valueFromString(stringValue));
      }
      catch (Exception ex)
      {
        if (ff.getSelectionValues() != null)
        {
          for (Object sv : ff.getSelectionValues().keySet())
          {
            String svdesc = ff.getSelectionValues().get(sv).toString();
            if (stringValue.equals(svdesc))
            {
              return setValue(ff.getName(), sv);
            }
          }
        }
        throw new IllegalArgumentException(MessageFormat.format(Cres.get().getString("dtIllegalFieldValue"), Util.getObjectDescription(value), ff.toDetailedString()) + ex.getMessage(), ex);
      }
    }
  }
  
  /**
   * Adds new value to the record.
   */
  public DataRecord addValue(Object value)
  {
    if (!checkNumberOfDataFieldsSet(value))
    {
      return this;
    }
    
    return setValue(data.size(), value);
  }
  
  /**
   * Returns index of field with specified name or throws an IllegalArgumentException if field is not found.
   */
  private int findIndex(String name)
  {
    int index = format.getFieldIndex(name);
    
    if (index == -1)
    {
      // Оптимизация: используем ArrayList вместо LinkedList для лучшей производительности
      // Предварительно резервируем размер на основе количества полей
      int fieldCount = getFormat().getFieldCount();
      List<String> fields = new ArrayList<String>(fieldCount);
      
      for (FieldFormat ff : getFormat())
      {
        fields.add(ff.getName());
      }
      
      throw new IllegalArgumentException(MessageFormat.format(Cres.get().getString("dtFieldNotFound"), name) + ": " + StringUtils.print(fields));
    }
    
    return index;
  }
  
  /**
   * Returns value of String field with specified name.
   */
  public String getString(String name)
  {
    return getString(findIndex(name));
  }
  
  /**
   * Returns value of String field with specified index.
   */
  public String getString(int index)
  {
    return (String) getValue(index);
  }
  
  /**
   * Returns value of Integer field with specified name.
   */
  public Integer getInt(String name)
  {
    return getInt(findIndex(name));
  }
  
  /**
   * Returns value of Integer field with specified index.
   */
  public Integer getInt(int index)
  {
    return (Integer) getValue(index);
  }
  
  /**
   * Returns value of Boolean field with specified name.
   */
  public Boolean getBoolean(String name)
  {
    return getBoolean(findIndex(name));
  }
  
  /**
   * Returns value of Boolean field with specified index.
   */
  public Boolean getBoolean(int index)
  {
    return (Boolean) getValue(index);
  }
  
  /**
   * Returns value of Long field with specified name.
   */
  public Long getLong(String name)
  {
    return getLong(findIndex(name));
  }
  
  /**
   * Returns value of Long field with specified index.
   */
  public Long getLong(int index)
  {
    return (Long) getValue(index);
  }
  
  /**
   * Returns value of Float field with specified name.
   */
  public Float getFloat(String name)
  {
    return getFloat(findIndex(name));
  }
  
  /**
   * Returns value of Float field with specified index.
   */
  public Float getFloat(int index)
  {
    return (Float) getValue(index);
  }
  
  /**
   * Returns value of Double field with specified name.
   */
  public Double getDouble(String name)
  {
    return getDouble(findIndex(name));
  }
  
  /**
   * Returns value of Double field with specified index.
   */
  public Double getDouble(int index)
  {
    return (Double) getValue(index);
  }
  
  /**
   * Returns value of Date field with specified name.
   */
  public Date getDate(String name)
  {
    return getDate(findIndex(name));
  }
  
  /**
   * Returns value of Date field with specified index.
   */
  public Date getDate(int index)
  {
    return (Date) getValue(index);
  }
  
  /**
   * Returns value of DataTable field with specified name.
   */
  public DataTable getDataTable(String name)
  {
    return getDataTable(findIndex(name));
  }
  
  /**
   * Returns value of DataTable field with specified index.
   */
  public DataTable getDataTable(int index)
  {
    return (DataTable) getValue(index);
  }
  
  /**
   * Returns value of Color field with specified name.
   */
  public Color getColor(String name)
  {
    return getColor(findIndex(name));
  }
  
  /**
   * Returns value of Color field with specified index.
   */
  public Color getColor(int index)
  {
    return (Color) getValue(index);
  }
  
  /**
   * Returns value of Data field with specified name.
   */
  public Data getData(String name)
  {
    return getData(findIndex(name));
  }
  
  /**
   * Returns value of Data field with specified index.
   */
  public Data getData(int index)
  {
    return (Data) getValue(index);
  }
  
  /**
   * Returns value of field with specified index.
   */
  public Object getValue(int index)
  {
    FieldFormat ff = format.getField(index);
    
    return getValue(ff);
  }
  
  /**
   * Returns value of field with specified name.
   */
  public Object getValue(String name)
  {
    return getValue(findIndex(name));
  }
  
  private Object getValue(FieldFormat ff)
  {
    if (data.containsKey(ff.getName()))
    {
      return data.get(ff.getName());
    }
    
    if (ff.isDefaultOverride())
    {
      return null;
    }
    
    return isTableImmutable() ? ff.getDefaultValue() : ff.getDefaultValueCopy();
  }
  
  /**
   * Returns textual description of field value. The description is taken from selection values list specified in table format.
   */
  public Object getValueDescription(String name)
  {
    Object value = getValue(name);
    
    final FieldFormat ff = getFormat(name);
    
    Map<Object, String> sv = ff.getSelectionValues();
    
    String description = sv != null ? sv.get(value) : null;
    
    return description != null ? description : ff.valueToString(value);
  }
  
  public String getValueAsString(String name)
  {
    return getValueAsString(findIndex(name));
  }
  
  public String getValueAsString(int index)
  {
    return format.getField(index).valueToString(getValue(index));
  }
  
  public DataRecord setId(String id)
  {
    ensureMutable();
    
    this.id = id;
    
    return this;
  }
  
  protected void setTable(DataTable table)
  {
    this.table = table;
  }
  
  void setFormat(TableFormat format)
  {
    format.makeImmutable(null);
    this.format = format;
  }
  
  @Override
  public boolean equals(Object obj)
  {
    if (obj == this)
    {
      return true;
    }
    
    if (!(obj instanceof DataRecord))
    {
      return false;
    }
    
    DataRecord rec = (DataRecord) obj;
    
    if (!Util.equals(getId(), rec.getId()))
    {
      return false;
    }
    
    // Formats are compared only if record does not belong to a table
    if (table == null)
    {
      if (!format.equals(rec.getFormat()))
      {
        return false;
      }
    }
    
    for (int i = 0; i < getFieldCount(); i++)
    {
      Object field = getValue(i);
      Object value = rec.getValue(i);
      if (field != null ? !field.equals(value) : value != null)
      {
        return false;
      }
    }
    
    return true;
  }
  
  @Override
  public int hashCode()
  {
    int result = 1;
    result = 31 * result + (format != null ? format.hashCode() : 0);
    result = 31 * result + (id != null ? id.hashCode() : 0);
    for (int i = 0; i < getFieldCount(); i++)
    {
      Object field = getValue(i);
      result = 31 * result + (field != null ? field.hashCode() : 0);
    }
    return result;
  }
  
  /**
   * Returns true if record has field with specified name.
   */
  public boolean hasField(String name)
  {
    return getFormat().hasField(name);
  }
  
  protected boolean meetToCondition(QueryCondition condition)
  {
    if (hasField(condition.getField()))
    {
      Object recValue = getValue(condition.getField());
      Object condValue = condition.getValue();
      int operator = condition.getOperator();
      
      if (recValue == null || condValue == null)
      {
        if (operator == QueryCondition.EQ)
        {
          return condValue == recValue;
        }
        
        if (operator == QueryCondition.NE)
        {
          return condValue != recValue;
        }
        
        throw new IllegalArgumentException("Can't compare value to NULL");
      }
      
      if (operator == QueryCondition.NE)
      {
        return !recValue.equals(condValue);
      }
      
      if ((operator & QueryCondition.EQ) > 0)
      {
        if (recValue.equals(condValue))
        {
          return true;
        }
        
        if (operator == QueryCondition.EQ)
        {
          return false;
        }
      }
      
      if (!(recValue instanceof Comparable))
      {
        throw new IllegalArgumentException("Value isn't comparable: " + recValue);
      }
      
      Comparable compRecValue = (Comparable) recValue;
      
      if ((operator & QueryCondition.GT) > 0)
      {
        return compRecValue.compareTo(condValue) > 0;
      }
      
      if ((operator & QueryCondition.LT) > 0)
      {
        return compRecValue.compareTo(condValue) < 0;
      }
      
      throw new IllegalArgumentException("Illegal operator: " + operator);
    }
    
    return false;
  }
  
  public void cloneFormatFromTable()
  {
    if (table != null)
    {
      format = table.getFormat().clone();
    }
    else
    {
      format = format.clone();
    }
  }
  
  public String dataAsString(boolean showFieldNames, boolean showHiddenFields)
  {
    return dataAsString(showFieldNames, showHiddenFields, true);
  }
  
  public String dataAsString(boolean showFieldNames, boolean showHiddenFields, boolean showPasswords)
  {
    // Оптимизация: используем StringBuilder вместо StringBuffer
    // StringBuffer устарел и синхронизирован, StringBuilder быстрее в однопоточном коде
    StringBuilder res = new StringBuilder();
    
    boolean needSeparator = false;
    
    for (FieldFormat ff : getFormat())
    {
      if (ff.isHidden() && !showHiddenFields)
      {
        continue;
      }
      
      if (needSeparator)
      {
        res.append(", ");
      }
      else
      {
        needSeparator = true;
      }
      
      String value = valueAsString(ff.getName(), showFieldNames, showHiddenFields, showPasswords);
      
      if (StringFieldFormat.EDITOR_PASSWORD.equals(ff.getEditor()) && !showPasswords)
      {
        value = StringUtils.createMaskedPasswordString(value.length());
      }
      res.append((showFieldNames ? ff.toString() + "=" : "") + value);
    }
    
    return res.toString();
  }
  
  public String valueAsString(String name)
  {
    return valueAsString(name, true, false, true);
  }
  
  public String valueAsString(String name, boolean showFieldNames, boolean showHiddenFields, boolean showPasswords)
  {
    FieldFormat ff = getFormat(name);
    
    Object val = getValue(name);
    
    String value = val != null
        ? (FieldFormat.DATATABLE_FIELD == ff.getType() && !((DataTable) val).isSimple()) ? ((DataTable) val).dataAsString(showFieldNames, showHiddenFields, showPasswords) : val.toString()
        : "NULL";
    
    if (ff.hasSelectionValues())
    {
      Object sv = ff.getSelectionValues().get(val);
      value = sv != null ? sv.toString() : value;
    }
    
    return value;
  }
  
  @Override
  public String toString()
  {
    return dataAsString(true, true);
  }
  
  /**
   * Constructs new <code>DataTable</code> and adds this record to it.
   */
  public DataTable wrap()
  {
    return new SimpleDataTable(this);
  }
  
  @Override
  public DataRecord clone()
  {
    DataRecord cl = null;
    
    try
    {
      cl = (DataRecord) super.clone();
    }
    catch (CloneNotSupportedException ex)
    {
      throw new IllegalStateException(ex.getMessage(), ex);
    }
    
    cl.data = (Map) CloneUtils.deepClone(data);
    
    return cl;
  }
  
  private void ensureMutable()
  {
    if (isTableImmutable())
    {
      if (Log.DATATABLE.isDebugEnabled())
      {
        Log.DATATABLE.warn("Attempt to change immutable record", new IllegalStateException());
      }
      else
      {
        Log.DATATABLE.warn("Attempt to change immutable record");
      }
      throw new IllegalStateException("Immutable");
    }
  }
  
  private boolean isTableImmutable()
  {
    return table != null && table.isImmutable();
  }
}
