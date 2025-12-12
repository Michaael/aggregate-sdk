package com.tibbo.aggregate.common.filter;

import com.tibbo.aggregate.common.util.DateUtils;
import java.util.Date;

public class OnFunctionOperation extends CompareDateFunctionOperation {

    public OnFunctionOperation(Expression[] operands) {
        super(FilterFunctions.ON.getName(), operands);
        if (operands.length != 2) {
            throw new IllegalArgumentException("Illegal operand number in function: " + getName());
        }
    }

    @Override
    public boolean compareDate(Date column, Date value) {
        Date start = DateUtils.getStartOfDay(value);
        Date end = DateUtils.getEndOfDay(value);
        return column.compareTo(start) >= 0 && column.compareTo(end) <= 0;
    }
}
