package com.tibbo.aggregate.common.datatable;

import java.lang.reflect.Constructor;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.Log;
import com.tibbo.aggregate.common.binding.Binding;
import com.tibbo.aggregate.common.context.ContextException;
import com.tibbo.aggregate.common.context.Contexts;
import com.tibbo.aggregate.common.datatable.converter.editor.EditorOptionsUtils;
import com.tibbo.aggregate.common.datatable.encoding.ClassicEncodingSettings;
import com.tibbo.aggregate.common.datatable.field.StringFieldFormat;
import com.tibbo.aggregate.common.datatable.validator.AbstractFieldValidator;
import com.tibbo.aggregate.common.datatable.validator.LimitsValidator;
import com.tibbo.aggregate.common.datatable.validator.TableExpressionValidator;
import com.tibbo.aggregate.common.datatable.validator.TableKeyFieldsValidator;
import com.tibbo.aggregate.common.datatable.validator.ValidatorHelper;
import com.tibbo.aggregate.common.expression.function.DefaultFunctions;
import com.tibbo.aggregate.common.server.UtilitiesContextConstants;

public class DataTableBuilding
{
  public static final String FIELD_TABLE_FORMAT_MIN_RECORDS = "minRecords";
  public static final String FIELD_TABLE_FORMAT_MAX_RECORDS = "maxRecords";
  public static final String FIELD_TABLE_FORMAT_FIELDS = "fields";
  public static final String FIELD_TABLE_FORMAT_REORDERABLE = "reorderable";
  public static final String FIELD_TABLE_FORMAT_UNRESIZABLE = "unresizable";
  public static final String FIELD_TABLE_FORMAT_BINDINGS = "bindings";
  public static final String FIELD_TABLE_FORMAT_ENCODED = "encoded";
  public static final String FIELD_TABLE_FORMAT_NAMING_EXPRESSION = "namingExpression";
  
  public static final String FIELD_FIELDS_FORMAT_OLDNAME = "oldname";
  public static final String FIELD_FIELDS_FORMAT_NAME = "name";
  public static final String FIELD_FIELDS_FORMAT_TYPE = "type";
  public static final String FIELD_FIELDS_FORMAT_DESCRIPTION = "description";
  public static final String FIELD_FIELDS_FORMAT_GROUP = "group";
  public static final String FIELD_FIELDS_FORMAT_DEFAULT_VALUE = "defaultValue";
  public static final String FIELD_FIELDS_FORMAT_HIDDEN = "hidden";
  public static final String FIELD_FIELDS_FORMAT_INLINE = "inline";
  public static final String FIELD_FIELDS_FORMAT_ENCRYPTED = "encrypted";
  public static final String FIELD_FIELDS_FORMAT_READONLY = "readonly";
  public static final String FIELD_FIELDS_FORMAT_NULLABLE = "nullable";
  public static final String FIELD_FIELDS_FORMAT_KEY = "key";
  public static final String FIELD_FIELDS_FORMAT_SELVALS = "selvals";
  public static final String FIELD_FIELDS_FORMAT_EXTSELVALS = "extselvals";
  public static final String FIELD_FIELDS_FORMAT_HELP = "help";
  public static final String FIELD_FIELDS_FORMAT_EDITOR = "editor";
  public static final String FIELD_FIELDS_FORMAT_EDITOR_OPTIONS = "editorOptions";
  public static final String FIELD_FIELDS_FORMAT_VALIDATORS = "validators";
  
  public static final String FIELD_SELECTION_VALUES_VALUE = "value";
  public static final String FIELD_SELECTION_VALUES_DESCRIPTION = "description";
  
  public static final String FIELD_BINDINGS_TARGET = "target";
  public static final String FIELD_BINDINGS_EXPRESSION = "expression";
  
  public static final String FIELD_VALIDATORS_VALIDATOR = "validator";
  public static final String FIELD_VALIDATORS_OPTIONS = "options";
  
  public static final TableFormat SELECTION_VALUES_FORMAT = new TableFormat(true);
  
