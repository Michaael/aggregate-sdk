package com.tibbo.aggregate.common.filter;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.tibbo.aggregate.common.util.DateUtils;

public abstract class AbstractBaseBinaryOperation extends BinaryOperation {
    public AbstractBaseBinaryOperation(String name, Expression operand1, Expression operand2) {
        super(name, operand1, operand2);
    }

    @Override
    public int getPriority() {
        return 4;
    }

    @Override
    protected final List<Object> evaluateOperands() {
        Object value1 = operand1.evaluate();
        Object value2 = operand2.evaluate();

        validateOperandType(value1);
        validateOperandType(value2);

        if (value1 instanceof Number && value2 instanceof Number) {
            Class<? extends Number> greatestType = CastUtils.promoteTypes(((Number)value1).getClass(), ((Number)value2).getClass());
            value1 = new BigDecimal(value1.toString());
            value2 = new BigDecimal(value2.toString());
        }

        if (value1 instanceof Date && value2 instanceof String) {
            try {
                value2 = DateUtils.parseSmart((String) value2);
            } catch (ParseException e) {
                throw new SmartFilterIllegalOperandException(this, "Operand: \"" + value2 + "\" not a valid Date");
            }
        } else if (value1 instanceof String && value2 instanceof Date) {
            try {
                value1 = DateUtils.parseSmart((String) value1);
            } catch (ParseException e) {
                throw new SmartFilterIllegalOperandException(this, "Operand: \"" + value1 + "\" not a valid Date");
            }
        } else if (value1 instanceof Number && value2 instanceof String) {
            try {
                value1 = new Date((Long) value1);
                value2 = DateUtils.parseSmart((String) value2);
            } catch (ParseException e) {
                throw new SmartFilterIllegalOperandException(this, "Operand: \"" + value2 + "\" not a valid Date");
            }
        }

        if (value1 != null && value2 != null) {
            if (value1.getClass() != value2.getClass() && !value2.getClass().isAssignableFrom(value1.getClass()) && value1.getClass().isAssignableFrom(value2.getClass())) {
                throw new SmartFilterIllegalOperandException(this, "Operand types are not compatible");
            }
        }

        List<Object> result = new ArrayList<>(getOperandCount());
        result.add(value1);
        result.add(value2);

        return result;
    }

    protected abstract void validateOperandType(Object value);
}
