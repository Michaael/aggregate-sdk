package com.tibbo.aggregate.common.filter.converter;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.datatable.DataRecord;
import com.tibbo.aggregate.common.datatable.DataTable;
import com.tibbo.aggregate.common.datatable.SimpleDataTable;
import com.tibbo.aggregate.common.datatable.TableFormat;
import com.tibbo.aggregate.common.filter.AbstractBooleanBinaryOperation;
import com.tibbo.aggregate.common.filter.BeginWithFunctionOperation;
import com.tibbo.aggregate.common.filter.ContainsFunctionOperation;
import com.tibbo.aggregate.common.filter.DoesNotEqualOperation;
import com.tibbo.aggregate.common.filter.EndsWithFunctionOperation;
import com.tibbo.aggregate.common.filter.EqualsOperation;
import com.tibbo.aggregate.common.filter.Expression;
import com.tibbo.aggregate.common.filter.GreaterOrEqualThanOperation;
import com.tibbo.aggregate.common.filter.GreaterThanOperation;
import com.tibbo.aggregate.common.filter.InFunctionOperation;
import com.tibbo.aggregate.common.filter.IsNotNullFunctionOperation;
import com.tibbo.aggregate.common.filter.IsNullFunctionOperation;
import com.tibbo.aggregate.common.filter.LastDaysFunctionOperation;
import com.tibbo.aggregate.common.filter.LastHoursFunctionOperation;
import com.tibbo.aggregate.common.filter.LastMonthsFunctionOperation;
import com.tibbo.aggregate.common.filter.LastWeeksFunctionOperation;
import com.tibbo.aggregate.common.filter.LastYearsFunctionOperation;
import com.tibbo.aggregate.common.filter.LessOrEqualThanOperation;
import com.tibbo.aggregate.common.filter.LessThanOperation;
import com.tibbo.aggregate.common.filter.LogicalAndOperation;
import com.tibbo.aggregate.common.filter.LogicalNotOperation;
import com.tibbo.aggregate.common.filter.LogicalOrOperation;
import com.tibbo.aggregate.common.filter.NextDaysFunctionOperation;
import com.tibbo.aggregate.common.filter.NextHoursFunctionOperation;
import com.tibbo.aggregate.common.filter.NextMonthsFunctionOperation;
import com.tibbo.aggregate.common.filter.NextWeeksFunctionOperation;
import com.tibbo.aggregate.common.filter.NextYearsFunctionOperation;
import com.tibbo.aggregate.common.filter.OnAfterFunctionOperation;
import com.tibbo.aggregate.common.filter.OnBeforeFunctionOperation;
import com.tibbo.aggregate.common.filter.OnFunctionOperation;
import com.tibbo.aggregate.common.filter.ThisHourFunctionOperation;
import com.tibbo.aggregate.common.filter.ThisMonthFunctionOperation;
import com.tibbo.aggregate.common.filter.ThisWeekFunctionOperation;
import com.tibbo.aggregate.common.filter.ThisYearFunctionOperation;
import com.tibbo.aggregate.common.filter.TodayFunctionOperation;
import com.tibbo.aggregate.common.filter.TomorrowFunctionOperation;
import com.tibbo.aggregate.common.filter.YesterdayFunctionOperation;
import com.tibbo.aggregate.common.view.ViewFilterElement;

public class ClassDteFilterConverter {

    private static final Map<Class<? extends Expression>, BiFunction<Expression, DataRecord, DataRecord>> operationConverters = new HashMap<>();
    private static final Map<String, BiFunction<Expression, DataRecord, DataRecord>> functionConverters = new HashMap<>();

    private static final Set<Class<? extends Expression>> logicalNotOperationAllowedSubexpressions = new HashSet<>();

