package com.tibbo.aggregate.common.filter.converter;

import com.tibbo.aggregate.common.datatable.DataRecord;
import com.tibbo.aggregate.common.filter.Expression;

public interface ExpressionConverter {
    void convert(Expression expression, DataRecord dr);
}
