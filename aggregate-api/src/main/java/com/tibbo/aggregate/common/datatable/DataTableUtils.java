package com.tibbo.aggregate.common.datatable;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.binding.Binding;
import com.tibbo.aggregate.common.binding.DefaultBindingProcessor;
import com.tibbo.aggregate.common.context.CallerController;
import com.tibbo.aggregate.common.context.Context;
import com.tibbo.aggregate.common.context.ContextException;
import com.tibbo.aggregate.common.context.ContextManager;
import com.tibbo.aggregate.common.data.Data;
import com.tibbo.aggregate.common.datatable.encoding.ClassicEncodingSettings;
import com.tibbo.aggregate.common.datatable.encoding.TransferEncodingHelper;
import com.tibbo.aggregate.common.datatable.field.ColorFieldFormat;
import com.tibbo.aggregate.common.datatable.field.DataFieldFormat;
import com.tibbo.aggregate.common.datatable.field.DataTableFieldFormat;
import com.tibbo.aggregate.common.datatable.field.DateFieldFormat;
import com.tibbo.aggregate.common.datatable.field.IntFieldFormat;
import com.tibbo.aggregate.common.datatable.field.LongFieldFormat;
import com.tibbo.aggregate.common.datatable.field.StringFieldFormat;
import com.tibbo.aggregate.common.expression.DefaultReferenceResolver;
import com.tibbo.aggregate.common.expression.Evaluator;
import com.tibbo.aggregate.common.util.CloneUtils;
import com.tibbo.aggregate.common.util.ErrorCollector;
import com.tibbo.aggregate.common.util.StringUtils;
import com.tibbo.aggregate.common.util.Util;

public class DataTableUtils
{
  public enum FilterMode
  {
    TEXT, REGEXP, EXPRESSION
  }
  
  public static final String NAMING_ENVIRONMENT_SHORT_DATA = "short";
  public static final String NAMING_ENVIRONMENT_FULL_DATA = "full";
  
  public final static char ELEMENT_START = '\u001c';
  public final static char ELEMENT_END = '\u001d';
  public final static char ELEMENT_NAME_VALUE_SEPARATOR = '\u001e';
  
  public final static char ELEMENT_VISIBLE_START = '<';
  public final static char ELEMENT_VISIBLE_END = '>';
  public final static char ELEMENT_VISIBLE_NAME_VALUE_SEPARATOR = '=';
  
  public final static String DATA_TABLE_NULL = String.valueOf('\u001a');
  public final static String DATA_TABLE_VISIBLE_NULL = "<NULL>";
  
  private static final Map<Object, String> EDITOR_SELECTION_VALUES = new LinkedHashMap<>();
  
