package com.tibbo.aggregate.common.server;

import java.util.*;

import com.tibbo.aggregate.common.event.*;

public interface ServerRuntimeConfig
{
  
  public String getHomeDirectory();
  
  public String getDevicePluginsDir();
  
  public List<EventWriter> getEventWriters();
  
  public boolean isCommandLineCreateDatabase();
  
  public String getCommandLineConfigFilename();
  
  public boolean isCommandLineUpdateDatabase();
  
  public boolean isUpgradeMode();
  
  public boolean isGuiMode();
  
  public Map<String, String> getCommandLinePasswordChangesList();
  
  public boolean isFirstLaunch();
  
  public String getCommandLineDatabaseSchemaUpdateFilename();
  
  public String getCommandLineStartupScriptFilename();
  
  public boolean isServiceMode();
  
  public boolean isSafeMode();
  
  public boolean isPublicStore();
  
  public String getCommandLinePluginsFilename();
  
  public boolean isEditableAdminPermissions();
  
  public void setCommandLineConfigFilename(String commandLineConfigFilename);
  
  public void setCommandLineCreateDatabase(boolean commandLineCreateDatabase);
  
  public void setCommandLineUpdateDatabase(boolean commandLineUpdateDatabase);
  
  public void setUpgradeMode(boolean upgradeMode);
  
  public void setGuiMode(boolean guiMode);
  
  public void setFirstLaunch(boolean firstLaunch);
  
  public void setCommandLineDatabaseSchemaUpdateFilename(String commandLineDatabaseSchemaUpdateFilename);
  
  public void setCommandLineStartupScriptFilename(String commandLineStartupScriptFilename);
  
  public void setCommandLinePluginsFilename(String commandLinePluginsFilename);
  
  public void setEditableAdminPermissions(boolean editableAdminPermissions);
  
  public void setPublicStore(boolean publicStore);

  void setStartedOnCleanSchema(boolean startedOnCleanSchema);

  boolean isStartedOnCleanSchema();
  
}