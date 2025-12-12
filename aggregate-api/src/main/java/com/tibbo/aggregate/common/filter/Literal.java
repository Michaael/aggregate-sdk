package com.tibbo.aggregate.common.filter;

public class Literal implements Expression {

    private final Object value;

    public Literal(Object value) {
        this.value = value;
    }

    @Override
    public Object evaluate() {
        return value;
    }

    @Override
    public Expression[] getChildren() {
        return new Expression[0];
    }

    @Override
    public int getPriority() {
        return 4;
    }

    @Override
    public String getDescription() {
        return value.toString();
    }
}
