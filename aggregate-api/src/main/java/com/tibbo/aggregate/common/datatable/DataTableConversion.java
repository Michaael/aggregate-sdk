package com.tibbo.aggregate.common.datatable;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.Nullable;

import org.apache.commons.beanutils.ConstructorUtils;
import org.apache.commons.beanutils.PropertyUtils;

import com.tibbo.aggregate.common.Log;
import com.tibbo.aggregate.common.datatable.converter.FCByte;
import com.tibbo.aggregate.common.datatable.converter.FCComparable;
import com.tibbo.aggregate.common.datatable.converter.FCDouble;
import com.tibbo.aggregate.common.datatable.converter.FCNumber;
import com.tibbo.aggregate.common.datatable.converter.FCShort;
import com.tibbo.aggregate.common.datatable.converter.FCSimpleBoolean;
import com.tibbo.aggregate.common.datatable.converter.FCSimpleByte;
import com.tibbo.aggregate.common.datatable.converter.FCSimpleDouble;
import com.tibbo.aggregate.common.datatable.converter.FCSimpleFloat;
import com.tibbo.aggregate.common.datatable.converter.FCSimpleInteger;
import com.tibbo.aggregate.common.datatable.converter.FCSimpleLong;
import com.tibbo.aggregate.common.datatable.converter.FCSimpleShort;
import com.tibbo.aggregate.common.datatable.converter.FormatConverter;
import com.tibbo.aggregate.common.datatable.converter.SimplePropertyFormatConverter;
import com.tibbo.aggregate.common.structure.Pinpoint;
import com.tibbo.aggregate.common.structure.PinpointAware;
import com.tibbo.aggregate.common.util.Util;

public class DataTableConversion
{
  static final List<FormatConverter> FORMAT_CONVERTERS = new LinkedList();
  static final ReentrantReadWriteLock FORMAT_CONVERTERS_LOCK = new ReentrantReadWriteLock();
  
  static
  {
    // Base classes must go after classes derived from them
    
    DataTableConversion.registerFormatConverter(new FCByte());
    DataTableConversion.registerFormatConverter(new FCDouble());
    DataTableConversion.registerFormatConverter(new FCShort());
    DataTableConversion.registerFormatConverter(new FCSimpleBoolean());
    DataTableConversion.registerFormatConverter(new FCSimpleByte());
    DataTableConversion.registerFormatConverter(new FCSimpleDouble());
    DataTableConversion.registerFormatConverter(new FCSimpleFloat());
    DataTableConversion.registerFormatConverter(new FCSimpleInteger());
    DataTableConversion.registerFormatConverter(new FCSimpleLong());
    DataTableConversion.registerFormatConverter(new FCSimpleShort());
    DataTableConversion.registerFormatConverter(new FCNumber());
    DataTableConversion.registerFormatConverter(new SimplePropertyFormatConverter(Calendar.class, "time"));
    DataTableConversion.registerFormatConverter(new FCComparable());
    
    DataTableConversion.registerFormatConverter(new FCBigDecimal());
    DataTableConversion.registerFormatConverter(new FCBigInteger());
  }
  
  public static DataTable beanToTable(Object bean, TableFormat format, boolean setReadOnlyFields, boolean ignoreErrors) throws DataTableException
  {
    return beanToRecord(bean, format, setReadOnlyFields, ignoreErrors).wrap();
  }
  
  public static DataTable beanToTable(Object bean, TableFormat format) throws DataTableException
  {
    return beanToTable(bean, format, true, false);
  }
  
  public static DataTable beanToTable(Object bean, TableFormat format, boolean setReadOnlyFields) throws DataTableException
  {
    return beanToTable(bean, format, setReadOnlyFields, false);
  }
  
  public static DataTable beansToTable(Collection beans, TableFormat format) throws DataTableException
  {
    return beansToTable(beans, format, true);
  }
  
  public static DataTable beansToTable(Collection beans, TableFormat format, boolean setReadOnlyFields) throws DataTableException
  {
    DataTable table = new SimpleDataTable(format);
    
    for (Iterator iter = beans.iterator(); iter.hasNext();)
    {
      Object bean = iter.next();
      table.addRecord(beanToRecord(bean, format, setReadOnlyFields, false));
    }
    
    return table;
  }
  
  public static <C> C beanFromTable(DataTable table, Class<C> beanClass, TableFormat format, boolean setReadOnlyFields) throws DataTableException
  {
    List<C> list = beansFromTable(table, beanClass, format, setReadOnlyFields);
    
    if (list.size() > 1)
    {
      Log.DATATABLE.warn("More than one bean generated from data table, returning the first one only", new Exception());
    }
    
    return list.get(0);
  }
  
