package com.tibbo.aggregate.common.action;

import com.tibbo.aggregate.common.datatable.*;

public class EntityRelatedActionDescriptor extends AggreGateBean
{
  public static final String FIELD_MASK = "mask";
  public static final String FIELD_ENTITY = "entity";
  public static final String FIELD_TARGET = "target";
  public static final String FIELD_ACTION = "action";
  public static final String FIELD_DESCRIPTION = "description";
  public static final String FIELD_ICON = "icon";
  
  public static final TableFormat FORMAT = new TableFormat();
  static
  {
    FORMAT.addField("<" + FIELD_MASK + "><S><F=N>");
    FORMAT.addField("<" + FIELD_ENTITY + "><S><F=N>");
    FORMAT.addField("<" + FIELD_TARGET + "><S><F=N>");
    FORMAT.addField("<" + FIELD_ACTION + "><S>");
    FORMAT.addField("<" + FIELD_DESCRIPTION + "><S>");
    FORMAT.addField("<" + FIELD_ICON + "><S><F=N>");
  }
  
  /**
   * Mask of contexts for those action is available. If NULL, action is available for all contexts.
   */
  private String mask;
  
  /**
   * Entity, group of entities (ending with .*) for those action is available of NULL for any entity.
   */
  private String entity;
  
  /**
   * Path of context to call action from. If NULL, action will be called from current context. May include username pattern ('%'), that will be substituted by the login of current user.
   */
  private String target;
  
  /**
   * Name of the action to call.
   */
  private String action;
  
  /**
   * Description of the action.
   */
  private String description;
  
  /**
   * Icon ID of the action.
   */
  private String icon;
  
  public EntityRelatedActionDescriptor(String mask, String entity, String target, String action, String description, String iconId)
  {
    this();
    this.mask = mask;
    this.entity = entity;
    this.target = target;
    this.action = action;
    this.description = description;
    this.icon = iconId;
  }
  
  public EntityRelatedActionDescriptor(DataRecord data)
  {
    super(FORMAT, data);
  }
  
  public EntityRelatedActionDescriptor()
  {
    super(FORMAT);
  }
  
  public String getMask()
  {
    return mask;
  }
  
  public void setMask(String mask)
  {
    this.mask = mask;
  }
  
  public String getEntity()
  {
    return entity;
  }
  
  public String getTarget()
  {
    return target;
  }
  
  public void setTarget(String target)
  {
    this.target = target;
  }
  
  public String getAction()
  {
    return action;
  }
  
  public void setAction(String action)
  {
    this.action = action;
  }
  
  public String getDescription()
  {
    return description;
  }
  
  public String getIcon()
  {
    return icon;
  }
  
  public void setEntity(String group)
  {
    this.entity = group;
  }
  
  public void setDescription(String description)
  {
    this.description = description;
  }
  
  public void setIcon(String icon)
  {
    this.icon = icon;
  }
}
