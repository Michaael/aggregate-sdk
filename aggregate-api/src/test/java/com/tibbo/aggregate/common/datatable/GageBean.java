package com.tibbo.aggregate.common.datatable;

import java.awt.*;
import java.util.*;

import com.tibbo.aggregate.common.data.*;

/**
 * Bean corresponding {@link TestEncodingUtils#TEST_TABLE_FORMAT}
 * 
 * @see TestEncodingUtils#createTestDataTable(boolean, int)
 * 
 * @author Anton Boronnikov
 * 
 */
public class GageBean
{
  private int intField;
  private String string;
  private float floatField;
  private DataTable table;
  private boolean booleanField;
  private long longField;
  private double doubleField;
  private Date date;
  private Color color;
  private Data data;
  
  public int getIntField()
  {
    return intField;
  }
  
  public void setIntField(int intField)
  {
    this.intField = intField;
  }
  
  public String getString()
  {
    return string;
  }
  
  public void setString(String string)
  {
    this.string = string;
  }
  
  public float getFloatField()
  {
    return floatField;
  }
  
  public void setFloatField(float floatField)
  {
    this.floatField = floatField;
  }
  
  public DataTable getTable()
  {
    return table;
  }
  
  public void setTable(DataTable table)
  {
    this.table = table;
  }
  
  public boolean isBooleanField()
  {
    return booleanField;
  }
  
  public void setBooleanField(boolean booleanField)
  {
    this.booleanField = booleanField;
  }
  
  public long getLongField()
  {
    return longField;
  }
  
  public void setLongField(long longField)
  {
    this.longField = longField;
  }
  
  public double getDoubleField()
  {
    return doubleField;
  }
  
  public void setDoubleField(double doubleField)
  {
    this.doubleField = doubleField;
  }
  
  public Date getDate()
  {
    return date;
  }
  
  public void setDate(Date date)
  {
    this.date = date;
  }
  
  public Color getColor()
  {
    return color;
  }
  
  public void setColor(Color color)
  {
    this.color = color;
  }
  
  public Data getData()
  {
    return data;
  }
  
  public void setData(Data data)
  {
    this.data = data;
  }
}
