package com.tibbo.aggregate.common.binding;

import com.tibbo.aggregate.common.context.ContextUtils;
import com.tibbo.aggregate.common.expression.ExpressionUtils;
import com.tibbo.aggregate.common.expression.Reference;
import com.tibbo.aggregate.common.util.SyntaxErrorException;

public class BindingUtils
{
  
  /**
   * Returns function Reference if passed binding related to function defined in passed Reference <code>functionRef</code>. If <code>functionRef</code> is null then returns first found function
   * reference (if binding related to any).
   */
  public static Reference isFunctionBinding(Binding bg, Reference functionRef) throws SyntaxErrorException
  {
    if (functionRef == null)
    {
      if (bg.getTarget().getEntityType() == ContextUtils.ENTITY_FUNCTION)
      {
        return bg.getTarget();
      }
    }
    else if (functionRef.getEntity().equals(bg.getTarget().getEntity()))
    {
      return bg.getTarget();
    }
    
    if (bg.getExpression() != null)
    {
      for (Reference r : ExpressionUtils.findReferences(bg.getExpression()))
      {
        if (r.getEntityType() == ContextUtils.ENTITY_FUNCTION)
        {
          if ((functionRef == null || functionRef.getEntity().equals(r.getEntity())))
          {
            return r;
          }
        }
      }
    }
    
    return null;
  }
  
}
