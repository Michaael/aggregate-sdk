package com.tibbo.aggregate.common.datatable.converter;

import java.beans.*;
import java.lang.reflect.*;

import com.tibbo.aggregate.common.datatable.*;

public class SimplePropertyFormatConverter extends SimpleFormatConverter<Object>
{
  private final PropertyDescriptor propertyDescriptor;
  
  public SimplePropertyFormatConverter(Class valueClass, String field)
  {
    super(valueClass);

    try
    {
      final BeanInfo beanInfo = Introspector.getBeanInfo(valueClass);
      final PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
      for (PropertyDescriptor pd : propertyDescriptors)
      {
        if (field.equals(pd.getName()))
        {
          propertyDescriptor = pd;
          return;
        }
      }
      throw new IntrospectionException("Field '" + field + "' not found in class '" + valueClass.getSimpleName() + "'");
    }
    catch (IntrospectionException e)
    {
      throw new IllegalStateException(e.getMessage(), e);
      
    }
  }
  
  @Override
  public FieldFormat createFieldFormat(String name)
  {
    return DataTableConversion.createFieldFormat(name, propertyDescriptor.getPropertyType()).setNullable(true);
  }
  
  @Override
  public Object simpleToBean(Object value)
  {
    if (value == null)
    {
      return null;
    }
    
    try
    {
      if (propertyDescriptor.getWriteMethod() != null)
      {
        Object bean = getValueClass().newInstance();
        propertyDescriptor.getWriteMethod().invoke(bean, value);
        return bean;
      }
      else
      {
        final Constructor constructor = getValueClass().getConstructor(propertyDescriptor.getPropertyType());
        return constructor.newInstance(value);
      }
    }
    catch (InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e)
    {
      throw new IllegalStateException(e.getMessage(), e);
    }
  }
  
  @Override
  public Object convertToTable(Object value, TableFormat format)
  {
    if (value == null)
      return null;

    try
    {
      return propertyDescriptor.getReadMethod().invoke(value);
    }
    catch (InvocationTargetException | IllegalAccessException e)
    {
      throw new IllegalStateException(e.getMessage(), e);
    }
  }
}