  static
  {
    SELECTION_VALUES_FORMAT.addField("<" + FIELD_SELECTION_VALUES_VALUE + "><S><F=NK><D=" + Cres.get().getString("value") + ">");
    SELECTION_VALUES_FORMAT.addField("<" + FIELD_SELECTION_VALUES_DESCRIPTION + "><S><D=" + Cres.get().getString("description") + "><V=<L=1 " + Integer.MAX_VALUE + ">>");
    
    SELECTION_VALUES_FORMAT.addTableValidator(new TableKeyFieldsValidator());
  }
  
  public static final TableFormat BINDINGS_FORMAT = new TableFormat(true);
  
  static
  {
    BINDINGS_FORMAT.addField("<" + FIELD_BINDINGS_TARGET + "><S><D=" + Cres.get().getString("target") + "><V=<L=1 " + Integer.MAX_VALUE + ">>");
    BINDINGS_FORMAT.addField("<" + FIELD_BINDINGS_EXPRESSION + "><S><D=" + Cres.get().getString("expression") + "><E=" + StringFieldFormat.EDITOR_EXPRESSION + "><V=<L=1 " + Integer.MAX_VALUE + ">>");
  }
  
  public static final TableFormat VALIDATORS_FORMAT = new TableFormat(true);
  
  static
  {
    FieldFormat ff = FieldFormat.create("<" + FIELD_VALIDATORS_VALIDATOR + "><S><F=N><D=" + Cres.get().getString("type") + ">");
    ff.setSelectionValues(DataTableUtils.getValidatorSelectionValues());
    VALIDATORS_FORMAT.addField(ff);
    VALIDATORS_FORMAT.addField("<" + FIELD_VALIDATORS_OPTIONS + "><S><D=" + Cres.get().getString("options") + ">");
  }
  
  public static final String FIELD_EDITOR_OPTIONS_SIMPLE_FORMAT_OPTIONS = "options";
  
  public static final TableFormat EDITOR_OPTIONS_SIMPLE_FORMAT = new TableFormat(1, 1);
  
  static
  {
    EDITOR_OPTIONS_SIMPLE_FORMAT.addField(FieldFormat.create(FIELD_EDITOR_OPTIONS_SIMPLE_FORMAT_OPTIONS, FieldFormat.STRING_FIELD, Cres.get().getString("dtEditorOptions")));
  }
  
  public static final TableFormat FIELDS_FORMAT = new TableFormat(true);
  
