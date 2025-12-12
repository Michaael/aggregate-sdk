package com.tibbo.aggregate.common.datatable.encoding;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.text.translate.AggregateTranslator;
import org.apache.commons.lang3.text.translate.CharSequenceTranslator;
import org.apache.commons.lang3.text.translate.EntityArrays;
import org.apache.commons.lang3.text.translate.LookupTranslator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.tibbo.aggregate.common.binding.Binding;
import com.tibbo.aggregate.common.data.Data;
import com.tibbo.aggregate.common.datatable.AbstractDataTable;
import com.tibbo.aggregate.common.datatable.DataRecord;
import com.tibbo.aggregate.common.datatable.DataTable;
import com.tibbo.aggregate.common.datatable.DataTableBuilding;
import com.tibbo.aggregate.common.datatable.FieldFormat;
import com.tibbo.aggregate.common.datatable.SimpleDataTable;
import com.tibbo.aggregate.common.datatable.TableFormat;
import com.tibbo.aggregate.common.datatable.field.DataTableFieldFormat;
import com.tibbo.aggregate.common.datatable.field.DateFieldFormat;
import com.tibbo.aggregate.common.util.StringUtils;

public class JsonEncodingHelper
{
  private static final String WRAPPER_FIELD = "wrapper";
  public static final String VALUE_FIELD = "value";
  
  public static String tableToJson(DataTable payload)
  {
    return tableToJson(payload, true);
  }
  
  public static String tableToJson(DataTable payload, boolean convertLongToString)
  {
    return encodeToJson(payload, convertLongToString).toString();
  }
  
  public static String tableFormatToJson(TableFormat tf)
  {
    return createTableFormatJsonObject(tf, true).toString();
  }
  
  public static String messageToJson(String key, String message)
  {
    JSONObject result = new JSONObject();
    result.put(key, message);
    return result.toJSONString();
  }
  
  private static JSONArray encodeToJson(DataTable payload, boolean convertLongToString)
  {
    JSONArray records = new JSONArray();
    
    if (payload == null)
    {
      return records;
    }
    else
    {
      for (DataRecord dr : payload)
      {
        JSONObject record = new JSONObject();
        for (int i = 0; i < dr.getFieldCount(); i++)
        {
          record.put(dr.getFormat(i).getName(), encodeFieldValueToJSON(dr.getFormat(i), dr.getValue(i), convertLongToString));
        }
        records.add(record);
      }
    }
    return records;
  }
  
  private static Object encodeFieldValueToJSON(FieldFormat ff, Object value, boolean convertLongToString)
  {
    switch (ff.getType())
    {
      case FieldFormat.DATATABLE_FIELD:
        return encodeToJson((DataTable) value, convertLongToString);
      
      case FieldFormat.DATE_FIELD:
      case FieldFormat.COLOR_FIELD:
        return ff.valueToString(value);
      case FieldFormat.LONG_FIELD:
        return convertLongToString ? ff.valueToString(value) : value;
      // TODO: Attention! Change this if the encode to json has been broken!
      case FieldFormat.DATA_FIELD:
        Data data = (Data) value;
        return data != null ? data.toJsonString() : "";
      
      case FieldFormat.STRING_FIELD:
        return value;
      
      default:
        return value;
    }
  }
  
