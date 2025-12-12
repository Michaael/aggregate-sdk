package com.tibbo.aggregate.common.filter;

import java.util.Calendar;
import java.util.Date;

import com.tibbo.aggregate.common.util.DateUtils;

public class NextHoursFunctionOperation extends LastNextPeriodFunctionOperation {
    public NextHoursFunctionOperation(Expression[] operands) {
        super(FilterFunctions.NEXT_X_HOURS.getName(), operands);
    }

    @Override
    protected Object checkDate(Date column, Number operand) {
        Date end = DateUtils.getStartOfHour(new Date());
        Calendar cal = Calendar.getInstance();
        cal.setTime(end);
        cal.add(Calendar.HOUR_OF_DAY, operand.intValue());
        Date start = cal.getTime();

        return column.compareTo(start) >= 0 && column.compareTo(end) <= 0;
    }
}
