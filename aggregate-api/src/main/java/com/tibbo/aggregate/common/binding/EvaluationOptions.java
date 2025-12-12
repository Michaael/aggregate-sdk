
package com.tibbo.aggregate.common.binding;

import static com.google.common.base.Preconditions.checkState;

import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nullable;

import com.tibbo.aggregate.common.expression.Evaluator;
import com.tibbo.aggregate.common.expression.Expression;
import com.tibbo.aggregate.common.expression.Reference;
import com.tibbo.aggregate.common.expression.ReferenceResolver;
import com.tibbo.aggregate.common.structure.Pinpoint;
import com.tibbo.aggregate.common.structure.PinpointAware;
import com.tibbo.aggregate.common.util.PublicCloneable;

public class EvaluationOptions implements Cloneable, PublicCloneable, PinpointAware
{
  public static final int STARTUP = 1;
  public static final int EVENT = 2;
  public static final int PERIODIC = 4;

  /**
   * The constant TEST is used to indicate the evaluation type in the context of testing.
   * The value TEST signifies that the evaluation is carried out solely for testing purposes and has no impact on the user interface (UI).
   * This is useful for distinguishing evaluation options performed within the testing framework.
   */
  public static final int TEST = 8;
  
  private int pattern;
  private long period = 0; // Milliseconds
  private Reference activator;
  private Expression condition;

  /**
   * A resolver for standard references that should be used instead of {@link Evaluator#getDefaultResolver()}
   * with a particular binding
   */
  @Nullable
  private ReferenceResolver customDefaultReferenceResolver;
  
  /**
   * An origin of the binding in the form of (possibly deep) reference, for example:
   * <pre><code>
   *   users.admin.dashboards.visDashboard:elements$parameters[0].condition[1]
   * </code></pre>
   * May be null if the origin is not known (yet).
   */
  @Nullable
  private Pinpoint pinpoint;
  
  public EvaluationOptions()
  {
    this(STARTUP);
  }
  
  public EvaluationOptions(boolean startup, boolean event)
  {
    this(startup, event, 0);
  }
  
  public EvaluationOptions(boolean startup, boolean event, long period)
  {
    super();
    if (startup)
    {
      pattern |= STARTUP;
    }
    if (event)
    {
      pattern |= EVENT;
    }
    if (period > 0)
    {
      pattern |= PERIODIC;
      this.period = period;
    }
  }
  
  public EvaluationOptions(int pattern)
  {
    super();
    this.pattern = pattern;
  }
  
  public EvaluationOptions(boolean startup, String activator, String condition)
  {
    this(startup, activator);
    this.condition = new Expression(condition);
  }
  
  public EvaluationOptions(boolean startup, String activator)
  {
    super();
    if (startup)
    {
      pattern |= STARTUP;
    }
    pattern |= EVENT;
    setActivator(new Reference(activator));
  }
  
  public int getPattern()
  {
    return pattern;
  }
  
  public long getPeriod()
  {
    return period;
  }
  
  public Reference getActivator()
  {
    return activator;
  }
  
  public Expression getCondition()
  {
    return condition;
  }
  
  public void setPattern(int pattern)
  {
    this.pattern = pattern;
  }
  
  public void setPeriod(long period)
  {
    this.period = period;
  }
  
  public void setActivator(Reference activator)
  {
    this.activator = activator;
  }
  
  public void setCondition(Expression condition)
  {
    this.condition = condition;
  }
  
  public boolean isProcessOnStartup()
  {
    return (pattern & STARTUP) > 0;
  }
  
  public boolean isProcessOnEvent()
  {
    return (pattern & EVENT) > 0;
  }
  
  public boolean isProcessPeriodically()
  {
    return (pattern & PERIODIC) > 0;
  }

  @Nullable
  public ReferenceResolver getCustomDefaultReferenceResolver() {
    return customDefaultReferenceResolver;
  }

  public void setCustomDefaultReferenceResolver(@Nullable ReferenceResolver customDefaultReferenceResolver)
  {
    this.customDefaultReferenceResolver = customDefaultReferenceResolver;
  }

  @Override
  public EvaluationOptions clone()
  {
    try
    {
      EvaluationOptions clone = (EvaluationOptions) super.clone();
      if (activator != null)
      {
        clone.activator = activator.clone();
      }
      if (condition != null)
      {
        clone.condition = condition.clone();
      }
      if (pinpoint != null)
      {
        clone.pinpoint = pinpoint.copy();
      }
      return clone;
    }
    catch (CloneNotSupportedException ex)
    {
      throw new IllegalStateException(ex.getMessage(), ex);
    }
  }

  @Override
  public boolean equals(Object o)
  {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    EvaluationOptions that = (EvaluationOptions) o;

    if (pattern != that.pattern) return false;
    if (period != that.period) return false;
    if (!Objects.equals(activator, that.activator)) return false;
    return Objects.equals(condition, that.condition);
  }

  @Override
  public int hashCode()
  {
    int result = pattern;
    result = 31 * result + (int) (period ^ (period >>> 32));
    result = 31 * result + (activator != null ? activator.hashCode() : 0);
    result = 31 * result + (condition != null ? condition.hashCode() : 0);
    return result;
  }

  @Override
  public String toString()
  {
    return (isProcessOnStartup() ? "1" : "0")
            + (isProcessOnEvent() ? "1" : "0")
            + (isProcessPeriodically() ? "1" : "0")
            + (isProcessPeriodically() ? ", period=" + period : "")
            + ", activator=" + activator
            + ", condition=" + condition
            + (customDefaultReferenceResolver != null ? (", resolver=" + customDefaultReferenceResolver) : "");
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
