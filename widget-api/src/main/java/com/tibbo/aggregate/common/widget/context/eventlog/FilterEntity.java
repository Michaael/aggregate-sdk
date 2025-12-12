package com.tibbo.aggregate.common.widget.context.eventlog;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.datatable.AggreGateBean;
import com.tibbo.aggregate.common.datatable.TableFormat;

public class FilterEntity extends AggreGateBean {

    public static final String FIELD_CONTEXT = "context";
    public static final String FIELD_DESCRIPTION = "description";
    public static final String FIELD_DEFAULT_FILTER = "defaultFilter";

    public static TableFormat FORMAT = new TableFormat();

    static {
        FORMAT.addField("<" + FIELD_CONTEXT + "><S><D=" + Cres.get().getString("context") + ">");
        FORMAT.addField(
                "<" + FIELD_DESCRIPTION + "><S><D=" + Cres.get().getString("description") + ">"
        );
        FORMAT.addField(
                "<" + FIELD_DEFAULT_FILTER + "><B><D=" + Cres.get().getString("efDefaultFilter") + ">"
        );
    }

    private String context;
    private String description;
    private Boolean defaultFilter;

    public FilterEntity() {
        super(FORMAT);
    }

    public FilterEntity(String context, String description, Boolean defaultFilter)
    {
        this();
        this.context = context;
        this.description = description;
        this.defaultFilter = defaultFilter;
    }


    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getDefaultFilter() {
        return defaultFilter;
    }

    public void setDefaultFilter(Boolean defaultFilter) {
        this.defaultFilter = defaultFilter;
    }
}
