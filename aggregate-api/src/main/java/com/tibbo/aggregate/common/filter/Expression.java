package com.tibbo.aggregate.common.filter;

public interface Expression {
    Object evaluate();
    Expression[] getChildren();
    int getPriority();
    String getDescription();
}
