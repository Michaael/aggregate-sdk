package com.tibbo.aggregate.common.datatable.field;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.context.Context;
import com.tibbo.aggregate.common.context.ContextUtils;
import com.tibbo.aggregate.common.context.Contexts;
import com.tibbo.aggregate.common.datatable.DataRecord;
import com.tibbo.aggregate.common.datatable.DataTable;
import com.tibbo.aggregate.common.datatable.DataTableException;
import com.tibbo.aggregate.common.datatable.DataTableUtils;
import com.tibbo.aggregate.common.datatable.FieldFormat;
import com.tibbo.aggregate.common.datatable.SimpleDataTable;
import com.tibbo.aggregate.common.datatable.TableFormat;
import com.tibbo.aggregate.common.datatable.ValidationException;
import com.tibbo.aggregate.common.datatable.converter.editor.ContextMaskConverter;
import com.tibbo.aggregate.common.datatable.converter.editor.EditorOptionsUtils;
import com.tibbo.aggregate.common.datatable.encoding.ClassicEncodingSettings;
import com.tibbo.aggregate.common.expression.Reference;
import com.tibbo.aggregate.common.security.KeyUtils;
import com.tibbo.aggregate.common.util.StringUtils;

public class StringFieldFormat extends FieldFormat<String>
{
  public static final String EDITOR_CONTEXT_MASK = "contextmask";
  public static final String EDITOR_CONTEXT = "context";
  public static final String EDITOR_EXPRESSION = "expression";
  public static final String EDITOR_FUNCTION_SELECTOR = "functionselector";
  public static final String EDITOR_ACTIVATOR = "activator";
  public static final String EDITOR_TARGET = "target";
  public static final String EDITOR_CODE = "code";
  public static final String EDITOR_TEXT = "text";
  public static final String EDITOR_TEXT_AREA = "textarea";
  public static final String EDITOR_EMBEDDED_TEXT_AREA = "etextarea";
  public static final String EDITOR_REFERENCE = "reference";
  public static final String EDITOR_PASSWORD = "password";
  public static final String EDITOR_FONT = "font";
  public static final String EDITOR_IP = "ip";
  public static final String EDITOR_HTML = "html";
  public static final String FIELD_ADDITIONAL_REFERENCES_REFERENCE = "reference";
  public static final String FIELD_ADDITIONAL_REFERENCES_DESCRIPTION = "description";
  public static final TableFormat ADDITIONAL_REFERENCES_FORMAT = new TableFormat();
  public static final String FIELD_DEFAULT_TABLE = "table";
  public static final String FIELD_DEFAULT_CONTEXT = "context";
  public static final String FIELD_REFERENCES = "references";
  public static final String FIELD_EXPECTED_RESULT = "expectedResult";
  public static final String FIELD_DEFAULT_TABLE_DESCRIPTION = "tableDescription";
  public static final String FIELD_DEFAULT_CONTEXT_DESCRIPTION = "contextDescription";
  public static final TableFormat EXPRESSION_BUILDER_OPTIONS_FORMAT = new TableFormat(1, 1);
  public static final String TEXT_EDITOR_MODE_AGGREGATE = "aggregate";
  public static final String TEXT_EDITOR_MODE_JAVA = "java";
  public static final String TEXT_EDITOR_MODE_XML = "xml";
  public static final String TEXT_EDITOR_MODE_SQL = "sql";
  public static final String TEXT_EDITOR_MODE_HTML = "html";
  public static final String TEXT_EDITOR_MODE_SHELLSCRIPT = "shellscript";
  public static final String TEXT_EDITOR_MODE_SMI_MIB = "smi-mib";
  public static final String CODE_EDITOR_MODE_ST = "st";
  public static final String CODE_EDITOR_MODE_JAVA = "java";
  public static final String CODE_EDITOR_MODE_JAVASCRIPT = "typescript";
  public static final String CODE_EDITOR_MODE_INTERPRETED_LANGUAGE = "interpretedLanguage";
  public static final String EDITOR_TARGET_MODE_MODELS = "models";
  public static final String EDITOR_TARGET_MODE_ACTIONS_ONLY = "targetActionsOnly";
  public static final String EDITOR_TARGET_MODE_FUNCTIONS_ONLY = "targetFunctionsOnly";
  private static final String CONTEXT_EDITOR_TYPES_SEPARATOR = " ";

  static
  {
    ADDITIONAL_REFERENCES_FORMAT.addField(FieldFormat.create(FIELD_ADDITIONAL_REFERENCES_REFERENCE, FieldFormat.STRING_FIELD, Cres.get().getString("reference")));
    ADDITIONAL_REFERENCES_FORMAT.addField(FieldFormat.create(FIELD_ADDITIONAL_REFERENCES_DESCRIPTION, FieldFormat.STRING_FIELD, Cres.get().getString("description")));
  }

