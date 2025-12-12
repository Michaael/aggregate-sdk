package com.tibbo.aggregate.common.event;

import java.util.*;

import com.tibbo.aggregate.common.*;
import com.tibbo.aggregate.common.datatable.*;
import com.tibbo.aggregate.common.datatable.converter.*;

public class Acknowledgement implements Cloneable
{
  public static final String FIELD_AUTHOR = "author";
  public static final String FIELD_TIME = "time";
  public static final String FIELD_TEXT = "text";
  
  public static final TableFormat FORMAT = new TableFormat();
  static
  {
    FORMAT.addField("<" + FIELD_AUTHOR + "><S><F=N><D=" + Cres.get().getString("author") + ">");
    FORMAT.addField("<" + FIELD_TIME + "><D><D=" + Cres.get().getString("time") + ">");
    FORMAT.addField("<" + FIELD_TEXT + "><S><D=" + Cres.get().getString("text") + ">");
    
    FORMAT.setNamingExpression("print({}, \"{" + FIELD_TIME + "} + ': ' + {" + FIELD_TEXT + "} + ' (' + {" + FIELD_AUTHOR + "} + ')'\", \"; \")");
  }
  
  static
  {
    DataTableConversion.registerFormatConverter(new DefaultFormatConverter(Acknowledgement.class, FORMAT));
  }
  
  private String author;
  private Date time;
  private String text;
  
  public Acknowledgement()
  {
    
  }
  
  public Acknowledgement(String author, Date time, String text)
  {
    this.author = author;
    this.time = time;
    this.text = text;
  }
  
  public String getAuthor()
  {
    return author;
  }
  
  public String getText()
  {
    return text;
  }
  
  public Date getTime()
  {
    return time;
  }
  
  public void setAuthor(String author)
  {
    this.author = author;
  }
  
  public void setText(String text)
  {
    this.text = text;
  }
  
  public void setTime(Date time)
  {
    this.time = time;
  }
  
  public TableFormat getFormat()
  {
    return FORMAT;
  }
  
  @Override
  public Acknowledgement clone()
  {
    try
    {
      return (Acknowledgement) super.clone();
    }
    catch (CloneNotSupportedException ex)
    {
      throw new IllegalStateException(ex);
    }
  }
}
