package com.tibbo.aggregate.common.datatable.converter;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import com.tibbo.aggregate.common.datatable.DataRecord;
import com.tibbo.aggregate.common.datatable.DataTable;
import com.tibbo.aggregate.common.datatable.DataTableConversion;
import com.tibbo.aggregate.common.datatable.DataTableException;
import com.tibbo.aggregate.common.datatable.FieldFormat;
import com.tibbo.aggregate.common.datatable.SimpleDataTable;
import com.tibbo.aggregate.common.datatable.TableFormat;

public class DefaultFormatConverter<T> extends AbstractFormatConverter<T>
{
  private final Set<String> constructorArguments = new LinkedHashSet();
  
  public DefaultFormatConverter(Class valueClass, TableFormat format)
  {
    super(valueClass, format);
  }
  
  public T convertToBean(Object value, T originalValue)
  {
    try
    {
      final DataRecord rec = ((DataTable) value).rec();
      if (rec.hasField(VF_IS_NULL) && rec.getBoolean(VF_IS_NULL))
        return null;
      
      if (originalValue != null)
      {
        Object bean = clone(originalValue, false);
        if (bean == null)
        {
          bean = instantiate(rec);
        }
        
        DataTableConversion.populateBeanFromRecord(bean, rec, getFormat(), true, FIELDS_TO_SKIP);
        return (T) bean;
      }
      else
      {
        if (constructorArguments.size() == 0)
        {
          return DataTableConversion.beanFromTable((DataTable) value, getValueClass(), getFormat(), true);
        }
        else
        {
          Object bean = instantiate(rec);
          DataTableConversion.populateBeanFromRecord(bean, rec, getFormat(), true);
          return (T) bean;
        }
      }
    }
    catch (Exception ex)
    {
      throw new IllegalStateException(ex);
    }
  }
  
  public Object convertToTable(T value, TableFormat format)
  {
    try
    {
      if (value == null)
        if (format.hasField(VF_IS_NULL))
        {
          final DataTable dataTable = new SimpleDataTable(format, true);
          dataTable.rec().setValue(VF_IS_NULL, true);
          return dataTable;
        }
      
      return DataTableConversion.beanToRecord(value, getFormat(), true, false, new LinkedHashSet<String>(Arrays.asList(VF_IS_NULL))).wrap();
    }
    catch (DataTableException ex)
    {
      throw new IllegalStateException(ex);
    }
  }
  
  public FieldFormat createFieldFormat(String name)
  {
    return null;
  }
  
  public void addConstructorField(String field)
  {
    constructorArguments.add(field);
  }
  
  public T instantiate(DataRecord source) throws InstantiationException
  {
    try
    {
      if (constructorArguments.size() == 0)
      {
        try
        {
          T bean = getValueClass().newInstance();
          DataTableConversion.populateBeanFromRecord(bean, source, getFormat(), true);
          return bean;
        }
        catch (Exception ex)
        {
          throw new IllegalStateException(ex);
        }
      }
      else
      {
        Class[] types = new Class[constructorArguments.size()];
        Object[] parameters = new Object[constructorArguments.size()];
        
        int i = 0;
        for (String s : constructorArguments)
        {
          Object fv = source.getValue(s);
          
          types[i] = fv.getClass();
          parameters[i] = fv;
          i++;
          
        }
        
        Constructor<T> c = getValueClass().getConstructor(types);
        T bean = c.newInstance(parameters);
        DataTableConversion.populateBeanFromRecord(bean, source, getFormat(), true, constructorArguments);
        return bean;
      }
    }
    catch (InstantiationException ex)
    {
      throw ex;
    }
    catch (Exception ex)
    {
      throw new IllegalStateException(ex);
    }
  }
}
