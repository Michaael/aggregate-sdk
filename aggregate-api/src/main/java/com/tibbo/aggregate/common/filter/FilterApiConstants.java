package com.tibbo.aggregate.common.filter;

import java.time.Instant;
import java.util.Date;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.datatable.DataTable;
import com.tibbo.aggregate.common.datatable.FieldFormat;
import com.tibbo.aggregate.common.datatable.TableFormat;
import com.tibbo.aggregate.common.datatable.field.DataTableFieldFormat;
import com.tibbo.aggregate.common.datatable.field.LongFieldFormat;

public class FilterApiConstants {

    public static final String EXPERIMENTAL_LAZY_SELECTION_VALUES = "__EXPERIMENTAL__LAZY_SELECTION_VALUES";
    public static final String EXPERIMENTAL_LAZY_MULTI_SELECTION = "__EXPERIMENTAL__LAZY_MULTI_SELECTION";
    
    public static final String VALUE = "value";
    public static final String F_SET_SMART_FILTER_EXPRESSION = "setSmartFilterExpression";
    public static final String FIF_SMART_FILTER_EXPRESSION_TEXT = "text";
    public static final String FIF_DRY = "dry";
    public static final String FOF_SMART_FILTER_FUNC_RESULT_CODE = "code";
    public static final String FOF_SMART_FILTER_FUNC_RESULT_MESSAGE = "message";
    public static final TableFormat FIFT_SET_SMART_FILTER_EXPRESSION = new TableFormat(1, 1);
    static {
        FieldFormat<?> ff = FieldFormat.create(FIF_SMART_FILTER_EXPRESSION_TEXT, FieldFormat.STRING_FIELD, Cres.get().getString("setSmartFilterExpressionParamText"));
        FIFT_SET_SMART_FILTER_EXPRESSION.addField(ff);

        ff = FieldFormat.create(FIF_DRY, FieldFormat.BOOLEAN_FIELD, Cres.get().getString("setSmartFilterExpressionParamDry"));
        FIFT_SET_SMART_FILTER_EXPRESSION.addField(ff);
    }

    public static final TableFormat FOFT_SMART_FILTER_FUNC_RESULT = new TableFormat(1, 1);
    static {
        FieldFormat<?> ff = FieldFormat.create(FOF_SMART_FILTER_FUNC_RESULT_CODE, FieldFormat.INTEGER_FIELD, Cres.get().getString("setSmartFilterExpressionResultCode"));
        FOFT_SMART_FILTER_FUNC_RESULT.addField(ff);

        ff = FieldFormat.create(FOF_SMART_FILTER_FUNC_RESULT_MESSAGE, FieldFormat.STRING_FIELD, Cres.get().getString("setSmartFilterExpressionResultMessage"));
        FOFT_SMART_FILTER_FUNC_RESULT.addField(ff);
    }

    public static final TableFormat FOFT_SET_SMART_FILTER_EXPRESSION = FOFT_SMART_FILTER_FUNC_RESULT.clone();

    // function getSmartFilterExpression()
    public static final String F_GET_SMART_FILTER_EXPRESSION = "getSmartFilterExpression";
    public static final String FOF_GET_SMART_FILTER_EXPRESSION_TEXT = "text";
    public static final TableFormat FIFT_GET_SMART_FILTER_EXPRESSION = new TableFormat();
    public static final TableFormat FOFT_GET_SMART_FILTER_EXPRESSION = new TableFormat(1, 1);
    static {
        FieldFormat<?> ff = FieldFormat.create(FOF_GET_SMART_FILTER_EXPRESSION_TEXT, FieldFormat.STRING_FIELD);
        FOFT_GET_SMART_FILTER_EXPRESSION.addField(ff);
    }

    // Allowed filter modes
    public static final String SMART_FILTER_TEXT_MODE         = "SMART_FILTER_TEXT_MODE";
    public static final String SMART_FILTER_COMPONENT_MODE    = "SMART_FILTER_COMPONENT_MODE";

    // setSmartFilterDisplayMode() function definitions
    public static final String F_SET_SMART_FILTER_DISPLAY_MODE = "setSmartFilterMode";
    public static final String FIF_SET_SMART_FILTER_DISPLAY_MODE = "mode";

    public static final TableFormat FIFT_SET_SMART_FILTER_DISPLAY_MODE = new TableFormat(1, 1);
    static {
        FieldFormat<?> ff = FieldFormat.create(FIF_SET_SMART_FILTER_DISPLAY_MODE, FieldFormat.STRING_FIELD, Cres.get().getString("setSmartFilterDisplayModeParamMode"));
        FIFT_SET_SMART_FILTER_DISPLAY_MODE.addField(ff);

        ff = FieldFormat.create(FIF_DRY, FieldFormat.BOOLEAN_FIELD, Cres.get().getString("setSmartFilterDisplayModeParamDry"));
        FIFT_SET_SMART_FILTER_DISPLAY_MODE.addField(ff);
    }

