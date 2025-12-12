package com.tibbo.aggregate.common.util;

import com.tibbo.aggregate.common.context.*;

public interface RemoteConnector
{
  ContextManager getContextManager();
  
  CallerController getCallerController();
  
  UserSettings getSettings();
}
