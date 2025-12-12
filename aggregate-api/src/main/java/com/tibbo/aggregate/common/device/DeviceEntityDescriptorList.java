package com.tibbo.aggregate.common.device;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.collect.ImmutableList;
import com.tibbo.aggregate.common.datatable.DataTable;
import com.tibbo.aggregate.common.datatable.DataTableConversion;
import com.tibbo.aggregate.common.datatable.DataTableException;

public class DeviceEntityDescriptorList implements DeviceEntities
{
  public static final DeviceEntityDescriptorList EMPTY = new DeviceEntityDescriptorList(ImmutableList.of());
  
  private final Map<String, DeviceEntityDescriptor> entities = new LinkedHashMap<>();
  
  public DeviceEntityDescriptorList(List<DeviceEntityDescriptor> entities)
  {
    super();
    
    for (DeviceEntityDescriptor descr : entities)
    {
      this.entities.put(descr.getName(), descr);
    }
  }
  
  public DeviceEntityDescriptorList()
  {
  }
  
  public static DeviceEntityDescriptorList difference(DeviceEntityDescriptorList left, DeviceEntityDescriptorList right)
  {
    DeviceEntityDescriptorList list = new DeviceEntityDescriptorList();
    for (Map.Entry<String, DeviceEntityDescriptor> entry : left.getDescriptors().entrySet())
    {
      if (!right.getDescriptors().containsKey(entry.getKey()))
      {
        list.getDescriptors().put(entry.getKey(), entry.getValue());
        continue;
      }
      if (!right.getDescriptors().get(entry.getKey()).equals(entry.getValue()))
      {
        list.getDescriptors().put(entry.getKey(), entry.getValue());
      }
    }
    return list;
  }
  
  public DataTable toDataTable()
  {
    try
    {
      return DataTableConversion.beansToTable(entities.values(), DeviceEntityDescriptor.FORMAT, true);
    }
    catch (DataTableException ex)
    {
      throw new IllegalStateException(ex.getMessage(), ex);
    }
  }
  
  private boolean contains(String entity)
  {
    return entities.containsKey(entity);
  }
  
  public boolean isActive(String entity)
  {
    if (entities.containsKey(entity))
    {
      return entities.get(entity).getActive();
    }
    
    return true;
  }
  
  public void merge(DeviceEntityDescriptorList other, Set<String> defaultActiveEntities)
  {
    for (Entry<String, DeviceEntityDescriptor> e : entities.entrySet())
    {
      if (other.contains(e.getKey()))
      {
        e.getValue().setActive(other.isActive(e.getKey()));
      }
      else if (!e.getValue().getActive() && defaultActiveEntities != null && defaultActiveEntities.contains(e.getKey()))
      {
        e.getValue().setActive(true);
      }
    }
  }
  
  @Override
  public Map<String, DeviceEntityDescriptor> getDescriptors()
  {
    return entities;
  }
  
  @Override
  public boolean hasSpecificEntities()
  {
    return this != EMPTY;
  }
}