    public static final TableFormat FOFT_SET_SMART_FILTER_DISPLAY_MODE = FOFT_SMART_FILTER_FUNC_RESULT.clone();

    // getSmartFilterDisplayMode() function definitions
    public static final String F_GET_SMART_FILTER_DISPLAY_MODE = "getSmartFilterMode";
    public static final String FOF_GET_SMART_FILTER_DISPLAY_MODE = "mode";
    public static final TableFormat FIFT_GET_SMART_FILTER_DISPLAY_MODE = new TableFormat();
    public static final TableFormat FOFT_GET_SMART_FILTER_DISPLAY_MODE = new TableFormat(1, 1);
    static {
        FieldFormat<?> ff = FieldFormat.create(FOF_GET_SMART_FILTER_DISPLAY_MODE, FieldFormat.STRING_FIELD);
        FOFT_GET_SMART_FILTER_DISPLAY_MODE.addField(ff);
    }

    // Smart filter control identifiers
// Pane types
    public static final int CONTROL_PANE_UNDEFINED_VALUE       = 0;
    public static final int CONTROL_PANE_NUMERIC_VALUE         = 1;
    public static final int CONTROL_PANE_STRING_VALUE          = 2;
    public static final int CONTROL_PANE_BOOLEAN_VALUE         = 3;
    public static final int CONTROL_PANE_COLOR_VALUE           = 4;
    public static final int CONTROL_PANE_DATE_VALUE            = 5;
    public static final int CONTROL_PANE_DATA_TABLE_VALUE      = 6;
    public static final int CONTROL_PANE_DATA_BLOCK_VALUE      = 7;

// Pane field names
    public static final String PANE_FIELD_VALUE_PRESENCE          = "PANE_FIELD_VALUE_PRESENCE";
    public static final String PANE_FIELD_EQUALS                  = "PANE_FIELD_EQUALS";
    public static final String PANE_FIELD_INCLUDED_IN             = "PANE_FIELD_INCLUDED_IN";
    public static final String PANE_FIELD_DOES_NOT_EQUAL          = "PANE_FIELD_DOES_NOT_EQUAL";
    public static final String PANE_FIELD_GREATER_THAN            = "PANE_FIELD_GREATER_THAN";
    public static final String PANE_FIELD_GREATER_OR_EQUAL_THAN   = "PANE_FIELD_GREATER_OR_EQUAL_THAN";
    public static final String PANE_FIELD_LESS_THAN               = "PANE_FIELD_LESS_THAN";
    public static final String PANE_FIELD_LESS_OR_EQUAL_THAN      = "PANE_FIELD_LESS_OR_EQUAL_THAN";
    public static final String PANE_FIELD_CONTAINS                = "PANE_FIELD_CONTAINS";
    public static final String PANE_FIELD_DOES_NOT_CONTAIN        = "PANE_FIELD_DOES_NOT_CONTAIN";
    public static final String PANE_FIELD_BEGINS_WITH             = "PANE_FIELD_BEGINS_WITH";
    public static final String PANE_FIELD_DOES_NOT_BEGIN_WITH     = "PANE_FIELD_DOES_NOT_BEGIN_WITH";
    public static final String PANE_FIELD_ENDS_WITH               = "PANE_FIELD_ENDS_WITH";
    public static final String PANE_FIELD_DOES_NOT_END_WITH       = "PANE_FIELD_DOES_NOT_END_WITH";
    public static final String PANE_FIELD_LAST_INTERVAL           = "PANE_FIELD_LAST_INTERVAL";
    public static final String PANE_FIELD_CURRENT_INTERVAL        = "PANE_FIELD_CURRENT_INTERVAL";
    public static final String PANE_FIELD_NEXT_INTERVAL           = "PANE_FIELD_NEXT_INTERVAL";
    public static final String PANE_FIELD_RECORD_COUNT            = "PANE_FIELD_RECORD_COUNT";
    public static final String PANE_FIELD_FILE_TYPE               = "PANE_FIELD_FILE_TYPE";
    public static final String PANE_FIELD_DATE_TIME_RANGE         = "PANE_FIELD_DATE_TIME_RANGE";
    public static final String PANE_FIELD_EDITOR                  = "PANE_FIELD_EDITOR";

