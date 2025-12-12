package com.tibbo.aggregate.common.expression;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.datatable.FieldFormat;
import com.tibbo.aggregate.common.expression.function.DefaultFunctions;
import com.tibbo.aggregate.common.expression.parser.ASTAddNode;
import com.tibbo.aggregate.common.expression.parser.ASTBitwiseAndNode;
import com.tibbo.aggregate.common.expression.parser.ASTBitwiseNotNode;
import com.tibbo.aggregate.common.expression.parser.ASTBitwiseOrNode;
import com.tibbo.aggregate.common.expression.parser.ASTBitwiseXorNode;
import com.tibbo.aggregate.common.expression.parser.ASTConditionalNode;
import com.tibbo.aggregate.common.expression.parser.ASTDivNode;
import com.tibbo.aggregate.common.expression.parser.ASTEQNode;
import com.tibbo.aggregate.common.expression.parser.ASTFalseNode;
import com.tibbo.aggregate.common.expression.parser.ASTFloatConstNode;
import com.tibbo.aggregate.common.expression.parser.ASTFunctionNode;
import com.tibbo.aggregate.common.expression.parser.ASTGENode;
import com.tibbo.aggregate.common.expression.parser.ASTGTNode;
import com.tibbo.aggregate.common.expression.parser.ASTLENode;
import com.tibbo.aggregate.common.expression.parser.ASTLTNode;
import com.tibbo.aggregate.common.expression.parser.ASTLeftShiftNode;
import com.tibbo.aggregate.common.expression.parser.ASTLogicalAndNode;
import com.tibbo.aggregate.common.expression.parser.ASTLogicalNotNode;
import com.tibbo.aggregate.common.expression.parser.ASTLogicalOrNode;
import com.tibbo.aggregate.common.expression.parser.ASTLongConstNode;
import com.tibbo.aggregate.common.expression.parser.ASTModNode;
import com.tibbo.aggregate.common.expression.parser.ASTMulNode;
import com.tibbo.aggregate.common.expression.parser.ASTNENode;
import com.tibbo.aggregate.common.expression.parser.ASTNullNode;
import com.tibbo.aggregate.common.expression.parser.ASTRegexMatchNode;
import com.tibbo.aggregate.common.expression.parser.ASTRightShiftNode;
import com.tibbo.aggregate.common.expression.parser.ASTStart;
import com.tibbo.aggregate.common.expression.parser.ASTStringConstNode;
import com.tibbo.aggregate.common.expression.parser.ASTSubtractNode;
import com.tibbo.aggregate.common.expression.parser.ASTTrueNode;
import com.tibbo.aggregate.common.expression.parser.ASTUnaryNode;
import com.tibbo.aggregate.common.expression.parser.ASTUnsignedRightShiftNode;
import com.tibbo.aggregate.common.expression.parser.ExpressionParserVisitor;
import com.tibbo.aggregate.common.expression.parser.SimpleNode;
import com.tibbo.aggregate.common.structure.CallKind;
import com.tibbo.aggregate.common.structure.CallLocation;
import com.tibbo.aggregate.common.structure.Pinpoint;
import com.tibbo.aggregate.common.util.Util;

public abstract class AbstractEvaluatingVisitor implements ExpressionParserVisitor
{
  private static final String TEMP_FORMAT_NAME = "name";
  
  public static Map<String, Function> DEFAULT_FUNCTIONS = new LinkedHashMap<>();
  
  static
  {
   
    DefaultFunctions.registerDefaultFunctions(DEFAULT_FUNCTIONS);
  }
  
  private final Evaluator evaluator;
  
  protected int top = -1;
  private final List<AttributedObject> stack = new ArrayList<>();
  
  public AbstractEvaluatingVisitor(Evaluator evaluator)
  {
    this.evaluator = evaluator;
    
  }
  

  
  protected Evaluator getEvaluator()
  {
    return evaluator;
  }
  
  protected AttributedObject set(int delta, AttributedObject value)
  {
    top += delta;
    
    for (int i = stack.size(); i <= top; i++)
    {
      stack.add(null);
    }
    
    stack.set(top, value);
    
    return value;
  }
  
