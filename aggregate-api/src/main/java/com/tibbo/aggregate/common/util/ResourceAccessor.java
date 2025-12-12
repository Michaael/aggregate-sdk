package com.tibbo.aggregate.common.util;

import java.util.*;

import com.tibbo.aggregate.common.*;
import com.tibbo.aggregate.common.resource.*;

public class ResourceAccessor
{
  private static final String RESOURCE_PACKAGE_NAME = "res";
  
  public static ResourceBundle fetch(Class clazz, Locale locale)
  {
    try
    {
      return new WrappingResourceBundle((PropertyResourceBundle) ResourceBundle.getBundle(getBundleName(clazz), locale));
    }
    catch (Exception ex)
    {
      Log.CORE.error(ex.getMessage(), ex);
      return new WrappingResourceBundle((PropertyResourceBundle) ResourceBundle.getBundle(getBundleName(clazz), Locale.ENGLISH));
    }
  }
  
  public static ResourceBundle fetch(Class clazz, Locale locale, ClassLoader classLoader)
  {
    try
    {
      return new WrappingResourceBundle((PropertyResourceBundle) ResourceBundle.getBundle(getBundleName(clazz), locale, classLoader));
    }
    catch (Exception ex)
    {
      Log.CORE.error(ex.getMessage(), ex);
      return new WrappingResourceBundle((PropertyResourceBundle) ResourceBundle.getBundle(getBundleName(clazz), Locale.ENGLISH, classLoader));
    }
  }
  
  public static String getBundleName(Class clazz)
  {
    Package aPackage = clazz.getPackage();
    
    // This class can be loaded without a package by third-party class loaders, so we get the package from the class name in this case
    String packageName = aPackage == null ?
        clazz.getCanonicalName().replace("." + clazz.getSimpleName(), "") :
        aPackage.getName();
    
    return packageName + "." + RESOURCE_PACKAGE_NAME + "." + clazz.getSimpleName();
  }
}
