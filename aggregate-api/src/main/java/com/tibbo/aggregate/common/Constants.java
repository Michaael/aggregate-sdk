package com.tibbo.aggregate.common;

import java.awt.GraphicsEnvironment;

public class Constants
{
  // Global constants
  
  public final static String PLUGIN_PREFIX = "com.tibbo.linkserver.plugin.";
  public final static String GROUP_CORE = "com.tibbo.aggregate";
  public final static String GROUP_CONTEXT = "com.tibbo.aggregate.context";
  public final static String GROUP_DEVICE = "com.tibbo.aggregate.device";
  public final static String GROUP_WEB = "com.tibbo.aggregate.web";
  public final static String GROUP_PERSISTENCE = "com.tibbo.aggregate.persistence";
  public final static String GROUP_AUTH = "com.tibbo.aggregate.auth";
  public final static String GROUP_DS = "com.tibbo.aggregate.ds";
  public final static String GROUP_RESOURCE = "com.tibbo.aggregate.resource";
  
  public final static String VERSION = "6.31.04";
  
  public final static String DOCS_SUBDIR = "docs/";
  public final static String SCRIPTS_SUBDIR = "scripts/";
  public final static String LIBRARIES_SUBDIR = "lib/";
  public final static String JARS_SUBDIR = "jar/";
  public final static String PLUGINS_SUBDIR = "plugins/";
  public final static String LOCAL_AGENT_CACHE_SUBDIR = "local_agent_cache/";
  public final static String LOCAL_AGENT_EVENTS_CACHE = "events";
  public final static String LOCAL_AGENT_UPDATES_CACHE = "updates";
  
  public final static String ROOT_FOLDER_PATH_PATTERN = "$root$";
  
  public final static String DOCS_FILE_EXTENSION = "htm";
  
  public final static String PLATFORM_WIN32 = "win32";
  public final static String PLATFORM_WIN64 = "win64";
  public final static String PLATFORM_LINUX32 = "linux32";
  public final static String PLATFORM_LINUX64 = "linux64";
  public final static String PLATFORM_MAC_OS = "macOs";
  public final static String PLATFORM_ARM32 = "arm32";
  public final static String PLATFORM_ARM64 = "arm64";
  
  public final static String MAVEN_REPOSITORY_RELEASE_TEST = "http://192.168.75.167:8081/repository/maven-releases";
  public final static String MAVEN_REPOSITORY_RELEASE_PUBLIC = "https://store.aggregate.digital/repository/maven-releases";
  public static final String SNAPSHOT = "SNAPSHOT";

  public static final boolean IS_HEADLESS = GraphicsEnvironment.getLocalGraphicsEnvironment().isHeadlessInstance();
  public static final boolean HAS_GUI = !IS_HEADLESS;

  public static void main(String[] args)
  {
    System.out.print(SoftwareVersion.getCurrentVersion());
  }
}
