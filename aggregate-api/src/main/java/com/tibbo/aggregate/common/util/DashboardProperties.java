package com.tibbo.aggregate.common.util;

import com.tibbo.aggregate.common.*;
import com.tibbo.aggregate.common.datatable.*;
import com.tibbo.aggregate.common.datatable.validator.*;

public class DashboardProperties extends AggreGateBean
{
  public static final String FIELD_NAME = "name";
  public static final String FIELD_DESCRIPTION = "description";
  public static final String FIELD_LAYOUT = "layout";
  public static final String FIELD_COLUMNS = "columns";
  public static final String FIELD_CLOSABLE = "closable";
  public static final String FIELD_CLEANUP = "cleanup";
  public static final String FIELD_STORAGE_CONTEXT = "storageContext";
  public static final String FIELD_STORAGE_CLASS = "storageClass";
  public static final String FIELD_STORAGE_SESSION_ID = "storageSessionId";
  public static final String FIELD_DEFAULT_EVENT = "defaultEvent";
  public static final String FIELD_STORAGE_INSTANCE_ID = "storageInstanceId";
  public static final String FIELD_DASHBOARD_CONTEXT = "dashboardContext";
  public static final String FIELD_ELEMENT_ID = "elementId";
  public static final String FIELD_PARENT_ELEMENT_ID = "parentElementId";
  public static final String FIELD_CLOSE_DASHBOARD_ON_REOPEN = "closeDashboardOnReopen";
  
  public static final int LAYOUT_DOCKABLE = 0;
  public static final int LAYOUT_SCROLLABLE = 1;
  public static final int LAYOUT_GRID = 2;
  public static final int LAYOUT_ABSOLUTE = 3;
  // The Dockable layout on the Web is not the same thing as on the Desktop.
  public static final int LAYOUT_DOCKABLE_WEB = 4;

  public static final int DESTINATION_WEB = 0;
  public static final int DESTINATION_DESKTOP = 1;
  
  public static TableFormat FORMAT = new TableFormat(1, 1);
  static
  {
    FieldFormat ff = FieldFormat.create("<" + FIELD_NAME + "><S><F=N><D=" + Cres.get().getString("name") + ">");
    FORMAT.addField(ff);
    
    ff = FieldFormat.create("<" + FIELD_DESCRIPTION + "><S><F=N><D=" + Cres.get().getString("description") + ">");
    ff.addValidator(ValidatorHelper.DESCRIPTION_LENGTH_VALIDATOR);
    ff.addValidator(ValidatorHelper.DESCRIPTION_SYNTAX_VALIDATOR);
    FORMAT.addField(ff);
    
    ff = FieldFormat.create("<" + FIELD_LAYOUT + "><I><A=" + LAYOUT_DOCKABLE + "><D=" + Cres.get().getString("layout") + ">");
    FORMAT.addField(ff);
    
    ff = FieldFormat.create("<" + FIELD_COLUMNS + "><I><A=3><D=" + Cres.get().getString("columns") + ">");
    FORMAT.addField(ff);
    
    FORMAT.addField(FieldFormat.create("<" + FIELD_CLOSABLE + "><B><A=1><D=" + Cres.get().getString("clDashboardClosable") + ">"));
    
    FORMAT.addField(FieldFormat.create("<" + FIELD_CLOSE_DASHBOARD_ON_REOPEN + "><B><D=" + Cres.get().getString("clCloseDashboardOnReopen") + ">").setDefault(false));
    
    FORMAT.addField(FieldFormat.create("<" + FIELD_CLEANUP + "><B><F=H>"));
    
    FORMAT.addField(FieldFormat.create(FIELD_STORAGE_CONTEXT, FieldFormat.STRING_FIELD, Cres.get().getString("storageContext")).setNullable(true));
    FORMAT.addField(FieldFormat.create(FIELD_STORAGE_CLASS, FieldFormat.STRING_FIELD).setNullable(true).setHidden(true));
    FORMAT.addField(FieldFormat.create(FIELD_STORAGE_SESSION_ID, FieldFormat.LONG_FIELD).setNullable(true).setHidden(true));
    
    FORMAT.addField(FieldFormat.create(FIELD_DEFAULT_EVENT, FieldFormat.STRING_FIELD).setNullable(true).setHidden(true));
    
    FORMAT.addField(FieldFormat.create(FIELD_DASHBOARD_CONTEXT, FieldFormat.STRING_FIELD).setNullable(true).setHidden(false));
    
    FORMAT.addField(FieldFormat.create(FIELD_ELEMENT_ID, FieldFormat.STRING_FIELD).setNullable(true).setHidden(false));
    
    FORMAT.addField(FieldFormat.create(FIELD_PARENT_ELEMENT_ID, FieldFormat.STRING_FIELD).setNullable(true).setHidden(false));
    
    FORMAT.setNamingExpression("{" + FIELD_DESCRIPTION + "} != null ? {" + FIELD_DESCRIPTION + "} : ({" + FIELD_NAME + "} != null ? {" + FIELD_NAME + "} : '')");
  }
  
