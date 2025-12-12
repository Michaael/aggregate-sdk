package com.tibbo.aggregate.common.discovery;

public class DeviceRecommendation
{
  private String name;
  private String description;
  
  public DeviceRecommendation()
  {
    super();
  }
  
  public DeviceRecommendation(String name, String description)
  {
    super();
    this.name = name;
    this.description = description;
  }
  
  public static DeviceRecommendation getBlank()
  {
    return new DeviceRecommendation();
  }
  
  public String getName()
  {
    return name;
  }
  
  public String getDescription()
  {
    return description;
  }
  
  @Override
  public String toString()
  {
    return "DeviceRecommendation [" + name + ", " + description + "]";
  }
  
}
