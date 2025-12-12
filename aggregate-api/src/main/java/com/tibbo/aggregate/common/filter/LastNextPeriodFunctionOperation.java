package com.tibbo.aggregate.common.filter;

import java.util.Date;

import com.tibbo.aggregate.common.Cres;

public abstract class LastNextPeriodFunctionOperation extends ColumnFunctionOperation{

    public LastNextPeriodFunctionOperation(String name, Expression... operands) {
        super(name, operands);
    }

    @Override
    public Object evaluate() {
        ColumnName columnName = getColumn();
        Object columnValue = columnName.evaluate();
        Object operandValue = operands[1].evaluate();

        if (columnValue == null || operandValue == null) {
            return null;
        }

        if (!(columnValue instanceof Date)) {
            throw new SmartFilterEvaluationException(Cres.get().getString("smartFilterIllegalColumnTypeDateExpected") + ": " + columnName.getName());
        }

        if (!(operandValue instanceof Number)) {
            throw new SmartFilterEvaluationException(Cres.get().getString("smartFilterOperandNotValidDate") + ": Number");
        }
        Date column = (Date) columnValue;
        Number operand = (Number) operandValue;
        return checkDate(column, operand);
    }

    protected abstract Object checkDate(Date column, Number operand);
}
