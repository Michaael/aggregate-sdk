package com.tibbo.aggregate.common.server;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.context.ContextUtils;
import com.tibbo.aggregate.common.datatable.FieldFormat;
import com.tibbo.aggregate.common.datatable.TableFormat;

public class StoreContextConstants
{
  public static final String IOT_PLATFORM = "IoT Platform";
  public static final String NETWORK_MANAGMENT = "Network Management";
  public static final String SCADA_HMI = "SCADA/HMI";
  public static final String DATA_CENTER_SUPERVISION = "Data Center Supervision";
  public static final String METER_READING = "Meter Reading";
  public static final String FLEET_MANAGMENT = "Fleet Management";
  public static final String TIME_AND_ATTENDANCE = "Time and Attendance";
  public static final String ACCESS_CONTROL = "Access Control";
  public static final String DEVICE_SERVER_MANAGEMENT_EXTENSION = "Device Server Management Extension";
  public static final String OFFLINE_DOCUMENTATION = "Offline Documentation";
  public static final String CLIENT = "Client";
  public static final String MYSQL_FOR_WINDOWS = "MySQL for Windows";
  public static final String BUILDING_AUTOMATION = "Building Automation";
  public static final String REPORT_EDITOR = "Report Editor";
  
  public static final String STORE_PATH = "./store/";
  
  public static final String PLUGIN_STORE_SERVER_ID = "com.tibbo.linkserver.plugin.context.store-server";
  
  public static final String F_GET_MODULE_DEPENDENCIES = "getModuleDependencies";
  public static final String F_GET_MODULES = "getModules";
  public static final String F_GET_CONTENT_ARCHIVE = "getModuleArchive";
  
  public static final String F_GET_SELECTION_MODULES = "getSelectionModules";
  
  public static final String FIF_GET_MODULE_DEPENDENCIES_ID = "id";
  public static final String FIF_GET_MODULE_DEPENDENCIES_VERSION = "version";
  public static final TableFormat FIFT_GET_MODULE_DEPENDENCIES = new TableFormat();

  public static final String F_CHECK_UPDATE = "checkUpdate";
  
  static
  {
    FieldFormat id = FieldFormat.create(FIF_GET_MODULE_DEPENDENCIES_ID, FieldFormat.STRING_FIELD, Cres.get().getString("id"), "", false, ContextUtils.GROUP_DEFAULT);
    FIFT_GET_MODULE_DEPENDENCIES.addField(id);
    
    FieldFormat version = FieldFormat.create(FIF_GET_MODULE_DEPENDENCIES_VERSION, FieldFormat.STRING_FIELD, Cres.get().getString("version"), "", false, ContextUtils.GROUP_DEFAULT);
    FIFT_GET_MODULE_DEPENDENCIES.addField(version);
  }
  
  public static final String FOF_GET_MODULE_DEPENDENCIES_ID = "id";
  public static final String FOF_GET_MODULE_DEPENDENCIES_VERSION = "version";
  public static final TableFormat FOFT_GET_MODULE_DEPENDENCIES = new TableFormat();
  static
  {
    FieldFormat id = FieldFormat.create(FOF_GET_MODULE_DEPENDENCIES_ID, FieldFormat.STRING_FIELD, Cres.get().getString("id"), "", false, ContextUtils.GROUP_DEFAULT);
    FOFT_GET_MODULE_DEPENDENCIES.addField(id);
    
    FieldFormat version = FieldFormat.create(FOF_GET_MODULE_DEPENDENCIES_VERSION, FieldFormat.STRING_FIELD, Cres.get().getString("version"), "", false, ContextUtils.GROUP_DEFAULT);
    FOFT_GET_MODULE_DEPENDENCIES.addField(version);
  }
  
  public static final String FIF_GET_MODULE_ARCHIVE_ID = "id";
  public static final TableFormat FIFT_GET_MODULE_ARCHIVE = new TableFormat(1, 1);
  
  static
  {
    FieldFormat id = FieldFormat.create(FIF_GET_MODULE_ARCHIVE_ID, FieldFormat.STRING_FIELD, Cres.get().getString("id"), "", false, ContextUtils.GROUP_DEFAULT);
    FIFT_GET_MODULE_ARCHIVE.addField(id);
  }
  
  public static final String FOF_GET_CONTENT_ARCHIVE_DATA = "data";
  public static final TableFormat FOFT_GET_CONTENT_ARCHIVE = new TableFormat(1, 1);
  
  static
  {
    FieldFormat id = FieldFormat.create(FOF_GET_CONTENT_ARCHIVE_DATA, FieldFormat.DATA_FIELD, Cres.get().getString("data"));
    FOFT_GET_CONTENT_ARCHIVE.addField(id);
  }
  
  public static final String FOF_GET_SELECTION_MODULES_VALUE = "value";
  public static final String FOF_GET_SELECTION_MODULES_DESCRIPTION = "description";
  public static final TableFormat FOFT_GET_SELECTION_MODULES = new TableFormat();
  
  static
  {
    FieldFormat id = FieldFormat.create(FOF_GET_SELECTION_MODULES_VALUE, FieldFormat.STRING_FIELD, Cres.get().getString("value"));
    FOFT_GET_SELECTION_MODULES.addField(id);
    
    FieldFormat desc = FieldFormat.create(FOF_GET_SELECTION_MODULES_DESCRIPTION, FieldFormat.STRING_FIELD, Cres.get().getString("description"));
    FOFT_GET_SELECTION_MODULES.addField(desc);
  }
}
