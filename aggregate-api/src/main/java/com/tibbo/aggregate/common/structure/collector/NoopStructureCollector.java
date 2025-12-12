package com.tibbo.aggregate.common.structure.collector;

import com.google.common.annotations.VisibleForTesting;
import com.tibbo.aggregate.common.context.ContextOperationType;
import com.tibbo.aggregate.common.structure.Pinpoint;

/**
 * A stub for {@link ApplicationStructureCollector} suitable for cases when no collector is available or when there
 * was an error while loading it
 *
 * @since AGG-10879
 * @author Vladimir Plizga
 */
class NoopStructureCollector implements ApplicationStructureCollector
{
  @VisibleForTesting
  static final NoopStructureCollector INSTANCE = new NoopStructureCollector();
  
  @Override
  public void recordInteraction(Pinpoint source, Pinpoint target, ContextOperationType type)
  {
    // do nothing to suppress any output
  }
  
  private NoopStructureCollector()
  {
  }
}
