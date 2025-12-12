package com.tibbo.aggregate.common.widget.component;

import java.beans.*;

import com.tibbo.aggregate.common.datatable.*;

public interface IComponent
{
  
  String getKey();
  
  String getName();
  
  void addPropertyChangeListener(PropertyChangeListener listener);
  
  void removePropertyChangeListener(PropertyChangeListener listener);
  
  void setName(String name);
  
  Object getProperty(String name);
  
  void setProperty(String name, String value);
  
  boolean hasProperty(String name);
  
  DataTable getCustomProperty(String name);
  
  void setCustomProperty(String name, DataTable newDataTable);
  
  boolean hasCustomProperty(String name);
  
  void setOrCreateCustomProperty(String name, DataTable newDataTable, String description, String help);
  
  DataTable removeCustomProperty(String name);
  
  DataTable getCustomProperties();
  
  void setCustomProperties(DataTable value);
}
