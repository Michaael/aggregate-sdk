package com.tibbo.aggregate.common.expression;

import static com.google.common.base.Preconditions.checkState;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Nullable;

import com.tibbo.aggregate.common.expression.parser.NodeEvaluationDetails;
import com.tibbo.aggregate.common.structure.Pinpoint;
import com.tibbo.aggregate.common.structure.PinpointAware;

public class EvaluationEnvironment implements Cloneable, PinpointAware
{
  private Reference cause;
  private Map<String, Object> environment;
  @Nullable
  private Pinpoint pinpoint;
  /**
   * A set of {@linkplain ReferenceResolver}s that can be used instead of evaluator-wide ones (i.e. on a
   * per-expression basis).<p/>
   * The format of the map is exactly the same as of the {@link Evaluator#getResolvers()}: the key is the reference
   * schema and the value is corresponding reference resolver. The special key {@link Evaluator#DEFAULT_RESOLVER_KEY}
   * ({@code null}) is reserved for default resolver which is responsible for resolving
   * <a href="https://aggregate.digital/docs/en/ls_internals_references_standard.htm">standard references</a>.
   */
  private final Map<String, ReferenceResolver> customResolvers = new HashMap<>();

  private final ThreadLocal<Boolean> debug = new ThreadLocal();
  private final ThreadLocal<NodeEvaluationDetails> rootNode = new ThreadLocal();
  private final ThreadLocal<NodeEvaluationDetails> activeNode = new ThreadLocal();

  public EvaluationEnvironment()
  {
  }

  public EvaluationEnvironment(ReferenceResolver customDefaultReferenceResolver)
  {
    customResolvers.put(Evaluator.DEFAULT_RESOLVER_KEY, customDefaultReferenceResolver);
  }
  
  public EvaluationEnvironment(Reference cause)
  {
    this.cause = cause;
  }
  
  public EvaluationEnvironment(Map<String, Object> environment)
  {
    this.environment = environment;
  }
  
  public EvaluationEnvironment(Reference cause, Map<String, Object> environment)
  {
    this.cause = cause;
    this.environment = environment;
  }

  public Reference getCause()
  {
    return cause;
  }
  
  public void setCause(Reference cause)
  {
    this.cause = cause;
  }
  
  public Map<String, Object> getEnvironment()
  {
    if (environment == null)
    {
      environment = new HashMap();
    }
    
    return environment;
  }
  
  public void setEnvironment(Map<String, Object> environment)
  {
    this.environment = environment;
  }
  
  @Override
  public EvaluationEnvironment clone()
  {
    try
    {
      EvaluationEnvironment cloneEnv = (EvaluationEnvironment) super.clone();
      if (cause != null)
      {
        cloneEnv.cause = cause.clone();
      }
      if (pinpoint != null)
      {
        cloneEnv.pinpoint = pinpoint.copy();
      }
      return cloneEnv;
    }
    catch (CloneNotSupportedException e)
    {
      throw new RuntimeException(e);
    }
  }
  
  public void setActiveNode(NodeEvaluationDetails ed)
  {
    if (rootNode.get() == null)
    {
      rootNode.set(ed);
    }
    
    activeNode.set(ed);
  }
  
  public NodeEvaluationDetails getRootNode()
  {
    return rootNode.get();
  }
  
  public NodeEvaluationDetails getActiveNode()
  {
    return activeNode.get();
  }
  
  public void setDebug(boolean debug)
  {
    this.debug.set(debug);
  }
  
  public boolean isDebug()
  {
    Boolean isDebug = debug.get();
    
    return isDebug != null && isDebug;
  }

  public Map<String, ReferenceResolver> getCustomResolvers()
  {
    return customResolvers;
  }

  /**
   * Adds a new custom default reference resolver for using within this particular environment
   * @implNote The method is a part of customResolvers usage contract; can be used or modified as needed.
   */
  public void addCustomDefaultResolver(ReferenceResolver resolver)
  {
    customResolvers.put(Evaluator.DEFAULT_RESOLVER_KEY, resolver); // may re-write the previous one, and it's OK so far
  }

  @Override
  public void assignPinpoint(Pinpoint pinpoint)
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

  @Override
  public void removePinpoint()
  {
    // Since environment can be re-used between different evaluations, it might be needed to reset the pinpoint
    pinpoint = null;
  }
}
