package com.tibbo.aggregate.common.datatable.converter;

import java.util.*;

import com.tibbo.aggregate.common.datatable.*;

public class ChoicesConverter extends AbstractFormatConverter<Object>
{
  private List<Choice> choices = new LinkedList<Choice>();
  private Choice defaultChoice;
  
  public ChoicesConverter(Class valueClass)
  {
    super(valueClass);
  }
  
  public FieldFormat createFieldFormat(String name)
  {
    FieldFormat ff = FieldFormat.create(name, FieldFormat.STRING_FIELD);
    
    for (Choice c : choices)
    {
      ff.addSelectionValue(c.getName(), c.getDescription());
    }
    
    if (defaultChoice != null)
    {
      ff.setDefault(defaultChoice.getName());
    }
    
    return ff;
  }
  
  public FieldFormat createFieldFormat(String name, String description)
  {
    final FieldFormat result = createFieldFormat(name);
    result.setDescription(description);
    return result;
  }
  
  public FieldFormat createFieldFormat(String name, String description, Object defaultValue)
  {
    final FieldFormat result = createFieldFormat(name, description);
    result.setDefault(defaultValue);
    return result;
  }
  
  public void add(Choice c)
  {
    if (choices.size() == 0)
    {
      defaultChoice = c;
    }
    
    choices.add(c);
  }
  
  public void add(String description, Object object)
  {
    add(new Choice(description, object));
  }
  
  public void add(String name, String description, Object object)
  {
    add(new Choice(name, description, object));
  }
  
  public void setDefault(Choice defaultChoice)
  {
    this.defaultChoice = defaultChoice;
  }
  
  public void setDefault(String name)
  {
    for (Choice choice : choices)
    {
      if (choice.getName().equals(name))
      {
        defaultChoice = choice;
        return;
      }
    }
    throw new IllegalStateException("Can not find choice item with name: " + name);
  }
  
  public Object convertToBean(Object value)
  {
    return convertToBean(value, null);
  }
  
  public Object convertToBean(Object value, Object originalValue)
  {
    if (value instanceof DataTable)
    {
      value = ((DataTable) value).get();
    }
    
    for (Choice c : choices)
    {
      if (c.getName().equals(value))
      {
        return c.getObject();
      }
    }
    
    throw new IllegalArgumentException("Unknown value: " + value);
  }
  
  public Object convertToTable(Object value, TableFormat format)
  {
    for (Choice c : choices)
    {
      if (c.getObject().equals(value))
      {
        return c.getName();
      }
    }
    throw new IllegalArgumentException("Unknown value: " + value);
  }
  
  public DataTable createTable(Object value, TableFormat format) throws DataTableException
  {
    return new SimpleDataTable(format, convertToTable(value));
  }
}