  private static JSONObject createTableFormatJsonObject(TableFormat tf, boolean convertLongToString)
  {
    JSONObject format = new JSONObject();
    
    if (tf == null)
    {
      return format;
    }
    
    format.put(XMLEncodingHelper.ATTRIBUTE_MINIMUM_RECORDS, tf.getMinRecords());
    format.put(XMLEncodingHelper.ATTRIBUTE_MAXIMUM_RECORDS, tf.getMaxRecords());
    format.put(XMLEncodingHelper.ATTRIBUTE_REORDERABLE, tf.isReorderable());
    format.put(XMLEncodingHelper.ATTRIBUTE_UNRESIZABLE, tf.isUnresizable());
    
    JSONArray fields = new JSONArray();
    if (tf.getFields().size() > 0)
    {
      for (FieldFormat ff : tf.getFields())
      {
        JSONObject field = new JSONObject();
        
        field.put(XMLEncodingHelper.ATTRIBUTE_NAME, ff.getName());
        field.put(XMLEncodingHelper.ATTRIBUTE_TYPE, String.valueOf(ff.getType()));
        field.put(XMLEncodingHelper.ATTRIBUTE_DESCRIPTION, ff.getDescription());
        field.put(XMLEncodingHelper.ATTRIBUTE_NULLABLE, ff.isNullable());
        field.put(XMLEncodingHelper.ATTRIBUTE_OPTIONAL, ff.isOptional());
        field.put(DataTableBuilding.FIELD_FIELDS_FORMAT_EXTSELVALS, ff.isExtendableSelectionValues());
        field.put(XMLEncodingHelper.ATTRIBUTE_READONLY, ff.isReadonly());
        field.put(XMLEncodingHelper.ATTRIBUTE_NOT_REPLICATED, ff.isNotReplicated());
        field.put(XMLEncodingHelper.ATTRIBUTE_HIDDEN, ff.isHidden());
        field.put(XMLEncodingHelper.ATTRIBUTE_INLINE, ff.isInlineData());
        field.put(XMLEncodingHelper.ATTRIBUTE_ADVANCED, ff.isAdvanced());
        field.put(XMLEncodingHelper.ATTRIBUTE_KEY_FIELD, ff.isKeyField());
        field.put(XMLEncodingHelper.ATTRIBUTE_ICON, ff.getIcon());
        field.put(XMLEncodingHelper.ATTRIBUTE_EDITOR, ff.getEditor());
        field.put(XMLEncodingHelper.ELEMENT_HELP, ff.getHelp());
        field.put(XMLEncodingHelper.ELEMENT_GROUP, ff.getGroup());
        field.put(XMLEncodingHelper.ELEMENT_VALIDATORS, ff.getEncodedValidators(new ClassicEncodingSettings(true)));
        
        field.put(XMLEncodingHelper.ELEMENT_EDITOR_OPTIONS, ff.getEditorOptions());
        
        String vals = ff.getEncodedValidators(new ClassicEncodingSettings(true));
        field.put(XMLEncodingHelper.ELEMENT_VALIDATORS, vals);
        
        // Adding selection values
        JSONArray selVals = new JSONArray();
        
        Map<Object, String> sv = ff.getSelectionValues();
        if (sv != null && sv.size() > 0)
        {
          for (Object key : sv.keySet())
          {
            JSONObject val = new JSONObject();
            val.put(XMLEncodingHelper.ATTRIBUTE_DESCRIPTION, sv.get(key));
            val.put(XMLEncodingHelper.ELEMENT_FIELD_VALUE, encodeFieldValueToJSON(ff, key, convertLongToString));
            selVals.add(val);
          }
        }
        field.put(XMLEncodingHelper.ELEMENT_SELECTION_VALUES, selVals);
        
        field.put(XMLEncodingHelper.ELEMENT_HELP, ff.getHelp());
        field.put(XMLEncodingHelper.ELEMENT_GROUP, ff.getGroup());
        
        field.put(DataTableBuilding.FIELD_FIELDS_FORMAT_SELVALS, selVals);
        
        final Object defaultValue = ff.getDefaultValue();
        
        if (ff.getType() == FieldFormat.DATATABLE_FIELD && defaultValue != null && !AbstractDataTable.DEFAULT_FORMAT.equals(((DataTable) defaultValue).getFormat()))
        {
          JSONObject defTable = new JSONObject();
          
          DataTable defdt = (DataTable) defaultValue;
          TableFormat defdtFormat = defdt.getFormat();
          
          defTable.put(XMLEncodingHelper.ELEMENT_FORMAT, createTableFormatJsonObject(defdtFormat, convertLongToString));
          defTable.put(XMLEncodingHelper.ELEMENT_RECORDS, encodeToJson(defdt, convertLongToString));
          
          field.put(XMLEncodingHelper.ELEMENT_DEFAULT_VALUE, defTable);
        }
        else
        {
          field.put(XMLEncodingHelper.ELEMENT_DEFAULT_VALUE, encodeFieldValueToJSON(ff, defaultValue, convertLongToString));
        }
        
        fields.add(field);
      }
    }
    
    format.put(XMLEncodingHelper.ELEMENT_FIELDS, fields);
    
    if (!tf.getBindings().isEmpty())
    {
      JSONArray bindings = new JSONArray();
      
      for (Binding b : tf.getBindings())
      {
        JSONObject binding = new JSONObject();
        binding.put(XMLEncodingHelper.ELEMENT_REFERENCE, b.getTarget().toString());
        binding.put(XMLEncodingHelper.ELEMENT_EXPRESSION, b.getExpression().toString());
        
        bindings.add(binding);
      }
      format.put(XMLEncodingHelper.ELEMENT_BINDINGS, bindings);
    }
    
    String validators = tf.getEncodedTableValidators(new ClassicEncodingSettings(true));
    if (!StringUtils.isEmpty(validators))
    {
      format.put(XMLEncodingHelper.ELEMENT_VALIDATORS, validators);
    }
    
    return format;
  }
  
