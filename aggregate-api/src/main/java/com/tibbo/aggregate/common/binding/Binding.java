package com.tibbo.aggregate.common.binding;

import com.tibbo.aggregate.common.expression.Expression;
import com.tibbo.aggregate.common.expression.ExpressionUtils;
import com.tibbo.aggregate.common.expression.Reference;
import com.tibbo.aggregate.common.util.PublicCloneable;

public class Binding implements Cloneable, PublicCloneable
{
  private Long id;
  
  private Reference target;
  
  private Expression expression;
  
  private String queue;
  
  public Binding(Reference target, Expression expression)
  {
    this.target = target;
    this.expression = expression;
  }
  
  public Binding(String reference, String expression)
  {
    this.target = new Reference(reference);
    this.expression = new Expression(expression);
  }
  
  public Expression getExpression()
  {
    return expression;
  }

  public void setExpression(Expression expression)
  {
    this.expression = expression;
  }

  public Reference getTarget()
  {
    return target;
  }
  
  public Long getId()
  {
    return id;
  }
  
  public void setId(Long id)
  {
    this.id = id;
  }
  
  public String getQueue()
  {
    return queue;
  }
  
  public void setQueue(String queue)
  {
    this.queue = queue;
  }
  
  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((expression == null) ? 0 : expression.hashCode());
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    result = prime * result + ((target == null) ? 0 : target.hashCode());
    return result;
  }
  
  @Override
  public boolean equals(Object obj)
  {
    if (this == obj)
    {
      return true;
    }
    if (obj == null)
    {
      return false;
    }
    if (getClass() != obj.getClass())
    {
      return false;
    }
    Binding other = (Binding) obj;
    if (expression == null)
    {
      if (other.expression != null)
      {
        return false;
      }
    }
    else if (!expression.equals(other.expression))
    {
      return false;
    }
    if (id == null)
    {
      if (other.id != null)
      {
        return false;
      }
    }
    else if (!id.equals(other.id))
    {
      return false;
    }
    if (target == null)
    {
      if (other.target != null)
      {
        return false;
      }
    }
    else if (!target.equals(other.target))
    {
      return false;
    }
    return true;
  }
  
  @Override
  public Binding clone()
  {
    try
    {
      Binding clone = (Binding) super.clone();
      clone.target = target.clone();
      clone.expression = expression != null ? expression.clone() : null;
      clone.id = id == null ? null : ExpressionUtils.generateBindingId();
      return clone;
    }
    catch (CloneNotSupportedException ex)
    {
      throw new IllegalStateException(ex.getMessage(), ex);
    }
  }
  
  @Override
  public String toString()
  {
    return target + " = " + expression;
  }
}
