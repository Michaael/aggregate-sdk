package com.tibbo.aggregate.common.filter;

import java.math.BigDecimal;

public class DoesNotEqualOperation extends AbstractEqualsOperation {

    public static final String NAME = "DOES_NOT_EQUAL";

    public DoesNotEqualOperation(Expression operand1, Expression operand2) {
        super(NAME, operand1, operand2);
    }

    @Override
    protected Object binaryEvaluation(Object operand1, Object operand2) {
        if (operand1 == null || operand2 == null) {
            return null;
        }

        if (operand1 instanceof BigDecimal && operand2 instanceof  BigDecimal)
        {
            return ((BigDecimal) operand1).compareTo((BigDecimal) operand2) != 0;
        }
        
        return !operand1.equals(operand2);
    }

    @Override
    public String getDescription() {
        return "!=";
    }
}
