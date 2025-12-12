package com.tibbo.aggregate.common.context;

import static com.google.common.base.Preconditions.checkState;

import java.util.Optional;

import javax.annotation.Nullable;

import com.tibbo.aggregate.common.expression.Evaluator;
import com.tibbo.aggregate.common.structure.Pinpoint;

public class DefaultRequestController implements RequestController
{
  private final Originator originator = new DefaultRequestOriginator();
  private Long lockTimeout;
  private Evaluator evaluator;
  private String queue;
  private boolean replyRequired = true;
  private boolean loggerRequest;
  @Nullable
  private Pinpoint pinpoint;
  
  public DefaultRequestController()
  {
  }
  
  public DefaultRequestController(boolean loggerRequest)
  {
    this.loggerRequest = loggerRequest;
  }
  
  public DefaultRequestController(Long lockTimeout)
  {
    this.lockTimeout = lockTimeout;
  }
  
  public DefaultRequestController(Object originatorObject)
  {
    this.originator.setOriginatorObject(originatorObject);
  }
  
  public DefaultRequestController(Evaluator evaluator)
  {
    this.evaluator = evaluator;
    if (evaluator != null) {
      loggerRequest = evaluator.isLoggerRequest();
    }
  }

  public DefaultRequestController(String queue)
  {
    this.queue = queue;
  }
  
  @Override
  public Originator getOriginator()
  {
    return originator;
  }
  
  @Override
  public Long getLockTimeout()
  {
    return lockTimeout;
  }
  
  @Override
  public Evaluator getEvaluator()
  {
    return evaluator;
  }
  
  public void setEvaluator(Evaluator evaluator)
  {
    this.evaluator = evaluator;
  }
  
  @Override
  public String getQueue()
  {
    return queue;
  }
  
  @Override
  public boolean isReplyRequired()
  {
    return replyRequired;
  }

  public void setReplyRequired(boolean replyRequired)
  {
    this.replyRequired = replyRequired;
  }

  @Override
  public boolean isLoggerRequest() {
    return loggerRequest;
  }

  @Override
  public void assignPinpoint(Pinpoint pinpoint) throws IllegalStateException
  {
    checkState(this.pinpoint == null, "This '%s' already contains pinpoint '%s' but " +
        "somebody attempted to assign another one: '%s'", this, this.pinpoint, pinpoint);
    this.pinpoint = pinpoint;
  }

  @Override
  public Optional<Pinpoint> obtainPinpoint()
  {
    return Optional.ofNullable(pinpoint);
  }
}