    // Time intervals choices
    public static final int TIME_INTERVAL_NOT_SET              = 0;
    public static final int TIME_INTERVAL_HOUR                 = 1;
    public static final int TIME_INTERVAL_DAY                  = 2;
    public static final int TIME_INTERVAL_WEEK                 = 3;
    public static final int TIME_INTERVAL_MONTH                = 4;
    public static final int TIME_INTERVAL_YEAR                 = 5;

// Value presence choices
    public static final int VALUE_PRESENCE_ALL                 = 1;
    public static final int VALUE_PRESENCE_SET                 = 2;
    public static final int VALUE_PRESENCE_NOT_SET             = 3;

// Boolean equals choices
    public static final int BOOLEAN_ALL                        = 1;
    public static final int BOOLEAN_FALSE                      = 2;
    public static final int BOOLEAN_TRUE                       = 3;

///////////////////////////////////////////////////////////
// Pane table formats
///////////////////////////////////////////////////////////

// Auxiliary date/time table formats
    public static final String TIME_INTERVAL_AMOUNT               = "TIME_INTERVAL_AMOUNT";
    public static final String TIME_INTERVAL_TYPE                 = "TIME_INTERVAL_TYPE";

    public static final TableFormat TF_TIME_INTERVAL              = new TableFormat(1, 1);
    static {
        FieldFormat<?> ff = FieldFormat.create(TIME_INTERVAL_AMOUNT, FieldFormat.INTEGER_FIELD, Cres.get().getString("smartFilterTimeIntervalAmount"));
        ff.setNullable(true);
        ff.setDefault(null);
        TF_TIME_INTERVAL.addField(ff);

        FieldFormat<Integer> fi = FieldFormat.create(TIME_INTERVAL_TYPE, FieldFormat.INTEGER_FIELD, Cres.get().getString("smartFilterTimeIntervalType"));
        fi.addSelectionValue(TIME_INTERVAL_NOT_SET, Cres.get().getString("smartFilterTimeIntervalTypeNotSet"));
        fi.addSelectionValue(TIME_INTERVAL_HOUR, Cres.get().getString("smartFilterTimeIntervalTypeHour"));
        fi.addSelectionValue(TIME_INTERVAL_DAY, Cres.get().getString("smartFilterTimeIntervalTypeDay"));
        fi.addSelectionValue(TIME_INTERVAL_WEEK, Cres.get().getString("smartFilterTimeIntervalTypeWeek"));
        fi.addSelectionValue(TIME_INTERVAL_MONTH, Cres.get().getString("smartFilterTimeIntervalTypeMonth"));
        fi.addSelectionValue(TIME_INTERVAL_YEAR, Cres.get().getString("smartFilterTimeIntervalTypeYear"));
        fi.setDefault(TIME_INTERVAL_NOT_SET);
        TF_TIME_INTERVAL.addField(fi);
    }

    public static final String TIME_RANGE_START_DATE = "startDate";
    public static final String TIME_RANGE_END_DATE   = "endDate";

    public static final TableFormat DATE_RANGE_VALUE     = new TableFormat(1, 1);

    static {
        FieldFormat<Date> ff = FieldFormat.create(TIME_RANGE_START_DATE, FieldFormat.DATE_FIELD, Cres.get().getString("smartFilterTimeRangeStartDate"));
        ff.setDefault(Date.from(Instant.now()));
        DATE_RANGE_VALUE.addField(ff);

        ff = FieldFormat.create(TIME_RANGE_END_DATE, FieldFormat.DATE_FIELD, Cres.get().getString("smartFilterTimeRangeEndDate"));
        ff.setDefault(Date.from(Instant.now()));

        DATE_RANGE_VALUE.addField(ff);
    }


