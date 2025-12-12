package com.tibbo.aggregate.common.context;

import com.tibbo.aggregate.common.datatable.*;
import com.tibbo.aggregate.common.security.*;

public class FunctionDefinition extends AbstractEntityDefinition implements Cloneable, Comparable<FunctionDefinition>
{
  private TableFormat inputFormat;
  private TableFormat outputFormat;
  private boolean hidden = false;
  private Permissions permissions = null;
  
  private FunctionImplementation implementation;
  
  private boolean concurrent = false;
  
  public FunctionDefinition(String name, TableFormat inputFormat, TableFormat outputFormat)
  {
    init(name, inputFormat, outputFormat, null, null);
  }
  
  public FunctionDefinition(String name, TableFormat inputFormat, TableFormat outputFormat, String description)
  {
    init(name, inputFormat, outputFormat, description, null);
  }
  
  public FunctionDefinition(String name, TableFormat inputFormat, TableFormat outputFormat, String description, String group)
  {
    init(name, inputFormat, outputFormat, description, group);
  }
  
  private void init(String name, TableFormat inputFormat, TableFormat outputFormat, String description, String group)
  {
    setName(name);
    
    setInputFormat(inputFormat);
    setOutputFormat(outputFormat);
    
    setDescription(description);
    setGroup(group);
  }
  
  public TableFormat getInputFormat()
  {
    return inputFormat;
  }
  
  public TableFormat getOutputFormat()
  {
    return outputFormat;
  }
  
  public boolean isHidden()
  {
    return hidden;
  }
  
  public Permissions getPermissions()
  {
    return permissions;
  }
  
  public FunctionImplementation getImplementation()
  {
    return implementation;
  }
  
  public void setInputFormat(TableFormat inputFormat)
  {
    if (inputFormat != null)
    {
      inputFormat.makeImmutable(null);
    }
    this.inputFormat = inputFormat;
  }
  
  public void setOutputFormat(TableFormat outputFormat)
  {
    if (outputFormat != null)
    {
      outputFormat.makeImmutable(null);
    }
    this.outputFormat = outputFormat;
  }
  
  public void setHidden(boolean hidden)
  {
    this.hidden = hidden;
  }
  
  public void setPermissions(Permissions permissions)
  {
    this.permissions = permissions;
  }
  
  public boolean isConcurrent()
  {
    return concurrent;
  }
  
  public void setConcurrent(boolean concurrent)
  {
    this.concurrent = concurrent;
  }
  
  public void setImplementation(FunctionImplementation implementation)
  {
    this.implementation = implementation;
  }
  
  @Override
  public FunctionDefinition clone()
  {
    try
    {
      return (FunctionDefinition) super.clone();
    }
    catch (CloneNotSupportedException ex)
    {
      throw new IllegalStateException(ex.getMessage(), ex);
    }
  }
  
  @Override
  public int compareTo(FunctionDefinition d)
  {
    if (getIndex() != null || d.getIndex() != null)
    {
      Integer my = getIndex() != null ? getIndex() : new Integer(0);
      Integer other = d.getIndex() != null ? d.getIndex() : new Integer(0);
      return other.compareTo(my);
    }
    
    return 0;
  }
  
  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((getDescription() == null) ? 0 : getDescription().hashCode());
    result = prime * result + ((getGroup() == null) ? 0 : getGroup().hashCode());
    result = prime * result + ((getHelp() == null) ? 0 : getHelp().hashCode());
    result = prime * result + (hidden ? 1231 : 1237);
    result = prime * result + ((getIndex() == null) ? 0 : getIndex().hashCode());
    result = prime * result + ((inputFormat == null) ? 0 : inputFormat.hashCode());
    result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
    result = prime * result + ((outputFormat == null) ? 0 : outputFormat.hashCode());
    result = prime * result + ((implementation == null) ? 0 : implementation.hashCode());
    result = prime * result + ((permissions == null) ? 0 : permissions.hashCode());
    return result;
  }
  
  @Override
  public boolean equals(Object obj)
  {
    if (this == obj)
    {
      return true;
    }
    if (obj == null)
    {
      return false;
    }
    if (getClass() != obj.getClass())
    {
      return false;
    }
    FunctionDefinition other = (FunctionDefinition) obj;
    if (getDescription() == null)
    {
      if (other.getDescription() != null)
      {
        return false;
      }
    }
    else if (!getDescription().equals(other.getDescription()))
    {
      return false;
    }
    if (getGroup() == null)
    {
      if (other.getGroup() != null)
      {
        return false;
      }
    }
    else if (!getGroup().equals(other.getGroup()))
    {
      return false;
    }
    if (getHelp() == null)
    {
      if (other.getHelp() != null)
      {
        return false;
      }
    }
    else if (!getHelp().equals(other.getHelp()))
    {
      return false;
    }
    if (hidden != other.hidden)
    {
      return false;
    }
    if (getIndex() == null)
    {
      if (other.getIndex() != null)
      {
        return false;
      }
    }
    else if (!getIndex().equals(other.getIndex()))
    {
      return false;
    }
    if (inputFormat == null)
    {
      if (other.inputFormat != null)
      {
        return false;
      }
    }
    else if (!inputFormat.equals(other.inputFormat))
    {
      return false;
    }
    if (getName() == null)
    {
      if (other.getName() != null)
      {
        return false;
      }
    }
    else if (!getName().equals(other.getName()))
    {
      return false;
    }
    if (outputFormat == null)
    {
      if (other.outputFormat != null)
      {
        return false;
      }
    }
    else if (!outputFormat.equals(other.outputFormat))
    {
      return false;
    }
    if (implementation == null)
    {
      if (other.implementation != null)
      {
        return false;
      }
    }
    else if (!implementation.equals(other.implementation))
    {
      return false;
    }
    if (permissions == null)
    {
      if (other.permissions != null)
      {
        return false;
      }
    }
    else if (!permissions.equals(other.permissions))
    {
      return false;
    }
    return true;
  }
  
  @Override
  public Integer getEntityType()
  {
    return ContextUtils.ENTITY_FUNCTION;
  }
}
