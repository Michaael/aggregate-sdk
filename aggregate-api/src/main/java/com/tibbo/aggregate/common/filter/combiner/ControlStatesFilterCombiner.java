package com.tibbo.aggregate.common.filter.combiner;

import static com.tibbo.aggregate.common.filter.FilterApiConstants.COMPONENT_SMART_FILTER_COLUMN;
import static com.tibbo.aggregate.common.filter.FilterApiConstants.COMPONENT_SMART_FILTER_STATE;
import static com.tibbo.aggregate.common.filter.FilterApiConstants.COMPONENT_SMART_FILTER_TYPE;
import static com.tibbo.aggregate.common.filter.FilterApiConstants.CONTROL_PANE_BOOLEAN_VALUE;
import static com.tibbo.aggregate.common.filter.FilterApiConstants.CONTROL_PANE_COLOR_VALUE;
import static com.tibbo.aggregate.common.filter.FilterApiConstants.CONTROL_PANE_DATA_BLOCK_VALUE;
import static com.tibbo.aggregate.common.filter.FilterApiConstants.CONTROL_PANE_DATA_TABLE_VALUE;
import static com.tibbo.aggregate.common.filter.FilterApiConstants.CONTROL_PANE_DATE_VALUE;
import static com.tibbo.aggregate.common.filter.FilterApiConstants.CONTROL_PANE_NUMERIC_VALUE;
import static com.tibbo.aggregate.common.filter.FilterApiConstants.CONTROL_PANE_STRING_VALUE;
import static com.tibbo.aggregate.common.filter.FilterApiConstants.PANE_FIELD_BEGINS_WITH;
import static com.tibbo.aggregate.common.filter.FilterApiConstants.PANE_FIELD_CONTAINS;
import static com.tibbo.aggregate.common.filter.FilterApiConstants.PANE_FIELD_CURRENT_INTERVAL;
import static com.tibbo.aggregate.common.filter.FilterApiConstants.PANE_FIELD_DATE_TIME_RANGE;
import static com.tibbo.aggregate.common.filter.FilterApiConstants.PANE_FIELD_DOES_NOT_BEGIN_WITH;
import static com.tibbo.aggregate.common.filter.FilterApiConstants.PANE_FIELD_DOES_NOT_CONTAIN;
import static com.tibbo.aggregate.common.filter.FilterApiConstants.PANE_FIELD_DOES_NOT_END_WITH;
import static com.tibbo.aggregate.common.filter.FilterApiConstants.PANE_FIELD_DOES_NOT_EQUAL;
import static com.tibbo.aggregate.common.filter.FilterApiConstants.PANE_FIELD_ENDS_WITH;
import static com.tibbo.aggregate.common.filter.FilterApiConstants.PANE_FIELD_EQUALS;
import static com.tibbo.aggregate.common.filter.FilterApiConstants.PANE_FIELD_FILE_TYPE;
import static com.tibbo.aggregate.common.filter.FilterApiConstants.PANE_FIELD_GREATER_OR_EQUAL_THAN;
import static com.tibbo.aggregate.common.filter.FilterApiConstants.PANE_FIELD_GREATER_THAN;
import static com.tibbo.aggregate.common.filter.FilterApiConstants.PANE_FIELD_INCLUDED_IN;
import static com.tibbo.aggregate.common.filter.FilterApiConstants.PANE_FIELD_LAST_INTERVAL;
import static com.tibbo.aggregate.common.filter.FilterApiConstants.PANE_FIELD_LESS_OR_EQUAL_THAN;
import static com.tibbo.aggregate.common.filter.FilterApiConstants.PANE_FIELD_LESS_THAN;
import static com.tibbo.aggregate.common.filter.FilterApiConstants.PANE_FIELD_NEXT_INTERVAL;
import static com.tibbo.aggregate.common.filter.FilterApiConstants.PANE_FIELD_RECORD_COUNT;
import static com.tibbo.aggregate.common.filter.FilterApiConstants.PANE_FIELD_VALUE_PRESENCE;
import static com.tibbo.aggregate.common.filter.FilterApiConstants.TF_CONTROL_PANE_BOOLEAN_VALUE;
import static com.tibbo.aggregate.common.filter.FilterApiConstants.TF_CONTROL_PANE_COLOR_VALUE;
import static com.tibbo.aggregate.common.filter.FilterApiConstants.TF_CONTROL_PANE_DATABLOCK_VALUE;
import static com.tibbo.aggregate.common.filter.FilterApiConstants.TF_CONTROL_PANE_DATATABLE_VALUE;
import static com.tibbo.aggregate.common.filter.FilterApiConstants.TF_CONTROL_PANE_DATE_VALUE;
import static com.tibbo.aggregate.common.filter.FilterApiConstants.TF_CONTROL_PANE_NUMERIC_VALUE;
import static com.tibbo.aggregate.common.filter.FilterApiConstants.VALUE;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import com.tibbo.aggregate.common.datatable.DataRecord;
import com.tibbo.aggregate.common.datatable.DataTable;
import com.tibbo.aggregate.common.datatable.FieldFormat;
import com.tibbo.aggregate.common.filter.FilterApiConstants;

