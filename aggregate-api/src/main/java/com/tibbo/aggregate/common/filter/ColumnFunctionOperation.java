package com.tibbo.aggregate.common.filter;

public abstract class ColumnFunctionOperation extends FunctionOperation {

    public ColumnFunctionOperation(String name, Expression... operands) {
        super(name, operands);
        if (operands.length < 1 || !(operands[0] instanceof ColumnName)) {
            throw new IllegalArgumentException("Column reference is expected in function: " + name);
        }
    }

    public ColumnName getColumn() {
        return (ColumnName) operands[0];
    }
}
