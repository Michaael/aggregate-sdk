package com.tibbo.aggregate.common.action.command;

import com.tibbo.aggregate.common.*;
import com.tibbo.aggregate.common.action.*;
import com.tibbo.aggregate.common.datatable.*;
import com.tibbo.aggregate.common.datatable.field.*;
import com.tibbo.aggregate.common.util.*;

public class GridDashboardActionCommand extends GenericActionCommand
{
  public static final String CF_DEFAULT_CONTEXT = "defaultContext";
  public static final String CF_CONTEXT_PATH = "contextPath";
  public static final String CF_LOCATION = "location";
  public static final String CF_COMPONENT_LOCATION = "componentLocation";
  public static final String CF_PARAMS = "params";

  public static TableFormat CFT_GRID_DASHBOARD = new TableFormat();
  static
  {
    CFT_GRID_DASHBOARD.addField("<" + CF_DEFAULT_CONTEXT + "><S><F=N><D=" + Cres.get().getString("conDefaultContext") + "><E=" + StringFieldFormat.EDITOR_CONTEXT + ">");
    
    FieldFormat ff = FieldFormat.create(CF_CONTEXT_PATH, FieldFormat.STRING_FIELD);
    ff.setNullable(true);
    CFT_GRID_DASHBOARD.addField(ff);
    
    ff = FieldFormat.create(CF_LOCATION, FieldFormat.DATATABLE_FIELD);
    ff.setNullable(true);
    ff.setDefault(new WebWindowLocation().toDataTable());
    CFT_GRID_DASHBOARD.addField(ff);

    CFT_GRID_DASHBOARD.addField("<" + CF_COMPONENT_LOCATION + "><T><F=NH><D=" + Cres.get().getString("componentLocation") + ">");

    ff = FieldFormat.create(CF_PARAMS, FieldFormat.DATATABLE_FIELD);
    ff.setNullable(true);
    ff.setDefault(null);
    CFT_GRID_DASHBOARD.addField(ff);
  }
  private final String contextPath;
  private final String defaultContext;
  private final WebWindowLocation location;
  
  public GridDashboardActionCommand(String type, String title, String contextPath, String defaultContext, WebWindowLocation location)
  {
    super(type, title);
    this.contextPath = contextPath;
    this.defaultContext = defaultContext;
    this.location = location;
  }
  
  public GridDashboardActionCommand(String cmdOpenGridDashboard, String title, DataTable parameters)
  {
    super(cmdOpenGridDashboard, title, parameters);
    this.contextPath = parameters.rec().getString(CF_CONTEXT_PATH);
    this.defaultContext = parameters.rec().getString(CF_DEFAULT_CONTEXT);
    this.location = parameters.rec().getDataTable(CF_LOCATION) != null ? new WebWindowLocation(parameters.rec().getDataTable(CF_LOCATION).rec()) : null;
  }

  @Override
  protected DataTable constructParameters()
  {
    DataRecord dr = new DataRecord(CFT_GRID_DASHBOARD);
    dr.setValue(CF_DEFAULT_CONTEXT, defaultContext);
    dr.setValue(CF_CONTEXT_PATH, contextPath);
    dr.setValue(CF_LOCATION, location != null ? location.toDataTable() : null);
    dr.addDataTable(getComponentLocation() != null ? getComponentLocation().toDataTable() : null);
    return dr.wrap();
  }
}