  public Object getResult()
  {
    Object result = get(0);
    top--;
    return result;
  }
  
  protected AttributedObject get(int delta)
  {
    return stack.isEmpty() ? null : stack.get(top + delta);
  }
  
  public void describeNode(EvaluationEnvironment data, String image)
  {
    if (data != null && data.getActiveNode() != null)
    {
      data.getActiveNode().setNodeImage(image);
    }
  }
  
  @Override
  public AttributedObject visit(SimpleNode node, EvaluationEnvironment data)
  {
    return null;
  }
  
  @Override
  public AttributedObject visit(ASTStart node, EvaluationEnvironment data)
  {
    describeNode(data, "");
    
    node.childrenAccept(this, data);
    
    if (top == -1) // Empty expression
    {
      return null;
    }
    else
    {
      return get(0);
    }
  }
  
  @Override
  public AttributedObject visit(ASTConditionalNode node, EvaluationEnvironment data)
  {
    describeNode(data, "? :");
    
    node.jjtGetChild(0).jjtAccept(this, data);
    
    AttributedObject ao = get(0);
    
    boolean condition = Util.convertToBoolean(ExpressionUtils.getValue(ao), true, false);
    
    if (condition)
    {
      node.jjtGetChild(1).jjtAccept(this, data);
    }
    else
    {
      node.jjtGetChild(2).jjtAccept(this, data);
    }
    
    return set(-1, get(0));
  }
  
  @Override
  public AttributedObject visit(ASTLogicalOrNode node, EvaluationEnvironment data)
  {
    describeNode(data, "||");
    
    node.jjtGetChild(0).jjtAccept(this, data);
    
    AttributedObject lao = get(0);
    
    boolean left = Util.convertToBoolean(ExpressionUtils.getValue(lao), true, false);
    
    if (left)
    {
      return set(0, ExpressionUtils.toAttributed(Boolean.TRUE, lao));
    }
    
    node.jjtGetChild(1).jjtAccept(this, data);
    
    AttributedObject rao = get(0);
    
    boolean right = Util.convertToBoolean(ExpressionUtils.getValue(rao), true, false);
    
    return set(-1, ExpressionUtils.toAttributed(left || right, lao, rao));
  }
  
  @Override
  public AttributedObject visit(ASTLogicalAndNode node, EvaluationEnvironment data)
  {
    describeNode(data, "&&");
    
    node.jjtGetChild(0).jjtAccept(this, data);
    
    AttributedObject lao = get(0);
    
    boolean left = Util.convertToBoolean(ExpressionUtils.getValue(lao), true, false);
    
    if (!left)
    {
      return set(0, ExpressionUtils.toAttributed(Boolean.FALSE, lao));
    }
    
    node.jjtGetChild(1).jjtAccept(this, data);
    
    AttributedObject rao = get(0);
    
    boolean right = Util.convertToBoolean(ExpressionUtils.getValue(rao), true, false);
    
    return set(-1, ExpressionUtils.toAttributed(left && right, lao, rao));
  }
  
  @Override
  public AttributedObject visit(ASTBitwiseAndNode node, EvaluationEnvironment data)
  {
    describeNode(data, "&");
    
    node.childrenAccept(this, data);
    
    AttributedObject lao = get(-1);
    AttributedObject rao = get(0);
    
    Number left = Util.convertToNumber(ExpressionUtils.getValue(lao), false, true);
    Number right = Util.convertToNumber(ExpressionUtils.getValue(rao), false, true);
    
    if ((left == null) || (right == null))
    {
      return set(-1, ExpressionUtils.toAttributed(null));
    }
    
    if ((left instanceof Integer) && (right instanceof Integer))
    {
      return set(-1, ExpressionUtils.toAttributed(left.intValue() & right.intValue(), lao, rao));
    }
    else
    {
      return set(-1, ExpressionUtils.toAttributed(left.longValue() & right.longValue(), lao, rao));
    }
  }
  
