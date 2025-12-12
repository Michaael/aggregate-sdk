package com.tibbo.aggregate.common.expression;

import com.tibbo.aggregate.common.expression.parser.*;
import com.tibbo.aggregate.common.tests.*;

public class TestEvaluationDetails extends CommonsTestCase
{
  private Evaluator ev;
  
  public void setUp() throws Exception
  {
    super.setUp();
    ev = CommonsFixture.createTestEvaluator();
  }
  
  public void testEvaluationDetails() throws Exception
  {
    EvaluationEnvironment env = new EvaluationEnvironment();
    env.setDebug(true);
    
    ev.evaluate(new Expression("1 + 2"), env);
    
    assertEquals(1, env.getRootNode().getChildren().size());
    assertEquals(null, env.getRootNode().getNodeImage());
    
    NodeEvaluationDetails addNode = env.getRootNode().getChildren().get(0);
    assertEquals("+", addNode.getNodeImage());
    assertEquals(3l, addNode.getNodeResult().getValue());
    assertEquals(2, addNode.getChildren().size());
    
    NodeEvaluationDetails constNode = addNode.getChildren().get(1);
    assertEquals("2", constNode.getNodeImage());
    assertEquals(2l, constNode.getNodeResult().getValue());
    assertEquals(0, constNode.getChildren().size());
    
  }
  
}