    // Numeric value
    public static final TableFormat TF_CONTROL_PANE_NUMERIC_VALUE      = new TableFormat(1, 1);
    static {
        FieldFormat<Integer> fi = FieldFormat.create(PANE_FIELD_VALUE_PRESENCE, FieldFormat.INTEGER_FIELD, Cres.get().getString("smartFilterPaneFieldValuePresence"));
        fi.addSelectionValue(VALUE_PRESENCE_ALL, Cres.get().getString("smartFilterValuePresenceAll"));
        fi.addSelectionValue(VALUE_PRESENCE_SET, Cres.get().getString("smartFilterValuePresenceSet"));
        fi.addSelectionValue(VALUE_PRESENCE_NOT_SET, Cres.get().getString("smartFilterValuePresenceNotSet"));
        fi.setDefault(VALUE_PRESENCE_ALL);
        TF_CONTROL_PANE_NUMERIC_VALUE.addField(fi);

        FieldFormat<?> ff = FieldFormat.create(PANE_FIELD_EQUALS, FieldFormat.DATATABLE_FIELD, Cres.get().getString("smartFilterPaneFieldEquals"));
        ff.setNullable(true);
        ff.setDefault(null);
        TF_CONTROL_PANE_NUMERIC_VALUE.addField(ff);

        ff = FieldFormat.create(PANE_FIELD_INCLUDED_IN, FieldFormat.DATATABLE_FIELD, Cres.get().getString("smartFilterPaneFieldIncludedIn"));
        ff.setNullable(true);
        ff.setDefault(null);
        TF_CONTROL_PANE_NUMERIC_VALUE.addField(ff);

        ff = FieldFormat.create(PANE_FIELD_DOES_NOT_EQUAL, FieldFormat.DATATABLE_FIELD, Cres.get().getString("smartFilterPaneFieldNotEqual"));
        ff.setNullable(true);
        ff.setDefault(null);
        TF_CONTROL_PANE_NUMERIC_VALUE.addField(ff);

        ff = FieldFormat.create(PANE_FIELD_GREATER_THAN, FieldFormat.DATATABLE_FIELD, Cres.get().getString("smartFilterPaneFieldGreaterThan"));
        ff.setNullable(true);
        ff.setDefault(null);
        TF_CONTROL_PANE_NUMERIC_VALUE.addField(ff);

        ff = FieldFormat.create(PANE_FIELD_GREATER_OR_EQUAL_THAN, FieldFormat.DATATABLE_FIELD, Cres.get().getString("smartFilterPaneFieldGreaterOrEqualThan"));
        ff.setNullable(true);
        ff.setDefault(null);
        TF_CONTROL_PANE_NUMERIC_VALUE.addField(ff);

        ff = FieldFormat.create(PANE_FIELD_LESS_THAN, FieldFormat.DATATABLE_FIELD, Cres.get().getString("smartFilterPaneFieldLessThan"));
        ff.setNullable(true);
        ff.setDefault(null);
        TF_CONTROL_PANE_NUMERIC_VALUE.addField(ff);

        ff = FieldFormat.create(PANE_FIELD_LESS_OR_EQUAL_THAN, FieldFormat.DATATABLE_FIELD, Cres.get().getString("smartFilterPaneFieldLessOrEqualThan"));
        ff.setNullable(true);
        ff.setDefault(null);
        TF_CONTROL_PANE_NUMERIC_VALUE.addField(ff);
    }

    // String value
    public static final TableFormat TF_CONTROL_PANE_STRING_VALUE        = new TableFormat(1, 1);
    static {
        FieldFormat<Integer> fi = FieldFormat.create(PANE_FIELD_VALUE_PRESENCE, FieldFormat.INTEGER_FIELD, Cres.get().getString("smartFilterPaneFieldValuePresence"));
        fi.addSelectionValue(VALUE_PRESENCE_ALL, Cres.get().getString("smartFilterValuePresenceAll"));
        fi.addSelectionValue(VALUE_PRESENCE_SET, Cres.get().getString("smartFilterValuePresenceSet"));
        fi.addSelectionValue(VALUE_PRESENCE_NOT_SET, Cres.get().getString("smartFilterValuePresenceNotSet"));
        fi.setDefault(VALUE_PRESENCE_ALL);
        TF_CONTROL_PANE_STRING_VALUE.addField(fi);

        FieldFormat<?> ff = FieldFormat.create(PANE_FIELD_EQUALS, FieldFormat.STRING_FIELD, Cres.get().getString("smartFilterPaneFieldEquals"));
        ff.setNullable(true);
        ff.setDefault(null);
        ff.setExtendableSelectionValues(true);
        ff.setEditorOptions(EXPERIMENTAL_LAZY_SELECTION_VALUES);
        TF_CONTROL_PANE_STRING_VALUE.addField(ff);

        ff = FieldFormat.create(PANE_FIELD_INCLUDED_IN, FieldFormat.DATATABLE_FIELD, Cres.get().getString("smartFilterPaneFieldIncludedIn"));
        ff.setNullable(true);
        ff.setDefault(null);
        ff.setEditorOptions(EXPERIMENTAL_LAZY_MULTI_SELECTION);
        TF_CONTROL_PANE_STRING_VALUE.addField(ff);

        ff = FieldFormat.create(PANE_FIELD_DOES_NOT_EQUAL, FieldFormat.STRING_FIELD, Cres.get().getString("smartFilterPaneFieldNotEqual"));
        ff.setNullable(true);
        ff.setExtendableSelectionValues(true);
        ff.setEditorOptions(EXPERIMENTAL_LAZY_SELECTION_VALUES);
        ff.setDefault(null);
        TF_CONTROL_PANE_STRING_VALUE.addField(ff);

        ff = FieldFormat.create(PANE_FIELD_CONTAINS, FieldFormat.STRING_FIELD, Cres.get().getString("smartFilterPaneFieldContains"));
        ff.setNullable(true);
        ff.setDefault(null);
        TF_CONTROL_PANE_STRING_VALUE.addField(ff);

        ff = FieldFormat.create(PANE_FIELD_DOES_NOT_CONTAIN, FieldFormat.STRING_FIELD, Cres.get().getString("smartFilterPaneFieldNotContain"));
        ff.setNullable(true);
        ff.setDefault(null);
        TF_CONTROL_PANE_STRING_VALUE.addField(ff);

        ff = FieldFormat.create(PANE_FIELD_BEGINS_WITH, FieldFormat.STRING_FIELD, Cres.get().getString("smartFilterPaneFieldBeginsWith"));
        ff.setNullable(true);
        ff.setDefault(null);
        TF_CONTROL_PANE_STRING_VALUE.addField(ff);

        ff = FieldFormat.create(PANE_FIELD_DOES_NOT_BEGIN_WITH, FieldFormat.STRING_FIELD, Cres.get().getString("smartFilterPaneFieldNotBeginWith"));
        ff.setNullable(true);
        ff.setDefault(null);
        TF_CONTROL_PANE_STRING_VALUE.addField(ff);

        ff = FieldFormat.create(PANE_FIELD_ENDS_WITH, FieldFormat.STRING_FIELD, Cres.get().getString("smartFilterPaneFieldEndsWith"));
        ff.setNullable(true);
        ff.setDefault(null);
        TF_CONTROL_PANE_STRING_VALUE.addField(ff);

        ff = FieldFormat.create(PANE_FIELD_DOES_NOT_END_WITH, FieldFormat.STRING_FIELD, Cres.get().getString("smartFilterPaneFieldNotEndWith"));
        ff.setNullable(true);
        ff.setDefault(null);
        TF_CONTROL_PANE_STRING_VALUE.addField(ff);
    }

