package com.tibbo.aggregate.common.util;

import java.io.*;
import java.util.*;
import java.util.Map.*;

import com.csvreader.*;
import com.tibbo.aggregate.common.*;
import com.tibbo.aggregate.common.datatable.*;
import org.apache.commons.io.input.BOMInputStream;

public class CsvImportExportUtils
{
  private static final String NULL = "NULL";
  
  public static final String CSV_IMPORT_FIELD_DELIMITER = "delimiter";
  public static final String CSV_IMPORT_FIELD_USE_QUALIFIER = "useQualifier";
  public static final String CSV_IMPORT_FIELD_QUALIFIER = "qualifier";
  public static final String CSV_IMPORT_FIELD_COMMENT = "comment";
  public static final String CSV_IMPORT_FIELD_ESCAPE_MODE = "escapeMode";
  public static final String CSV_IMPORT_FIELD_HEADER = "header";
  
  public static final String CSV_EXPORT_FIELD_DELIMITER = "delimiter";
  public static final String CSV_EXPORT_FIELD_USE_QUALIFIER = "useQualifier";
  public static final String CSV_EXPORT_FIELD_QUALIFIER = "qualifier";
  public static final String CSV_EXPORT_FIELD_ESCAPE_MODE = "escapeMode";
  public static final String CSV_EXPORT_FIELD_HEADER = "header";
  public static final String CSV_EXPORT_FIELD_USE_DESCRIPTIONS = "useDescriptions";
  
  public final static int QUALIFIER_NONE = 0;
  public final static int QUALIFIER_NORMAL = 1;
  public final static int QUALIFIER_FORCE = 2;
  
  public final static int HEADER_NONE = 0;
  public final static int HEADER_NAMES = 1;
  public final static int HEADER_DESCRIPTIONS = 2;
  public final static int HEADER_SKIP = 3;

  public static TableFormat CSV_EXPORT_FORMAT = new TableFormat(1, 1);
  
  static
  {
    CSV_EXPORT_FORMAT.addField("<" + CSV_EXPORT_FIELD_DELIMITER + "><S><A=;><D=" + Cres.get().getString("ieFieldDelimiter") + "><V=<L=1 1>>");
    
    CSV_EXPORT_FORMAT.addField("<"
        + CSV_EXPORT_FIELD_USE_QUALIFIER
        + "><I><A=1><D="
        + Cres.get().getString("ieUseTextQualifier")
        + "><S=<"
        + Cres.get().getString("ieDoNotUse")
        + "="
        + QUALIFIER_NONE
        + "><"
        + Cres.get().getString("ieUseWhenNecessary")
        + "="
        + QUALIFIER_NORMAL
        + "><"
        + Cres.get().getString("ieUseAlways")
        + "="
        + QUALIFIER_FORCE
        + ">>");
    
    CSV_EXPORT_FORMAT.addField("<" + CSV_EXPORT_FIELD_QUALIFIER + "><S><A=\"><D=" + Cres.get().getString("ieTextQualifier") + "><V=<L=1 1>>");
    
    CSV_EXPORT_FORMAT.addField("<"
        + CSV_EXPORT_FIELD_ESCAPE_MODE
        + "><I><A="
        + CsvWriter.ESCAPE_MODE_DOUBLED
        + "><D="
        + Cres.get().getString("ieEscapeMode")
        + "><S=<"
        + Cres.get().getString("ieEscBackslash")
        + "="
        + CsvWriter.ESCAPE_MODE_BACKSLASH
        + "><"
        + Cres.get().getString("ieEscDouble")
        + "="
        + CsvWriter.ESCAPE_MODE_DOUBLED
        + ">>");
    
    CSV_EXPORT_FORMAT.addField("<"
        + CSV_EXPORT_FIELD_HEADER
        + "><I><D="
        + Cres.get().getString("ieHeaderRecord")
        + "><S=<"
        + Cres.get().getString("none")
        + "="
        + HEADER_DESCRIPTIONS
        + "><"
        + Cres.get().getString("ieFieldNames")
        + "="
        + HEADER_NAMES
        + "><"
        + Cres.get().getString("ieFieldDescriptions")
        + "="
        + HEADER_DESCRIPTIONS
        + ">>");
    
    CSV_EXPORT_FORMAT.addField("<" + CSV_EXPORT_FIELD_USE_DESCRIPTIONS + "><B><A=0><D=" + Cres.get().getString("ieUseDescriptions") + ">");
    
    String ref = CSV_EXPORT_FIELD_QUALIFIER + "#" + DataTableBindingProvider.PROPERTY_ENABLED;
    String exp = "{" + CSV_EXPORT_FIELD_USE_QUALIFIER + "} == " + QUALIFIER_NORMAL + " || {" + CSV_EXPORT_FIELD_USE_QUALIFIER + "} == " + QUALIFIER_FORCE;
    CSV_EXPORT_FORMAT.addBinding(ref, exp);
  }
  
