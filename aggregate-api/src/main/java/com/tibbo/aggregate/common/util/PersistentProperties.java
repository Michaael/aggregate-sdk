package com.tibbo.aggregate.common.util;

import java.util.*;

import com.tibbo.aggregate.common.datatable.*;

public class PersistentProperties
{
  private final Map<String, DataTable> properties = new LinkedHashMap();
  
  public Map<String, DataTable> getProperties()
  {
    return properties;
  }
  
  public DataTable getProperty(String name)
  {
    return properties.get(name);
  }
}