    // Boolean value
    public static final TableFormat TF_CONTROL_PANE_BOOLEAN_VALUE       = new TableFormat(1, 1);
    static {
        FieldFormat<Integer> fi = FieldFormat.create(PANE_FIELD_VALUE_PRESENCE, FieldFormat.INTEGER_FIELD, Cres.get().getString("smartFilterPaneFieldValuePresence"));
        fi.addSelectionValue(VALUE_PRESENCE_ALL, Cres.get().getString("smartFilterValuePresenceAll"));
        fi.addSelectionValue(VALUE_PRESENCE_SET, Cres.get().getString("smartFilterValuePresenceSet"));
        fi.addSelectionValue(VALUE_PRESENCE_NOT_SET, Cres.get().getString("smartFilterValuePresenceNotSet"));
        fi.setDefault(VALUE_PRESENCE_ALL);
        TF_CONTROL_PANE_BOOLEAN_VALUE.addField(fi);

        fi = FieldFormat.create(PANE_FIELD_EQUALS, FieldFormat.INTEGER_FIELD, Cres.get().getString("smartFilterPaneFieldEquals"));
        fi.addSelectionValue(BOOLEAN_ALL, Cres.get().getString("smartFilterPaneFieldEqualsBooleanAll"));
        fi.addSelectionValue(BOOLEAN_FALSE, Cres.get().getString("smartFilterPaneFieldEqualsBooleanFalse"));
        fi.addSelectionValue(BOOLEAN_TRUE, Cres.get().getString("smartFilterPaneFieldEqualsBooleanTrue"));
        fi.setDefault(BOOLEAN_ALL);
        TF_CONTROL_PANE_BOOLEAN_VALUE.addField(fi);
    }

    // Color value
    public static final TableFormat TF_CONTROL_PANE_COLOR_VALUE        = new TableFormat(1, 1);
    static {
        FieldFormat<Integer> fi = FieldFormat.create(PANE_FIELD_VALUE_PRESENCE, FieldFormat.INTEGER_FIELD, Cres.get().getString("smartFilterPaneFieldValuePresence"));
        fi.addSelectionValue(VALUE_PRESENCE_ALL, Cres.get().getString("smartFilterValuePresenceAll"));
        fi.addSelectionValue(VALUE_PRESENCE_SET, Cres.get().getString("smartFilterValuePresenceSet"));
        fi.addSelectionValue(VALUE_PRESENCE_NOT_SET, Cres.get().getString("smartFilterValuePresenceNotSet"));
        fi.setDefault(VALUE_PRESENCE_ALL);
        TF_CONTROL_PANE_COLOR_VALUE.addField(fi);

        FieldFormat<?> ff = FieldFormat.create(PANE_FIELD_EQUALS, FieldFormat.COLOR_FIELD, Cres.get().getString("smartFilterPaneFieldEquals"));
        ff.setNullable(true);
        ff.setDefault(null);
        TF_CONTROL_PANE_COLOR_VALUE.addField(ff);

        ff = FieldFormat.create(PANE_FIELD_DOES_NOT_EQUAL, FieldFormat.COLOR_FIELD, Cres.get().getString("smartFilterPaneFieldNotEqual"));
        ff.setNullable(true);
        ff.setDefault(null);
        TF_CONTROL_PANE_COLOR_VALUE.addField(ff);
    }

    // Date value

