package com.tibbo.aggregate.common.filter.converter;

import static com.tibbo.aggregate.common.datatable.FieldFormat.BOOLEAN_FIELD;
import static com.tibbo.aggregate.common.datatable.FieldFormat.COLOR_FIELD;
import static com.tibbo.aggregate.common.datatable.FieldFormat.DATATABLE_FIELD;
import static com.tibbo.aggregate.common.datatable.FieldFormat.DATA_FIELD;
import static com.tibbo.aggregate.common.datatable.FieldFormat.DATE_FIELD;
import static com.tibbo.aggregate.common.datatable.FieldFormat.DOUBLE_FIELD;
import static com.tibbo.aggregate.common.datatable.FieldFormat.EDITOR_FOREIGN_INSTANCE;
import static com.tibbo.aggregate.common.datatable.FieldFormat.EDITOR_INSTANCE;
import static com.tibbo.aggregate.common.datatable.FieldFormat.FLOAT_FIELD;
import static com.tibbo.aggregate.common.datatable.FieldFormat.INTEGER_FIELD;
import static com.tibbo.aggregate.common.datatable.FieldFormat.LONG_FIELD;
import static com.tibbo.aggregate.common.datatable.FieldFormat.STRING_FIELD;
import static com.tibbo.aggregate.common.filter.FilterApiConstants.BOOLEAN_ALL;
import static com.tibbo.aggregate.common.filter.FilterApiConstants.BOOLEAN_TRUE;
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
import static com.tibbo.aggregate.common.filter.FilterApiConstants.CONTROL_PANE_UNDEFINED_VALUE;
import static com.tibbo.aggregate.common.filter.FilterApiConstants.PANE_FIELD_BEGINS_WITH;
import static com.tibbo.aggregate.common.filter.FilterApiConstants.PANE_FIELD_CONTAINS;
import static com.tibbo.aggregate.common.filter.FilterApiConstants.PANE_FIELD_CURRENT_INTERVAL;
import static com.tibbo.aggregate.common.filter.FilterApiConstants.PANE_FIELD_DATE_TIME_RANGE;
import static com.tibbo.aggregate.common.filter.FilterApiConstants.PANE_FIELD_DOES_NOT_BEGIN_WITH;
import static com.tibbo.aggregate.common.filter.FilterApiConstants.PANE_FIELD_DOES_NOT_CONTAIN;
import static com.tibbo.aggregate.common.filter.FilterApiConstants.PANE_FIELD_DOES_NOT_END_WITH;
import static com.tibbo.aggregate.common.filter.FilterApiConstants.PANE_FIELD_DOES_NOT_EQUAL;
import static com.tibbo.aggregate.common.filter.FilterApiConstants.PANE_FIELD_EDITOR;
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
import static com.tibbo.aggregate.common.filter.FilterApiConstants.TF_COMPONENT_SMART_FILTER;
import static com.tibbo.aggregate.common.filter.FilterApiConstants.TF_CONTROL_PANE_BOOLEAN_VALUE;
import static com.tibbo.aggregate.common.filter.FilterApiConstants.TF_CONTROL_PANE_COLOR_VALUE;
import static com.tibbo.aggregate.common.filter.FilterApiConstants.TF_CONTROL_PANE_DATABLOCK_VALUE;
import static com.tibbo.aggregate.common.filter.FilterApiConstants.TF_CONTROL_PANE_DATATABLE_VALUE;
import static com.tibbo.aggregate.common.filter.FilterApiConstants.TF_CONTROL_PANE_DATE_VALUE;
import static com.tibbo.aggregate.common.filter.FilterApiConstants.TF_CONTROL_PANE_NUMERIC_VALUE;
import static com.tibbo.aggregate.common.filter.FilterApiConstants.TF_CONTROL_PANE_STRING_VALUE;
import static com.tibbo.aggregate.common.filter.FilterApiConstants.TIME_INTERVAL_DAY;
import static com.tibbo.aggregate.common.filter.FilterApiConstants.TIME_INTERVAL_HOUR;
import static com.tibbo.aggregate.common.filter.FilterApiConstants.TIME_INTERVAL_MONTH;
import static com.tibbo.aggregate.common.filter.FilterApiConstants.TIME_INTERVAL_NOT_SET;
import static com.tibbo.aggregate.common.filter.FilterApiConstants.TIME_INTERVAL_WEEK;
import static com.tibbo.aggregate.common.filter.FilterApiConstants.TIME_INTERVAL_YEAR;
import static com.tibbo.aggregate.common.filter.FilterApiConstants.TIME_RANGE_END_DATE;
import static com.tibbo.aggregate.common.filter.FilterApiConstants.TIME_RANGE_START_DATE;
import static com.tibbo.aggregate.common.filter.FilterApiConstants.VALUE_PRESENCE_ALL;
import static com.tibbo.aggregate.common.filter.FilterApiConstants.VALUE_PRESENCE_NOT_SET;
import static com.tibbo.aggregate.common.filter.FilterApiConstants.VALUE_PRESENCE_SET;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;

import org.apache.logging.log4j.util.Strings;

import com.tibbo.aggregate.common.datatable.DataRecord;
import com.tibbo.aggregate.common.datatable.DataTable;
import com.tibbo.aggregate.common.datatable.FieldFormat;
import com.tibbo.aggregate.common.datatable.SimpleDataTable;
import com.tibbo.aggregate.common.datatable.TableFormat;
import com.tibbo.aggregate.common.datatable.field.DateFieldFormat;
import com.tibbo.aggregate.common.expression.function.DefaultFunctions;
import com.tibbo.aggregate.common.filter.FunctionOperation;

public class ControlStatesFilterConverter {

    // This interval conversions are taken from frontend side
    private static final long SECOND_IN_MS  = 1000;
    private static final long MINUTE_IN_MS  = SECOND_IN_MS * 60;
    private static final long HOUR_IN_MS    = MINUTE_IN_MS * 60;
    private static final long DAY_IN_MS     = HOUR_IN_MS * 24;
    private static final long WEEK_IN_MS    = DAY_IN_MS * 7;
    private static final long MONTH_IN_MS   = DAY_IN_MS * 30;
    private static final long QUARTER_IN_MS = DAY_IN_MS * 91;
    private static final long YEAR_IN_MS    = DAY_IN_MS * 365;

    private static final Map<Character, BiFunction<DataTable, String, DataRecord>> typeHandlers = new HashMap<>();
    private static final Map<String, BiFunction<DataTable, String, DataRecord>> editorHandlers = new HashMap<>();
    private static final Map<Integer, BiFunction<String, DataTable, String>> paneHandlers = new HashMap<>();

