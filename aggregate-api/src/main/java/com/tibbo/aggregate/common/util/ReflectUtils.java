package com.tibbo.aggregate.common.util;

import org.apache.log4j.Logger;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;

public class ReflectUtils
{
  public static Object getPrivateField(Object obj, String fieldName)
  {
    Class c = obj.getClass();
    while (c != Object.class)
      try
      {
        return getPrivateField(obj, fieldName, c);
      }
      catch (NoSuchFieldException e)
      {
        c = c.getSuperclass();
      }
      catch (Exception e)
      {
        throw new IllegalStateException(e);
      }
    return null;
  }
  
  public static Object getPrivateField(Object obj, String fieldName, Class c) throws NoSuchFieldException, IllegalAccessException
  {
    final Field field = c.getDeclaredField(fieldName);
    field.setAccessible(true);
    return field.get(obj);
  }
  
  public static void setPrivateField(Object obj, String fieldName, Object value)
  {
    Class c = obj.getClass();
    while (c != Object.class)
      try
      {
        setPrivateField(obj, fieldName, value, c);
        return;
      }
      catch (NoSuchFieldException e)
      {
        c = c.getSuperclass();
      }
      catch (Exception e)
      {
        throw new IllegalStateException(e);
      }
  }
  
  public static void setPrivateField(Object obj, String fieldName, Object value, Class c)
      throws NoSuchFieldException, IllegalAccessException
  {
    final Field field = c.getDeclaredField(fieldName);
    field.setAccessible(true);
    field.set(obj, value);
  }
  
  public static <F, T> void copyProperties(F from, Class<F> stopFromClass, T to, Class<T> stopToClass)
  {
    PropertyDescriptor[] fromProperties = new PropertyDescriptor[0];
    PropertyDescriptor[] toProperties = new PropertyDescriptor[0];
    
    try
    {
      fromProperties = Introspector.getBeanInfo(from.getClass(), stopFromClass).getPropertyDescriptors();
      toProperties = Introspector.getBeanInfo(to.getClass(), stopToClass).getPropertyDescriptors();
    }
    catch (IntrospectionException ignored)
    {
    }
    
    for (PropertyDescriptor fromPropDesc : fromProperties)
    {
      if (fromPropDesc.getReadMethod() != null)
        for (PropertyDescriptor toPropDesc : toProperties)
        {
          boolean propertyIsTheSame = fromPropDesc.getName().equals(toPropDesc.getName());
          if (propertyIsTheSame && toPropDesc.getWriteMethod() != null)
            try
            {
              
              Object value = fromPropDesc.getReadMethod().invoke(from);
              toPropDesc.getWriteMethod().invoke(to, value);
            }
            catch (Exception e)
            {
              Logger.getLogger(ReflectUtils.class).info("Copying property: " + fromPropDesc.getName() + ", from " + from + " to " + to + " caused an exception", e);
            }
        }
    }
  }
}
