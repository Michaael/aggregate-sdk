package com.tibbo.aggregate.common.action.command;

import java.util.*;

import com.tibbo.aggregate.common.*;
import com.tibbo.aggregate.common.action.*;
import com.tibbo.aggregate.common.context.*;
import com.tibbo.aggregate.common.datatable.*;
import com.tibbo.aggregate.common.datatable.field.*;
import com.tibbo.aggregate.common.util.*;

public class ShowSystemTree extends GenericActionCommand
{
  public static final String CF_ROOT = "root";
  public static final String CF_ROOTS = "roots";
  public static final String CF_LOCATION = "location";
  public static final String CF_DASHBOARD = "dashboard";
  public static final String CF_KEY = "key";
  public static final String CF_RELATED_ACTIONS = "relatedActions";
  public static final String CF_CONTEXT_MENU = "contextMenu";
  public static final String CF_SHOW_TOOLBAR = "showToolbar";
  public static final String CF_NODE_CLICK_EXPRESSION = "nodeClickExpression";
  public static final String CF_NODE_FILTER_EXPRESSION = "nodeFilterExpression";
  
  public static final String CF_DASHBOARDS_HIERARCHY_INFO = "dashboardsHierarchyInfo";
  
  public static final String CF_ROOTS_ROOT = "root";
  public static final String CF_COMPONENT_LOCATION = "componentLocation";

  public static final TableFormat CFT_SHOW_SYSTEM_TREE_ROOTS = FieldFormat.create("<" + CF_ROOTS_ROOT + "><S>").wrap();

  public static final TableFormat CFT_SHOW_SYSTEM_TREE = new TableFormat(1, 1);
  
  static
  {
    CFT_SHOW_SYSTEM_TREE.addField("<" + CF_ROOT + "><S><F=N><D=" + Cres.get().getString("root") + "><E=" + StringFieldFormat.EDITOR_CONTEXT + ">");
    
    FieldFormat<DataTable> ff = FieldFormat.create("<" + CF_ROOTS + "><T>");
    ff.setDefault(new SimpleDataTable(CFT_SHOW_SYSTEM_TREE_ROOTS, true));
    CFT_SHOW_SYSTEM_TREE.addField(ff);
    
    ff = FieldFormat.create("<" + CF_RELATED_ACTIONS + "><B><A=1><D=" + Cres.get().getString("relatedActions") + ">");
    CFT_SHOW_SYSTEM_TREE.addField(ff);
    
    ff = FieldFormat.create("<" + CF_CONTEXT_MENU + "><B><A=1><D=" + Cres.get().getString("contextMenu") + ">");
    CFT_SHOW_SYSTEM_TREE.addField(ff);
    
    ff = FieldFormat.create("<" + CF_SHOW_TOOLBAR + "><B><A=1><D=" + Cres.get().getString("toolbar") + ">");
    CFT_SHOW_SYSTEM_TREE.addField(ff);
    
    ff = FieldFormat.create("<" + CF_LOCATION + "><T><F=NH>");
    ff.setDefault(new WindowLocation().toDataTable());
    CFT_SHOW_SYSTEM_TREE.addField(ff);
    
    ff = FieldFormat.create("<" + CF_DASHBOARD + "><T><F=NH>");
    ff.setDefault(new DashboardProperties().toDataTable());
    CFT_SHOW_SYSTEM_TREE.addField(ff);
    
    CFT_SHOW_SYSTEM_TREE.addField("<" + CF_KEY + "><S><F=NH><D=" + Cres.get().getString("key") + ">");
    
    ff = FieldFormat.create("<" + CF_DASHBOARDS_HIERARCHY_INFO + "><T><F=NH>");
    ff.setDefault(new DashboardsHierarchyInfo().toDataTable());
    CFT_SHOW_SYSTEM_TREE.addField(ff);
    
    CFT_SHOW_SYSTEM_TREE.addField(FieldFormat.create(ShowSystemTree.CF_NODE_CLICK_EXPRESSION, FieldFormat.STRING_FIELD,
        Cres.get().getString("nodeClickExpression")).setNullable(true).setDefault(null).setEditor(StringFieldFormat.EDITOR_EXPRESSION));
    
    CFT_SHOW_SYSTEM_TREE.addField(FieldFormat.create(ShowSystemTree.CF_NODE_FILTER_EXPRESSION, FieldFormat.STRING_FIELD,
        Cres.get().getString("nodeFilterExpression")).setNullable(true).setEditor(StringFieldFormat.EDITOR_EXPRESSION));

    CFT_SHOW_SYSTEM_TREE.addField("<" + CF_COMPONENT_LOCATION + "><T><F=NH><D=" + Cres.get().getString("componentLocation") + ">");
    
    CFT_SHOW_SYSTEM_TREE.addBinding(CF_NODE_CLICK_EXPRESSION + "#" + DataTableBindingProvider.PROPERTY_HIDDEN, Boolean.toString(true));
    CFT_SHOW_SYSTEM_TREE.addBinding(CF_NODE_FILTER_EXPRESSION + "#" + DataTableBindingProvider.PROPERTY_HIDDEN, Boolean.toString(true));
  }
  
  private String root;
  private List<String> roots;
  private boolean relatedActions = true;
  private boolean contextMenu = true;
  private boolean showToolbar = true;
  private WindowLocation location;
  private DashboardProperties dashboard;
  private String key;
  private DashboardsHierarchyInfo dhInfo;
  private String nodeClickExpression;
  private String nodeFilterExpression;
  
