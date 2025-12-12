package com.tibbo.aggregate.common.event;

public class EventSortDirective
{
    private String field;
    private boolean tablefield;
    private boolean ascending;

    public EventSortDirective(String column, boolean tablefield, boolean ascending)
    {
        this.field = column;
        this.tablefield = tablefield;
        this.ascending = ascending;
    }

    public String getField()
    {
        return field;
    }

    public boolean isAscending()
    {
        return ascending;
    }

    public boolean isTablefield()
    {
        return tablefield;
    }

    public void setAscending(boolean ascending)
    {
        this.ascending = ascending;
    }

}