    private static final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

    private static class ExtraStringDataHandler implements BiFunction<DataTable, String, DataRecord> {

        private Map<String, DataTable> extraData;
        public ExtraStringDataHandler(Map<String, DataTable> extraData) {
            this.extraData = extraData;
        }
        @Override
        public DataRecord apply(DataTable dataTable, String columnName) {
            DataRecord columnFilter = dataTable.addRecord();
            TableFormat tf = TF_CONTROL_PANE_STRING_VALUE.clone();
            if (extraData.containsKey(columnName)) {
                FieldFormat equalsField = tf.getField(PANE_FIELD_EQUALS);
                FieldFormat notEqualField = tf.getField(PANE_FIELD_DOES_NOT_EQUAL);
                //equalsField.addSelectionValue(null);
                DataTable values = extraData.get(columnName);
                for (int i = 0; i < values.getRecordCount(); i++) {
                    DataRecord dr = values.getRecord(i);
                    if (dr.getValue(0) instanceof String && !((String) dr.getValue(0)).isEmpty()) {
                        equalsField.addSelectionValue(dr.getValue(0));
                        notEqualField.addSelectionValue(dr.getValue(0));
                    }
                }
            }
            SimpleDataTable paneTable = new SimpleDataTable(tf);
            DataRecord paneRecord = paneTable.addRecord();
            paneRecord.setValue(PANE_FIELD_VALUE_PRESENCE, VALUE_PRESENCE_ALL);
            paneRecord.setValue(PANE_FIELD_EQUALS, null);
            paneRecord.setValue(PANE_FIELD_INCLUDED_IN, ControlStatesFilterUtil.makeEmptyStringList());
            paneRecord.setValue(PANE_FIELD_DOES_NOT_EQUAL, null);
            paneRecord.setValue(PANE_FIELD_CONTAINS, null);
            paneRecord.setValue(PANE_FIELD_DOES_NOT_CONTAIN, null);
            paneRecord.setValue(PANE_FIELD_BEGINS_WITH, null);
            paneRecord.setValue(PANE_FIELD_DOES_NOT_BEGIN_WITH, null);
            paneRecord.setValue(PANE_FIELD_ENDS_WITH, null);
            paneRecord.setValue(PANE_FIELD_DOES_NOT_END_WITH, null);

            columnFilter.setValue(COMPONENT_SMART_FILTER_COLUMN, columnName);
            columnFilter.setValue(COMPONENT_SMART_FILTER_TYPE, CONTROL_PANE_STRING_VALUE);
            columnFilter.setValue(COMPONENT_SMART_FILTER_STATE, paneTable);
            return columnFilter;
        }
    }

    static {

        typeHandlers.put(INTEGER_FIELD, ControlStatesFilterConverter::integerConverter);
        typeHandlers.put(STRING_FIELD, ControlStatesFilterConverter::stringConverter);
        typeHandlers.put(BOOLEAN_FIELD, ControlStatesFilterConverter::booleanConverter);
        typeHandlers.put(LONG_FIELD, ControlStatesFilterConverter::longConverter);
        typeHandlers.put(FLOAT_FIELD, ControlStatesFilterConverter::floatConverter);
        typeHandlers.put(DOUBLE_FIELD, ControlStatesFilterConverter::doubleConverter);
        typeHandlers.put(DATE_FIELD, ControlStatesFilterConverter::dateTimeConverter);
        typeHandlers.put(DATATABLE_FIELD, ControlStatesFilterConverter::dataTableConverter);
        typeHandlers.put(COLOR_FIELD, ControlStatesFilterConverter::colorConverter);
        typeHandlers.put(DATA_FIELD, ControlStatesFilterConverter::dataConverter);

        editorHandlers.put(EDITOR_INSTANCE, ControlStatesFilterConverter::stringConverter);
        editorHandlers.put(EDITOR_FOREIGN_INSTANCE, ControlStatesFilterConverter::stringConverter);
        editorHandlers.put(DateFieldFormat.EDITOR_DATE, ControlStatesFilterConverter::dateConverter);
        editorHandlers.put(DateFieldFormat.EDITOR_TIME, ControlStatesFilterConverter::timeConverter);

        paneHandlers.put(CONTROL_PANE_UNDEFINED_VALUE, ControlStatesFilterConverter::undefinedPaneHandler);
        paneHandlers.put(CONTROL_PANE_NUMERIC_VALUE, ControlStatesFilterConverter::numericPaneHandler);
        paneHandlers.put(CONTROL_PANE_STRING_VALUE, ControlStatesFilterConverter::stringPaneHandler);
        paneHandlers.put(CONTROL_PANE_BOOLEAN_VALUE, ControlStatesFilterConverter::booleanPaneHandler);
        paneHandlers.put(CONTROL_PANE_COLOR_VALUE, ControlStatesFilterConverter::colorPaneHandler);
        paneHandlers.put(CONTROL_PANE_DATE_VALUE, ControlStatesFilterConverter::datePaneHandler);
        paneHandlers.put(CONTROL_PANE_DATA_TABLE_VALUE, ControlStatesFilterConverter::dataTablePaneHandler);
        paneHandlers.put(CONTROL_PANE_DATA_BLOCK_VALUE, ControlStatesFilterConverter::dataBlockPaneHandler);
    }

    public static DataTable buildEmptyStateTableFromMetadata(DataTable table) {
        DataTable result = new SimpleDataTable(TF_COMPONENT_SMART_FILTER.clone());

        List<FieldFormat> fieldFormats = table.getFormat().getFields();
        for (FieldFormat ff : fieldFormats) {
            BiFunction<DataTable, String, DataRecord> func = typeHandlers.get(ff.getType());
            if (ff.isHidden())
            {
                continue;
            }
            if (ff.getEditor() != null && editorHandlers.get(ff.getEditor()) != null)
            {
                func = editorHandlers.get(ff.getEditor());
            }
            if (func == null) {
                throw new IllegalStateException("");
            }
            func.apply(result, ff.getName());
        }

        return result;
    }

    public static String buildFilerTextExpression(DataTable dataTable) {
        Objects.requireNonNull(dataTable, "dataTable is null");

        if (dataTable.getRecordCount() == 0) {
            return "";
        }
        if (!dataTable.getFormat().equals(TF_COMPONENT_SMART_FILTER)) {
            throw new IllegalArgumentException("Illegal smart filter format (DataTable)");
        }
        StringBuilder feb = new StringBuilder();
        for (int i = 0; i < dataTable.getRecordCount(); i++) {
            String columnExpression = buildFilerTextExpressionFromFilterRow(dataTable.getRecord(i));
            if (columnExpression.length() != 0) {
                combineByLogicAnd(feb);
            }
            feb.append(buildFilerTextExpressionFromFilterRow(dataTable.getRecord(i)));
        }
        return feb.toString();
    }

