package com.tibbo.aggregate.common.expression.function.other;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.expression.EvaluationEnvironment;
import com.tibbo.aggregate.common.expression.EvaluationException;
import com.tibbo.aggregate.common.expression.Evaluator;
import com.tibbo.aggregate.common.expression.Function;
import com.tibbo.aggregate.common.expression.function.AbstractFunction;
import com.tibbo.aggregate.common.util.Util;

public class AndFunction extends AbstractFunction
{
    public AndFunction()
    {
        super("and", Function.GROUP_OTHER, "Boolean condition1 [, Boolean condition2, ...]", "Boolean", Cres.get().getString("fDescAnd"));
    }

    @Override
    public Object execute(Evaluator evaluator, EvaluationEnvironment environment, Object... parameters) throws EvaluationException
    {
        checkParameters(1, false, parameters);

        boolean result = true;

        for (int i = 0; i < parameters.length; i++)
        {
            result &= Util.convertToBoolean(parameters[i], true, false);
        }

        return result;
    }
}