  static
  {
    EDITOR_SELECTION_VALUES.put(null, Cres.get().getString("default"));
    
    EDITOR_SELECTION_VALUES.put(FieldFormat.EDITOR_LIST, Cres.get().getString("dtEditorList"));
    EDITOR_SELECTION_VALUES.put(DateFieldFormat.EDITOR_DATE, Cres.get().getString("date"));
    EDITOR_SELECTION_VALUES.put(DateFieldFormat.EDITOR_TIME, Cres.get().getString("time"));
    EDITOR_SELECTION_VALUES.put(FieldFormat.EDITOR_BAR, Cres.get().getString("dtEditorBar"));
    EDITOR_SELECTION_VALUES.put(FieldFormat.EDITOR_BYTES, Cres.get().getString("dtEditorBytes"));
    EDITOR_SELECTION_VALUES.put(FieldFormat.EDITOR_FORMAT_STRING, Cres.get().getString("dtEditorFormatValue"));
    EDITOR_SELECTION_VALUES.put(FieldFormat.EDITOR_INSTANCE, Cres.get().getString("dtEditorInstance"));
    EDITOR_SELECTION_VALUES.put(LongFieldFormat.EDITOR_PERIOD, Cres.get().getString("dtEditorPeriod"));
    EDITOR_SELECTION_VALUES.put(LongFieldFormat.EDITOR_FOREIGN_INSTANCE, Cres.get().getString("dtEditorForeignInstance"));
    EDITOR_SELECTION_VALUES.put(StringFieldFormat.EDITOR_EXPRESSION, Cres.get().getString("expression"));
    EDITOR_SELECTION_VALUES.put(StringFieldFormat.EDITOR_FUNCTION_SELECTOR, Cres.get().getString("functionSelector"));
    EDITOR_SELECTION_VALUES.put(StringFieldFormat.EDITOR_PASSWORD, Cres.get().getString("password"));
    EDITOR_SELECTION_VALUES.put(StringFieldFormat.EDITOR_TEXT, Cres.get().getString("dtEditorTextEditor"));
    EDITOR_SELECTION_VALUES.put(StringFieldFormat.EDITOR_HTML, Cres.get().getString("dtEditorHtml"));
    EDITOR_SELECTION_VALUES.put(StringFieldFormat.EDITOR_TEXT_AREA, Cres.get().getString("dtEditorTextArea"));
    EDITOR_SELECTION_VALUES.put(StringFieldFormat.EDITOR_EMBEDDED_TEXT_AREA, Cres.get().getString("dtEditorEmbeddedTextArea"));
    EDITOR_SELECTION_VALUES.put(StringFieldFormat.EDITOR_CONTEXT, Cres.get().getString("context"));
    EDITOR_SELECTION_VALUES.put(StringFieldFormat.EDITOR_CONTEXT_MASK, Cres.get().getString("conContextMask"));
    EDITOR_SELECTION_VALUES.put(StringFieldFormat.EDITOR_FONT, Cres.get().getString("font"));
    EDITOR_SELECTION_VALUES.put(StringFieldFormat.EDITOR_IP, Cres.get().getString("dtEditorIp"));
    EDITOR_SELECTION_VALUES.put(ColorFieldFormat.EDITOR_BOX, Cres.get().getString("dtEditorBox"));
    EDITOR_SELECTION_VALUES.put(DataFieldFormat.EDITOR_IMAGE, Cres.get().getString("image"));
    EDITOR_SELECTION_VALUES.put(DataFieldFormat.EDITOR_SOUND, Cres.get().getString("sound"));
    EDITOR_SELECTION_VALUES.put(DataFieldFormat.EDITOR_HEX, Cres.get().getString("dtEditorHex"));
    EDITOR_SELECTION_VALUES.put(DataFieldFormat.EDITOR_REFERENCE, Cres.get().getString("dtEditorReference"));
    EDITOR_SELECTION_VALUES.put(StringFieldFormat.EDITOR_ACTIVATOR, Cres.get().getString("activator"));
    EDITOR_SELECTION_VALUES.put(StringFieldFormat.EDITOR_CODE, Cres.get().getString("dtEditorCodeEditor"));
    EDITOR_SELECTION_VALUES.put(IntFieldFormat.EDITOR_SPINNER, Cres.get().getString("wSpinner"));
    EDITOR_SELECTION_VALUES.put(IntFieldFormat.EDITOR_EVENT_LEVEL, Cres.get().getString("efEventLevel"));
    EDITOR_SELECTION_VALUES.put(DataTableFieldFormat.EDITOR_DATE_RANGE, Cres.get().getString("dtEditorDateRange"));
  }
  
  private static final Map<Object, String> VALIDATOR_SELECTION_VALUES = new LinkedHashMap<>();
  
  static
  {
    VALIDATOR_SELECTION_VALUES.put(null, Cres.get().getString("default"));
    
    VALIDATOR_SELECTION_VALUES.put(String.valueOf(FieldFormat.VALIDATOR_ID), Cres.get().getString("dtIdValidator"));
    VALIDATOR_SELECTION_VALUES.put(String.valueOf(FieldFormat.VALIDATOR_LIMITS), Cres.get().getString("dtLimitsValidator"));
    VALIDATOR_SELECTION_VALUES.put(String.valueOf(FieldFormat.VALIDATOR_NON_NULL), Cres.get().getString("dtNonNullValidator"));
    VALIDATOR_SELECTION_VALUES.put(String.valueOf(FieldFormat.VALIDATOR_REGEX), Cres.get().getString("dtRegexValidator"));
    VALIDATOR_SELECTION_VALUES.put(String.valueOf(FieldFormat.VALIDATOR_EXPRESSION), Cres.get().getString("dtExpressionValidator"));
  }
  
