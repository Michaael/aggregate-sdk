package com.tibbo.aggregate.common.datatable.converter;

import java.text.*;

import com.tibbo.aggregate.common.datatable.*;

public class FCComparable extends SimpleFormatConverter<Comparable>
{
  public FCComparable()
  {
    super(Comparable.class);
  }
  
  public FieldFormat createFieldFormat(String name)
  {
    return FieldFormat.create(name, FieldFormat.FLOAT_FIELD);
  }
  
  public Comparable simpleToBean(Object value)
  {
    String s = (String) value;
    Comparable result = (Comparable) value;
    try
    {
      result = Integer.parseInt(s);
      result = Float.parseFloat(s);
      result = DateFormat.getDateInstance().parse(s);
    }
    catch (NumberFormatException e)
    {
      return result;
    } catch (ParseException e)
    {
      return result;
    }
    return result;
    
  }
  
  public Object convertToTable(Comparable value, TableFormat format)
  {
    return value != null ? value.toString() : value;
  }
}