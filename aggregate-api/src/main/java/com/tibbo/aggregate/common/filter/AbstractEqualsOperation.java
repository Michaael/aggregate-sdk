package com.tibbo.aggregate.common.filter;

import java.util.Date;

public abstract class AbstractEqualsOperation extends AbstractBaseBinaryOperation {

    public AbstractEqualsOperation(String name, Expression operand1, Expression operand2) {
        super(name, operand1, operand2);
    }


    @Override
    protected void validateOperandType(Object value) {
        if (value != null && !(value instanceof Number)
                && !(value instanceof String) && !(value instanceof Boolean) && !(value instanceof Date)) {
            throw new SmartFilterIllegalOperandException(this, Number.class, String.class, Boolean.class);
        }
    }

    @Override
    public int getPriority() {
        return 4;
    }
}