public class ControlStatesFilterCombiner {

    private final DataTable baseFilterTable;
    private final Map<String, Integer> columnNameToPaneType = new HashMap<>();
    private final Map<String, DataTable> columnNameToPaneTable = new HashMap<>();
    private static final Map<Integer, BiFunction<DataTable, DataTable, DataTable>> paneCombiner = new HashMap<>();

    static {
        paneCombiner.put(CONTROL_PANE_NUMERIC_VALUE, ControlStatesFilterCombiner::numericPaneCombiner);
        paneCombiner.put(CONTROL_PANE_STRING_VALUE, ControlStatesFilterCombiner::stringPaneCombiner);
        paneCombiner.put(CONTROL_PANE_BOOLEAN_VALUE, ControlStatesFilterCombiner::booleanPaneCombiner);
        paneCombiner.put(CONTROL_PANE_COLOR_VALUE, ControlStatesFilterCombiner::colorPaneCombiner);
        paneCombiner.put(CONTROL_PANE_DATE_VALUE, ControlStatesFilterCombiner::datePaneCombiner);
        paneCombiner.put(CONTROL_PANE_DATA_TABLE_VALUE, ControlStatesFilterCombiner::dataTablePaneCombiner);
        paneCombiner.put(CONTROL_PANE_DATA_BLOCK_VALUE, ControlStatesFilterCombiner::dataBlockPaneCombiner);
    }

    public ControlStatesFilterCombiner(DataTable baseFilterTable) {
        validateTableFormat(baseFilterTable);
        this.baseFilterTable = baseFilterTable.clone();
        baseFilterTable.stream().forEach(dr -> {
            columnNameToPaneType.put(dr.getString(COMPONENT_SMART_FILTER_COLUMN), dr.getInt(COMPONENT_SMART_FILTER_TYPE));
            columnNameToPaneTable.put(dr.getString(COMPONENT_SMART_FILTER_COLUMN), dr.getDataTable(COMPONENT_SMART_FILTER_STATE));
        });
    }

