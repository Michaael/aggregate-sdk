package com.tibbo.aggregate.common.filter;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.expression.Function;

public abstract class SubstringFunctionOperation extends ColumnFunctionOperation {

    private final Function func;

    public SubstringFunctionOperation(Function func, String name, Expression... operands) {
        super(name, operands);
        if (operands.length != 2) {
            throw new SmartFilterIllegalOperationException(Cres.get().getString("smartFilterIllegalNumberOfOperandsInFunction") + ": " + getName());
        }
        this.func = func;
    }

    @Override
    public final Object evaluate() {
        Object columnValue = getColumn().evaluate();
        Object operandValue = operands[1].evaluate();
        if (columnValue == null || operandValue == null)
        {
            return false;
        }
        if (!(columnValue instanceof String) || !(operandValue instanceof String)) {
            throw new SmartFilterIllegalOperandException(this, String.class);
        }
        try {
            return func.execute(null, null, columnValue, operandValue);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