    public static final TableFormat TF_CONTROL_PANE_DATE_VALUE      = new TableFormat(1, 1);
    static {
        FieldFormat<Integer> fi = FieldFormat.create(PANE_FIELD_VALUE_PRESENCE, FieldFormat.INTEGER_FIELD, Cres.get().getString("smartFilterPaneFieldValuePresence"));
        fi.addSelectionValue(VALUE_PRESENCE_ALL, Cres.get().getString("smartFilterValuePresenceAll"));
        fi.addSelectionValue(VALUE_PRESENCE_SET, Cres.get().getString("smartFilterValuePresenceSet"));
        fi.addSelectionValue(VALUE_PRESENCE_NOT_SET, Cres.get().getString("smartFilterValuePresenceNotSet"));
        fi.setDefault(VALUE_PRESENCE_ALL);
        TF_CONTROL_PANE_DATE_VALUE.addField(fi);

        FieldFormat<?> ff = FieldFormat.create(PANE_FIELD_EQUALS, FieldFormat.DATE_FIELD, Cres.get().getString("smartFilterPaneFieldEquals"));
        ff.setNullable(true);
        ff.setDefault(null);
        TF_CONTROL_PANE_DATE_VALUE.addField(ff);

        ff = FieldFormat.create(PANE_FIELD_INCLUDED_IN, FieldFormat.DATATABLE_FIELD, Cres.get().getString("smartFilterPaneFieldIncludedIn"));
        ff.setNullable(true);
        ff.setDefault(null);
        TF_CONTROL_PANE_DATE_VALUE.addField(ff);

        FieldFormat<DataTable> range = FieldFormat.create(PANE_FIELD_DATE_TIME_RANGE, FieldFormat.DATATABLE_FIELD, Cres.get().getString("smartFilterPaneFieldDateTimeRange"));
        range.setNullable(true);
        range.setDefault(null);
        range.setEditor(DataTableFieldFormat.EDITOR_DATE_RANGE);
        TF_CONTROL_PANE_DATE_VALUE.addField(range);

        ff = FieldFormat.create(PANE_FIELD_DOES_NOT_EQUAL, FieldFormat.DATE_FIELD, Cres.get().getString("smartFilterPaneFieldNotEqual"));
        ff.setNullable(true);
        ff.setDefault(null);
        TF_CONTROL_PANE_DATE_VALUE.addField(ff);

        ff = FieldFormat.create(PANE_FIELD_GREATER_THAN, FieldFormat.DATE_FIELD, Cres.get().getString("smartFilterPaneFieldGreaterThan"));
        ff.setNullable(true);
        ff.setDefault(null);
        TF_CONTROL_PANE_DATE_VALUE.addField(ff);

        ff = FieldFormat.create(PANE_FIELD_GREATER_OR_EQUAL_THAN, FieldFormat.DATE_FIELD, Cres.get().getString("smartFilterPaneFieldGreaterOrEqualThan"));
        ff.setNullable(true);
        ff.setDefault(null);
        TF_CONTROL_PANE_DATE_VALUE.addField(ff);

        ff = FieldFormat.create(PANE_FIELD_LESS_THAN, FieldFormat.DATE_FIELD, Cres.get().getString("smartFilterPaneFieldLessThan"));
        ff.setNullable(true);
        ff.setDefault(null);
        TF_CONTROL_PANE_DATE_VALUE.addField(ff);

        ff = FieldFormat.create(PANE_FIELD_LESS_OR_EQUAL_THAN, FieldFormat.DATE_FIELD, Cres.get().getString("smartFilterPaneFieldLessOrEqualThan"));
        ff.setNullable(true);
        ff.setDefault(null);
        TF_CONTROL_PANE_DATE_VALUE.addField(ff);

        ff = FieldFormat.create(PANE_FIELD_LAST_INTERVAL, FieldFormat.LONG_FIELD, Cres.get().getString("smartFilterPaneFieldLastInterval"));
        ff.setNullable(true);
        ff.setDefault(null);
        ff.setEditor(LongFieldFormat.EDITOR_PERIOD);
        ff.setEditorOptions("3 7");
        TF_CONTROL_PANE_DATE_VALUE.addField(ff);

        fi = FieldFormat.create(PANE_FIELD_CURRENT_INTERVAL, FieldFormat.INTEGER_FIELD, Cres.get().getString("smartFilterPaneFieldCurrentInterval"));
        fi.addSelectionValue(TIME_INTERVAL_NOT_SET, Cres.get().getString("smartFilterTimeIntervalTypeNotSet"));
        fi.addSelectionValue(TIME_INTERVAL_HOUR, Cres.get().getString("smartFilterTimeIntervalTypeHour"));
        fi.addSelectionValue(TIME_INTERVAL_DAY, Cres.get().getString("smartFilterTimeIntervalTypeDay"));
        fi.addSelectionValue(TIME_INTERVAL_WEEK, Cres.get().getString("smartFilterTimeIntervalTypeWeek"));
        fi.addSelectionValue(TIME_INTERVAL_MONTH, Cres.get().getString("smartFilterTimeIntervalTypeMonth"));
        fi.addSelectionValue(TIME_INTERVAL_YEAR, Cres.get().getString("smartFilterTimeIntervalTypeYear"));
        fi.setDefault(TIME_INTERVAL_NOT_SET);
        TF_CONTROL_PANE_DATE_VALUE.addField(fi);

        ff = FieldFormat.create(PANE_FIELD_NEXT_INTERVAL, FieldFormat.LONG_FIELD, Cres.get().getString("smartFilterPaneFieldNextInterval"));
        ff.setNullable(true);
        ff.setDefault(null);
        ff.setEditor(LongFieldFormat.EDITOR_PERIOD);
        ff.setEditorOptions("3 7");
        TF_CONTROL_PANE_DATE_VALUE.addField(ff);

        ff = FieldFormat.create(PANE_FIELD_EDITOR, FieldFormat.STRING_FIELD, Cres.get().getString("dtEditorViewer"));
        ff.setNullable(true);
        ff.setDefault(null);
        ff.setHidden(true);
        TF_CONTROL_PANE_DATE_VALUE.addField(ff);
    }

