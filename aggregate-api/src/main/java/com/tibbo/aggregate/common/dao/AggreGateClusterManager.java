package com.tibbo.aggregate.common.dao;

import java.util.List;

// This interface allows a client to do some basic operation with
// an AggreGate cluster that manager is instantiated for. The interface is
// only for AggreGate cluster managing.
public interface AggreGateClusterManager
{
  void startHeartbeat() throws DaoException;
  
  void updateNoteStatus(ClusterNodeStatus status) throws DaoException;
  
  List<ClusterNodeStatus> getNodeStatuses() throws DaoException;
  
  void resetHeartbeat() throws DaoException;
  
  void dispose();
}