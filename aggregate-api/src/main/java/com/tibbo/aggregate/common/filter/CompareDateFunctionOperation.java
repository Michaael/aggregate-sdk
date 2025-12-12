package com.tibbo.aggregate.common.filter;

import java.text.ParseException;
import java.util.Date;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.util.DateUtils;

public abstract class CompareDateFunctionOperation extends ColumnFunctionOperation {

    public CompareDateFunctionOperation(String name, Expression... operands) {
        super(name, operands);
        if (operands.length != 2) {
            throw new IllegalArgumentException("Illegal operand number in function: " + getName());
        }
    }

    @Override
    public final Object evaluate() {
        ColumnName columnName = getColumn();
        Object columnValue = columnName.evaluate();
        Object operandValue = operands[1].evaluate();
        if (columnValue == null || operandValue == null) {
            return null;
        }

        if (!(columnValue instanceof Date)) {
            throw new SmartFilterEvaluationException(Cres.get().getString("smartFilterIllegalColumnTypeDateExpected") + ": " + columnName.getName());
        }

        if (operandValue instanceof String) {
            try {
                operandValue = DateUtils.parseSmart((String) operandValue);
            } catch (ParseException e) {
                throw new SmartFilterIllegalOperandException(this, Cres.get().getString("smartFilterOperandNotValidDate") + ": " + operandValue);
            }
        }

        Date column = (Date) columnValue;
        Date operand = (Date) operandValue;
        return compareDate(column, operand);
    }

    public abstract boolean compareDate(Date column, Date value);
}
