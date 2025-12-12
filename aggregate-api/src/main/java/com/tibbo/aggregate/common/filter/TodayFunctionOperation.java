package com.tibbo.aggregate.common.filter;

import com.tibbo.aggregate.common.util.DateUtils;
import java.util.Date;

public class TodayFunctionOperation extends CheckDateFunctionOperation {
    public TodayFunctionOperation(Expression[] operands) {
        super(FilterFunctions.TODAY.getName(), operands);
    }

    @Override
    protected boolean checkDate(Date column) {
        Date date = new Date();
        Date start = DateUtils.getStartOfDay(date);
        Date end = DateUtils.getEndOfDay(date);

        return column.compareTo(start) >= 0 && column.compareTo(end) <= 0;
    }
}
