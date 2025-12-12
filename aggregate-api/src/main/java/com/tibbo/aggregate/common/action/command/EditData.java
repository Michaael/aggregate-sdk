package com.tibbo.aggregate.common.action.command;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.action.ActionUtils;
import com.tibbo.aggregate.common.action.EditDataMerger;
import com.tibbo.aggregate.common.action.GenericActionCommand;
import com.tibbo.aggregate.common.action.GenericActionResponse;
import com.tibbo.aggregate.common.context.Contexts;
import com.tibbo.aggregate.common.datatable.DataRecord;
import com.tibbo.aggregate.common.datatable.DataTable;
import com.tibbo.aggregate.common.datatable.DataTableBindingProvider;
import com.tibbo.aggregate.common.datatable.DataTableBuilding;
import com.tibbo.aggregate.common.datatable.FieldFormat;
import com.tibbo.aggregate.common.datatable.SimpleDataTable;
import com.tibbo.aggregate.common.datatable.TableFormat;
import com.tibbo.aggregate.common.datatable.field.LongFieldFormat;
import com.tibbo.aggregate.common.datatable.field.StringFieldFormat;
import com.tibbo.aggregate.common.expression.function.DefaultFunctions;
import com.tibbo.aggregate.common.server.UtilitiesContextConstants;
import com.tibbo.aggregate.common.util.ComponentLocation;
import com.tibbo.aggregate.common.util.DashboardProperties;
import com.tibbo.aggregate.common.util.DashboardsHierarchyInfo;
import com.tibbo.aggregate.common.util.WindowLocation;
import com.tibbo.aggregate.common.view.StorageHelper;
import com.tibbo.aggregate.common.view.ViewFilterElement;

public class EditData extends GenericActionCommand
{
  public static final String CF_DATA = "data";
  public static final String CF_USE_DOCKABLE_FRAME = "useDockableFrame";
  public static final String CF_READ_ONLY = "readOnly";
  public static final String CF_ENABLE_POPUP_MENU = "enablePopupMenu";
  public static final String CF_ICON_ID = "iconId";
  public static final String CF_HELP_ID = "helpId";
  public static final String CF_HELP = "help";
  public static final String CF_DEFAULT_CONTEXT = "defaultContext";
  public static final String CF_LOCATION = "location";
  public static final String CF_DASHBOARD = "dashboard";
  
  public static final String CF_KEY = "key";
  
  public static final String CF_EXPRESSION = "expression";
  public static final String CF_PERIOD = "period";
  public static final String CF_STORAGE_CONTEXT = "storageContext";
  public static final String CF_RECORD_INDEX = "recordIndex";
  public static final String CF_RECORD_DESCRIPTION = "recordDescription";
  public static final String CF_INLUDE_RECORD = "includeRecord";
  public static final String CF_STORAGE_VIEW = "storageView";
  public static final String CF_STORAGE_QUERY = "storageQuery";
  public static final String CF_STORAGE_TABLE = "storageTable";
  public static final String CF_STORAGE_COLUMNS = "storageColumns";
  public static final String CF_STORAGE_FILTER = "storageFilter";
  public static final String CF_STORAGE_SORTING = "storageSorting";
  public static final String CF_STORAGE_SESSION_ID = "storageSessionId";
  public static final String CF_STORAGE_INSTANCE = "storageInstance";
  public static final String CF_STORAGE_INSTANCE_ID = "storageInstanceId";
  public static final String CF_STORAGE_BINDINGS = "storageBindings";
  public static final String CF_RELATION_FIELD = "relationField";
  public static final String CF_SHOW_TOOLBAR = "showToolbar";
  public static final String CF_SHOW_HEADER = "showHeader";
  public static final String CF_SHOW_LINE_NUMBERS = "showLineNumbers";
  public static final String CF_HORIZONTAL_SCROLLING = "horizontalScrolling";
  public static final String CF_DASHBOARDS_HIERARCHY_INFO = "dashboardsHierarchyInfo";
  public static final String CF_ADD_ROW_TABLE_ACTION = "addRowTableAction";
  public static final String CF_REMOVE_ROW_TABLE_ACTION = "removeRowTableAction";
  public static final String CF_UPDATE_ROW_TABLE_ACTION = "updateRowTableAction";
  public static final String CF_ADD_ROW_TABLE_ACTION_INPUT = "addRowTableActionInput";
  public static final String CF_REMOVE_ROW_TABLE_ACTION_INPUT = "removeRowTableActionInput";
  public static final String CF_UPDATE_ROW_TABLE_ACTION_INPUT = "updateRowTableActionInput";
  public static final String CF_ADD_ROW_TABLE_SHOW_RESULT = "addRowTableShowResult";
  public static final String CF_REMOVE_ROW_TABLE_SHOW_RESULT = "removeRowTableShowResult";
  public static final String CF_UPDATE_ROW_TABLE_SHOW_RESULT = "updateRowTableShowResult";
  public static final String CF_EDITING_IN_NEW_WINDOW = "editingInNewWindow";
  public static final String CF_COMPONENT_LOCATION = "componentLocation";
  