    private static String buildFilerTextExpressionFromFilterRow(DataRecord filterRecord) {
        String columnName = filterRecord.getString(COMPONENT_SMART_FILTER_COLUMN);
        Integer smartFilterPaneType = filterRecord.getInt(COMPONENT_SMART_FILTER_TYPE);
        DataTable smartFilterPane = filterRecord.getDataTable(COMPONENT_SMART_FILTER_STATE);

        return paneHandlers.get(smartFilterPaneType).apply(columnName, smartFilterPane);
    }


    private static DataRecord integerConverter(DataTable dataTable, String columnName) {
        DataRecord columnFilter = dataTable.addRecord();
        SimpleDataTable paneTable = new SimpleDataTable(TF_CONTROL_PANE_NUMERIC_VALUE);
        DataRecord paneRecord = paneTable.addRecord();
        paneRecord.setValue(PANE_FIELD_VALUE_PRESENCE, VALUE_PRESENCE_ALL);
        paneRecord.setValue(PANE_FIELD_EQUALS, ControlStatesFilterUtil.makeEmptyInteger());
        paneRecord.setValue(PANE_FIELD_INCLUDED_IN, ControlStatesFilterUtil.makeEmptyIntegerList());
        paneRecord.setValue(PANE_FIELD_DOES_NOT_EQUAL, ControlStatesFilterUtil.makeEmptyInteger());
        paneRecord.setValue(PANE_FIELD_GREATER_THAN, ControlStatesFilterUtil.makeEmptyInteger());
        paneRecord.setValue(PANE_FIELD_GREATER_OR_EQUAL_THAN, ControlStatesFilterUtil.makeEmptyInteger());
        paneRecord.setValue(PANE_FIELD_LESS_THAN, ControlStatesFilterUtil.makeEmptyInteger());
        paneRecord.setValue(PANE_FIELD_LESS_OR_EQUAL_THAN, ControlStatesFilterUtil.makeEmptyInteger());

        columnFilter.setValue(COMPONENT_SMART_FILTER_COLUMN, columnName);
        columnFilter.setValue(COMPONENT_SMART_FILTER_TYPE, CONTROL_PANE_NUMERIC_VALUE);
        columnFilter.setValue(COMPONENT_SMART_FILTER_STATE, paneTable);
        return columnFilter;
    }

    private static DataRecord stringConverter(DataTable dataTable, String columnName) {
        DataRecord columnFilter = dataTable.addRecord();
        SimpleDataTable paneTable = new SimpleDataTable(TF_CONTROL_PANE_STRING_VALUE);
        DataRecord paneRecord = paneTable.addRecord();
        paneRecord.setValue(PANE_FIELD_VALUE_PRESENCE, VALUE_PRESENCE_ALL);
        paneRecord.setValue(PANE_FIELD_EQUALS, null);
        paneRecord.setValue(PANE_FIELD_INCLUDED_IN, ControlStatesFilterUtil.makeEmptyStringList());
        paneRecord.setValue(PANE_FIELD_DOES_NOT_EQUAL, null);
        paneRecord.setValue(PANE_FIELD_CONTAINS, null);
        paneRecord.setValue(PANE_FIELD_DOES_NOT_CONTAIN, null);
        paneRecord.setValue(PANE_FIELD_BEGINS_WITH, null);
        paneRecord.setValue(PANE_FIELD_DOES_NOT_BEGIN_WITH, null);
        paneRecord.setValue(PANE_FIELD_ENDS_WITH, null);
        paneRecord.setValue(PANE_FIELD_DOES_NOT_END_WITH, null);

        columnFilter.setValue(COMPONENT_SMART_FILTER_COLUMN, columnName);
        columnFilter.setValue(COMPONENT_SMART_FILTER_TYPE, CONTROL_PANE_STRING_VALUE);
        columnFilter.setValue(COMPONENT_SMART_FILTER_STATE, paneTable);
        return columnFilter;
    }

    private static DataRecord booleanConverter(DataTable dataTable, String columnName) {
        DataRecord columnFilter = dataTable.addRecord();
        SimpleDataTable paneTable = new SimpleDataTable(TF_CONTROL_PANE_BOOLEAN_VALUE);
        DataRecord paneRecord = paneTable.addRecord();
        paneRecord.setValue(PANE_FIELD_VALUE_PRESENCE, VALUE_PRESENCE_ALL);
        paneRecord.setValue(PANE_FIELD_EQUALS, BOOLEAN_ALL);

        columnFilter.setValue(COMPONENT_SMART_FILTER_COLUMN, columnName);
        columnFilter.setValue(COMPONENT_SMART_FILTER_TYPE, CONTROL_PANE_BOOLEAN_VALUE);
        columnFilter.setValue(COMPONENT_SMART_FILTER_STATE, paneTable);
        return columnFilter;
    }

    private static DataRecord longConverter(DataTable dataTable, String columnName) {
        DataRecord columnFilter = dataTable.addRecord();
        SimpleDataTable paneTable = new SimpleDataTable(TF_CONTROL_PANE_NUMERIC_VALUE);
        DataRecord paneRecord = paneTable.addRecord();
        paneRecord.setValue(PANE_FIELD_VALUE_PRESENCE, VALUE_PRESENCE_ALL);
        paneRecord.setValue(PANE_FIELD_EQUALS, ControlStatesFilterUtil.makeEmptyLong());
        paneRecord.setValue(PANE_FIELD_INCLUDED_IN, ControlStatesFilterUtil.makeEmptyLongList());
        paneRecord.setValue(PANE_FIELD_DOES_NOT_EQUAL, ControlStatesFilterUtil.makeEmptyLong());
        paneRecord.setValue(PANE_FIELD_GREATER_THAN, ControlStatesFilterUtil.makeEmptyLong());
        paneRecord.setValue(PANE_FIELD_GREATER_OR_EQUAL_THAN, ControlStatesFilterUtil.makeEmptyLong());
        paneRecord.setValue(PANE_FIELD_LESS_THAN, ControlStatesFilterUtil.makeEmptyLong());
        paneRecord.setValue(PANE_FIELD_LESS_OR_EQUAL_THAN, ControlStatesFilterUtil.makeEmptyLong());

        columnFilter.setValue(COMPONENT_SMART_FILTER_COLUMN, columnName);
        columnFilter.setValue(COMPONENT_SMART_FILTER_TYPE, CONTROL_PANE_NUMERIC_VALUE);
        columnFilter.setValue(COMPONENT_SMART_FILTER_STATE, paneTable);
        return columnFilter;
    }

