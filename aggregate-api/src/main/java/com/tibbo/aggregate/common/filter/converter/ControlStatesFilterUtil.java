package com.tibbo.aggregate.common.filter.converter;

import com.tibbo.aggregate.common.datatable.DataTable;
import com.tibbo.aggregate.common.datatable.FieldFormat;
import com.tibbo.aggregate.common.datatable.SimpleDataTable;
import com.tibbo.aggregate.common.datatable.TableFormat;
import com.tibbo.aggregate.common.filter.FilterApiConstants;

public class ControlStatesFilterUtil {

    public static final String COLUMN_VALUE                 = "value";

    public static final TableFormat WRAPPED_INTEGER         = new TableFormat(1, 1);
    public static final TableFormat WRAPPED_LONG            = new TableFormat(1, 1);
    public static final TableFormat WRAPPED_FLOAT           = new TableFormat(1, 1);
    public static final TableFormat WRAPPED_DOUBLE          = new TableFormat(1, 1);
    public static final TableFormat LIST_OF_INTEGERS        = new TableFormat();
    public static final TableFormat LIST_OF_LONGS           = new TableFormat();
    public static final TableFormat LIST_OF_FLOATS          = new TableFormat();
    public static final TableFormat LIST_OF_DOUBLES         = new TableFormat();
    public static final TableFormat LIST_OF_STRINGS         = new TableFormat();
    public static final TableFormat LIST_OF_DATES           = new TableFormat();

    static {
        FieldFormat<?> integerFieldFormat = FieldFormat.create(COLUMN_VALUE, FieldFormat.INTEGER_FIELD);
        integerFieldFormat.setNullable(true);

        FieldFormat<?> longFieldFormat = FieldFormat.create(COLUMN_VALUE, FieldFormat.LONG_FIELD);
        longFieldFormat.setNullable(true);

        FieldFormat<?> floatFieldFormat = FieldFormat.create(COLUMN_VALUE, FieldFormat.FLOAT_FIELD);
        floatFieldFormat.setNullable(true);

        FieldFormat<?> doubleFieldFormat = FieldFormat.create(COLUMN_VALUE, FieldFormat.DOUBLE_FIELD);
        doubleFieldFormat.setNullable(true);

        WRAPPED_INTEGER.addField(integerFieldFormat);
        WRAPPED_LONG.addField(longFieldFormat);
        WRAPPED_FLOAT.addField(floatFieldFormat);
        WRAPPED_DOUBLE.addField(doubleFieldFormat);

        FieldFormat<?> integerVectorFieldFormat = FieldFormat.create(COLUMN_VALUE, FieldFormat.INTEGER_FIELD);
        FieldFormat<?> longVectorFieldFormat = FieldFormat.create(COLUMN_VALUE, FieldFormat.LONG_FIELD);
        FieldFormat<?> floatVectorFieldFormat = FieldFormat.create(COLUMN_VALUE, FieldFormat.FLOAT_FIELD);
        FieldFormat<?> doubleVectorFieldFormat = FieldFormat.create(COLUMN_VALUE, FieldFormat.DOUBLE_FIELD);
        FieldFormat<?> stringVectorFieldFormat = FieldFormat.create(COLUMN_VALUE, FieldFormat.STRING_FIELD);
        FieldFormat<?> dateVectorFieldFormat = FieldFormat.create(COLUMN_VALUE, FieldFormat.DATE_FIELD);

        LIST_OF_INTEGERS.addField(integerVectorFieldFormat);
        LIST_OF_LONGS.addField(longVectorFieldFormat);
        LIST_OF_FLOATS.addField(floatVectorFieldFormat);
        LIST_OF_DOUBLES.addField(doubleVectorFieldFormat);
        LIST_OF_STRINGS.addField(stringVectorFieldFormat);
        LIST_OF_DATES.addField(dateVectorFieldFormat);
    }

    public static DataTable wrapNumber(Number number) {
        SimpleDataTable dataTable;
        if (number instanceof Integer) {
            dataTable = new SimpleDataTable(WRAPPED_INTEGER);
            dataTable.addRecord(number);
        } else if (number instanceof Long) {
            dataTable = new SimpleDataTable(WRAPPED_LONG);
            dataTable.addRecord(number);
        } else if (number instanceof Float) {
            dataTable = new SimpleDataTable(WRAPPED_FLOAT);
            dataTable.addRecord(number);
        } else if (number instanceof Double) {
            dataTable = new SimpleDataTable(WRAPPED_DOUBLE);
            dataTable.addRecord(number);
        } else {
            throw new IllegalArgumentException("Illegal number type");
        }
        return dataTable;
    }

    public static Number unwrapNumber(DataTable wrappedNumber) {
        if (wrappedNumber.getRecordCount() == 0) {
            return null;
        } else if (wrappedNumber.getRecordCount() == 1) {
            Object value = wrappedNumber.rec().getValue(0);
            if (value instanceof Number) {
                return (Number) value;
            } else {
                throw new IllegalArgumentException("Illegal wrapped value. Number expected");
            }
        } else {
            throw new IllegalArgumentException("Illegal table format");
        }
    }

    public static DataTable makeEmptyInteger() {
        SimpleDataTable dataTable = new SimpleDataTable(WRAPPED_INTEGER);
        dataTable.addRecord().setValue(COLUMN_VALUE, null);
        return dataTable;
    }

    public static DataTable makeEmptyIntegerList() {
        return new SimpleDataTable(LIST_OF_INTEGERS);
    }

    public static DataTable makeEmptyLong() {
        SimpleDataTable dataTable = new SimpleDataTable(WRAPPED_LONG);
        dataTable.addRecord().setValue(COLUMN_VALUE, null);
        return dataTable;
    }

    public static DataTable makeEmptyLongList() {
        return new SimpleDataTable(LIST_OF_LONGS);
    }

    public static DataTable makeEmptyFloat() {
        SimpleDataTable dataTable = new SimpleDataTable(WRAPPED_FLOAT);
        dataTable.addRecord().setValue(COLUMN_VALUE, null);
        return dataTable;
    }

    public static DataTable makeEmptyFloatList() {
        return new SimpleDataTable(LIST_OF_FLOATS);
    }

    public static DataTable makeEmptyDouble() {
        SimpleDataTable dataTable = new SimpleDataTable(WRAPPED_DOUBLE);
        dataTable.addRecord().setValue(COLUMN_VALUE, null);
        return dataTable;
    }

    public static DataTable makeEmptyDoubleList() {
        return new SimpleDataTable(LIST_OF_DOUBLES);
    }

    public static DataTable makeEmptyStringList() {
        return new SimpleDataTable(LIST_OF_STRINGS);
    }

    public static DataTable makeEmptyDateList() {
        return new SimpleDataTable(LIST_OF_DATES);
    }

    public static DataTable makeEmptyRange() {
        return new SimpleDataTable(FilterApiConstants.DATE_RANGE_VALUE);
    }

}