  public static String transferDecode(String value)
  {
    try
    {
      return TransferEncodingHelper.decode(value);
    }
    catch (Exception ex)
    {
      throw new IllegalArgumentException("Error decoding string value '" + value + "'", ex);
    }
  }
  
  public static String transferEncode(String value)
  {
    return TransferEncodingHelper.encode(value, 1);
  }
  
  public static void inlineData(DataTable table, ContextManager cm, CallerController cc) throws ContextException
  {
    if (table == null)
    {
      return;
    }
    
    for (FieldFormat ff : table.getFormat())
    {
      if (ff.getType() == FieldFormat.DATA_FIELD)
      {
        for (DataRecord rec : table)
        {
          Data data = rec.getData(ff.getName());
          if (data != null)
          {
            data.fetchData(cm, cc);
            data.setId(null);
          }
        }
      }
      
      if (ff.getType() == FieldFormat.DATATABLE_FIELD)
      {
        for (DataRecord rec : table)
        {
          DataTable dt = rec.getDataTable(ff.getName());
          inlineData(dt, cm, cc);
        }
      }
    }
  }
  
  public static Map<Object, String> getEditorSelectionValues()
  {
    return EDITOR_SELECTION_VALUES;
  }
  
  public static Map<Object, String> getValidatorSelectionValues()
  {
    return VALIDATOR_SELECTION_VALUES;
  }
  
  public static DataTable wrapToTable(List<Object> values)
  {
    Map<String, Object> tableSource = new LinkedHashMap<>();
    for (int i = 0; i < values.size(); i++)
    {
      tableSource.put(String.valueOf(i), values.get(i));
    }
    return DataTableUtils.wrapToTable(tableSource);
  }
  
  public static DataTable wrapToTable(Map<String, Object> values)
  {
    if (values.isEmpty())
    {
      return new SimpleDataTable();
    }
    
    TableFormat rf = new TableFormat();
    
    for (String field : values.keySet())
    {
      Object value = values.get(field);
      rf.addField(DataTableConversion.createFieldFormat(field, value));
    }
    
    DataRecord result = new DataRecord(rf);
    
    for (String field : values.keySet())
    {
      result.addValue(values.get(field));
    }
    
    return result.wrap();
  }
  
  public static Set<String> findDifferingFields(DataTable first, DataTable second)
  {
    if (first.getRecordCount() != second.getRecordCount())
    {
      return null;
    }
    
    Set<String> fields = new LinkedHashSet<>();
    
    for (FieldFormat ff : first.getFormat())
    {
      if (!second.getFormat().hasField(ff.getName()))
      {
        continue;
      }
      
      for (int i = 0; i < Math.min(first.getRecordCount(), second.getRecordCount()); i++)
      {
        if (!Util.equals(first.getRecord(i).getValue(ff.getName()), second.getRecord(i).getValue(ff.getName())))
        {
          fields.add(ff.getName());
        }
      }
    }
    
    return fields;
  }

  public static DataTable makeSubtable(DataTable table, Collection<String> fields)
  {
    TableFormat rf = new TableFormat(table.getFormat().getMinRecords(), table.getFormat().getMaxRecords());
    rf.setBindings(filterBindings(table.getFormat(), fields));
    
    for (String field : fields)
    {
      FieldFormat ff = table.getFormat(field);
      if (ff != null)
      {
        rf.addField(ff);
      }
    }
    
    DataTable result = new SimpleDataTable(rf);
    
    DataTableReplication.copyWithoutKeyFields(table, result, true, true, true, true, true, null);
    
    return result;
  }
  
  public static List<Binding> filterBindings(TableFormat tableFormat, Collection<String> fields)
  {
    List<FieldFormat> formats = tableFormat.getFields().stream().filter(fieldFormat -> !fields.contains(fieldFormat.getName())).collect(Collectors.toList());
    List<Binding> result = (List) CloneUtils.deepClone(new ArrayList<>(tableFormat.getBindings()));
    result = result.stream().filter(binding -> {
      Optional<FieldFormat> res = formats.stream().filter(fieldFormat -> bindingContainsField(binding, fieldFormat.getName())).findAny();
      return !res.isPresent();
    }).collect(Collectors.toList());
    return result;
  }
  
