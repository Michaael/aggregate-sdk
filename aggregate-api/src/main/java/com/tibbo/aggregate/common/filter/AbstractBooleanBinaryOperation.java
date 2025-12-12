package com.tibbo.aggregate.common.filter;

public abstract class AbstractBooleanBinaryOperation extends AbstractBaseBinaryOperation {
    public AbstractBooleanBinaryOperation(String name, Expression operand1, Expression operand2) {
        super(name, operand1, operand2);
    }

    @Override
    protected void validateOperandType(Object value) {
        if (value != null && !(value instanceof Boolean)) {
            throw new SmartFilterIllegalOperandException(this, Boolean.class);
        }
    }
}
