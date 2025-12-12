package com.tibbo.aggregate.common.filter;

import java.util.Date;

public abstract class CheckDateFunctionOperation extends ColumnFunctionOperation {
    public CheckDateFunctionOperation(String name, Expression... operands) {
        super(name, operands);
    }

    @Override
    public Object evaluate() {
        return null;
    }

    protected abstract boolean checkDate(Date column);
}
