package com.tibbo.aggregate.common.filter;

import java.util.Date;

import com.tibbo.aggregate.common.util.DateUtils;

public class ThisYearFunctionOperation extends CheckDateFunctionOperation {
    public ThisYearFunctionOperation(Expression[] operands) {
        super(FilterFunctions.THIS_YEAR.getName(), operands);
    }


    @Override
    protected boolean checkDate(Date column) {
        Date date = new Date();
        Date start = DateUtils.getStartOfYear(date);
        Date end = DateUtils.getEndOfYear(date);
        return column.compareTo(start) >= 0 && column.compareTo(end) <= 0;
    }
}