  public static final TableFormat CFT_EDIT_DATA = new TableFormat(1, 1)
  {
    {
      addField("<" + CF_DATA + "><T>");
      addField("<" + CF_EXPRESSION + "><S><F=N><D=" + Cres.get().getString("acDataExpression") + "><E=" + StringFieldFormat.EDITOR_EXPRESSION + ">");
      addField("<" + CF_PERIOD + "><L><F=N><D=" + Cres.get().getString("acRefreshPeriod") + "><E=" + LongFieldFormat.EDITOR_PERIOD + ">");
      addField("<" + CF_USE_DOCKABLE_FRAME + "><B><A=0><D=" + Cres.get().getString("acUseDockableFrame") + ">");
      addField("<" + CF_READ_ONLY + "><B><A=1><D=" + Cres.get().getString("readOnly") + ">");
      addField(FieldFormat.create("<" + CF_ENABLE_POPUP_MENU + "><B><A=1><D=" + Cres.get().getString("wEnablePopupMenu") + ">").setNullable(true).setAdvanced(true));
      addField("<" + CF_DEFAULT_CONTEXT + "><S><F=N><D=" + Cres.get().getString("conDefaultContext") + ">");
      addField("<" + CF_LOCATION + "><T><F=NH>");
      addField("<" + CF_DASHBOARD + "><T><F=NH>");
      
      FieldFormat ff = FieldFormat.create("<" + CF_STORAGE_BINDINGS + "><T><D=" + Cres.get().getString("bindings") + "><G=" + Cres.get().getString("view") + ">");
      ff.setDefault(new SimpleDataTable(DataTableBuilding.BINDINGS_FORMAT));
      addField(ff);
      
      addField("<" + CF_KEY + "><S><F=NH><D=" + Cres.get().getString("key") + ">");
      
      addField("<" + CF_STORAGE_CONTEXT + "><S><F=N><D=" + Cres.get().getString("storageContext") + "><G=" + Cres.get().getString("view") + "><E=" + StringFieldFormat.EDITOR_CONTEXT + ">");
      addField("<" + CF_STORAGE_VIEW + "><S><F=N><D=" + Cres.get().getString("view") + "><G=" + Cres.get().getString("view") + ">");
      addField("<" + CF_STORAGE_QUERY + "><S><F=N><D=" + Cres.get().getString("viewQuery") + "><G=" + Cres.get().getString("view") + ">");
      addField("<" + CF_STORAGE_TABLE + "><S><F=N><D=" + Cres.get().getString("acClassTable") + "><G=" + Cres.get().getString("view") + ">");
      
      ff = FieldFormat.create("<" + CF_STORAGE_COLUMNS + "><T><D=" + Cres.get().getString("columns") + "><G=" + Cres.get().getString("view") + ">");
      addField(ff);
      
      ff = FieldFormat.create("<" + CF_STORAGE_FILTER + "><T><D=" + Cres.get().getString("filter") + "><G=" + Cres.get().getString("view") + ">");
      addField(ff);
      
      ff = FieldFormat.create("<" + CF_STORAGE_SORTING + "><T><D=" + Cres.get().getString("sorting") + "><G=" + Cres.get().getString("view") + ">");
      addField(ff);
      
      ff = FieldFormat.create("<" + CF_STORAGE_SESSION_ID + "><L><F=N>");
      ff.setHidden(true);
      addField(ff);
      
      ff = FieldFormat.create("<" + CF_STORAGE_INSTANCE_ID + "><S><F=N>");
      ff.setHidden(true);
      addField(ff);
      
      ff = FieldFormat.create("<" + CF_STORAGE_INSTANCE + "><T><F=N>");
      ff.setHidden(true);
      addField(ff);
      
      ff = FieldFormat.create("<" + CF_RELATION_FIELD + "><S><F=N><D=" + Cres.get().getString("relation") + ">");
      ff.setExtendableSelectionValues(true);
      ff.setHidden(true);
      addField(ff);
      
      addField("<" + CF_ICON_ID + "><S><F=N><D=" + Cres.get().getString("conIconId") + ">");
      addField("<" + CF_HELP_ID + "><S><F=N><D=" + Cres.get().getString("conHelpId") + ">");
      addField("<" + CF_HELP + "><S><F=AN><D=" + Cres.get().getString("help") + ">");
      
      addField(FieldFormat.create(CF_SHOW_TOOLBAR, FieldFormat.BOOLEAN_FIELD, Cres.get().getString("showToolbar")).setDefault(true).setAdvanced(true));
      addField(FieldFormat.create(CF_SHOW_HEADER, FieldFormat.BOOLEAN_FIELD, Cres.get().getString("showHeader")).setNullable(true).setAdvanced(true));
      addField(FieldFormat.create(CF_SHOW_LINE_NUMBERS, FieldFormat.BOOLEAN_FIELD, Cres.get().getString("showLineNumbers")).setNullable(true).setAdvanced(true));
      addField(FieldFormat.create(CF_HORIZONTAL_SCROLLING, FieldFormat.BOOLEAN_FIELD, Cres.get().getString("horizontalScrolling")).setNullable(true).setAdvanced(true));
      
      addField("<" + CF_DASHBOARDS_HIERARCHY_INFO + "><T><F=NH>");
      
      ff = FieldFormat.create("<" + CF_ADD_ROW_TABLE_ACTION + "><S><F=N><D=" + Cres.get().getString("dtEditorAddRowTableAction") + ">")
          .setGroup(Cres.get().getString("actions")).setDefault(null).setAdvanced(true)
          .setEditor(StringFieldFormat.EDITOR_TARGET).setEditorOptions(StringFieldFormat.EDITOR_TARGET_MODE_ACTIONS_ONLY);
      addField(ff);
      
      ff = FieldFormat.create("<" + CF_ADD_ROW_TABLE_ACTION_INPUT + "><S><F=N><D=" + Cres.get().getString("dtEditorAddRowTableActionInput") + ">")
          .setGroup(Cres.get().getString("actions")).setDefault(null).setAdvanced(true).setEditor(StringFieldFormat.EDITOR_EXPRESSION);
      addField(ff);
      
      ff = FieldFormat.create("<" + CF_ADD_ROW_TABLE_SHOW_RESULT + "><B><D=" + Cres.get().getString("dtEditorAddRowTableShowResult") + ">")
          .setGroup(Cres.get().getString("actions")).setDefault(true).setAdvanced(true);
      addField(ff);
      
      ff = FieldFormat.create("<" + CF_REMOVE_ROW_TABLE_ACTION + "><S><F=N><D=" + Cres.get().getString("dtEditorRemoveRowTableAction") + ">")
          .setGroup(Cres.get().getString("actions")).setDefault(null).setAdvanced(true)
          .setEditor(StringFieldFormat.EDITOR_TARGET).setEditorOptions(StringFieldFormat.EDITOR_TARGET_MODE_ACTIONS_ONLY);
      addField(ff);
      
      ff = FieldFormat.create("<" + CF_REMOVE_ROW_TABLE_ACTION_INPUT + "><S><F=N><D=" + Cres.get().getString("dtEditorRemoveRowTableActionInput") + ">")
          .setGroup(Cres.get().getString("actions")).setDefault(null).setAdvanced(true).setEditor(StringFieldFormat.EDITOR_EXPRESSION);
      addField(ff);
      
      ff = FieldFormat.create("<" + CF_REMOVE_ROW_TABLE_SHOW_RESULT + "><B><D=" + Cres.get().getString("dtEditorRemoveTableShowResult") + ">")
          .setGroup(Cres.get().getString("actions")).setAdvanced(true).setDefault(true);
      addField(ff);
      
      ff = FieldFormat.create("<" + CF_EDITING_IN_NEW_WINDOW + "><B><A=" + Boolean.FALSE + "><D=" + Cres.get().getString("dteEditingInNewWindow") + ">");
      ff.setGroup(Cres.get().getString("actions"));
      ff.setAdvanced(true);
      addField(ff);
      
      ff = FieldFormat.create("<" + CF_UPDATE_ROW_TABLE_ACTION + "><S><F=N><D=" + Cres.get().getString("dtEditorUpdateRowTableAction") + ">")
          .setGroup(Cres.get().getString("actions")).setDefault(null).setAdvanced(true)
          .setEditor(StringFieldFormat.EDITOR_TARGET).setEditorOptions(StringFieldFormat.EDITOR_TARGET_MODE_ACTIONS_ONLY);
      addField(ff);
      
      ff = FieldFormat.create("<" + CF_UPDATE_ROW_TABLE_ACTION_INPUT + "><S><F=N><D=" + Cres.get().getString("dtEditorUpdateRowTableActionInput") + ">")
          .setGroup(Cres.get().getString("actions")).setDefault(null).setAdvanced(true).setEditor(StringFieldFormat.EDITOR_EXPRESSION);
      addField(ff);
      
      ff = FieldFormat.create("<" + CF_UPDATE_ROW_TABLE_SHOW_RESULT + "><B><D=" + Cres.get().getString("dtEditorUpdateRowTableShowResult") + ">")
          .setGroup(Cres.get().getString("actions")).setAdvanced(true).setDefault(true);
      addField(ff);

      addField("<" + CF_COMPONENT_LOCATION + "><T><F=NH><D=" + Cres.get().getString("componentLocation") + ">");
      
      String ref = CF_PERIOD + "#" + DataTableBindingProvider.PROPERTY_ENABLED;
      String exp = "{" + CF_EXPRESSION + "} != null";
      addBinding(ref, exp);
      
      ref = CF_ENABLE_POPUP_MENU + "#" + DataTableBindingProvider.PROPERTY_ENABLED;
      exp = "{" + CF_EXPRESSION + "} != null";
      addBinding(ref, exp);
      
      ref = CF_STORAGE_CONTEXT + "#" + DataTableBindingProvider.PROPERTY_ENABLED;
      exp = "{" + CF_EXPRESSION + "} == null";
      addBinding(ref, exp);
      
      ref = CF_STORAGE_QUERY + "#" + DataTableBindingProvider.PROPERTY_ENABLED;
      exp = "{" + CF_EXPRESSION + "} == null && {" + CF_STORAGE_CONTEXT + "} != null && ({" + CF_STORAGE_VIEW + "} == null || " + DefaultFunctions.LENGTH + "({" + CF_STORAGE_VIEW + "}) == 0)";
      addBinding(ref, exp);
      
      ref = CF_STORAGE_TABLE + "#" + DataTableBindingProvider.PROPERTY_ENABLED;
      exp = "{" + CF_EXPRESSION + "} == null && {" + CF_STORAGE_QUERY + "} == null && {" + CF_STORAGE_CONTEXT + "} != null && length({" + CF_STORAGE_CONTEXT + "}) > 0 && ({" + CF_STORAGE_VIEW
          + "} == null || " + DefaultFunctions.LENGTH + "({" + CF_STORAGE_VIEW + "}) == 0) && " + DefaultFunctions.FUNCTION_AVAILABLE + "({" + CF_STORAGE_CONTEXT + "}, '" + StorageHelper.F_STORAGE_TABLES + "')";
      addBinding(ref, exp);
      
      ref = CF_STORAGE_VIEW + "#" + DataTableBindingProvider.PROPERTY_CHOICES;
      String tableExp = DefaultFunctions.CALL_FUNCTION + "('\" + {" + CF_STORAGE_CONTEXT + "} + \"', '" + StorageHelper.F_STORAGE_VIEWS + "')";
      String valueExp = "{" + DataTableBuilding.FIELD_SELECTION_VALUES_VALUE + "}";
      String descriptionExp = "{" + DataTableBuilding.FIELD_SELECTION_VALUES_DESCRIPTION + "}";
      exp = "({" + CF_STORAGE_CONTEXT + "} != null && length({" + CF_STORAGE_CONTEXT + "}) > 0) ? " + DefaultFunctions.CALL_FUNCTION + "(\"" + Contexts.CTX_UTILITIES + "\", \""
          + UtilitiesContextConstants.F_SELECTION_VALUES + "\", \"" + tableExp + "\",  \"" + valueExp + "\",  \"" + descriptionExp + "\") : null";
      addBinding(ref, exp);
      
      ref = CF_STORAGE_TABLE + "#" + DataTableBindingProvider.PROPERTY_CHOICES;
      tableExp = DefaultFunctions.CALL_FUNCTION + "('\" + {" + CF_STORAGE_CONTEXT + "} + \"', '" + StorageHelper.F_STORAGE_TABLES + "')";
      valueExp = "{" + DataTableBuilding.FIELD_SELECTION_VALUES_VALUE + "}";
      descriptionExp = "{" + DataTableBuilding.FIELD_SELECTION_VALUES_DESCRIPTION + "}";
      exp = "({" + CF_STORAGE_CONTEXT + "} != null && length({" + CF_STORAGE_CONTEXT + "}) > 0) ? " + DefaultFunctions.CALL_FUNCTION + "(\"" + Contexts.CTX_UTILITIES + "\", \""
          + UtilitiesContextConstants.F_SELECTION_VALUES + "\", \"" + tableExp + "\",  \"" + valueExp + "\",  \"" + descriptionExp + "\") : null";
      addBinding(ref, exp);
      
      ref = CF_STORAGE_COLUMNS + "#" + DataTableBindingProvider.PROPERTY_ENABLED;
      exp = "{" + CF_EXPRESSION + "} == null && {" + CF_STORAGE_QUERY + "} == null && {" + CF_STORAGE_CONTEXT + "} != null && length({" + CF_STORAGE_CONTEXT + "}) > 0 && ({" + CF_STORAGE_VIEW
          + "} == null || " + DefaultFunctions.LENGTH + "({" + CF_STORAGE_VIEW + "}) == 0) && " + DefaultFunctions.FUNCTION_AVAILABLE + "({" + CF_STORAGE_CONTEXT + "}, '" + StorageHelper.F_STORAGE_COLUMNS + "')";
      addBinding(ref, exp);
      
      ref = CF_STORAGE_COLUMNS;
      exp = "({" + CF_STORAGE_CONTEXT + "} != null && length({" + CF_STORAGE_CONTEXT + "}) > 0 && {" + CF_STORAGE_TABLE + "} != null) ? " + DefaultFunctions.CALL_FUNCTION + "({" + CF_STORAGE_CONTEXT + "}, '"
          + StorageHelper.F_STORAGE_COLUMNS + "', {" + CF_STORAGE_TABLE + "}, {" + CF_STORAGE_COLUMNS + "}) : table()";
      addBinding(ref, exp);
      
      ref = CF_STORAGE_FILTER + "#" + DataTableBindingProvider.PROPERTY_ENABLED;
      exp = "{" + CF_EXPRESSION + "} == null && {" + CF_STORAGE_QUERY + "} == null && {" + CF_STORAGE_CONTEXT + "} != null && length({" + CF_STORAGE_CONTEXT + "}) > 0 && ({" + CF_STORAGE_VIEW
          + "} == null || " + DefaultFunctions.LENGTH + "({" + CF_STORAGE_VIEW + "}) == 0) && " + DefaultFunctions.FUNCTION_AVAILABLE + "({" + CF_STORAGE_CONTEXT + "}, '" + StorageHelper.F_STORAGE_FILTER + "')";
      addBinding(ref, exp);
      
      ref = CF_STORAGE_FILTER;
      exp = "({" + CF_STORAGE_CONTEXT + "} != null && length({" + CF_STORAGE_CONTEXT + "}) > 0 && {" + CF_STORAGE_TABLE + "} != null) ? " + DefaultFunctions.CALL_FUNCTION + "({" + CF_STORAGE_CONTEXT + "}, '"
          + StorageHelper.F_STORAGE_FILTER + "', {" + CF_STORAGE_TABLE + "}, {" + CF_STORAGE_FILTER + "}) : table()";
      addBinding(ref, exp);
      
      ref = CF_STORAGE_SORTING + "#" + DataTableBindingProvider.PROPERTY_ENABLED;
      exp = "{" + CF_EXPRESSION + "} == null && {" + CF_STORAGE_QUERY + "} == null && {" + CF_STORAGE_CONTEXT + "} != null && length({" + CF_STORAGE_CONTEXT + "}) > 0 && ({" + CF_STORAGE_VIEW
          + "} == null || " + DefaultFunctions.LENGTH + "({" + CF_STORAGE_VIEW + "}) == 0) && " + DefaultFunctions.FUNCTION_AVAILABLE + "({" + CF_STORAGE_CONTEXT + "}, '" + StorageHelper.F_STORAGE_COLUMNS + "')";
      addBinding(ref, exp);
      
      ref = CF_STORAGE_SORTING;
      exp = "({" + CF_STORAGE_CONTEXT + "} != null && length({" + CF_STORAGE_CONTEXT + "}) > 0 && {" + CF_STORAGE_TABLE + "} != null) ? " + DefaultFunctions.CALL_FUNCTION + "({" + CF_STORAGE_CONTEXT + "}, '"
          + StorageHelper.F_STORAGE_SORTING + "', {" + CF_STORAGE_TABLE + "}, {" + CF_STORAGE_SORTING + "}) : table()";
      addBinding(ref, exp);
      
      addBinding(CF_ADD_ROW_TABLE_ACTION + "#" + DataTableBindingProvider.PROPERTY_HIDDEN, Boolean.toString(true));
      addBinding(CF_ADD_ROW_TABLE_ACTION_INPUT + "#" + DataTableBindingProvider.PROPERTY_HIDDEN, Boolean.toString(true));
      addBinding(CF_ADD_ROW_TABLE_SHOW_RESULT + "#" + DataTableBindingProvider.PROPERTY_HIDDEN, Boolean.toString(true));
      addBinding(CF_REMOVE_ROW_TABLE_ACTION + "#" + DataTableBindingProvider.PROPERTY_HIDDEN, Boolean.toString(true));
      addBinding(CF_REMOVE_ROW_TABLE_ACTION_INPUT + "#" + DataTableBindingProvider.PROPERTY_HIDDEN, Boolean.toString(true));
      addBinding(CF_REMOVE_ROW_TABLE_SHOW_RESULT + "#" + DataTableBindingProvider.PROPERTY_HIDDEN, Boolean.toString(true));
      addBinding(CF_UPDATE_ROW_TABLE_ACTION + "#" + DataTableBindingProvider.PROPERTY_HIDDEN, Boolean.toString(true));
      addBinding(CF_UPDATE_ROW_TABLE_ACTION_INPUT + "#" + DataTableBindingProvider.PROPERTY_HIDDEN, Boolean.toString(true));
      addBinding(CF_UPDATE_ROW_TABLE_SHOW_RESULT + "#" + DataTableBindingProvider.PROPERTY_HIDDEN, Boolean.toString(true));
      
      addBinding(CF_EDITING_IN_NEW_WINDOW + "#" + DataTableBindingProvider.PROPERTY_HIDDEN, Boolean.toString(true));
      
      addBinding(CF_UPDATE_ROW_TABLE_ACTION + "#" + DataTableBindingProvider.PROPERTY_ENABLED, "{" + CF_EDITING_IN_NEW_WINDOW + "}");
      addBinding(CF_UPDATE_ROW_TABLE_ACTION_INPUT + "#" + DataTableBindingProvider.PROPERTY_ENABLED, "{" + CF_EDITING_IN_NEW_WINDOW + "}");
      
      addBinding(CF_ADD_ROW_TABLE_ACTION_INPUT + "#" + DataTableBindingProvider.PROPERTY_ENABLED, "{" + CF_ADD_ROW_TABLE_ACTION + "} != null ? true : false");
      addBinding(CF_REMOVE_ROW_TABLE_ACTION_INPUT + "#" + DataTableBindingProvider.PROPERTY_ENABLED, "{" + CF_REMOVE_ROW_TABLE_ACTION + "} != null ? true : false");
      
      addBinding(CF_ADD_ROW_TABLE_SHOW_RESULT + "#" + DataTableBindingProvider.PROPERTY_ENABLED, "{" + CF_ADD_ROW_TABLE_ACTION + "} != null ? true : false");
      addBinding(CF_REMOVE_ROW_TABLE_SHOW_RESULT + "#" + DataTableBindingProvider.PROPERTY_ENABLED, "{" + CF_REMOVE_ROW_TABLE_ACTION + "} != null ? true : false");
      addBinding(CF_UPDATE_ROW_TABLE_SHOW_RESULT + "#" + DataTableBindingProvider.PROPERTY_ENABLED, "{" + CF_UPDATE_ROW_TABLE_ACTION + "} != null ? true : false");
      
      ref = CF_ADD_ROW_TABLE_ACTION_INPUT + "#" + DataTableBindingProvider.PROPERTY_OPTIONS;
      exp = DefaultFunctions.TABLE + "(\"" + StringFieldFormat.EXPRESSION_BUILDER_OPTIONS_FORMAT.encode(true) + "\", {" + CF_STORAGE_CONTEXT + "}, "
          + DefaultFunctions.TABLE + "(\"" + StorageHelper.FOFT_ADD_ROW_ACTION.encode(true) + "\"))";
      addBinding(ref, exp);
      
      ref = CF_REMOVE_ROW_TABLE_ACTION_INPUT + "#" + DataTableBindingProvider.PROPERTY_OPTIONS;
      exp = DefaultFunctions.TABLE + "(\"" + StringFieldFormat.EXPRESSION_BUILDER_OPTIONS_FORMAT.encode(true) + "\", {" + CF_STORAGE_CONTEXT + "}, "
          + DefaultFunctions.TABLE + "(\"" + StorageHelper.FOFT_REMOVE_OR_UPDATE_ROW_ACTION.encode(true) + "\"))";
      addBinding(ref, exp);
      
      ref = CF_UPDATE_ROW_TABLE_ACTION_INPUT + "#" + DataTableBindingProvider.PROPERTY_OPTIONS;
      exp = DefaultFunctions.TABLE + "(\"" + StringFieldFormat.EXPRESSION_BUILDER_OPTIONS_FORMAT.encode(true) + "\", {" + CF_STORAGE_CONTEXT + "}, "
          + DefaultFunctions.TABLE + "(\"" + StorageHelper.FOFT_REMOVE_OR_UPDATE_ROW_ACTION.encode(true) + "\"))";
      addBinding(ref, exp);
    }
  };
  
