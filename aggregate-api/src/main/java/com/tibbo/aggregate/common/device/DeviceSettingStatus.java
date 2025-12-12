package com.tibbo.aggregate.common.device;

import java.util.*;

import com.tibbo.aggregate.common.datatable.*;

public class DeviceSettingStatus
{
  public static final String FIELD_NAME = "name";
  public static final String FIELD_TIME = "time";
  public static final String FIELD_DURATION = "duration";
  public static final String FIELD_UPDATED = "updated";
  public static final String FIELD_DIRECTION = "direction";
  public static final String FIELD_ERROR = "error";
  
  public static final TableFormat FORMAT = new TableFormat();
  static
  {
    FORMAT.addField("<" + FIELD_NAME + "><S>");
    FORMAT.addField("<" + FIELD_TIME + "><D><F=N>");
    FORMAT.addField("<" + FIELD_DURATION + "><L><F=N>");
    FORMAT.addField("<" + FIELD_UPDATED + "><B>");
    FORMAT.addField("<" + FIELD_DIRECTION + "><I><A=" + DeviceContext.DIRECTION_DEVICE_TO_SERVER + ">");
    FORMAT.addField("<" + FIELD_ERROR + "><S><F=N>");
  }
  
  private Date time;
  private Long duration;
  private boolean updated;
  private int direction = DeviceContext.DIRECTION_DEVICE_TO_SERVER;
  private String error;
  
  public Date getTime()
  {
    return time;
  }
  
  public void setTime(Date time)
  {
    this.time = time;
  }
  
  public Long getDuration()
  {
    return duration;
  }
  
  public void setDuration(Long duration)
  {
    this.duration = duration;
  }
  
  public boolean isUpdated()
  {
    return updated;
  }
  
  public void setUpdated(boolean updated)
  {
    this.updated = updated;
  }
  
  public int getDirection()
  {
    return direction;
  }
  
  public void setDirection(int direction)
  {
    this.direction = direction;
  }
  
  public String getError()
  {
    return error;
  }
  
  public void setError(String error)
  {
    this.error = error;
  }
}
