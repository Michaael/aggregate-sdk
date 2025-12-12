package com.tibbo.aggregate.common.util;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.datatable.*;

public class ComponentLocationGrid {

    public static final TableFormat OFT_COMPONENT_LOCATION_GRID = new TableFormat(1, 1);

    public static final String F_ROW        = "row";
    public static final String F_COLUMN     = "column";
    public static final String F_ROWSPAN    = "rowSpan";
    public static final String F_COLUMNSPAN = "columnSpan";

    static {
        OFT_COMPONENT_LOCATION_GRID.addField("<" + F_ROW + "><I><F=N><D=" + Cres.get().getString("componentLocationRow") + ">");
        OFT_COMPONENT_LOCATION_GRID.addField("<" + F_COLUMN + "><I><F=N><D=" + Cres.get().getString("componentLocationColumn") + ">");
        OFT_COMPONENT_LOCATION_GRID.addField("<" + F_ROWSPAN + "><I><F=N><D=" + Cres.get().getString("componentLocationRowspan") + ">");
        OFT_COMPONENT_LOCATION_GRID.addField("<" + F_COLUMNSPAN + "><I><F=N><D=" + Cres.get().getString("componentLocationColumnspan") + ">");
    }

    private Integer row;
    private Integer column;
    private Integer rowSpan;
    private Integer columnSpan;

    public Integer getRow() {
        return row;
    }

    public void setRow(Integer row) {
        this.row = row;
    }

    public Integer getColumn() {
        return column;
    }

    public void setColumn(Integer column) {
        this.column = column;
    }

    public Integer getRowSpan() {
        return rowSpan;
    }

    public void setRowSpan(Integer rowSpan) {
        this.rowSpan = rowSpan;
    }

    public Integer getColumnSpan() {
        return columnSpan;
    }

    public void setColumnSpan(Integer columnSpan) {
        this.columnSpan = columnSpan;
    }

    public DataTable toDataTable() {
        try {
            return DataTableConversion.beanToTable(this, OFT_COMPONENT_LOCATION_GRID);
        } catch (DataTableException e) {
            throw new RuntimeException(e);
        }
    }

    public static ComponentLocationGrid fromDataTable(DataTable dataTable) {
        if (dataTable == null) {
            return null;
        }

        ComponentLocationGrid componentLocation = new ComponentLocationGrid();
        if (dataTable.hasField(F_ROW)) componentLocation.setRow(dataTable.rec().getInt(F_ROW));
        if (dataTable.hasField(F_COLUMN)) componentLocation.setColumn(dataTable.rec().getInt(F_COLUMN));
        if (dataTable.hasField(F_ROWSPAN)) componentLocation.setRowSpan(dataTable.rec().getInt(F_ROWSPAN));
        if (dataTable.hasField(F_COLUMNSPAN)) componentLocation.setColumnSpan(dataTable.rec().getInt(F_COLUMNSPAN));

        return componentLocation;
    }
}
