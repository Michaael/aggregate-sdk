package com.tibbo.aggregate.common.filter;

import com.tibbo.aggregate.common.datatable.DataRecord;
import com.tibbo.aggregate.common.datatable.TableFormat;

import java.util.function.Supplier;

public interface DataSupplier {

    Supplier<DataRecord> getDataRecordSupplier();

    TableFormat getTableFormat();
}
