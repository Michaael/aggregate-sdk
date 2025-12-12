package com.tibbo.aggregate.common.dao;

import java.util.*;

import com.tibbo.aggregate.common.datatable.*;
import com.tibbo.aggregate.common.event.*;

public interface EventStorage
{
  int STORAGE_DEFAULT = -1;

  int getType();
  void setType(int type);
  
  String getTable();

  void setTable(String table);

  String getContext();

  void setContext(String context);

  String getEvent();

  void setEvent(String event);
  
  List<PersistenceBinding> getBindings();
  
  void setBindings(List<PersistenceBinding> bindings);
  
  void addBinding(PersistenceBinding binding);
  
  boolean isPersistContext();
  
  void setPersistContext(boolean persistContext);
  
  boolean isPersistName();
  
  void setPersistName(boolean persistName);
  
  boolean isPersistExpirationtime();
  
  void setPersistExpirationtime(boolean persistExpirationtime);
  
  boolean isPersistLevel();
  
  void setPersistLevel(boolean persistLevel);
  
  boolean isPersistPermissions();
  
  void setPersistPermissions(boolean persistPermissions);
  
  boolean isPersistCount();
  
  void setPersistCount(boolean persistCount);
  
  boolean isPersistAcknowledgements();
  
  void setPersistAcknowledgements(boolean persistAcknowledgements);
  
  boolean isPersistEnrichments();
  
  void setPersistEnrichments(boolean persistEnrichments);
  
  boolean isPersistFormat();
  
  void setPersistFormat(boolean persistFormat);
  
  boolean isPersistData();
  
  void setPersistData(boolean persistData);
  
  boolean isDefault();
  
  DataTable toDataTable();
  
}