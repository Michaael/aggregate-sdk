package com.tibbo.aggregate.common.datatable.converter.editor;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.action.command.EditData;
import com.tibbo.aggregate.common.context.ContextUtils;
import com.tibbo.aggregate.common.context.Contexts;
import com.tibbo.aggregate.common.datatable.DataRecord;
import com.tibbo.aggregate.common.datatable.DataTable;
import com.tibbo.aggregate.common.datatable.DataTableBindingProvider;
import com.tibbo.aggregate.common.datatable.DataTableBuilding;
import com.tibbo.aggregate.common.datatable.FieldFormat;
import com.tibbo.aggregate.common.datatable.TableFormat;
import com.tibbo.aggregate.common.datatable.field.LongFieldFormat;
import com.tibbo.aggregate.common.datatable.field.StringFieldFormat;
import com.tibbo.aggregate.common.expression.Reference;
import com.tibbo.aggregate.common.expression.function.DefaultFunctions;
import com.tibbo.aggregate.common.server.UtilitiesContextConstants;
import com.tibbo.aggregate.common.view.StorageHelper;

public class ForeignInstanceConverter extends AbstractEditorOptionsConverter
{
  public static final String FIELD_DESCRIPTION = "description";
  public static final String FIELD_REFERENCE = "reference";
  
  public static final String FIELD_ICON = "icon";
  
  public static final String FIELD_STORAGE_CONTEXT = "storageContext";
  public static final String FIELD_STORAGE_VIEW = "storageView";
  public static final String FIELD_STORAGE_QUERY = "storageQuery";
  public static final String FIELD_STORAGE_TABLE = "storageTable";
  public static final String FIELD_REFERENCE_FIELD = "referenceField";
  public static final String FIELD_STORAGE_COLUMNS = "storageColumns";
  public static final String FIELD_STORAGE_FILTER = "storageFilter";
  public static final String FIELD_STORAGE_SORTING = "storageSorting";
  public static final String FIELD_DASHBOARD = "dashboard";
  
