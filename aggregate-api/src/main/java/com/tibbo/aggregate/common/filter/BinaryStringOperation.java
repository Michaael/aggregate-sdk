package com.tibbo.aggregate.common.filter;

import java.util.ArrayList;
import java.util.List;

public abstract class BinaryStringOperation extends BinaryOperation {

    public BinaryStringOperation(String name, Expression operand1, Expression operand2) {
        super(name, operand1, operand2);
    }

    @Override
    protected List<Object> evaluateOperands() {
        Object val1 = operand1.evaluate();
        Object val2 = operand2.evaluate();

        if (!(val1 instanceof String)) {
            throw new SmartFilterIllegalOperandException(this, String.class);
        }

        if (!(val2 instanceof String)) {
            throw new SmartFilterIllegalOperandException(this, String.class);
        }

        List<Object> result = new ArrayList<>();
        result.add(val1);
        result.add(val2);

        return result;
    }
}