  private static boolean bindingContainsField(Binding binding, String field)
  {
    return binding.getTarget().getImage().contains(field) || binding.getExpression().getText().contains(field);
  }
  
  public static Evaluator createEvaluator(final DataTable dataTable, ContextManager contextManager, Context context)
  {
    DefaultReferenceResolver resolver = new DefaultReferenceResolver();
    resolver.setDefaultTable(dataTable);
    resolver.setContextManager(contextManager);
    resolver.setDefaultContext(context);
    
    return new Evaluator(resolver);
  }
  
  public static String fieldValueToString(FieldFormat ff, Object value)
  {
    if (ff.hasSelectionValues())
    {
      return value.toString();
    }
    
    if (value instanceof Data)
    {
      Data data = (Data) value;
      
      if (data.getData() == null) // Data blocks without data cannot be copied
      {
        return null;
      }
      else
      {
        /* Temporarily removing data block ID to avoid its duplication - see AGG-515 */
        Long id = data.getId();
        data.setId(null);
        String res = ff.valueToString(value);
        data.setId(id);
        return res;
      }
    }
    
    return ff.valueToEncodedString(value, new ClassicEncodingSettings(false, true));
  }
  
  // Older versions contained choice between UTF-8 and UTF-16
  public static Charset detectCharset(byte[] data)
  {
    return StringUtils.UTF8_CHARSET;
  }
  
  // this function has an opposite function findRowNumber() (be sure that logic is vice-versa)
  public static String createRecordKeyString(DataRecord record, Integer rowNumber, String keyField)
  {
    List<String> keyFields = Collections.emptyList();
    if (record != null)
    {
      final boolean autoKeyFields = keyField == null;
      keyFields = autoKeyFields ? record.getFormat().getKeyFields() : Collections.singletonList(keyField);
    }
    
    if (keyFields.isEmpty())
    {
      return Integer.toString(rowNumber);
    }
    
    return record.getValueAsString(keyFields.get(0));
  }
  
  public static DataTable processBindings(DataTable table, Evaluator evaluator)
  {
    return processBindings(table, evaluator, null, false);
  }
  
  public static DataTable processBindings(DataTable table, Evaluator evaluator, ErrorCollector errorCollector)
  {
    return processBindings(table, evaluator, errorCollector, false);
  }
  
  public static DataTable processBindings(DataTable table, Evaluator evaluator, ErrorCollector errorCollector, boolean split)
  {
    if (table == null)
    {
      return table;
    }
    
    if (table.getFormat().getBindings().size() == 0)
    {
      return table;
    }
    
    DataTable result;
    if (split)
    {
      result = table.clone();
      result.splitFormat();
    }
    else
    {
      result = table;
    }
    
    evaluator.getDefaultResolver().setDefaultTable(result);
    
    DefaultBindingProcessor processor = new DefaultBindingProcessor(new DataTableBindingProvider(result, errorCollector), evaluator);
    
    processor.start();
    
    return result;
  }
  
  public static void processBindingsForAllRows(DataTable table, Evaluator evaluator, ErrorCollector errorCollector)
  {
    // It seems like processBindings works only for first row. Therefore, we use this:
    for (DataRecord rec : table)
    {
      processBindings(rec.wrap(), evaluator, errorCollector, false);
    }
  }
  
  public static boolean isEncodedTable(String string)
  {
    return string != null && string.length() > 0 && string.charAt(0) == DataTableUtils.ELEMENT_START;
  }
  
  public static void removeFieldsWithBindings(TableFormat format, String[] fieldsToRemove)
  {
    final List<Binding> bindings = format.getBindings();
    final LinkedList<Binding> bindingsToRemove = new LinkedList<>();
    for (String fieldToRemove : fieldsToRemove)
    {
      for (Binding binding : bindings)
      {
        if (binding.getTarget().getImage().contains(fieldToRemove))
        {
          bindingsToRemove.add(binding);
        }
      }
      format.removeField(fieldToRemove);
    }
    for (Binding bindingToRemove : bindingsToRemove)
    {
      format.removeBinding(bindingToRemove);
    }
  }