  public static <C> List<C> beansFromTable(DataTable table, Class<C> beanClass, TableFormat format) throws DataTableException
  {
    return beansFromTable(table, beanClass, format, true);
  }

  public static <C> List<C> beansFromTable(DataTable table, Class<C> beanClass, TableFormat format,
      boolean setReadOnlyFields) throws DataTableException
  {
    return beansFromTable(table, beanClass, format, setReadOnlyFields, null);
  }

  public static <C> List<C> beansFromTable(DataTable table, Class<C> beanClass, TableFormat format, boolean setReadOnlyFields,
      @Nullable Pinpoint origin) throws DataTableException
  {
    try
    {
      List<C> res = new LinkedList<>();
      
      if (table == null)
      {
        return res;
      }
      
      int i = 0;
      for (DataRecord rec : table)
      {
        if (FieldFormat.isFieldClass(beanClass))
        {
          res.add((C) rec.getValue(0));
          continue;
        }
        
        C bean = beanFromRecord(rec, beanClass, format, setReadOnlyFields);

        if (origin != null && PinpointAware.class.isAssignableFrom(beanClass))
        {
          PinpointAware pinpointAware = (PinpointAware) bean;
          pinpointAware.assignPinpoint(origin.withOriginRow(i++));
        }
        
        res.add(bean);
      }
      
      return res;
    }
    catch (Exception ex)
    {
      throw new DataTableException("Error converting data table to the list of beans of type '" + beanClass.getName() + "': " + ex.getMessage(), ex);
    }
  }
  
  public static <C> C beanFromRecord(DataRecord rec, Class<C> beanClass, TableFormat format, boolean setReadOnlyFields) throws DataTableException
  {
    try
    {
      C bean;
      FormatConverter<C> fc = getFormatConverter(beanClass);
      if (fc == null)
      {
        Constructor<C> recordConstructor = ConstructorUtils.getAccessibleConstructor(beanClass, DataRecord.class);
        if (recordConstructor != null)
        {
          bean = recordConstructor.newInstance(rec);
        }
        else
        {
          bean = beanClass.newInstance();
          populateBeanFromRecord(bean, rec, format, setReadOnlyFields);
        }
      }
      else
      {
        bean = fc.instantiate(rec);
      }
      return bean;
    }
    catch (Exception ex)
    {
      throw new DataTableException("Error converting data record to the bean of type '" + beanClass.getName() + "': " + ex.getMessage(), ex);
    }
  }
  
  public static void populateBeanFromRecord(Object bean, DataRecord rec, TableFormat format, boolean setReadOnlyFields) throws DataTableException
  {
    populateBeanFromRecord(bean, rec, format, setReadOnlyFields, new LinkedHashSet<String>(0));
  }
  
  public static void populateBeanFromRecord(Object bean, DataRecord rec, TableFormat format, boolean setReadOnlyFields, Set<String> fieldsToSkip) throws DataTableException
  {
    try
    {
      for (FieldFormat ff : format)
      {
        if (fieldsToSkip.contains(ff.getName()))
        {
          continue;
        }
        
        if (ff.isReadonly() && !setReadOnlyFields)
        {
          continue;
        }
        
        Object value;
        if (rec.getFormat().hasField(ff.getName()))
        {
          value = rec.getValue(ff.getName());
        }
        else
        {
          value = ff.getDefaultValueCopy();
        }
        
        try
        {
          if (value != null)
          {
            Class requiredClass = PropertyUtils.getPropertyType(bean, ff.getName());
            
            if (requiredClass == null)
            {
              Log.DATATABLE.debug("No method to set property '" + ff.getName() + "' (" + ff.getFieldWrappedClass() + ") in object of type '" + bean.getClass().getName() + "'");
              continue;
            }
            
            if (List.class.isAssignableFrom(requiredClass))
            {
              value = createList(bean, rec, setReadOnlyFields, ff);
              
              if (value == null)
              {
                continue;
              }
            }
            else
            {
              FormatConverter fc = DataTableConversion.getFormatConverter(requiredClass);
              
              if (fc != null)
              {
                Object originalValue = PropertyUtils.getProperty(bean, ff.getName());
                
                value = fc.convertToBean(value, originalValue);
              }
              else if (AggreGateBean.class.isAssignableFrom(requiredClass) && (value instanceof DataTable))
              {
                value = createAggreGateBean(value, requiredClass);
              }
              
              if (requiredClass.isArray())
              {
                value = createArray(ff, value);
              }
            }
          }
          
          PropertyUtils.setProperty(bean, ff.getName(), value);
        }
        catch (InvocationTargetException ex1)
        {
          throw new DataTableException("Error setting property '" + ff.getName() + "' to '" + value + "' (" + (value != null ? value.getClass().getName() : "null") + "): " + ex1.getCause()
              .getMessage(), ex1.getCause());
        }
        catch (Exception ex1)
        {
          throw new DataTableException("Error setting property '" + ff.getName() + "' to '" + value + "' (" + (value != null ? value.getClass().getName() : "null") + "): " + ex1.getMessage(), ex1);
        }
      }
    }
    catch (Exception ex)
    {
      throw new DataTableException("Error populating bean of type '" + bean.getClass().getName() + "' from data table: " + ex.getMessage(), ex);
    }
  }
  
