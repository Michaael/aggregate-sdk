package com.tibbo.aggregate.common.filter.converter;

import com.tibbo.aggregate.common.datatable.DataRecord;
import com.tibbo.aggregate.common.filter.ColumnName;
import com.tibbo.aggregate.common.filter.Expression;
import com.tibbo.aggregate.common.filter.StringLiteral;
import com.tibbo.aggregate.common.view.ViewFilterElement;

import java.util.Arrays;
import java.util.function.BiFunction;

public class BinaryExpressionConverter implements BiFunction<Expression, DataRecord, DataRecord> {

    private final String classFilterCondition;
    private final Class<?>[] allowedOperandTypes;

    private BinaryExpressionConverter(String classFilterCondition, Class<?>[] allowedOperandTypes) {
        this.classFilterCondition = classFilterCondition;
        this.allowedOperandTypes = allowedOperandTypes;
    }

    @Override
    public DataRecord apply(Expression expression, DataRecord dataRecord) {
        Expression leftExpression = expression.getChildren()[0];
        Expression rightExpression = expression.getChildren()[1];

        String colName;
        Object value;

        if (leftExpression instanceof ColumnName) {
            colName = ((ColumnName) leftExpression).getName();
            value = rightExpression.evaluate();
            if (rightExpression instanceof StringLiteral)
            {
                value = "'" + value + "'";
            }
        } else if (rightExpression instanceof ColumnName) {
            colName = ((ColumnName) rightExpression).getName();
            value = leftExpression.evaluate();
            if (leftExpression instanceof StringLiteral)
            {
                value = "'" + value + "'";
            }
        } else {
            throw new IllegalArgumentException("Column reference is expected");
        }

        validateValue(value);
        dataRecord.setValue(ViewFilterElement.FIELD_OPERATION, classFilterCondition);
        dataRecord.setValue(ViewFilterElement.FIELD_COLUMN, colName);
        dataRecord.setValue(ViewFilterElement.FIELD_VALUE, value.toString());

        return dataRecord;
    }

    private void validateValue(Object value) {
        if (Arrays.stream(allowedOperandTypes).noneMatch(c -> c.isAssignableFrom(value.getClass()))) {
            throw new IllegalArgumentException("Illegal value type");
        }
    }

    public static BiFunction<Expression, DataRecord, DataRecord> create(String classFilterCondition, Class<?> ... allowedOperandTypes) {
        return new BinaryExpressionConverter(classFilterCondition, allowedOperandTypes);
    }
}
