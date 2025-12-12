package com.tibbo.aggregate.common.datatable.encoding;

import com.tibbo.aggregate.common.datatable.TableFormat;

public interface FormatCache {

    void put(int index, TableFormat format);
    
    TableFormat get(int id);
    
    void clear();
    
    Integer addIfNotExists(TableFormat tableFormat);
    
    TableFormat getCachedVersion(TableFormat format);
    
    String getName();
}