  public static final TableFormat FORMAT = new TableFormat(1, 1);
  static
  {
    String exp;
    String ref;
    String tableExp;
    String valueExp;
    String descriptionExp;
    
    FORMAT.addField(FieldFormat.create("<" + FIELD_STORAGE_CONTEXT + "><S><F=N><D=" + Cres.get().getString("storageContext") + ">").setEditor(StringFieldFormat.EDITOR_CONTEXT));
    FORMAT.addField(FieldFormat.create("<" + FIELD_STORAGE_VIEW + "><S><D=" + Cres.get().getString("view") + ">"));
    FORMAT.addField(FieldFormat.create("<" + FIELD_STORAGE_TABLE + "><S><F=N><D=" + Cres.get().getString("acClassTable") + ">"));
    FORMAT.addField(FieldFormat.create("<" + FIELD_REFERENCE_FIELD + "><S><F=AN><D=" + Cres.get().getString("acReferenceField") + ">"));
    
    FORMAT.addField(FieldFormat.create("<" + FIELD_STORAGE_COLUMNS + "><T><D=" + Cres.get().getString("columns") + ">"));
    FORMAT.addField(FieldFormat.create("<" + FIELD_STORAGE_FILTER + "><T><D=" + Cres.get().getString("filter") + ">"));
    FORMAT.addField(FieldFormat.create("<" + FIELD_STORAGE_SORTING + "><T><D=" + Cres.get().getString("sorting") + ">"));
    FORMAT.addField(FieldFormat.create("<" + FIELD_DASHBOARD + "><S><F=N><D=" + Cres.get().getString("dashboard") + ">").setEditor(StringFieldFormat.EDITOR_CONTEXT)
        .setEditorOptions(StringFieldFormat.encodeMaskEditorOptions(Contexts.TYPE_DASHBOARD, Contexts.CTX_DASHBOARDS)));
    
    FORMAT.addField(FieldFormat.create("<" + FIELD_ICON + "><S><F=N><D=" + Cres.get().getString("icon") + ">"));
    
    ref = FIELD_STORAGE_VIEW + "#" + DataTableBindingProvider.PROPERTY_CHOICES;
    tableExp = DefaultFunctions.CALL_FUNCTION + "('\" + {" + FIELD_STORAGE_CONTEXT + "} + \"', '" + StorageHelper.F_STORAGE_VIEWS + "')";
    valueExp = "{" + DataTableBuilding.FIELD_SELECTION_VALUES_VALUE + "}";
    descriptionExp = "{" + DataTableBuilding.FIELD_SELECTION_VALUES_DESCRIPTION + "}";
    exp = "({" + FIELD_STORAGE_CONTEXT + "} != null && length({" + FIELD_STORAGE_CONTEXT + "}) > 0) ? " + DefaultFunctions.CALL_FUNCTION + "(\"" + Contexts.CTX_UTILITIES + "\", \""
        + UtilitiesContextConstants.F_SELECTION_VALUES + "\", \"" + tableExp + "\",  \"" + valueExp + "\",  \"" + descriptionExp + "\") : null";
    FORMAT.addBinding(ref, exp);
    
    ref = FIELD_STORAGE_TABLE + "#" + DataTableBindingProvider.PROPERTY_CHOICES;
    tableExp = DefaultFunctions.CALL_FUNCTION + "('\" + {" + FIELD_STORAGE_CONTEXT + "} + \"', '" + StorageHelper.F_STORAGE_TABLES + "')";
    valueExp = "{" + DataTableBuilding.FIELD_SELECTION_VALUES_VALUE + "}";
    descriptionExp = "{" + DataTableBuilding.FIELD_SELECTION_VALUES_DESCRIPTION + "}";
    exp = "({" + FIELD_STORAGE_CONTEXT + "} != null && length({" + FIELD_STORAGE_CONTEXT + "}) > 0) ? " + DefaultFunctions.CALL_FUNCTION + "(\"" + Contexts.CTX_UTILITIES + "\", \""
        + UtilitiesContextConstants.F_SELECTION_VALUES + "\", \"" + tableExp + "\",  \"" + valueExp + "\",  \"" + descriptionExp + "\") : null";
    FORMAT.addBinding(ref, exp);
    
    ref = FIELD_STORAGE_COLUMNS + "#" + DataTableBindingProvider.PROPERTY_ENABLED;
    exp = " {" + FIELD_STORAGE_CONTEXT + "} != null && length({" + FIELD_STORAGE_CONTEXT + "}) > 0 && ({" + FIELD_STORAGE_VIEW + "} == null || " + DefaultFunctions.LENGTH + "({" + FIELD_STORAGE_VIEW
        + "}) == 0) && " + DefaultFunctions.FUNCTION_AVAILABLE + "({" + FIELD_STORAGE_CONTEXT + "}, '" + StorageHelper.F_STORAGE_COLUMNS + "')";
    FORMAT.addBinding(ref, exp);
    
    ref = FIELD_STORAGE_COLUMNS;
    exp = "({" + FIELD_STORAGE_CONTEXT + "} != null && length({" + FIELD_STORAGE_CONTEXT + "}) > 0 && {" + FIELD_STORAGE_TABLE + "} != null) ? " + DefaultFunctions.CALL_FUNCTION + "({"
        + FIELD_STORAGE_CONTEXT + "}, '" + StorageHelper.F_STORAGE_COLUMNS + "', {" + FIELD_STORAGE_TABLE + "}, {" + FIELD_STORAGE_COLUMNS + "}) : table()";
    FORMAT.addBinding(ref, exp);
    
    ref = FIELD_STORAGE_FILTER + "#" + DataTableBindingProvider.PROPERTY_ENABLED;
    exp = "{" + FIELD_STORAGE_CONTEXT + "} != null && length({" + FIELD_STORAGE_CONTEXT + "}) > 0 && ({" + FIELD_STORAGE_VIEW + "} == null || " + DefaultFunctions.LENGTH + "({" + FIELD_STORAGE_VIEW
        + "}) == 0) && " + DefaultFunctions.FUNCTION_AVAILABLE + "({" + FIELD_STORAGE_CONTEXT + "}, '" + StorageHelper.F_STORAGE_FILTER + "')";
    FORMAT.addBinding(ref, exp);
    
    ref = FIELD_STORAGE_FILTER;
    exp = "({" + FIELD_STORAGE_CONTEXT + "} != null && length({" + FIELD_STORAGE_CONTEXT + "}) > 0 && {" + FIELD_STORAGE_TABLE + "} != null) ? " + DefaultFunctions.CALL_FUNCTION + "({"
        + FIELD_STORAGE_CONTEXT + "}, '" + StorageHelper.F_STORAGE_FILTER + "', {" + FIELD_STORAGE_TABLE + "}, {" + FIELD_STORAGE_FILTER + "}) : table()";
    FORMAT.addBinding(ref, exp);
    
    ref = FIELD_STORAGE_SORTING + "#" + DataTableBindingProvider.PROPERTY_ENABLED;
    exp = "{" + FIELD_STORAGE_CONTEXT + "} != null && length({" + FIELD_STORAGE_CONTEXT + "}) > 0 && ({" + FIELD_STORAGE_VIEW + "} == null || " + DefaultFunctions.LENGTH + "({" + FIELD_STORAGE_VIEW
        + "}) == 0) && " + DefaultFunctions.FUNCTION_AVAILABLE + "({" + FIELD_STORAGE_CONTEXT + "}, '" + StorageHelper.F_STORAGE_COLUMNS + "')";
    FORMAT.addBinding(ref, exp);
    
    ref = FIELD_STORAGE_SORTING;
    exp = "({" + FIELD_STORAGE_CONTEXT + "} != null && length({" + FIELD_STORAGE_CONTEXT + "}) > 0 && {" + FIELD_STORAGE_TABLE + "} != null) ? " + DefaultFunctions.CALL_FUNCTION + "({"
        + FIELD_STORAGE_CONTEXT + "}, '" + StorageHelper.F_STORAGE_SORTING + "', {" + FIELD_STORAGE_TABLE + "}, {" + FIELD_STORAGE_SORTING + "}) : table()";
    
    FORMAT.addBinding(ref, exp);
  }
  
