package com.tibbo.aggregate.common.filter;

import com.tibbo.aggregate.common.util.DateUtils;

import java.util.Calendar;
import java.util.Date;

public class TomorrowFunctionOperation extends CheckDateFunctionOperation {
    public TomorrowFunctionOperation(Expression[] operands) {
        super(FilterFunctions.TOMORROW.getName(), operands);
    }

    @Override
    protected boolean checkDate(Date column) {
        Date date = new Date();
        Date start = DateUtils.getEndOfDay(date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(start);
        cal.add(Calendar.DAY_OF_MONTH, 1);
        Date end = cal.getTime();

        return column.compareTo(start) >= 0 && column.compareTo(end) <= 0;
    }
}
