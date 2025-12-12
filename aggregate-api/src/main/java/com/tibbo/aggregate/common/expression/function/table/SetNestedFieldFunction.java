package com.tibbo.aggregate.common.expression.function.table;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.datatable.DataTable;
import com.tibbo.aggregate.common.expression.EvaluationEnvironment;
import com.tibbo.aggregate.common.expression.EvaluationException;
import com.tibbo.aggregate.common.expression.Evaluator;
import com.tibbo.aggregate.common.expression.Function;
import com.tibbo.aggregate.common.expression.function.AbstractFunction;
import com.tibbo.aggregate.common.util.Util;

public class SetNestedFieldFunction extends AbstractFunction
{
    public SetNestedFieldFunction()
    {
        super("setNestedField", Function.GROUP_DATA_TABLE_PROCESSING, "DataTable table, Object value, String field1, Integer row1 [, String field2, Integer row2, ...]", "DataTable", Cres.get().getString("fDescSetNestedField"));
    }

    @Override
    public Object execute(Evaluator evaluator, EvaluationEnvironment environment, Object... parameters) throws EvaluationException
    {
        checkParameters(4, true, parameters);
        checkParameterType(0, parameters[0], DataTable.class);

        DataTable table = ((DataTable) parameters[0]).cloneIfImmutable();

        DataTable sourceTable = table;
        String field = null;
        int row = 0;

        for (int pairNum = 1; pairNum <= (parameters.length - 2) / 2; pairNum++)
        {
            if (pairNum > 1)
            {
                table = table.getRecord(row).getDataTable(field);
            }

            field = Util.convertToString(parameters[2 + (pairNum - 1) * 2], true, false);

            row  = Util.convertToNumber(parameters[2 + (pairNum - 1) * 2 + 1], true, false).intValue();
        }

        table.getRecord(row).setValueSmart(field, parameters[1]);

        return sourceTable;
    }
}
