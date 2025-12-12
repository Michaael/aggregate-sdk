package com.tibbo.aggregate.common.structure.collector;

import com.tibbo.aggregate.common.context.ContextOperationType;
import com.tibbo.aggregate.common.structure.Pinpoint;

/**
 * A component interface responsible for recording interactions between contexts of Unified Data Model
 *
 * @since AGG-10879
 * @author Vladimir Plizga
 */
public interface ApplicationStructureCollector
{

  void recordInteraction(Pinpoint source, Pinpoint target, ContextOperationType type);
}
