package com.tibbo.aggregate.common.device;

import java.util.Objects;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.datatable.AggreGateBean;
import com.tibbo.aggregate.common.datatable.DataRecord;
import com.tibbo.aggregate.common.datatable.FieldFormat;
import com.tibbo.aggregate.common.datatable.TableFormat;

public class DeviceEntityDescriptor extends AggreGateBean
{
  public static final String FIELD_NAME = "name";
  public static final String FIELD_DESCRIPTION = "description";
  public static final String FIELD_GROUP = "group";
  public static final String FIELD_ACTIVE = "active";
  
  public static final TableFormat FORMAT = new TableFormat();
  static
  {
    FORMAT.setUnresizable(true);
    
    FieldFormat ff = FieldFormat.create("<" + FIELD_NAME + "><S><F=R><D=" + Cres.get().getString("name") + ">");
    FORMAT.addField(ff);
    
    ff = FieldFormat.create("<" + FIELD_DESCRIPTION + "><S><F=RN><D=" + Cres.get().getString("description") + ">");
    FORMAT.addField(ff);
    
    FORMAT.addField("<" + FIELD_GROUP + "><S><F=RN><D=" + Cres.get().getString("group") + ">");
    
    FORMAT.addField("<" + FIELD_ACTIVE + "><B><A=1><D=" + Cres.get().getString("active") + ">");
  }
  
  private String name;
  private String description;
  private String group;
  private Boolean active;
  
  public DeviceEntityDescriptor(String name, String description, String group, Boolean active)
  {
    super(FORMAT);
    this.name = name;
    this.description = description;
    this.group = group;
    this.active = active;
  }
  
  public DeviceEntityDescriptor(DataRecord rec)
  {
    super(FORMAT, rec);
  }
  
  public String getName()
  {
    return name;
  }
  
  public void setName(String name)
  {
    this.name = name;
  }
  
  public String getDescription()
  {
    return description;
  }
  
  public void setDescription(String description)
  {
    this.description = description;
  }
  
  public String getGroup()
  {
    return group;
  }
  
  public void setGroup(String group)
  {
    this.group = group;
  }
  
  public Boolean getActive()
  {
    return active;
  }
  
  public void setActive(Boolean active)
  {
    this.active = active;
  }
  
  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + ((description == null) ? 0 : description.hashCode());
    result = prime * result + ((group == null) ? 0 : group.hashCode());
    result = prime * result + ((active == null) ? 0 : active.hashCode());
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
    DeviceEntityDescriptor other = (DeviceEntityDescriptor) obj;
    
    if (!Objects.equals(name, other.name))
    {
      return false;
    }
    
    if (!Objects.equals(description, other.description))
    {
      return false;
    }
    
    if (!Objects.equals(group, other.group))
    {
      return false;
    }
  
    return Objects.equals(active, other.active);
  }
}
