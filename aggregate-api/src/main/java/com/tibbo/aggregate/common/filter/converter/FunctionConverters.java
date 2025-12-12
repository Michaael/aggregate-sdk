package com.tibbo.aggregate.common.filter.converter;

import com.tibbo.aggregate.common.datatable.DataRecord;
import com.tibbo.aggregate.common.filter.ColumnName;
import com.tibbo.aggregate.common.filter.Expression;
import com.tibbo.aggregate.common.view.ViewFilterElement;


public class FunctionConverters {

    public static DataRecord contains(Expression expression, DataRecord dataRecord) {
        return makeColumnOperation(expression, dataRecord, ViewFilterElement.OPERATION_CONTAINS, String.class);
    }

    public static DataRecord startWith(Expression expression, DataRecord dataRecord) {
        return makeColumnOperation(expression, dataRecord, ViewFilterElement.OPERATION_BEGINS_WITH, String.class);
    }

    public static DataRecord endsWith(Expression expression, DataRecord dataRecord) {
        return makeColumnOperation(expression, dataRecord, ViewFilterElement.OPERATION_ENDS_WITH, String.class);
    }

    public static DataRecord onAfter(Expression expression, DataRecord dataRecord) {
        return makeColumnOperation(expression, dataRecord, ViewFilterElement.OPERATION_ON_OR_AFTER, String.class);
    }

    public static DataRecord onBefore(Expression expression, DataRecord dataRecord) {
        return makeColumnOperation(expression, dataRecord, ViewFilterElement.OPERATION_ON_OR_BEFORE, String.class);
    }

    public static DataRecord on(Expression expression, DataRecord dataRecord) {
        return makeColumnOperation(expression, dataRecord, ViewFilterElement.OPERATION_ON, String.class);
    }

    public static DataRecord thisHour(Expression expression, DataRecord dataRecord) {
        return makeSingleColumnOperation(expression, dataRecord, ViewFilterElement.OPERATION_THIS_HOUR);
    }

    public static DataRecord yesterday(Expression expression, DataRecord dataRecord) {
        return makeSingleColumnOperation(expression, dataRecord, ViewFilterElement.OPERATION_YESTERDAY);
    }

    public static DataRecord today(Expression expression, DataRecord dataRecord) {
        return makeSingleColumnOperation(expression, dataRecord, ViewFilterElement.OPERATION_TODAY);
    }

    public static DataRecord tomorrow(Expression expression, DataRecord dataRecord) {
        return makeSingleColumnOperation(expression, dataRecord, ViewFilterElement.OPERATION_TOMORROW);
    }

    public static DataRecord thisWeek(Expression expression, DataRecord dataRecord) {
        return makeSingleColumnOperation(expression, dataRecord, ViewFilterElement.OPERATION_THIS_WEEK);
    }

    public static DataRecord thisMonth(Expression expression, DataRecord dataRecord) {
        return makeSingleColumnOperation(expression, dataRecord, ViewFilterElement.OPERATION_THIS_MONTH);
    }

    public static DataRecord thisYear(Expression expression, DataRecord dataRecord) {
        return makeSingleColumnOperation(expression, dataRecord, ViewFilterElement.OPERATION_THIS_YEAR);
    }

    public static DataRecord lastHours(Expression expression, DataRecord dataRecord) {
        return makeColumnOperation(expression, dataRecord, ViewFilterElement.OPERATION_LAST_X_HOURS, Number.class);
    }

    public static DataRecord nextHours(Expression expression, DataRecord dataRecord) {
        return makeColumnOperation(expression, dataRecord, ViewFilterElement.OPERATION_NEXT_X_HOURS, Number.class);
    }

    public static DataRecord lastDays(Expression expression, DataRecord dataRecord) {
        return makeColumnOperation(expression, dataRecord, ViewFilterElement.OPERATION_LAST_X_DAYS, Number.class);
    }

