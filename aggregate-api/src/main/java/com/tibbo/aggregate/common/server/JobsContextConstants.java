package com.tibbo.aggregate.common.server;

public interface JobsContextConstants
{
  public static final String A_CREATE_BY_DND = "createByDnD";
  public static final String A_UPGRADE_SERVER = "upgrade";
  
  public static final Integer TIMEOUT = 600000;
  public static final Integer WAIT_PERIOD = 500;
  
  public static final String JAVA = "java";
  public static final String AGG_JAVA = "jre/bin/java";
  
  public static final String C_CP = "-cp";
  public static final String C_JAR = "-jar";
  public static final String JAR = "jar/";
  public static final String DELIMITER = ";";
  public static final String AT = "@";
  public static final String LINUX_PREFIX = "./";
  public static final String LINUX_CHMOD = "chmod";
  public static final String LINUX_777 = "777";
  
  public static final String COMMONS_LIB = "commons-libs.jar";
  public static final String UPGRADE_LIB = "upgrade-app.jar";
  public static final String UPGRADE_LOG = "upgrade.log";
  
  public static final String UPDATE_MACOS = "macos";
  public static final String UPDATE_OS_NAME = "os.name";
  public static final String UPDATE_OS_X = "os x";
  public static final String UPDATE_WINDOWS = "windows";
  public static final String UPDATE_UNIX = "unix";
  public static final String UPDATE_LINUX = "linux";
  public static final String UPDATE_OS_ARCH = "os.arch";
  public static final String UPDATE_OS_ARCH_64 = "64";
  
  public static final String F_UPGRADE = "upgrade";
  
  public static final String FIF_UPGRADE_FILE = "upgardeFile";
  public static final String FIF_UPGRADE_INSTANTLY = "instantly";
  public static final String FIF_UPGRADE_DELAY = "delay";
  public static final String FIF_UPGRADE_REASON = "reason";
  
}
