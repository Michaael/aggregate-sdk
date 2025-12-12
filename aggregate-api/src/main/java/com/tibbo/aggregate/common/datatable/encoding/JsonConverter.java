package com.tibbo.aggregate.common.datatable.encoding;

import java.text.MessageFormat;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.context.ContextException;
import com.tibbo.aggregate.common.datatable.DataRecord;
import com.tibbo.aggregate.common.datatable.DataTable;
import com.tibbo.aggregate.common.datatable.FieldFormat;
import com.tibbo.aggregate.common.datatable.SimpleDataTable;
import com.tibbo.aggregate.common.datatable.TableFormat;

public class JsonConverter
{
  
  public DataTable tableFromJson(String payload) throws ContextException
  {
    return tableFromJson(parse(payload));
  }
  
  public DataTable tableFromJson(String payload, boolean innerDataTable, boolean implicitCasting) throws ContextException
  {
    return tableFromJson(parse(payload), innerDataTable, implicitCasting);
  }
  
  public DataTable tableFromJson(Object payload) throws ContextException
  {
    return tableFromJson(payload, true, false);
  }
  
  public DataTable tableFromJson(Object payload, boolean innerDataTable, boolean implicitCasting) throws ContextException
  {
    if (payload instanceof JSONArray)
    {
      JSONArray array = (JSONArray) payload;
      if (array.isEmpty())
        return new SimpleDataTable(TableFormat.EMPTY_FORMAT);
      TableFormat format = JsonEncodingHelper.calculateTableFormat((JSONObject) array.get(0), false, innerDataTable, implicitCasting);
      format.setMinRecords(TableFormat.DEFAULT_MIN_RECORDS);
      format.setMaxRecords(TableFormat.DEFAULT_MAX_RECORDS);
      DataTable dt = new SimpleDataTable(format);
      array.forEach(object -> JsonEncodingHelper.fillDataTableWithObject((JSONObject) object, dt, innerDataTable));
      return dt;
    }
    else
    {
      throw new ContextException("JSON must be array");
    }
  }
  
  public DataTable createDataTable(TableFormat tf, String payload) throws ContextException
  {
    return createDataTable(tf, payload, true, false);
  }
  
  public DataTable createDataTable(TableFormat tf, String payload, boolean innerDataTable, boolean implicitCasting) throws ContextException
  {
    if (tf == null || tf.isEmpty())
      return tableFromJson(payload);
    
    Object json = parse(payload);
    
    if (json instanceof JSONArray)
    {
      DataTable dt = new SimpleDataTable(tf);
      JSONArray array = (JSONArray) json;
      for (Object object : array)
        fillDataTableWithObject((JSONObject) object, dt, innerDataTable, implicitCasting);
      return dt;
    }
    else
    {
      throw new ContextException("JSON must be array");
    }
  }
  
  public Map<String, Object> recordFromJson(TableFormat tf, String payload) throws ContextException
  {
    Map<String, Object> res = new LinkedHashMap<>();
    
    Object json = parse(payload);
    
    if (json instanceof JSONArray)
      throw new ContextException("JSON must be object");
    
    JSONObject jsonObject = (JSONObject) (json);
    
    for (Object keyObject : jsonObject.keySet())
    {
      String key = (String) keyObject;
      
      if (tf.getField(key) == null)
      {
        throw new ContextException(MessageFormat.format(Cres.get().getString("dtFieldNotFound"), key));
      }
      
      Object value = jsonObject.get(key);
      
      FieldFormat ff = tf.getField(key);
      
      if (value instanceof JSONArray)
      {
        if (ff.getType() != FieldFormat.DATATABLE_FIELD)
        {
          throw new ContextException(MessageFormat.format(Cres.get().getString("dtIllegalFieldValue"), value.toString(), key));
        }
        
        DataTable table;
        
        if (ff.getDefaultValue() != null && ff.getDefaultValue() instanceof DataTable)
        {
          table = (DataTable) ff.getDefaultValueCopy();
          JSONArray array = (JSONArray) value;
          for (Object object : array)
            fillDataTableWithObject((JSONObject) object, table, true, false);
        }
        else
        {
          table = tableFromJson(value);
        }
        
        res.put(key, table);
      }
      else
      {
        res.put(key, convertValue(ff, value));
      }
    }
    return res;
  }
  
  private Object convertValue(FieldFormat ff, Object value)
  {
    if (value == null)
      return null;
    switch (ff.getType())
    {
      case FieldFormat.DATATABLE_FIELD:
      case FieldFormat.DATE_FIELD:
      case FieldFormat.COLOR_FIELD:
      case FieldFormat.DATA_FIELD:
      case FieldFormat.LONG_FIELD:
        return ff.valueFromString((String) value);
      case FieldFormat.STRING_FIELD:
        return StringEscapeUtils.unescapeJson(value.toString());
      default:
        return value;
    }
  }
  
  private void fillDataTableWithObject(JSONObject jsonObject, DataTable dataTable, boolean innerDataTable, boolean implicitCasting) throws ContextException
  {
    boolean isVertical = dataTable.getFormat().getMaxRecords() == 1 && dataTable.getRecordCount() == 1;
    DataRecord dataRecord = isVertical ? dataTable.rec() : dataTable.addRecord();
    
    for (Object keyObject : jsonObject.keySet())
    {
      String key = (String) keyObject;
      
      if (dataRecord.getFormat(key) == null)
      {
        throw new ContextException(MessageFormat.format(Cres.get().getString("dtFieldNotFound"), key));
      }
      
      Object value = jsonObject.get(key);
      
      if (value instanceof JSONArray)
      {
        DataTable table = dataRecord.getDataTable(key);
        
        if (table == null && dataRecord.getFormat().getField(key).isInlineData())
        {
          table = new SimpleDataTable(dataRecord.getFormat());
        }
        if (table == null || table.getFormat().getFieldCount() == 0)
        {
          table = tableFromJson(value, innerDataTable, implicitCasting);
        }
        else
        {
          JSONArray array = (JSONArray) value;
          
          for (Object object : array)
            fillDataTableWithObject((JSONObject) object, table, innerDataTable, implicitCasting);
        }
        
        dataRecord.setValue(key, table);
      }
      else
      {
        dataRecord.setValue(key, convertValue(dataRecord.getFormat(key), value));
      }
    }
  }
  
  private Object parse(String payload) throws ContextException
  {
    JSONParser parser = new JSONParser();
    try
    {
      return parser.parse(payload);
    }
    catch (ParseException e)
    {
      throw new ContextException(e.getMessage(), e);
    }
  }
}
