package com.tibbo.aggregate.common.filter;

public class IsNullFunctionOperation extends ColumnFunctionOperation {

    public IsNullFunctionOperation(Expression[] operands) {
        super(FilterFunctions.IS_NULL.getName(), operands);
    }

    @Override
    public Object evaluate() {
        return getColumn().evaluate() == null;
    }
}
