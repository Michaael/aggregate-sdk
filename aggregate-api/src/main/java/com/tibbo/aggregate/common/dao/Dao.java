package com.tibbo.aggregate.common.dao;

public interface Dao
{
  boolean isManaged(String table);

  void shutdown();
}
