package com.tibbo.aggregate.common.context;

import com.tibbo.aggregate.common.*;
import com.tibbo.aggregate.common.datatable.*;
import com.tibbo.aggregate.common.datatable.field.*;

public class VariableInfo
{
  public static final TableFormat FORMAT = new TableFormat(1, 1);
  static
  {
    FORMAT.addField("<name><S><D=" + Cres.get().getString("name") + ">");
    FORMAT.addField("<description><S><D=" + Cres.get().getString("description") + ">");
    FORMAT.addField("<format><T><F=N><D=" + Cres.get().getString("format") + ">");
    FORMAT.addField("<readable><B><D=" + Cres.get().getString("acReadable") + ">");
    FORMAT.addField("<writable><B><D=" + Cres.get().getString("acWritable") + ">");
    FORMAT.addField("<help><S><F=N><D=" + Cres.get().getString("help") + "><E=" + StringFieldFormat.EDITOR_TEXT_AREA + ">");
    FORMAT.addField("<group><S><F=N><D=" + Cres.get().getString("group") + ">");
  }
  
  public static DataTable createInfoTable(VariableDefinition vd)
  {
    DataRecord res = new DataRecord(FORMAT);
    res.addString(vd.getName());
    res.addString(vd.getDescription());
    res.addDataTable(vd.getFormat() != null ? DataTableBuilding.formatToFieldsTable(vd.getFormat(), false, null, false) : null);
    res.addBoolean(vd.isReadable());
    res.addBoolean(vd.isWritable());
    res.addString(vd.getHelp());
    res.addString(vd.getGroup());
    
    return res.wrap();
  }
}
