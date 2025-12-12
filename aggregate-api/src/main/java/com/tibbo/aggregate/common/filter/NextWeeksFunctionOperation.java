package com.tibbo.aggregate.common.filter;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import com.tibbo.aggregate.common.util.DateUtils;

public class NextWeeksFunctionOperation extends LastNextPeriodFunctionOperation
{
  public NextWeeksFunctionOperation(Expression[] operands)
  {
    super(FilterFunctions.NEXT_X_WEEKS.getName(), operands);
  }
  
  @Override
  protected Object checkDate(Date column, Number operand)
  {
    LocalDate date = LocalDate.now();
    Date end = DateUtils.getStartOfHour(new Date());
    date = date.plusWeeks(operand.intValue());
    return column.compareTo(Date.from(date.atStartOfDay()
        .atZone(ZoneId.systemDefault())
        .toInstant())) >= 0 && column.compareTo(end) <= 0;
  }
}
