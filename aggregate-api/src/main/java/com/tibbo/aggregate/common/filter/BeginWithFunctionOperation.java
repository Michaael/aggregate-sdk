package com.tibbo.aggregate.common.filter;

import com.tibbo.aggregate.common.expression.function.DefaultFunctions;

public class BeginWithFunctionOperation extends SubstringFunctionOperation {

    public BeginWithFunctionOperation(Expression[] operands) {
        super(DefaultFunctions.STARTS_WITH.impl, DefaultFunctions.STARTS_WITH.getName(), operands);
    }

}
