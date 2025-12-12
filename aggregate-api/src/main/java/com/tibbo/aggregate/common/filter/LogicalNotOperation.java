package com.tibbo.aggregate.common.filter;

public class LogicalNotOperation extends Operation {

    private final Expression expression;

    public LogicalNotOperation(Expression expression) {
        super("NOT");
        this.expression = expression;
    }

    @Override
    public Object evaluate() {
        return !(boolean)expression.evaluate();
    }

    @Override
    public Expression[] getChildren() {
        return new Expression[] { expression };
    }

    @Override
    public int getPriority() {
        return 4;
    }

    @Override
    public String getDescription() {
        return "!";
    }

    @Override
    public int getOperandCount() {
        return 1;
    }
}
