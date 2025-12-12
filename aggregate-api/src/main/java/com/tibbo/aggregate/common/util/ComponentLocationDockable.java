package com.tibbo.aggregate.common.util;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.datatable.DataTable;
import com.tibbo.aggregate.common.datatable.DataTableConversion;
import com.tibbo.aggregate.common.datatable.DataTableException;
import com.tibbo.aggregate.common.datatable.FieldFormat;
import com.tibbo.aggregate.common.datatable.SimpleDataTable;
import com.tibbo.aggregate.common.datatable.TableFormat;

public class ComponentLocationDockable {

    public static final TableFormat OFT_COMPONENT_LOCATION_DOCKABLE = new TableFormat(1, 1);

    public static final String F_X                          = "x";
    public static final String F_Y                          = "y";
    public static final String F_WIDTH                      = "width";
    public static final String F_HEIGHT                     = "height";
    public static final String F_POSITION                   = "position";
    public static final String F_POSITION_ATTRIBUTE         = "positionAttribute";
    public static final String F_TAB_POSITION               = "tabPosition";
    public static final String F_TARGET_ID                  = "targetId";
    public static final String F_TAB_INDEX                  = "tabIndex";
    public static final String F_PREFFERED_WIDTH            = "preferredWidth";
    public static final String F_PREFFERED_HEIGHT           = "preferredHeight";
    public static final String F_MINIMUM_WIDTH              = "minimumWidth";
    public static final String F_MINIMUM_HEIGHT             = "minimumHeight";
    public static final String F_MOVABLE                    = "movable";
    public static final String F_CLOSABLE                   = "closable";
    public static final String F_RESIZABLE                  = "resizable";
    public static final String F_COLLAPSIBLE                = "collapsible";
    public static final String F_MAXIMIZABLE                = "maximizable";
    public static final String F_FLOATABLE                  = "floatable";
    public static final String F_COllAPSED                  = "collapsed";
    public static final String F_SHOW_HEADER                = "showHeader";
    public static final String F_DESCRIPTION                = "description";
    public static final String F_ICON                       = "icon";

    public static final String SEL_LEFT                     = "left";
    public static final String SEL_RIGHT                    = "right";
    public static final String SEL_TOP                      = "top";
    public static final String SEL_BOTTOM                   = "bottom";
    public static final String SEL_TAB                      = "tab";
    public static final String SEL_FLOAT                    = "float";
    public static final String SEL_INNER                    = "inner";
    public static final String SEL_OUTER                    = "outer";


