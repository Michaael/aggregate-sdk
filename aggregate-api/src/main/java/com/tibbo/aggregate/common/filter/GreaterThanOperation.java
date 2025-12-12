package com.tibbo.aggregate.common.filter;

public class GreaterThanOperation extends AbstractCompareOperation {

    public static final String NAME = "GREATER_THAN";

    public GreaterThanOperation(Expression operand1, Expression operand2) {
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
        return res > 0;
    }

    @Override
    public String getDescription() {
        return ">";
    }
}
