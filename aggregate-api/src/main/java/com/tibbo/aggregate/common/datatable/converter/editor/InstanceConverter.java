package com.tibbo.aggregate.common.datatable.converter.editor;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.context.ContextUtils;
import com.tibbo.aggregate.common.context.Contexts;
import com.tibbo.aggregate.common.datatable.DataRecord;
import com.tibbo.aggregate.common.datatable.DataTable;
import com.tibbo.aggregate.common.datatable.DataTableBindingProvider;
import com.tibbo.aggregate.common.datatable.DataTableBuilding;
import com.tibbo.aggregate.common.datatable.FieldFormat;
import com.tibbo.aggregate.common.datatable.TableFormat;
import com.tibbo.aggregate.common.datatable.field.StringFieldFormat;
import com.tibbo.aggregate.common.expression.Reference;
import com.tibbo.aggregate.common.expression.function.DefaultFunctions;
import com.tibbo.aggregate.common.server.UtilitiesContextConstants;
import com.tibbo.aggregate.common.view.StorageHelper;

public class InstanceConverter extends AbstractEditorOptionsConverter
{
  public static final String FIELD_STORAGE_CONTEXT = "storageContext";
  public static final String FIELD_STORAGE_TABLE = "storageTable";
  public static final String FIELD_DASHBOARD = "dashboard";
  public static final String FIELD_ICON = "icon";
  
  public static final TableFormat FORMAT = new TableFormat(1, 1);
  static
  {
    String exp;
    String ref;
    String tableExp;
    String valueExp;
    String descriptionExp;
    
    FORMAT.addField(FieldFormat.create("<" + FIELD_STORAGE_CONTEXT + "><S><F=N><D=" + Cres.get().getString("storageContext") + ">").setEditor(StringFieldFormat.EDITOR_CONTEXT));
    FORMAT.addField(FieldFormat.create("<" + FIELD_STORAGE_TABLE + "><S><F=N><D=" + Cres.get().getString("acClassTable") + ">"));
    
    FORMAT.addField(FieldFormat.create("<" + FIELD_DASHBOARD + "><S><F=N><D=" + Cres.get().getString("dashboard") + ">").setEditor(StringFieldFormat.EDITOR_CONTEXT)
        .setEditorOptions(StringFieldFormat.encodeMaskEditorOptions(Contexts.TYPE_DASHBOARD, Contexts.CTX_DASHBOARDS)));
    
    FORMAT.addField(FieldFormat.create("<" + FIELD_ICON + "><S><F=N><D=" + Cres.get().getString("icon") + ">"));
    
    ref = FIELD_STORAGE_TABLE + "#" + DataTableBindingProvider.PROPERTY_CHOICES;
    tableExp = DefaultFunctions.CALL_FUNCTION + "('\" + {" + FIELD_STORAGE_CONTEXT + "} + \"', '" + StorageHelper.F_STORAGE_TABLES + "')";
    valueExp = "{" + DataTableBuilding.FIELD_SELECTION_VALUES_VALUE + "}";
    descriptionExp = "{" + DataTableBuilding.FIELD_SELECTION_VALUES_DESCRIPTION + "}";
    exp = "({" + FIELD_STORAGE_CONTEXT + "} != null && length({" + FIELD_STORAGE_CONTEXT + "}) > 0) ? " + DefaultFunctions.CALL_FUNCTION + "(\"" + Contexts.CTX_UTILITIES + "\", \""
        + UtilitiesContextConstants.F_SELECTION_VALUES + "\", \"" + tableExp + "\",  \"" + valueExp + "\",  \"" + descriptionExp + "\") : null";
    FORMAT.addBinding(ref, exp);
  }
  
  public static final TableFormat TF_PARAMETERS = new TableFormat(1, 1);
  static
  {
    TF_PARAMETERS.addField(FieldFormat.create("<" + StorageHelper.CLASS_FIELD_INSTANCE_ID + "><S><F=N>"));
  }
  
  public InstanceConverter()
  {
    editors.add(FieldFormat.EDITOR_INSTANCE);
    types.add(String.valueOf(FieldFormat.INTEGER_FIELD));
    types.add(String.valueOf(FieldFormat.FLOAT_FIELD));
    types.add(String.valueOf(FieldFormat.DOUBLE_FIELD));
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
    Reference ref = new Reference();
    
    if (options != null)
    {
      for (DataRecord rec : options)
      {
        ref.setSchema("class");
        ref.setContext(rec.getString(FIELD_STORAGE_CONTEXT));
        ref.setEntity(rec.getString(FIELD_STORAGE_TABLE));
        ref.setEntityType(ContextUtils.ENTITY_INSTANCE);
      }
    }
    
    return ref;
  }
  
  @Override
  public TableFormat getFormat()
  {
    return FORMAT;
  }
}
