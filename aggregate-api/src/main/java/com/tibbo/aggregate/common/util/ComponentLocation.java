package com.tibbo.aggregate.common.util;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.action.InitialRequest;
import com.tibbo.aggregate.common.action.ServerActionInput;
import com.tibbo.aggregate.common.datatable.*;

public class ComponentLocation {

    public static final TableFormat OFT_COMPONENT_LOCATION = new TableFormat(1, 1);

    public static final String F_PATH           = "path";
    public static final String F_ABSOLUTE       = "absolute";
    public static final String F_GRID           = "grid";
    public static final String F_DOCKABLE       = "dockable";

    static
    {
        FieldFormat  ff = FieldFormat.create("<" + F_PATH + "><S><F=N><D=" + Cres.get().getString("componentLocationPath") + ">");
        OFT_COMPONENT_LOCATION.addField(ff);

        ff = FieldFormat.create("<" + F_ABSOLUTE + "><T><D=" + Cres.get().getString("componentLocationAbsolute") + ">");
        ff.setDefault(new ComponentLocationAbsolute().toDataTable());
        OFT_COMPONENT_LOCATION.addField(ff);

        ff = FieldFormat.create("<" + F_GRID + "><T><D=" + Cres.get().getString("componentLocationGrid") + ">");
        ff.setDefault(new ComponentLocationGrid().toDataTable());
        OFT_COMPONENT_LOCATION.addField(ff);

        ff = FieldFormat.create("<" + F_DOCKABLE + "><T><D=" + Cres.get().getString("componentLocationDockable") + ">");
        ff.setDefault(new ComponentLocationDockable().toDataTable());
        OFT_COMPONENT_LOCATION.addField(ff);
    }

    private String path;
    private ComponentLocationAbsolute absolute;
    private ComponentLocationGrid grid;
    private ComponentLocationDockable dockable;

    public ComponentLocation() {
        path = null;
        absolute = new ComponentLocationAbsolute();
        grid = new ComponentLocationGrid();
        dockable = new ComponentLocationDockable();
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public ComponentLocationAbsolute getAbsolute() {
        return this.absolute;
    }

    public void setAbsolute(ComponentLocationAbsolute absolute) {
        if (absolute == null) {
            throw new NullPointerException("Parameter absolute is null");
        }
        this.absolute = absolute;
    }

    public ComponentLocationGrid getGrid() {
        return this.grid;
    }

    public void setGrid(ComponentLocationGrid grid) {
        if (grid == null) {
            throw new NullPointerException("Parameter grid is null");
        }
        this.grid = grid;
    }

    public ComponentLocationDockable getDockable() {
        return this.dockable;
    }

    public void setDockable(ComponentLocationDockable dockable) {
        if (dockable == null) {
            throw new NullPointerException("Parameter dockable is null");
        }
        this.dockable = dockable;
    }

    public DataTable toDataTable() {
        SimpleDataTable dt = new SimpleDataTable(OFT_COMPONENT_LOCATION);
        try {
            dt.addRecord(
                    this.path,
                    DataTableConversion.beanToTable(this.absolute, ComponentLocationAbsolute.OFT_COMPONENT_LOCATION_ABSOLUTE),
                    DataTableConversion.beanToTable(this.grid, ComponentLocationGrid.OFT_COMPONENT_LOCATION_GRID),
                    DataTableConversion.beanToTable(this.dockable, ComponentLocationDockable.OFT_COMPONENT_LOCATION_DOCKABLE)
            );
            return dt;
        } catch (DataTableException e) {
            throw new RuntimeException(e);
        }
    }

    public static ComponentLocation fromInitialRequest(InitialRequest initialRequest) {
        if (!(initialRequest instanceof ServerActionInput) ||
                ((ServerActionInput)initialRequest).getData() == null) {
            return null;
        }

        return fromDataTable(((ServerActionInput)initialRequest).getData());
    }

    public static ComponentLocation fromDataTable(DataTable dataTable) {
        if (dataTable == null) {
            return null;
        }

        ComponentLocation componentLocation = new ComponentLocation();
        if (dataTable.hasField(F_PATH)) componentLocation.setPath(dataTable.rec().getString(F_PATH));
        if (dataTable.hasField(F_ABSOLUTE)) componentLocation.setAbsolute(ComponentLocationAbsolute.fromDataTable(dataTable.rec().getDataTable(F_ABSOLUTE)));
        if (dataTable.hasField(F_GRID)) componentLocation.setGrid(ComponentLocationGrid.fromDataTable(dataTable.rec().getDataTable(F_GRID)));
        if (dataTable.hasField(F_DOCKABLE)) componentLocation.setDockable(ComponentLocationDockable.fromDataTable(dataTable.rec().getDataTable(F_DOCKABLE)));

        return componentLocation;
    }

}