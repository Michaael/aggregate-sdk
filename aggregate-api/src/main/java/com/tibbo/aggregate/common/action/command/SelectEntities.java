package com.tibbo.aggregate.common.action.command;

import java.util.*;

import com.tibbo.aggregate.common.*;
import com.tibbo.aggregate.common.action.*;
import com.tibbo.aggregate.common.datatable.*;

public class SelectEntities extends GenericActionCommand
{
  public static final String CF_TYPES = "types";
  public static final String CF_ROOT = "root";
  public static final String CF_DEFAULT = "default";
  public static final String CF_EXPANDED = "expanded";
  public static final String CF_SHOW_CHILDREN = "showChildren";
  public static final String CF_ALLOW_MASKS = "allowMasks";
  public static final String CF_SHOW_VARS = "showVars";
  public static final String CF_SHOW_FUNCS = "showFuncs";
  public static final String CF_SHOW_EVENTS = "showEvents";
  public static final String CF_SHOW_FIELDS = "showFields";
  public static final String CF_SINGLE_SELECTION = "singleSelection";
  public static final String CF_USE_CHECKBOXES = "useCheckboxes";

  public static final String CF_TYPES_TYPE = "type";
  
  public static final String RF_REFERENCE = "reference";
  
  private static final TableFormat CFT_SELECT_ENTITIES_TYPES = FieldFormat.create("<" + CF_TYPES_TYPE + "><S>").wrap();
  
  public static final TableFormat CFT_SELECT_ENTITIES = new TableFormat(1, 1);
  static
  {
    FieldFormat<Object> ff = FieldFormat.create("<" + CF_TYPES + "><T><F=N><D="+ Cres.get().getString("conContextTypes")+">");
    ff.setDefault(new SimpleDataTable(CFT_SELECT_ENTITIES_TYPES)).setDefaultOverride(true);
    CFT_SELECT_ENTITIES.addField(ff);

    CFT_SELECT_ENTITIES.addField("<" + CF_ROOT + "><S><D="+ Cres.get().getString("root")+">");
    CFT_SELECT_ENTITIES.addField("<" + CF_DEFAULT + "><S><F=N><D="+ Cres.get().getString("conDefaultContext")+">");
    CFT_SELECT_ENTITIES.addField("<" + CF_EXPANDED + "><S><F=N><D="+ Cres.get().getString("acExpandedContext")+">");
    CFT_SELECT_ENTITIES.addField("<" + CF_SHOW_CHILDREN + "><B><D="+ Cres.get().getString("acShowChildren")+">");
    CFT_SELECT_ENTITIES.addField("<" + CF_ALLOW_MASKS + "><B><D="+ Cres.get().getString("acAllowMasks")+">");
    CFT_SELECT_ENTITIES.addField("<" + CF_SHOW_VARS + "><B><D="+ Cres.get().getString("acShowVariables")+">");
    CFT_SELECT_ENTITIES.addField("<" + CF_SHOW_FUNCS + "><B><D="+ Cres.get().getString("acShowFunctions")+">");
    CFT_SELECT_ENTITIES.addField("<" + CF_SHOW_EVENTS + "><B><D="+ Cres.get().getString("acShowEvents")+">");
    CFT_SELECT_ENTITIES.addField("<" + CF_SHOW_FIELDS + "><B><D="+ Cres.get().getString("acShowFields")+">");
    CFT_SELECT_ENTITIES.addField("<" + CF_SINGLE_SELECTION + "><B><D="+ Cres.get().getString("acSingleSelection")+">");
    CFT_SELECT_ENTITIES.addField("<" + CF_USE_CHECKBOXES + "><B><D="+ Cres.get().getString("acUseCheckboxes")+">");
    CFT_SELECT_ENTITIES.addField("<" + CF_COMPONENT_LOCATION + "><T><F=NH><D=" + Cres.get().getString("componentLocation") + ">");
  }
  
  public static final TableFormat RFT_SELECT_ENTITIES = FieldFormat.create("<" + RF_REFERENCE + "><S>").wrap();
  
