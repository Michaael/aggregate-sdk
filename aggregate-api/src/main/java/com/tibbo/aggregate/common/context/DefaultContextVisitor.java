package com.tibbo.aggregate.common.context;

import java.util.LinkedList;
import java.util.concurrent.Callable;

public abstract class DefaultContextVisitor<T extends Context> implements ContextVisitor<T>
{
  private final boolean concurrent;

  private final boolean visitThenAcceptOrder;

  public LinkedList<Callable<Object>> tasks = new LinkedList<>();

  private boolean startContext = true;

  public DefaultContextVisitor()
  {
    this(false);
  }

  public DefaultContextVisitor(boolean concurrent)
  {
    this(concurrent, true);
  }

  public DefaultContextVisitor(boolean concurrent, boolean visitThenAcceptOrder)
  {
    this.concurrent = concurrent;
    this.visitThenAcceptOrder = visitThenAcceptOrder;
  }
  
  @Override
  public boolean isConcurrent()
  {
    return concurrent;
  }

  @Override
  public boolean isCurrentThenChildrenOrder()
  {
    return visitThenAcceptOrder;
  }

  @Override
  public boolean shouldVisit(T context) throws ContextException
  {
    return !context.isProxy();
  }
  
  @Override
  public LinkedList<Callable<Object>> getTasks()
  {
    return tasks;
  }
  
  @Override
  public boolean isStartContext()
  {
    boolean res = startContext;
    startContext = false;
    return res;
  }
  
}