  public static final TableFormat TF_PARAMETERS = new TableFormat(1, 1);
  static
  {
    TF_PARAMETERS.addField(FieldFormat.create("<" + StorageHelper.CLASS_FIELD_INSTANCE_ID + "><S><F=N>"));
    TF_PARAMETERS.addField(FieldFormat.create("<" + EditData.CF_RELATION_FIELD + "><S><F=N>"));
  }
  
  public ForeignInstanceConverter()
  {
    editors.add(LongFieldFormat.EDITOR_FOREIGN_INSTANCE);
    types.add(String.valueOf(FieldFormat.LONG_FIELD));
    types.add(String.valueOf(FieldFormat.STRING_FIELD));
  }
  
  @Override
  public String convertToString(DataTable options)
  {
    
    return options.encode();
  }
  
  public static Reference toReference(DataTable options)
  {
    if (options == null)
      return new Reference();
    Reference ref = new Reference();
    for (DataRecord rec : options)
    {
      ref.setSchema("class");
      
      ref.setContext(rec.getString(FIELD_STORAGE_CONTEXT));
      ref.setEntity(rec.getString(FIELD_STORAGE_TABLE));
      ref.setEntityType(ContextUtils.ENTITY_INSTANCE);
    }
    return ref;
  }
  
  @Override
  public TableFormat getFormat()
  {
    return FORMAT;
  }
  
}