    public static DataRecord nextDays(Expression expression, DataRecord dataRecord) {
        return makeColumnOperation(expression, dataRecord, ViewFilterElement.OPERATION_NEXT_X_DAYS, Number.class);
    }

    public static DataRecord lastWeeks(Expression expression, DataRecord dataRecord) {
        return makeColumnOperation(expression, dataRecord, ViewFilterElement.OPERATION_LAST_X_WEEKS, Number.class);
    }

    public static DataRecord nextWeeks(Expression expression, DataRecord dataRecord) {
        return makeColumnOperation(expression, dataRecord,  ViewFilterElement.OPERATION_NEXT_X_WEEKS, Number.class);
    }

    public static DataRecord lastMonths(Expression expression, DataRecord dataRecord) {
        return makeColumnOperation(expression, dataRecord, ViewFilterElement.OPERATION_LAST_X_MONTHS, Number.class);
    }

    public static DataRecord nextMonths(Expression expression, DataRecord dataRecord) {
        return makeColumnOperation(expression, dataRecord, ViewFilterElement.OPERATION_NEXT_X_MONTHS, Number.class);
    }

    public static DataRecord lastYears(Expression expression, DataRecord dataRecord) {
        return makeColumnOperation(expression, dataRecord, ViewFilterElement.OPERATION_LAST_X_YEARS, Number.class);
    }

    public static DataRecord nextYears(Expression expression, DataRecord dataRecord) {
        return makeColumnOperation(expression, dataRecord, ViewFilterElement.OPERATION_NEXT_X_YEARS, Number.class);
    }

    public static DataRecord isNull(Expression expression, DataRecord dataRecord) {
        return makeSingleColumnOperation(expression, dataRecord, ViewFilterElement.OPERATION_IS_NULL);
    }

    public static DataRecord isNotNull(Expression expression, DataRecord dataRecord) {
        return makeSingleColumnOperation(expression, dataRecord, ViewFilterElement.OPERATION_IS_NOT_NULL);
    }

    public static DataRecord includedIn(Expression expression, DataRecord dataRecord) {
        Expression columnExpression = expression.getChildren()[0];
        if (!(columnExpression instanceof ColumnName)) {
            throw new IllegalArgumentException("Column reference is expected. Operation included in");
        }
        ColumnName columnName = (ColumnName) columnExpression;
        StringBuilder values = new StringBuilder();
        boolean first = true;
        for (int i = 1; i < expression.getChildren().length; i++) {
            Object value = expression.getChildren()[i].evaluate();
            if (!first) {
                values.append(',');
            }
            values.append(value.toString());
            first = false;
        }
        dataRecord.setValue(ViewFilterElement.FIELD_OPERATION, ViewFilterElement.OPERATION_IN);
        dataRecord.setValue(ViewFilterElement.FIELD_COLUMN, columnName.getName());
        dataRecord.setValue(ViewFilterElement.FIELD_VALUE, values.toString());
        return dataRecord;
    }

    private static DataRecord makeSingleColumnOperation(Expression expression, DataRecord dataRecord, String operation) {
        Expression expr = expression.getChildren()[0];
        dataRecord.setValue(ViewFilterElement.FIELD_OPERATION, operation);
        dataRecord.setValue(ViewFilterElement.FIELD_COLUMN, ((ColumnName)expr).getName());
        return dataRecord;
    }

    private static DataRecord makeColumnOperation(Expression expression, DataRecord dataRecord, String operation, Class<?> allowedOperand) {
        Expression leftExpression = expression.getChildren()[0];
        Expression rightExpression = expression.getChildren()[1];

        dataRecord.setValue(ViewFilterElement.FIELD_OPERATION, operation);
        dataRecord.setValue(ViewFilterElement.FIELD_COLUMN, ((ColumnName)leftExpression).getName());
        dataRecord.setValue(ViewFilterElement.FIELD_VALUE, rightExpression.evaluate().toString());
        return dataRecord;
    }
}