  @Override
  public AttributedObject visit(ASTBitwiseOrNode node, EvaluationEnvironment data)
  {
    describeNode(data, "|");
    
    node.childrenAccept(this, data);
    
    AttributedObject lao = get(-1);
    AttributedObject rao = get(0);
    
    Number left = Util.convertToNumber(ExpressionUtils.getValue(lao), false, true);
    Number right = Util.convertToNumber(ExpressionUtils.getValue(rao), false, true);
    
    if ((left == null) || (right == null))
    {
      return set(-1, null);
    }
    
    if ((left instanceof Integer) && (right instanceof Integer))
    {
      return set(-1, ExpressionUtils.toAttributed(left.intValue() | right.intValue(), lao, rao));
    }
    else
    {
      return set(-1, ExpressionUtils.toAttributed(left.longValue() | right.longValue(), lao, rao));
    }
  }
  
  @Override
  public AttributedObject visit(ASTBitwiseXorNode node, EvaluationEnvironment data)
  {
    describeNode(data, "^");
    
    node.childrenAccept(this, data);
    
    AttributedObject lao = get(-1);
    AttributedObject rao = get(0);
    
    Number left = Util.convertToNumber(ExpressionUtils.getValue(lao), false, true);
    Number right = Util.convertToNumber(ExpressionUtils.getValue(rao), false, true);
    
    if ((left == null) || (right == null))
    {
      return set(-1, null);
    }
    
    if ((left instanceof Integer) && (right instanceof Integer))
    {
      return set(-1, ExpressionUtils.toAttributed(left.intValue() ^ right.intValue(), lao, rao));
    }
    else
    {
      return set(-1, ExpressionUtils.toAttributed(left.longValue() ^ right.longValue(), lao, rao));
    }
  }
  
  public static boolean equal(Object v1, Object v2)
  {
    if ((v1 == null) || (v2 == null))
    {
      return Util.equals(v1, v2);
    }
    else
    {
      if (v1.getClass().isAssignableFrom(v2.getClass()) || v2.getClass().isAssignableFrom(v1.getClass()))
      {
        return v1.equals(v2);
      }
      else
      {
        FieldFormat f1 = FieldFormat.create(TEMP_FORMAT_NAME, v1.getClass());
        FieldFormat f2 = FieldFormat.create(TEMP_FORMAT_NAME, v2.getClass());
        
        return f1.valueToString(v1).equals(f2.valueToString(v2));
      }
    }
  }
  
  public static int compare(Object v1, Object v2)
  {
    if ((v1 == null) && (v2 == null))
    {
      return 0;
    }
    else if (v1 == null || v2 == null)
    {
      return -1;
    }
    else
    {
      if ((v1 instanceof Comparable) && (v2 instanceof Comparable))
      {
        Comparable c1 = (Comparable) v1;
        Comparable c2 = (Comparable) v2;
        
        if (c1.getClass().equals(c2.getClass()))
        {
          return c1.compareTo(c2);
        }
        else
        {
          Number n1 = Util.convertToNumber(c1, false, true);
          Number n2 = Util.convertToNumber(c2, false, true);
          if ((n1 != null) && (n2 != null))
          {
            if (isFloatingPoint(n1) || isFloatingPoint(n2))
            {
              return Float.compare(n1.floatValue(), n2.floatValue());
            }
            else
            {
              return Long.compare(n1.longValue(), n2.longValue());
            }
          }
        }
      }
    }
    
    FieldFormat f1 = FieldFormat.create(TEMP_FORMAT_NAME, v1.getClass());
    FieldFormat f2 = FieldFormat.create(TEMP_FORMAT_NAME, v2.getClass());
    
    return f1.valueToString(v1).compareTo(f2.valueToString(v2));
  }
  
  private static boolean isFloatingPoint(Number number)
  {
    return (number instanceof Float) || (number instanceof Double);
  }
  
  @Override
  public AttributedObject visit(ASTEQNode node, EvaluationEnvironment data)
  {
    describeNode(data, "==");
    
    node.childrenAccept(this, data);
    AttributedObject lao = get(-1);
    AttributedObject rao = get(0);
    return set(-1, ExpressionUtils.toAttributed(equal(ExpressionUtils.getValue(lao), ExpressionUtils.getValue(rao)), lao, rao));
  }
  