  public ShowSystemTree()
  {
    super(ActionUtils.CMD_SHOW_SYSTEM_TREE, CFT_SHOW_SYSTEM_TREE, null);
  }
  
  public ShowSystemTree(String title, Context root)
  {
    super(ActionUtils.CMD_SHOW_SYSTEM_TREE, title);
    this.root = root.getPath();
  }
  
  public ShowSystemTree(String title, String... roots)
  {
    super(ActionUtils.CMD_SHOW_SYSTEM_TREE, title);
    this.roots = Arrays.asList(roots);
  }
  
  public ShowSystemTree(String title, DataTable parameters)
  {
    super(ActionUtils.CMD_SHOW_SYSTEM_TREE, title);
    
    root = parameters.rec().getString(CF_ROOT);
    
    if (parameters.getFormat().hasField(CF_ROOTS))
    {
      roots = new LinkedList();
      for (DataRecord rec : parameters.rec().getDataTable(CF_ROOTS))
      {
        roots.add(rec.getString(CF_ROOTS_ROOT));
      }
    }
    
    relatedActions = parameters.rec().getBoolean(CF_RELATED_ACTIONS);
    
    contextMenu = parameters.rec().getBoolean(CF_CONTEXT_MENU);
    
    showToolbar = parameters.rec().getBoolean(CF_SHOW_TOOLBAR);
    
    if (parameters.getFormat().hasField(CF_LOCATION))
    {
      DataTable loc = parameters.rec().getDataTable(CF_LOCATION);
      location = loc != null ? new WindowLocation(loc.rec()) : null;
    }
    
    if (parameters.getFormat().hasField(CF_DASHBOARD))
    {
      DataTable db = parameters.rec().getDataTable(CF_DASHBOARD);
      dashboard = db != null ? new DashboardProperties(db.rec()) : null;
    }
    
    if (parameters.getFormat().hasField(CF_KEY))
    {
      key = parameters.rec().getString(CF_KEY);
    }
    
    if (parameters.getFormat().hasField(CF_DASHBOARDS_HIERARCHY_INFO))
    {
      DataTable dh = parameters.rec().getDataTable(CF_DASHBOARDS_HIERARCHY_INFO);
      dhInfo = dh != null ? new DashboardsHierarchyInfo(dh.rec()) : null;
    }
  }
  
  @Override
  protected DataTable constructParameters()
  {
    DataRecord res = new DataRecord(CFT_SHOW_SYSTEM_TREE);
    
    res.addString(root);
    
    DataTable t = new SimpleDataTable(CFT_SHOW_SYSTEM_TREE_ROOTS);
    if (roots != null)
    {
      for (Object each : roots)
      {
        t.addRecord(each);
      }
    }
    res.addDataTable(t);
    res.addBoolean(relatedActions);
    res.addBoolean(contextMenu);
    res.addBoolean(showToolbar);
    res.addDataTable(location != null ? location.toDataTable() : null);
    res.addDataTable(dashboard != null ? dashboard.toDataTable() : null);
    res.addString(key);
    res.addDataTable(dhInfo != null ? dhInfo.toDataTable() : null);
    res.setValue(CF_NODE_CLICK_EXPRESSION, nodeClickExpression);
    res.setValue(CF_NODE_FILTER_EXPRESSION, nodeFilterExpression);
    res.addDataTable(getComponentLocation() != null ? getComponentLocation().toDataTable() : null);
    return res.wrap();
  }
  
  public boolean isRelatedActions()
  {
    return relatedActions;
  }
  
  public void setRelatedActions(boolean relatedActions)
  {
    this.relatedActions = relatedActions;
  }
  
  public boolean isContextMenu()
  {
    return contextMenu;
  }
  
  public void setContextMenu(boolean contextMenu)
  {
    this.contextMenu = contextMenu;
  }
  
  public boolean isShowToolbar()
  {
    return showToolbar;
  }
  
  public void setShowToolbar(boolean showToolbar)
  {
    this.showToolbar = showToolbar;
  }
  
  public WindowLocation getLocation()
  {
    return location;
  }
  
  public void setLocation(WindowLocation location)
  {
    this.location = location;
  }
  
  public DashboardProperties getDashboard()
  {
    return dashboard;
  }
  
  public void setDashboard(DashboardProperties dashboard)
  {
    this.dashboard = dashboard;
  }
  
  public String getKey()
  {
    return key;
  }
  
  public void setKey(String key)
  {
    this.key = key;
  }
  
  public String getNodeClickExpression()
  {
    return nodeClickExpression;
  }
  
  public void setNodeClickExpression(String nodeClickExpression)
  {
    this.nodeClickExpression = nodeClickExpression;
  }
  
  public String getNodeFilterExpression()
  {
    return nodeFilterExpression;
  }
  
  public void setNodeFilterExpression(String nodeFilterExpression)
  {
    this.nodeFilterExpression = nodeFilterExpression;
  }
  
  public DashboardsHierarchyInfo getDashboardsHierarchyInfo()
  {
    return dhInfo;
  }
  
  public void setDashboardsHierarchyInfo(DashboardsHierarchyInfo dhInfo)
  {
    this.dhInfo = dhInfo;
  }

}