  static
  {
    FIELDS_FORMAT.addTableValidator(new TableKeyFieldsValidator());
    
    FIELDS_FORMAT.addField(FieldFormat.create("<" + FIELD_FIELDS_FORMAT_OLDNAME + "><S><F=H>"));
    
    FieldFormat ff = FieldFormat.create("<" + FIELD_FIELDS_FORMAT_NAME + "><S><F=K><D=" + Cres.get().getString("name") + ">");
    ff.getValidators().add(ValidatorHelper.NAME_SYNTAX_VALIDATOR);
    FIELDS_FORMAT.addField(ff);
    
    ff = FieldFormat.create("<" + FIELD_FIELDS_FORMAT_TYPE + "><S><A=" + FieldFormat.INTEGER_FIELD + "><D=" + Cres.get().getString("type") + ">");
    ff.setSelectionValues(FieldFormat.getTypeSelectionValues());
    FIELDS_FORMAT.addField(ff);
    
    ff = FieldFormat.create("<" + FIELD_FIELDS_FORMAT_DESCRIPTION + "><S><F=N><D=" + Cres.get().getString("description") + ">");
    ff.getValidators().add(ValidatorHelper.DESCRIPTION_SYNTAX_VALIDATOR);
    FIELDS_FORMAT.addField(ff);
    
    FIELDS_FORMAT.addField(FieldFormat.create("<" + FIELD_FIELDS_FORMAT_DEFAULT_VALUE + "><S><F=N><A=><D=" + Cres.get().getString("default") + ">"));
    FIELDS_FORMAT.addField(FieldFormat.create("<" + FIELD_FIELDS_FORMAT_READONLY + "><B><D=" + Cres.get().getString("dtReadOnly") + ">"));
    FIELDS_FORMAT.addField(FieldFormat.create("<" + FIELD_FIELDS_FORMAT_NULLABLE + "><B><D=" + Cres.get().getString("dtNullable") + ">"));
    FIELDS_FORMAT.addField(FieldFormat.create("<" + FIELD_FIELDS_FORMAT_KEY + "><B><D=" + Cres.get().getString("dtKeyField") + ">"));
    
    ff = FieldFormat.create("<" + FIELD_FIELDS_FORMAT_SELVALS + "><T><D=" + Cres.get().getString("dtSelectionValues") + ">");
    ff.setDefault(new SimpleDataTable(SELECTION_VALUES_FORMAT));
    FIELDS_FORMAT.addField(ff);
    
    FIELDS_FORMAT.addField(FieldFormat.create("<" + FIELD_FIELDS_FORMAT_EXTSELVALS + "><B><D=" + Cres.get().getString("dtExtendableSelVals") + ">"));
    
    ff = FieldFormat.create("<" + FIELD_FIELDS_FORMAT_HIDDEN + "><B><D=" + Cres.get().getString("hidden") + ">");
    ff.setAdvanced(true);
    FIELDS_FORMAT.addField(ff);
    
    ff = FieldFormat.create("<" + FIELD_FIELDS_FORMAT_INLINE + "><B><D=" + Cres.get().getString("inline") + ">");
    ff.setAdvanced(true);
    FIELDS_FORMAT.addField(ff);

    ff = FieldFormat.create("<" + FIELD_FIELDS_FORMAT_ENCRYPTED + "><B><D=" + Cres.get().getString("encrypted") + ">");
    ff.setAdvanced(true);
    FIELDS_FORMAT.addField(ff);
    
    ff = FieldFormat.create("<" + FIELD_FIELDS_FORMAT_HELP + "><S><F=N><D=" + Cres.get().getString("help") + ">");
    
    ff.getValidators().add(ValidatorHelper.DESCRIPTION_SYNTAX_VALIDATOR);
    ff.setEditor(StringFieldFormat.EDITOR_TEXT_AREA);
    FIELDS_FORMAT.addField(ff);
    
    ff = FieldFormat.create("<" + FIELD_FIELDS_FORMAT_EDITOR + "><S><F=N><D=" + Cres.get().getString("dtEditorViewer") + ">");
    ff.setSelectionValues(DataTableUtils.getEditorSelectionValues());
    ff.setExtendableSelectionValues(true);
    FIELDS_FORMAT.addField(ff);
    
    ff = FieldFormat.create("<" + FIELD_FIELDS_FORMAT_EDITOR_OPTIONS + "><T><F=N><D=" + Cres.get().getString("dtEditorOptions") + ">");
    FIELDS_FORMAT.addField(ff);
    
    String ref = FIELD_FIELDS_FORMAT_EDITOR_OPTIONS;
    String exp = DefaultFunctions.CALL_FUNCTION + "(\"" + Contexts.CTX_UTILITIES + "\", \"" + UtilitiesContextConstants.F_EDITOR_OPTIONS + "\", {" + FIELD_FIELDS_FORMAT_TYPE + "}, {"
        + FIELD_FIELDS_FORMAT_EDITOR
        + "}, {" + FIELD_FIELDS_FORMAT_EDITOR_OPTIONS + "})";
    FIELDS_FORMAT.addBinding(ref, exp);
    
    ref = FIELD_FIELDS_FORMAT_EDITOR + "#" + DataTableBindingProvider.PROPERTY_CHOICES;
    exp = "{" + Contexts.CTX_UTILITIES + ":" + UtilitiesContextConstants.F_EDITORS + "('{" + FIELD_FIELDS_FORMAT_TYPE + "}')}";
    FIELDS_FORMAT.addBinding(ref, exp);
    
    ff = FieldFormat.create("<" + FIELD_FIELDS_FORMAT_VALIDATORS + "><T><F=N><D=" + Cres.get().getString("dtValidators") + ">");
    ff.setDefault(new SimpleDataTable(VALIDATORS_FORMAT));
    FIELDS_FORMAT.addField(ff);
    
    ff = FieldFormat.create("<" + FIELD_FIELDS_FORMAT_GROUP + "><S><F=N><D=" + Cres.get().getString("group") + ">");
    FIELDS_FORMAT.addField(ff);
    
  }
  