  @Override
  public AttributedObject visit(ASTNENode node, EvaluationEnvironment data)
  {
    describeNode(data, "!=");
    
    node.childrenAccept(this, data);
    AttributedObject lao = get(-1);
    AttributedObject rao = get(0);
    return set(-1, ExpressionUtils.toAttributed(!equal(ExpressionUtils.getValue(lao), ExpressionUtils.getValue(rao)), lao, rao));
  }
  
  @Override
  public AttributedObject visit(ASTRegexMatchNode node, EvaluationEnvironment data)
  {
    describeNode(data, "~=");
    
    node.childrenAccept(this, data);
    AttributedObject lao = get(-1);
    AttributedObject rao = get(0);
    Object left = ExpressionUtils.getValue(lao);
    Object right = ExpressionUtils.getValue(rao);
    if ((left == null) || (right == null))
    {
      return ExpressionUtils.toAttributed(Boolean.FALSE, lao, rao);
    }
    return set(-1, ExpressionUtils.toAttributed(left.toString().matches(right.toString()), lao, rao));
  }
  
  @Override
  public AttributedObject visit(ASTLTNode node, EvaluationEnvironment data)
  {
    describeNode(data, "<");
    
    node.childrenAccept(this, data);
    AttributedObject lao = get(-1);
    AttributedObject rao = get(0);
    return set(-1, ExpressionUtils.toAttributed(compare(ExpressionUtils.getValue(lao), ExpressionUtils.getValue(rao)) < 0, lao, rao));
  }
  
  @Override
  public AttributedObject visit(ASTGTNode node, EvaluationEnvironment data)
  {
    describeNode(data, ">");
    
    node.childrenAccept(this, data);
    AttributedObject lao = get(-1);
    AttributedObject rao = get(0);
    return set(-1, ExpressionUtils.toAttributed(compare(ExpressionUtils.getValue(lao), ExpressionUtils.getValue(rao)) > 0, lao, rao));
  }
  
  @Override
  public AttributedObject visit(ASTLENode node, EvaluationEnvironment data)
  {
    describeNode(data, "<=");
    
    node.childrenAccept(this, data);
    AttributedObject lao = get(-1);
    AttributedObject rao = get(0);
    return set(-1, ExpressionUtils.toAttributed(compare(ExpressionUtils.getValue(lao), ExpressionUtils.getValue(rao)) <= 0, lao, rao));
  }
  
  @Override
  public AttributedObject visit(ASTGENode node, EvaluationEnvironment data)
  {
    describeNode(data, ">=");
    
    node.childrenAccept(this, data);
    AttributedObject lao = get(-1);
    AttributedObject rao = get(0);
    return set(-1, ExpressionUtils.toAttributed(compare(ExpressionUtils.getValue(lao), ExpressionUtils.getValue(rao)) >= 0, lao, rao));
  }
  
  @Override
  public AttributedObject visit(ASTAddNode node, EvaluationEnvironment data)
  {
    describeNode(data, "+");
    
    node.childrenAccept(this, data);
    
    AttributedObject lao = get(-1);
    AttributedObject rao = get(0);
    
    Object lo = ExpressionUtils.getValue(lao);
    Object ro = ExpressionUtils.getValue(rao);
    
    if ((lo instanceof String) && (ro instanceof String))
    {
      return set(-1, ExpressionUtils.toAttributed((String) lo + (String) ro, lao, rao));
    }
    else
    {
      if ((lo instanceof String) || (ro instanceof String))
      {
        String s1 = lo != null ? lo.toString() : "";
        String s2 = ro != null ? ro.toString() : "";
        return set(-1, ExpressionUtils.toAttributed(s1 + s2, lao, rao));
      }
      else
      {
        Number left = Util.convertToNumber(lo, false, true);
        Number right = Util.convertToNumber(ro, false, true);
        
        if ((left == null) || (right == null))
        {
          return set(-1, ExpressionUtils.toAttributed(null));
        }
        
        if (isFloatingPoint(left) || isFloatingPoint(right))
        {
          return set(-1, ExpressionUtils.toAttributed(left.doubleValue() + right.doubleValue(), lao, rao));
        }
        else
        {
          return set(-1, ExpressionUtils.toAttributed(left.longValue() + right.longValue(), lao, rao));
        }
      }
    }
  }
  