    // DataTable value
    public static final TableFormat TF_CONTROL_PANE_DATATABLE_VALUE     = new TableFormat(1, 1);
    static {
        FieldFormat<Integer> fi = FieldFormat.create(PANE_FIELD_VALUE_PRESENCE, FieldFormat.INTEGER_FIELD, Cres.get().getString("smartFilterPaneFieldValuePresence"));
        fi.addSelectionValue(VALUE_PRESENCE_ALL, Cres.get().getString("smartFilterValuePresenceAll"));
        fi.addSelectionValue(VALUE_PRESENCE_SET, Cres.get().getString("smartFilterValuePresenceSet"));
        fi.addSelectionValue(VALUE_PRESENCE_NOT_SET, Cres.get().getString("smartFilterValuePresenceNotSet"));
        fi.setDefault(VALUE_PRESENCE_ALL);
        TF_CONTROL_PANE_DATATABLE_VALUE.addField(fi);

        FieldFormat<?> ff = FieldFormat.create(PANE_FIELD_CONTAINS, FieldFormat.STRING_FIELD, Cres.get().getString("smartFilterPaneFieldContains"));
        ff.setNullable(true);
        ff.setDefault(null);
        TF_CONTROL_PANE_DATATABLE_VALUE.addField(ff);

        ff = FieldFormat.create(PANE_FIELD_DOES_NOT_CONTAIN, FieldFormat.STRING_FIELD, Cres.get().getString("smartFilterPaneFieldNotContain"));
        ff.setNullable(true);
        ff.setDefault(null);
        TF_CONTROL_PANE_DATATABLE_VALUE.addField(ff);


        ff = FieldFormat.create(PANE_FIELD_RECORD_COUNT, FieldFormat.INTEGER_FIELD, Cres.get().getString("smartFilterPaneFieldRecordCount"));
        ff.setNullable(true);
        ff.setDefault(null);
        TF_CONTROL_PANE_DATATABLE_VALUE.addField(ff);
    }

    // Data block value
    public static final TableFormat TF_CONTROL_PANE_DATABLOCK_VALUE   = new TableFormat(1, 1);
    static {
        FieldFormat<Integer> fi = FieldFormat.create(PANE_FIELD_VALUE_PRESENCE, FieldFormat.INTEGER_FIELD, Cres.get().getString("smartFilterPaneFieldValuePresence"));
        fi.addSelectionValue(VALUE_PRESENCE_ALL, Cres.get().getString("smartFilterValuePresenceAll"));
        fi.addSelectionValue(VALUE_PRESENCE_SET, Cres.get().getString("smartFilterValuePresenceSet"));
        fi.addSelectionValue(VALUE_PRESENCE_NOT_SET, Cres.get().getString("smartFilterValuePresenceNotSet"));
        fi.setDefault(VALUE_PRESENCE_ALL);
        TF_CONTROL_PANE_DATABLOCK_VALUE.addField(fi);

        FieldFormat<?> ff = FieldFormat.create(PANE_FIELD_FILE_TYPE, FieldFormat.DATA_FIELD, Cres.get().getString("smartFilterPaneFieldFileType"));
        ff.setNullable(true);
        ff.setDefault(null);
        TF_CONTROL_PANE_DATABLOCK_VALUE.addField(ff);
    }

    ///////////////////////////////////////////////////////////
    // Component smart filter table format
    ///////////////////////////////////////////////////////////
    public static final String COMPONENT_SMART_FILTER_COLUMN      = "COMPONENT_SMART_FILTER_COLUMN";
    public static final String COMPONENT_SMART_FILTER_TYPE        = "COMPONENT_SMART_FILTER_TYPE";
    public static final String COMPONENT_SMART_FILTER_STATE       = "COMPONENT_SMART_FILTER_STATE";

