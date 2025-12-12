package com.tibbo.aggregate.common.binding;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.tibbo.aggregate.common.expression.Expression;
import com.tibbo.aggregate.common.expression.ExpressionUtils;
import com.tibbo.aggregate.common.expression.Reference;
import com.tibbo.aggregate.common.expression.parser.ASTStart;
import com.tibbo.aggregate.common.expression.util.ReferencesFinderVisitor;
import com.tibbo.aggregate.common.util.PublicCloneable;
import com.tibbo.aggregate.common.util.SyntaxErrorException;
import com.tibbo.aggregate.common.util.Util;

public class ExtendedBinding implements Cloneable, PublicCloneable
{
  private Binding binding;
  private EvaluationOptions evaluationOptions;
  
  private String cachedExpression;
  private ASTStart cachedRootNode;
  
  public ExtendedBinding(Binding binding, EvaluationOptions evaluationOptions)
  {
    super();
    this.binding = binding;
    this.evaluationOptions = evaluationOptions;
  }
  
  @Override
  public ExtendedBinding clone()
  {
    try
    {
      ExtendedBinding clone = (ExtendedBinding) super.clone();
      clone.binding = binding.clone();
      clone.evaluationOptions = evaluationOptions.clone();
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
    return "ExtendedBinding [binding=" + binding + "]";
  }
  
  public Binding getBinding()
  {
    return binding;
  }
  
  public void setBinding(Binding binding)
  {
    this.binding = binding;
  }
  
  public EvaluationOptions getEvaluationOptions()
  {
    return evaluationOptions;
  }
  
  public void setEvaluationOptions(EvaluationOptions evaluationOptions)
  {
    this.evaluationOptions = evaluationOptions;
  }
  
  public Collection<Reference> getAllReferences() throws SyntaxErrorException
  {
    final ArrayList<Reference> allReferences = new ArrayList<Reference>();
    
    if (binding.getTarget() != null)
      allReferences.add(binding.getTarget());
    
    if (getEvaluationOptions().getActivator() != null)
      allReferences.add(getEvaluationOptions().getActivator());
    
    allReferences.addAll(findExpressionReferences());
    
    return allReferences;
  }
  
  private List<Reference> findExpressionReferences() throws SyntaxErrorException
  {
    List<Reference> result = Collections.emptyList();
    
    Expression expression = binding.getExpression();
    
    if (expression == null)
    {
      return result;
    }
    
    boolean notCachedYet = !Util.equals(expression.getText(), cachedExpression);
    
    if (notCachedYet)
    {
      cachedExpression = expression.getText();
      cachedRootNode = ExpressionUtils.parse(expression, true);
    }
    
    if (cachedRootNode != null)
    {
      ReferencesFinderVisitor visitor = new ReferencesFinderVisitor();
      cachedRootNode.jjtAccept(visitor, null);
      result = visitor.getIdentifiers();
    }
    
    return result;
  }
  
  public String displayString()
  {
    return binding.toString();
  }

  @Override
  public boolean equals(Object o)
  {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    ExtendedBinding that = (ExtendedBinding) o;

    if (!binding.equals(that.binding)) return false;
    return evaluationOptions.equals(that.evaluationOptions);
  }

  @Override
  public int hashCode()
  {
    int result = binding.hashCode();
    result = 31 * result + evaluationOptions.hashCode();
    return result;
  }
}
