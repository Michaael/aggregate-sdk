package com.tibbo.aggregate.common.filter;

import java.util.Date;

import com.tibbo.aggregate.common.util.DateUtils;

public class ThisWeekFunctionOperation extends CheckDateFunctionOperation {

    public ThisWeekFunctionOperation(Expression[] operands) {
        super(FilterFunctions.THIS_WEEK.getName(), operands);
    }


    @Override
    protected boolean checkDate(Date column) {
        Date date = new Date();
        Date start = DateUtils.getStartOfWeek(date);
        Date end = DateUtils.getEndOfWeek(date);
        return column.compareTo(start) >= 0 && column.compareTo(end) <= 0;
    }
}
