package com.tibbo.aggregate.common.filter;

public class LogicalAndOperation extends AbstractBooleanBinaryOperation {

    public static final String NAME = "AND";

    public LogicalAndOperation(Expression operand1, Expression operand2) {
        super(NAME, operand1, operand2);
    }

    @Override
    protected Object binaryEvaluation(Object operand1, Object operand2) {
        if (operand1 == null || operand2 == null) {
            return null;
        }

        return (boolean)operand1 && (boolean)operand2;
    }

    @Override
    public int getPriority() {
        return 2;
    }

    @Override
    public String getDescription() {
        return "&&";
    }
}
