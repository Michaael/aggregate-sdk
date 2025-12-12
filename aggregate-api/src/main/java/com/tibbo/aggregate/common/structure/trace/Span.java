package com.tibbo.aggregate.common.structure.trace;

import com.tibbo.aggregate.common.structure.Pinpoint;

/**
 * Span is an act of inter-context communication within AggreGate low-code application
 *
 * @author Vladimir Plizga
 * @since AGG-10879
 */
public class Span
{
  private final Pinpoint source;
  private final Pinpoint target;

  public Span(Pinpoint source, Pinpoint target)
  {
    this.source = source;
    this.target = target;
  }

  public Pinpoint getSource()
  {
    return source;
  }

  public Pinpoint getTarget()
  {
    return target;
  }
}
