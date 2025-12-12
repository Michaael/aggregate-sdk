package com.tibbo.aggregate.common.structure;

/**
 * Classification of origins of cross-context interactions (as distinguished by the observability logic). Note that
 * a reference can also reside inside an expression but from the origin point of view it is considered expression,
 * not reference.
 *
 * @author Vladimir Plizga
 * @since AGG-10879
 */
public enum OriginKind
{
  REFERENCE,
  EXPRESSION
}
