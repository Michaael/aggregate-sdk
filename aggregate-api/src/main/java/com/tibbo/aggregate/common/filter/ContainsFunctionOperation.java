package com.tibbo.aggregate.common.filter;

import com.tibbo.aggregate.common.expression.function.DefaultFunctions;

public class ContainsFunctionOperation extends SubstringFunctionOperation {

    public ContainsFunctionOperation(Expression[] operands) {
        super(DefaultFunctions.CONTAINS.impl, DefaultFunctions.CONTAINS.getName(), operands);
    }
}