  public static final TableFormat TABLE_FORMAT = new TableFormat(1, 1);
  
  static
  {
    FieldFormat ff = FieldFormat.create("<" + FIELD_TABLE_FORMAT_MIN_RECORDS + "><I><A=0><D=" + Cres.get().getString("dtMinRecords") + ">");
    ff.getValidators().add(new LimitsValidator(0, Integer.MAX_VALUE));
    TABLE_FORMAT.addField(ff);
    
    ff = FieldFormat.create("<" + FIELD_TABLE_FORMAT_MAX_RECORDS + "><I><F=E><A=" + Integer.MAX_VALUE + "><D=" + Cres.get().getString("dtMaxRecords") + "><S=<" + Cres.get().getString("dtNotLimited")
        + "=" + Integer.MAX_VALUE + ">>");
    ff.getValidators().add(new LimitsValidator(0, Integer.MAX_VALUE));
    TABLE_FORMAT.addField(ff);
    
    ff = FieldFormat.create("<" + FIELD_TABLE_FORMAT_FIELDS + "><T><D=" + Cres.get().getString("fields") + ">");
    ff.setDefault(new SimpleDataTable(FIELDS_FORMAT));
    TABLE_FORMAT.addField(ff);
    
    ff = FieldFormat.create("<" + FIELD_TABLE_FORMAT_REORDERABLE + "><B><D=" + Cres.get().getString("dtReorderable") + ">");
    TABLE_FORMAT.addField(ff);
    
    ff = FieldFormat.create("<" + FIELD_TABLE_FORMAT_UNRESIZABLE + "><B><D=" + Cres.get().getString("dtUnresizable") + ">");
    TABLE_FORMAT.addField(ff);
    
    ff = FieldFormat.create("<" + FIELD_TABLE_FORMAT_BINDINGS + "><T><D=" + Cres.get().getString("bindings") + ">");
    ff.setDefault(new SimpleDataTable(BINDINGS_FORMAT));
    TABLE_FORMAT.addField(ff);
    
    ff = FieldFormat.create("<" + FIELD_TABLE_FORMAT_ENCODED + "><S><F=R><D=" + Cres.get().getString("dtEncodedFormat") + "><H=" + Cres.get().getString("dtEncodedFormatHelp") + "><E="
        + StringFieldFormat.EDITOR_TEXT_AREA + ">");
    TABLE_FORMAT.addField(ff);
    
    ff = FieldFormat.create("<" + FIELD_TABLE_FORMAT_NAMING_EXPRESSION + "><S><F=N><D=" + Cres.get().getString("namingExpression") + "><E="
        + StringFieldFormat.EDITOR_EXPRESSION + ">");
    TABLE_FORMAT.addField(ff);
    
    TABLE_FORMAT.addTableValidator(new TableExpressionValidator("{" + FIELD_TABLE_FORMAT_MIN_RECORDS + "} <= {" + FIELD_TABLE_FORMAT_MAX_RECORDS + "} ? null : '"
        + Cres.get().getString("dtMaxRecsLessThenMin") + "'"));
    
    TABLE_FORMAT.setNamingExpression(DefaultFunctions.PRINT + "({" + FIELD_TABLE_FORMAT_FIELDS + "}, '{" + FIELD_FIELDS_FORMAT_NAME + "}', ', ')");
  }
  
  public static TableFormat createTableFormat(DataTable formatTable) throws ContextException
  {
    return createTableFormat(formatTable, null, false);
  }
  
