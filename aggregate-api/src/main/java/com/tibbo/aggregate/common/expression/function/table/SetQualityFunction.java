package com.tibbo.aggregate.common.expression.function.table;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.datatable.DataTable;
import com.tibbo.aggregate.common.expression.EvaluationEnvironment;
import com.tibbo.aggregate.common.expression.EvaluationException;
import com.tibbo.aggregate.common.expression.Evaluator;
import com.tibbo.aggregate.common.expression.Function;
import com.tibbo.aggregate.common.expression.function.AbstractFunction;
import com.tibbo.aggregate.common.util.Util;

public class SetQualityFunction extends AbstractFunction
{

    public SetQualityFunction() {
        super("setQuality", Function.GROUP_DATA_TABLE_PROCESSING, "DataTable table, Integer quality", "DataTable", Cres.get().getString("fDescSetQuality"));
    }

    @Override
    public Object execute(Evaluator evaluator, EvaluationEnvironment environment, Object... parameters) throws EvaluationException
    {
        checkParameters(2, true, parameters);
        checkParameterType(0, parameters[0], DataTable.class);
        DataTable source = (DataTable) parameters[0];
        Number quality = Util.convertToNumber(parameters[1], true, true);
        source.setQuality(quality != null ? quality.intValue() : null);
        return source;
    }
}
