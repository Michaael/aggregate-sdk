package com.tibbo.aggregate.common.expression;

import java.util.*;

public interface AttributedObject
{
  public Object getValue();
  
  public Date getTimestamp();
  
  public void setTimestamp(Date timestamp);
  
  public Integer getQuality();
  
  public void setQuality(Integer quality);
  
}