  public static DataRecord beanToRecord(Object bean, TableFormat format, boolean setReadOnlyFields, boolean ignoreErrors) throws DataTableException
  {
    return DataTableConversion.beanToRecord(bean, format, setReadOnlyFields, ignoreErrors, new LinkedHashSet<String>(0));
  }
  
  public static DataRecord beanToRecord(Object bean, TableFormat format, boolean setReadOnlyFields, boolean ignoreErrors, Set<String> fieldsToSkip) throws DataTableException
  {
    try
    {
      if (format == null)
      {
        throw new IllegalStateException("No format found for " + bean.getClass());
      }
      
      DataRecord rec = new DataRecord(format.isImmutable() ? format.clone() : format);
      
      if (bean != null && FieldFormat.isFieldClass(bean.getClass()))
      {
        rec.addValue(bean);
        return rec;
      }
      
      for (FieldFormat ff : format)
      {
        if (fieldsToSkip.contains(ff.getName()) || (!setReadOnlyFields && ff.isReadonly()))
        {
          continue;
        }
        
        Object value = null;
        try
        {
          if (bean != null)
          {
            value = PropertyUtils.getProperty(bean, ff.getName());
          }
        }
        catch (NoSuchMethodException ex3)
        {
          Log.DATATABLE.debug(MessageFormat.format("Error getting property ''{0}'' ({1}) from object of type ''{2}'': {3}", ff.getName(), ff.getFieldWrappedClass(), bean != null ? bean.getClass()
              .getName() : "null", ex3.getMessage()));
          continue;
        }
        catch (Exception ex3)
        {
          if (!ignoreErrors)
          {
            Log.DATATABLE.warn(
                MessageFormat.format("Error getting property ''{0}'' ({1}) from object of type ''{2}''", ff.getName(), ff.getFieldWrappedClass(), bean != null ? bean.getClass().getName() : "null"),
                ex3);
          }
          continue;
        }
        
        FormatConverter fc = bean == null ? null : DataTableConversion.getFormatConverter(PropertyUtils.getPropertyType(bean, ff.getName()));
        
        if (fc != null)
        {
          value = fc.convertToTable(value, DataTableConversion.getFormatFromDefaultValue(ff));
        }
        
        if (value != null)
        {
          if (value instanceof List)
          {
            value = convertList(bean, value, setReadOnlyFields, ff);
            
            if (value == null)
            {
              continue;
            }
          }
          else if (value.getClass().isArray())
          {
            value = convertArray(ff, value);
          }
          else if (value instanceof AggreGateBean)
          {
            value = ((AggreGateBean) value).toDataTable();
          }
        }
        
        try
        {
          rec.setValue(ff.getName(), value);
        }
        catch (Exception ex)
        {
          if (!ignoreErrors)
          {
            throw ex;
          }
        }
      }
      
      return rec;
    }
    catch (Exception ex2)
    {
      throw new DataTableException("Error converting bean of type '" + bean == null ? null : bean.getClass().getName() + "' to data record: " + ex2.getMessage(), ex2);
    }
  }
  
  private static TableFormat getFormatFromDefaultValue(FieldFormat ff)
  {
    TableFormat frmt = null;
    if (ff.getDefaultValue() instanceof DataTable)
    {
      frmt = ((DataTable) ff.getDefaultValue()).getFormat();
    }
    return frmt;
  }
  
  public static Object convertValueToField(FieldFormat ff, Object value, Class requiredClass)
  {
    if (value == null)
    {
      return null;
    }
    
    if (ff.getFieldWrappedClass().isAssignableFrom(value.getClass()))
    {
      return value;
    }
    else
    {
      FormatConverter<Object> fc = DataTableConversion.getFormatConverter(requiredClass);
      
      if (fc != null)
      {
        return fc.convertToTable(value, getFormatFromDefaultValue(ff));
      }
      
      return ff.valueFromString(value.toString());
    }
  }
  