  @Override
  public AttributedObject visit(ASTSubtractNode node, EvaluationEnvironment data)
  {
    describeNode(data, "-");
    
    node.childrenAccept(this, data);
    
    AttributedObject lao = get(-1);
    AttributedObject rao = get(0);
    
    Object lo = ExpressionUtils.getValue(lao);
    Object ro = ExpressionUtils.getValue(rao);
    
    Number left = Util.convertToNumber(lo, false, true);
    Number right = Util.convertToNumber(ro, false, true);
    
    if ((left == null) || (right == null))
    {
      return set(-1, ExpressionUtils.toAttributed(null));
    }
    
    if (isFloatingPoint(left) || isFloatingPoint(right))
    {
      return set(-1, ExpressionUtils.toAttributed(left.doubleValue() - right.doubleValue(), lao, rao));
    }
    else
    {
      return set(-1, ExpressionUtils.toAttributed(left.longValue() - right.longValue(), lao, rao));
    }
  }
  
  @Override
  public AttributedObject visit(ASTMulNode node, EvaluationEnvironment data)
  {
    describeNode(data, "*");
    
    node.childrenAccept(this, data);
    
    AttributedObject lao = get(-1);
    AttributedObject rao = get(0);
    
    Object lo = ExpressionUtils.getValue(lao);
    Object ro = ExpressionUtils.getValue(rao);
    
    Number left = Util.convertToNumber(lo, false, true);
    Number right = Util.convertToNumber(ro, false, true);
    
    if ((left == null) || (right == null))
    {
      return set(-1, ExpressionUtils.toAttributed(null));
    }
    
    if (isFloatingPoint(left) || isFloatingPoint(right))
    {
      return set(-1, ExpressionUtils.toAttributed(left.doubleValue() * right.doubleValue(), lao, rao));
    }
    else
    {
      return set(-1, ExpressionUtils.toAttributed(left.longValue() * right.longValue(), lao, rao));
    }
  }
  
  @Override
  public AttributedObject visit(ASTDivNode node, EvaluationEnvironment data)
  {
    describeNode(data, "/");
    
    node.childrenAccept(this, data);
    
    AttributedObject lao = get(-1);
    AttributedObject rao = get(0);
    
    Object lo = ExpressionUtils.getValue(lao);
    Object ro = ExpressionUtils.getValue(rao);
    
    Number left = Util.convertToNumber(lo, false, true);
    Number right = Util.convertToNumber(ro, false, true);
    
    if ((left == null) || (right == null))
    {
      return set(-1, ExpressionUtils.toAttributed(null));
    }
    
    if (isFloatingPoint(left) || isFloatingPoint(right) || ((left.longValue() % right.longValue()) != 0))
    {
      return set(-1, ExpressionUtils.toAttributed(left.doubleValue() / right.doubleValue(), lao, rao));
    }
    else
    {
      return set(-1, ExpressionUtils.toAttributed(left.longValue() / right.longValue(), lao, rao));
    }
  }
  
  @Override
  public AttributedObject visit(ASTModNode node, EvaluationEnvironment data)
  {
    describeNode(data, "%");
    
    node.childrenAccept(this, data);
    
    AttributedObject lao = get(-1);
    AttributedObject rao = get(0);
    
    Object lo = ExpressionUtils.getValue(lao);
    Object ro = ExpressionUtils.getValue(rao);
    
    Number left = Util.convertToNumber(lo, false, true);
    Number right = Util.convertToNumber(ro, false, true);
    
    if ((left == null) || (right == null))
    {
      return set(-1, null);
    }
    
    return set(-1, ExpressionUtils.toAttributed(left.longValue() % right.longValue(), lao, rao));
  }
  
