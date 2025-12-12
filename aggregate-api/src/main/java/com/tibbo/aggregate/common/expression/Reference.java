package com.tibbo.aggregate.common.expression;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.tibbo.aggregate.common.context.ContextUtils;
import com.tibbo.aggregate.common.util.CloneUtils;
import com.tibbo.aggregate.common.util.Pair;
import com.tibbo.aggregate.common.util.PublicCloneable;
import com.tibbo.aggregate.common.util.StringUtils;

public class Reference implements Cloneable, PublicCloneable
{
  // Format: schema/server^context:entity('param1', expression, null, ...)$field[row]#property
  
  public static final String SCHEMA_FORM = "form";
  public static final String SCHEMA_WEB = "web";
  public static final String SCHEMA_TABLE = "table";
  public static final String SCHEMA_STATISTICS = "statistics";
  public static final String SCHEMA_GRANULATION = "granulation";
  public static final String SCHEMA_ENVIRONMENT = "env";
  public static final String SCHEMA_PARENT = "parent";
  public static final String SCHEMA_MENU = "menu";
  
  // Deprecated in favour of new action reference syntax: context.path:action!
  // Support should be terminated in AggreGate 6
  public static final String SCHEMA_ACTION = "action";
  
  public static final char EVENT_SIGN = '@';
  public static final char ACTION_SIGN = '!';
  public static final char PARAMS_BEGIN = '(';
  public static final char PARAMS_END = ')';
  public static final char SCHEMA_END = '/';
  public static final char SERVER_END = '^';
  public static final char CONTEXT_END = ':';
  public static final char FIELD_BEGIN = '$';
  public static final char FIELD_SEPARATOR = '.';
  public static final char ROW_BEGIN = '[';
  public static final char ROW_END = ']';
  public static final char PROPERTY_BEGIN = '#';
  
  public static final Integer APPEARANCE_LINK = 1;
  public static final Integer APPEARANCE_BUTTON = 2;
  
  private String image;
  
  private String schema;
  private String server;
  private String context;
  private String entity;
  private int entityType = ContextUtils.ENTITY_VARIABLE;
  private List<Object> parameters = new LinkedList<>(); // May contain Strings, Expressions and NULLs
  private List<Pair<String, Integer>> fields = new LinkedList();
  private String property;
  private Integer appearance = APPEARANCE_LINK;
  
  public Reference()
  {
    
  }
  
  public Reference(String source)
  {
    parse(source);
  }
  
  public Reference(String server, String context)
  {
    super();
    this.server = server;
    this.context = context;
  }
  
  public Reference(String entity, int entityType)
  {
    super();
    this.entity = entity;
    this.entityType = entityType;
  }
  
  public Reference(String entity, int entityType, String field)
  {
    super();
    this.entity = entity;
    this.entityType = entityType;
    this.fields.add(new Pair(field, null));
  }
  
  public Reference(String entity, int entityType, String field, int line)
  {
    super();
    this.entity = entity;
    this.entityType = entityType;
    this.fields.add(new Pair(field, line));
  }
  
  public Reference(String context, String entity, int entityType, String field)
  {
    this(entity, entityType, field);
    this.context = context;
  }
  
  public Reference(String context, String entity, int entityType)
  {
    this.context = context;
    this.entity = entity;
    this.entityType = entityType;
  }
  
  public Reference(String context, String function, Object[] parameters)
  {
    super();
    this.context = context;
    this.entity = function;
    this.entityType = ContextUtils.ENTITY_FUNCTION;
    Collections.addAll(this.parameters, parameters);
  }
  
