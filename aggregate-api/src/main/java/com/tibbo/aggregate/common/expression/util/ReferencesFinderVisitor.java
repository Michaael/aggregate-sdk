package com.tibbo.aggregate.common.expression.util;

import java.util.*;

import com.tibbo.aggregate.common.expression.*;
import com.tibbo.aggregate.common.expression.parser.*;
import com.tibbo.aggregate.common.util.*;

public class ReferencesFinderVisitor implements ExpressionParserVisitor
{
  private final List<Reference> references = new LinkedList();
  
  public ReferencesFinderVisitor()
  {
    super();
  }
  
  @Override
  public AttributedObject visit(SimpleNode node, EvaluationEnvironment data)
  {
    node.childrenAccept(this, data);
    return null;
  }
  
  @Override
  public AttributedObject visit(ASTStart node, EvaluationEnvironment data)
  {
    node.childrenAccept(this, data);
    return null;
  }
  
  @Override
  public AttributedObject visit(ASTConditionalNode node, EvaluationEnvironment data)
  {
    node.childrenAccept(this, data);
    return null;
  }
  
  @Override
  public AttributedObject visit(ASTLogicalOrNode node, EvaluationEnvironment data)
  {
    node.childrenAccept(this, data);
    return null;
  }
  
  @Override
  public AttributedObject visit(ASTLogicalAndNode node, EvaluationEnvironment data)
  {
    node.childrenAccept(this, data);
    return null;
  }
  
  @Override
  public AttributedObject visit(ASTBitwiseOrNode node, EvaluationEnvironment data)
  {
    node.childrenAccept(this, data);
    return null;
  }
  
  @Override
  public AttributedObject visit(ASTBitwiseXorNode node, EvaluationEnvironment data)
  {
    node.childrenAccept(this, data);
    return null;
  }
  
  @Override
  public AttributedObject visit(ASTBitwiseAndNode node, EvaluationEnvironment data)
  {
    node.childrenAccept(this, data);
    return null;
  }
  
  @Override
  public AttributedObject visit(ASTEQNode node, EvaluationEnvironment data)
  {
    node.childrenAccept(this, data);
    return null;
  }
  
  @Override
  public AttributedObject visit(ASTNENode node, EvaluationEnvironment data)
  {
    node.childrenAccept(this, data);
    return null;
  }
  
  @Override
  public AttributedObject visit(ASTRegexMatchNode node, EvaluationEnvironment data)
  {
    node.childrenAccept(this, data);
    return null;
  }
  
  @Override
  public AttributedObject visit(ASTLTNode node, EvaluationEnvironment data)
  {
    node.childrenAccept(this, data);
    return null;
  }
  
  @Override
  public AttributedObject visit(ASTGTNode node, EvaluationEnvironment data)
  {
    node.childrenAccept(this, data);
    return null;
  }
  
  @Override
  public AttributedObject visit(ASTLENode node, EvaluationEnvironment data)
  {
    node.childrenAccept(this, data);
    return null;
  }
  
  @Override
  public AttributedObject visit(ASTGENode node, EvaluationEnvironment data)
  {
    node.childrenAccept(this, data);
    return null;
  }
  
  @Override
  public AttributedObject visit(ASTAddNode node, EvaluationEnvironment data)
  {
    node.childrenAccept(this, data);
    return null;
  }
  
  @Override
  public AttributedObject visit(ASTSubtractNode node, EvaluationEnvironment data)
  {
    node.childrenAccept(this, data);
    return null;
  }
  
  @Override
  public AttributedObject visit(ASTMulNode node, EvaluationEnvironment data)
  {
    node.childrenAccept(this, data);
    return null;
  }
  
  @Override
  public AttributedObject visit(ASTDivNode node, EvaluationEnvironment data)
  {
    node.childrenAccept(this, data);
    return null;
  }
  
  @Override
  public AttributedObject visit(ASTModNode node, EvaluationEnvironment data)
  {
    node.childrenAccept(this, data);
    return null;
  }
  
  @Override
  public AttributedObject visit(ASTUnaryNode node, EvaluationEnvironment data)
  {
    node.childrenAccept(this, data);
    return null;
  }
  
  @Override
  public AttributedObject visit(ASTLogicalNotNode node, EvaluationEnvironment data)
  {
    node.childrenAccept(this, data);
    return null;
  }
  
  @Override
  public AttributedObject visit(ASTBitwiseNotNode node, EvaluationEnvironment data)
  {
    node.childrenAccept(this, data);
    return null;
  }
  
  @Override
  public AttributedObject visit(ASTFunctionNode node, EvaluationEnvironment data)
  {
    node.childrenAccept(this, data);
    return null;
  }
  
  @Override
  public AttributedObject visit(ASTValueReferenceNode node, EvaluationEnvironment data)
  {
    Reference ref = new Reference(node.uriImage);
    
    references.add(ref);
    
    for (Object param : ref.getParameters())
    {
      if (param instanceof Expression)
      {
        try
        {
          references.addAll(ExpressionUtils.findReferences((Expression) param));
        }
        catch (SyntaxErrorException ex)
        {
          throw new IllegalStateException(ex.getMessage(), ex);
        }
      }
    }
    
    return null;
  }
  
  @Override
  public AttributedObject visit(ASTLongConstNode node, EvaluationEnvironment data)
  {
    node.childrenAccept(this, data);
    return null;
  }
  
  @Override
  public AttributedObject visit(ASTFloatConstNode node, EvaluationEnvironment data)
  {
    node.childrenAccept(this, data);
    return null;
  }
  
  @Override
  public AttributedObject visit(ASTStringConstNode node, EvaluationEnvironment data)
  {
    node.childrenAccept(this, data);
    return null;
  }
  
  @Override
  public AttributedObject visit(ASTTrueNode node, EvaluationEnvironment data)
  {
    node.childrenAccept(this, data);
    return null;
  }
  
  @Override
  public AttributedObject visit(ASTFalseNode node, EvaluationEnvironment data)
  {
    node.childrenAccept(this, data);
    return null;
  }
  
  @Override
  public AttributedObject visit(ASTNullNode node, EvaluationEnvironment data)
  {
    node.childrenAccept(this, data);
    return null;
  }
  
  public List getIdentifiers()
  {
    return references;
  }
  
  @Override
  public AttributedObject visit(ASTRightShiftNode node, EvaluationEnvironment data)
  {
    node.childrenAccept(this, data);
    return null;
  }
  
  @Override
  public AttributedObject visit(ASTUnsignedRightShiftNode node, EvaluationEnvironment data)
  {
    node.childrenAccept(this, data);
    return null;
  }
  
  @Override
  public AttributedObject visit(ASTLeftShiftNode node, EvaluationEnvironment data)
  {
    node.childrenAccept(this, data);
    return null;
  }
}
