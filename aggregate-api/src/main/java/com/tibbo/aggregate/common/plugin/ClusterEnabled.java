package com.tibbo.aggregate.common.plugin;

import com.tibbo.aggregate.common.dao.DatabaseClusterManager;
import com.tibbo.aggregate.common.server.ServerConfig;

/**
 * This interface marks a persistent plugin as cluster enabled which
 * means that the plugin exposes additional operation to manage the cluster storages
 */
public interface ClusterEnabled {

    /**
     * Method returns a database cluster manager that provide cluster related API
     * that manages the storage cluster
     * @param serverConfig the config of the current server instance
     * @param params arbitrary params that might be used for instantiating the cluster manager
     * @return the cluster manager for the cluster of storages
     */
    DatabaseClusterManager getClusterManager(ServerConfig serverConfig, String... params);

}
