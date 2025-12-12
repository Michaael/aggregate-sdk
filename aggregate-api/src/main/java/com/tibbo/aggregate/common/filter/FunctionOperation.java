package com.tibbo.aggregate.common.filter;

import com.tibbo.aggregate.common.expression.function.DefaultFunctions;

public abstract class FunctionOperation extends Operation {

    protected final Expression[] operands;

    public FunctionOperation(String name, Expression ... operands) {
        super(name);
        this.operands = operands;
    }

    @Override
    public int getOperandCount() {
        return operands.length;
    }
    @Override
    public final Expression[] getChildren() {
        return operands.clone();
    }

    @Override
    public int getPriority() {
        return 4;
    }

    @Override
    public String getDescription() {
        return getName() + "()";
    }

    public enum FilterFunctions {
        IS_NULL("isNull"),
        IS_NOT_NULL("isNotNull"),
        CONTAINS(DefaultFunctions.CONTAINS.getName()),
        BEGINS_WITH(DefaultFunctions.STARTS_WITH.getName()),
        END_WITH(DefaultFunctions.ENDS_WITH.getName()),
        IN("in"),
        ON("on"),
        ON_OR_AFTER("onAfter"),
        ON_OR_BEFORE("onBefore"),
        LAST_HOUR("lastHour"),
        THIS_HOUR("thisHour"),
        NEXT_HOUR("nextHour"),
        YESTERDAY("yesterday"),
        TODAY("today"),
        TOMORROW("tomorrow"),
        LAST_WEEK("lastWeek"),
        THIS_WEEK("thisWeek"),
        NEXT_WEEK("nextWeek"),
        LAST_MONTH("lastMonth"),
        THIS_MONTH("thisMonth"),
        NEXT_MONTH("nextMonth"),
        LAST_YEAR("lastYear"),
        THIS_YEAR("thisYear"),
        NEXT_YEAR("nextYear"),
        LAST_X_HOURS("lastHours"),
        NEXT_X_HOURS("nextHours"),
        LAST_X_DAYS("lastDays"),
        NEXT_X_DAYS("nextDays"),
        LAST_X_WEEKS("lastWeeks"),
        NEXT_X_WEEKS("nextWeeks"),
        LAST_X_MONTHS("lastMonths"),
        NEXT_X_MONTHS("nextMonths"),
        LAST_X_YEARS("lastYears"),
        NEXT_X_YEARS("nextYears"),
        COLOR("color"),
        SIMPLE_CONTAINS("simpleContains")
        ;

        FilterFunctions(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        private String name;
    }
}
