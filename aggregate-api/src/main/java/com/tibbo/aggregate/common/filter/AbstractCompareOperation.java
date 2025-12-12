package com.tibbo.aggregate.common.filter;

import java.util.Date;

public abstract class AbstractCompareOperation extends AbstractBaseBinaryOperation {

    public AbstractCompareOperation(String name, Expression operand1, Expression operand2) {
        super(name, operand1, operand2);
    }


    @Override
    protected void validateOperandType(Object value) {
        if (value != null && !(value instanceof Number)
                && !(value instanceof Date)
                && !(value instanceof String)) {
            throw new SmartFilterIllegalOperandException(this, Number.class, String.class, Date.class);
        }
    }

    @Override
    public int getPriority() {
        return 3;
    }
}
