package com.tibbo.aggregate.common.expression.function.other;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.expression.EvaluationEnvironment;
import com.tibbo.aggregate.common.expression.EvaluationException;
import com.tibbo.aggregate.common.expression.Evaluator;
import com.tibbo.aggregate.common.expression.Function;
import com.tibbo.aggregate.common.expression.function.AbstractFunction;
import com.tibbo.aggregate.common.util.Util;

public class OrFunction extends AbstractFunction
{
    public OrFunction()
    {
        super("or", Function.GROUP_OTHER, "Boolean condition1 [, Boolean condition2, ...]", "Boolean", Cres.get().getString("fDescOr"));
    }

    @Override
    public Object execute(Evaluator evaluator, EvaluationEnvironment environment, Object... parameters) throws EvaluationException
    {
        checkParameters(1, false, parameters);

        boolean result = false;

        for (int i = 0; i < parameters.length; i++)
        {
            result |= Util.convertToBoolean(parameters[i], true, false);
        }

        return result;
    }
}