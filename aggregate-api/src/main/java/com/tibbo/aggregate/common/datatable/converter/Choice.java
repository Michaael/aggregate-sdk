package com.tibbo.aggregate.common.datatable.converter;

import java.util.*;

public class Choice
{
  private static final String WHITESPACE_PATTERN = "\\W";
  
  private String name;
  private String description;
  private Object object;
  
  public Choice(String description, Object object)
  {
    super();
    this.name = description.toLowerCase(Locale.ENGLISH).replaceAll(WHITESPACE_PATTERN, "");
    this.description = description;
    this.object = object;
  }
  
  public Choice(String name, String description, Object object)
  {
    super();
    this.name = name;
    this.description = description;
    this.object = object;
  }
  
  public String getName()
  {
    return name;
  }
  
  public String getDescription()
  {
    return description;
  }
  
  public Object getObject()
  {
    return object;
  }
}
