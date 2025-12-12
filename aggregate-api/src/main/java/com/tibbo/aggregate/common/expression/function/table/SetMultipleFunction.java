package com.tibbo.aggregate.common.expression.function.table;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.datatable.DataRecord;
import com.tibbo.aggregate.common.datatable.DataTable;
import com.tibbo.aggregate.common.datatable.DataTableReplication;
import com.tibbo.aggregate.common.datatable.SimpleDataTable;
import com.tibbo.aggregate.common.expression.EvaluationEnvironment;
import com.tibbo.aggregate.common.expression.EvaluationException;
import com.tibbo.aggregate.common.expression.Evaluator;
import com.tibbo.aggregate.common.expression.Expression;
import com.tibbo.aggregate.common.expression.Function;
import com.tibbo.aggregate.common.expression.Reference;
import com.tibbo.aggregate.common.expression.ReferenceResolver;
import com.tibbo.aggregate.common.expression.function.AbstractFunction;
import com.tibbo.aggregate.common.util.SyntaxErrorException;

public class SetMultipleFunction extends AbstractFunction
{
  public SetMultipleFunction()
  {
    super("setMultiple", Function.GROUP_DATA_TABLE_PROCESSING, "DataTable table, String field, Object value [, String condition]", "Object", Cres.get().getString("fDescSetMultiple"));
  }
  
  @Override
  public Object execute(Evaluator evaluator, EvaluationEnvironment environment, Object... parameters) throws EvaluationException
  {
    checkParameters(3, true, parameters);
    checkParameterType(0, parameters[0], DataTable.class);
    
    DataTable source = (DataTable) parameters[0];
    
    String field = parameters[1].toString();
    Object newValue = parameters[2];
    Expression expression = null;
    if (parameters.length > 3)
      expression = new Expression(parameters[3].toString());
    DataTable result = new SimpleDataTable(source.getFormat().clone().setMinRecords(0));
    Integer oldRow = evaluator.getDefaultResolver().getDefaultRow();
    
    ReferenceResolver resolver = evaluator.getDefaultResolver();
    Evaluator localEvaluator = new Evaluator(resolver.getContextManager(), resolver.getDefaultContext(), resolver.getDefaultTable(), resolver.getCallerController());
    localEvaluator.getDefaultResolver().setDefaultTable(source);
    localEvaluator.setResolver(Reference.SCHEMA_ENVIRONMENT, evaluator.getEnvironmentResolver());
    
    try
    {
      for (int i = 0; i < source.getRecordCount(); i++)
      {
        localEvaluator.getDefaultResolver().setDefaultRow(i);
        
        try
        {
          DataRecord rec = result.addRecord();
          DataTableReplication.copyRecord(source.getRecord(i), rec, true, true);
          
          if (expression == null || localEvaluator.evaluateToBoolean(expression))
            rec.setValueSmart(field, newValue);
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
}