    public DataTable combine(DataTable filerTable) {
        validateTableFormat(filerTable);
        DataTable clonedBaseFilterTable = baseFilterTable.clone();
        for (int i = 0; i < filerTable.getRecordCount(); i++) {
            DataRecord columnFilter = filerTable.getRecord(i);
            String columnName = columnFilter.getString(COMPONENT_SMART_FILTER_COLUMN);
            if (!columnNameToPaneType.containsKey(columnName)) {
                throw new IllegalArgumentException("Illegal column name: " + columnName);
            }
            Integer paneType = columnFilter.getInt(COMPONENT_SMART_FILTER_TYPE);
            if (!columnNameToPaneType.get(columnName).equals(paneType)) {
                throw new IllegalArgumentException("Pane type does not match for column: " + columnName);
            }
            DataTable fromPane = columnFilter.getDataTable(COMPONENT_SMART_FILTER_STATE);

            BiFunction<DataTable, DataTable, DataTable> combiner = paneCombiner.get(paneType);
            if (combiner == null) {
                throw new IllegalArgumentException("Illegal pane type");
            }
            DataTable intoPane = getPaneReference(columnName, clonedBaseFilterTable);
            combiner.apply(intoPane, fromPane);
        }
        return clonedBaseFilterTable;
    }

    private static DataTable getPaneReference(String columnName, DataTable filterTable) {
        validateTableFormat(filterTable);
        for (int i = 0; i < filterTable.getRecordCount(); i++) {
            if (columnName.equals(filterTable.getRecord(i).getString(COMPONENT_SMART_FILTER_COLUMN))) {
                return filterTable.getRecord(i).getDataTable(COMPONENT_SMART_FILTER_STATE);
            }
        }
        return null;
    }

    private static void validateTableFormat(DataTable filterTable) {
        if (!FilterApiConstants.TF_COMPONENT_SMART_FILTER.equals(filterTable.getFormat())) {
            throw new IllegalArgumentException("Illegal filter table format");
        }
    }

    private static DataTable numericPaneCombiner(DataTable into, DataTable from) {
        if (!TF_CONTROL_PANE_NUMERIC_VALUE.equals(from.getFormat())) {
            throw new IllegalArgumentException("Illegal pane table format. Numeric pane is expected");
        }
        if (from.hasField(PANE_FIELD_VALUE_PRESENCE)) {
            into.rec().setValue(PANE_FIELD_VALUE_PRESENCE, from.rec().getValue(PANE_FIELD_VALUE_PRESENCE));
        }
        if (from.hasField(PANE_FIELD_EQUALS)) {
            copyVariant(into.rec().getDataTable(PANE_FIELD_EQUALS), from.rec().getDataTable(PANE_FIELD_EQUALS));
        }
        if (from.hasField(PANE_FIELD_INCLUDED_IN)) {
            copyVector(into.rec().getDataTable(PANE_FIELD_INCLUDED_IN), from.rec().getDataTable(PANE_FIELD_INCLUDED_IN));
        }
        if (from.hasField(PANE_FIELD_DOES_NOT_EQUAL)) {
            copyVariant(into.rec().getDataTable(PANE_FIELD_DOES_NOT_EQUAL), from.rec().getDataTable(PANE_FIELD_DOES_NOT_EQUAL));
        }
        if (from.hasField(PANE_FIELD_GREATER_THAN)) {
            copyVariant(into.rec().getDataTable(PANE_FIELD_GREATER_THAN), from.rec().getDataTable(PANE_FIELD_GREATER_THAN));
        }
        if (from.hasField(PANE_FIELD_GREATER_OR_EQUAL_THAN)) {
            copyVariant(into.rec().getDataTable(PANE_FIELD_GREATER_OR_EQUAL_THAN), from.rec().getDataTable(PANE_FIELD_GREATER_OR_EQUAL_THAN));
        }
        if (from.hasField(PANE_FIELD_LESS_THAN)) {
            copyVariant(into.rec().getDataTable(PANE_FIELD_LESS_THAN), from.rec().getDataTable(PANE_FIELD_LESS_THAN));
        }
        if (from.hasField(PANE_FIELD_LESS_OR_EQUAL_THAN)) {
            copyVariant(into.rec().getDataTable(PANE_FIELD_LESS_OR_EQUAL_THAN), from.rec().getDataTable(PANE_FIELD_LESS_OR_EQUAL_THAN));
        }
        return into;
    }