  public static TableFormat createTableFormat(DataTable formatTable, ClassicEncodingSettings settings, boolean allowNull) throws ContextException
  {
    DataRecord rec = formatTable.rec();
    
    int minRecords = rec.getInt(FIELD_TABLE_FORMAT_MIN_RECORDS);
    int maxRecords = rec.getInt(FIELD_TABLE_FORMAT_MAX_RECORDS);
    
    DataTable fields = rec.getDataTable(FIELD_TABLE_FORMAT_FIELDS);
    
    if (allowNull && fields.getRecordCount() == 0)
    {
      return null;
    }
    
    boolean reorderable = rec.getBoolean(FIELD_TABLE_FORMAT_REORDERABLE);
    boolean unresizable = rec.getBoolean(FIELD_TABLE_FORMAT_UNRESIZABLE);
    
    DataTable bindings = rec.getDataTable(FIELD_TABLE_FORMAT_BINDINGS);
    
    TableFormat format = createTableFormat(minRecords, maxRecords, reorderable, fields, settings);
    
    format.setUnresizable(unresizable);
    
    for (DataRecord binding : bindings)
    {
      format.addBinding(binding.getString(FIELD_BINDINGS_TARGET), binding.getString(FIELD_BINDINGS_EXPRESSION));
    }
    
    if (rec.getString(FIELD_TABLE_FORMAT_NAMING_EXPRESSION) != null)
      format.setNamingExpression(rec.getString(FIELD_TABLE_FORMAT_NAMING_EXPRESSION));
    
    return format;
  }
  
  public static TableFormat createTableFormat(int minRecords, int maxRecords, boolean reorderable, DataTable fields, ClassicEncodingSettings settings) throws ContextException
  {
    TableFormat rf = new TableFormat(reorderable);
    
    rf.setMinRecords(minRecords);
    rf.setMaxRecords(maxRecords);
    
    boolean hasKeys = false;
    
    for (DataRecord fdata : fields)
    {
      FieldFormat ff = FieldFormat.create(fdata.getString(FIELD_FIELDS_FORMAT_NAME), fdata.getString(FIELD_FIELDS_FORMAT_TYPE).charAt(0));
      
      ff.setDescription(fdata.getString(FIELD_FIELDS_FORMAT_DESCRIPTION));
      ff.setHelp(fdata.getString(FIELD_FIELDS_FORMAT_HELP));
      
      String def = fdata.getString(FIELD_FIELDS_FORMAT_DEFAULT_VALUE);
      if (def != null && def.length() > 0)
      {
        ff.setDefault(ff.valueFromString(def, settings, true));
      }
      
      ff.setReadonly(fdata.getBoolean(FIELD_FIELDS_FORMAT_READONLY));
      ff.setNullable(fdata.getBoolean(FIELD_FIELDS_FORMAT_NULLABLE));
      if (fdata.hasField(FIELD_FIELDS_FORMAT_HIDDEN))
      {
        ff.setHidden(fdata.getBoolean(FIELD_FIELDS_FORMAT_HIDDEN));
      }
      
      if (fdata.hasField(FIELD_FIELDS_FORMAT_INLINE))
      {
        ff.setInlineData(fdata.getBoolean(FIELD_FIELDS_FORMAT_INLINE));
      }

      if (fdata.hasField(FIELD_FIELDS_FORMAT_ENCRYPTED))
      {
        ff.setEncrypted(fdata.getBoolean(FIELD_FIELDS_FORMAT_ENCRYPTED));
      }
      
      if (fdata.hasField(FIELD_FIELDS_FORMAT_DESCRIPTION))
      {
        ff.setDescription(fdata.getString(FIELD_FIELDS_FORMAT_DESCRIPTION));
      }
      
      if (fdata.hasField(FIELD_FIELDS_FORMAT_GROUP))
      {
        ff.setGroup(fdata.getString(FIELD_FIELDS_FORMAT_GROUP));
      }
      
      boolean key = fdata.getFormat().hasField(FIELD_FIELDS_FORMAT_KEY) ? fdata.getBoolean(FIELD_FIELDS_FORMAT_KEY) : false;
      ff.setKeyField(key);
      if (key)
      {
        hasKeys = true;
      }
      
      DataTable selVals = fdata.getDataTable(FIELD_FIELDS_FORMAT_SELVALS);
      
      ff.setExtendableSelectionValues(fdata.getBoolean(FIELD_FIELDS_FORMAT_EXTSELVALS));
      
      if (selVals.getRecordCount() > 0)
      {
        Map<Object, String> sv = new LinkedHashMap();
        
        for (DataRecord rec : selVals)
        {
          String val = rec.getString(FIELD_SELECTION_VALUES_VALUE);
          sv.put(val != null ? ff.valueFromString(val) : null, rec.getString(FIELD_SELECTION_VALUES_DESCRIPTION));
        }
        
        if (!ff.isExtendableSelectionValues())
        {
          if (!sv.containsKey(ff.getDefaultValue()))
          {
            if (def != null || ff.isNullable())
            {
              sv.put(def != null ? ff.valueFromString(def) : null, def != null ? def.toString() : Cres.get().getString("dtNullCell"));
            }
          }
        }
        
        ff.setSelectionValues(sv);
      }
      
      ff.setEditor(fdata.getString(FIELD_FIELDS_FORMAT_EDITOR));
      if (fdata.hasField(FIELD_FIELDS_FORMAT_EDITOR_OPTIONS))
      {
        String eo = EditorOptionsUtils.convertToString(fdata);
        ff.setEditorOptions(eo);
      }
      
      DataTable validators = fdata.getDataTable(FIELD_FIELDS_FORMAT_VALIDATORS);
      if (validators != null)
      {
        for (DataRecord validator : validators)
        {
          try
          {
            Constructor<? extends AbstractFieldValidator> contructor;
            
            String type = validator.getString(FIELD_VALIDATORS_VALIDATOR);
            String options = validator.getString(FIELD_VALIDATORS_OPTIONS);
            
            if (type == null)
              continue;
            
            if (options != null && !options.isEmpty())
            {
              if (type.equals(String.valueOf(FieldFormat.VALIDATOR_LIMITS)))
              {
                contructor = FieldFormat.getValidatorToTypeMap().get(type).getConstructor(FieldFormat.class, String.class);
                ff.addValidator(contructor.newInstance(ff, options));
              }
              else
              {
                contructor = FieldFormat.getValidatorToTypeMap().get(type).getConstructor(String.class);
                ff.addValidator(contructor.newInstance(options));
              }
            }
            else
            {
              contructor = FieldFormat.getValidatorToTypeMap().get(type).getConstructor();
              ff.addValidator(contructor.newInstance());
            }
          }
          catch (Exception ex)
          {
            Log.DATATABLE.error("Error validating data table field", ex);
          }
        }
      }
      
      rf.addField(ff);
    }
    
    if (hasKeys)
    {
      rf.addTableValidator(new TableKeyFieldsValidator());
    }
    return rf;
  }
  