  static
  {
    EXPRESSION_BUILDER_OPTIONS_FORMAT.addField(FieldFormat.create(FIELD_DEFAULT_CONTEXT, FieldFormat.STRING_FIELD, Cres.get().getString("conDefaultContext")).setNullable(true));
    EXPRESSION_BUILDER_OPTIONS_FORMAT.addField(FieldFormat.create(FIELD_DEFAULT_TABLE, FieldFormat.DATATABLE_FIELD, Cres.get().getString("defaultTable")).setNullable(true));
    EXPRESSION_BUILDER_OPTIONS_FORMAT.addField(
        FieldFormat.create(FIELD_REFERENCES, FieldFormat.DATATABLE_FIELD, Cres.get().getString("references")).setNullable(true).setDefault(new SimpleDataTable(ADDITIONAL_REFERENCES_FORMAT)));
    EXPRESSION_BUILDER_OPTIONS_FORMAT.addField(FieldFormat.create(FIELD_EXPECTED_RESULT, FieldFormat.STRING_FIELD, Cres.get().getString("expectedResultType")).setNullable(true));
    EXPRESSION_BUILDER_OPTIONS_FORMAT.addField(FieldFormat.create(FIELD_DEFAULT_CONTEXT_DESCRIPTION, FieldFormat.STRING_FIELD, Cres.get().getString("defaultContextDesc")).setNullable(true));
    EXPRESSION_BUILDER_OPTIONS_FORMAT.addField(FieldFormat.create(FIELD_DEFAULT_TABLE_DESCRIPTION, FieldFormat.STRING_FIELD, Cres.get().getString("defaultTableDesc")).setNullable(true));
  }
  
  public StringFieldFormat(String name)
  {
    super(name);
    setTransferEncode(true);
  }
  
  public static String encodeExpressionEditorOptions(Map<Reference, String> references)
  {
    return encodeExpressionEditorOptions((String) null, null, references);
  }
  
  public static String encodeExpressionEditorOptions(Context defaultContext, DataTable defaultTable, Map<Reference, String> references)
  {
    return encodeExpressionEditorOptions(defaultContext != null ? defaultContext.getPath() : null, defaultTable, references, null, null, null);
  }
  
  public static String encodeExpressionEditorOptions(String defaultContext, DataTable defaultTable, Map<Reference, String> references)
  {
    return encodeExpressionEditorOptions(defaultContext, defaultTable, references, null, null, null);
  }
  
  public static String encodeExpressionEditorOptions(String defaultContext, DataTable defaultTable)
  {
    return encodeExpressionEditorOptions(defaultContext, defaultTable, null, null, null, null);
  }
  
  public static String encodeExpressionEditorOptions(String defaultContext)
  {
    return encodeExpressionEditorOptions(defaultContext, new SimpleDataTable(), null, null, null, null);
  }
  
  public static String encodeExpressionEditorOptions(String defaultContext, DataTable defaultTable, Map<Reference, String> references, String expectedResult, String defaultContextDescription,
      String defaultTableDescription)
  {
    DataRecord op = new DataRecord(EXPRESSION_BUILDER_OPTIONS_FORMAT);

    op.addValue(defaultContext);

    op.addValue(defaultTable);

    if (references != null)
    {
      DataTable refs = new SimpleDataTable(ADDITIONAL_REFERENCES_FORMAT);
      for (Map.Entry<Reference, String> entry : references.entrySet())
      {
        refs.addRecord().addString(entry.getKey().getImage()).addString(entry.getValue() != null ? entry.getValue() : entry.getKey().getImage());
      }
      op.addValue(refs);
    }

    op.addValue(expectedResult);

    op.addValue(defaultContextDescription);

    op.addValue(defaultTableDescription);

    return op.wrap().encode(false);
  }
  
  public static String encodeMaskEditorOptions(String contextType)
  {
    return encodeMaskEditorOptions(null, Collections.singletonList(contextType), null);
  }
  
  public static String encodeMaskEditorOptions(Class contextClass)
  {
    return encodeMaskEditorOptions(null, Collections.singletonList(ContextUtils.getTypeForClass(contextClass)), null);
  }
  
  public static String encodeMaskEditorOptions(String contextType, List<String> contextMasks)
  {
    return encodeMaskEditorOptions(null, Collections.singletonList(contextType), contextMasks);
  }
  