  @Override
  public AttributedObject visit(ASTUnaryNode node, EvaluationEnvironment data)
  {
    describeNode(data, "-X");
    
    node.childrenAccept(this, data);
    
    AttributedObject ao = get(0);
    
    Number val = Util.convertToNumber(ExpressionUtils.getValue(ao), false, true);
    
    if (val == null)
    {
      return set(0, ExpressionUtils.toAttributed(null));
    }
    
    if (isFloatingPoint(val))
    {
      return set(0, ExpressionUtils.toAttributed(-val.floatValue(), ao));
    }
    else
    {
      return set(0, ExpressionUtils.toAttributed(-val.longValue(), ao));
    }
  }
  
  @Override
  public AttributedObject visit(ASTLogicalNotNode node, EvaluationEnvironment data)
  {
    describeNode(data, "!");
    
    node.childrenAccept(this, data);
    
    AttributedObject ao = get(0);
    
    Boolean val = Util.convertToBoolean(ExpressionUtils.getValue(ao), true, true);
    
    return set(0, val != null ? ExpressionUtils.toAttributed(!val, ao) : null);
  }
  
  @Override
  public AttributedObject visit(ASTBitwiseNotNode node, EvaluationEnvironment data)
  {
    describeNode(data, "~");
    
    node.childrenAccept(this, data);
    
    AttributedObject ao = get(0);
    
    Number val = Util.convertToNumber(ExpressionUtils.getValue(ao), false, true);
    
    if (val == null)
    {
      return set(0, ExpressionUtils.toAttributed(null));
    }
    
    if (val instanceof Integer)
    {
      return set(0, ExpressionUtils.toAttributed(~val.intValue(), ao));
    }
    else
    {
      return set(0, ExpressionUtils.toAttributed(~val.longValue(), ao));
    }
  }
  
  @Override
  public AttributedObject visit(ASTFunctionNode node, EvaluationEnvironment data)
  {
    describeNode(data, "function: " + node.name);
    
    if (DefaultFunctions.CATCH.getName().equals(node.name))
    {
      int lastTop = top;
      try
      {
        return node.jjtGetChild(0).jjtAccept(this, data);
      }
      catch (Exception ex)
      {
        Evaluator.EvaluationStatistics.onErrorCatch();
        if (node.jjtGetNumChildren() > 1)
        {
          top = lastTop;
          return node.jjtGetChild(1).jjtAccept(this, data);
        }
        else
        {
          top = lastTop + 1;
          return set(0, ExpressionUtils.toAttributed(ex.getMessage() != null ? ex.getMessage() : ex.toString()));
        }
      }
    }
    
    Function fi = DEFAULT_FUNCTIONS.get(node.name);
    
    if (fi == null)
    {
      fi = evaluator.getCustomFunction(node.name);
    }
    
    if (fi == null)
    {
      throw new IllegalStateException(Cres.get().getString("exprUnknownFunction") + node.name);
    }
    
    node.childrenAccept(this, data);
    
    List<AttributedObject> parameters = new ArrayList<>();
    
    for (int i = 0; i < node.jjtGetNumChildren(); i++)
    {
      AttributedObject ao = get((i - node.jjtGetNumChildren()) + 1);
      
      parameters.add(ao);
    }
    
    try
    {
      AttributedObject[] pa = new AttributedObject[parameters.size()];

      data.obtainPinpoint().ifPresent(pinpoint -> {
        CallLocation callLocation = new CallLocation(
            CallKind.FUNCTION,
            node.jjtGetFirstToken().beginLine,
            node.jjtGetFirstToken().beginColumn,
            node.name);
        pinpoint.pushLocation(callLocation);
      });

      AttributedObject result = fi.executeAttributed(evaluator, data, parameters.toArray(pa));

      return set(1 - parameters.size(), result);
    }
    catch (Exception ex)
    {
      throw new IllegalStateException(MessageFormat.format(Cres.get().getString("exprErrExecutingFunction"), node.name) + ex.getMessage(), ex);
    }
    finally
    {
      data.obtainPinpoint().ifPresent(Pinpoint::popLocation);
    }
  }
  
  @Override
  public AttributedObject visit(ASTLongConstNode node, EvaluationEnvironment data)
  {
    describeNode(data, String.valueOf(node.val));
    
    return set(1, ExpressionUtils.toAttributed(node.val));
  }
  