  protected void parse(String source)
  {
    source = source.trim();
    
    boolean isFunction = false;
    boolean isEvent = false;
    boolean isAction = false;
    
    image = source;
    
    String src = image;
    
    int paramsBegin = src.indexOf(PARAMS_BEGIN);
    int paramsEnd = src.lastIndexOf(PARAMS_END);
    
    if (paramsBegin != -1)
    {
      if (paramsEnd == -1)
      {
        throw new IllegalArgumentException("No closing ')' for parameters");
      }
      
      int actionSignPos = src.lastIndexOf(ACTION_SIGN);
      
      if (actionSignPos == paramsEnd + 1)
      {
        isAction = true;
        
        String paramsSrc = src.substring(paramsBegin + 1, paramsEnd);
        
        parameters = ExpressionUtils.getFunctionParameters(paramsSrc, true);
        
        entityType = ContextUtils.ENTITY_ACTION;
        
        src = src.substring(0, paramsBegin) + src.substring(actionSignPos + 1);
      }
      else
      {
        isFunction = true;
        
        String paramsSrc = src.substring(paramsBegin + 1, paramsEnd);
        
        parameters = ExpressionUtils.getFunctionParameters(paramsSrc, true);
        
        entityType = ContextUtils.ENTITY_FUNCTION;
        
        src = src.substring(0, paramsBegin) + src.substring(paramsEnd + 1);
      }
    }
    else
    {
      int eventSignPos = src.lastIndexOf(EVENT_SIGN);
      
      if (eventSignPos != -1)
      {
        isEvent = true;
        
        entityType = ContextUtils.ENTITY_EVENT;
        
        src = src.substring(0, eventSignPos) + src.substring(eventSignPos + 1);
      }
      else
      {
        int actionSignPos = src.lastIndexOf(ACTION_SIGN);
        
        if (actionSignPos != -1)
        {
          isAction = true;
          
          entityType = ContextUtils.ENTITY_ACTION;
          
          src = src.substring(0, actionSignPos) + src.substring(actionSignPos + 1);
        }
      }
    }
    
    int schemaEnd = src.indexOf(SCHEMA_END);
    
    if (schemaEnd != -1)
    {
      schema = src.substring(0, schemaEnd);
      src = src.substring(schemaEnd + 1);
    }
    
    int serverEnd = src.indexOf(SERVER_END);
    
    if (serverEnd != -1)
    {
      server = src.substring(0, serverEnd);
      src = src.substring(serverEnd + 1);
    }
    
    int contextEnd = src.indexOf(CONTEXT_END);
    
    if (contextEnd != -1)
    {
      context = src.substring(0, contextEnd);
      src = src.substring(contextEnd + 1);
    }
    
    int propertyBegin = src.indexOf(PROPERTY_BEGIN);
    
    if (propertyBegin != -1)
    {
      property = src.substring(propertyBegin + 1);
      src = src.substring(0, propertyBegin);
    }
    
    int fieldBegin = src.indexOf(FIELD_BEGIN);
    
    if (fieldBegin != -1)
    {
      entity = src.substring(0, fieldBegin);
      if (fieldBegin != src.length() - 1)
      {
        src = src.substring(fieldBegin + 1);
      }
      else
      {
        return;
      }
    }
    else if (!src.isEmpty())
    {
      if (context != null || isFunction || isEvent || isAction)
      {
        entity = src;
        return;
      }
    }
    
    if (src.isEmpty()) // No fields
    {
      return;
    }
    
    List<String> flds = StringUtils.split(src, FIELD_SEPARATOR);
    
    Integer row = null;
    String field;
    
    for (String fld : flds)
    {
      int rowBegin = fld.indexOf(ROW_BEGIN);
      int rowEnd = fld.indexOf(ROW_END);
      
      if (rowBegin != -1)
      {
        
        if (rowEnd == -1)
        {
          throw new IllegalArgumentException("No closing ']' in row reference");
        }
        
        row = Integer.valueOf(fld.substring(rowBegin + 1, rowEnd));
        
        field = fld.substring(0, rowBegin);
      }
      else
      {
        field = fld;
      }

      fields.add(new Pair(field, row));
    }
  }
  
  public String getServer()
  {
    return server;
  }
  
  public String getContext()
  {
    return context;
  }
  
  public String getEntity()
  {
    return entity;
  }
  
  public int getEntityType()
  {
    return entityType;
  }
  
  public String getField()
  {
    Pair<String, Integer> field = !fields.isEmpty() ? fields.get(0) : null;
    return field != null ? field.getFirst() : null;
  }
  
  public List<Pair<String, Integer>> getFields()
  {
    return fields;
  }
  
  public List<Object> getParameters()
  {
    return parameters;
  }
  
  public Integer getRow()
  {
    Pair<String, Integer> field = !fields.isEmpty() ? fields.get(0) : null;
    return field != null ? field.getSecond() : null;
  }
  
  public String getSchema()
  {
    return schema;
  }
  
  public String getProperty()
  {
    return property;
  }
  
  public String getImage()
  {
    if (image != null)
    {
      return image;
    }
    else
    {
      return createImage();
    }
  }
  
