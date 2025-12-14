package com.tibbo.aggregate.common.util;

import java.awt.*;
import java.lang.reflect.*;
import java.util.*;

import com.tibbo.aggregate.common.*;
import com.tibbo.aggregate.common.datatable.*;
import com.tibbo.aggregate.common.datatable.converter.*;

/**
 * Утилитный класс для клонирования объектов различных типов.
 * <p>
 * Этот класс предоставляет методы для глубокого и поверхностного клонирования объектов,
 * включая поддержку коллекций, мапов, массивов и пользовательских типов.
 * 
 * <p><b>Поддерживаемые типы:</b>
 * <ul>
 *   <li>Примитивные типы и их обертки (String, Number, Boolean, Character)</li>
 *   <li>Объекты, реализующие {@link PublicCloneable}</li>
 *   <li>Коллекции (ArrayList, LinkedList, HashSet, TreeSet)</li>
 *   <li>Мапы (HashMap, LinkedHashMap, Hashtable)</li>
 *   <li>Массивы (Object[], примитивные массивы)</li>
 *   <li>Date, Color и другие стандартные типы</li>
 * </ul>
 * 
 * <p><b>Примеры использования:</b>
 * <pre>{@code
 * // Клонирование объекта
 * Object clone = CloneUtils.genericClone(original);
 * 
 * // Глубокое клонирование
 * Object deepClone = CloneUtils.deepClone(original);
 * 
 * // Клонирование коллекции
 * List<String> clonedList = (List<String>) CloneUtils.genericClone(originalList);
 * }</pre>
 *
 * @author AggreGate SDK Team
 * @version 1.3.7
 * @since 1.0
 */
public class CloneUtils
{
  public static Object genericClone(Object object)
  {
    if (object == null)
    {
      return null;
    }
    
    if (Object.class == object.getClass() || object instanceof String || object instanceof Number || object instanceof Boolean || object instanceof Character || object instanceof Throwable)
    {
      return object;
    }
    
    if (object instanceof PublicCloneable)
    {
      return ((PublicCloneable) object).clone();
    }
    
    if (object instanceof Date)
    {
      return ((Date) object).clone();
    }
    
    if (object instanceof Color)
    {
      Color c = (Color) object;
      return new Color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha());
    }
    
    if (object instanceof ArrayList)
    {
      @SuppressWarnings("unchecked")
      ArrayList<?> list = (ArrayList<?>) object;
      return (ArrayList<?>) list.clone();
    }
    
    if (object instanceof LinkedList)
    {
      @SuppressWarnings("unchecked")
      LinkedList<?> list = (LinkedList<?>) object;
      return (LinkedList<?>) list.clone();
    }
    
    if (object instanceof HashMap)
    {
      @SuppressWarnings("unchecked")
      HashMap<?, ?> map = (HashMap<?, ?>) object;
      return (HashMap<?, ?>) map.clone();
    }
    
    if (object instanceof Hashtable)
    {
      @SuppressWarnings("unchecked")
      Hashtable<?, ?> table = (Hashtable<?, ?>) object;
      return (Hashtable<?, ?>) table.clone();
    }
    
    FormatConverter converter = DataTableConversion.getFormatConverter(object.getClass());
    if (converter != null)
    {
      return converter.clone(object, true);
    }
    
    if (!(object instanceof Cloneable))
    {
      throw new IllegalStateException("Object is not cloneable: " + object + " (" + object.getClass().getName() + ")");
    }
    
