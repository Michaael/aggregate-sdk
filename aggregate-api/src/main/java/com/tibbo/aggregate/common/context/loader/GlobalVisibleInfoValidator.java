package com.tibbo.aggregate.common.context.loader;

import java.util.Map;

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
 * A Validator which checks is the context matched to any of the filters provided
 *
 * @author Alexander Sidorov
 * @since 01.04.2023
 * @see <a href="https://tibbotech.atlassian.net/browse/AGG-14058">AGG-14058</a>
 */
public class GlobalVisibleInfoValidator implements ContextValidator
{
  private final ContextManager contextManager;
  private final CallerController callerController;
  private final Map<String, String> propertyFilter;
  
  public GlobalVisibleInfoValidator(ContextManager contextManager, CallerController callerController, Map<String, String> propertyFilter)
  {
    
    this.contextManager = contextManager;
    this.callerController = callerController;
    this.propertyFilter = propertyFilter;
  }
  
  @Override
  public boolean validate(String contextPath)
  {
    Context context = contextManager.get(contextPath, callerController);
    
    if (context == null)
    {
      return false;
    }
    
    if (propertyFilter.isEmpty())
    {
      return true;
    }
    
    for (Map.Entry<String, String> property : propertyFilter.entrySet())
    {
      switch (property.getKey())
      {
        case AbstractContext.VF_CHILDREN_NAME:
          if (Strings.nullToEmpty(context.getName()).contains(property.getValue()))
          {
            return true;
          }
          break;
        
        case AbstractContext.VF_INFO_DESCRIPTION:
          if (Strings.nullToEmpty(context.getDescription()).contains(property.getValue()))
          {
            return true;
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
              
              if (unionInfo.hasField(property.getKey()))
              {
                Object value = unionInfo.rec().getValue(property.getKey());
                
                if (!(value instanceof String))
                {
                  continue;
                }
                
                if (Strings.nullToEmpty(((String) value)).contains(property.getValue()))
                {
                  return true;
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
    return false;
  }
}
