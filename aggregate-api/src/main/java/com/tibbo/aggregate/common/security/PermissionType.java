package com.tibbo.aggregate.common.security;

public class PermissionType
{
  private int pattern;
  private String name;
  private String description;
  
  public PermissionType(int pattern, String name, String description)
  {
    super();
    this.pattern = pattern;
    this.name = name;
    this.description = description;
  }
  
  public int getPattern()
  {
    return pattern;
  }
  
  public void setPattern(int pattern)
  {
    this.pattern = pattern;
  }
  
  public String getName()
  {
    return name;
  }
  
  public void setName(String name)
  {
    this.name = name;
  }
  
  public String getDescription()
  {
    return description;
  }
  
  public void setDescription(String description)
  {
    this.description = description;
  }
}
