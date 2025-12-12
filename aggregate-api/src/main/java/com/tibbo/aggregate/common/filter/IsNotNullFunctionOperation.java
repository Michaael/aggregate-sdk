package com.tibbo.aggregate.common.filter;

public class IsNotNullFunctionOperation extends ColumnFunctionOperation {

    public IsNotNullFunctionOperation(Expression[] operands) {
        super(FilterFunctions.IS_NOT_NULL.getName(), operands);
    }

    @Override
    public Object evaluate() {
        return getColumn().evaluate() != null;
    }

}