  private String createImage()
  {
    StringBuilder sb = new StringBuilder();
    
    if (schema != null)
    {
      sb.append(schema);
      sb.append(SCHEMA_END);
    }
    
    if (server != null)
    {
      sb.append(server);
      sb.append(SERVER_END);
    }
    
    if (context != null)
    {
      sb.append(context);
      sb.append(CONTEXT_END);
    }
    
    if (entity != null)
    {
      sb.append(entity);
      
      if (entityType == ContextUtils.ENTITY_FUNCTION)
      {
        sb.append(PARAMS_BEGIN);
        sb.append(ExpressionUtils.getFunctionParameters(parameters));
        sb.append(PARAMS_END);
      }
      
      if (entityType == ContextUtils.ENTITY_EVENT)
      {
        sb.append(EVENT_SIGN);
      }
      
      if (entityType == ContextUtils.ENTITY_ACTION)
      {
        if (!parameters.isEmpty())
        {
          sb.append(PARAMS_BEGIN);
          sb.append(ExpressionUtils.getFunctionParameters(parameters));
          sb.append(PARAMS_END);
        }
        
        sb.append(ACTION_SIGN);
      }
      
      if (entityType == ContextUtils.ENTITY_INSTANCE)
      {
        if (!parameters.isEmpty())
        {
          sb.append(PARAMS_BEGIN);
          sb.append(ExpressionUtils.getFunctionParameters(parameters));
          sb.append(PARAMS_END);
        }
      }
      
      if (!fields.isEmpty() || (context == null && entityType == ContextUtils.ENTITY_VARIABLE))
      {
        sb.append(FIELD_BEGIN);
      }
    }
    
    int i = 0;
    
    for (Pair<String, Integer> field : fields)
    {
      if (i > 0)
      {
        sb.append(FIELD_SEPARATOR);
      }
      
      if (field.getFirst() != null)
      {
        sb.append(field.getFirst());
      }
      
      if (field.getSecond() != null)
      {
        sb.append(ROW_BEGIN);
        sb.append(field.getSecond());
        sb.append(ROW_END);
      }
      
      i++;
    }
    
    if (property != null)
    {
      sb.append(PROPERTY_BEGIN);
      sb.append(property);
    }
    
    image = sb.toString();
    
    return image;
  }
  
  @Override
  public String toString()
  {
    return getImage();
  }
  
  public void setContext(String context)
  {
    this.context = context;
    image = null;
  }
  
  public void setEntity(String entity)
  {
    this.entity = entity;
    image = null;
  }
  
  public void setEntityType(int entityType)
  {
    this.entityType = entityType;
    image = null;
  }
  
  public void addParameter(String parameter)
  {
    parameters.add(parameter);
    image = null;
  }
  
  public void addParameter(Expression parameter)
  {
    parameters.add(parameter);
    image = null;
  }
  
  public void setField(String field)
  {
    if (fields.isEmpty())
    {
      fields.add(new Pair(field, null));
    }
    else
    {
      fields.get(0).setFirst(field);
    }
    image = null;
  }
  
  public void setFields(List<Pair<String, Integer>> fields)
  {
    this.fields = fields;
    image = null;
  }
  
  public void addField(String field)
  {
    this.fields.add(new Pair(field, null));
    image = null;
  }
  
  public void addField(String field, Integer record)
  {
    this.fields.add(new Pair(field, record));
    image = null;
  }
  
  public void setProperty(String property)
  {
    this.property = property;
    image = null;
  }
  
  public void setSchema(String schema)
  {
    this.schema = schema;
    image = null;
  }
  
  public void setRow(Integer row)
  {
    if (fields.isEmpty())
    {
      fields.add(new Pair(null, row));
    }
    else
    {
      fields.get(0).setSecond(row);
    }
    image = null;
  }
  
  public void setServer(String server)
  {
    this.server = server;
    image = null;
  }
  
  @Override
  public Reference clone()
  {
    try
    {
      Reference cl = (Reference) super.clone();
      
      cl.fields = (List) CloneUtils.deepClone(fields);
      
      return cl;
    }
    catch (CloneNotSupportedException ex)
    {
      throw new IllegalStateException(ex.getMessage(), ex);
    }
  }
  
  @Override
  public boolean equals(Object obj)
  {
    final boolean isReferenceNotNull = obj instanceof Reference;
    return isReferenceNotNull && getImage().equals(((Reference) obj).getImage());
    
  }
  
  @Override
  public int hashCode()
  {
    return getImage().hashCode();
  }
  
  public Integer getAppearance()
  {
    return appearance;
  }
  
  public void setAppearance(Integer appearance)
  {
    this.appearance = appearance;
  }
}
