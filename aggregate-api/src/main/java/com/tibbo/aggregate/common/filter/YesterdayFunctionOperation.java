package com.tibbo.aggregate.common.filter;

import com.tibbo.aggregate.common.util.DateUtils;

import java.util.Calendar;
import java.util.Date;

public class YesterdayFunctionOperation extends CheckDateFunctionOperation {
    public YesterdayFunctionOperation(Expression[] operands) {
        super(FilterFunctions.YESTERDAY.getName(), operands);
    }

    @Override
    protected boolean checkDate(Date column) {
        Date date = new Date();
        Date end = DateUtils.getStartOfDay(date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(end);
        cal.add(Calendar.DAY_OF_MONTH, -1);
        Date start = cal.getTime();

        return column.compareTo(start) >= 0 && column.compareTo(end) <= 0;
    }
}