    static {
        // Logical not operation allowed subexpression
        logicalNotOperationAllowedSubexpressions.add(LogicalNotOperation.class);            // itself is allowed
        logicalNotOperationAllowedSubexpressions.add(BeginWithFunctionOperation.class);
        logicalNotOperationAllowedSubexpressions.add(EndsWithFunctionOperation.class);
        logicalNotOperationAllowedSubexpressions.add(ContainsFunctionOperation.class);
        logicalNotOperationAllowedSubexpressions.add(EqualsOperation.class);
        logicalNotOperationAllowedSubexpressions.add(DoesNotEqualOperation.class);
        logicalNotOperationAllowedSubexpressions.add(LessThanOperation.class);
        logicalNotOperationAllowedSubexpressions.add(LessOrEqualThanOperation.class);
        logicalNotOperationAllowedSubexpressions.add(GreaterThanOperation.class);
        logicalNotOperationAllowedSubexpressions.add(GreaterOrEqualThanOperation.class);

        operationConverters.put(EqualsOperation.class, BinaryExpressionConverter.create(ViewFilterElement.OPERATION_EQUALS, Number.class, Boolean.class, String.class, Date.class));
        operationConverters.put(DoesNotEqualOperation.class, BinaryExpressionConverter.create(ViewFilterElement.OPERATION_DOES_NOT_EQUAL, Number.class, Boolean.class, String.class, Date.class));
        operationConverters.put(GreaterThanOperation.class, BinaryExpressionConverter.create(ViewFilterElement.OPERATION_IS_GREATER_THAN, Number.class, Boolean.class, String.class, Date.class));
        operationConverters.put(GreaterOrEqualThanOperation.class, BinaryExpressionConverter.create(ViewFilterElement.OPERATION_IS_GREATER_OR_EQUAL_THAN, Number.class, Boolean.class, String.class, Date.class));
        operationConverters.put(LessThanOperation.class, BinaryExpressionConverter.create(ViewFilterElement.OPERATION_IS_LESS_THAN, Number.class, Boolean.class, String.class, Date.class));
        operationConverters.put(LessOrEqualThanOperation.class, BinaryExpressionConverter.create(ViewFilterElement.OPERATION_IS_LESS_OR_EQUAL_THAN, Number.class, Boolean.class, String.class, Date.class));

        operationConverters.put(ContainsFunctionOperation.class, FunctionConverters::contains);
        operationConverters.put(BeginWithFunctionOperation.class, FunctionConverters::startWith);
        operationConverters.put(EndsWithFunctionOperation.class, FunctionConverters::endsWith);
        operationConverters.put(OnAfterFunctionOperation.class, FunctionConverters::onAfter);
        operationConverters.put(OnBeforeFunctionOperation.class, FunctionConverters::onBefore);
        operationConverters.put(OnFunctionOperation.class, FunctionConverters::on);
        operationConverters.put(ThisHourFunctionOperation.class, FunctionConverters::thisHour);
        operationConverters.put(YesterdayFunctionOperation.class, FunctionConverters::yesterday);
        operationConverters.put(TodayFunctionOperation.class, FunctionConverters::today);
        operationConverters.put(TomorrowFunctionOperation.class, FunctionConverters::tomorrow);
        operationConverters.put(ThisWeekFunctionOperation.class, FunctionConverters::thisWeek);
        operationConverters.put(ThisMonthFunctionOperation.class, FunctionConverters::thisMonth);
        operationConverters.put(ThisYearFunctionOperation.class, FunctionConverters::thisYear);
        operationConverters.put(LastHoursFunctionOperation.class, FunctionConverters::lastHours);
        operationConverters.put(NextHoursFunctionOperation.class, FunctionConverters::nextHours);
        operationConverters.put(LastDaysFunctionOperation.class, FunctionConverters::lastDays);
        operationConverters.put(NextDaysFunctionOperation.class, FunctionConverters::nextDays);
        operationConverters.put(LastWeeksFunctionOperation.class, FunctionConverters::lastWeeks);
        operationConverters.put(NextWeeksFunctionOperation.class, FunctionConverters::nextWeeks);
        operationConverters.put(LastMonthsFunctionOperation.class, FunctionConverters::lastMonths);
        operationConverters.put(NextMonthsFunctionOperation.class, FunctionConverters::nextMonths);
        operationConverters.put(LastYearsFunctionOperation.class, FunctionConverters::lastYears);
        operationConverters.put(NextYearsFunctionOperation.class, FunctionConverters::nextYears);
        operationConverters.put(IsNullFunctionOperation.class, FunctionConverters::isNull);
        operationConverters.put(IsNotNullFunctionOperation.class, FunctionConverters::isNotNull);
        operationConverters.put(InFunctionOperation.class, FunctionConverters::includedIn);
    }

    public static DataTable convertExpressionToClassFilter(Expression filterExpression) {
        TableFormat fmt = ViewFilterElement.FORMAT.clone();
        DataTable res = new SimpleDataTable(fmt);
        expand(res, filterExpression, false);
        return res;
    }

    public static Expression convertClassFilterToExpression(DataTable classFilter) {
        return null;
    }