    private static DataTable stringPaneCombiner(DataTable into, DataTable from) {
        checkAndCopyControlStateValue(into, from, PANE_FIELD_VALUE_PRESENCE);
        checkAndCopyControlStateValue(into, from, PANE_FIELD_EQUALS);
        checkAndCopyControlStateVector(into, from, PANE_FIELD_INCLUDED_IN);
        checkAndCopyControlStateValue(into, from, PANE_FIELD_DOES_NOT_EQUAL);
        checkAndCopyControlStateValue(into, from, PANE_FIELD_CONTAINS);
        checkAndCopyControlStateValue(into, from, PANE_FIELD_DOES_NOT_CONTAIN);
        checkAndCopyControlStateValue(into, from, PANE_FIELD_BEGINS_WITH);
        checkAndCopyControlStateValue(into, from, PANE_FIELD_DOES_NOT_BEGIN_WITH);
        checkAndCopyControlStateValue(into, from, PANE_FIELD_ENDS_WITH);
        checkAndCopyControlStateValue(into, from, PANE_FIELD_DOES_NOT_END_WITH);
        return into;
    }

    private static DataTable booleanPaneCombiner(DataTable into, DataTable from) {
        if (!TF_CONTROL_PANE_BOOLEAN_VALUE.equals(from.getFormat())) {
            throw new IllegalArgumentException("Illegal pane table format. Boolean pane is expected");
        }
        checkAndCopyControlStateValue(into, from, PANE_FIELD_VALUE_PRESENCE);
        checkAndCopyControlStateValue(into, from, PANE_FIELD_EQUALS);
        return into;
    }

    private static DataTable colorPaneCombiner(DataTable into, DataTable from) {
        if (!TF_CONTROL_PANE_COLOR_VALUE.equals(from.getFormat())) {
            throw new IllegalArgumentException("Illegal pane table format. Color pane is expected");
        }
        checkAndCopyControlStateValue(into, from, PANE_FIELD_VALUE_PRESENCE);
        checkAndCopyControlStateValue(into, from, PANE_FIELD_EQUALS);
        checkAndCopyControlStateValue(into, from, PANE_FIELD_DOES_NOT_EQUAL);
        return into;
    }

    private static DataTable datePaneCombiner(DataTable into, DataTable from) {
        if (!TF_CONTROL_PANE_DATE_VALUE.equals(from.getFormat())) {
            throw new IllegalArgumentException("Illegal pane table format. Date pane is expected");
        }
        checkAndCopyControlStateValue(into, from, PANE_FIELD_VALUE_PRESENCE);
        checkAndCopyControlStateValue(into, from, PANE_FIELD_EQUALS);
        checkAndCopyControlStateVector(into, from, PANE_FIELD_INCLUDED_IN);
        checkAndCopyControlStateValue(into, from, PANE_FIELD_DOES_NOT_EQUAL);
        checkAndCopyControlStateValue(into, from, PANE_FIELD_GREATER_THAN);
        checkAndCopyControlStateValue(into, from, PANE_FIELD_GREATER_OR_EQUAL_THAN);
        checkAndCopyControlStateValue(into, from, PANE_FIELD_LESS_THAN);
        checkAndCopyControlStateValue(into, from, PANE_FIELD_LESS_OR_EQUAL_THAN);
        checkAndCopyControlStateValue(into, from, PANE_FIELD_LAST_INTERVAL);
        checkAndCopyControlStateValue(into, from, PANE_FIELD_DATE_TIME_RANGE);
        checkAndCopyControlStateValue(into, from, PANE_FIELD_CURRENT_INTERVAL);
        checkAndCopyControlStateValue(into, from, PANE_FIELD_NEXT_INTERVAL);
        checkAndCopyControlStateValue(into, from, PANE_FIELD_NEXT_INTERVAL);
        return into;
    }

