package com.tibbo.aggregate.common.filter;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.expression.function.DefaultFunctions;

public class ContainsExFunctionOperation extends FunctionOperation {
    public ContainsExFunctionOperation(Expression... operands) {
        super(FilterFunctions.SIMPLE_CONTAINS.getName(), operands);
        if (operands.length < 2) {
            throw new SmartFilterIllegalOperationException(Cres.get().getString("smartFilterIllegalNumberOfOperandsInFunction") + ": " + getName());
        }
    }

    @Override
    public Object evaluate() {
        Object first = operands[0].evaluate();
        Object second = operands[1].evaluate();
        if (!(first instanceof String) || !(second instanceof String)) {
            throw new SmartFilterIllegalOperandException(this, String.class);
        }
        try {
            return DefaultFunctions.CONTAINS.impl.execute(null, null, first, second);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
