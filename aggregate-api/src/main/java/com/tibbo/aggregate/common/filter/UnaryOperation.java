package com.tibbo.aggregate.common.filter;

import java.util.Objects;

public abstract class UnaryOperation extends Operation {

    protected final Expression operand;

    public UnaryOperation(String name, Expression operand) {
        super(name);
        Objects.requireNonNull(operand, "Operand is null");
        this.operand = operand;
    }

    @Override
    public Expression[] getChildren() {
        return new Expression[] {operand};
    }

    @Override
    public int getPriority() {
        return 3;
    }
}