    public static final TableFormat TF_COMPONENT_SMART_FILTER       = new TableFormat();
    static {
        FieldFormat<?> ff = FieldFormat.create(COMPONENT_SMART_FILTER_COLUMN, FieldFormat.STRING_FIELD, Cres.get().getString("smartFilterColumn"));
        TF_COMPONENT_SMART_FILTER.addField(ff);

        FieldFormat<Integer> fi = FieldFormat.create(COMPONENT_SMART_FILTER_TYPE, FieldFormat.INTEGER_FIELD, Cres.get().getString("smartFilterType"));
        fi.addSelectionValue(CONTROL_PANE_UNDEFINED_VALUE, Cres.get().getString("smartFilterPaneUndefined"));
        fi.addSelectionValue(CONTROL_PANE_NUMERIC_VALUE, Cres.get().getString("smartFilterPaneNumeric"));
        fi.addSelectionValue(CONTROL_PANE_STRING_VALUE, Cres.get().getString("smartFilterPaneString"));
        fi.addSelectionValue(CONTROL_PANE_BOOLEAN_VALUE, Cres.get().getString("smartFilterPaneBoolean"));
        fi.addSelectionValue(CONTROL_PANE_COLOR_VALUE, Cres.get().getString("smartFilterPaneColor"));
        fi.addSelectionValue(CONTROL_PANE_DATE_VALUE, Cres.get().getString("smartFilterPaneDate"));
        fi.addSelectionValue(CONTROL_PANE_DATA_TABLE_VALUE, Cres.get().getString("smartFilterPaneTable"));
        fi.addSelectionValue(CONTROL_PANE_DATA_BLOCK_VALUE, Cres.get().getString("smartFilterPaneDataBlock"));
        fi.setDefault(CONTROL_PANE_UNDEFINED_VALUE);
        TF_COMPONENT_SMART_FILTER.addField(fi);

        ff = FieldFormat.create(COMPONENT_SMART_FILTER_STATE, FieldFormat.DATATABLE_FIELD, Cres.get().getString("smartFilterState"));
        TF_COMPONENT_SMART_FILTER.addField(ff);
    }

    // setSmartFilterControlStates()
    public static final String F_SET_SMART_FILTER_CONTROL_STATES  = "setSmartFilterControlStates";
    public static final String FIF_SMART_FILTER_CONTROL_STATES    = "controlStates";

    public static final TableFormat FIFT_SET_SMART_FILTER_CONTROL_STATES    = new TableFormat(1, 1);
    static {
        FieldFormat<?> ff = FieldFormat.create(FIF_SMART_FILTER_CONTROL_STATES, FieldFormat.DATATABLE_FIELD, Cres.get().getString("smartFilterControlStates"));
        FIFT_SET_SMART_FILTER_CONTROL_STATES.addField(ff);

        ff = FieldFormat.create(FIF_DRY, FieldFormat.BOOLEAN_FIELD, Cres.get().getString("smartFilterControlDry"));
        FIFT_SET_SMART_FILTER_CONTROL_STATES.addField(ff);
    }

    public static final TableFormat FOFT_SET_SMART_FILTER_CONTROL_STATES = FOFT_SMART_FILTER_FUNC_RESULT.clone();

    // getSmartFilterControlStates()
    public static final String F_GET_SMART_FILTER_CONTROL_STATES  = "getSmartFilterControlStates";
    public static final String FOF_SMART_FILTER_CONTROL_STATES    = "controlStates";
    public static final TableFormat FIFT_GET_SMART_FILTER_CONTROL_STATES = new TableFormat();
    public static final TableFormat FOFT_GET_SMART_FILTER_CONTROL_STATES = new TableFormat(1, 1);

    static {
        FieldFormat<?> ff = FieldFormat.create(FOF_SMART_FILTER_CONTROL_STATES, FieldFormat.DATATABLE_FIELD, Cres.get().getString("smartFilterControlStates"));
        FOFT_GET_SMART_FILTER_CONTROL_STATES.addField(ff);

        ff = FieldFormat.create(FIF_DRY, FieldFormat.BOOLEAN_FIELD, Cres.get().getString("smartFilterControlDry"));
        FOFT_GET_SMART_FILTER_CONTROL_STATES.addField(ff);

    }

    ///////////////////////////////////////////////////////////
    // Set filter result codes
    ///////////////////////////////////////////////////////////
    public static final int RES_OK                             = 0;
    public static final int RES_PARSE_ERROR                    = 1;
    public static final int RES_CONVERT_ERROR                  = 2;
    public static final int RES_UNEXPECTED_ERROR               = 3;
}


