package com.tibbo.aggregate.common.dao;

import java.util.*;

import com.tibbo.aggregate.common.datatable.*;

public interface GenericDao extends Dao
{
  List executeSelectQuery(String query) throws DaoException;
  
  List executeSelectQuery(String query, int numberOfRows, Map<String, Object> parameters) throws DaoException;
  
  int executeNativeUpdateQuery(String query) throws DaoException;
  
  DataTable executeNativeSelectQuery(String query) throws DaoException;
  
  void save(List objects) throws DaoException;
  
  void save(String table, List objects) throws DaoException;
}