    private static DataRecord floatConverter(DataTable dataTable, String columnName) {
        DataRecord columnFilter = dataTable.addRecord();
        SimpleDataTable paneTable = new SimpleDataTable(TF_CONTROL_PANE_NUMERIC_VALUE);
        DataRecord paneRecord = paneTable.addRecord();
        paneRecord.setValue(PANE_FIELD_VALUE_PRESENCE, VALUE_PRESENCE_ALL);
        paneRecord.setValue(PANE_FIELD_EQUALS, ControlStatesFilterUtil.makeEmptyFloat());
        paneRecord.setValue(PANE_FIELD_INCLUDED_IN, ControlStatesFilterUtil.makeEmptyFloatList());
        paneRecord.setValue(PANE_FIELD_DOES_NOT_EQUAL, ControlStatesFilterUtil.makeEmptyFloat());
        paneRecord.setValue(PANE_FIELD_GREATER_THAN, ControlStatesFilterUtil.makeEmptyFloat());
        paneRecord.setValue(PANE_FIELD_GREATER_OR_EQUAL_THAN, ControlStatesFilterUtil.makeEmptyFloat());
        paneRecord.setValue(PANE_FIELD_LESS_THAN, ControlStatesFilterUtil.makeEmptyFloat());
        paneRecord.setValue(PANE_FIELD_LESS_OR_EQUAL_THAN, ControlStatesFilterUtil.makeEmptyFloat());

        columnFilter.setValue(COMPONENT_SMART_FILTER_COLUMN, columnName);
        columnFilter.setValue(COMPONENT_SMART_FILTER_TYPE, CONTROL_PANE_NUMERIC_VALUE);
        columnFilter.setValue(COMPONENT_SMART_FILTER_STATE, paneTable);
        return columnFilter;
    }

    private static DataRecord doubleConverter(DataTable dataTable, String columnName) {
        DataRecord columnFilter = dataTable.addRecord();
        SimpleDataTable paneTable = new SimpleDataTable(TF_CONTROL_PANE_NUMERIC_VALUE);
        DataRecord paneRecord = paneTable.addRecord();
        paneRecord.setValue(PANE_FIELD_VALUE_PRESENCE, VALUE_PRESENCE_ALL);
        paneRecord.setValue(PANE_FIELD_EQUALS, ControlStatesFilterUtil.makeEmptyDouble());
        paneRecord.setValue(PANE_FIELD_INCLUDED_IN, ControlStatesFilterUtil.makeEmptyDoubleList());
        paneRecord.setValue(PANE_FIELD_DOES_NOT_EQUAL, ControlStatesFilterUtil.makeEmptyDouble());
        paneRecord.setValue(PANE_FIELD_GREATER_THAN, ControlStatesFilterUtil.makeEmptyDouble());
        paneRecord.setValue(PANE_FIELD_GREATER_OR_EQUAL_THAN, ControlStatesFilterUtil.makeEmptyDouble());
        paneRecord.setValue(PANE_FIELD_LESS_THAN, ControlStatesFilterUtil.makeEmptyDouble());
        paneRecord.setValue(PANE_FIELD_LESS_OR_EQUAL_THAN, ControlStatesFilterUtil.makeEmptyDouble());

        columnFilter.setValue(COMPONENT_SMART_FILTER_COLUMN, columnName);
        columnFilter.setValue(COMPONENT_SMART_FILTER_TYPE, CONTROL_PANE_NUMERIC_VALUE);
        columnFilter.setValue(COMPONENT_SMART_FILTER_STATE, paneTable);
        return columnFilter;
    }

    private static DataRecord timeConverter(DataTable dataTable, String columnName)
    {
        return dateTimeConverter(dataTable, columnName, DateFieldFormat.EDITOR_TIME);
    }

    private static DataRecord dateConverter(DataTable dataTable, String columnName)
    {
        return dateTimeConverter(dataTable, columnName, DateFieldFormat.EDITOR_DATE);
    }

    private static DataRecord dateTimeConverter(DataTable dataTable, String columnName) {
        return dateTimeConverter(dataTable, columnName, null);
    }

    private static DataRecord dateTimeConverter(DataTable dataTable, String columnName, String editor)
    {
        DataRecord columnFilter = dataTable.addRecord();
        SimpleDataTable paneTable = new SimpleDataTable(TF_CONTROL_PANE_DATE_VALUE);
        DataRecord paneRecord = paneTable.addRecord();
        paneRecord.setValue(PANE_FIELD_VALUE_PRESENCE, VALUE_PRESENCE_ALL);
        paneRecord.setValue(PANE_FIELD_EQUALS, null);
        paneRecord.setValue(PANE_FIELD_INCLUDED_IN, ControlStatesFilterUtil.makeEmptyDateList());
        paneRecord.setValue(PANE_FIELD_DOES_NOT_EQUAL, null);
        paneRecord.setValue(PANE_FIELD_GREATER_THAN, null);
        paneRecord.setValue(PANE_FIELD_GREATER_OR_EQUAL_THAN, null);
        paneRecord.setValue(PANE_FIELD_LESS_THAN, null);
        paneRecord.setValue(PANE_FIELD_LESS_OR_EQUAL_THAN, null);
        paneRecord.setValue(PANE_FIELD_LAST_INTERVAL, null);
        paneRecord.setValue(PANE_FIELD_CURRENT_INTERVAL, TIME_INTERVAL_NOT_SET);
        paneRecord.setValue(PANE_FIELD_NEXT_INTERVAL, null);
        paneRecord.setValue(PANE_FIELD_DATE_TIME_RANGE, ControlStatesFilterUtil.makeEmptyRange());
        paneRecord.setValue(PANE_FIELD_EDITOR, editor);
        columnFilter.setValue(COMPONENT_SMART_FILTER_COLUMN, columnName);
        columnFilter.setValue(COMPONENT_SMART_FILTER_TYPE, CONTROL_PANE_DATE_VALUE);
        columnFilter.setValue(COMPONENT_SMART_FILTER_STATE, paneTable);
        return columnFilter;
    }