  public static Object convertValueFromField(Object value)
  {
    return DataTableConversion.convertValueFromField(value, value.getClass());
  }
  
  public static Object convertValueFromField(Object value, Class requiredClass)
  {
    if (value == null)
    {
      return null;
    }
    
    FormatConverter fc = DataTableConversion.getFormatConverter(requiredClass);
    
    if (fc != null)
    {
      return fc.convertToBean(value, null);
    }
    
    return value;
  }
  
  public static Object convertValueToField(FieldFormat ff, Object value)
  {
    if (value == null)
    {
      return null;
    }
    return convertValueToField(ff, value, value.getClass());
  }
  
  public static void registerFormatConverter(FormatConverter converter)
  {
    FORMAT_CONVERTERS_LOCK.writeLock().lock();
    try
    {
      int i = 0;
      for (FormatConverter fc : DataTableConversion.FORMAT_CONVERTERS)
      {
        if (fc.getValueClass().isAssignableFrom(converter.getValueClass()))
        {
          break;
        }
        i++;
      }
      DataTableConversion.FORMAT_CONVERTERS.add(i, converter);
    }
    finally
    {
      FORMAT_CONVERTERS_LOCK.writeLock().unlock();
    }
  }
  
  public static <C> FormatConverter<C> getFormatConverter(Class<C> valueClass)
  {
    if (valueClass == null)
    {
      return null;
    }
    
    if (AggreGateBean.class.isAssignableFrom(valueClass))
    {
      return null; // AggreGateBean's cannot have additional converters
    }
    
    if (FieldFormat.isFieldClass(valueClass))
    {
      return null;
    }
    
    FORMAT_CONVERTERS_LOCK.readLock().lock();
    try
    {
      for (FormatConverter fc : DataTableConversion.FORMAT_CONVERTERS)
      {
        if (fc.getValueClass().isAssignableFrom(valueClass))
        {
          return fc;
        }
      }
    }
    finally
    {
      FORMAT_CONVERTERS_LOCK.readLock().unlock();
    }
    
    return null;
  }
  
  public static <S> FieldFormat<S> createFieldFormat(String name, Object value)
  {
    FieldFormat fieldFormat = FieldFormat.create(name, FieldFormat.STRING_FIELD);
    if (value == null)
    {
      fieldFormat.setNullable(true);
    }
    else
    {
      try
      {
        fieldFormat = createFieldFormat(name, value.getClass());
      }
      catch (Exception ex)
      {
        Log.DATATABLE.debug("Error constructing field format for value", ex);
      }
    }
    return fieldFormat;
  }
  
  public static <S> FieldFormat<S> createFieldFormat(String name, String valueClassName) throws ClassNotFoundException
  {
    FORMAT_CONVERTERS_LOCK.readLock().lock();
    try
    {
      for (FormatConverter converter : FORMAT_CONVERTERS)
      {
        if (valueClassName.equals(converter.getValueClass().getSimpleName()))
        {
          return converter.createFieldFormat(name);
        }
      }
    }
    finally
    {
      FORMAT_CONVERTERS_LOCK.readLock().unlock();
    }
    
    return FieldFormat.create(name, Class.forName(valueClassName));
  }
  
  public static <S> FieldFormat<S> createFieldFormat(String name, Class valueClass)
  {
    FORMAT_CONVERTERS_LOCK.readLock().lock();
    try
    {
      for (FormatConverter converter : FORMAT_CONVERTERS)
      {
        if (valueClass == converter.getValueClass())
        {
          return converter.createFieldFormat(name);
        }
      }
    }
    finally
    {
      FORMAT_CONVERTERS_LOCK.readLock().unlock();
    }
    
    return FieldFormat.create(name, valueClass);
  }
  
  public static FieldFormat createTableField(String name, TableFormat format)
  {
    FieldFormat ff = FieldFormat.create(name, FieldFormat.DATATABLE_FIELD);
    ff.setDefault(new SimpleDataTable(format, true));
    return ff;
  }
  
  public static FieldFormat createTableField(String name, String description, TableFormat format)
  {
    FieldFormat ff = FieldFormat.create(name, FieldFormat.DATATABLE_FIELD, description);
    ff.setDefault(new SimpleDataTable(format, true));
    return ff;
  }
  
  public static Object createAggreGateBean(Object value, Class requiredClass) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException
  {
    DataTable table = (DataTable) value;
    
    return ConstructorUtils.invokeConstructor(requiredClass, new Object[] { table.rec() });
  }
  
