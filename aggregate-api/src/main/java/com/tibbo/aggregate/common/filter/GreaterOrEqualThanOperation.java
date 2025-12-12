package com.tibbo.aggregate.common.filter;


public class GreaterOrEqualThanOperation extends AbstractCompareOperation {

    public static final String NAME = "GREATER_OR_EQUAL_THAN";

    public GreaterOrEqualThanOperation(Expression operand1, Expression operand2) {
        super(NAME, operand1, operand2);
    }

    @Override
    protected Object binaryEvaluation(Object operand1, Object operand2) {
        if (operand1 == null || operand2 == null) {
            return null;
        }

        Comparable c1 = (Comparable) operand1;
        Comparable c2 = (Comparable) operand2;

        int res = c1.compareTo(c2);
        return res >= 0;
    }

    @Override
    public String getDescription() {
        return ">=";
    }
}