    private static DataTable dataTablePaneCombiner(DataTable into, DataTable from) {
        if (!TF_CONTROL_PANE_DATATABLE_VALUE.equals(from.getFormat())) {
            throw new IllegalArgumentException("Illegal pane table format. Data table pane is expected");
        }
        checkAndCopyControlStateValue(into, from, PANE_FIELD_VALUE_PRESENCE);
        checkAndCopyControlStateValue(into, from, PANE_FIELD_CONTAINS);
        checkAndCopyControlStateValue(into, from, PANE_FIELD_DOES_NOT_CONTAIN);
        checkAndCopyControlStateValue(into, from, PANE_FIELD_RECORD_COUNT);
        return into;
    }

    private static DataTable dataBlockPaneCombiner(DataTable into, DataTable from) {
        if (!TF_CONTROL_PANE_DATABLOCK_VALUE.equals(from.getFormat())) {
            throw new IllegalArgumentException("Illegal pane table format. Data block pane is expected");
        }
        checkAndCopyControlStateValue(into, from, PANE_FIELD_VALUE_PRESENCE);
        checkAndCopyControlStateValue(into, from, PANE_FIELD_FILE_TYPE);
        return into;
    }

    private static void checkAndCopyControlStateValue(DataTable into, DataTable from, String control) {
        if (from.hasField(control)) {
            into.rec().setValue(control, from.rec().getValue(control));
        }
    }

    private static void checkAndCopyControlStateVariant(DataTable into, DataTable from, String control) {
        if (from.hasField(control)) {
            copyVector(into.rec().getDataTable(control), from.rec().getDataTable(control));
        }
    }

    private static void checkAndCopyControlStateVector(DataTable into, DataTable from, String control) {
        if (from.hasField(control)) {
            copyVector(into.rec().getDataTable(control), from.rec().getDataTable(control));
        }
    }

    private static void copyVariant(DataTable into, DataTable from) {
        validateVariantMatchType(into, from, false);
        Object fromValue = from.rec().getValue(VALUE);
        into.rec().setValue(VALUE, fromValue);
    }

    private static void copyVector(DataTable into, DataTable from) {
        if (from == null)
        {
            from = into.clone();
        }
        validateVariantMatchType(into, from, true);
        for (int i = into.getRecordCount() - 1; i >= 0; i--) {
            into.removeRecord(i);
        }
        for (int i = 0; i < from.getRecordCount(); i++) {
            into.addRecord(from.getRecord(i));
        }
    }

    private static void validateVariantContainer(DataTable variantContainer, boolean vector) {
        if (vector && variantContainer.getFieldCount() != 1) {
            throw new IllegalArgumentException("Illegal vector table format. Nx1 table is expected");
        }

        if (!vector && (variantContainer.getFieldCount() != 1 || variantContainer.getRecordCount() != 1)) {
            throw new IllegalArgumentException("Illegal variant table format. 1x1 table is expected");
        }
        if (!variantContainer.hasField(VALUE)) {
            throw new IllegalArgumentException("Illegal variant table format. Field: " + VALUE + " is expected");
        }
        FieldFormat<?> valueField = variantContainer.getFormat(VALUE);
        if (valueField.getType() != FieldFormat.INTEGER_FIELD
                && valueField.getType() != FieldFormat.LONG_FIELD
                && valueField.getType() != FieldFormat.FLOAT_FIELD
                && valueField.getType() != FieldFormat.DOUBLE_FIELD
                && valueField.getType() != FieldFormat.STRING_FIELD
                && valueField.getType() != FieldFormat.DATE_FIELD) {
            throw new IllegalArgumentException("Illegal variant table format. Numeric or String field type is expected");
        }
    }

    private static void validateVariantMatchType(DataTable into, DataTable from, boolean vector) {
        validateVariantContainer(into, vector);
        validateVariantContainer(from, vector);
        if (into.getFormat(VALUE).getType() != from.getFormat(VALUE).getType()) {
            throw new IllegalArgumentException("Incompatible variant formats");
        }
    }

}
