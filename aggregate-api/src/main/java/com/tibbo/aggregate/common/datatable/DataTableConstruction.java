package com.tibbo.aggregate.common.datatable;

import java.util.*;

import com.tibbo.aggregate.common.expression.*;
import com.tibbo.aggregate.common.util.*;

public class DataTableConstruction
{
  
  public static DataTable constructTable(Object object) throws SyntaxErrorException, EvaluationException, DataTableException
  {
    return DataTableConstruction.constructTable(Collections.singletonList(object), null, null, null);
  }
  
  public static DataTable constructTable(List parameters) throws SyntaxErrorException, EvaluationException, DataTableException
  {
    return DataTableConstruction.constructTable(parameters, null, null, null);
  }
  
  public static DataTable constructTable(List parameters, TableFormat format) throws SyntaxErrorException, EvaluationException, DataTableException
  {
    return DataTableConstruction.constructTable(parameters, format, null, null);
  }
  
  public static DataTable constructTable(List parameters, TableFormat format, Evaluator evaluator, EvaluationEnvironment environment) throws SyntaxErrorException, EvaluationException,
      DataTableException
  {
    try
    {
      List params = DataTableConstruction.resolveParameters(parameters, evaluator, environment);
      
      DataTable res;
      
      if (format == null)
      {
        if (params.size() == 1 && params.get(0) instanceof DataTable)
        {
          res = (DataTable) params.get(0);
        }
        else
        {
          res = DataTableUtils.wrapToTable(params);
        }
      }
      else
      {
        res = DataTableConstruction.fillDataTable(format, params);
      }
      
      return res;
    }
    catch (SyntaxErrorException ex)
    {
      throw ex;
    }
    catch (EvaluationException ex)
    {
      throw ex;
    }
    catch (Exception ex)
    {
      throw new EvaluationException("Error constructing data table" + (format != null ? " of format " + format.encode(true) : "") + " from parameters '" + StringUtils.print(parameters) + "': "
          + ex.getMessage(), ex);
    }
  }
  
  private static List resolveParameters(List parameters, Evaluator evaluator, EvaluationEnvironment environment) throws SyntaxErrorException, EvaluationException
  {
    List params = new LinkedList();
    for (Object param : parameters)
    {
      if (param instanceof Expression)
      {
        if (evaluator == null)
        {
          throw new IllegalStateException("Evaluator not defined");
        }
        
        params.add(evaluator.evaluate((Expression) param, environment));
      }
      else
      {
        params.add(param);
      }
    }
    return params;
  }
  
  private static DataTable fillDataTable(TableFormat format, List params)
  {
    DataTable table = new SimpleDataTable(format);
    
    if (params.size() != 0)
    {
      long maxCells = (long) format.getFieldCount() * (long) format.getMaxRecords();
      if (params.size() > maxCells)
      {
        throw new IllegalStateException("Maximum number of cells in the table is " + maxCells + ", but " + params.size() + " parameters were received.");
      }
      
      DataRecord rec = table.addRecord();
      
      int fieldNum = 0;
      
      for (int i = 0; i < params.size(); i++)
      {
        if (fieldNum >= format.getFieldCount())
        {
          fieldNum = 0;
          rec = table.addRecord();
        }
        
        FieldFormat ff = format.getField(fieldNum);
        
        Object param = params.get(i);
        
        if (param == null || ff.getFieldClass().isAssignableFrom(param.getClass()) || ff.getFieldWrappedClass().isAssignableFrom(param.getClass()))
        {
          rec.addValue(param);
        }
        else
        {
          rec.addValue(ff.valueFromString(param.toString()));
        }
        
        fieldNum++;
      }
    }
    
    while (table.getRecordCount() < format.getMinRecords())
    {
      table.addRecord();
    }
    
    return table;
  }
  
}
