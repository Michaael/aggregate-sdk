package com.tibbo.aggregate.common.filter;

import java.util.Date;

import com.tibbo.aggregate.common.util.DateUtils;

public class ThisMonthFunctionOperation extends CheckDateFunctionOperation {
    public ThisMonthFunctionOperation(Expression[] operands) {
        super(FilterFunctions.THIS_MONTH.getName(), operands);
    }


    @Override
    protected boolean checkDate(Date column) {
        Date date = new Date();
        Date start = DateUtils.getStartOfMonth(date);
        Date end = DateUtils.getEndOfMonth(date);
        return column.compareTo(start) >= 0 && column.compareTo(end) <= 0;
    }
}
