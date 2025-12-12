package com.tibbo.aggregate.common.context;

import java.util.LinkedList;
import java.util.concurrent.Callable;

public interface ContextVisitor<T extends Context>
{
  boolean isConcurrent();
  
  boolean shouldVisit(T context) throws ContextException;
  
  void visit(T context) throws ContextException;
  
  LinkedList<Callable<Object>> getTasks();
  
  boolean isStartContext();

  /**
   * @return <code>true</code> if this visitor should traverse context in forward order, i.e. first visit the
   * children list, then process the current node; otherwise visitor first processes the current node and then visits
   * the children. The forward order is usually preferable but can be unsuitable for example when visitor
   * structurally modifies the subtree during traversal.
   */
  default boolean isCurrentThenChildrenOrder() {
    return true;
  };
}