  public static DataTable formatToFieldsTable(TableFormat tf, boolean readOnly)
  {
    return formatToFieldsTable(tf, readOnly, null);
  }
  
  public static DataTable formatToFieldsTable(TableFormat tf, boolean readOnly, ClassicEncodingSettings settings)
  {
    return formatToFieldsTable(tf, readOnly, settings, true);
  }
  
  public static DataTable formatToFieldsTable(TableFormat tf, boolean readOnly, ClassicEncodingSettings settings, boolean ignoreHiddenFields)
  {
    DataTable res = new SimpleDataTable(FIELDS_FORMAT);
    
    for (FieldFormat ff : tf)
    {
      if (ignoreHiddenFields && ff.isHidden())
      {
        continue;
      }
      
      DataTable selVals = new SimpleDataTable(SELECTION_VALUES_FORMAT);
      
      if (ff.getSelectionValues() != null)
      {
        for (Object value : ff.getSelectionValues().keySet())
        {
          selVals.addRecord(ff.valueToString(value, settings), ff.getSelectionValues().get(value));
        }
      }
      
      DataTable validators = new SimpleDataTable(VALIDATORS_FORMAT);
      if (ff.getValidators() != null)
      {
        for (Object validator : ff.getValidators())
        {
          validators.addRecord(((AbstractFieldValidator) validator).getType(), ((AbstractFieldValidator) validator).encode());
        }
      }
      
      DataRecord rec = res.addRecord();
      String type = String.valueOf(ff.getType());
      String help = ff.getHelp();
      String helpTxt = help;
      if (help != null)
      {
        Pattern HelpTxtPattern = Pattern.compile("[\\p{Cntrl}[^\\p{Graph}]]", Pattern.UNICODE_CHARACTER_CLASS);
        helpTxt = HelpTxtPattern.matcher(helpTxt).replaceAll(" ");
      }
      
      rec.setValue(FIELD_FIELDS_FORMAT_OLDNAME, ff.getName());
      rec.setValue(FIELD_FIELDS_FORMAT_NAME, ff.getName());
      rec.setValue(FIELD_FIELDS_FORMAT_TYPE, type);
      rec.setValue(FIELD_FIELDS_FORMAT_DESCRIPTION, ff.getDescription());
      rec.setValue(FIELD_FIELDS_FORMAT_DEFAULT_VALUE, ff.valueToString(ff.getDefaultValue(), settings));
      rec.setValue(FIELD_FIELDS_FORMAT_READONLY, readOnly || ff.isReadonly());
      rec.setValue(FIELD_FIELDS_FORMAT_NULLABLE, ff.isNullable());
      rec.setValue(FIELD_FIELDS_FORMAT_KEY, ff.isKeyField());
      rec.setValue(FIELD_FIELDS_FORMAT_SELVALS, selVals);
      rec.setValue(FIELD_FIELDS_FORMAT_EXTSELVALS, ff.isExtendableSelectionValues());
      rec.setValue(FIELD_FIELDS_FORMAT_HIDDEN, ff.isHidden());
      rec.setValue(FIELD_FIELDS_FORMAT_INLINE, ff.isInlineData());
      rec.setValue(FIELD_FIELDS_FORMAT_ENCRYPTED, ff.isEncrypted());
      rec.setValue(FIELD_FIELDS_FORMAT_HELP, helpTxt);
      rec.setValue(FIELD_FIELDS_FORMAT_GROUP, ff.getGroup());
      rec.setValue(FIELD_FIELDS_FORMAT_VALIDATORS, validators);
      
      try
      {
        String editor = ff.getEditor();
        rec.setValue(FIELD_FIELDS_FORMAT_EDITOR, editor);
        
        DataTable optionsTable = EditorOptionsUtils.createEditorOptionsTable(type, editor, ff.getEditorOptions());
        rec.setValue(FIELD_FIELDS_FORMAT_EDITOR_OPTIONS, optionsTable);
      }
      catch (Exception ex)
      {
        // Not every editor is suitable here. Make sure all editors are listed in EDITOR_SELECTION_VALUES.
      }
    }
    
    return res;
  }
  