  public static TableFormat CSV_IMPORT_FORMAT = new TableFormat(1, 1);
  
  static
  {
    CSV_IMPORT_FORMAT.addField("<" + CSV_IMPORT_FIELD_DELIMITER + "><S><A=;><D=" + Cres.get().getString("ieFieldDelimiter") + "><V=<L=1 1>>");
    
    CSV_IMPORT_FORMAT.addField("<" + CSV_IMPORT_FIELD_USE_QUALIFIER + "><B><D=" + Cres.get().getString("ieUseTextQualifier") + ">");
    
    CSV_IMPORT_FORMAT.addField("<" + CSV_IMPORT_FIELD_QUALIFIER + "><S><A=\"><D=" + Cres.get().getString("ieTextQualifier") + "><V=<L=1 1>>");
    
    CSV_IMPORT_FORMAT.addField("<" + CSV_IMPORT_FIELD_COMMENT + "><S><A=#><D=" + Cres.get().getString("ieCommentChar") + "><V=<L=1 1>>");
    
    CSV_IMPORT_FORMAT.addField("<"
        + CSV_IMPORT_FIELD_ESCAPE_MODE
        + "><I><A="
        + CsvWriter.ESCAPE_MODE_DOUBLED
        + "><D="
        + Cres.get().getString("ieEscapeMode")
        + "><S=<"
        + Cres.get().getString("ieEscBackslash")
        + "="
        + CsvWriter.ESCAPE_MODE_BACKSLASH
        + "><"
        + Cres.get().getString("ieEscDouble")
        + "="
        + CsvWriter.ESCAPE_MODE_DOUBLED
        + ">>");
    
    CSV_IMPORT_FORMAT.addField("<"
        + CSV_IMPORT_FIELD_HEADER
        + "><I><D="
        + Cres.get().getString("ieHeaderRecord")
        + "><S=<"
        + Cres.get().getString("none")
        + "="
        + HEADER_NONE
        + "><"
        + Cres.get().getString("ieFieldNames")
        + "="
        + HEADER_NAMES
        + "><"
        + Cres.get().getString("ieFieldDescriptions")
        + "="
        + HEADER_DESCRIPTIONS
        + "><"
        + Cres.get().getString("ieSkip")
        + "="
        + HEADER_SKIP
        + ">>");
    
    String ref = CSV_IMPORT_FIELD_QUALIFIER + "#" + DataTableBindingProvider.PROPERTY_ENABLED;
    String exp = "{" + CSV_IMPORT_FIELD_USE_QUALIFIER + "}";
    CSV_IMPORT_FORMAT.addBinding(ref, exp);
  }
  
  private CsvImportExportUtils()
  {
  }
  
  public static void readCsvRecords(DataTable table, CsvReader reader, int header, boolean hasMoreRecords) throws AggreGateException, IOException
  {
    while (hasMoreRecords)
    {
      DataRecord record = table.addRecord();
      for (int i = 0; i < reader.getColumnCount(); i++)
      {
        FieldFormat ff = null;
        
        if (header == HEADER_NONE || header == HEADER_SKIP)
        {
          if (record.getFormat().getFieldCount() > i)
          {
            ff = record.getFormat().getField(i);
          }
        }
        
        String headerString = reader.getHeader(i);
        
        if (header == HEADER_NAMES)
        {
          ff = record.getFormat().getField(headerString);
        }
        else if (header == HEADER_DESCRIPTIONS)
        {
          if (headerString != null)
          {
            for (int j = 0; j < table.getFieldCount(); j++)
            {
              FieldFormat cur = record.getFormat().getField(j);
              if (headerString.equals(cur.getDescription()))
              {
                ff = cur;
                break;
              }
            }
          }
        }
        else
        {
          ff = record.getFormat().getField(i);
        }
        
        if (ff == null)
        {
          Log.DATATABLEEDITOR.warn("Data table field not found for column " + i + " (" + reader.getHeader(i) + ")");
          continue;
        }
        
        String stringValue = reader.get(i);
        
        try
        {
          Object value;
          
          if (stringValue != null && NULL.equals(stringValue.toUpperCase()))
          {
            value = null;
          }
          else
          {
            value = ff.valueFromString(stringValue);
          }
          
          record.setValue(ff.getName(), value);
        }
        catch (Exception ex)
        {
          if (stringValue == null || !ff.hasSelectionValues())
          {
            throw new AggreGateException(ex.getMessage(), ex);
          }
          boolean found = false;
          Map<Object, String> sv = ff.getSelectionValues();
          for (Entry<Object, String> entry : sv.entrySet())
          {
            if (Util.equals(stringValue, entry.getValue()))
            {
              record.setValue(ff.getName(), entry.getKey());
              found = true;
              break;
            }
          }
          if (!found)
          {
            throw new AggreGateException(ex.getMessage(), ex);
          }
        }
        
      }
      
      hasMoreRecords = reader.readRecord();
    }
  }


