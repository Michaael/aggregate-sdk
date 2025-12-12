package com.tibbo.aggregate.common.action;

import com.tibbo.aggregate.common.context.*;
import com.tibbo.aggregate.common.datatable.*;
import com.tibbo.aggregate.common.util.*;
import org.apache.commons.lang3.StringUtils;

public class EntityRelatedActions
{
  public static final String FIELD_CONTEXT = "context";
  public static final String FIELD_ENTITY = "entity";
  public static final String FIELD_RECORD = "record";
  public static final String FIELD_FIELD = "field";
  
  public static TableFormat EXECUTION_FORMAT = new TableFormat();
  static
  {
    EXECUTION_FORMAT.addField("<" + FIELD_CONTEXT + "><S>");
    EXECUTION_FORMAT.addField("<" + FIELD_ENTITY + "><S>");
    EXECUTION_FORMAT.addField("<" + FIELD_RECORD + "><T><F=N>");
    EXECUTION_FORMAT.addField("<" + FIELD_FIELD + "><S><F=N>");
  }
  
  public static Context getTargetContext(EntityRelatedActionDescriptor ad, Context context, String entity, int entityType, CallerController caller)
  {
    if (ad.getMask() != null && !ContextUtils.matchesToMask(ad.getMask(), context.getPath()))
    {
      return null;
    }
    
    final Context con = ad.getTarget() != null ? context.get(ad.getTarget(), caller) : context;
    
    if (con == null)
    {
      return null;
    }
    
    return allowedContextOrNull(entity, entityType, ad.getEntity(), con);
  }
  
  protected static Context allowedContextOrNull(String entity, int entityType, String allowedEntities, Context con)
  {
    if (allowedEntities == null)
    {
      return con;
    }
    
    String[] allowedEntitiesArray = StringUtils.split(allowedEntities, ContextUtils.MASK_LIST_SEPARATOR);
    for (String allowedEntity : allowedEntitiesArray)
    {
      allowedEntity = StringUtils.trim(allowedEntity);
      String allowedGroup = ContextUtils.getGroupName(allowedEntity);
      
      if (allowedGroup == null)
      {
        if (Util.equals(allowedEntity, entity))
        {
          return con;
        }
      }
      else
      {
        String entityBaseGroup = ContextUtils.getBaseGroup(entityGroup(entity, entityType, con));
        
        if (Util.equals(allowedGroup, entityBaseGroup))
        {
          return con;
        }
      }
    }
    
    return null;
  }
  
  private static String entityGroup(String entity, int entityType, Context con)
  {
    if (con == null)
      return null;
    
    switch (entityType)
    {
      case ContextUtils.ENTITY_VARIABLE:
        if (con.getVariableDefinition(entity) == null)
          return null;
        return con.getVariableDefinition(entity).getGroup();
      
      case ContextUtils.ENTITY_FUNCTION:
        if (con.getFunctionDefinition(entity) == null)
          return null;
        return con.getFunctionDefinition(entity).getGroup();
      
      case ContextUtils.ENTITY_EVENT:
        if (con.getEventDefinition(entity) == null)
          return null;
        return con.getEventDefinition(entity).getGroup();
      
      default:
        throw new IllegalStateException("Unknown entity type: " + entityType);
    }
  }
}