    private static DataRecord dataTableConverter(DataTable dataTable, String columnName) {
        DataRecord columnFilter = dataTable.addRecord();
        SimpleDataTable paneTable = new SimpleDataTable(TF_CONTROL_PANE_DATATABLE_VALUE);
        DataRecord paneRecord = paneTable.addRecord();
        paneRecord.setValue(PANE_FIELD_VALUE_PRESENCE, VALUE_PRESENCE_ALL);
        paneRecord.setValue(PANE_FIELD_CONTAINS, null);
        paneRecord.setValue(PANE_FIELD_DOES_NOT_CONTAIN, null);
        paneRecord.setValue(PANE_FIELD_RECORD_COUNT, null);

        columnFilter.setValue(COMPONENT_SMART_FILTER_COLUMN, columnName);
        columnFilter.setValue(COMPONENT_SMART_FILTER_TYPE, CONTROL_PANE_DATA_TABLE_VALUE);
        columnFilter.setValue(COMPONENT_SMART_FILTER_STATE, paneTable);
        return columnFilter;
    }

    private static DataRecord colorConverter(DataTable dataTable, String columnName) {
        DataRecord columnFilter = dataTable.addRecord();
        SimpleDataTable paneTable = new SimpleDataTable(TF_CONTROL_PANE_COLOR_VALUE);
        DataRecord paneRecord = paneTable.addRecord();
        paneRecord.setValue(PANE_FIELD_VALUE_PRESENCE, VALUE_PRESENCE_ALL);
        paneRecord.setValue(PANE_FIELD_EQUALS, null);
        paneRecord.setValue(PANE_FIELD_DOES_NOT_EQUAL, null);

        columnFilter.setValue(COMPONENT_SMART_FILTER_COLUMN, columnName);
        columnFilter.setValue(COMPONENT_SMART_FILTER_TYPE, CONTROL_PANE_COLOR_VALUE);
        columnFilter.setValue(COMPONENT_SMART_FILTER_STATE, paneTable);
        return columnFilter;
    }

    private static DataRecord dataConverter(DataTable dataTable, String columnName) {
        DataRecord columnFilter = dataTable.addRecord();
        SimpleDataTable paneTable = new SimpleDataTable(TF_CONTROL_PANE_DATABLOCK_VALUE);
        DataRecord paneRecord = paneTable.addRecord();
        paneRecord.setValue(PANE_FIELD_VALUE_PRESENCE, VALUE_PRESENCE_ALL);
        paneRecord.setValue(PANE_FIELD_FILE_TYPE, null);

        columnFilter.setValue(COMPONENT_SMART_FILTER_COLUMN, columnName);
        columnFilter.setValue(COMPONENT_SMART_FILTER_TYPE, CONTROL_PANE_DATA_BLOCK_VALUE);
        columnFilter.setValue(COMPONENT_SMART_FILTER_STATE, paneTable);
        return columnFilter;
    }

    private static String undefinedPaneHandler(String columnName, DataTable paneTable) {
        throw new IllegalArgumentException("Undefined pane type");
    }

    private static String numericPaneHandler(String columnName, DataTable paneTable) {
        if (paneTable == null || paneTable.getRecordCount() == 0) {
            return "";
        }
        if (!paneTable.getFormat().equals(TF_CONTROL_PANE_NUMERIC_VALUE)) {
            throw new IllegalArgumentException("Illegal numeric pane table");
        }
        StringBuilder fb = new StringBuilder();
        expandValuePresence(columnName, paneTable, fb);
        expandNumericFieldEquals(columnName, paneTable, fb);
        expandNumericFieldIn(columnName, paneTable, fb);
        expandNumericFieldNotEqual(columnName, paneTable, fb);
        expandNumericGreaterThan(columnName, paneTable, fb);
        expandNumericGreaterOrEqualThan(columnName, paneTable, fb);
        expandNumericLessThan(columnName, paneTable, fb);
        expandNumericLessOrEqualThan(columnName, paneTable, fb);
        return fb.toString();
    }

    private static String stringPaneHandler(String columnName, DataTable paneTable) {
        if (paneTable == null || paneTable.getRecordCount() == 0) {
            return "";
        }

        StringBuilder fb = new StringBuilder();
        expandValuePresence(columnName, paneTable, fb);
        expandStringFieldOperation(columnName, paneTable, fb, PANE_FIELD_EQUALS, "==");
        expandStringFieldInOperation(columnName, paneTable, fb);
        expandStringFieldOperation(columnName, paneTable, fb, PANE_FIELD_DOES_NOT_EQUAL, "!=");
        expandStringFieldFunction(columnName, paneTable, fb, PANE_FIELD_CONTAINS, FunctionOperation.FilterFunctions.CONTAINS.getName());
        expandStringFieldFunction(columnName, paneTable, fb, PANE_FIELD_DOES_NOT_CONTAIN, "!" + FunctionOperation.FilterFunctions.CONTAINS.getName());
        expandStringFieldFunction(columnName, paneTable, fb, PANE_FIELD_BEGINS_WITH, FunctionOperation.FilterFunctions.BEGINS_WITH.getName());
        expandStringFieldFunction(columnName, paneTable, fb, PANE_FIELD_DOES_NOT_BEGIN_WITH, "!" + FunctionOperation.FilterFunctions.BEGINS_WITH.getName());
        expandStringFieldFunction(columnName, paneTable, fb, PANE_FIELD_ENDS_WITH, FunctionOperation.FilterFunctions.END_WITH.getName());
        expandStringFieldFunction(columnName, paneTable, fb, PANE_FIELD_DOES_NOT_END_WITH, "!" + FunctionOperation.FilterFunctions.END_WITH.getName());
        return fb.toString();
    }

    private static String booleanPaneHandler(String columnName, DataTable paneTable) {
        if (paneTable == null || paneTable.getRecordCount() == 0) {
            return "";
        }
        if (!paneTable.getFormat().equals(TF_CONTROL_PANE_BOOLEAN_VALUE)) {
            throw new IllegalArgumentException("Illegal boolean pane table");
        }
        StringBuilder fb = new StringBuilder();
        expandValuePresence(columnName, paneTable, fb);
        expandBooleanFieldOperation(columnName, paneTable, fb, PANE_FIELD_EQUALS, "==");
        return fb.toString();
    }

    private static String colorPaneHandler(String columnName, DataTable paneTable) {
        if (paneTable == null || paneTable.getRecordCount() == 0) {
            return "";
        }
        if (!paneTable.getFormat().equals(TF_CONTROL_PANE_COLOR_VALUE)) {
            throw new IllegalArgumentException("Illegal color pane table");
        }
        StringBuilder fb = new StringBuilder();
        expandValuePresence(columnName, paneTable, fb);
        expandColorFieldOperation(columnName, paneTable, fb, PANE_FIELD_EQUALS, "==");
        expandColorFieldOperation(columnName, paneTable, fb, PANE_FIELD_DOES_NOT_EQUAL, "!=");
        return fb.toString();
    }

