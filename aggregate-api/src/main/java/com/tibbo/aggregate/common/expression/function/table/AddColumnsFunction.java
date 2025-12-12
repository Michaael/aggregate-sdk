package com.tibbo.aggregate.common.expression.function.table;

import static com.google.common.collect.Lists.newArrayList;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.datatable.DataRecord;
import com.tibbo.aggregate.common.datatable.DataTable;
import com.tibbo.aggregate.common.datatable.FieldFormat;
import com.tibbo.aggregate.common.datatable.TableFormat;
import com.tibbo.aggregate.common.expression.EvaluationEnvironment;
import com.tibbo.aggregate.common.expression.EvaluationException;
import com.tibbo.aggregate.common.expression.Evaluator;
import com.tibbo.aggregate.common.expression.Expression;
import com.tibbo.aggregate.common.expression.Function;
import com.tibbo.aggregate.common.expression.ReferenceResolver;
import com.tibbo.aggregate.common.expression.function.AbstractFunction;
import com.tibbo.aggregate.common.util.SyntaxErrorException;

public class AddColumnsFunction extends AbstractFunction
{
  public AddColumnsFunction()
  {
    this("addColumns", "DataTable table, String format1, String expression1, String format2, String expression2, ...", Cres.get().getString("fDescAddColumns"));
  }
  
  public AddColumnsFunction(String name, String parametersFootprint, String description)
  {
    super(name, Function.GROUP_DATA_TABLE_PROCESSING, parametersFootprint, "DataTable", description);
  }
  
  @Override
  public Object execute(Evaluator evaluator, EvaluationEnvironment environment, Object... parameters) throws EvaluationException
  {
    checkParameters(3, true, parameters);
    
    checkParameterType(0, parameters[0], DataTable.class);
    
    DataTable table = ((DataTable) parameters[0]).clone();
    
    TableFormat clone = table.getFormat().clone();
    
    Map<FieldFormat, Expression> newFields = new LinkedHashMap<>();
    
    for (int i = 1; i < parameters.length - 1; i = i + 2)
    {
      FieldFormat ff = FieldFormat.create(parameters[i].toString());
      clone.addField(ff);
      newFields.put(ff, new Expression(parameters[i + 1].toString()));
    }
    
    return processAddColumns(evaluator, environment, table, clone, newFields, false);
    
  }
  
  protected DataTable processAddColumns(Evaluator evaluator, EvaluationEnvironment environment, DataTable table, TableFormat clone, Map<FieldFormat, Expression> newFields, boolean concurrent)
      throws EvaluationException
  {
    table.setFormat(clone);
    
    table.joinFormats();
    
    for (Entry<FieldFormat, Expression> entry : newFields.entrySet())
    {
      
      List<Callable<Void>> tasks = newArrayList();
      for (int j = 0; j < table.getRecordCount(); j++)
      {
        final int finalJ = j;
        tasks.add(() -> {
          
          ReferenceResolver resolver = evaluator.getDefaultResolver();
          Evaluator localEvaluator = new Evaluator(resolver.getContextManager(), resolver.getDefaultContext(), table, resolver.getCallerController());
          localEvaluator.getEnvironmentResolver().setEnvironment(evaluator.getEnvironmentResolver().getEnvironment());
          
          for (Entry<String, ReferenceResolver> e : evaluator.getResolvers().entrySet())
          {
            if (e.getKey() != null)
            {
              localEvaluator.setResolver(e.getKey(), e.getValue());
            }
          }
          localEvaluator.getDefaultResolver().setDefaultRow(finalJ);
          
          // AGG-11208 Data Table binding expression is always evaluated for first record
          // it is scary to make changes to the general order of working with strings, so the changes were made only to this function
          EvaluationEnvironment eClone = environment.clone();
          if (eClone.getCause() != null)
            eClone.getCause().setRow(null);
          try
          {
            Object value = localEvaluator.evaluate(entry.getValue(), eClone);
            DataRecord rec = table.getRecord(finalJ);
            rec.setValue(entry.getKey().getName(), value);
            return null;
          }
          catch (SyntaxErrorException ex)
          {
            throw new EvaluationException(ex);
          }
          
        });
      }
      executeTasks(tasks, evaluator.getExecutorService(), concurrent);
    }
    return table;
  }
}