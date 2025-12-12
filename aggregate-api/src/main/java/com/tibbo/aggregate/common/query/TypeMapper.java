package com.tibbo.aggregate.common.query;

import java.util.*;

import org.apache.log4j.*;

import com.tibbo.aggregate.common.datatable.*;

/**
 * <p>
 * Title: AggreGate SQL support
 * </p>
 * 
 * <p>
 * Description: SQL interface to aggregate's context tree
 * </p>
 * 
 * <p>
 * SQL to AggreGate SQL and FieldFormat types mapper
 * </p>
 */
public abstract class TypeMapper
{
  private static Logger logger = Logger.getLogger(TypeMapper.class);
  
  public enum Type
  {
    INTEGER, VARCHAR, BOOLEAN, BIGINT /* deprecated */, REAL, TIMESTAMP, CLOB, BLOB, LONG, DOUBLE
  }
  
  private static LinkedHashMap<Character, Type> fieldFormatToSQL = new LinkedHashMap<Character, Type>();
  private static LinkedHashMap<Type, Character> sqlToFieldFormat = new LinkedHashMap<Type, Character>();
  private static LinkedHashMap<Type, String> fieldFormatToSQLString = new LinkedHashMap<Type, String>();
  private static LinkedHashMap<Integer, Type> sqlTypeToType = new LinkedHashMap<Integer, Type>();
  
  static
  {
    fieldFormatToSQL.put(FieldFormat.INTEGER_FIELD, Type.INTEGER);
    fieldFormatToSQL.put(FieldFormat.STRING_FIELD, Type.VARCHAR);
    fieldFormatToSQL.put(FieldFormat.BOOLEAN_FIELD, Type.BOOLEAN);
    fieldFormatToSQL.put(FieldFormat.LONG_FIELD, Type.LONG);
    fieldFormatToSQL.put(FieldFormat.FLOAT_FIELD, Type.REAL);
    fieldFormatToSQL.put(FieldFormat.DOUBLE_FIELD, Type.DOUBLE);
    fieldFormatToSQL.put(FieldFormat.DATE_FIELD, Type.TIMESTAMP);
    fieldFormatToSQL.put(FieldFormat.DATATABLE_FIELD, Type.CLOB);
    
    sqlToFieldFormat.put(Type.INTEGER, FieldFormat.INTEGER_FIELD);
    sqlToFieldFormat.put(Type.VARCHAR, FieldFormat.STRING_FIELD);
    sqlToFieldFormat.put(Type.BOOLEAN, FieldFormat.BOOLEAN_FIELD);
    sqlToFieldFormat.put(Type.LONG, FieldFormat.LONG_FIELD);
    sqlToFieldFormat.put(Type.REAL, FieldFormat.FLOAT_FIELD);
    sqlToFieldFormat.put(Type.DOUBLE, FieldFormat.DOUBLE_FIELD);
    sqlToFieldFormat.put(Type.TIMESTAMP, FieldFormat.DATE_FIELD);
    sqlToFieldFormat.put(Type.CLOB, FieldFormat.DATATABLE_FIELD);
    sqlToFieldFormat.put(Type.BIGINT, FieldFormat.LONG_FIELD);
    sqlToFieldFormat.put(Type.BLOB, FieldFormat.DATA_FIELD);
    
    fieldFormatToSQLString.put(Type.INTEGER, "INTEGER");
    fieldFormatToSQLString.put(Type.LONG, "NUMERIC(20)");
    fieldFormatToSQLString.put(Type.VARCHAR, "VARCHAR(16777216)");
    fieldFormatToSQLString.put(Type.BOOLEAN, "BOOLEAN");
    fieldFormatToSQLString.put(Type.BIGINT, "NUMERIC(20)");
    fieldFormatToSQLString.put(Type.REAL, "REAL");
    fieldFormatToSQLString.put(Type.DOUBLE, "DOUBLE");
    fieldFormatToSQLString.put(Type.TIMESTAMP, "TIMESTAMP");
    fieldFormatToSQLString.put(Type.CLOB, "LONGVARCHAR");
    
    sqlTypeToType.put(java.sql.Types.TINYINT, Type.INTEGER);
    sqlTypeToType.put(java.sql.Types.SMALLINT, Type.INTEGER);
    sqlTypeToType.put(java.sql.Types.INTEGER, Type.INTEGER);
    sqlTypeToType.put(java.sql.Types.BIGINT, Type.LONG);
    sqlTypeToType.put(java.sql.Types.VARCHAR, Type.VARCHAR);
    sqlTypeToType.put(java.sql.Types.BOOLEAN, Type.BOOLEAN);
    sqlTypeToType.put(java.sql.Types.BIT, Type.BOOLEAN);
    sqlTypeToType.put(java.sql.Types.NUMERIC, Type.DOUBLE);
    sqlTypeToType.put(java.sql.Types.REAL, Type.REAL);
    sqlTypeToType.put(java.sql.Types.DOUBLE, Type.DOUBLE);
    sqlTypeToType.put(java.sql.Types.DECIMAL, Type.REAL);
    sqlTypeToType.put(java.sql.Types.TIMESTAMP, Type.TIMESTAMP);
    sqlTypeToType.put(java.sql.Types.LONGVARCHAR, Type.CLOB);
    sqlTypeToType.put(java.sql.Types.LONGNVARCHAR, Type.CLOB);
    sqlTypeToType.put(java.sql.Types.BLOB, Type.BLOB);
    sqlTypeToType.put(java.sql.Types.LONGVARBINARY, Type.BLOB);
  }
  
  public static char getFieldFormatByType(Type t)
  {
    Character type = sqlToFieldFormat.get(t);
    
    if (type != null)
    {
      return type;
    }
    
    throw new IllegalArgumentException("Unsupported type: " + t);
  }
  
  public static Type getSqlTypeForFieldFormat(char fieldFormat)
  {
    if (!fieldFormatToSQL.containsKey(fieldFormat))
    {
      return null;
    }
    
    return fieldFormatToSQL.get(fieldFormat);
  }
  
  public static String getSqlStringForFieldFormat(char fieldFormat)
  {
    if (!fieldFormatToSQL.containsKey(fieldFormat))
    {
      throw new IllegalArgumentException("Unsupported fieldFormat: " + fieldFormat);
    }
    
    Type type = fieldFormatToSQL.get(fieldFormat);
    
    return fieldFormatToSQLString.get(type);
  }
  
  public static Type getTypeForSqlType(int sqlType)
  {
    if (!sqlTypeToType.containsKey(sqlType))
    {
      logger.debug("Unsupported SQL type: " + sqlType);
      return Type.VARCHAR;
      // throw new IllegalArgumentException("Unsupported SQL type: " + sqlType);
    }
    
    return sqlTypeToType.get(sqlType);
  }
  
  public static String getSqlStringForType(Type t)
  {
    if (!fieldFormatToSQLString.containsKey(t))
    {
      return null;
    }
    
    return fieldFormatToSQLString.get(t);
  }
}