    private static void expand(DataTable res, Expression expression, boolean makeSubexpression) {
        if (expression instanceof AbstractBooleanBinaryOperation) {
            AbstractBooleanBinaryOperation booleanOperation = (AbstractBooleanBinaryOperation) expression;
            if (makeSubexpression) {
                DataRecord rec = res.getRecordCount() > 0 ? res.getRecord(res.getRecordCount() - 1)
                        : res.addRecord();
                DataTable subExpression = new SimpleDataTable(ViewFilterElement.FORMAT.clone());
                expandBoolean(subExpression, booleanOperation);
                rec.setValue(ViewFilterElement.FIELD_TYPE, ViewFilterElement.TYPE_NESTED_CONDITIONS);
                rec.setValue(ViewFilterElement.FIELD_NESTED, subExpression);
            } else {
                expandBoolean(res, booleanOperation);
            }
        } else if (expression instanceof LogicalNotOperation) {
            LogicalNotOperation logicalNotOperation = (LogicalNotOperation) expression;
            validateNotOperationAvailabilityForExpression(logicalNotOperation.getChildren()[0]);
            expand(res, logicalNotOperation.getChildren()[0], makeSubexpression);
            applyLogicalInversion(res);
        } else {
            DataRecord rec = res.getRecordCount() > 0 ? res.getRecord(res.getRecordCount() - 1)
                    : res.addRecord();
            //rec.setValue(ViewFilterElement.FIELD_OPERATION, getClassDteFilterName(expression));
            rec.setValue(ViewFilterElement.FIELD_TYPE, ViewFilterElement.TYPE_CONDITION);
            BiFunction<Expression, DataRecord, DataRecord> ec = operationConverters.get(expression.getClass());
            ec.apply(expression, rec);
        }
    }

    private static void applyLogicalInversion(DataTable res) {
        assert res.getRecordCount() > 0;
        DataRecord rec = res.getRecord(res.getRecordCount() - 1);
        String operation = rec.getString(ViewFilterElement.FIELD_OPERATION);
        switch (operation) {
            case ViewFilterElement.OPERATION_BEGINS_WITH:
                rec.setValue(ViewFilterElement.FIELD_OPERATION, ViewFilterElement.OPERATION_DOES_NOT_BEGIN_WITH);
                break;
            case ViewFilterElement.OPERATION_DOES_NOT_BEGIN_WITH:
                rec.setValue(ViewFilterElement.FIELD_OPERATION, ViewFilterElement.OPERATION_BEGINS_WITH);
                break;
            case ViewFilterElement.OPERATION_ENDS_WITH:
                rec.setValue(ViewFilterElement.FIELD_OPERATION, ViewFilterElement.OPERATION_DOES_NOT_END_WITH);
                break;
            case ViewFilterElement.OPERATION_DOES_NOT_END_WITH:
                rec.setValue(ViewFilterElement.FIELD_OPERATION, ViewFilterElement.OPERATION_ENDS_WITH);
                break;
            case ViewFilterElement.OPERATION_CONTAINS:
                rec.setValue(ViewFilterElement.FIELD_OPERATION, ViewFilterElement.OPERATION_DOES_NOT_CONTAIN);
                break;
            case ViewFilterElement.OPERATION_DOES_NOT_CONTAIN:
                rec.setValue(ViewFilterElement.FIELD_OPERATION, ViewFilterElement.OPERATION_CONTAINS);
                break;
        }
    }

    private static void validateNotOperationAvailabilityForExpression(Expression expression) {
        if (!logicalNotOperationAllowedSubexpressions.contains(expression.getClass())) {
            throw new ConversionException(Cres.get().getString("smartFilterLogicalNotOperationNotSupportedFor") + ": " + expression.getDescription());
        }
    }

    private static void expandBoolean(DataTable res, AbstractBooleanBinaryOperation operation) {

        Expression left = operation.getChildren()[0];
        Expression right = operation.getChildren()[1];
        expand(res, left, operation.getPriority() > left.getPriority());

        assert res.getRecordCount() > 0;
        DataRecord rec = res.addRecord();
        rec.setValue(ViewFilterElement.FIELD_LOGICAL, getBooleanOperationCode(operation));
        rec.setValue(ViewFilterElement.FIELD_TYPE, ViewFilterElement.TYPE_CONDITION);
        expand(res, right, operation.getPriority() >= right.getPriority());
    }

    private static int getBooleanOperationCode(AbstractBooleanBinaryOperation operation) {
        if (operation instanceof LogicalAndOperation) {
            return ViewFilterElement.LOGICAL_OPERATION_AND;
        } else if (operation instanceof LogicalOrOperation) {
            return ViewFilterElement.LOGICAL_OPERATION_OR;
        } else {
            throw new IllegalStateException("Illegal boolean operation");
        }
    }

}
