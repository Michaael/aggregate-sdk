package com.tibbo.aggregate.common.filter;

import com.tibbo.aggregate.common.expression.function.DefaultFunctions;

public class EndsWithFunctionOperation extends SubstringFunctionOperation {
    public EndsWithFunctionOperation(Expression... operands) {
        super(DefaultFunctions.ENDS_WITH.impl, DefaultFunctions.ENDS_WITH.getName(), operands);
    }
}
