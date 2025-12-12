package com.tibbo.aggregate.common.expression.function;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.tibbo.aggregate.common.context.CallerController;
import com.tibbo.aggregate.common.context.Context;
import com.tibbo.aggregate.common.context.ContextException;
import com.tibbo.aggregate.common.context.ContextManager;
import com.tibbo.aggregate.common.context.ContextUtils;
import com.tibbo.aggregate.common.context.Contexts;
import com.tibbo.aggregate.common.datatable.DataRecord;
import com.tibbo.aggregate.common.datatable.DataTable;
import com.tibbo.aggregate.common.datatable.SimpleDataTable;
import com.tibbo.aggregate.common.datatable.field.StringFieldFormat;
import com.tibbo.aggregate.common.expression.EvaluationEnvironment;
import com.tibbo.aggregate.common.expression.EvaluationException;
import com.tibbo.aggregate.common.expression.Evaluator;
import com.tibbo.aggregate.common.expression.Function;
import com.tibbo.aggregate.common.expression.Reference;
import com.tibbo.aggregate.common.server.UtilitiesContextConstants;

public class ExpressionEditorOptionsFunction extends AbstractFunction
{
  public ExpressionEditorOptionsFunction()
  {
    super(Function.EXPRESSION_EDITOR_OPTIONS, Function.GROUP_SYSTEM, null, "String", "");
  }
  
  @Override
  public Object execute(Evaluator evaluator, EvaluationEnvironment environment, Object... parameters) throws EvaluationException
  {
    checkParameters(1, true, parameters);
    
    CallerController caller = evaluator.getDefaultResolver().getCallerController();
    ContextManager contextManager = evaluator.getDefaultResolver().getContextManager();
    
    String defaultContextMask = parameters[0] != null ? parameters[0].toString() : null;
    Context defaultContext = null;
    
    List<Context> contexts = new LinkedList<Context>();
    if (defaultContextMask != null && contextManager != null)
    {
      contexts = ContextUtils.expandMaskToContexts(defaultContextMask, contextManager, caller);
    }
    
    if (contexts.size() == 1)
    {
      defaultContext = contexts.get(0);
    }
    
    Map<Reference, String> references = new LinkedHashMap<Reference, String>();
    
    String entity = (parameters.length >= 3 && parameters[1] != null) ? parameters[1].toString() : null;
    Integer entityType = (parameters.length >= 3 && parameters[2] != null) ? Integer.valueOf(parameters[2].toString()) : null;
    DataTable defaultTable = null;
    
    DataTable additionalReferences = null;
    
    if (parameters.length >= 4 && parameters[3] != null && parameters[3] instanceof DataTable)
    {
      additionalReferences = (DataTable) parameters[3];
    }
    
    if (additionalReferences != null)
    {
      for (DataRecord rec : additionalReferences)
      {
        references.put(new Reference(rec.getString(StringFieldFormat.FIELD_ADDITIONAL_REFERENCES_REFERENCE)), rec.getString(StringFieldFormat.FIELD_ADDITIONAL_REFERENCES_DESCRIPTION));
      }
    }
    
    try
    {
      if (entity != null && entityType != null && contextManager != null)
      {
        final Context utilitiesContext = contextManager.get(Contexts.CTX_UTILITIES, caller);
        switch (entityType)
        {
          case ContextUtils.ENTITY_VARIABLE:
            for (Context cur : contexts)
            {
              if (cur.getVariableDefinition(entity, caller) != null)
              {
                defaultContext = cur;
                defaultTable = defaultContext.getVariable(entity, caller);
              }
            }
            
            if (defaultContextMask != null && utilitiesContext != null)
            {
              DataTable refs = utilitiesContext.callFunction(UtilitiesContextConstants.F_VARIABLE_FIELDS, caller, defaultContextMask, entity);
              for (DataRecord rec : refs)
              {
                references.put(new Reference(rec.getString(StringFieldFormat.FIELD_ADDITIONAL_REFERENCES_REFERENCE)), rec.getString(StringFieldFormat.FIELD_ADDITIONAL_REFERENCES_DESCRIPTION));
              }
            }
            break;
          
          case ContextUtils.ENTITY_FUNCTION:
            for (Context cur : contexts)
            {
              if (cur.getFunctionDefinition(entity, caller) != null)
              {
                defaultContext = cur;
                defaultTable = new SimpleDataTable(defaultContext.getFunctionDefinition(entity).getInputFormat(), true);
              }
            }
            break;
          
          case ContextUtils.ENTITY_EVENT:
            for (Context cur : contexts)
            {
              if (cur.getEventDefinition(entity, caller) != null)
              {
                defaultContext = cur;
                defaultTable = new SimpleDataTable(defaultContext.getEventDefinition(entity).getFormat(), true);
              }
            }
            
            if (defaultContextMask != null && utilitiesContext != null)
            {
              DataTable refs = utilitiesContext.callFunction(UtilitiesContextConstants.F_EVENT_FIELDS, caller, defaultContextMask, entity);
              for (DataRecord rec : refs)
              {
                references.put(new Reference(rec.getString(StringFieldFormat.FIELD_ADDITIONAL_REFERENCES_REFERENCE)), rec.getString(StringFieldFormat.FIELD_ADDITIONAL_REFERENCES_DESCRIPTION));
              }
            }
            break;
          
          default:
            throw new IllegalArgumentException("Unknown entity type: " + entityType);
        }
      }
    }
    catch (ContextException ex)
    {
      throw new EvaluationException(ex);
    }
    
    return StringFieldFormat.encodeExpressionEditorOptions(defaultContext, defaultTable, references);
  }
}