    try
    {
      if (Log.CORE.isDebugEnabled())
      {
        Log.CORE.debug("Using slow reflection cloning for: " + object.getClass().getName(), new Exception());
      }
      
      Method method = object.getClass().getMethod("clone", (Class[]) null);
      method.setAccessible(true);
      return method.invoke(object, (Object[]) null);
    }
    catch (Exception e)
    {
      throw new IllegalStateException(e.getMessage(), e);
    }
  }
  
  /**
   * Try to create a deep clone of the provides object. This handles arrays, collections and maps. If the class in not a supported standard JDK collection type the <code>genericClone</code> will be
   * used instead.
   * 
   * @param object
   *          The object to be copied.
   */
  public static Object deepClone(Object object)
  {
    if (null == object)
    {
      return null;
    }
    
    String classname = object.getClass().getName();
    
    // check if it's an array
    if ('[' == classname.charAt(0))
    {
      // handle 1 dimensional primitive arrays
      if (classname.charAt(1) != '[' && classname.charAt(1) != 'L')
      {
        switch (classname.charAt(1))
        {
          case 'B':
            return ((byte[]) object).clone();
          case 'Z':
            return ((boolean[]) object).clone();
          case 'C':
            return ((char[]) object).clone();
          case 'S':
            return ((short[]) object).clone();
          case 'I':
            return ((int[]) object).clone();
          case 'J':
            return ((long[]) object).clone();
          case 'F':
            return ((float[]) object).clone();
          case 'D':
            return ((double[]) object).clone();
          default:
            throw new IllegalStateException("Unknown primitive array class: " + classname);
        }
      }
      
      // get the base type and the dimension count of the array
      int dimension_count = 1;
      while (classname.charAt(dimension_count) == '[')
      {
        dimension_count += 1;
      }
      Class<?> baseClass = null;
      if (classname.charAt(dimension_count) != 'L')
      {
        baseClass = getBaseClass(object);
      }
      else
      {
        try
        {
          baseClass = Class.forName(classname.substring(dimension_count + 1, classname.length() - 1));
        }
        catch (ClassNotFoundException e)
        {
          throw new IllegalStateException(e.getMessage(), e);
        }
      }
      
      // instantiate the array but make all but the first dimension 0.
      int[] dimensions = new int[dimension_count];
      dimensions[0] = Array.getLength(object);
      for (int i = 1; i < dimension_count; i += 1)
      {
        dimensions[i] = 0;
      }
      Object copy = Array.newInstance(baseClass, dimensions);
      
      // now fill in the next level down by recursion.
      for (int i = 0; i < dimensions[0]; i += 1)
      {
        Array.set(copy, i, deepClone(Array.get(object, i)));
      }
      
      return copy;
    }
    // handle cloneable collections
    else if (object instanceof Collection && object instanceof Cloneable)
    {
      @SuppressWarnings("unchecked")
      Collection<Object> collection = (Collection<Object>) object;
      
      // instantiate the new collection and clear it
      @SuppressWarnings("unchecked")
      Collection<Object> copy = (Collection<Object>) CloneUtils.genericClone(object);
      copy.clear();
      
      // clone all the values in the collection individually
      for (Object item : collection)
      {
        copy.add(deepClone(item));
      }
      
      return copy;
    }
    // handle cloneable maps
    else if (object instanceof Map && object instanceof Cloneable)
    {
      @SuppressWarnings("unchecked")
      Map<Object, Object> map = (Map<Object, Object>) object;
      
      // instantiate the new map and clear it
      @SuppressWarnings("unchecked")
      Map<Object, Object> copy = (Map<Object, Object>) CloneUtils.genericClone(object);
      copy.clear();
      
      // now clone all the keys and values of the entries
      for (Map.Entry<Object, Object> entry : map.entrySet())
      {
        copy.put(deepClone(entry.getKey()), deepClone(entry.getValue()));
      }
      
      return copy;
    }
    // use the generic clone method
    else
    {
      Object copy = CloneUtils.genericClone(object);
      if (null == copy)
      {
        throw new IllegalStateException("Clone not supported: " + object.getClass().getName());
      }
      return copy;
    }
  }
  
  /**
   * This routine returns the base class of an object. This is just the class of the object for non-arrays.
   * 
   * @param object
   *          The object whose base class you want to retrieve.
   */
  private static Class<?> getBaseClass(Object object)
  {
    if (object == null)
    {
      return Void.TYPE;
    }
    
    String className = object.getClass().getName();
    
    // skip forward over the array dimensions
    int dims = 0;
    while (className.charAt(dims) == '[')
    {
      dims += 1;
    }
    
    // if there were no array dimensions, just return the class of the
    // provided object
    if (dims == 0)
    {
      return object.getClass();
    }
    
    switch (className.charAt(dims))
    {
    // handle the boxed primitives
      case 'Z':
        return Boolean.TYPE;
      case 'B':
        return Byte.TYPE;
      case 'S':
        return Short.TYPE;
      case 'C':
        return Character.TYPE;
      case 'I':
        return Integer.TYPE;
      case 'J':
        return Long.TYPE;
      case 'F':
        return Float.TYPE;
      case 'D':
        return Double.TYPE;
        // look up the class of another reference type
      case 'L':
        try
        {
          return Class.forName(className.substring(dims + 1, className.length() - 1));
        }
        catch (ClassNotFoundException e)
        {
          return null;
        }
      default:
        return null;
    }
  }
}
