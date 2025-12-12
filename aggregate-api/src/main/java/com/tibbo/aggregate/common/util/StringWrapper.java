package com.tibbo.aggregate.common.util;

import java.util.*;

public class StringWrapper
{
  private final String source;
  private String result = null;
  
  private int beginIndex = -1;
  private int endIndex = -1;
  
  private StringWrapper(String source)
  {
    this.source = source;
  }
  
  private StringWrapper(String source, int beginIndex, int endIndex)
  {
    this.source = source;
    this.beginIndex = beginIndex;
    this.endIndex = endIndex;
  }
  
  public static StringWrapper valueOf(String source)
  {
    return new StringWrapper(source);
  }
  
  public static StringWrapper valueOf(String source, int beginIndex, int endIndex)
  {
    return new StringWrapper(source, beginIndex, endIndex);
  }
  
  public String getString()
  {
    if (result == null)
    {
      if (beginIndex >= 0)
      {
        result = source.substring(beginIndex, endIndex);
      }
      else
        result = source;
    }
    
    return result;
  }
  
  @Override
  public String toString()
  {
    return getString();
  }
  
  @Override
  public boolean equals(Object obj)
  {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    
    if (obj instanceof String)
    {
      String anotherString = (String) obj;
      
      int bi = (beginIndex >= 0) ? beginIndex : 0;
      int l = (beginIndex >= 0) ? endIndex - beginIndex : source.length();
      
      if (anotherString.length() != l)
        return false;
      
      for (int i = 0; i < l; i++)
        if (source.charAt(bi + i) != anotherString.charAt(i))
          return false;
        
      return true;
    }
    
    if (obj instanceof StringWrapper)
    {
      StringWrapper anotherString = (StringWrapper) obj;
      
      int bi = (beginIndex >= 0) ? beginIndex : 0;
      int l = (beginIndex >= 0) ? endIndex - beginIndex : source.length();
      
      if (anotherString.length() != l)
        return false;
      
      for (int i = 0; i < l; i++)
        if (source.charAt(bi + i) != anotherString.charAt(i))
          return false;
        
      return true;
    }
    
    return false;
  }
  
  public int length()
  {
    if (beginIndex >= 0)
      return endIndex - beginIndex;
    else
      return source.length();
  }
  
  public char charAt(int i)
  {
    if (beginIndex >= 0)
      return source.charAt(i + beginIndex);
    
    return source.charAt(i);
  }
  
  public static List<StringWrapper> split(String str, char ch)
  {
    // Оптимизация: используем ArrayList вместо LinkedList для лучшей производительности
    // Оцениваем количество частей: примерно 1 часть на каждые 50 символов
    int estimatedSize = Math.max(10, str.length() / 50);
    List<StringWrapper> res = new ArrayList<StringWrapper>(estimatedSize);
    
    int index = 0;
    int newindex = 0;
    
    boolean finished = false;
    
    for (;;)
    {
      newindex = str.indexOf(ch, index);
      
      if (newindex == -1)
      {
        finished = true;
        newindex = str.length();
      }
      
      res.add(StringWrapper.valueOf(str, index, newindex));
      
      if (finished)
      {
        break;
      }
      
      index = newindex + 1;
      
      if (index == str.length())
      {
        res.add(StringWrapper.valueOf(""));
        break;
      }
    }
    
    return res;
  }
}
