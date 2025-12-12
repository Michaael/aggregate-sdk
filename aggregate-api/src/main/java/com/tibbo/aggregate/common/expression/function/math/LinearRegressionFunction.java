package com.tibbo.aggregate.common.expression.function.math;

import java.text.MessageFormat;

import org.apache.commons.math3.stat.regression.SimpleRegression;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.datatable.DataRecord;
import com.tibbo.aggregate.common.datatable.DataTable;
import com.tibbo.aggregate.common.datatable.FieldFormat;
import com.tibbo.aggregate.common.datatable.SimpleDataTable;
import com.tibbo.aggregate.common.datatable.TableFormat;
import com.tibbo.aggregate.common.expression.EvaluationEnvironment;
import com.tibbo.aggregate.common.expression.EvaluationException;
import com.tibbo.aggregate.common.expression.Evaluator;
import com.tibbo.aggregate.common.expression.Function;
import com.tibbo.aggregate.common.expression.function.AbstractFunction;

public class LinearRegressionFunction extends AbstractFunction
{
  
  final static String FOF_INTERCEPT = "intercept";
  final static String FOF_SLOPE = "slope";
  final static String FOF_MEAN_SQUARE_ERROR = "meanSquareError";
  final static String FOF_SUM_SQUARE_ERRORS = "sumSquaredErrors";
  
  public LinearRegressionFunction()
  {
    super("linearRegression", Function.GROUP_MATH, "DataTable table, String xSeriesFieldName, String ySeriesFieldName", "DataTable", Cres.get().getString("fDescLinearRegression"));
  }
  
  @Override
  public Object execute(Evaluator evaluator, EvaluationEnvironment environment, Object... parameters) throws EvaluationException
  {
    checkParameters(3, false, parameters);
    checkParameterType(0, parameters[0], DataTable.class);
    DataTable table = (DataTable) parameters[0];
    String fieldXName = getColumnName(parameters[1], table);
    String fieldYName = getColumnName(parameters[2], table);
    int recordCount = table.getRecordCount();
    double[][] seriesData = new double[recordCount][2];
    int count = 0;
    for (DataRecord rec : table)
    {
      if ((rec.getValue(fieldXName) instanceof Number) && !(rec.getValue(fieldXName) instanceof Long))
      {
        seriesData[count][0] = ((Number) rec.getValue(fieldXName)).doubleValue();
      }
      else
      {
        throw new EvaluationException(
            MessageFormat.format(Cres.get().getString("exprInvalidParameterType"), fieldXName, Double.class.getSimpleName(), rec.getValue(fieldXName).getClass().getSimpleName()));
      }
      if ((rec.getValue(fieldYName) instanceof Number) && !(rec.getValue(fieldYName) instanceof Long))
      {
        seriesData[count][1] = ((Number) rec.getValue(fieldYName)).doubleValue();
      }
      else
      {
        throw new EvaluationException(
            MessageFormat.format(Cres.get().getString("exprInvalidParameterType"), fieldYName, Double.class.getSimpleName(), rec.getValue(fieldYName).getClass().getSimpleName()));
      }
      count++;
    }
    
    TableFormat outputTableFormat = new TableFormat();
    outputTableFormat.addField(FieldFormat.create(FOF_SLOPE, FieldFormat.DOUBLE_FIELD));
    outputTableFormat.addField(FieldFormat.create(FOF_INTERCEPT, FieldFormat.DOUBLE_FIELD));
    outputTableFormat.addField(FieldFormat.create(FOF_MEAN_SQUARE_ERROR, FieldFormat.DOUBLE_FIELD));
    outputTableFormat.addField(FieldFormat.create(FOF_SUM_SQUARE_ERRORS, FieldFormat.DOUBLE_FIELD));
    DataTable outputTable = new SimpleDataTable(outputTableFormat);

    SimpleRegression regression = new SimpleRegression();
    regression.addData(seriesData);
    regression.regress();

    double intercept = regression.getIntercept();
    double slope = regression.getSlope();
    double meanSquareError = regression.getMeanSquareError();
    double sumSquaredErrors = regression.getSumSquaredErrors();

    outputTable.addRecord(slope, intercept, meanSquareError, sumSquaredErrors);
    return outputTable;
  }
  
  private String getColumnName(Object param, DataTable table) throws EvaluationException
  {
    if (param instanceof Number)
    {
      Number fieldIndex = (Number) param;
      
      if (table.getFieldCount() < fieldIndex.intValue())
      {
        throw new EvaluationException(Cres.get().getString("exprTableHasNoFieldIndex") + fieldIndex.intValue());
      }
      
      return table.getFormat(fieldIndex.intValue()).getName();
    }
    else
    {
      return param.toString();
    }
  }
}
