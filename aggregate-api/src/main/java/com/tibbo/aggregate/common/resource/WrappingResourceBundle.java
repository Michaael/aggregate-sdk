package com.tibbo.aggregate.common.resource;

import java.util.*;

import com.tibbo.aggregate.common.*;
import com.tibbo.aggregate.common.util.*;

public class WrappingResourceBundle extends ResourceBundle
{
  private String baseName;
  
  private final PropertyResourceBundle wrapped;
  
  private final Map<String, String> replacements = new Hashtable();
  
  public WrappingResourceBundle(PropertyResourceBundle wrapped)
  {
    this.wrapped = wrapped;
    
    ResourceManager.process(this);
    
    ResourceManager.add(this);
  }
  
  public void addReplacement(String key, String value)
  {
    replacements.put(key, value);
  }
  
  @Override
  protected Object handleGetObject(String key)
  {
    if (replacements.containsKey(key))
    {
      return replacements.get(key);
    }
    
    try
    {
      Object value = wrapped.handleGetObject(key);
      
      if (value != null)
      {
        return value;
      }
      
      return getEnglishValue(key);
      
    }
    catch (Exception ex)
    {
      Log.RESOURCE.warn("Missing resource: " + key + " in " + wrapped.getClass().getName());
      return getEnglishValue(key);
    }
  }
  
  private Object getEnglishValue(String key)
  {
    try
    {
      if (baseName == null)
      {
        baseName = ReflectUtils.getPrivateField(wrapped, "name").toString();
      }
      
      PropertyResourceBundle englishBundle = (PropertyResourceBundle) getBundle(baseName, Locale.ENGLISH);
      
      Object value = englishBundle.handleGetObject(key);
      
      return value != null ? value : key;
    }
    catch (Exception ex)
    {
      Log.RESOURCE.warn("Error getting resource: " + key, new Exception());
      return key;
    }
  }
  
  @Override
  public Enumeration<String> getKeys()
  {
    return wrapped.getKeys();
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
    
    WrappingResourceBundle other = (WrappingResourceBundle) obj;
    
    if (!Util.equals(getClass().getName(), other.getClass().getName()))
    {
      return false;
    }
    
    return true;
  }
  
  @Override
  public int hashCode()
  {
    return getClass().getName().hashCode();
  }
}
