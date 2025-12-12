package com.tibbo.aggregate.common.filter;

import java.util.List;
import java.util.Objects;

public abstract class BinaryOperation extends Operation {

    private static final int OPERAND_COUNT = 2;
    protected final Expression operand1;
    protected final Expression operand2;

    public BinaryOperation(String name, Expression operand1, Expression operand2) {
        super(name);
        Objects.requireNonNull(operand1, "Operand 1 is null");
        Objects.requireNonNull(operand2, "Operand 2 is null");

        this.operand1 = operand1;
        this.operand2 = operand2;
    }

    protected abstract List<Object> evaluateOperands();
    protected abstract Object binaryEvaluation(Object operand1, Object operand2);

    @Override
    public final Object evaluate() {
        List<Object> operands = evaluateOperands();
        return binaryEvaluation(operands.get(0), operands.get(1));
    }

    @Override
    public final int getOperandCount() {
        return OPERAND_COUNT;
    }

    @Override
    public final Expression[] getChildren() {
        Expression[] children = new Expression[OPERAND_COUNT];
        children[0] = operand1;
        children[1] = operand2;
        return children;
    }
}
