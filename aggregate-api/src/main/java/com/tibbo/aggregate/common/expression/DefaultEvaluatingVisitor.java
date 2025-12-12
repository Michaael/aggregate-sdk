package com.tibbo.aggregate.common.expression;

import java.text.MessageFormat;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.expression.parser.ASTValueReferenceNode;
import com.tibbo.aggregate.common.structure.CallKind;
import com.tibbo.aggregate.common.structure.CallLocation;
import com.tibbo.aggregate.common.structure.Pinpoint;

public class DefaultEvaluatingVisitor extends AbstractEvaluatingVisitor
{
  public DefaultEvaluatingVisitor(Evaluator evaluator)
  {
    super(evaluator);
  }
  
  @Override
  public AttributedObject visit(ASTValueReferenceNode node, EvaluationEnvironment environment)
  {
    describeNode(environment, Expression.REFERENCE_START + node.uriImage + Expression.REFERENCE_END);
    
    if (node.reference == null)
    {
      node.reference = new Reference(node.uriImage);
    }
    
    Reference ref = node.reference;
    
    try
    {
      // First, check for resolver in the current environment (i.e. on per-expression basis)
      ReferenceResolver resolver = environment.getCustomResolvers().get(ref.getSchema());
      // Then, if no, check for it outside (i.e. on evaluator-wide level)
      if (resolver == null)
      {
        resolver = getEvaluator().getResolver(ref.getSchema());
      }

      if (resolver == null)
      {
        throw new IllegalStateException(Cres.get().getString("exprNoResolverForSchema") + ref.getSchema());
      }

      environment.obtainPinpoint().ifPresent(pinpoint -> {
        CallLocation callLocation = new CallLocation(
            CallKind.REFERENCE,
            node.jjtGetFirstToken().beginLine,
            node.jjtGetFirstToken().beginColumn,
            node.uriImage);
        pinpoint.pushLocation(callLocation);
      });

      Object referencedValue = resolver.resolveReference(ref, environment);
      return set(1, ExpressionUtils.toAttributed(referencedValue));
    }
    catch (Exception ex)
    {
      throw new IllegalStateException(MessageFormat.format(Cres.get().getString("exprErrResolvingReference"), ref) + ex.getMessage(), ex);
    }
    finally
    {
      environment.obtainPinpoint().ifPresent(Pinpoint::popLocation);
    }
  }
}
