package com.tibbo.aggregate.common.filter;

import com.tibbo.aggregate.common.util.DateUtils;
import java.util.Date;

public class ThisHourFunctionOperation extends CheckDateFunctionOperation {
    public ThisHourFunctionOperation(Expression[] operands) {
        super(FilterFunctions.THIS_HOUR.getName(), operands);
    }

    @Override
    protected boolean checkDate(Date column) {
        Date date = new Date();
        Date start = DateUtils.getStartOfHour(date);
        Date end = DateUtils.getEndOfHour(date);
        return column.compareTo(start) >= 0 && column.compareTo(end) <= 0;
    }
}
