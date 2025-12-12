package com.tibbo.aggregate.common.util;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.datatable.*;

public class ComponentLocationAbsolute {

    public static final TableFormat OFT_COMPONENT_LOCATION_ABSOLUTE = new TableFormat(1, 1);

    public static final String F_X          = "x";
    public static final String F_Y          = "y";
    public static final String F_WIDTH      = "width";
    public static final String F_HEIGHT     = "height";
    public static final String F_ZINDEX     = "zIndex";


    static {
        OFT_COMPONENT_LOCATION_ABSOLUTE.addField("<" + F_X + "><I><F=N><D=" + Cres.get().getString("componentLocationX") + ">");
        OFT_COMPONENT_LOCATION_ABSOLUTE.addField("<" + F_Y + "><I><F=N><D=" + Cres.get().getString("componentLocationY") + ">");
        OFT_COMPONENT_LOCATION_ABSOLUTE.addField("<" + F_WIDTH + "><I><F=N><D=" + Cres.get().getString("componentLocationWidth") + ">");
        OFT_COMPONENT_LOCATION_ABSOLUTE.addField("<" + F_HEIGHT + "><I><F=N><D=" + Cres.get().getString("componentLocationHeight") + ">");
        OFT_COMPONENT_LOCATION_ABSOLUTE.addField("<" + F_ZINDEX + "><I><F=N><D=" + Cres.get().getString("componentLocationZIndex") + ">");
    }

    private Integer x;
    private Integer y;
    private Integer width;
    private Integer height;
    private Integer zIndex;

    public Integer getX() {
        return x;
    }

    public void setX(Integer x) {
        this.x = x;
    }

    public Integer getY() {
        return y;
    }

    public void setY(Integer y) {
        this.y = y;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Integer getzIndex() {
        return zIndex;
    }

    public void setzIndex(Integer zIndex) {
        this.zIndex = zIndex;
    }

    public DataTable toDataTable() {
        try {
            return DataTableConversion.beanToTable(this, OFT_COMPONENT_LOCATION_ABSOLUTE);
        } catch (DataTableException e) {
            throw new RuntimeException(e);
        }
    }

    public static ComponentLocationAbsolute fromDataTable(DataTable dataTable) {
        if (dataTable == null) {
            return null;
        }

        ComponentLocationAbsolute componentLocation = new ComponentLocationAbsolute();
        if (dataTable.hasField(F_X)) componentLocation.setX(dataTable.rec().getInt(F_X));
        if (dataTable.hasField(F_Y)) componentLocation.setY(dataTable.rec().getInt(F_Y));
        if (dataTable.hasField(F_WIDTH)) componentLocation.setWidth(dataTable.rec().getInt(F_WIDTH));
        if (dataTable.hasField(F_HEIGHT)) componentLocation.setHeight(dataTable.rec().getInt(F_HEIGHT));
        if (dataTable.hasField(F_ZINDEX)) componentLocation.setzIndex(dataTable.rec().getInt(F_ZINDEX));

        return componentLocation;
    }
}