    private static String datePaneHandler(String columnName, DataTable paneTable) {
        if (paneTable == null || paneTable.getRecordCount() == 0) {
            return "";
        }
        if (paneTable.getFormat().getFieldCount() != TF_CONTROL_PANE_DATE_VALUE.getFieldCount()) {
            throw new IllegalArgumentException("Illegal date pane table");
        }
        StringBuilder fb = new StringBuilder();
        expandValuePresence(columnName, paneTable, fb);
        expandDateFieldOperation(columnName, paneTable, fb, PANE_FIELD_EQUALS, "==");
        expandDateFieldInOperation(columnName, paneTable, fb);
        expandDateFieldOperation(columnName, paneTable, fb, PANE_FIELD_DOES_NOT_EQUAL, "!=");
        expandDateFieldOperation(columnName, paneTable, fb, PANE_FIELD_GREATER_THAN, ">");
        expandDateFieldOperation(columnName, paneTable, fb, PANE_FIELD_GREATER_OR_EQUAL_THAN, ">=");
        expandDateFieldOperation(columnName, paneTable, fb, PANE_FIELD_LESS_THAN, "<");
        expandDateFieldOperation(columnName, paneTable, fb, PANE_FIELD_LESS_OR_EQUAL_THAN, "<=");
        expandDateLastIntervalFieldOperation(columnName, paneTable, fb);
        expandDateCurrentIntervalFieldOperation(columnName, paneTable, fb);
        expandDateNextIntervalFieldOperation(columnName, paneTable, fb);
        expandDateRangeFieldOperation(columnName, paneTable, fb);
        return fb.toString();
    }

    private static String dataTablePaneHandler(String columnName, DataTable paneTable) {
        if (paneTable == null || paneTable.getRecordCount() == 0) {
            return "";
        }
        if (!paneTable.getFormat().equals(TF_CONTROL_PANE_DATATABLE_VALUE)) {
            throw new IllegalArgumentException("Illegal dataTable pane table");
        }
        StringBuilder fb = new StringBuilder();
        expandValuePresence(columnName, paneTable, fb);
        expandDataTableEncodedFieldOperation(columnName, paneTable, fb, PANE_FIELD_CONTAINS, FunctionOperation.FilterFunctions.SIMPLE_CONTAINS.getName());
        expandDataTableEncodedFieldOperation(columnName, paneTable, fb, PANE_FIELD_DOES_NOT_CONTAIN, "!" + FunctionOperation.FilterFunctions.SIMPLE_CONTAINS.getName());
        expandDataTableRecordsCountFieldOperation(columnName, paneTable, fb);
        return fb.toString();
    }

    private static String dataBlockPaneHandler(String columnName, DataTable paneTable) {
        return "";
    }

    private static void expandValuePresence(String columnName, DataTable paneTable, StringBuilder fb) {
        if (paneTable.rec().getInt(PANE_FIELD_VALUE_PRESENCE) == VALUE_PRESENCE_SET) {
            combineByLogicAnd(fb);
            wrapSingleColumnArgument(fb, FunctionOperation.FilterFunctions.IS_NOT_NULL.getName(), columnName);
        } else if (paneTable.rec().getInt(PANE_FIELD_VALUE_PRESENCE) == VALUE_PRESENCE_NOT_SET) {
            combineByLogicAnd(fb);
            wrapSingleColumnArgument(fb, FunctionOperation.FilterFunctions.IS_NULL.getName(), columnName);
        }
    }

    private static void expandNumericFieldEquals(String columnName, DataTable paneTable, StringBuilder fb) {
        DataTable variant = paneTable.rec().getDataTable(PANE_FIELD_EQUALS);
        String strValue = extractNumeric(variant);
        if (!strValue.isEmpty()) {
            combineByLogicAnd(fb);
            wrapBinaryOperationColumn(fb, columnName, "==", strValue);
        }
    }

    private static void expandNumericFieldIn(String columnName, DataTable paneTable, StringBuilder fb) {
        DataTable inValuesList = paneTable.rec().getDataTable(PANE_FIELD_INCLUDED_IN);
        String numericList = extractNumerics(inValuesList);
        if (!numericList.isEmpty()) {
            combineByLogicAnd(fb);
            wrapFunctionWithTwoParameters(fb, FunctionOperation.FilterFunctions.IN.getName(), columnName, numericList);
        }
    }

    private static void expandNumericFieldNotEqual(String columnName, DataTable paneTable, StringBuilder fb) {
        DataTable variant = paneTable.rec().getDataTable(PANE_FIELD_DOES_NOT_EQUAL);
        String strValue = extractNumeric(variant);
        if (!strValue.isEmpty()) {
            combineByLogicAnd(fb);
            wrapBinaryOperationColumn(fb, columnName, "!=", strValue);
        }
    }

    private static void expandNumericGreaterThan(String columnName, DataTable paneTable, StringBuilder fb) {
        DataTable variant = paneTable.rec().getDataTable(PANE_FIELD_GREATER_THAN);
        String strValue = extractNumeric(variant);
        if (!strValue.isEmpty()) {
            combineByLogicAnd(fb);
            wrapBinaryOperationColumn(fb, columnName, ">", strValue);
        }
    }

    private static void expandNumericGreaterOrEqualThan(String columnName, DataTable paneTable, StringBuilder fb) {
        DataTable variant = paneTable.rec().getDataTable(PANE_FIELD_GREATER_OR_EQUAL_THAN);
        String strValue = extractNumeric(variant);
        if (!strValue.isEmpty()) {
            combineByLogicAnd(fb);
            wrapBinaryOperationColumn(fb, columnName, ">=", strValue);
        }
    }

    private static void expandNumericLessThan(String columnName, DataTable paneTable, StringBuilder fb) {
        DataTable variant = paneTable.rec().getDataTable(PANE_FIELD_LESS_THAN);
        String strValue = extractNumeric(variant);
        if (!strValue.isEmpty()) {
            combineByLogicAnd(fb);
            wrapBinaryOperationColumn(fb, columnName, "<", strValue);
        }
    }

    private static void expandNumericLessOrEqualThan(String columnName, DataTable paneTable, StringBuilder fb) {
        DataTable variant = paneTable.rec().getDataTable(PANE_FIELD_LESS_OR_EQUAL_THAN);
        String strValue = extractNumeric(variant);
        if (!strValue.isEmpty()) {
            combineByLogicAnd(fb);
            wrapBinaryOperationColumn(fb, columnName, "<=", strValue);
        }
    }

