package com.tibbo.aggregate.common.dao;

import java.util.*;

import com.tibbo.aggregate.common.datatable.*;

public class ClusterNodeStatus extends AggreGateBean
{
  private static final String ID = "id";
  private static final String ROLE = "role";
  private static final String ACTIVE = "active";
  private static final String COUNTER = "counter";
  private static final String SYNCHRONIZING = "synchronizing";
  private static final String LAST_HEART_BEAT_TIME = "lastHeartBeatTime";
  
  private static TableFormat FORMAT = new TableFormat(1, 1);
  static
  {
    FORMAT.addField(FieldFormat.create(ID, FieldFormat.STRING_FIELD));
    FORMAT.addField(FieldFormat.create(ROLE, FieldFormat.INTEGER_FIELD));
    FORMAT.addField(FieldFormat.create(ACTIVE, FieldFormat.BOOLEAN_FIELD));
    FORMAT.addField(FieldFormat.create(COUNTER, FieldFormat.INTEGER_FIELD));
    FORMAT.addField(FieldFormat.create(SYNCHRONIZING, FieldFormat.BOOLEAN_FIELD));
    FORMAT.addField(FieldFormat.create(LAST_HEART_BEAT_TIME, FieldFormat.DATE_FIELD).setNullable(true));
  }
  
  private String id;
  private int role;
  private boolean active;
  private int counter;
  private boolean synchronizing;
  private Date lastHeartBeatTime;
  
  
  public ClusterNodeStatus()
  {
    super(FORMAT);
  }
  
  public ClusterNodeStatus(DataRecord data)
  {
    super(FORMAT, data);
  }
  
  public String getId()
  {
    return id;
  }
  
  public void setId(String id)
  {
    this.id = id;
  }
  
  public int getRole()
  {
    return role;
  }
  
  public void setRole(int role)
  {
    this.role = role;
  }
  
  public boolean isActive()
  {
    return active;
  }
  
  public void setActive(boolean active)
  {
    this.active = active;
  }
  
  public int getCounter()
  {
    return counter;
  }
  
  public void setCounter(int counter)
  {
    this.counter = counter;
  }
  
  public boolean isSynchronizing()
  {
    return synchronizing;
  }
  
  public void setSynchronizing(boolean synchronizing)
  {
    this.synchronizing = synchronizing;
  }
  
  public Date getLastHeartBeatTime()
  {
    return lastHeartBeatTime;
  }
  
  public void setLastHeartBeatTime(Date lastHeartBeatTime)
  {
    this.lastHeartBeatTime = lastHeartBeatTime;
  }
  
  @Override
  public String toString()
  {
    StringBuilder sb = new StringBuilder();
    sb.append("id=").append(id).append(", ");
    sb.append("role=").append(role).append(", ");
    sb.append("active=").append(active).append(", ");
    sb.append("counter=").append(counter).append(", ");
    sb.append("synchronizing=").append(synchronizing).append(", ");
    sb.append("lastHeartBeatTime=").append(lastHeartBeatTime);
    
    return sb.toString();
  }
}