  public static DataTable tableFromJson(String payload, boolean convertUnequalFieldTypesToString) throws Exception
  {
    String jsonText = "{\"" + WRAPPER_FIELD + "\":" + payload + "}"; // wrap initial text to make sure outer element is always JSONObject
    
    JSONParser parser = new JSONParser();
    
    JSONObject json = (JSONObject) parser.parse(jsonText);
    
    return processJSONObject(json, convertUnequalFieldTypesToString);
  }
  
  private static DataTable processJSONObject(JSONObject jsonObject, boolean convertUnequalFieldTypesToString)
  {
    TableFormat tableFormat = calculateTableFormat(jsonObject, convertUnequalFieldTypesToString, true, false);
    DataTable result = new SimpleDataTable(tableFormat);
    
    fillDataTableWithObject(jsonObject, result, true);
    
    return result;
  }
  
  public static TableFormat calculateTableFormat(JSONObject jsonObject, boolean convertUnequalFieldTypesToString, boolean innerDataTable, boolean implicitCasting)
  {
    TableFormat result = new TableFormat(1, 1);
    
    for (Object keyObject : jsonObject.keySet())
    {
      FieldFormat fieldFormat = extractFieldFormatFromNode((String) keyObject, jsonObject.get(keyObject), convertUnequalFieldTypesToString, innerDataTable, implicitCasting);
      result.addField(fieldFormat);
    }
    
    return result;
  }
  
  public static void fillDataTableWithObject(JSONObject jsonObject, DataTable dataTable, boolean innerDataTable)
  {
    DataRecord dataRecord = dataTable.addRecord();
    
    for (Object keyObject : jsonObject.keySet())
    {
      String key = (String) keyObject;
      Object value = jsonObject.get(key);
      
      if (value instanceof JSONObject)
      {
        DataTable table = dataRecord.getDataTable(key);
        fillDataTableWithObject((JSONObject) value, table, innerDataTable);
        dataRecord.setValue(key, table);
      }
      else if (value instanceof JSONArray)
      {
        DataTable table = dataRecord.getDataTable(key);
        fillDataTableWithArray((JSONArray) value, table, innerDataTable);
        dataRecord.setValue(key, table);
      }
      else if (value instanceof String)
      {
        Date date = getDateIfDateFormat(value);
        if (date != null)
        {
          dataRecord.setValue(key, date);
        }
        else
        {
          dataRecord.setValue(key, value);
        }
      }
      else
      {
        dataRecord.setValue(key, value);
      }
    }
  }
  
