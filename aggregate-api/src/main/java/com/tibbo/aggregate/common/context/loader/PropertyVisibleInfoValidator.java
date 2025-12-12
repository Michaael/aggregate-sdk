package com.tibbo.aggregate.common.context.loader;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import javax.annotation.Nonnull;

import com.google.common.base.Strings;
import com.tibbo.aggregate.common.Log;
import com.tibbo.aggregate.common.context.AbstractContext;
import com.tibbo.aggregate.common.context.CallerController;
import com.tibbo.aggregate.common.context.Context;
import com.tibbo.aggregate.common.context.ContextException;
import com.tibbo.aggregate.common.context.ContextManager;
import com.tibbo.aggregate.common.datatable.DataTable;
import com.tibbo.aggregate.common.expression.EvaluationException;
import com.tibbo.aggregate.common.expression.function.table.UnionFunction;
import com.tibbo.aggregate.common.server.EditableChildContextConstants;
import com.tibbo.aggregate.common.server.ServerContextConstants;

/**
 * A Validator which checks is the context matched to the all the filters provided
 *
 * @author Alexander Sidorov
 * @since 01.04.2023
 * @see <a href="https://tibbotech.atlassian.net/browse/AGG-14058">AGG-14058</a>
 */
public class PropertyVisibleInfoValidator implements ContextValidator
{
  public static final String SEPARATOR = ",";
  private final ContextManager contextManager;
  private final CallerController callerController;
  private final Map<String, List<String>> propertyFilters;
  
  public PropertyVisibleInfoValidator(@Nonnull ContextManager contextManager,
      @Nonnull CallerController callerController,
      @Nonnull Map<String, List<String>> propertyFilters)
  {
    this.contextManager = contextManager;
    this.callerController = callerController;
    this.propertyFilters = propertyFilters;
  }
  
  @Override
  public boolean validate(String contextPath)
  {
    Context context = contextManager.get(contextPath, callerController);
    
    if (context == null)
    {
      return false;
    }
    
    if (propertyFilters.isEmpty())
    {
      return true;
    }
    
    for (String property : propertyFilters.keySet())
    {
      List<String> filters = propertyFilters.get(property);
      
      if (filters.isEmpty())
      {
        continue;
      }
      
      switch (property)
      {
        case AbstractContext.VF_CHILDREN_NAME:
          if (isNotMatched(filters, context.getName()))
          {
            return false;
          }
          
          break;
        
        case AbstractContext.VF_INFO_DESCRIPTION:
          if (isNotMatched(filters, context.getDescription()))
          {
            return false;
          }
          break;
        default:
          
          if (context.getVariableDefinition(EditableChildContextConstants.V_CHILD_INFO) != null)
          {
            try
            {
              DataTable childInfo = context.getVariable(EditableChildContextConstants.V_CHILD_INFO, callerController);
              DataTable unionInfo = childInfo;
              if (context.getVariableDefinition(ServerContextConstants.V_VISIBLE_INFO) != null)
              {
                DataTable visibleInfo = context.getVariable(ServerContextConstants.V_VISIBLE_INFO, callerController);
                unionInfo = (DataTable) new UnionFunction().execute(null, null, childInfo, visibleInfo);
              }
              
              if (unionInfo.hasField(property))
              {
                Object value = unionInfo.rec().getValue(property);
                
                if (!(value instanceof String))
                {
                  continue;
                }
                
                if (isNotMatched(filters, ((String) value)))
                {
                  return false;
                }
              }
            }
            catch (ContextException | EvaluationException e)
            {
              Log.CONTEXT_CHILDREN.debug("Unable to evaluate children filter expression fo the context: " + contextPath, e);
            }
          }
      }
    }
    return true;
  }
  
  private static boolean isNotMatched(List<String> filters, String value)
  {
    return filters.stream()
        .noneMatch(s -> Stream.of(s.split(SEPARATOR, -1))
            .anyMatch(Strings.nullToEmpty(value)::contains));
  }
}
