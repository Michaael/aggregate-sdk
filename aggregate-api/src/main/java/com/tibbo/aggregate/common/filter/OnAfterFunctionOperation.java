package com.tibbo.aggregate.common.filter;

import com.tibbo.aggregate.common.util.DateUtils;
import java.util.Date;

public class OnAfterFunctionOperation extends CompareDateFunctionOperation {
    public OnAfterFunctionOperation(Expression[] operands) {
        super(FilterFunctions.ON_OR_AFTER.getName(), operands);
    }

    @Override
    public boolean compareDate(Date column, Date value) {
        Date start = DateUtils.getStartOfDay(value);
        return column.compareTo(start) >= 0;
    }
}
