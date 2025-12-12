package com.tibbo.aggregate.common.expression.function.table;

import java.util.Date;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.datatable.DataTable;
import com.tibbo.aggregate.common.expression.EvaluationEnvironment;
import com.tibbo.aggregate.common.expression.EvaluationException;
import com.tibbo.aggregate.common.expression.Evaluator;
import com.tibbo.aggregate.common.expression.Function;
import com.tibbo.aggregate.common.expression.function.AbstractFunction;
import com.tibbo.aggregate.common.util.Util;

public class SetTimestampFunction extends AbstractFunction
{

    public SetTimestampFunction() {
        super("setTimestamp", Function.GROUP_DATA_TABLE_PROCESSING, "DataTable table, Date timestamp", "DataTable", Cres.get().getString("fDescSetTimestamp"));
    }

    @Override
    public Object execute(Evaluator evaluator, EvaluationEnvironment environment, Object... parameters) throws EvaluationException
    {
        checkParameters(2, true, parameters);
        checkParameterType(0, parameters[0], DataTable.class);
        DataTable source = (DataTable) parameters[0];
        Date timestamp = Util.convertToDate(parameters[1], true, true);
        source.setTimestamp(timestamp);
        return source;
    }
}