package com.tibbo.aggregate.common.device;

import java.util.LinkedList;
import java.util.List;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.datatable.AggreGateBean;
import com.tibbo.aggregate.common.datatable.DataRecord;
import com.tibbo.aggregate.common.datatable.DataTableBindingProvider;
import com.tibbo.aggregate.common.datatable.FieldFormat;
import com.tibbo.aggregate.common.datatable.TableFormat;
import com.tibbo.aggregate.common.expression.DefaultReferenceResolver;
import com.tibbo.aggregate.common.expression.function.DefaultFunctions;

public class DeviceAssetDefinition extends AggreGateBean implements Comparable<DeviceAssetDefinition>
{
  public static final String FIELD_ID = "id";
  private static final String FIELD_DESCRIPTION = "description";
  public static final String FIELD_ENABLED = "enabled";
  private static final String FIELD_CHILDREN = "children";
  
  public static final TableFormat FORMAT = new TableFormat();
  static
  {
    FORMAT.setUnresizable(true);
    
    FORMAT.addField("<" + FIELD_ID + "><S><F=HRK>");
    FORMAT.addField("<" + FIELD_DESCRIPTION + "><S><F=R><D=" + Cres.get().getString("description") + ">");
    FORMAT.addField("<" + FIELD_ENABLED + "><B><A=1><D=" + Cres.get().getString("enabled") + ">");
    
    FORMAT.addField(FieldFormat.create("<" + FIELD_CHILDREN + "><T><F=N><D=" + Cres.get().getString("devNestedAssets") + ">"));
    
    FORMAT.setNamingExpression(DefaultFunctions.AGGREGATE + "({}, \"{env/previous} + ({" + FIELD_ENABLED + "} ? 1 : 0)\", 0) + '/' + {#" + DefaultReferenceResolver.RECORDS + "}");
    
    String ref = FIELD_CHILDREN + "#" + DataTableBindingProvider.PROPERTY_ENABLED;
    String exp = "{" + FIELD_ENABLED + "}";
    FORMAT.addBinding(ref, exp);
  }
  
  private String id;
  private String description;
  private boolean enabled;
  private List<DeviceAssetDefinition> children;
  
  public DeviceAssetDefinition()
  {
    super(FORMAT);
  }
  
  public DeviceAssetDefinition(DataRecord data)
  {
    super(FORMAT, data);
  }
  
  public DeviceAssetDefinition(String id, String description)
  {
    this();
    this.id = id;
    this.description = description;
  }
  
  public String getId()
  {
    return id;
  }
  
  public void setId(String id)
  {
    this.id = id;
  }
  
  public String getDescription()
  {
    return description;
  }
  
  public void setDescription(String description)
  {
    this.description = description;
  }
  
  public boolean isEnabled()
  {
    return enabled;
  }
  
  public void setEnabled(boolean enabled)
  {
    this.enabled = enabled;
  }
  
  public List<DeviceAssetDefinition> getChildren()
  {
    return children;
  }
  
  public void setChildren(List<DeviceAssetDefinition> children)
  {
    this.children = children != null ? children : new LinkedList();
  }
  
  public void addSubgroup(DeviceAssetDefinition child)
  {
    getChildren().add(child);
  }
  
  public int compareTo(DeviceAssetDefinition other)
  {
    return description.compareTo(other.description);
  }
  
  @Override
  public String toString()
  {
    return "GroupDefinition [id=" + id + ", description=" + description + ", enabled=" + enabled + ", children=" + (children != null ? children.size() : 0) + "]";
  }
  
  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((description == null) ? 0 : description.hashCode());
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
    DeviceAssetDefinition other = (DeviceAssetDefinition) obj;
    if (description == null)
    {
      if (other.description != null)
      {
        return false;
      }
    }
    else if (!description.equals(other.description))
    {
      return false;
    }
    return true;
  }
  
}
