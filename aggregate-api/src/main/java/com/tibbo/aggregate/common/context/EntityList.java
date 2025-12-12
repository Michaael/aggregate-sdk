package com.tibbo.aggregate.common.context;

import java.util.*;

import com.tibbo.aggregate.common.datatable.*;

public class EntityList implements Cloneable, Iterable<EntityReference>
{
  public static final String FIELD_CONTEXT = "context";
  public static final String FIELD_ENTITY = "entity";
  
  public final static TableFormat FORMAT = new TableFormat();
  static
  {
    FORMAT.addField("<" + FIELD_CONTEXT + "><S>");
    FORMAT.addField("<" + FIELD_ENTITY + "><S>");
  }
  
  private List<EntityReference> entities = new LinkedList<>();
  
  public EntityList()
  {
    
  }
  
  public EntityList(DataTable data)
  {
    for (DataRecord rec : data)
    {
      entities.add(new EntityReference(rec.getString(FIELD_CONTEXT), rec.getString(FIELD_ENTITY)));
    }
  }
  
  public EntityList(String context, String entity)
  {
    add(context, entity);
  }
  
  public EntityList(EntityReference ref)
  {
    entities.add(ref);
  }
  
  public boolean includes(EntityReference ref)
  {
    return includes(ref.getContext(), ref.getEntity());
  }
  
  public boolean includes(String context, String entity)
  {
    for (EntityReference ref : entities)
    {
      if (ref.getContext().equals(context) && ref.getEntity().equals(entity))
      {
        return true;
      }
    }
    
    return false;
  }
  
  public synchronized DataTable toDataTable()
  {
    DataTable tab = new SimpleDataTable(FORMAT);
    
    for (EntityReference ref : entities)
    {
      tab.addRecord().addString(ref.getContext()).addString(ref.getEntity());
    }
    
    return tab;
  }
  
  public boolean isEmpty()
  {
    return entities.isEmpty();
  }
  
  public void add(String context, String entity)
  {
    if (!includes(context, entity))
    {
      add(new EntityReference(context, entity));
    }
  }
  
  public void add(EntityReference ref)
  {
    if (!includes(ref))
    {
      entities.add(ref);
    }
  }
  
  public List<EntityReference> getEntities()
  {
    return entities;
  }
  
  public Iterator<EntityReference> iterator()
  {
    return entities.iterator();
  }
  
  @Override
  public String toString()
  {
    return entities.toString();
  }
  
  @Override
  public EntityList clone()
  {
    try
    {
      EntityList clone = (EntityList) super.clone();
      clone.entities = new LinkedList<>();
      for (EntityReference er : entities)
      {
        clone.entities.add(er.clone());
      }
      return clone;
    }
    catch (CloneNotSupportedException ex)
    {
      throw new IllegalStateException(ex.getMessage(), ex);
    }
  }
  
  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((entities == null) ? 0 : entities.hashCode());
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
    EntityList other = (EntityList) obj;
    if (entities == null)
    {
      if (other.entities != null)
        return false;
    }
    else if (!entities.equals(other.entities))
      return false;
    return true;
  }
}
