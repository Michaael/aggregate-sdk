package com.tibbo.aggregate.common.util;

import com.tibbo.aggregate.common.*;
import com.tibbo.aggregate.common.datatable.*;

public class DashboardsHierarchyInfo extends AggreGateBean
{
  public static final String FIELD_NESTED_DASHBOARDS_PROPERTIES = "nestedDashboardsProperties";
  public static final String FIELD_NESTED_DASHBOARDS_LOCATIONS = "nestedDashboardsLocations";
  
  public static TableFormat DASHBOARD_PROPERTIES_FORMAT = DashboardProperties.FORMAT.clone();
  static
  {
    DASHBOARD_PROPERTIES_FORMAT.setMaxRecords(Integer.MAX_VALUE);
  };
  
  public static TableFormat LOCATIONS_FORMAT = WindowLocation.FORMAT.clone();
  static
  {
    LOCATIONS_FORMAT.setMaxRecords(Integer.MAX_VALUE);
  };
  
  public static TableFormat FORMAT = new TableFormat(1, 1);
  static
  {
    FieldFormat ff = FieldFormat.create(FIELD_NESTED_DASHBOARDS_PROPERTIES, FieldFormat.DATATABLE_FIELD, Cres.get().getString("nestedDashboardsProperties")).setNullable(true);
    ff.setDefault(new SimpleDataTable(DASHBOARD_PROPERTIES_FORMAT));
    FORMAT.addField(ff);
    ff = FieldFormat.create(FIELD_NESTED_DASHBOARDS_LOCATIONS, FieldFormat.DATATABLE_FIELD, Cres.get().getString("nestedDashboardsLocations")).setNullable(true);
    ff.setDefault(new SimpleDataTable(LOCATIONS_FORMAT));
    FORMAT.addField(ff);
  }
  
  private DataTable nestedDashboardsProperties;
  private DataTable nestedDashboardsLocations;
  
  public DashboardsHierarchyInfo()
  {
    super(FORMAT);
  }
  
  public DashboardsHierarchyInfo(DataRecord data)
  {
    super(FORMAT, data);
  }
  
  public DataTable getNestedDashboardsProperties()
  {
    return nestedDashboardsProperties;
  }
  
  public void setNestedDashboardsProperties(DataTable nestedDashboardsProperties)
  {
    this.nestedDashboardsProperties = nestedDashboardsProperties;
  }
  
  public DataTable getNestedDashboardsLocations()
  {
    return nestedDashboardsLocations;
  }
  
  public void setNestedDashboardsLocations(DataTable nestedDashboardsLocations)
  {
    this.nestedDashboardsLocations = nestedDashboardsLocations;
  }
  
  public void addLocation(DataTable locationData)
  {
    nestedDashboardsLocations.addRecord(locationData.rec());
  }
  
  public void addDashboardProperty(DataTable dashboardData)
  {
    nestedDashboardsProperties.addRecord(dashboardData.rec());
  }
  
  public WindowLocation getLocation(int i)
  {
    if (nestedDashboardsLocations.getRecordCount() > i)
    {
      return new WindowLocation(nestedDashboardsLocations.getRecord(i));
    }
    else
    {
      return null;
    }
  }
  
  public DashboardProperties getDashboardProperty(int i)
  {
    if (nestedDashboardsProperties.getRecordCount() > i)
    {
      return new DashboardProperties(nestedDashboardsProperties.getRecord(i));
    }
    else
    {
      return null;
    }
  }
  
  public DashboardProperties getLastDashboardProperties()
  {
    int recCount = nestedDashboardsProperties.getRecordCount();
    if (recCount > 0)
    {
      return new DashboardProperties(nestedDashboardsProperties.getRecord(recCount - 1));
    }
    
    return new DashboardProperties(); // TODO
  }
}
