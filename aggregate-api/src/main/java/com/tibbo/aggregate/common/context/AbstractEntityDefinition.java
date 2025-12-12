package com.tibbo.aggregate.common.context;

import com.tibbo.aggregate.common.*;
import com.tibbo.aggregate.common.datatable.*;
import com.tibbo.aggregate.common.datatable.validator.*;

public class AbstractEntityDefinition implements EntityDefinition
{
  private String name;
  private String description;
  private String help;
  private String group;
  private Integer index;
  private String iconId;
  private Object owner;
  
  @Override
  public String getName()
  {
    return name;
  }
  
  public void setName(String name)
  {
    if (Log.CONTEXT.isDebugEnabled())
    {
      try
      {
        ValidatorHelper.NAME_SYNTAX_VALIDATOR.validate(name);
      }
      catch (ValidationException ve)
      {
        Log.CONTEXT.debug(getClass().getSimpleName() + " name '" + name + "' breaks naming policy. The entity can be broken after ecnoding->decoding sequence.", new Exception());
      }
    }
    this.name = name;
  }
  
  public void setDescription(String description)
  {
    this.description = description;
  }
  
  public void setHelp(String help)
  {
    this.help = help;
  }
  
  public void setGroup(String group)
  {
    this.group = group;
  }
  
  @Override
  public String getDescription()
  {
    return description;
  }
  
  @Override
  public String getHelp()
  {
    return help;
  }
  
  @Override
  public String getGroup()
  {
    return group;
  }
  
  @Override
  public Integer getIndex()
  {
    return index;
  }
  
  public void setIndex(Integer index)
  {
    this.index = index;
  }
  
  public void setIconId(String iconId)
  {
    this.iconId = iconId;
  }
  
  @Override
  public String getIconId()
  {
    return iconId;
  }
  
  @Override
  public Object getOwner()
  {
    return owner;
  }
  
  public void setOwner(Object owner)
  {
    this.owner = owner;
  }
  
  @Override
  public String toString()
  {
    return description != null ? description : name;
  }
  
  @Override
  public String toDetailedString()
  {
    return description != null ? description + " (" + name + ")" : name;
  }
  
  public Integer getEntityType()
  {
    return null;
  }
}
