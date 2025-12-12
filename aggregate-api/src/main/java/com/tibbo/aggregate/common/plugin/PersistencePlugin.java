package com.tibbo.aggregate.common.plugin;

import com.tibbo.aggregate.common.dao.*;
import com.tibbo.aggregate.common.server.*;

public interface PersistencePlugin
{
  DaoFactory createDaoFactory(DaoFactory parent, ServerRuntimeConfig runtimeConfig, ServerConfig config, EventStorageManager manager);
}
