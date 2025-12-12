package com.tibbo.aggregate.common.filter;

import com.tibbo.aggregate.common.datatable.DataRecord;
import com.tibbo.aggregate.common.datatable.DataTable;
import com.tibbo.aggregate.common.datatable.SimpleDataTable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static com.tibbo.aggregate.common.filter.FilterApiConstants.*;

public class ControlStatesFilterReducer {

    private static final Map<Integer, Function<DataRecord, Boolean>> emptyPanePredicates = new HashMap<>();

    static {
        emptyPanePredicates.put(CONTROL_PANE_NUMERIC_VALUE, ControlStatesFilterReducer::numericEmptyPanePredicate);
        emptyPanePredicates.put(CONTROL_PANE_STRING_VALUE, ControlStatesFilterReducer::stringEmptyPanePredicate);
        emptyPanePredicates.put(CONTROL_PANE_BOOLEAN_VALUE, ControlStatesFilterReducer::booleanEmptyPanePredicate);
        emptyPanePredicates.put(CONTROL_PANE_COLOR_VALUE, ControlStatesFilterReducer::colorEmptyPanePredicate);
        emptyPanePredicates.put(CONTROL_PANE_DATE_VALUE, ControlStatesFilterReducer::dateEmptyPanePredicate);
        emptyPanePredicates.put(CONTROL_PANE_DATA_TABLE_VALUE, ControlStatesFilterReducer::dataTableEmptyPanePredicate);
        emptyPanePredicates.put(CONTROL_PANE_DATA_BLOCK_VALUE, ControlStatesFilterReducer::dataBlockEmptyPanePredicate);
    }

    public static DataTable reduceFilter(DataTable initialFilter) {
        if (!TF_COMPONENT_SMART_FILTER.equals(initialFilter.getFormat())) {
            throw new IllegalArgumentException("Illegal smart filter table format");
        }
        SimpleDataTable result = new SimpleDataTable(TF_COMPONENT_SMART_FILTER);
        for (int i = 0; i < initialFilter.getRecordCount(); i++) {
            DataRecord dr = initialFilter.getRecord(i);
            Integer paneType = dr.getInt(COMPONENT_SMART_FILTER_TYPE);
            Function<DataRecord, Boolean> emptyPredicate = emptyPanePredicates.get(paneType);
            if (!emptyPredicate.apply(dr)) {
                result.addRecord(dr);
            }
        }
        return result;
    }

    private static Boolean numericEmptyPanePredicate(DataRecord columnFilter) {
        DataTable pane = columnFilter.getDataTable(COMPONENT_SMART_FILTER_STATE);
        return pane.rec().getInt(PANE_FIELD_VALUE_PRESENCE) == VALUE_PRESENCE_ALL &&
                emptyVariant(pane.rec().getDataTable(PANE_FIELD_EQUALS)) &&
                emptyVector(pane.rec().getDataTable(PANE_FIELD_INCLUDED_IN)) &&
                emptyVariant(pane.rec().getDataTable(PANE_FIELD_DOES_NOT_EQUAL)) &&
                emptyVariant(pane.rec().getDataTable(PANE_FIELD_GREATER_THAN)) &&
                emptyVariant(pane.rec().getDataTable(PANE_FIELD_GREATER_OR_EQUAL_THAN)) &&
                emptyVariant(pane.rec().getDataTable(PANE_FIELD_LESS_THAN)) &&
                emptyVariant(pane.rec().getDataTable(PANE_FIELD_LESS_OR_EQUAL_THAN));
    }

    private static Boolean stringEmptyPanePredicate(DataRecord columnFilter) {
        DataTable pane = columnFilter.getDataTable(COMPONENT_SMART_FILTER_STATE);
        return pane.rec().getInt(PANE_FIELD_VALUE_PRESENCE) == VALUE_PRESENCE_ALL &&
                pane.rec().getString(PANE_FIELD_EQUALS) == null &&
                emptyVector(pane.rec().getDataTable(PANE_FIELD_INCLUDED_IN)) &&
                pane.rec().getString(PANE_FIELD_DOES_NOT_EQUAL) == null &&
                pane.rec().getString(PANE_FIELD_CONTAINS) == null &&
                pane.rec().getString(PANE_FIELD_DOES_NOT_CONTAIN) == null &&
                pane.rec().getString(PANE_FIELD_BEGINS_WITH) == null &&
                pane.rec().getString(PANE_FIELD_DOES_NOT_BEGIN_WITH) == null &&
                pane.rec().getString(PANE_FIELD_ENDS_WITH) == null &&
                pane.rec().getString(PANE_FIELD_DOES_NOT_END_WITH) == null;
    }

