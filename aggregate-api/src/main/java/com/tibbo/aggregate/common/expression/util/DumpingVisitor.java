package com.tibbo.aggregate.common.expression.util;

import com.tibbo.aggregate.common.*;
import com.tibbo.aggregate.common.expression.*;
import com.tibbo.aggregate.common.expression.parser.*;

public class DumpingVisitor implements ExpressionParserVisitor
{
  private int indent = 0;
  
  private String indentString()
  {
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < indent; ++i)
    {
      sb.append(" ");
    }
    return sb.toString();
  }
  
  @Override
  public AttributedObject visit(SimpleNode node, EvaluationEnvironment data)
  {
    Log.CORE.warn(indentString() + node + ": acceptor not unimplemented in subclass?");
    ++indent;
    data = (EvaluationEnvironment) node.childrenAccept(this, data);
    --indent;
    return new DefaultAttributedObject(data);
  }
  
  protected AttributedObject dumpNode(SimpleNode node, EvaluationEnvironment data)
  {
    Log.CORE.warn(indentString() + node.jjtGetFirstToken().image + node + node.jjtGetLastToken().image);
    // Log.CORE.warn(indentString() + node);
    ++indent;
    data = (EvaluationEnvironment) node.childrenAccept(this, data);
    --indent;
    return new DefaultAttributedObject(data);
    
  }
  
  @Override
  public AttributedObject visit(ASTStart node, EvaluationEnvironment data)
  {
    return dumpNode(node, data);
  }
  
  @Override
  public AttributedObject visit(ASTConditionalNode node, EvaluationEnvironment data)
  {
    return dumpNode(node, data);
  }
  
  @Override
  public AttributedObject visit(ASTLogicalOrNode node, EvaluationEnvironment data)
  {
    return dumpNode(node, data);
  }
  
  @Override
  public AttributedObject visit(ASTLogicalAndNode node, EvaluationEnvironment data)
  {
    return dumpNode(node, data);
  }
  
  @Override
  public AttributedObject visit(ASTBitwiseOrNode node, EvaluationEnvironment data)
  {
    return dumpNode(node, data);
  }
  
  @Override
  public AttributedObject visit(ASTBitwiseXorNode node, EvaluationEnvironment data)
  {
    return dumpNode(node, data);
  }
  
  @Override
  public AttributedObject visit(ASTBitwiseAndNode node, EvaluationEnvironment data)
  {
    return dumpNode(node, data);
  }
  
  @Override
  public AttributedObject visit(ASTEQNode node, EvaluationEnvironment data)
  {
    return dumpNode(node, data);
  }
  
  @Override
  public AttributedObject visit(ASTNENode node, EvaluationEnvironment data)
  {
    return dumpNode(node, data);
  }
  
  @Override
  public AttributedObject visit(ASTRegexMatchNode node, EvaluationEnvironment data)
  {
    return dumpNode(node, data);
  }
  
  @Override
  public AttributedObject visit(ASTLTNode node, EvaluationEnvironment data)
  {
    return dumpNode(node, data);
  }
  
  @Override
  public AttributedObject visit(ASTGTNode node, EvaluationEnvironment data)
  {
    return dumpNode(node, data);
  }
  
  @Override
  public AttributedObject visit(ASTLENode node, EvaluationEnvironment data)
  {
    return dumpNode(node, data);
  }
  
  @Override
  public AttributedObject visit(ASTGENode node, EvaluationEnvironment data)
  {
    return dumpNode(node, data);
  }
  
  @Override
  public AttributedObject visit(ASTAddNode node, EvaluationEnvironment data)
  {
    return dumpNode(node, data);
  }
  
  @Override
  public AttributedObject visit(ASTSubtractNode node, EvaluationEnvironment data)
  {
    return dumpNode(node, data);
  }
  
  @Override
  public AttributedObject visit(ASTMulNode node, EvaluationEnvironment data)
  {
    return dumpNode(node, data);
  }
  
  @Override
  public AttributedObject visit(ASTDivNode node, EvaluationEnvironment data)
  {
    return dumpNode(node, data);
  }
  
  @Override
  public AttributedObject visit(ASTModNode node, EvaluationEnvironment data)
  {
    return dumpNode(node, data);
  }
  
  @Override
  public AttributedObject visit(ASTUnaryNode node, EvaluationEnvironment data)
  {
    return dumpNode(node, data);
  }
  
  @Override
  public AttributedObject visit(ASTLogicalNotNode node, EvaluationEnvironment data)
  {
    return dumpNode(node, data);
  }
  
  @Override
  public AttributedObject visit(ASTBitwiseNotNode node, EvaluationEnvironment data)
  {
    return dumpNode(node, data);
  }
  
  @Override
  public AttributedObject visit(ASTFunctionNode node, EvaluationEnvironment data)
  {
    return dumpNode(node, data);
  }
  
  @Override
  public AttributedObject visit(ASTValueReferenceNode node, EvaluationEnvironment data)
  {
    return dumpNode(node, data);
  }
  
  @Override
  public AttributedObject visit(ASTLongConstNode node, EvaluationEnvironment data)
  {
    return dumpNode(node, data);
  }
  
  @Override
  public AttributedObject visit(ASTFloatConstNode node, EvaluationEnvironment data)
  {
    return dumpNode(node, data);
  }
  
  @Override
  public AttributedObject visit(ASTStringConstNode node, EvaluationEnvironment data)
  {
    return dumpNode(node, data);
  }
  
  @Override
  public AttributedObject visit(ASTTrueNode node, EvaluationEnvironment data)
  {
    return dumpNode(node, data);
  }
  
  @Override
  public AttributedObject visit(ASTFalseNode node, EvaluationEnvironment data)
  {
    return dumpNode(node, data);
  }
  
  @Override
  public AttributedObject visit(ASTNullNode node, EvaluationEnvironment data)
  {
    return dumpNode(node, data);
  }
  
  @Override
  public AttributedObject visit(ASTRightShiftNode node, EvaluationEnvironment data)
  {
    return dumpNode(node, data);
  }
  
  @Override
  public AttributedObject visit(ASTUnsignedRightShiftNode node, EvaluationEnvironment data)  {    return dumpNode(node, data);  }
  
  @Override
  public AttributedObject visit(ASTLeftShiftNode node, EvaluationEnvironment data)
  {
    return dumpNode(node, data);
  }
}
