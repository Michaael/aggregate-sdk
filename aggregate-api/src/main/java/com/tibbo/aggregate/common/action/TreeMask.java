package com.tibbo.aggregate.common.action;

import com.tibbo.aggregate.common.action.*;

public class TreeMask implements ResourceMask<TreeMask>
{
  public static final char SEPARATOR = '.';
  
  private String mask;
  
  public TreeMask()
  {
  }
  
  public TreeMask(String mask)
  {
    this.mask = mask;
  }
  
  public boolean includes(TreeMask resourceMask)
  {
    if (resourceMask == null)
    {
      throw new NullPointerException();
    }
    
    if (mask == null)
    {
      return true;
    }
    
    if (resourceMask.mask == null)
    {
      return false;
    }
    
    return resourceMask.mask.startsWith(mask) && resourceMask.mask.charAt(mask.length()) == SEPARATOR;
  }
  
  public String toString()
  {
    return mask == null ? "" : mask;
  }
}