    private static void expandStringFieldOperation(String columnName, DataTable paneTable, StringBuilder fb, String paneField, String opCode) {
        String strValue = paneTable.rec().getString(paneField);
        if (strValue != null && !strValue.isEmpty()) {
            combineByLogicAnd(fb);
            wrapBinaryOperationColumn(fb, columnName, opCode, "\"" + strValue + "\"");
        }
    }

    private static void expandBooleanFieldOperation(String columnName, DataTable paneTable, StringBuilder fb, String paneField, String opCode) {
        Integer value = paneTable.rec().getInt(paneField);
        if (value != null && value != BOOLEAN_ALL) {
            combineByLogicAnd(fb);
            wrapBinaryOperationColumn(fb, columnName, opCode, ((Boolean)(value == BOOLEAN_TRUE)).toString());
        }
    }

    private static void expandStringFieldInOperation(String columnName, DataTable paneTable, StringBuilder fb) {
        DataTable inValuesList = paneTable.rec().getDataTable(PANE_FIELD_INCLUDED_IN);
        String stringList = extractStrings(inValuesList);
        if (!stringList.isEmpty()) {
            combineByLogicAnd(fb);
            wrapFunctionWithTwoParameters(fb, FunctionOperation.FilterFunctions.IN.getName(), columnName, stringList);
        }
    }

    private static void expandStringFieldFunction(String columnName, DataTable paneTable, StringBuilder fb, String paneField, String func) {
        String strValue = paneTable.rec().getString(paneField);
        if (strValue != null && !strValue.isEmpty()) {
            combineByLogicAnd(fb);
            wrapFunctionWithTwoParameters(fb, func, columnName, "\"" + strValue + "\"");
        }
    }

    private static void expandColorFieldOperation(String columnName, DataTable paneTable, StringBuilder fb, String paneField, String opCode) {
        Color color = paneTable.rec().getColor(paneField);
        if (color != null) {
            combineByLogicAnd(fb);
            wrapBinaryOperationColumn(fb, columnName, opCode, "color(" + color.getRed() + ", "
                    + color.getGreen() + ", " + color.getBlue() + ")");
        }
    }

    private static void expandDateFieldOperation(String columnName, DataTable paneTable, StringBuilder fb, String paneField, String opCode) {
        Date date = paneTable.rec().getDate(paneField);
        String editor = paneTable.rec().getString(PANE_FIELD_EDITOR);
        SimpleDateFormat simpleDateFormat = getSimpleDateFormat(editor);
        expandDateFieldOperation(columnName, fb, opCode, date, simpleDateFormat);

    }

    private static void expandDateFieldOperation(String columnName, StringBuilder fb, String opCode, Date date, SimpleDateFormat dateFormat) {
        if (date != null) {
            combineByLogicAnd(fb);
            wrapBinaryOperationColumn(fb, columnName, opCode, "\"" + dateFormat.format(date) + "\"");
        }
    }

    private static void expandDateFieldInOperation(String columnName, DataTable paneTable, StringBuilder fb) {
        DataTable inValuesList = paneTable.rec().getDataTable(PANE_FIELD_INCLUDED_IN);
        String dateList = extractDates(inValuesList);
        if (!dateList.isEmpty()) {
            combineByLogicAnd(fb);
            wrapFunctionWithTwoParameters(fb, FunctionOperation.FilterFunctions.IN.getName(), columnName, dateList);
        }
    }

    private static void expandDateLastIntervalFieldOperation(String columnName, DataTable paneTable, StringBuilder fb) {
        expandLongDateIntervalFieldOperation(columnName, paneTable, fb, false);
    }

    private static void expandDateNextIntervalFieldOperation(String columnName, DataTable paneTable, StringBuilder fb) {
        expandLongDateIntervalFieldOperation(columnName, paneTable, fb, true);
    }

    private static void expandDateRangeFieldOperation(String columnName, DataTable paneTable, StringBuilder fb) {
        DataTable dateTimeRange = paneTable.rec().getDataTable(PANE_FIELD_DATE_TIME_RANGE);
        String editor = paneTable.rec().getString(PANE_FIELD_EDITOR);
        if (dateTimeRange != null && dateTimeRange.getRecordCount() != 0)
        {
            Date startDate = dateTimeRange.rec().getDate(TIME_RANGE_START_DATE);
            Date endDate = dateTimeRange.rec().getDate(TIME_RANGE_END_DATE);
            SimpleDateFormat simpleDateFormat = getSimpleDateFormat(editor);
            expandDateFieldOperation(columnName, fb, ">=", startDate, simpleDateFormat);
            expandDateFieldOperation(columnName, fb, "<=", endDate, simpleDateFormat);
        }
    }

    private static SimpleDateFormat getSimpleDateFormat(String editor) {
        SimpleDateFormat simpleDateFormat = dateTimeFormat;
        if (editor != null) {
            if (editor.equals(DateFieldFormat.EDITOR_DATE)) {
                simpleDateFormat = dateFormat;
            } else {
                simpleDateFormat = timeFormat;
            }
        }
        return simpleDateFormat;
    }

    private static void expandDateCurrentIntervalFieldOperation(String columnName, DataTable paneTable, StringBuilder fb) {
        Integer interval = paneTable.rec().getInt(PANE_FIELD_CURRENT_INTERVAL);
        if (interval != null) {
            String func;
            switch (interval) {
                case TIME_INTERVAL_NOT_SET:
                    return;
                case TIME_INTERVAL_HOUR:
                    func = FunctionOperation.FilterFunctions.THIS_HOUR.getName();
                    break;
                case TIME_INTERVAL_DAY:
                    func = FunctionOperation.FilterFunctions.TODAY.getName();
                    break;
                case TIME_INTERVAL_WEEK:
                    func = FunctionOperation.FilterFunctions.THIS_WEEK.getName();
                    break;
                case TIME_INTERVAL_MONTH:
                    func = FunctionOperation.FilterFunctions.THIS_MONTH.getName();
                    break;
                case TIME_INTERVAL_YEAR:
                    func = FunctionOperation.FilterFunctions.THIS_YEAR.getName();
                    break;
                default:
                    throw new IllegalArgumentException("Illegal time interval");

            }
            combineByLogicAnd(fb);
            wrapSingleColumnArgument(fb, func, columnName);
        }
    }