  public static String encodeMaskEditorOptions(String rootContext, List<String> contextTypes, List<String> contextMasks)
  {
    DataTable options = EditorOptionsUtils.createEditorOptionsTable(String.valueOf(StringFieldFormat.STRING_FIELD), StringFieldFormat.EDITOR_CONTEXT_MASK);

    options.addRecord();
    options.rec().setValue(ContextMaskConverter.FIELD_ROOT_CONTEXT, rootContext);

    if (contextTypes != null)
    {
      DataTable typesTable = options.rec().getDataTable(ContextMaskConverter.FIELD_CONTEXT_TYPES).clone();
      for (String contextType : contextTypes)
      {
        typesTable.addRecord(contextType);
      }
      options.rec().setValue(ContextMaskConverter.FIELD_CONTEXT_TYPES, typesTable);
    }

    if (contextMasks != null)
    {
      DataTable masksTable = options.rec().getDataTable(ContextMaskConverter.FIELD_CONTEXT_MASKS).clone();
      for (String contextMask : contextMasks)
      {
        masksTable.addRecord(contextMask);
      }
      options.rec().setValue(ContextMaskConverter.FIELD_CONTEXT_MASKS, masksTable);
    }

    return options.encode();
  }
  
  public static String encodeMaskEditorOptions(Class contextClass, String containerName)
  {
    return encodeMaskEditorOptions(ContextUtils.getTypeForClass(contextClass), containerName);
  }
  
  public static String encodeMaskEditorOptions(String contextType, String containerName)
  {

    String[] masks = new String[] {
        ContextUtils.createName(Contexts.CTX_USERS, ContextUtils.CONTEXT_GROUP_MASK, containerName, ContextUtils.CONTEXT_GROUP_MASK, ContextUtils.CONTEXT_GROUP_MASK),
        ContextUtils.createName(Contexts.CTX_USERS, ContextUtils.CONTEXT_GROUP_MASK, ContextUtils.groupsContextName(containerName), ContextUtils.CONTEXT_GROUP_MASK, ContextUtils.CONTEXT_GROUP_MASK,
            ContextUtils.CONTEXT_GROUP_MASK) };

    return StringFieldFormat.encodeMaskEditorOptions(contextType, Arrays.asList(masks));
  }
  
  public static ContextMaskConverter.Options decodeMaskEditorOptions(String options)
  {
    if (options != null && DataTableUtils.isEncodedTable(options))
    {

      DataTable optionsTable;
      try
      {
        optionsTable = new SimpleDataTable(options);
        return new ContextMaskConverter.Options(optionsTable.rec());
      }
      catch (DataTableException ex)
      {
        // Ignore
      }
    }
    else if (options != null)
    {
      List<String> contextTypes = StringUtils.split(options, CONTEXT_EDITOR_TYPES_SEPARATOR.charAt(0));
      ContextMaskConverter.Options editorOptions = new ContextMaskConverter.Options();
      editorOptions.setContextTypes(contextTypes);
      return editorOptions;
    }
    return new ContextMaskConverter.Options();
  }
  
  @Override
  public char getType()
  {
    return FieldFormat.STRING_FIELD;
  }
  
  @Override
  public Class getFieldClass()
  {
    return String.class;
  }
  
  @Override
  public Class getFieldWrappedClass()
  {
    return String.class;
  }
  
  @Override
  public String getNotNullDefault()
  {
    return "";
  }
  
  @Override
  protected Object convertValue(Object value) throws ValidationException
  {
    if (value != null && !(value instanceof String))
    {
      value = value.toString();
    }

    return value;
  }
  
  @Override
  public String valueFromString(String value, ClassicEncodingSettings settings, boolean validate)
  {
    if ((isEncrypted() || EDITOR_PASSWORD.equals(getEditor())) && settings != null && settings.isEncryptedPasswords())
    {
      try
      {
        value = KeyUtils.decryptString(value);
      }
      catch (SecurityException e)
      {
        throw e;
      }
      catch (Throwable e)
      {
        return value;
      }
    }

    return value;
  }
  
  @Override
  public String valueToString(String value, ClassicEncodingSettings settings)
  {
    if ((isEncrypted() || EDITOR_PASSWORD.equals(getEditor())) && settings != null && settings.isEncryptedPasswords())
    {
      try
      {
        value = KeyUtils.encryptString(value);
      }
      catch (SecurityException e)
      {
        throw e;
      }
      catch (Throwable e)
      {
        return value;
      }
    }

    return value;
  }
  
  @Override
  public List<String> getSuitableEditors()
  {
    return Arrays.asList(EDITOR_LIST, EDITOR_CONTEXT_MASK, EDITOR_CONTEXT, EDITOR_TEXT_AREA, EDITOR_EMBEDDED_TEXT_AREA, EDITOR_TEXT, EDITOR_CODE, EDITOR_REFERENCE, EDITOR_EXPRESSION, EDITOR_TARGET,
        EDITOR_ACTIVATOR, EDITOR_PASSWORD, EDITOR_BAR, EDITOR_BYTES, EDITOR_FONT, EDITOR_IP, EDITOR_HTML, EDITOR_FUNCTION_SELECTOR, EDITOR_INSTANCE, EDITOR_FOREIGN_INSTANCE);
  }
}
