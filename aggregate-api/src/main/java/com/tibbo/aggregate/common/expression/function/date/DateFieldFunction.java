package com.tibbo.aggregate.common.expression.function.date;

import java.util.*;

import com.tibbo.aggregate.common.expression.*;

public class DateFieldFunction extends DateFunction
{
  private int field;

  public DateFieldFunction(String name, int field, String description)
  {
    super(name, "Integer", description);
    this.field = field;
  }
  
  @Override
  public Object execute(GregorianCalendar calendar, Object... parameters) throws EvaluationException
  {
    return calendar.get(field);
  }
}