  private static Object createArray(FieldFormat ff, Object value)
  {
    if (ff.getType() != FieldFormat.DATATABLE_FIELD)
    {
      throw new IllegalStateException("Field '" + ff.getName() + "' is not a data table, it cannot store array: " + value);
    }
    
    DataTable table = (DataTable) value;
    
    value = Array.newInstance(ff.getFieldClass(), table.getRecordCount());
    
    for (int i = 0; i < table.getRecordCount(); i++)
    {
      Array.set(value, i, table.getRecord(i).getValue(0));
    }
    return value;
  }
  
  private static List createList(Object bean, DataRecord rec, boolean setReadOnlyFields, FieldFormat ff) throws NoSuchFieldException, DataTableException
  {
    Field field = bean.getClass().getDeclaredField(ff.getName());
    
    Class listElementClass = Util.getListElementType(field.getGenericType());
    
    if (listElementClass == null)
    {
      Log.DATATABLE.error("Cannot determine list element class for " + field.getGenericType());
      return null;
    }
    
    if (rec.getFormat(ff.getName()).getType() != FieldFormat.DATATABLE_FIELD)
    {
      Log.DATATABLE.error("Not a data table field " + ff.getName());
      return null;
    }
    
    DataTable data = rec.getDataTable(ff.getName());
    
    return beansFromTable(data, listElementClass, data.getFormat(), setReadOnlyFields);
  }
  
  private static DataTable convertList(Object bean, Object value, boolean setReadOnlyFields, FieldFormat ff) throws NoSuchFieldException, InstantiationException, IllegalAccessException,
      DataTableException
  {
    if (ff.getType() != FieldFormat.DATATABLE_FIELD)
    {
      Log.DATATABLE.error("Not a data table field " + ff.getName());
      return null;
    }
    
    DataTable dt = (DataTable) ff.getDefaultValue();
    
    TableFormat fieldFormat;
    
    if (dt == null)
    {
      // If field doesn't have a default value, trying to determine table format by treating list as list of AggreGateBean's
      
      List list = (List) value;
      AggreGateBean agb;
      
      if (list.size() > 0)
      {
        // List has element(s), using format of first element
        
        Object firstElement = list.get(0);
        
        if ((firstElement == null) || !(firstElement instanceof AggreGateBean))
        {
          Log.DATATABLE.error("Value is not AggreGateBean: " + firstElement);
          return null;
        }
        
        agb = (AggreGateBean) firstElement;
      }
      else
      {
        // List has no elements trying to get element type and format using generics reflection
        
        Field field = bean.getClass().getDeclaredField(ff.getName());
        
        Class listElementClass = Util.getListElementType(field.getGenericType());
        
        if (listElementClass == null)
        {
          Log.DATATABLE.error("Cannot determine list element class for " + field.getGenericType());
          return null;
        }
        
        if (!AggreGateBean.class.isAssignableFrom(listElementClass))
        {
          Log.DATATABLE.error("Field " + ff + " is not AggreGateBean list: " + listElementClass);
          return null;
        }
        
        agb = (AggreGateBean) listElementClass.newInstance();
      }
      
      fieldFormat = agb.getFormat();
    }
    else
    {
      fieldFormat = dt.getFormat();
    }
    
    return beansToTable((List) value, fieldFormat, setReadOnlyFields);
  }
  
  private static DataTable convertArray(FieldFormat ff, Object value)
  {
    if (ff.getType() != FieldFormat.DATATABLE_FIELD)
    {
      throw new IllegalStateException("Field '" + ff.getName() + "' is not a data table, it cannot store array: " + value);
    }
    
    DataTable tbl = (DataTable) ff.getDefaultValue();
    
    if (tbl == null)
    {
      tbl = new SimpleDataTable(new TableFormat(DataTableConversion.createFieldFormat(ff.getName(), value.getClass().getComponentType())));
    }
    tbl = tbl.cloneIfImmutable();
    for (int i = 0; i < Array.getLength(value); i++)
    {
      tbl.addRecord().addValue(i);
    }
    
    return tbl;
  }

  public static Object[] toObjects(DataRecord rec)
  {
    if (rec == null || rec.getFieldCount() == 0)
    {
      return null;
    }

    Object[] data = new Object[rec.getFieldCount()];
    int fieldIndex = 0;
    for (FieldFormat field : rec.getFormat().getFields())
    {
      data[fieldIndex++] = rec.getValue(field.getName());
    }
    return data;
  }
}
