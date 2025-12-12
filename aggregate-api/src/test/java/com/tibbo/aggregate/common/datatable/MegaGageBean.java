package com.tibbo.aggregate.common.datatable;

import java.awt.*;
import java.util.*;

import com.tibbo.aggregate.common.data.*;

public class MegaGageBean extends AggreGateBean
{
  private GageBean gageBean;
  
  public MegaGageBean()
  {
    super(TestEncodingUtils.TEST_TABLE_FORMAT);
  }
  
  public MegaGageBean(TableFormat format, DataRecord data)
  {
    super(format, data);
  }
  
  public MegaGageBean(TableFormat format)
  {
    super(format);
  }
  
  public MegaGageBean(GageBean gageBean)
  {
    super(TestEncodingUtils.TEST_TABLE_FORMAT);
    
    this.gageBean = gageBean;
  }
  
  public MegaGageBean(TableFormat format, GageBean gageBean)
  {
    super(format);
    this.gageBean = gageBean;
  }
  
  public int getIntField()
  {
    return getGageBean().getIntField();
  }
  
  public void setIntField(int intField)
  {
    getGageBean().setIntField(intField);
  }
  
  public String getString()
  {
    return getGageBean().getString();
  }
  
  public void setString(String string)
  {
    getGageBean().setString(string);
  }
  
  public float getFloatField()
  {
    return getGageBean().getFloatField();
  }
  
  public void setFloatField(float floatField)
  {
    getGageBean().setFloatField(floatField);
  }
  
  public DataTable getTable()
  {
    return getGageBean().getTable();
  }
  
  public void setTable(DataTable table)
  {
    getGageBean().setTable(table);
  }
  
  public boolean isBooleanField()
  {
    return getGageBean().isBooleanField();
  }
  
  public void setBooleanField(boolean booleanField)
  {
    getGageBean().setBooleanField(booleanField);
  }
  
  public long getLongField()
  {
    return getGageBean().getLongField();
  }
  
  public void setLongField(long longField)
  {
    getGageBean().setLongField(longField);
  }
  
  public double getDoubleField()
  {
    return getGageBean().getDoubleField();
  }
  
  public void setDoubleField(double doubleField)
  {
    getGageBean().setDoubleField(doubleField);
  }
  
  public Date getDate()
  {
    return getGageBean().getDate();
  }
  
  public void setDate(Date date)
  {
    getGageBean().setDate(date);
  }
  
  public Color getColor()
  {
    return getGageBean().getColor();
  }
  
  public void setColor(Color color)
  {
    getGageBean().setColor(color);
  }
  
  public Data getData()
  {
    return getGageBean().getData();
  }
  
  public void setData(Data data)
  {
    getGageBean().setData(data);
  }
  
  public GageBean getGageBean()
  {
    if (gageBean == null)
    {
      gageBean = new GageBean();
    }
    return gageBean;
  }
}