  private Collection<String> contextTypes;
  private String rootContext;
  private String defaultContext;
  private String expandedContext;
  private boolean showChildren;
  private boolean allowMasks;
  private boolean showVars;
  private boolean showFuncs;
  private boolean showEvents;
  private boolean showFields;
  private boolean singleSelection;
  private boolean useCheckboxes;

  public SelectEntities()
  {
    super(ActionUtils.CMD_SELECT_ENTITIES, CFT_SELECT_ENTITIES, RFT_SELECT_ENTITIES);
  }
  
  public SelectEntities(String title, DataTable parameters)
  {
    super(ActionUtils.CMD_SELECT_ENTITIES, title, parameters);
  }
  
  public SelectEntities(String title, Collection<String> contextTypes, String rootContext, String defaultContext, String expandedContext, boolean showChildren, boolean allowMasks, boolean showVars,
      boolean showFuncs, boolean showEvents, boolean showFields, boolean singleSelection, boolean useCheckboxes)
  {
    super(ActionUtils.CMD_SELECT_ENTITIES, title);
    this.contextTypes = contextTypes;
    this.rootContext = rootContext;
    this.defaultContext = defaultContext;
    this.expandedContext = expandedContext;
    this.showChildren = showChildren;
    this.allowMasks = allowMasks;
    this.showVars = showVars;
    this.showFuncs = showFuncs;
    this.showEvents = showEvents;
    this.showFields = showFields;
    this.singleSelection = singleSelection;
    this.useCheckboxes = useCheckboxes;
  }
  
  @Override
  protected DataTable constructParameters()
  {
    DataTable types = null;
    
    if (contextTypes != null)
    {
      types = new SimpleDataTable(CFT_SELECT_ENTITIES_TYPES);
      
      for (String type : contextTypes)
      {
        types.addRecord().addString(type);
      }
    }
    
    return new SimpleDataTable(CFT_SELECT_ENTITIES, types, rootContext, defaultContext, expandedContext, showChildren, allowMasks, showVars, showFuncs, showEvents, showFields, singleSelection, useCheckboxes,
            getComponentLocation() != null ? getComponentLocation().toDataTable() : null);
  }
  
  public Collection<String> getContextTypes()
  {
    return contextTypes;
  }
  
  public void setContextTypes(Collection<String> contextTypes)
  {
    this.contextTypes = contextTypes;
  }
  
  public String getRootContext()
  {
    return rootContext;
  }
  
  public void setRootContext(String rootContext)
  {
    this.rootContext = rootContext;
  }
  
  public String getDefaultContext()
  {
    return defaultContext;
  }
  
  public void setDefaultContext(String defaultContext)
  {
    this.defaultContext = defaultContext;
  }
  
  public String getExpandedContext()
  {
    return expandedContext;
  }
  
  public void setExpandedContext(String expandedContext)
  {
    this.expandedContext = expandedContext;
  }
  
  public boolean isShowChildren()
  {
    return showChildren;
  }
  
  public void setShowChildren(boolean showChildren)
  {
    this.showChildren = showChildren;
  }
  
  public boolean isAllowMasks()
  {
    return allowMasks;
  }
  
  public void setAllowMasks(boolean allowMasks)
  {
    this.allowMasks = allowMasks;
  }
  
  public boolean isShowVars()
  {
    return showVars;
  }
  
  public void setShowVars(boolean showVars)
  {
    this.showVars = showVars;
  }
  
  public boolean isShowFuncs()
  {
    return showFuncs;
  }
  
  public void setShowFuncs(boolean showFuncs)
  {
    this.showFuncs = showFuncs;
  }
  
  public boolean isShowEvents()
  {
    return showEvents;
  }
  
  public void setShowEvents(boolean showEvents)
  {
    this.showEvents = showEvents;
  }
  
  public boolean isShowFields()
  {
    return showFields;
  }
  
  public void setShowFields(boolean showFields)
  {
    this.showFields = showFields;
  }
  
  public boolean isSingleSelection()
  {
    return singleSelection;
  }
  
  public void setSingleSelection(boolean singleSelection)
  {
    this.singleSelection = singleSelection;
  }

  public boolean isUseCheckboxes() {
    return useCheckboxes;
  }

  public void setUseCheckboxes(boolean useCheckboxes) {
    this.useCheckboxes = useCheckboxes;
  }
}
