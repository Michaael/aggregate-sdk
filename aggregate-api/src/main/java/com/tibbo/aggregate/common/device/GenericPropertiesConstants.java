package com.tibbo.aggregate.common.device;

import com.tibbo.aggregate.common.server.DeviceContextConstants;

public interface GenericPropertiesConstants
{
  int METADATA_NONE = 0;
  int METADATA_NORMAL = 1;
  int METADATA_FULL = 2;
  
  int ACTIVE_ENTITIES_ALL = 0;
  int ACTIVE_ENTITIES_SELECTED = 1;
  
  int CACHE_DATABASE = 0;
  int CACHE_MEMORY = 1;
  
  public static final String FIELD_NAME = "name";
  public static final String FIELD_DESCRIPTION = "description";
  public static final String FIELD_SHOW_FULL_DEVICE_DESCRIPTION = "showFullDeviceDescription";
  public static final String FIELD_TYPE = DeviceContextConstants.VF_GENERIC_PROPERTIES_TYPE;
  public static final String FIELD_SYNC_PERIOD = "syncPeriod";
  public static final String FIELD_START_SYNC_ON_SETTING_CHANGE = "startSyncOnSettingChange";
  public static final String FIELD_INTERRUPT_ON_ERROR = "interruptOnError";
  public static final String FIELD_SUSPEND = "suspend";
  public static final String FIELD_SETTINGS_DEFAULT_QUALITY = "settingsDefaultQuality";
  public static final String FIELD_EXTENDED_STATUS = "extendedStatus";
  public static final String FIELD_TIME_ZONE = "timeZone";
  public static final String FIELD_METADATA = "metadata";
  public static final String FIELD_ACTIVE_ENTITIES = "activeEntities";
  public static final String FIELD_CACHE = "cache";
  public static final String FIELD_EVENT_STORAGE_PERIOD = "eventStoragePeriod";
  public static final String FIELD_DEPENDENCY = "dependency";
  public static final String FIELD_STATUS = "status";
  public static final String FIELD_COLOR = "color";
  public static final String FIELD_LATITUDE = "latitude";
  public static final String FIELD_LONGITUDE = "longitude";
  public static final String FIELD_LOCATION_STORAGE_PERIOD = "locationStoragePeriod";
  public static final String FIELD_OFFLINE_ALERT = "offlineAlert";
  public static final String FIELD_MAX_SYNC_LOG_ENTRIES = "maxSyncLogEntries";
  public static final String FIELD_VIRTUAL_NETWORK = "virtualNetwork";
  public static final String FIELD_DISABLE_SYNCHRONOUS_SETTING_VALUE_RW = "disableSynchronousSettingValueRW";
}
