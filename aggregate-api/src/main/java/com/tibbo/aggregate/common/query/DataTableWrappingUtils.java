package com.tibbo.aggregate.common.query;

import java.sql.*;
import java.util.*;

import com.tibbo.aggregate.common.*;
import com.tibbo.aggregate.common.data.*;
import com.tibbo.aggregate.common.datatable.*;
import com.tibbo.aggregate.common.datatable.encoding.*;
import com.tibbo.aggregate.common.util.*;

public class DataTableWrappingUtils
{
  
  public static List<FieldDescriptor> extractResultSetFields(ResultSetMetaData rsmd, boolean uppercase) throws SQLException
  {
    List<FieldDescriptor> result = new LinkedList();
    
    int columnCount = rsmd.getColumnCount();
    for (int i = 1; i <= columnCount; i++)
    {
      String colName = rsmd.getColumnLabel(i);
      if (colName == null || colName.length() == 0)
      {
        // Some databases (including hsql) create empty column names for function calls etc
        colName = generateUniqueName(result, i, colName);
      }
      int sqlType = rsmd.getColumnType(i);
      TypeMapper.Type type = TypeMapper.getTypeForSqlType(sqlType);
      
      String columnName = uppercase ? colName.toUpperCase(Locale.ENGLISH) : colName.toLowerCase(Locale.ENGLISH);
      
      FieldDescriptor fd = new FieldDescriptor(columnName, DataTableWrappingUtils.escapeColumnName(colName), rsmd.getColumnLabel(i), type);
      
      result.add(fd);
    }
    
    return result;
  }
  
  /**
   * SQL column names are case insensitive and their parts are separated with the separator sign So we'll escape column name by removing separator and capitalizing the character that follows the
   * dollar sign. All other chars are to be small letters.
   */
  public static String escapeColumnName(String columnName)
  {
    if (columnName == null)
    {
      return null;
    }
    
    StringBuffer r = new StringBuffer(columnName);
    // Кэшируем длину строки для оптимизации (хотя length() O(1), но улучшает читаемость)
    int rLength = r.length();
    for (int i = 0; i < rLength; i++)
    {
      char c = r.charAt(i);
      if (c == QueryConstants.SEPARATOR.charAt(0))
      {
        r.deleteCharAt(i);
        r.setCharAt(i, Character.toUpperCase(r.charAt(i)));
      }
      else
      {
        if (!Character.isLetter(c) && !Character.isDigit(c))
        {
          c = '_';
        }
        r.setCharAt(i, Character.toLowerCase(c));
      }
    }
    
    return r.toString();
  }
  
  public static String generateUniqueName(List<FieldDescriptor> result, int i, String colName) throws IllegalStateException
  {
    final String COLUMN_PREFIX = "COLUMN_";
    String resultName = colName;
    resultName = COLUMN_PREFIX + i;
    
    for (FieldDescriptor fd : result)
    {
      if (Util.equals(fd.getColumnName(), resultName))
      {
        throw new IllegalStateException("Can't generate unique column name");
      }
    }
    
    return resultName;
  }
  
  public static Object getFieldValue(ResultSet rs, int columnIndex, FieldFormat ff) throws SQLException
  {
    char ffType = ff.getType();
    Object value = null;
    
    switch (ffType)
    {
      case FieldFormat.INTEGER_FIELD:
        Number n = Util.convertToNumber(rs.getObject(columnIndex), false, true);
        value = n != null ? n.intValue() : null;
        break;
      case FieldFormat.STRING_FIELD:
        Object obj = rs.getObject(columnIndex);
        if (obj == null)
        {
          value = null;
        }
        else if (obj instanceof Blob)
        {
          Blob blob = (Blob) obj;
          value = new String(blob.getBytes(1, (int) blob.length()));
        }
        else if (obj instanceof Clob)
        {
          Clob clob = (Clob) obj;
          value = clob.getSubString(1, (int) clob.length());
        }
        else if (obj instanceof byte[])
        {
          value = new String((byte[]) obj);
        }
        else
        {
          value = obj.toString();
        }
        break;
      case FieldFormat.BOOLEAN_FIELD:
        value = Util.convertToBoolean(rs.getObject(columnIndex), false, true);
        break;
      case FieldFormat.LONG_FIELD:
        n = Util.convertToNumber(rs.getObject(columnIndex), false, true);
        value = n != null ? n.longValue() : null;
        break;
      case FieldFormat.FLOAT_FIELD:
        n = Util.convertToNumber(rs.getObject(columnIndex), false, true);
        value = n != null ? n.floatValue() : null;
        break;
      case FieldFormat.DOUBLE_FIELD:
        n = Util.convertToNumber(rs.getObject(columnIndex), false, true);
        value = n != null ? n.doubleValue() : null;
        break;
      case FieldFormat.DATE_FIELD:
        value = rs.getTimestamp(columnIndex);
        break;
      case FieldFormat.DATATABLE_FIELD:
        String s = rs.getString(columnIndex);
        try
        {
          value = s != null ? new SimpleDataTable(s) : null;
        }
        catch (DataTableException ex)
        {
          throw new IllegalStateException(ex);
        }
        break;
      case FieldFormat.DATA_FIELD:
        Object dataObj = rs.getObject(columnIndex);
        if (dataObj != null)
        {
          byte[] bytes = null;
          if (dataObj instanceof byte[])
          {
            bytes = (byte[]) dataObj;
            value = new String(bytes);
          }
          else if (dataObj instanceof Blob)
          {
            Blob blob = (Blob) dataObj;
            bytes = blob.getBytes(1, (int) blob.length());
            value = new String(bytes);
          }
          else
          {
            Log.DATATABLE.error("The data object is neither an array of bytes nor a Blob; the data field of the Data object will be set to null");
          }
          
          try
          {
            value = ff.valueFromEncodedString((String) value, new ClassicEncodingSettings(false), true);
          }
          catch (Exception ex)
          {
            value = new Data(bytes);
          }
        }
        break;
      default:
        value = rs.getString(columnIndex);
        if (value != null)
        {
          value = ff.valueFromString((String) value);
        }
        break;
    }
    if (rs.wasNull())
    {
      // ResultSet getters return primitive types that can't be nulls...
      // Fix it
      value = null;
    }
    return value;
  }
}
