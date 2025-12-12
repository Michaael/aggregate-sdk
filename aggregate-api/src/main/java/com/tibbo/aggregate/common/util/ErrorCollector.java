package com.tibbo.aggregate.common.util;

import com.tibbo.aggregate.common.datatable.*;

import java.util.*;

public class ErrorCollector
{
  public static TableFormat ERRORS = new TableFormat();

  static {
    ERRORS.addField("<message><S>");
  }

  /**
   * Список ошибок.
   * Используется ArrayList вместо LinkedList для лучшей производительности доступа.
   */
  private final List<Exception> errors = new ArrayList<Exception>();
  
  public void addError(Exception error)
  {
    errors.add(error);
  }
  
  public List<Exception> getErrors()
  {
    return errors;
  }
  
  @Override
  public String toString()
  {
    return "ErrorCollector{" +
        "errors=" + errors +
        '}';
  }
  
  @Override
  public boolean equals(Object o)
  {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    ErrorCollector that = (ErrorCollector) o;
    return Objects.equals(errors, that.errors);
  }

  public DataTable toDataTable()
  {
    SimpleDataTable res = new SimpleDataTable(ERRORS);
    for(Exception error: errors)
    {
      res.addRecord(error.getMessage());
    }
    return res;
  }
  
  @Override
  public int hashCode()
  {
    return Objects.hash(errors);
  }
}
