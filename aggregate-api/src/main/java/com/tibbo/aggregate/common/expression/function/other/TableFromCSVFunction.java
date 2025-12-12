package com.tibbo.aggregate.common.expression.function.other;

import java.io.IOException;
import java.text.MessageFormat;

import com.csvreader.CsvReader;
import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.datatable.DataTable;
import com.tibbo.aggregate.common.datatable.SimpleDataTable;
import com.tibbo.aggregate.common.datatable.TableFormat;
import com.tibbo.aggregate.common.expression.EvaluationEnvironment;
import com.tibbo.aggregate.common.expression.EvaluationException;
import com.tibbo.aggregate.common.expression.Evaluator;
import com.tibbo.aggregate.common.expression.ExpressionUtils;
import com.tibbo.aggregate.common.expression.Function;
import com.tibbo.aggregate.common.expression.function.AbstractFunction;
import com.tibbo.aggregate.common.util.CsvImportExportUtils;
import com.tibbo.aggregate.common.util.StringUtils;

public class TableFromCSVFunction extends AbstractFunction
{
  public TableFromCSVFunction()
  {
    super("tableFromCSV", Function.GROUP_DATA_TABLE_PROCESSING, "String csv, String header, String delimiter [, String format [, String qualifier [, Integer escapeMode [, String comment]]]]", "DataTable", Cres.get().getString("fDescTableFromCSV"));
  }
  
  @Override
  public Object execute(Evaluator evaluator, EvaluationEnvironment environment, Object... parameters) throws EvaluationException
  {
    checkParameters(3, false, parameters);
    
    try
    {
      final String text = parameters[0].toString();
      final byte[] data = text.getBytes(StringUtils.UTF8_CHARSET);
      
      final String headerType = parameters[1].toString();
      int header = getHeaderNumber(headerType);
      
      final String param2 = parameters[2].toString();
      final char delimiter = getChar(param2, "delimiter");
      
      final boolean useQualifier;
      final char qualifier;
      if (parameters.length >= 5 && parameters[4] != null)
      {
        useQualifier = true;
        qualifier = getChar(parameters[4].toString(), "qualifier");
      }
      else
      {
        useQualifier = false;
        qualifier = '"';
      }
      
      final int escapeMode;
      if (parameters.length >= 6 && parameters[5] != null)
      {
        Number escapeModeNumber = (Number) parameters[5];
        escapeMode = escapeModeNumber.intValue();
      }
      else
      {
        escapeMode = CsvReader.ESCAPE_MODE_BACKSLASH;
      }
      
      final boolean useComments;
      final char comment;
      if (parameters.length >= 7 && parameters[6] != null)
      {
        useComments = true;
        comment = getChar(parameters[6].toString(), "comment");
      }
      else
      {
        useComments = false;
        comment = '#';
      }
      
      final CsvReader reader = CsvImportExportUtils.createCsvReader(data, delimiter, useQualifier, qualifier, escapeMode, useComments, comment);
      
      if (header == CsvImportExportUtils.HEADER_SKIP)
      {
        reader.skipRecord();
      }
      else if (header == CsvImportExportUtils.HEADER_NAMES || header == CsvImportExportUtils.HEADER_DESCRIPTIONS)
      {
        reader.readHeaders();
      }
      
      final TableFormat format;
      if (parameters.length >= 4)
      {
        checkHeader(header, true);
        
        if (parameters[3] == null)
        {
          throw new EvaluationException(MessageFormat.format(Cres.get().getString("exprParamCantBeNull"), 3));
        }
        String formatString = parameters[3].toString();
        
        format = ExpressionUtils.readFormat(formatString);
      }
      else
      {
        checkHeader(header, false);
        
        format = CsvImportExportUtils.readFormat(null, reader, header);
      }
      
      if (header != CsvImportExportUtils.HEADER_NONE)
      {
        checkNumberOfColumns(format, reader);
      }
      
      final DataTable loaded = new SimpleDataTable(format);
      
      final boolean hasMoreRecords = reader.readRecord();
      
      CsvImportExportUtils.readCsvRecords(loaded, reader, header, hasMoreRecords);
      
      reader.close();
      
      return loaded;
    }
    catch (Exception ex)
    {
      throw new EvaluationException(ex.getMessage(), ex);
    }
  }
  
  private char getChar(String str, String parameterDescription) throws EvaluationException
  {
    if (str.length() != 1)
    {
      throw new EvaluationException("The " + parameterDescription + " must be a one-character string. Received: \"" + str.replace("\\", "\\\\") + "\".");
    }
    return str.charAt(0);
  }
  
  private int getHeaderNumber(String headerType) throws UnsupportedOperationException
  {
    int header;
    switch (headerType)
    {
      case "none":
        header = CsvImportExportUtils.HEADER_NONE;
        break;
      
      case "names":
        header = CsvImportExportUtils.HEADER_NAMES;
        break;
      
      case "descriptions":
        header = CsvImportExportUtils.HEADER_DESCRIPTIONS;
        break;
      
      case "skip":
        header = CsvImportExportUtils.HEADER_SKIP;
        break;
      
      default:
        throw new UnsupportedOperationException("Header of type \"" + headerType + "\" is not supported.");
    }
    return header;
  }
  
  private void checkHeader(int header, boolean isFormatProvided) throws EvaluationException
  {
    if (isFormatProvided)
    {
      if (header == CsvImportExportUtils.HEADER_NAMES || header == CsvImportExportUtils.HEADER_DESCRIPTIONS)
      {
        throw new EvaluationException("If format is specified the header type must be either \"none\" or \"skip\" and cannot be \"names\" or \"descriptions\".");
      }
    }
    else
    {
      if (header == CsvImportExportUtils.HEADER_NONE)
      {
        throw new EvaluationException("If the header type is \"none\" the table format must be specified using the format parameter.");
      }
    }
  }
  
  private void checkNumberOfColumns(TableFormat format, CsvReader reader) throws EvaluationException, IOException
  {
    int actualNumberOfColumns = reader.getHeaders() != null ? reader.getHeaderCount() : reader.getColumnCount();
    int numberOfColumnsInFormat = format.getFieldCount();
    
    if (actualNumberOfColumns != numberOfColumnsInFormat)
    {
      throw new EvaluationException("The number of columns in the specified format does not match the number of columns in the csv file content.");
    }
  }
}