  public static void fillDataTableWithArray(JSONArray jsonArray, DataTable dataTable, boolean innerDataTable)
  {
    for (Object arrayObject : jsonArray)
    {
      
      if (arrayObject instanceof JSONObject)
      {
        if (innerDataTable)
        {
          DataRecord dataRecord = dataTable.addRecord();
          DataTable table = dataRecord.getDataTable(VALUE_FIELD);
          fillDataTableWithObject((JSONObject) arrayObject, table, true);
          dataRecord.setValue(VALUE_FIELD, table);
        }
        else
        {
          fillDataTableWithObject((JSONObject) arrayObject, dataTable, false);
        }
      }
      else if (arrayObject instanceof JSONArray)
      {
        if (innerDataTable)
        {
          DataRecord dataRecord = dataTable.addRecord();
          DataTable table = dataRecord.getDataTable(VALUE_FIELD);
          fillDataTableWithArray((JSONArray) arrayObject, table, true);
          dataRecord.setValue(VALUE_FIELD, table);
        }
        else
        {
          fillDataTableWithArray((JSONArray) arrayObject, dataTable, false);
        }
      }
      else
      {
        DataRecord dataRecord = dataTable.addRecord();
        dataRecord.setValue(VALUE_FIELD, arrayObject);
      }
    }
  }
  
  private static FieldFormat extractFieldFormatFromNode(String key, Object node, boolean convertUnequalFieldTypesToString, boolean innerDataTable, boolean implicitCasting)
  {
    FieldFormat result = null;
    
    if (node instanceof String)
    {
      Date date = getDateIfDateFormat(node);
      if (date != null)
      {
        result = FieldFormat.create(key, FieldFormat.DATE_FIELD);
      }
      else
      {
        result = FieldFormat.create(key, FieldFormat.STRING_FIELD, true);
      }
    }
    else if (node instanceof Long)
    {
      result = FieldFormat.create(key, FieldFormat.LONG_FIELD);
    }
    else if (node instanceof Boolean)
    {
      result = FieldFormat.create(key, FieldFormat.BOOLEAN_FIELD);
    }
    else if (node instanceof Double)
    {
      result = FieldFormat.create(key, FieldFormat.DOUBLE_FIELD);
    }
    else if (node instanceof JSONArray)
    {
      result = FieldFormat.create(key, FieldFormat.DATATABLE_FIELD);
      
      JSONArray jSONArray = (JSONArray) node;
      LinkedList<FieldFormat> results = new LinkedList<>();
      
      for (int i = 0; i < jSONArray.size(); i++)
      {
        results.add(extractFieldFormatFromNode(VALUE_FIELD, jSONArray.get(i), convertUnequalFieldTypesToString, innerDataTable, implicitCasting));
      }
      
      FieldFormat innerField = mergeFormats(results, VALUE_FIELD, convertUnequalFieldTypesToString, implicitCasting);
      TableFormat innerFormat = new TableFormat(innerField);
      DataTable innerTable = new SimpleDataTable(innerFormat);
      if (innerDataTable)
        result.setDefault(innerTable);
      else
        result.setDefault(innerField.getDefaultValue());
    }
    else if (node instanceof JSONObject)
    {
      result = FieldFormat.create(key, FieldFormat.DATATABLE_FIELD);
      
      TableFormat innerFormat = calculateTableFormat((JSONObject) node, convertUnequalFieldTypesToString, innerDataTable, implicitCasting);
      DataTable innerTable = new SimpleDataTable(innerFormat);
      
      result.setDefault(innerTable);
    }
    else
    {
      result = createDefaultFieldFormat(key);
    }
    
    result.setNullable(true);
    
    return result;
  }
  
  private static Date getDateIfDateFormat(Object node)
  {
    try
    {
      return DateFieldFormat.dateFromString(node.toString());
    }
    catch (Exception e)
    {
      return null;
    }
  }
  