  private EditDataMerger merger;
  private DataTable data;
  private boolean useDockableFrame;
  private boolean readOnly;
  private Boolean enablePopupMenu;
  private String iconId;
  private String helpId;
  private String help;
  private String defaultContext;
  private WindowLocation location;
  private DashboardProperties dashboard;
  private DataTable storageBindings;
  private String key;
  
  private String expression;
  private Long period;
  private String storageContext;
  private String storageView;
  private String storageQuery;
  private String storageTable;
  private DataTable storageColumns;
  private DataTable storageFilter;
  private DataTable storageSorting;
  private Long storageSessionId;
  private Object storageInstanceId;
  private DataTable storageInstance;
  private String relationField;
  private boolean showToolbar;
  private Boolean showHeader;
  private Boolean showLineNumbers;
  private Boolean horizontalScrolling;
  private DashboardsHierarchyInfo dhInfo;
  private String addRowTableAction;
  private String addRowTableActionInput;
  private boolean addRowTableActionShowResult;
  private String removeRowTableAction;
  private String removeRowTableActionInput;
  private boolean removeRowTableActionShowResult;
  private String updateRowTableAction;
  private String updateRowTableActionInput;
  private boolean updateRowTableActionShowResult;
  private boolean editingInNewWindow;
  
  public EditData()
  {
    super(ActionUtils.CMD_EDIT_DATA, CFT_EDIT_DATA, null);
  }
  
