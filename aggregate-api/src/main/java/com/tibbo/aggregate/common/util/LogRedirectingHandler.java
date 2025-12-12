package com.tibbo.aggregate.common.util;

import java.util.logging.*;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.*;

import com.tibbo.aggregate.common.*;

public class LogRedirectingHandler extends ConsoleHandler
{
  public LogRedirectingHandler()
  {
    try
    {
      setEncoding(StringUtils.UTF8_CHARSET.name());
    }
    catch (Exception e)
    {
      throw new IllegalStateException("Failed to create java.util.logging to log4j redirecting handler", e);
    }
  }
  
  @Override
  public void publish(LogRecord record)
  {
    Logger logger = findCorrespondingLogger(record.getLoggerName(), record.getLoggerName());
    
    if (record.getMessage() == null)
      record.setMessage(digRecordMessage(record));
      
    String msg = getFormatter().format(record);
    Level level;
    
    level = convertLogLevel(record.getLevel());
    
    Throwable thrown = record.getThrown() == null ? new Throwable() : record.getThrown();
    if (logger != null)
    {
      if (logger.isEnabled(level))
        logger.log(level, msg, thrown);
    }
    else
    {
      logger = LogManager.getLogger("ag.stderr");
      if (logger.isDebugEnabled())
      {
        logger.debug("Unexpected logger name: '" + record.getLoggerName() + "'. Log record redirected to '" + Log.CORE + "'");
        logger.debug(record.getLoggerName() + " : " + msg, thrown);
      }
    }
  }
  
  private String digRecordMessage(LogRecord record)
  {
    Throwable cause = record.getThrown();
    while (cause != null)
    {
      if (cause.getMessage() != null)
        return cause.getMessage();
        
      cause = cause.getCause();
    }
    return "";
  }
  
  public static Logger findCorrespondingLogger(String name, String fullName)
  {
    LoggerContext lc = (LoggerContext) LogManager.getContext();
    Logger found = null;
    if (LogManager.exists(name) || lc.getConfiguration().getLoggers().containsKey(name))
      found = LogManager.getLogger(name);
      
    // If starts from the same prefix then found is ancestor or own logger of
    if (found != null && fullName.startsWith(found.getName()))
    {
      return found;
    }
    else
    {
      int psetPos = name.length() - 1;
      while (psetPos > 0 && !name.substring(0, psetPos).endsWith("."))
      {
        psetPos--;
      }
      if (psetPos == 0)
      {
        return null;
      }
      
      String curName = name.substring(0, psetPos - 1);
      return findCorrespondingLogger(curName, fullName);
    }
  }
  
  public static Level convertLogLevel(java.util.logging.Level level)
  {
    if (level == java.util.logging.Level.CONFIG)
    {
      return Level.INFO;
    }
    else if (level == java.util.logging.Level.SEVERE)
    {
      return Level.ERROR;
    }
    else if (level == java.util.logging.Level.WARNING)
    {
      return Level.WARN;
    }
    
    Level converted = Level.toLevel(level.getName());
    
    return converted;
  }
}
