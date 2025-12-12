package com.tibbo.aggregate.common.filter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class InFunctionOperation extends ColumnFunctionOperation {

    public InFunctionOperation(Expression[] operands) {
        super(FilterFunctions.IN.getName(), operands);
        if (operands.length < 2) {
            throw new IllegalArgumentException("included in list is expected");
        }
    }

    @Override
    public Object evaluate() {
        Object value1 = operands[0].evaluate();
        if (value1 == null) {
            return null;
        }
        List<Object> value2 = Arrays.stream(operands).skip(1).map(Expression::evaluate).collect(Collectors.toList());

        if (value1 != null && !(value1 instanceof Number)
                && !(value1 instanceof String) && !(value1 instanceof Date)) {
            throw new SmartFilterIllegalOperandException(this, Number.class, String.class, Date.class);
        }

        List<Object> inListValues = new ArrayList<>(value2);

        if (inListValues.stream()
                .filter(v -> !(v instanceof Number))
                .filter(v -> !(v instanceof String)).anyMatch(v -> !(v instanceof Date))) {

            throw new SmartFilterIllegalOperandException(this, Number.class, String.class, Date.class);
        }

        cast(value1, inListValues);

        if (value1 instanceof Number)
        {
            return inListValues.stream().anyMatch(o -> ((BigDecimal) o).compareTo(new BigDecimal(value1.toString())) == 0);
        }
        return inListValues.stream().anyMatch(o -> o.equals(value1));
    }

    private void cast(Object operand, List<Object> listOfValues) {
        if (operand instanceof Number) {
            // cast
            for (int i = 0; i < listOfValues.size(); i++) {
                Object lv = listOfValues.get(i);
                if (lv instanceof Number) {
                    listOfValues.set(i, new BigDecimal(lv.toString()));
                }
            }
        }
    }
}
