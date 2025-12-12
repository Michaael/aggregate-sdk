package com.tibbo.aggregate.common.filter;

import com.tibbo.aggregate.common.expression.Function;

import java.util.Arrays;
import java.util.stream.Collectors;

public class WrappingFunction extends FunctionOperation {

    private final Function func;

    public WrappingFunction(Function func, Expression... operands) {
        super(func.getName(), operands);
        this.func = func;
    }

    @Override
    public Object evaluate() {
        try {
            Object[] values = Arrays.stream(operands).map(Expression::evaluate).toArray();
            return func.execute(null, null, values);
        } catch (Exception e) {
            throw new IllegalStateException();
        }
    }
}
