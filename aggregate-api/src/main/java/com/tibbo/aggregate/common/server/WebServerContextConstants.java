package com.tibbo.aggregate.common.server;

import java.io.File;

public class WebServerContextConstants
{
  public static final String PLUGIN_ID = "com.tibbo.linkserver.plugin.context.webserver";
  public static final String V_WEBAPPS = "webapps";
  public static final String V_CONNECTION_PROPERTIES = "connectionProperties";
  public static final int V_CONNECTION_TYPE_ONLY_LOCAL = 0;
  public static final int V_CONNECTION_TYPE_ONLY_REMOTE = 1;
  public static final int V_CONNECTION_TYPE_PRECONFIGURED = 2;
  public static final String V_CONNECTION_PRECONFIGURED_PORT = "port";
  public static final String V_CONNECTION_PRECONFIGURED_ADDRESS = "address";
  public static final String V_CONNECTION_PRECONFIGURED_NAME = "connectionName";
  public static final String V_CONNECTION_PRECONFIGURED_DESCRIPTION = "connectionDescription";
  public static final String V_CONNECTION_PRECONFIGURED_DISABLED = "preconfiguredDisabled";
  
  public static final String V_WEBAPPS_WEB_SERVER_ENABLED = "webServerEnabled";
  public static final String V_WEBAPPS_WEB_SERVICE_ENABLED = "webServiceEnabled";
  public static final String V_WEBAPPS_WEB_SERVICE_USE_RESPONSE_ENCODING = "webServiceUseResponseEncoding";
  public static final String V_WEBAPPS_WEB_NON_SECURE_ACCESS_ENABLED = "webNonSecureAccessEnabled";
  public static final String V_WEBAPPS_WEB_APPLET_AND_JAVAWS_ENABLED = "webAppletAndJavaWSEnabled";
  public static final String V_WEBAPPS_WEB_APPS_ALIASES = "webAppsAliases";
  public static final String V_WEBAPPS_WEB_APPS_SSL_PORT = "webAppsSslPort";
  public static final String V_WEBAPPS_WEB_APPS_NON_SSL_PORT = "webAppsNonSslPort";
  public static final String V_WEBAPPS_WEB_KEYSTORE_FILE = "webAppsKeyStoreFile";
  public static final String V_WEBAPPS_WEB_KEYSTORE_PASSWORD = "webAppsKeyStorePassword";
  public static final String V_WEBAPPS_WEB_KEY_PASSWORD = "webAppsKeyPassword";
  public static final String VF_WEBAPPS_WEB_APP_READ_BUFFER_SIZE = "appReadBufferSize";
  public static final String VF_WEBAPPS_WEB_APP_WRITE_BUFFER_SIZE = "appWriteBufferSize";
  public static final String VF_WEBAPPS_MAX_THREADS = "maxThreads";
  public static final String VF_WEBAPPS_ACCESS_LOG_ENABLED = "accessLogEnabled";
  public static final String VF_WEB_SOCKET_COMPRESSION_DISABLED = "webSocketCompressionDisabled";

  public static final String V_WEB_CONNECTION_TYPE = "webConnectionType";

  public static final String V_WEB_CONNECTION_PRECONFIGURED = "webConnectionPreconfigured";
  public static final String V_WEB_CUSTOM_ERROR_CONNECTION_MESSAGE = "webCustomErrorConnectionMessage";

  public static final int WEBSOCKET_BUFFER_SIZE = 8192;

  public static final int DEFAULT_MAX_THREADS = 200;


  public static final String WEB_CONTEXT_PATH = "/web";
  public static final String WEB_TEMP_SUBDIR = "temp/";
  public static final String WEB_SERVER_APPS_SUBDIR = "admin/";
  public static final String WEB_SERVER_DEFAULT_HOST = "localhost";
  public static final String WEB_SERVER_SSL_PROTOCOL = "TLS";
  public static final String WEB_SERVER_SSL_ENABLED_PROTOCOLS = "TLSv1.1,TLSv1.2";
  public static final String WEB_SERVER_COMPRESSABLE_MIME_TYPES = "text/html, text/xml, text/css, text/plain, application/json, application/javascript, application/x-javascript, application/xml";
  public static final String WEB_SERVER_COMPRESSION_MODE = "force";
  public static final String WEB_SERVER_URL_SHEME = "https";
  public static final String ROOT_PATH = "";
  public static final String JARS_PATH = "/jar";
  public static final String CLIENT_PATH = "/client";
  public static final String CLIENT_WEBSTART_PATH = "/client-webstart";
  public static final String WEB_WIDGET_PLAYER_PATH = "/widget";
  public static final String WEB_SERVICE_PATH = "/ws";
  public static final String ROOT_APP_FOLDER = "root/";
  public static final String CLIENT_APP_FOLDER = "client/";
  public static final String CLIENT_WEBSTART_APP_FOLDER = "client-webstart/";
  public static final String WEB_SERVICE_APP_FILENAME = "web-service.war";
  public static final String OAUTH_LINKS = "oAuthLinks";
  public static final String OAUTH_DEFAULT = "oAuthDefault";
  public static final int SESSION_DIR_NAME_LENGTH = 32;
  public static final String CALLER_CONTROLLER_PROPERTY_WEB_BASED = "web_based";

  public static String getSessionDirPath(String sessionId)
  {
    return sessionId.substring(sessionId.length() - WebServerContextConstants.SESSION_DIR_NAME_LENGTH) + File.separator;
  }
}