  @Override
  public AttributedObject visit(ASTFloatConstNode node, EvaluationEnvironment data)
  {
    describeNode(data, String.valueOf(node.floatVal != null ? node.floatVal : node.doubleVal));
    
    if (node.floatVal != null)
    {
      return set(1, ExpressionUtils.toAttributed(node.floatVal));
    }
    else
    {
      return set(1, ExpressionUtils.toAttributed(node.doubleVal));
    }
  }
  
  @Override
  public AttributedObject visit(ASTStringConstNode node, EvaluationEnvironment data)
  {
    describeNode(data, node.val);
    
    return set(1, ExpressionUtils.toAttributed(node.val));
  }
  
  @Override
  public AttributedObject visit(ASTTrueNode node, EvaluationEnvironment data)
  {
    describeNode(data, "TRUE");
    
    return set(1, ExpressionUtils.toAttributed(Boolean.TRUE));
  }
  
  @Override
  public AttributedObject visit(ASTFalseNode node, EvaluationEnvironment data)
  {
    describeNode(data, "FALSE");
    
    return set(1, ExpressionUtils.toAttributed(Boolean.FALSE));
  }
  
  @Override
  public AttributedObject visit(ASTNullNode node, EvaluationEnvironment data)
  {
    describeNode(data, "NULL");
    
    return set(1, ExpressionUtils.toAttributed(null));
  }
  
  @Override
  public AttributedObject visit(ASTRightShiftNode node, EvaluationEnvironment data)
  {
    describeNode(data, ">>");
    
    node.childrenAccept(this, data);
    
    AttributedObject lao = get(-1);
    AttributedObject rao = get(0);
    
    Number left = Util.convertToNumber(ExpressionUtils.getValue(lao), false, true);
    Number right = Util.convertToNumber(ExpressionUtils.getValue(rao), false, true);
    
    if ((left == null) || (right == null))
    {
      return set(-1, ExpressionUtils.toAttributed(null));
    }
    
    if ((left instanceof Integer) && (right instanceof Integer))
    {
      return set(-1, ExpressionUtils.toAttributed(left.intValue() >> right.intValue(), lao, rao));
    }
    else
    {
      return set(-1, ExpressionUtils.toAttributed(left.longValue() >> right.longValue(), lao, rao));
    }
  }
  
  @Override
  public AttributedObject visit(ASTUnsignedRightShiftNode node, EvaluationEnvironment data)
  {
    describeNode(data, ">>>");
    
    node.childrenAccept(this, data);
    
    AttributedObject lao = get(-1);
    AttributedObject rao = get(0);
    
    Number left = Util.convertToNumber(ExpressionUtils.getValue(lao), false, true);
    Number right = Util.convertToNumber(ExpressionUtils.getValue(rao), false, true);
    
    if ((left == null) || (right == null))
    {
      return set(-1, ExpressionUtils.toAttributed(null));
    }
    
    if ((left instanceof Integer) && (right instanceof Integer))
    {
      return set(-1, ExpressionUtils.toAttributed(left.intValue() >>> right.intValue(), lao, rao));
    }
    else
    {
      return set(-1, ExpressionUtils.toAttributed(left.longValue() >>> right.longValue(), lao, rao));
    }
  }
  
  @Override
  public AttributedObject visit(ASTLeftShiftNode node, EvaluationEnvironment data)
  {
    describeNode(data, "<<");
    
    node.childrenAccept(this, data);
    
    AttributedObject lao = get(-1);
    AttributedObject rao = get(0);
    
    Number left = Util.convertToNumber(ExpressionUtils.getValue(lao), false, true);
    Number right = Util.convertToNumber(ExpressionUtils.getValue(rao), false, true);
    
    if ((left == null) || (right == null))
    {
      return set(-1, ExpressionUtils.toAttributed(null));
    }
    
    if ((left instanceof Integer) && (right instanceof Integer))
    {
      return set(-1, ExpressionUtils.toAttributed(left.intValue() << right.intValue(), lao, rao));
    }
    else
    {
      return set(-1, ExpressionUtils.toAttributed(left.longValue() << right.longValue(), lao, rao));
    }
  }
}
