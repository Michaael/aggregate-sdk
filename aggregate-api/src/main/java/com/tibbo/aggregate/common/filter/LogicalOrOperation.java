package com.tibbo.aggregate.common.filter;

public class LogicalOrOperation extends AbstractBooleanBinaryOperation {

    public static final String NAME = "OR";

    public LogicalOrOperation(Expression operand1, Expression operand2) {
        super(NAME, operand1, operand2);
    }

    @Override
    protected Object binaryEvaluation(Object operand1, Object operand2) {
        if (operand1 == null || operand2 == null) {
            return null;
        }
        return (boolean) operand1 || (boolean) operand2;
    }

    @Override
    public int getPriority() {
        return 1;
    }

    @Override
    public String getDescription() {
        return "||";
    }
}
