package com.tibbo.aggregate.common.expression.function;

import java.lang.reflect.*;
import java.util.*;

import com.tibbo.aggregate.common.expression.*;
import com.tibbo.aggregate.common.util.*;

public class JavaMethodFunction extends AbstractFunction
{
  private static final Class<?>[][] CONVERSIONS = new Class<?>[][] {
      { float.class, double.class },
      { float.class, Double.class },
      { Float.class, double.class },
      { Float.class, Double.class },
      { double.class, double.class },
      { double.class, Double.class },
      { Double.class, double.class },
      { Double.class, Double.class },
      { Character.class, char.class },
      { char.class, Character.class },
      { String.class, CharSequence.class } };

  private final String name;
  private final String clazz;
  private final String method;
  private final boolean statical;
  
  public JavaMethodFunction(String clazz, String name, String method, String category, String parametersFootprint, String returnValue, String description)
  {
    this(clazz, name, method, true, category, parametersFootprint, returnValue, description);
  }
  
  public JavaMethodFunction(String clazz, String name, String method, String category, String parametersFootprint, String returnValue)
  {
    this(clazz, name, method, true, category, parametersFootprint, returnValue, null);
  }
  
  public JavaMethodFunction(String clazz, String name, String method, boolean statical, String category, String parametersFootprint, String returnValue)
  {
    this(clazz, name, method, statical, category, parametersFootprint, returnValue, null);
  }
  
  public JavaMethodFunction(String clazz, String name, String method, boolean statical, String category, String parametersFootprint, String returnValue, String description)
  {
    super(name, category, parametersFootprint, returnValue, description);
    this.name = name;
    this.clazz = clazz;
    this.method = method;
    this.statical = statical;
  }
  
  @Override
  public Object execute(Evaluator evaluator, EvaluationEnvironment environment, Object... parameters) throws EvaluationException
  {
    try
    {
      Class<?> cls = Class.forName(clazz);
      
      Object instance = null;
      
      if (!statical)
      {
        instance = convertInstance(cls, parameters[0]);
        Object[] np = new Object[parameters.length - 1];
        System.arraycopy(parameters, 1, np, 0, parameters.length - 1);
        parameters = np;
      }
      
      for (int i = 0; i < parameters.length; i++)
      {
        parameters[i] = convertParameter(i, parameters[i]);
      }
      
      Class<?>[] types = new Class<?>[parameters.length];
      
      for (int i = 0; i < parameters.length; i++)
      {
        types[i] = parameters[i] != null ? parameters[i].getClass() : null;
      }
      
      Method executor = findExecutorMethod(cls, types, 0);
      
      if (executor == null)
      {
        final Method[] classMethods = cls.getMethods();
        for (Method cur : classMethods)
        {
          if (cur.getName().equals(method) && cur.getParameterTypes().length == types.length)
          {
            executor = cur;
            break;
          }
        }
      }
      
      if (executor == null)
      {
        throw new EvaluationException("Incompatible arguments");
      }
      
      castTypes(executor.getParameterTypes(), parameters);
      
      if (instance == null && !statical)
      {
        throw new EvaluationException("Object cannot be null");
      }
      
      return executor.invoke(instance, parameters);
    }
    catch (InvocationTargetException ex)
    {
      throw new EvaluationException(ex.getTargetException().getMessage(), ex.getTargetException());
    }
    catch (Exception ex)
    {
      throw new EvaluationException(ex);
    }
  }
  
  private Object convertInstance(Class<?> cls, Object instance)
  {
    if (instance == null)
    {
      return null;
    }
    
    if (cls.isAssignableFrom(instance.getClass()))
    {
      return instance;
    }
    
    if (cls == String.class)
    {
      return instance.toString();
    }
    
    if (cls == Date.class)
    {
      return Util.convertToDate(instance, false, false);
    }
    
    return instance;
  }
  
  private Method findExecutorMethod(Class<?> cls, Class<?>[] types, int firstParameterToRotate)
  {
    try
    {
      return cls.getMethod(method, types);
    }
    catch (NoSuchMethodException ex1)
    {
      // Continuing
    }
    
    for (int i = firstParameterToRotate; i < types.length; i++)
    {
      Class<?> original = types[i];
      for (Class<?>[] conversions : CONVERSIONS)
      {
        if (original == conversions[0])
        {
          types[i] = conversions[1];
          
          try
          {
            return cls.getMethod(method, types);
          }
          catch (NoSuchMethodException ex1)
          {
            // Continuing
          }
          
          Method executor = findExecutorMethod(cls, types, firstParameterToRotate + 1);
          
          if (executor != null)
          {
            return executor;
          }
        }
      }
      
      types[i] = original;
    }
    
    return null;
  }
  
  private void castTypes(Class<?>[] types, Object[] parameters)
  {
    for (int i = 0; i < types.length; i++)
    {
      Class<?> requiredType = types[i];
      Class<?> currentType = parameters[i] != null ? parameters[i].getClass() : null;
      
      if (requiredType.equals(currentType))
      {
        continue;
      }
      
      if (parameters[i] == null)
      {
        continue;
      }
      
      if (requiredType == Integer.class || requiredType == int.class)
      {
        parameters[i] = parameters[i] instanceof Number ? ((Number) parameters[i]).intValue() : new Integer(parameters[i].toString());
      }
      else if (requiredType == Long.class || requiredType == long.class)
      {
        parameters[i] = parameters[i] instanceof Number ? ((Number) parameters[i]).longValue() : new Long(parameters[i].toString());
      }
      else if (requiredType == Float.class || requiredType == float.class)
      {
        parameters[i] = parameters[i] instanceof Number ? ((Number) parameters[i]).floatValue() : new Float(parameters[i].toString());
      }
      else if (requiredType == Double.class || requiredType == double.class)
      {
        parameters[i] = parameters[i] instanceof Number ? ((Number) parameters[i]).doubleValue() : new Double(parameters[i].toString());
      }
      else if (requiredType == String.class)
      {
        parameters[i] = parameters[i].toString();
      }
    }
  }
  
  protected Object convertParameter(int i, Object value)
  {
    return value;
  }
}