  public EditData(String title, DataTable parameters)
  {
    super(ActionUtils.CMD_EDIT_DATA, title, parameters, CFT_EDIT_DATA);
  }
  
  public EditData(String title, DataTable data, boolean readonly)
  {
    super(ActionUtils.CMD_EDIT_DATA, title);
    this.data = data;
    this.readOnly = readonly;
  }
  
  public EditData(String title, String iconId, String expression, Long period)
  {
    super(ActionUtils.CMD_EDIT_DATA, title);
    this.data = new SimpleDataTable();
    this.iconId = iconId;
    this.expression = expression;
    this.period = period;
    this.readOnly = true;
    setParameters(getParameters());
  }
  
  @Override
  protected DataTable constructParameters()
  {
    DataRecord rec = new DataRecord(CFT_EDIT_DATA);
    rec.addDataTable(data);
    rec.addString(expression);
    rec.addLong(period);
    rec.addBoolean(useDockableFrame);
    rec.addBoolean(readOnly);
    rec.addBoolean(enablePopupMenu);
    rec.addString(defaultContext);
    rec.addDataTable(location != null ? location.toDataTable() : null);
    rec.addDataTable(dashboard != null ? dashboard.toDataTable() : null);
    rec.addDataTable(storageBindings != null ? storageBindings : new SimpleDataTable(DataTableBuilding.BINDINGS_FORMAT));
    rec.addString(key);
    rec.addString(storageContext);
    rec.addString(storageView);
    rec.addString(storageQuery);
    rec.addString(storageTable);
    rec.addDataTable(storageColumns != null ? storageColumns : new SimpleDataTable(StorageHelper.FORMAT_COLUMNS));
    rec.addDataTable(storageFilter != null ? storageFilter : new SimpleDataTable(ViewFilterElement.FORMAT));
    rec.addDataTable(storageSorting != null ? storageSorting : new SimpleDataTable(StorageHelper.FORMAT_SORTING));
    rec.addLong(storageSessionId);
    rec.addValue(storageInstanceId);
    rec.addDataTable(storageInstance);
    rec.addString(relationField);
    
    rec.addString(iconId);
    rec.addString(helpId);
    rec.addString(help);
    rec.addBoolean(isShowToolbar());
    rec.addBoolean(isShowHeader());
    rec.addBoolean(isShowLineNumbers());
    rec.addBoolean(isHorizontalScrolling());
    
    rec.addDataTable(dhInfo != null ? dhInfo.toDataTable() : null);
    
    rec.addString(addRowTableAction);
    rec.addString(addRowTableActionInput);
    rec.addBoolean(addRowTableActionShowResult);
    rec.addString(removeRowTableAction);
    rec.addString(removeRowTableActionInput);
    rec.addBoolean(removeRowTableActionShowResult);
    
    rec.addBoolean(editingInNewWindow);
    rec.addString(updateRowTableAction);
    rec.addString(updateRowTableActionInput);
    rec.addBoolean(updateRowTableActionShowResult);
    rec.addDataTable(getComponentLocation() != null ? getComponentLocation().toDataTable() : null);
    
    return rec.wrap();
  }
  
