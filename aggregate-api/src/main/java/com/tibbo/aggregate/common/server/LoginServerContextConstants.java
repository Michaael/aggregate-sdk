package com.tibbo.aggregate.common.server;

public interface LoginServerContextConstants
{
  String V_LOGIN_SERVER_CONFIGURATION = "loginServerConfiguration";
  String V_ADDRESS = "address";
  String V_PORT = "port";
  String V_USERNAME = RootContextConstants.FIF_LOGIN_USERNAME;
  String V_PASSWORD = RootContextConstants.FIF_LOGIN_PASSWORD;
  String V_ENABLED = "enabled";
  String V_LOGIN_SERVER_CACHE_NAME = "clusterCache";
  
  String V_PATH = "path";
  String V_NODE_NAME = "nodeName";
  
  String F_UPDATE_SYSTEM_TREE = "updateSystemTree";
  String F_UPDATE_AUTORUN = "updateAutorun";
  String F_UPDATE_EVENT_FILTERS = "updateEventFilters";
  
  String V_COMMAND_LAST = "last";
}
