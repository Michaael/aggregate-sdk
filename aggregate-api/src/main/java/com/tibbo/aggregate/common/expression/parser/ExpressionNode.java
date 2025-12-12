package com.tibbo.aggregate.common.expression.parser;

import com.tibbo.aggregate.common.expression.*;

public class ExpressionNode extends SimpleNode
{
  public ExpressionNode(int i)
  {
    super(i);
  }
  
  public ExpressionNode(ExpressionParser p, int i)
  {
    super(p, i);
  }
  
  public Object childrenAccept(ExpressionParserVisitor visitor, com.tibbo.aggregate.common.expression.EvaluationEnvironment data)
  {
    if (children != null)
    {
      NodeEvaluationDetails activeNode = null;
      
      boolean debug = data != null && data.isDebug();
      
      if (debug)
      {
        activeNode = data.getActiveNode();
        
        if (activeNode == null)
        {
          activeNode = new NodeEvaluationDetails();
          
          data.setActiveNode(activeNode);
        }
      }
      
      for (int i = 0; i < children.length; ++i)
      {
        NodeEvaluationDetails childNode = null;
        
        if (debug)
        {
          childNode = new NodeEvaluationDetails();
          
          activeNode.addChild(childNode);
          
          data.setActiveNode(childNode);
        }
        
        AttributedObject result = children[i].jjtAccept(visitor, data);
        
        if (debug)
        {
          data.setActiveNode(activeNode);
          
          childNode.setNodeResult(result);
        }
      }
      
    }
    
    return data;
  }
  
}
