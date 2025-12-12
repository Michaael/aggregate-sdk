package com.tibbo.aggregate.common.util;

import java.io.*;
import java.lang.reflect.*;
import java.net.*;
import java.util.*;

import com.tibbo.aggregate.common.*;

public class ClassPathHelper
{
  private static final String JAVA_LIBRARY_PATH_PROPERTY = "java.library.path";
  private static final String JAR_FILE_EXTENSION = ".jar";
  private static final String PROTOCOL_FILE = "file";
  
  public static void loadJars(File directory) throws IOException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, SecurityException, NoSuchMethodException
  {
    for (String path : listJars(directory))
    {
      addToClassPath(path);
    }
  }
  
  public static List<String> listJars(File directory)
  {
    if (!directory.isDirectory())
    {
      Log.CORE.error("Error loading JARs from a directory: Not a directory: " + directory);
      return Collections.emptyList();
    }
    // Оптимизация: используем ArrayList вместо LinkedList
    // Оцениваем количество файлов: обычно в директории lib не более 100 JAR файлов
    List<String> res = new ArrayList<String>(100);
    File[] files = directory.listFiles();
    if (files != null)
    {
      for (File file : files)
      {
        if (file.getName().endsWith(JAR_FILE_EXTENSION))
        {
          res.add(file.getAbsolutePath());
        }
        else if (file.isDirectory())
        {
          res.addAll(listJars(file));
        }
      }
    }
    return res;
  }
  
  public static void addToClassPath(URLClassLoader urlClassLoader, String path) throws InvocationTargetException, IllegalAccessException, MalformedURLException, NoSuchMethodException
  {
    URL u = new URL(PROTOCOL_FILE, null, path);
    Class urlClass = URLClassLoader.class;
    Method method = urlClass.getDeclaredMethod("addURL", new Class[] { URL.class });
    method.setAccessible(true);
    method.invoke(urlClassLoader, u);
  }
  
  public static void addToClassPath(String path) throws MalformedURLException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, SecurityException, NoSuchMethodException
  {
    Log.CORE.info("Loading external JAR file: " + path);
    URLClassLoader urlClassLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
    addToClassPath(urlClassLoader, path);
  }
  
  public static void addToLibraryPath(String path) throws IllegalArgumentException, IllegalAccessException
  {
    String newLibPath = path + File.pathSeparator + System.getProperty(JAVA_LIBRARY_PATH_PROPERTY);
    System.setProperty(JAVA_LIBRARY_PATH_PROPERTY, newLibPath);
    Field fieldSysPath;
    try
    {
      fieldSysPath = ClassLoader.class.getDeclaredField("sys_paths");
      fieldSysPath.setAccessible(true);
      if (fieldSysPath != null)
      {
        fieldSysPath.set(System.class.getClassLoader(), null);
      }
    }
    catch (NoSuchFieldException ex)
    {
      throw new IllegalStateException("Failed to extend java.library.path:" + ex.getMessage(), ex);
    }
  }
}
