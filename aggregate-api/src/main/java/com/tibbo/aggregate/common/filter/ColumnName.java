package com.tibbo.aggregate.common.filter;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.datatable.DataRecord;
import com.tibbo.aggregate.common.datatable.FieldFormat;
import com.tibbo.aggregate.common.datatable.TableFormat;

public final class ColumnName extends Identifier {

    private DataSupplier dataSupplier;
    private FieldFormat<?> fieldFormat;

    public ColumnName(String name) {
        super(name);
    }

    @Override
    protected Object fetchValue() {
        if (dataSupplier == null) {
            throw new IllegalStateException("Data supplier is not set");
        }

        DataRecord dr = dataSupplier.getDataRecordSupplier().get();
        return dr.getValue(getName());
    }

    public void setDataSupplier(DataSupplier dataSupplier) {
        this.dataSupplier = dataSupplier;
    }

    public DataSupplier getDataSupplier() {
        return dataSupplier;
    }

    public FieldFormat<?> getFieldFormat() {
        return fieldFormat;
    }

    public void bind(TableFormat tableFormat) {
        fieldFormat = tableFormat.getFields().stream()
                .filter(ff -> !ff.isHidden())
                .filter(ff -> ff.getName().equalsIgnoreCase(getName()))
                .findAny()
                .orElse(null);

        if (fieldFormat == null) {
            throw new SmartFilterDataBindingException(getName(), Cres.get().getString("smartFilterUnknownColumnError") + ": " + getName());
        }
    }
}

