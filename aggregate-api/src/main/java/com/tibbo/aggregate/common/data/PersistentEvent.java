package com.tibbo.aggregate.common.data;

import java.io.*;
import java.util.*;

import com.tibbo.aggregate.common.context.*;
import com.tibbo.aggregate.common.datatable.encoding.*;
import com.tibbo.aggregate.common.event.*;
import com.tibbo.aggregate.common.expression.*;
import com.tibbo.aggregate.common.util.*;

public class PersistentEvent implements Cloneable, Serializable
{
  private static final long serialVersionUID = -1206684793970688104L;
  
  public static final String KEY_NAME_PREFIX = "key_";
  
  private Long id;
  
  private Date creationtime;
  
  private Date expirationtime;
  
  private String context;
  
  private String name;
  
  private int level;
  
  private String permissions;
  
  private int count;
  
  private byte[] ack;
  
  private byte[] format;
  
  private byte[] data;
  
  private byte[] enrichments;
  
  private final Map<String, Object> keys = new HashMap();
  
  public PersistentEvent()
  {
  }
  
  public PersistentEvent(Event event, EventDefinition ed)
  {
    this();
    
    PersistenceOptions po = ed != null ? ed.getPersistenceOptions() : null;
    
    id = event.getId();
    creationtime = event.getCreationtime();
    expirationtime = event.getExpirationtime();
    context = event.getContext();
    name = event.getName();
    level = event.getLevel();
    permissions = (po != null && po.isPersistPermissions() && event.getPermissions() != null) ? event.getPermissions().encode() : null;
    count = event.getCount();
    
    if (po != null && po.isPersistAcknowledgements() && event.getAcknowledgements().size() != 0)
    {
      ack = event.getAcknowledgementsTable().encode().getBytes(StringUtils.UTF8_CHARSET);
    }
    
    if (po != null && po.isPersistEnrichments() && event.getEnrichments().size() != 0)
    {
      enrichments = event.getEnrichmentsTable().encode().getBytes(StringUtils.UTF8_CHARSET);
    }
    
    boolean includeFormat = (po != null && po.isPersistFormat()) ? (ed != null ? (ed.getFormat() == null) : true) : false;
    format = includeFormat ? event.getData().getFormat().encode(false).getBytes(StringUtils.UTF8_CHARSET) : null;
    
    data = (po != null && po.isPersistData()) ? event.getData().getEncodedData(new ClassicEncodingSettings(false, true)).getBytes(StringUtils.UTF8_CHARSET) : new byte[0];
  }
  
  public Long getId()
  {
    return id;
  }
  
  public Date getCreationtime()
  {
    return creationtime;
  }
  
  public String getContext()
  {
    return context;
  }
  
  public String getName()
  {
    return name;
  }
  
  public Date getExpirationtime()
  {
    return expirationtime;
  }
  
  public byte[] getAck()
  {
    return ack;
  }
  
  public void setAck(byte[] ack)
  {
    this.ack = ack;
  }
  
  public byte[] getEnrichments()
  {
    return enrichments;
  }
  
  public void setEnrichments(byte[] enrichments)
  {
    this.enrichments = enrichments;
  }
  
  public void setId(Long id)
  {
    this.id = id;
  }
  
  public void setCreationtime(Date creationtime)
  {
    this.creationtime = creationtime;
  }
  
  public void setName(String name)
  {
    this.name = name;
  }
  
  public void setContext(String context)
  {
    this.context = context;
  }
  
  public void setExpirationtime(Date expirationtime)
  {
    this.expirationtime = expirationtime;
  }
  
  public void setLevel(int level)
  {
    this.level = level;
  }
  
  public int getLevel()
  {
    return level;
  }
  
  public String getPermissions()
  {
    return permissions;
  }
  
  public void setPermissions(String perms)
  {
    this.permissions = perms;
  }
  
  public int getCount()
  {
    return count;
  }
  
  public void setCount(Integer count)
  {
    this.count = count != null ? count : 1;
  }
  
  public byte[] getFormat()
  {
    return format;
  }
  
  public void setFormat(byte[] format)
  {
    this.format = format;
  }
  
  public byte[] getData()
  {
    return data;
  }
  
  public void setData(byte[] data)
  {
    this.data = data;
  }
  
  public Object getKey(String name)
  {
    return keys.get(name);
  }
  
  public void setKey(String name, Object value)
  {
    keys.put(name, value);
  }
  
  public Map<String, Object> getKeys()
  {
    return keys;
  }
  
  @Override
  public PersistentEvent clone()
  {
    try
    {
      return (PersistentEvent) super.clone();
    }
    catch (CloneNotSupportedException ex)
    {
      throw new IllegalStateException(ex);
    }
  }
  
  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    return result;
  }
  
  @Override
  public boolean equals(Object obj)
  {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    PersistentEvent other = (PersistentEvent) obj;
    if (id == null)
    {
      if (other.id != null)
        return false;
    }
    else if (!id.equals(other.id))
      return false;
    return true;
  }
  
  @Override
  public String toString()
  {
    return "PersistentEvent [id=" + id + ", context=" + context + ", name=" + name + "]";
  }
  
  public static String createPersistentFieldName(String databaseField)
  {
    return KEY_NAME_PREFIX + databaseField;
  }
  
  public static PersistenceBinding createPersistenceBinding(String databaseFieldName, String databaseFieldType, String databaseIndexName, String eventFieldName)
  {
    return new PersistenceBinding(databaseFieldName, databaseFieldType, databaseIndexName, eventFieldName != null ? new Expression(new Reference(eventFieldName)).toString() : null);
  }
}