  public static CsvReader createCsvReader(byte[] data, char delimiter, boolean useQualifier, char qualifier, int escapeMode, boolean useComments, char comment)
  {
    CsvReader reader = new CsvReader(new BOMInputStream(new ByteArrayInputStream(data)), delimiter, StringUtils.UTF8_CHARSET);
    reader.setSkipEmptyRecords(true);
    reader.setUseTextQualifier(useQualifier);
    reader.setTextQualifier(qualifier);
    reader.setUseComments(useComments);
    reader.setComment(comment);
    reader.setEscapeMode(escapeMode);
    return reader;
  }
  
  public static CsvReader createCsvReader(byte[] data, DataTable options)
  {
    return createCsvReader(data,
                           options.rec().getString(CsvImportExportUtils.CSV_IMPORT_FIELD_DELIMITER).charAt(0),
                           options.rec().getBoolean(CsvImportExportUtils.CSV_IMPORT_FIELD_USE_QUALIFIER),
                           options.rec().getString(CsvImportExportUtils.CSV_IMPORT_FIELD_QUALIFIER).charAt(0),
                           options.rec().getInt(CsvImportExportUtils.CSV_IMPORT_FIELD_ESCAPE_MODE),
                           false, // default value of the CSVReader 'UseComments' option
                           options.rec().getString(CsvImportExportUtils.CSV_IMPORT_FIELD_COMMENT).charAt(0));
  }
  
  /**
   * Note: in case if <code>original</code> is null and <code>header</code> is <code>HEADER_NAMES</code>, any null or empty headers in <code>reader</code> will be replaced by the string representation
   * of the corresponding column number.
   */
  public static TableFormat readFormat(DataTable original, CsvReader reader, int header) throws IOException
  {
    TableFormat format = original != null ? original.getFormat().clone() : null;
    
    if (format == null)
    {
      format = new TableFormat();
      
      int numberOfColumns = reader.getHeaders() != null ? reader.getHeaderCount() : reader.getColumnCount();
      for (int i = 0; i < numberOfColumns; i++)
      {
        if (header == CsvImportExportUtils.HEADER_NAMES)
        {
          fillEmptyHeaders(reader);
          format.addField(FieldFormat.create(reader.getHeader(i), FieldFormat.STRING_FIELD));
        }
        else if (header == CsvImportExportUtils.HEADER_DESCRIPTIONS)
        {
          format.addField(FieldFormat.create(String.valueOf(i), FieldFormat.STRING_FIELD, reader.getHeader(i)));
        }
        else
        {
          format.addField(FieldFormat.create(String.valueOf(i), FieldFormat.STRING_FIELD));
        }
      }
    }
    return format;
  }
  
  /**
   * Replaces all null or empty headers in <code>reader</code> by the string representation of the corresponding column number.
   * 
   * @param reader
   * @return true if and only if <code>reader</code> has been modified
   * @throws IOException
   */
  public static boolean fillEmptyHeaders(CsvReader reader) throws IOException
  {
    if (reader.getHeaders() == null)
    {
      return false;
    }
    
    boolean isModified = false;
    
    for (int i = 0; i < reader.getHeaders().length; i++)
    {
      if (reader.getHeader(i) == null || reader.getHeader(i).isEmpty())
      {
        String[] headers = reader.getHeaders();
        headers[i] = String.valueOf(i);
        reader.setHeaders(headers);
        
        isModified = true;
      }
    }
    
    return isModified;
  }
}