    private static void expandDataTableEncodedFieldOperation(String columnName, DataTable paneTable, StringBuilder fb, String paneField, String funcName) {
        String value = paneTable.rec().getString(paneField);
        if (Strings.isNotBlank(value))
        {
            StringBuilder sb = new StringBuilder();
            wrapFunctionWithTwoParameters(sb, DefaultFunctions.ENCODE.getName(), columnName, Boolean.TRUE.toString());
            wrapFunctionWithTwoParametersNotBraced(fb, funcName, sb.toString(), "\"" + value + "\"");
        }
    }

    private static void expandDataTableRecordsCountFieldOperation(String columnName, DataTable paneTable, StringBuilder fb) {
        Integer recordsCount = paneTable.rec().getInt(PANE_FIELD_RECORD_COUNT);
        if (recordsCount != null) {
            combineByLogicAnd(fb);
            StringBuilder sb = new StringBuilder();
            wrapSingleColumnArgument(sb, DefaultFunctions.RECORDS.getName(), columnName);
            fb.append(sb)
              .append(" == ")
              .append(recordsCount);
        }
    }

    private static void expandLongDateIntervalFieldOperation(String columnName, DataTable paneTable, StringBuilder fb, boolean lastNext) {
        Long longInterval = paneTable.rec().getLong(lastNext ? PANE_FIELD_NEXT_INTERVAL : PANE_FIELD_LAST_INTERVAL);
        if (longInterval != null) {
            long amount;
            String func;
            if ((amount = longInterval / YEAR_IN_MS) >= 1) {
                func = lastNext ? FunctionOperation.FilterFunctions.NEXT_X_YEARS.getName() : FunctionOperation.FilterFunctions.LAST_X_YEARS.getName();
            } else if ((amount = longInterval / MONTH_IN_MS) >= 1) {
                func = lastNext ? FunctionOperation.FilterFunctions.NEXT_X_MONTHS.getName() : FunctionOperation.FilterFunctions.LAST_X_MONTHS.getName();
            } else if ((amount = longInterval / WEEK_IN_MS) >= 1) {
                func = lastNext ? FunctionOperation.FilterFunctions.NEXT_X_WEEKS.getName() : FunctionOperation.FilterFunctions.LAST_X_WEEKS.getName();
            } else if ((amount = longInterval / DAY_IN_MS) >= 1) {
                func = lastNext ? FunctionOperation.FilterFunctions.NEXT_X_DAYS.getName() : FunctionOperation.FilterFunctions.LAST_X_DAYS.getName();
            } else {
                amount = longInterval / HOUR_IN_MS;
                func = lastNext ? FunctionOperation.FilterFunctions.NEXT_X_HOURS.getName() : FunctionOperation.FilterFunctions.LAST_X_HOURS.getName();
            }
            combineByLogicAnd(fb);
            wrapFunctionWithTwoParameters(fb, func, columnName, "" + amount);
        }
    }


    private static String extractNumeric(DataTable variant) {
        if (variant == null || variant.getRecordCount() ==  0) {
            return "";
        }
        if (variant.getFieldCount() != 1) {
            throw new IllegalArgumentException("Illegal table format. 1x1 numeric table is expected");
        }
        Object value = variant.rec().getValue(0);
        if (value == null) {
            return "";
        }
        if (!(value instanceof Number)) {
            throw new IllegalArgumentException("Illegal value type. Numeric is expected");
        }
        return value.toString();
    }

    private static String extractNumerics(DataTable inValues) {
        if (inValues == null || inValues.getRecordCount() ==  0) {
            return "";
        }
        if (inValues.getFieldCount() != 1) {
            throw new IllegalArgumentException("Illegal table format. nx1 numeric table is expected");
        }
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (int i = 0; i < inValues.getRecordCount(); i++) {
            Object value = inValues.getRecord(i).getValue(0);
            if (!(value instanceof Number)) {
                throw new IllegalArgumentException("Illegal value type. Numeric is expected");
            }
            if (!first) {
                sb.append(", ");
            }
            first = false;
            sb.append(value);
        }
        return sb.toString();
    }

    private static String extractStrings(DataTable inValuesList) {
        if (inValuesList == null || inValuesList.getRecordCount() ==  0) {
            return "";
        }
        if (inValuesList.getFieldCount() != 1) {
            throw new IllegalArgumentException("Illegal table format. nx1 string table is expected");
        }
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (int i = 0; i < inValuesList.getRecordCount(); i++) {
            Object value = inValuesList.getRecord(i).getValue(0);
            if (!(value instanceof String)) {
                throw new IllegalArgumentException("Illegal value type. String is expected");
            }
            if (!first) {
                sb.append(", ");
            }
            first = false;
            sb.append('\"');
            sb.append(value);
            sb.append('\"');
        }
        return sb.toString();
    }

    private static String extractDates(DataTable inValuesList) {
        if (inValuesList == null || inValuesList.getRecordCount() ==  0) {
            return "";
        }
        if (inValuesList.getFieldCount() != 1) {
            throw new IllegalArgumentException("Illegal table format. nx1 date table is expected");
        }
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (int i = 0; i < inValuesList.getRecordCount(); i++) {
            Object value = inValuesList.getRecord(i).getValue(0);
            if (!(value instanceof Date)) {
                throw new IllegalArgumentException("Illegal value type. Date is expected");
            }
            if (!first) {
                sb.append(", ");
            }
            first = false;
            sb.append('\"');
            sb.append(dateTimeFormat.format((Date) value));
            sb.append('\"');
        }
        return sb.toString();
    }

    private static void combineByLogicAnd(StringBuilder sb) {
        if (sb.length() > 0) {
            sb.append(" && ");
        }
    }

    private static void wrapBinaryOperationColumn(StringBuilder sb, String columnName, String opCode, String value) {
        sb.append("{");
        sb.append(columnName);
        sb.append("} ");
        sb.append(opCode);
        sb.append(" ");
        sb.append(value);
    }

    private static void wrapSingleColumnArgument(StringBuilder sb, String funcName, String columnName) {
        sb.append(funcName);
        sb.append("({");
        sb.append(columnName);
        sb.append("})");
    }

    private static void wrapFunctionWithTwoParameters(StringBuilder sb, String funcName, String columnName, String value) {
        wrapFunctionWithTwoParametersNotBraced(sb, funcName, "{" + columnName + "}", value);
    }

    private static void wrapFunctionWithTwoParametersNotBraced(StringBuilder sb, String funcName, String arg1, String arg2) {
        sb.append(funcName);
        sb.append("(");
        sb.append(arg1);
        sb.append(", ");
        sb.append(arg2);
        sb.append(")");
    }


}