    static {
        OFT_COMPONENT_LOCATION_DOCKABLE.addField("<" + F_X + "><I><F=N><D=" + Cres.get().getString("componentLocationX") + ">");
        OFT_COMPONENT_LOCATION_DOCKABLE.addField("<" + F_Y + "><I><F=N><D=" + Cres.get().getString("componentLocationY") + ">");
        OFT_COMPONENT_LOCATION_DOCKABLE.addField("<" + F_WIDTH + "><I><F=N><D=" + Cres.get().getString("componentLocationWidth") + ">");
        OFT_COMPONENT_LOCATION_DOCKABLE.addField("<" + F_HEIGHT + "><I><F=N><D=" + Cres.get().getString("componentLocationHeight") + ">");

        FieldFormat ff = FieldFormat.create("<" + F_POSITION + "><S><D=" + Cres.get().getString("componentLocationPosition") + ">");
        ff.addSelectionValue(SEL_LEFT);
        ff.addSelectionValue(SEL_RIGHT);
        ff.addSelectionValue(SEL_TOP);
        ff.addSelectionValue(SEL_BOTTOM);
        ff.addSelectionValue(SEL_TAB);
        ff.addSelectionValue(SEL_FLOAT);
        ff.setDefault(SEL_FLOAT);
        OFT_COMPONENT_LOCATION_DOCKABLE.addField(ff);

        ff = FieldFormat.create("<" + F_POSITION_ATTRIBUTE + "><S><D=" + Cres.get().getString("componentLocationPositionAttribute") + ">");
        ff.addSelectionValue(SEL_INNER);
        ff.addSelectionValue(SEL_OUTER);
        ff.setDefault(SEL_INNER);
        OFT_COMPONENT_LOCATION_DOCKABLE.addField(ff);

        ff = FieldFormat.create("<" + F_TAB_POSITION + "><S><F=N><D=" + Cres.get().getString("componentLocationTabPosition") + ">");
        ff.addSelectionValue(SEL_LEFT);
        ff.addSelectionValue(SEL_RIGHT);
        ff.addSelectionValue(SEL_TOP);
        ff.addSelectionValue(SEL_BOTTOM);
        ff.setDefault(SEL_TOP);
        OFT_COMPONENT_LOCATION_DOCKABLE.addField(ff);

        OFT_COMPONENT_LOCATION_DOCKABLE.addField("<" + F_TARGET_ID + "><S><F=N><D=" + Cres.get().getString("componentLocationTargetId") + ">");
        OFT_COMPONENT_LOCATION_DOCKABLE.addField("<" + F_TAB_INDEX + "><I><F=N><D=" + Cres.get().getString("componentLocationTabIndex") + ">");
        OFT_COMPONENT_LOCATION_DOCKABLE.addField("<" + F_PREFFERED_WIDTH + "><I><F=N><D=" + Cres.get().getString("componentLocationPreferredWidth") + ">");
        OFT_COMPONENT_LOCATION_DOCKABLE.addField("<" + F_PREFFERED_HEIGHT + "><I><F=N><D=" + Cres.get().getString("componentLocationPreferredHeight") + ">");
        OFT_COMPONENT_LOCATION_DOCKABLE.addField("<" + F_MINIMUM_WIDTH + "><I><F=N><D=" + Cres.get().getString("componentLocationMinimumWidth") + ">");
        OFT_COMPONENT_LOCATION_DOCKABLE.addField("<" + F_MINIMUM_HEIGHT + "><I><F=N><D=" + Cres.get().getString("componentLocationMinimumHeight") + ">");

        ff = FieldFormat.create("<" + F_MOVABLE + "><B><D=" + Cres.get().getString("componentLocationMovable") + ">");
        ff.setDefault(true);
        OFT_COMPONENT_LOCATION_DOCKABLE.addField(ff);

        ff = FieldFormat.create("<" + F_CLOSABLE + "><B><D=" + Cres.get().getString("componentLocationClosable") + ">");
        ff.setDefault(true);
        OFT_COMPONENT_LOCATION_DOCKABLE.addField(ff);

        ff = FieldFormat.create("<" + F_RESIZABLE + "><B><D=" + Cres.get().getString("componentLocationResizable") + ">");
        ff.setDefault(true);
        OFT_COMPONENT_LOCATION_DOCKABLE.addField(ff);

        ff = FieldFormat.create("<" + F_COLLAPSIBLE + "><B><D=" + Cres.get().getString("componentLocationCollapsible") + ">");
        ff.setDefault(false);
        OFT_COMPONENT_LOCATION_DOCKABLE.addField(ff);

        ff = FieldFormat.create("<" + F_MAXIMIZABLE + "><B><D=" + Cres.get().getString("componentLocationMaximizable") + ">");
        ff.setDefault(true);
        OFT_COMPONENT_LOCATION_DOCKABLE.addField(ff);

        ff = FieldFormat.create("<" + F_FLOATABLE + "><B><D=" + Cres.get().getString("componentLocationFloatable") + ">");
        ff.setDefault(true);
        OFT_COMPONENT_LOCATION_DOCKABLE.addField(ff);

        ff = FieldFormat.create("<" + F_COllAPSED + "><B><D=" + Cres.get().getString("componentLocationCollapsed") + ">");
        ff.setDefault(false);
        OFT_COMPONENT_LOCATION_DOCKABLE.addField(ff);

        ff = FieldFormat.create("<" + F_SHOW_HEADER + "><B><D=" + Cres.get().getString("componentLocationShowHeader") + ">");
        ff.setDefault(true);
        OFT_COMPONENT_LOCATION_DOCKABLE.addField(ff);

        ff = FieldFormat.create("<" + F_DESCRIPTION + "><S><F=N><D=" + Cres.get().getString("componentLocationDescription") + ">");
        OFT_COMPONENT_LOCATION_DOCKABLE.addField(ff);

        ff = FieldFormat.create("<" + F_ICON + "><T><F=N><D=" + Cres.get().getString("componentLocationIcon") + ">");
        ff.setDefault(new SimpleDataTable(ImageObject.VFT_IMAGE, true));
        OFT_COMPONENT_LOCATION_DOCKABLE.addField(ff);
    }

