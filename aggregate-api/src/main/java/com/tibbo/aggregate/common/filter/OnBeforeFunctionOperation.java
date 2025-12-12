package com.tibbo.aggregate.common.filter;

import com.tibbo.aggregate.common.util.DateUtils;
import java.util.Date;

public class OnBeforeFunctionOperation extends CompareDateFunctionOperation {
    public OnBeforeFunctionOperation(Expression[] operands) {
        super(FilterFunctions.ON_OR_BEFORE.getName(), operands);
    }

    @Override
    public boolean compareDate(Date column, Date value) {
        Date end = DateUtils.getEndOfDay(value);
        return column.compareTo(end) <= 0;
    }
}