  private String name;
  private String description;
  private int destination;
  private int layout;
  private int columns;
  private boolean closable;
  private boolean cleanup;
  private String storageContext;
  private String storageClass;
  private Long storageSessionId;
  private String defaultEvent;
  private String dashboardContext;
  private String elementId;
  private String parentElementId;
  private boolean closeDashboardOnReopen;
  
  public boolean isCloseDashboardOnReopen()
  {
    return closeDashboardOnReopen;
  }
  
  public void setCloseDashboardOnReopen(boolean closeDashboardOnReopen)
  {
    this.closeDashboardOnReopen = closeDashboardOnReopen;
  }
  
  public DashboardProperties()
  {
    super(FORMAT);
  }
  
  public DashboardProperties(DataRecord data)
  {
    super(FORMAT, data);
  }
  
  public DashboardProperties(String name, String description)
  {
    this();
    this.name = name;
    this.description = description;
  }
  
  public DashboardProperties(String name, String description, int layout)
  {
    this(name, description);
    this.layout = layout;
  }
  
  public String getName()
  {
    return name;
  }
  
  public void setName(String name)
  {
    this.name = name;
  }
  
  public String getDescription()
  {
    return description;
  }
  
  public void setDescription(String description)
  {
    this.description = description;
  }
  
  public int getDestination()
  {
    return destination;
  }
  
  public void setDestination(int destination)
  {
    this.destination = destination;
  }
  
  public int getLayout()
  {
    return layout;
  }
  
  public void setLayout(int layout)
  {
    this.layout = layout;
  }
  
  public int getColumns()
  {
    return columns;
  }
  
  public void setColumns(int columns)
  {
    this.columns = columns;
  }
  
  public void setClosable(boolean closable)
  {
    this.closable = closable;
  }
  
  public boolean isCleanup()
  {
    return cleanup;
  }
  
  public void setCleanup(boolean cleanup)
  {
    this.cleanup = cleanup;
  }
  
  public boolean isClosable()
  {
    return closable;
  }
  
  @Override
  public String toString()
  {
    return "Dashboard [name=" + name + ", description=" + description + ", id=" + elementId + ", parentId=" + parentElementId + ", layout=" + layout + "]";
  }
  
  public String getStorageContext()
  {
    return storageContext;
  }
  
  public void setStorageContext(String storageContext)
  {
    this.storageContext = storageContext;
  }
  
  public String getStorageClass()
  {
    return storageClass;
  }
  
  public void setStorageClass(String className)
  {
    this.storageClass = className;
  }
  
  public Long getStorageSessionId()
  {
    return storageSessionId;
  }
  
  public void setStorageSessionId(Long storageSessionId)
  {
    this.storageSessionId = storageSessionId;
  }
  
  public static String getFieldStorageSessionId()
  {
    return FIELD_STORAGE_SESSION_ID;
  }
  
  public String getDefaultEvent()
  {
    return defaultEvent;
  }
  
  public void setDefaultEvent(String defaultEvent)
  {
    this.defaultEvent = defaultEvent;
  }
  
  public String getDashboardContext()
  {
    return dashboardContext;
  }
  
  public void setDashboardContext(String dashboardContextString)
  {
    dashboardContext = dashboardContextString;
  }
  
  public String getElementId()
  {
    return elementId;
  }
  
  public void setElementId(String elementIdString)
  {
    elementId = elementIdString;
  }
  
  public String getParentElementId()
  {
    return parentElementId;
  }
  
  public void setParentElementId(String parentElementId)
  {
    this.parentElementId = parentElementId;
  }
  
  public DashboardProperties copy()
  {
    return new DashboardProperties(this.toDataRecord());
  }
  
  public DashboardProperties copyWithNameAndDescription(String nameString, String descriptionString)
  {
    DashboardProperties dp = this.copy();
    dp.setName(nameString);
    dp.setDescription(descriptionString);
    return dp;
  }
  
}