    private static Boolean booleanEmptyPanePredicate(DataRecord columnFilter) {
        DataTable pane = columnFilter.getDataTable(COMPONENT_SMART_FILTER_STATE);
        return pane.rec().getInt(PANE_FIELD_VALUE_PRESENCE) == VALUE_PRESENCE_ALL &&
                pane.rec().getInt(PANE_FIELD_EQUALS) == BOOLEAN_ALL;
    }

    private static Boolean colorEmptyPanePredicate(DataRecord columnFilter) {
        DataTable pane = columnFilter.getDataTable(COMPONENT_SMART_FILTER_STATE);
        return pane.rec().getInt(PANE_FIELD_VALUE_PRESENCE) == VALUE_PRESENCE_ALL &&
                pane.rec().getColor(PANE_FIELD_EQUALS) == null &&
                pane.rec().getColor(PANE_FIELD_DOES_NOT_EQUAL) == null;
    }

    private static Boolean dateEmptyPanePredicate(DataRecord columnFilter) {
        DataTable pane = columnFilter.getDataTable(COMPONENT_SMART_FILTER_STATE);
        return pane.rec().getInt(PANE_FIELD_VALUE_PRESENCE) == VALUE_PRESENCE_ALL &&
                pane.rec().getDate(PANE_FIELD_EQUALS) == null &&
                emptyVector(pane.rec().getDataTable(PANE_FIELD_INCLUDED_IN)) &&
                pane.rec().getDate(PANE_FIELD_DOES_NOT_EQUAL) == null &&
                pane.rec().getDate(PANE_FIELD_GREATER_THAN) == null &&
                pane.rec().getDate(PANE_FIELD_GREATER_OR_EQUAL_THAN) == null &&
                pane.rec().getDate(PANE_FIELD_LESS_THAN) == null &&
                pane.rec().getDate(PANE_FIELD_LESS_OR_EQUAL_THAN) == null &&
                pane.rec().getLong(PANE_FIELD_LAST_INTERVAL) == null &&
                emptyTable(pane.rec().getDataTable(PANE_FIELD_DATE_TIME_RANGE)) &&
                pane.rec().getInt(PANE_FIELD_CURRENT_INTERVAL) == TIME_INTERVAL_NOT_SET &&
                pane.rec().getLong(PANE_FIELD_NEXT_INTERVAL) == null;
    }

    private static Boolean dataTableEmptyPanePredicate(DataRecord columnFilter) {
        DataTable pane = columnFilter.getDataTable(COMPONENT_SMART_FILTER_STATE);
        return pane.rec().getInt(PANE_FIELD_VALUE_PRESENCE) == VALUE_PRESENCE_ALL &&
                pane.rec().getString(PANE_FIELD_CONTAINS) == null &&
                pane.rec().getString(PANE_FIELD_DOES_NOT_CONTAIN) == null &&
                pane.rec().getInt(PANE_FIELD_RECORD_COUNT) == null;
    }

    private static Boolean dataBlockEmptyPanePredicate(DataRecord columnFilter) {
        DataTable pane = columnFilter.getDataTable(COMPONENT_SMART_FILTER_STATE);
        return pane.rec().getInt(PANE_FIELD_VALUE_PRESENCE) == VALUE_PRESENCE_ALL;
    }

    private static Boolean emptyVariant(DataTable variant) {
        if (variant.getRecordCount() != 1 || variant.getFieldCount() != 1) {
            throw new IllegalArgumentException("Illegal variant table. 1x1 table is expected");
        }
        return variant.rec().getValue(VALUE) == null;
    }

    private static Boolean emptyVector(DataTable vector) {
        if (vector.getFieldCount() != 1) {
            throw new IllegalArgumentException("Illegal vector table. Nx1 table is expected");
        }
        return vector.getRecordCount() == 0;
    }

    private static boolean emptyTable(DataTable dataTable) {
        return dataTable.getRecordCount() == 0;
    }

}
