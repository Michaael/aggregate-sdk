package com.tibbo.aggregate.common.action;

public class StringIdentifier
{
  private String id;
  
  protected StringIdentifier()
  {
    
  }
  
  public StringIdentifier(String id)
  {
    if (id == null)
    {
      throw new NullPointerException();
    }
    
    this.id = id;
  }
  
  public String getId()
  {
    return id;
  }
  
  public boolean equals(Object o)
  {
    if (!(o instanceof StringIdentifier))
    {
      return false;
    }
    
    StringIdentifier sid = (StringIdentifier) o;
    
    return id == null ? sid.id == null : id.equals(sid.id);
  }
  
  public int hashCode()
  {
    return id == null ? super.hashCode() : id.hashCode();
  }
  
  public String toString()
  {
    return id == null ? super.toString() : id;
  }
}