  static FieldFormat createDefaultFieldFormat(String fieldName)
  {
    return FieldFormat.create(fieldName, FieldFormat.STRING_FIELD).setNullable(true).setDefault(null);
  }
  
  private static FieldFormat mergeFormats(LinkedList<FieldFormat> formats, String fieldName, Boolean convertUnequalFieldTypesToString, boolean implicitCasting)
  {
    if (formats.isEmpty())
    {
      return FieldFormat.create(fieldName, FieldFormat.DATATABLE_FIELD);
    }
    
    if (!(formats.get(0).getDefaultValue() instanceof DataTable))
      return chooseFormat(formats, convertUnequalFieldTypesToString, fieldName);
    
    final FieldFormatDefiner fieldFormatDefiner = new FieldFormatDefiner(convertUnequalFieldTypesToString, implicitCasting);
    
    HashMap<String, LinkedList<FieldFormat>> fieldsMergeMap = new HashMap<>();
    
    for (FieldFormat format : formats)
    {
      Object def = format.getDefaultValue();
      if (def instanceof DataTable)
      {
        TableFormat tableFormat = ((DataTable) def).getFormat();
        for (FieldFormat ff : tableFormat.getFields())
        {
          final String key = ff.getName();
          if (ff instanceof DataTableFieldFormat)
          {
            if (!fieldsMergeMap.containsKey(key))
              fieldsMergeMap.put(key, new LinkedList<>());
            
            List mergeList = fieldsMergeMap.get(key);
            mergeList.add(ff);
          }
          else
            fieldFormatDefiner.put(key, ff);
        }
      }
    }
    
    for (String key : fieldsMergeMap.keySet())
    {
      fieldFormatDefiner.put(key, mergeFormats(fieldsMergeMap.get(key), key, convertUnequalFieldTypesToString, implicitCasting));
    }
    
    final TableFormat mergedFormat = new TableFormat();
    for (String key : fieldFormatDefiner.getFieldNames())
    {
      mergedFormat.addField(fieldFormatDefiner.get(key));
    }
    
    FieldFormat format = FieldFormat.create(fieldName, FieldFormat.DATATABLE_FIELD);
    format.setDefault(new SimpleDataTable(mergedFormat));
    format.setNullable(true);
    return format;
  }
  
  private static FieldFormat chooseFormat(List<FieldFormat> fieldFormats, Boolean convertUnequalFieldTypesToString, String fieldName)
  {
    if (convertUnequalFieldTypesToString && !isEqualFieldTypes(fieldFormats))
    {
      FieldFormat ff = FieldFormat.create(fieldName, FieldFormat.STRING_FIELD);
      ff.setNullable(true);
      return ff;
    }
    
    for (FieldFormat ff : fieldFormats)
    {
      if (!looksLikeDefaultField(ff))
        return ff;
    }
    
    return fieldFormats.get(0);
  }
  
  private static Boolean isEqualFieldTypes(List<FieldFormat> fieldFormats)
  {
    char type = fieldFormats.get(0).getType();
    for (FieldFormat ff : fieldFormats)
    {
      if (ff.getType() != type)
        return false;
    }
    return true;
  }
  
  static boolean looksLikeDefaultField(FieldFormat ff)
  {
    return ff.getType() == FieldFormat.STRING_FIELD && ff.isNullable() && ff.getDefaultValue() == null;
  }
  
  public static class EscapeJsonWithoutUnicode
  {
    private static final CharSequenceTranslator ESCAPE_JSON_WITHOUT_UNICODE = new AggregateTranslator(new LookupTranslator(new String[][] { { "\"", "\\\"" }, { "\\", "\\\\" } }),
        new LookupTranslator(EntityArrays.JAVA_CTRL_CHARS_ESCAPE()));
    
    public static String translate(String input)
    {
      return ESCAPE_JSON_WITHOUT_UNICODE.translate(input);
    }
  }
}
