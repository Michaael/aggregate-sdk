package com.tibbo.aggregate.common.filter;

public class DoesNotEndWithOperation extends BinaryStringOperation {

    public static final String NAME = "DOES_NOT_END_WITH";

    public DoesNotEndWithOperation(String name, Expression operand1, Expression operand2) {
        super(name, operand1, operand2);
    }

    @Override
    protected Object binaryEvaluation(Object operand1, Object operand2) {
        String val1 = (String) operand1;
        String val2 = (String) operand2;

        return !val1.endsWith(val2);
    }

    @Override
    public int getPriority() {
        return 4;
    }

    @Override
    public String getDescription() {
        return getName();
    }
}