  @Override
  public GenericActionResponse createDefaultResponse()
  {
    final DataTable table = getParameters().rec().getDataTable(CF_DATA);
    return new GenericActionResponse(table);
  }
  
  public DataTable getData()
  {
    return data;
  }
  
  public void setData(DataTable data)
  {
    this.data = data;
  }
  
  public boolean isUseDockableFrame()
  {
    return useDockableFrame;
  }
  
  public void setUseDockableFrame(boolean useDockableFrame)
  {
    this.useDockableFrame = useDockableFrame;
  }
  
  public boolean isReadOnly()
  {
    return readOnly;
  }
  
  public void setReadOnly(boolean readonly)
  {
    this.readOnly = readonly;
  }
  
  public String getIconId()
  {
    return iconId;
  }
  
  public void setIconId(String iconId)
  {
    this.iconId = iconId;
  }
  
  public String getHelpId()
  {
    return helpId;
  }
  
  public void setHelpId(String helpId)
  {
    this.helpId = helpId;
  }
  
  public String getHelp()
  {
    return help;
  }
  
  public void setHelp(String help)
  {
    this.help = help;
  }
  
  public String getDefaultContext()
  {
    return defaultContext;
  }
  
  public void setDefaultContext(String defaultContext)
  {
    this.defaultContext = defaultContext;
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

  public DataTable getStorageBindings()
  {
    return storageBindings;
  }
  
  public void setStorageBindings(DataTable storageBindings)
  {
    this.storageBindings = storageBindings;
  }
  
  public String getExpression()
  {
    return expression;
  }
  
  public void setExpression(String expression)
  {
    this.expression = expression;
  }
  
  public Long getPeriod()
  {
    return period;
  }
  
  public void setPeriod(Long period)
  {
    this.period = period;
  }
  
  public Boolean getEnablePopupMenu()
  {
    return enablePopupMenu;
  }
  
  public void setEnablePopupMenu(Boolean enablePopupMenu)
  {
    this.enablePopupMenu = enablePopupMenu;
  }
  
  public String getStorageContext()
  {
    return storageContext;
  }
  
  public void setStorageContext(String storageContext)
  {
    this.storageContext = storageContext;
  }
  
  public String getStorageView()
  {
    return storageView;
  }
  
  public void setStorageView(String storageView)
  {
    this.storageView = storageView;
  }
  
  public String getStorageQuery()
  {
    return storageQuery;
  }
  
  public void setStorageQuery(String storageQuery)
  {
    this.storageQuery = storageQuery;
  }
  
  public String getStorageTable()
  {
    return storageTable;
  }
  
  public void setStorageTable(String storageTable)
  {
    this.storageTable = storageTable;
  }
  
  public DataTable getStorageColumns()
  {
    return storageColumns;
  }
  
  public void setStorageColumns(DataTable storageColumns)
  {
    this.storageColumns = storageColumns;
  }
  
  public DataTable getStorageFilter()
  {
    return storageFilter;
  }
  
  public void setStorageFilter(DataTable storageFilter)
  {
    this.storageFilter = storageFilter;
  }
  
  public DataTable getStorageSorting()
  {
    return storageSorting;
  }
  
  public void setStorageSorting(DataTable storageSorting)
  {
    this.storageSorting = storageSorting;
  }
  
  public EditDataMerger getMerger()
  {
    return merger;
  }
  
  public void setMerger(EditDataMerger merger)
  {
    this.merger = merger;
  }
  
  public void setStorageSessionId(Long storageSessionId)
  {
    this.storageSessionId = storageSessionId;
  }
  
  public Long getStorageSessionId()
  {
    return storageSessionId;
  }
  
  public void setStorageInstanceId(Object storageInstanceId)
  {
    this.storageInstanceId = storageInstanceId;
  }
  
  public Object getInstanceID()
  {
    return storageInstanceId;
  }
  
  public void setStorageInstance(DataTable storageInstance)
  {
    this.storageInstance = storageInstance;
  }
  
  public DataTable getStorageInstance()
  {
    return storageInstance;
  }
  
  public void setRelationField(String relationField)
  {
    this.relationField = relationField;
  }
  
  public String getRelationField()
  {
    return relationField;
  }
  
  public boolean isShowToolbar()
  {
    return showToolbar;
  }
  
  public void setShowToolbar(boolean showToolbar)
  {
    this.showToolbar = showToolbar;
  }
  
  public Boolean isShowHeader()
  {
    return showHeader;
  }
  
  public void setShowHeader(Boolean showHeader)
  {
    this.showHeader = showHeader;
  }
  
  public Boolean isShowLineNumbers()
  {
    return showLineNumbers;
  }
  
  public void setShowLineNumbers(Boolean showLineNumbers)
  {
    this.showLineNumbers = showLineNumbers;
  }
  
  public Boolean isHorizontalScrolling()
  {
    return horizontalScrolling;
  }
  
  public void setHorizontalScrolling(Boolean horizontalScrolling)
  {
    this.horizontalScrolling = horizontalScrolling;
  }
  
  public String getKey()
  {
    return key;
  }
  
  public void setKey(String key)
  {
    this.key = key;
  }
  
  public DashboardsHierarchyInfo getDashboardsHierarchyInfo()
  {
    return dhInfo;
  }
  
  public void setDashboardsHierarchyInfo(DashboardsHierarchyInfo dhInfo)
  {
    this.dhInfo = dhInfo;
  }
  
  public String getAddRowTableAction()
  {
    return addRowTableAction;
  }
  
  public void setAddRowTableAction(String addRowTableAction)
  {
    this.addRowTableAction = addRowTableAction;
  }
  
  public String getRowTableAction()
  {
    return removeRowTableAction;
  }
  
  public void setRemoveRowTableAction(String removeRowTableAction)
  {
    this.removeRowTableAction = removeRowTableAction;
  }
  
  public String getUpdateRowTableAction()
  {
    return updateRowTableAction;
  }
  
  public void setUpdateRowTableAction(String updateRowTableAction)
  {
    this.updateRowTableAction = updateRowTableAction;
  }
  
  public void setEditingInNewWindow(boolean editingInNewWindow)
  {
    this.editingInNewWindow = editingInNewWindow;
  }
  
  public boolean isEditingInNewWindow()
  {
    return editingInNewWindow;
  }
  
  public String getAddRowTableActionInput()
  {
    return addRowTableActionInput;
  }
  
  public void setAddRowTableActionInput(String addRowTableActionInput)
  {
    this.addRowTableActionInput = addRowTableActionInput;
  }
  
  public String getRemoveRowTableActionInput()
  {
    return removeRowTableActionInput;
  }
  
  public void setRemoveRowTableActionInput(String removeRowTableActionInput)
  {
    this.removeRowTableActionInput = removeRowTableActionInput;
  }
  
  public String getUpdateRowTableActionInput()
  {
    return updateRowTableActionInput;
  }
  
  public void setUpdateRowTableActionInput(String updateRowTableActionInput)
  {
    this.updateRowTableActionInput = updateRowTableActionInput;
  }
  
  public boolean isAddRowTableActionShowResult()
  {
    return addRowTableActionShowResult;
  }
  
  public void setAddRowTableActionShowResult(boolean addRowTableActionShowResult)
  {
    this.addRowTableActionShowResult = addRowTableActionShowResult;
  }
  
  public boolean isRemoveRowTableActionShowResult()
  {
    return removeRowTableActionShowResult;
  }
  
  public void setRemoveRowTableActionShowResult(boolean removeRowTableActionShowResult)
  {
    this.removeRowTableActionShowResult = removeRowTableActionShowResult;
  }
  
  public boolean isUpdateRowTableActionShowResult()
  {
    return updateRowTableActionShowResult;
  }
  
  public void setUpdateRowTableActionShowResult(boolean updateRowTableActionShowResult)
  {
    this.updateRowTableActionShowResult = updateRowTableActionShowResult;
  }
}
