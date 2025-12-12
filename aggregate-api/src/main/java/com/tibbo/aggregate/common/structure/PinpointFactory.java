package com.tibbo.aggregate.common.structure;

import com.tibbo.aggregate.common.context.ContextUtils;
import com.tibbo.aggregate.common.expression.Reference;

/**
 * This factory is a facade for various pinpoint constructors aimed at hiding implementation details from developers
 * using it around the platform. <br/>
 * To make the using code smaller, leverage static import on this class, i.e. instead of
 * <code><pre>PinpointFactory.newPinpointFor(...)</pre></code> write just <code><pre>newPinpointFor(...)</pre></code>
 *
 * @author Vladimir Plizga
 * @since AGG-10879
 */
public class PinpointFactory
{

  // this inaccurate pinpoint should be either detailed further or used only where others are not applicable
  public static Pinpoint newPinpointFor(String contextPath)
  {
    Reference contextReference = new Reference();
    contextReference.setContext(contextPath);
    return new Pinpoint(contextReference);
  }

  public static Pinpoint newPinpointFor(String contextPath, String variableName)
  {
    return new Pinpoint(new Reference(contextPath, variableName, ContextUtils.ENTITY_VARIABLE));
  }

  public static Pinpoint newPinpointFor(String schema, String contextPath, String variableName)
  {
    Reference origin = new Reference(contextPath, variableName, ContextUtils.ENTITY_VARIABLE);
    origin.setSchema(schema);
    return new Pinpoint(origin);
  }

  public static Pinpoint newPinpointFor(String schema, String scope, String contextPath, String variableName)
  {
    Reference origin = new Reference(contextPath, variableName, ContextUtils.ENTITY_VARIABLE);
    origin.setSchema(schema);
    return new Pinpoint(origin, scope);
  }

  private PinpointFactory() { /* not instantiable */ }
}
