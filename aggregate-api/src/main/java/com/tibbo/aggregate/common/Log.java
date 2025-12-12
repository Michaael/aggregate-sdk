package com.tibbo.aggregate.common;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.logging.LogManager;

import org.apache.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;

import com.tibbo.aggregate.common.util.LogRedirectingHandler;

/**
 * IMPORTANT!
 *
 * Any logger added to this file must be immediately documented and registered in installed versions of server and client logging configuration files.
 *
 */
public class Log
{
  public final static String ROOT = "ag";
  
  public final static Logger ALERTS = Logger.getLogger("ag.alerts");

  public final static Logger APPLICATIONS = Logger.getLogger("ag.applications");

  public final static Logger AUTORUN = Logger.getLogger("ag.autorun");
  
  public final static Logger BINDINGS = Logger.getLogger("ag.bindings");
  
  public final static Logger COMMANDS = Logger.getLogger("ag.commands");
  public final static Logger COMMANDS_AGENT = Logger.getLogger("ag.commands.agent");
  public final static Logger COMMANDS_CLIENT = Logger.getLogger("ag.commands.client");
  public final static Logger COMMANDS_DISTRIBUTED = Logger.getLogger("ag.commands.distributed");
  public final static Logger COMMANDS_DS = Logger.getLogger("ag.commands.device_server");
  
  public final static Logger CDATA = Logger.getLogger("ag.commondata");
  
  public final static Logger CLIENTS = Logger.getLogger("ag.clients");
  
  public final static Logger FAILOVER = Logger.getLogger("ag.failover");
  
  public final static Logger CLUSTER = Logger.getLogger("ag.cluster");

  public final static Logger CONFIG = Logger.getLogger("ag.config");
  
  public final static Logger CONTEXT = Logger.getLogger("ag.context");
  public final static Logger CONTEXT_INFO = Logger.getLogger("ag.context.info");
  public final static Logger CONTEXT_CHILDREN = Logger.getLogger("ag.context.children");
  public final static Logger CONTEXT_VARIABLES = Logger.getLogger("ag.context.variables");
  public final static Logger CONTEXT_FUNCTIONS = Logger.getLogger("ag.context.functions");
  public final static Logger CONTEXT_EVENTS = Logger.getLogger("ag.context.events");
  public final static Logger CONTEXT_ACTIONS = Logger.getLogger("ag.context.actions");
  public final static Logger CONTEXT_WEBSERVER = Logger.getLogger("ag.context.webserver");
  public final static Logger CODE_EDITOR = Logger.getLogger("ag.code_editor");
  
  public final static Logger CORE = Logger.getLogger("ag.core");
  public final static Logger CORE_THREAD = Logger.getLogger("ag.core.thread");
  
  public final static Logger CCM = Logger.getLogger("ag.ccm");
  
  public final static Logger DASHBOARDS = Logger.getLogger("ag.dashboards");
  
  public final static Logger DATABASE = Logger.getLogger("ag.database");
  
  public final static Logger CONVERTER = Logger.getLogger("ag.converter");
  
  public final static Logger CORRELATOR = Logger.getLogger("ag.correlator");
  
  public final static Logger DEVICE = Logger.getLogger("ag.device");
  
  public final static Logger DEVICE_DISCOVERY = Logger.getLogger("ag.device.discovery");
  
  public final static Logger DEVICE_LICENSE = Logger.getLogger("ag.device.license");
  
  public final static Logger DEVICE_SYNCHRONIZATION = Logger.getLogger("ag.device.sync");
  
  public final static Logger DEVICE_AGENT = Logger.getLogger("ag.device.agent");
  
  public final static Logger DEVICEBROWSER = Logger.getLogger("ag.device_browser");
  
  public final static Logger DEVICESERVER = Logger.getLogger("ag.device_server");
  
  public final static Logger DEVICESERVER_INBANDS = Logger.getLogger("ag.device_server.inbands");
  
  public final static Logger DATATABLE = Logger.getLogger("ag.data_table");
  public final static Logger DATATABLE_FILTER = Logger.getLogger("ag.data_table.filter");
  
  public final static Logger DATATABLEEDITOR = Logger.getLogger("ag.data_table_editor");
  
  public final static Logger DNS = Logger.getLogger("ag.dns");
  
  public final static Logger ENTITYSELECTOR = Logger.getLogger("ag.entity_selector");
  
  public final static Logger EVENTFILTER = Logger.getLogger("ag.eventfilters");
  
  public final static Logger EVENTLOG = Logger.getLogger("ag.event_log");
  
  public final static Logger EXPRESSIONBUILDER = Logger.getLogger("ag.expression_builder");
  
  public final static Logger EXPRESSIONS = Logger.getLogger("ag.expressions");
  
  public final static Logger FAVOURITES = Logger.getLogger("ag.favourites");
  
  public final static Logger GROUPS = Logger.getLogger("ag.groups");
  
  public final static Logger GUI = Logger.getLogger("ag.gui");
  
  public final static Logger GUIBUILDER = Logger.getLogger("ag.gui_builder");
  