  public static DataTable formatToTable(TableFormat tf, boolean ignoreHiddenFields)
  {
    return formatToTable(tf, null, ignoreHiddenFields);
  }
  
  public static DataTable formatToTable(TableFormat tf)
  {
    return formatToTable(tf, null);
  }
  
  public static DataTable formatToTable(TableFormat tf, ClassicEncodingSettings settings)
  {
    return formatToTable(tf, settings, true);
  }
  
  public static DataTable formatToTable(TableFormat tf, ClassicEncodingSettings settings, boolean ignoreHiddenFields)
  {
    DataRecord rec = new DataRecord(TABLE_FORMAT);
    
    rec.addInt(tf.getMinRecords());
    rec.addInt(tf.getMaxRecords());
    rec.addDataTable(formatToFieldsTable(tf, false, settings, ignoreHiddenFields));
    rec.addBoolean(tf.isReorderable());
    rec.addBoolean(tf.isUnresizable());
    
    DataTable bindings = new SimpleDataTable(BINDINGS_FORMAT);
    for (Binding binding : tf.getBindings())
    {
      try
      {
        bindings.addRecord().addString(binding.getTarget().getImage()).addString(binding.getExpression().getText());
      }
      catch (Exception ex)
      {
        Log.DATATABLE.warn(ex.getMessage(), ex);
      }
    }
    rec.addDataTable(bindings);
    
    rec.addString(tf.encode(settings != null ? settings : new ClassicEncodingSettings(true)));
    
    if (tf.getNamingExpression() != null)
      rec.addString(tf.getNamingExpression().getText());
    
    return rec.wrap();
  }
  
}