    private Integer x;
    private Integer y;
    private Integer width;
    private Integer height;
    private String position = SEL_FLOAT;
    private String positionAttribute = SEL_INNER;
    private String tabPosition = SEL_TOP;
    private String targetId;
    private Integer tabIndex;
    private Integer preferredWidth;
    private Integer preferredHeight;
    private Integer minimumWidth;
    private Integer minimumHeight;
    private Boolean movable = true;
    private Boolean closable = true;
    private Boolean resizable = true;
    private Boolean collapsible = false;
    private Boolean maximizable = true;
    private Boolean floatable = true;
    private Boolean collapsed = false;
    private Boolean showHeader = true;
    private String description;
    private ImageObject icon;


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

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getPositionAttribute() {
        return positionAttribute;
    }

    public void setPositionAttribute(String positionAttribute) {
        this.positionAttribute = positionAttribute;
    }

    public String getTabPosition() {
        return tabPosition;
    }

    public void setTabPosition(String tabPosition) {
        this.tabPosition = tabPosition;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public Integer getTabIndex() {
        return tabIndex;
    }

    public void setTabIndex(Integer tabIndex) {
        this.tabIndex = tabIndex;
    }

    public Integer getPreferredWidth() {
        return preferredWidth;
    }

    public void setPreferredWidth(Integer preferredWidth) {
        this.preferredWidth = preferredWidth;
    }

    public Integer getPreferredHeight() {
        return preferredHeight;
    }

    public void setPreferredHeight(Integer preferredHeight) {
        this.preferredHeight = preferredHeight;
    }

    public Integer getMinimumWidth() {
        return minimumWidth;
    }

    public void setMinimumWidth(Integer minimumWidth) {
        this.minimumWidth = minimumWidth;
    }

    public Integer getMinimumHeight() {
        return minimumHeight;
    }

    public void setMinimumHeight(Integer minimumHeight) {
        this.minimumHeight = minimumHeight;
    }

    public Boolean getMovable() {
        return movable;
    }

    public void setMovable(Boolean movable) {
        this.movable = movable;
    }

    public Boolean getClosable() {
        return closable;
    }

    public void setClosable(Boolean closable) {
        this.closable = closable;
    }

    public Boolean getResizable() {
        return resizable;
    }

    public void setResizable(Boolean resizable) {
        this.resizable = resizable;
    }

    public Boolean getCollapsible() {
        return collapsible;
    }

    public void setCollapsible(Boolean collapsible) {
        this.collapsible = collapsible;
    }

    public Boolean getMaximizable() {
        return maximizable;
    }

    public void setMaximizable(Boolean maximizable) {
        this.maximizable = maximizable;
    }

    public Boolean getFloatable() {
        return floatable;
    }

    public void setFloatable(Boolean floatable) {
        this.floatable = floatable;
    }

    public Boolean getCollapsed() {
        return collapsed;
    }

    public void setCollapsed(Boolean collapsed) {
        this.collapsed = collapsed;
    }

    public Boolean getShowHeader() {
        return showHeader;
    }

    public void setShowHeader(Boolean showHeader) {
        this.showHeader = showHeader;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ImageObject getIcon()
    {
      return icon;
    }
    
    public void setIcon(ImageObject icon)
    {
      this.icon = icon;
    }

    public DataTable toDataTable() {
        try {
            return DataTableConversion.beanToTable(this, OFT_COMPONENT_LOCATION_DOCKABLE);
        } catch (DataTableException e) {
            throw new RuntimeException(e);
        }
    }

    public static ComponentLocationDockable fromDataTable(DataTable dataTable) {
        if (dataTable == null) {
            return null;
        }

        ComponentLocationDockable componentLocation = new ComponentLocationDockable();
        if (dataTable.hasField(F_X)) componentLocation.setX(dataTable.rec().getInt(F_X));
        if (dataTable.hasField(F_Y)) componentLocation.setY(dataTable.rec().getInt(F_Y));
        if (dataTable.hasField(F_WIDTH)) componentLocation.setWidth(dataTable.rec().getInt(F_WIDTH));
        if (dataTable.hasField(F_HEIGHT)) componentLocation.setHeight(dataTable.rec().getInt(F_HEIGHT));
        if (dataTable.hasField(F_POSITION)) componentLocation.setPosition(dataTable.rec().getString(F_POSITION));
        if (dataTable.hasField(F_POSITION_ATTRIBUTE)) componentLocation.setPositionAttribute(dataTable.rec().getString(F_POSITION_ATTRIBUTE));
        if (dataTable.hasField(F_TAB_POSITION)) componentLocation.setTabPosition(dataTable.rec().getString(F_TAB_POSITION));
        if (dataTable.hasField(F_TARGET_ID)) componentLocation.setTargetId(dataTable.rec().getString(F_TARGET_ID));
        if (dataTable.hasField(F_TAB_INDEX)) componentLocation.setTabIndex(dataTable.rec().getInt(F_TAB_INDEX));
        if (dataTable.hasField(F_PREFFERED_WIDTH)) componentLocation.setPreferredWidth(dataTable.rec().getInt(F_PREFFERED_WIDTH));
        if (dataTable.hasField(F_PREFFERED_HEIGHT)) componentLocation.setPreferredHeight(dataTable.rec().getInt(F_PREFFERED_HEIGHT));
        if (dataTable.hasField(F_MINIMUM_WIDTH)) componentLocation.setMinimumWidth(dataTable.rec().getInt(F_MINIMUM_WIDTH));
        if (dataTable.hasField(F_MINIMUM_HEIGHT)) componentLocation.setMinimumHeight(dataTable.rec().getInt(F_MINIMUM_HEIGHT));
        if (dataTable.hasField(F_MOVABLE)) componentLocation.setMovable(dataTable.rec().getBoolean(F_MOVABLE));
        if (dataTable.hasField(F_CLOSABLE)) componentLocation.setClosable(dataTable.rec().getBoolean(F_CLOSABLE));
        if (dataTable.hasField(F_RESIZABLE)) componentLocation.setResizable(dataTable.rec().getBoolean(F_RESIZABLE));
        if (dataTable.hasField(F_COLLAPSIBLE)) componentLocation.setCollapsible(dataTable.rec().getBoolean(F_COLLAPSIBLE));
        if (dataTable.hasField(F_MAXIMIZABLE)) componentLocation.setMaximizable(dataTable.rec().getBoolean(F_MAXIMIZABLE));
        if (dataTable.hasField(F_FLOATABLE)) componentLocation.setFloatable(dataTable.rec().getBoolean(F_FLOATABLE));
        if (dataTable.hasField(F_COllAPSED)) componentLocation.setCollapsed(dataTable.rec().getBoolean(F_COllAPSED));
        if (dataTable.hasField(F_SHOW_HEADER)) componentLocation.setShowHeader(dataTable.rec().getBoolean(F_SHOW_HEADER));
        if (dataTable.hasField(F_DESCRIPTION)) componentLocation.setDescription(dataTable.rec().getString(F_DESCRIPTION));
        if (dataTable.hasField(F_ICON)) componentLocation.setIcon(ImageObject.fromDataTable(dataTable.rec().getDataTable(F_ICON)));

        return componentLocation;
    }
}
