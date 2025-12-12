package com.tibbo.aggregate.common.event;

import java.util.*;

import com.tibbo.aggregate.common.*;
import com.tibbo.aggregate.common.datatable.*;
import com.tibbo.aggregate.common.datatable.field.*;
import com.tibbo.aggregate.common.expression.*;

public class PersistenceBinding extends AggreGateBean implements Cloneable
{
  public static final String TYPE_BOOLEAN = "boolean";
  public static final String TYPE_STRING = "string";
  public static final String TYPE_INT = "int";
  public static final String TYPE_LONG = "long";
  public static final String TYPE_FLOAT = "float";
  public static final String TYPE_DOUBLE = "double";
  public static final String TYPE_TIMESTAMP = "timestamp";
  public static final String TYPE_MATERIALIZED_BLOB = "materialized_blob";
  public static final String TYPE_MATERIALIZED_CLOB = "materialized_clob";
  
  public static final Map<String, String> CASSANDRA_TYPES = new HashMap<String, String>();
  
  public static TableFormat FORMAT = new TableFormat();
  static
  {
    FieldFormat name = FieldFormat.create("<name><S><F=N><D=" + Cres.get().getString("name") + ">");
    FORMAT.addField(name);
    
    FieldFormat type = FieldFormat.create("<type><S><F=N><D=" + Cres.get().getString("type") + ">");
    type.addSelectionValue(TYPE_BOOLEAN, Cres.get().getString("dtBoolean"));
    type.addSelectionValue(TYPE_STRING, Cres.get().getString("dtString"));
    type.addSelectionValue(TYPE_INT, Cres.get().getString("dtInteger"));
    type.addSelectionValue(TYPE_LONG, Cres.get().getString("dtLong"));
    type.addSelectionValue(TYPE_FLOAT, Cres.get().getString("dtFloat"));
    type.addSelectionValue(TYPE_DOUBLE, Cres.get().getString("dtDouble"));
    type.addSelectionValue(TYPE_TIMESTAMP, Cres.get().getString("date"));
    FORMAT.addField(type);
    
    FieldFormat index = FieldFormat.create("<index><S><F=N><D=" + Cres.get().getString("index") + ">");
    FORMAT.addField(index);
    
    FieldFormat expression = FieldFormat.create("<expression><S><F=N><D=" + Cres.get().getString("expression") + "><E=" + StringFieldFormat.EDITOR_EXPRESSION + ">");
    FORMAT.addField(expression);
    
    CASSANDRA_TYPES.put(TYPE_BOOLEAN, "BooleanType");
    CASSANDRA_TYPES.put(TYPE_STRING, "UTF8Type");
    CASSANDRA_TYPES.put(TYPE_INT, "LongType");
    CASSANDRA_TYPES.put(TYPE_LONG, "LongType");
    CASSANDRA_TYPES.put(TYPE_FLOAT, "FloatType");
    CASSANDRA_TYPES.put(TYPE_DOUBLE, "DoubleType");
    CASSANDRA_TYPES.put(TYPE_TIMESTAMP, "DateType");
  }
  
  private String name;
  private String type;
  private String index;
  private String expression;
  
  private Expression compiledExpression;
  
  public PersistenceBinding()
  {
    super(FORMAT);
  }
  
  public PersistenceBinding(DataRecord record)
  {
    super(FORMAT, record);
  }
  
  public PersistenceBinding(String name, String type, String index, String expression)
  {
    this();
    
    if (index != null && index.length() > 2)
    {
      throw new IllegalStateException("Index name length is limited to 2 characters to meed database name length limitations");
    }
    
    setName(name);
    setType(type);
    setIndex(index);
    setExpression(expression);
  }
  
  public String getType()
  {
    return type;
  }
  
  public String getIndex()
  {
    return index;
  }
  
  public String getName()
  {
    return name;
  }
  
  public String getExpression()
  {
    return expression;
  }
  
  public Expression getCompiledExpression()
  {
    if (compiledExpression == null)
    {
      compiledExpression = expression != null ? new Expression(expression) : null;
    }
    
    return compiledExpression;
  }
  
  public void setName(String name)
  {
    this.name = name;
  }
  
  public void setType(String type)
  {
    this.type = type;
  }
  
  public void setIndex(String index)
  {
    this.index = index;
  }
  
  public void setExpression(String expression)
  {
    this.expression = expression;
    compiledExpression = null;
  }
  
  @Override
  public PersistenceBinding clone()
  {
    try
    {
      return (PersistenceBinding) super.clone();
    }
    catch (CloneNotSupportedException ex)
    {
      throw new IllegalStateException(ex.getMessage(), ex);
    }
  }
  
  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((expression == null) ? 0 : expression.hashCode());
    result = prime * result + ((index == null) ? 0 : index.hashCode());
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + ((type == null) ? 0 : type.hashCode());
    return result;
  }
  
  @Override
  public boolean equals(Object obj)
  {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    PersistenceBinding other = (PersistenceBinding) obj;
    if (expression == null)
    {
      if (other.expression != null)
        return false;
    }
    else if (!expression.equals(other.expression))
      return false;
    if (index == null)
    {
      if (other.index != null)
        return false;
    }
    else if (!index.equals(other.index))
      return false;
    if (name == null)
    {
      if (other.name != null)
        return false;
    }
    else if (!name.equals(other.name))
      return false;
    if (type == null)
    {
      if (other.type != null)
        return false;
    }
    else if (!type.equals(other.type))
      return false;
    return true;
  }
  
  @Override
  public String toString()
  {
    return "PersistenceBinding [name=" + name + ", type=" + type + ", index=" + index + ", expression=" + expression + "]";
  }
}