  public static boolean isOneValueFormat(TableFormat format)
  {
    if (format == null)
    {
      return false;
    }
    
    int maxRec = format.getMaxRecords();
    int minRec = format.getMinRecords();
    int fieldCount = format.getFieldCount();
    
    if (maxRec == 1 && minRec == 1 && fieldCount == 1)
    {
      return format.getField(0).getType() != FieldFormat.DATATABLE_FIELD;
    }
    return false;
  }

  //<editor-fold desc="Custom Debug Renderer for DataTables">

  /**
   * Returns the contents of the table in a user-readable way. Intended to be used with IntelliJ IDEA Custom Type
   * Renderer. See <a href="https://tibbotech.atlassian.net/l/cp/eoxcFVZa">this KB article</a> for details.
   */
  @SuppressWarnings("unused")       // used by IntelliJ Debugger
  public static String prettyPrint(DataTable table)
  {
    return prettyPrint(table, 10);
  }

  public static String prettyPrint(DataTable table, int maxRecords)
  {
    return prettyPrint(table, maxRecords, 15);
  }

  public static String prettyPrint(DataTable table, int maxRecords, int columnWidthChars)
  {
    if (table.getRecordCount() == 1)
    {
      return transposedTable(table, maxRecords, columnWidthChars);
    }

    StringBuilder sb = new StringBuilder(" ");
    for (FieldFormat fieldFormat : table.getFormat().getFields())
    {
      StringBuilder name = limit(fieldFormat.getName(), columnWidthChars);
      sb.append(name).append(" | ");
    }
    int headerLength = sb.length();
    sb.append('\n');

    char[] divider = fill('-', (headerLength - 1));
    sb.append(' ').append(divider).append('\n');

    int recordIndex = 0;
    for (DataRecord record : table)
    {
      sb.append(' ');
      for (int fieldIndex = 0; fieldIndex < record.getFieldCount(); fieldIndex++)
      {
        StringBuilder value = limit(record.getValueAsString(fieldIndex), columnWidthChars);
        sb.append(value).append(" | ");
      }
      sb.append('\n');

      if (++recordIndex >= maxRecords)
      {
        sb.append(' ').append("…\n");
        break;
      }
    }

    return sb.toString();
  }

  private static String transposedTable(DataTable table, int maxRecords, int columnWidthChars)
  {
    StringBuilder sb = new StringBuilder(" ")
        .append(limit("Name", columnWidthChars))
        .append(" = ")
        .append(limit("Value", columnWidthChars))
        .append('\n');

    int headerWidth = sb.length() - 1;
    char[] divider = fill('-', headerWidth);
    sb.append(' ').append(divider).append('\n');

    TableFormat format = table.getFormat();
    DataRecord firstRecord = table.getRecord(0);
    int rowsLimit = Math.min(maxRecords, format.getFieldCount());

    for (int row = 0; row < rowsLimit; row++)
    {
      StringBuilder name = limit(format.getField(row).getName(), columnWidthChars);
      sb.append(' ').append(name).append(" = ");
      StringBuilder value = limit(firstRecord.getValueAsString(row), columnWidthChars);
      sb.append(value).append('\n');
    }

    if (maxRecords < format.getFieldCount())
    {
      sb.append(' ').append("…\n");
    }

    return sb.toString();
  }

  private static StringBuilder limit(String string, int limit)
  {
    if (string == null)
    {
      string = "";
    }
    StringBuilder sb = new StringBuilder(string);
    if (string.length() <= limit)
    {
      int tailLength = limit - string.length();
      char[] tailChars = fill(' ', tailLength);
      return sb.append(tailChars);
    }

    return sb.delete((limit - 1), string.length())
             .append("…");
  }

  private static char[] fill(char filler, int count)
  {
    char[] fillBuff = new char[count];
    Arrays.fill(fillBuff, filler);
    return fillBuff;
  }
  //</editor-fold>

}
