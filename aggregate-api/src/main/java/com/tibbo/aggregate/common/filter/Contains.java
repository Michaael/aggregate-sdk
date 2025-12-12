package com.tibbo.aggregate.common.filter;


public abstract class Contains extends BinaryStringOperation {

    public static final String NAME = "CONTAINS";

    public Contains(Expression operand1, Expression operand2) {
        super(NAME, operand1, operand2);
    }

    @Override
    protected Object binaryEvaluation(Object operand1, Object operand2) {
        String op1 = (String) operand1;
        String op2 = (String) operand2;

        return op1.contains(op2);
    }
}
