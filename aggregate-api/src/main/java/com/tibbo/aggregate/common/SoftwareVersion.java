package com.tibbo.aggregate.common;

import java.io.File;
import java.util.Arrays;

import com.tibbo.aggregate.common.util.JarVersion;

public class SoftwareVersion
{
  public static String[] SYSTEM_CLASS_PATH = System.getProperty("java.class.path").split(File.pathSeparator);
  
  private static String currentVersion = Constants.VERSION;
  
  private static String currentBuild = Constants.SNAPSHOT;
  
  public static void initVersion(String targetClassPath)
  {
      JarVersion.getClassPathJarVersionsByTarget(Arrays.asList(SYSTEM_CLASS_PATH), targetClassPath)
        .ifPresent(jarVersion -> {
          if (jarVersion.implementationVersion != null)
          {
            currentVersion = jarVersion.implementationVersion;
          }
          if (jarVersion.buildNumber != null)
          {
            currentBuild = jarVersion.buildNumber;
          }
        });
  }
  
  public static String getCurrentVersion()
  {
    return currentVersion;
  }
  
  public static String getCurrentBuild()
  {
    return currentBuild;
  }
  
  public static String getCurrentVersionAndBuild()
  {
    return currentVersion + "-" + currentBuild;
  }
}
