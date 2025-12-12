package com.tibbo.aggregate.common.expression.function.table;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.datatable.DataRecord;
import com.tibbo.aggregate.common.datatable.DataTable;
import com.tibbo.aggregate.common.datatable.DataTableReplication;
import com.tibbo.aggregate.common.datatable.FilteringDataTable;
import com.tibbo.aggregate.common.datatable.SimpleDataTable;
import com.tibbo.aggregate.common.expression.EvaluationEnvironment;
import com.tibbo.aggregate.common.expression.EvaluationException;
import com.tibbo.aggregate.common.expression.Evaluator;
import com.tibbo.aggregate.common.expression.Expression;
import com.tibbo.aggregate.common.expression.Function;
import com.tibbo.aggregate.common.expression.ReferenceResolver;
import com.tibbo.aggregate.common.expression.function.AbstractFunction;
import com.tibbo.aggregate.common.util.SyntaxErrorException;

public class FilterFunction extends AbstractFunction
{
  public FilterFunction()
  {
    super("filter", Function.GROUP_DATA_TABLE_PROCESSING, "DataTable table, String filterExpression", "DataTable", Cres.get().getString("fDescFilter"));
  }
  
  @Override
  public Object execute(Evaluator evaluator, EvaluationEnvironment environment, Object... parameters) throws EvaluationException
  {
    checkParameters(2, false, parameters);
    
    checkParameterType(0, parameters[0], DataTable.class);
    
    DataTable source = (DataTable) parameters[0];
    
    Expression filter = new Expression(parameters[1].toString());
    
    return source.isSimple()
        ? executeSimpleDataTableCase(source, filter, evaluator, environment)
        : executeNonSimpleDataTableCase(source, filter, evaluator, environment);
  }
  
  private DataTable executeSimpleDataTableCase(DataTable source, Expression filter, Evaluator evaluator,
      EvaluationEnvironment environment) throws EvaluationException
  {
    DataTable result = new SimpleDataTable(source.getFormat().clone().setMinRecords(0));
    
    Integer oldRow = evaluator.getDefaultResolver().getDefaultRow();
    
    ReferenceResolver resolver = evaluator.getDefaultResolver();
    Evaluator localEvaluator = new Evaluator(resolver.getContextManager(), resolver.getDefaultContext(), resolver.getDefaultTable(), resolver.getCallerController());
    
    localEvaluator.getDefaultResolver().setDefaultTable(source);
    
    try
    {
      for (int i = 0; i < source.getRecordCount(); i++)
      {
        localEvaluator.getDefaultResolver().setDefaultRow(i);
        
        try
        {
          if (localEvaluator.evaluateToBoolean(filter, environment != null ? environment.clone() : null))
          {
            DataRecord rec = result.addRecord();
            DataTableReplication.copyRecord(source.getRecord(i), rec, true, true);
          }
        }
        catch (SyntaxErrorException ex)
        {
          throw new EvaluationException(ex);
        }
      }
    }
    finally
    {
      evaluator.getDefaultResolver().setDefaultRow(oldRow);
    }
    
    return result;
  }
  
  private DataTable executeNonSimpleDataTableCase(DataTable source, Expression filter, Evaluator evaluator,
      EvaluationEnvironment environment)
  {
    if (source instanceof FilteringDataTable && ((FilteringDataTable) source).getFilterExpression().equals(filter))
      return source;
    
    return new FilteringDataTable(source, filter, evaluator, environment);
  }
  
}