  public final static Logger GUIDE = Logger.getLogger("ag.guide");
  
  public final static Logger MAIL = Logger.getLogger("ag.mail");
  
  public final static Logger MOBILE = Logger.getLogger("ag.mobile");
  
  public final static Logger MODELS = Logger.getLogger("ag.models");
  
  public final static Logger CLASSES = Logger.getLogger("ag.classes");
  
  public final static Logger NETADMIN = Logger.getLogger("ag.net_admin");
  
  public final static Logger NOSQL = Logger.getLogger("ag.database.no_sql");
  
  public final static Logger PERFORMANCE = Logger.getLogger("ag.performance");
  
  public final static Logger PERMISSIONS = Logger.getLogger("ag.permissions");
  
  public final static Logger PLUGINS = Logger.getLogger("ag.plugins");
  
  public final static Logger PROPERTIESEDITOR = Logger.getLogger("ag.properties_editor");
  
  public final static Logger PROTOCOL = Logger.getLogger("ag.protocol");
  
  public final static Logger PROTOCOL_CACHING = Logger.getLogger("ag.protocol.caching");
  
  public final static Logger QUERIES = Logger.getLogger("ag.queries");
  
  public final static Logger REPORTS = Logger.getLogger("ag.reports");
  
  public final static Logger REQUESTS = Logger.getLogger("ag.requests");
  
  public final static Logger RESOURCE = Logger.getLogger("ag.resource");
  
  public final static Logger REST = Logger.getLogger("ag.rest");
  
  public final static Logger SCHEDULER = Logger.getLogger("ag.scheduler");
  
  public final static Logger SCRIPTS = Logger.getLogger("ag.scripts");
  
  public final static Logger SECURITY = Logger.getLogger("ag.security");
  
  public final static Logger STATISTICS = Logger.getLogger("ag.statistics");
  
  public final static Logger STRUCTURE = Logger.getLogger("ag.structure");
  
  public final static Logger GRANULATION = Logger.getLogger("ag.granulation");
  
  public final static Logger TEMPLATES = Logger.getLogger("ag.templates");
  
  public final static Logger STDOUT = Logger.getLogger("ag.stdout");
  
  public final static Logger STDERR = Logger.getLogger("ag.stderr");
  
  public final static Logger STORE = Logger.getLogger("ag.store");
  
  public final static Logger SYSTEMTREE = Logger.getLogger("ag.system_tree");
  
  public final static Logger TEST = Logger.getLogger("ag.test");
  
  public final static Logger TRACKERS = Logger.getLogger("ag.trackers");
  
  public final static Logger USERS = Logger.getLogger("ag.users");
  
  public final static Logger VIEW = Logger.getLogger("ag.view");
  
  public final static Logger WEB = Logger.getLogger("ag.web");
  
  public final static Logger DEBUG = Logger.getLogger("ag.debug");
  
  public final static Logger WIDGETS = Logger.getLogger("ag.widgets");
  
  public final static Logger WORKFLOWS = Logger.getLogger("ag.workflows");

  public final static Logger WATCHDOG = Logger.getLogger("ag.watchdog");
  public final static Logger WATCHDOG_MEASURE = Logger.getLogger("ag.watchdog.measure");

  public final static Logger PING = Logger.getLogger("ag.device.ping");
  public final static Logger PING_STATS = Logger.getLogger("ag.device.ping.statistics");
  
  public final static Logger BILLING = Logger.getLogger("ag.billing");
  
  public final static String LOGGING_CONFIG_FILENAME = "logging.xml";
  
  public final static String CLIENT_LOGGING_CONFIG_FILENAME = "logging-client.xml";
  
  public final static int LOGGING_CONFIG_CHECK_INTERVAL = 10000;
  
  public static void start()
  {
    start(LOGGING_CONFIG_FILENAME);
  }
  
  public static void start(String loggingConfigFile)
  {
    start(System.getProperty("user.dir") + File.separator, loggingConfigFile);
  }
  
  public static void start(String homeDirectory, String loggingConfigFile)
  {
    configureLogger(new File(homeDirectory + loggingConfigFile).toURI().toString());
    
    setupRedirect();
  }
  
  public static void start(URL url)
  {
    configureLogger(url.toString());
    
    setupRedirect();
  }
  
  private static void configureLogger(String configLocation)
  {
    String propConfFile = System.getProperty("log4j.configurationFile");
    if (propConfFile == null || !propConfFile.equals(configLocation))
    {
      System.setProperty("log4j.configurationFile", configLocation);
      final LoggerContext ctx = (LoggerContext) org.apache.logging.log4j.LogManager.getContext(false);
      ctx.reconfigure();
    }
  }
  
  private static void setupRedirect()
  {
    // Adding java.util.logging - log4j redirecting handler
    try
    {
      InputStream stream = LogRedirectingHandler.class.getResourceAsStream("logging.properties");
      LogManager.getLogManager().readConfiguration(stream);
    }
    catch (Exception e)
    {
      throw new IllegalStateException(e);
    }
  }
  
}
