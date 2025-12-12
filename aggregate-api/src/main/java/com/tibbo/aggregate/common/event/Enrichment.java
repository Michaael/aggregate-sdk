package com.tibbo.aggregate.common.event;

import java.util.*;

import com.tibbo.aggregate.common.*;
import com.tibbo.aggregate.common.context.*;
import com.tibbo.aggregate.common.datatable.*;
import com.tibbo.aggregate.common.datatable.converter.*;

public class Enrichment
{
  public static final String FIRST_OCCURRENCE = "firstOccurrence";

  public static final String FIELD_NAME = "name";
  public static final String FIELD_VALUE = "value";
  public static final String FIELD_DATE = "date";
  public static final String FIELD_AUTHOR = "author";
  
  public static final TableFormat FORMAT = new TableFormat();
  static
  {
    FORMAT.addField("<" + FIELD_NAME + "><S><D=" + Cres.get().getString("name") + ">");
    FORMAT.addField("<" + FIELD_VALUE + "><S><D=" + Cres.get().getString("value") + ">");
    FORMAT.addField("<" + FIELD_DATE + "><D><D=" + Cres.get().getString("date") + ">");
    FORMAT.addField("<" + FIELD_AUTHOR + "><S><F=N><D=" + Cres.get().getString("author") + ">");
    
    FORMAT.setNamingExpression("print({}, \"{" + FIELD_NAME + "} + '=' + {" + FIELD_VALUE + "} + ' (' + {" + FIELD_DATE + "} + ', ' + {" + FIELD_AUTHOR + "} + ')'\", \"; \")");
  }
  
  static
  {
    DataTableConversion.registerFormatConverter(new DefaultFormatConverter(Enrichment.class, FORMAT));
  }
  
  private String name;
  private String value;
  private Date date;
  private String author;
  
  public Enrichment()
  {
    super();
  }
  
  public Enrichment(String name, String value, Date date, String author)
  {
    super();
    
    if (!ContextUtils.isValidContextName(name))
    {
      throw new IllegalStateException("Illegal enrichment name: " + name);
    }
    
    this.name = name;
    this.value = value;
    this.date = date;
    this.author = author;
  }
  
  public String getName()
  {
    return name;
  }
  
  public void setName(String name)
  {
    this.name = name;
  }
  
  public String getValue()
  {
    return value;
  }
  
  public void setValue(String value)
  {
    this.value = value;
  }
  
  public Date getDate()
  {
    return date;
  }
  
  public void setDate(Date date)
  {
    this.date = date;
  }
  
  public String getAuthor()
  {
    return author;
  }
  
  public void setAuthor(String author)
  {
    this.author = author;
  }
  
}
