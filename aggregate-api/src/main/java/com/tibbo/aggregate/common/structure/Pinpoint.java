package com.tibbo.aggregate.common.structure;

import java.util.Deque;
import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedDeque;

import javax.annotation.Nullable;

import com.google.common.annotations.VisibleForTesting;
import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.expression.Reference;

/**
 * Pinpoint is a place within Unified Data Model that precisely describes the outgoing coordinates of inter-context
 * communication. Think of it as a descriptor used to identify where a traceable object (like expression or reference)
 * comes from.
 * <p/>
 * The pinpoint may be created with just a rough "coordinates" initially (like only context and variable)
 * but then be supplemented with details (like particular field and column of the variable).
 * <p/>
 * To create Pinpoint instances, corresponding {@linkplain PinpointFactory factory methods} should be used.
 * <p/>
 * <em>Note</em> that all the {@code with…} methods return <em>new</em> instances rather than modify the current
 * one. This is to enable having different versions of the same initial pinpoint in different places of a context.
 *
 * @author Vladimir Plizga
 * @since AGG-10879
 */
public class Pinpoint
{
  private final Reference origin;
  @Nullable
  private String scope = null;            // null means that the scope is not (yet) known
  @Nullable
  private OriginKind originKind = null;   // null means that the kind is not (yet) known
  private final Deque<CallLocation> callLocations = new ConcurrentLinkedDeque<>();

  Pinpoint(Reference origin)
  {
    this.origin = origin;
  }

  Pinpoint(Reference origin, @Nullable String scope)
  {
    this.origin = origin;
    this.scope = scope;
  }

  private Pinpoint(Pinpoint toCopy)         // for internal use only
  {
    this.origin = toCopy.origin.clone();
    this.callLocations.addAll(toCopy.callLocations);
    this.originKind = toCopy.originKind;
    this.scope = toCopy.scope;
  }

  public Pinpoint copy()
  {
    return new Pinpoint(this);
  }

  public Pinpoint withOriginRow(int row)
  {
    Pinpoint newPinpoint = new Pinpoint(this);
    newPinpoint.origin.setRow(row);
    return newPinpoint;
  }

  public Pinpoint withOriginField(String field)
  {
    return withOriginField(field, null);
  }

  public Pinpoint withOriginField(String field, @Nullable OriginKind kind)
  {
    Pinpoint newPinpoint = new Pinpoint(this);
    newPinpoint.origin.setField(field);
    newPinpoint.originKind = kind;
    return newPinpoint;
  }

  public Pinpoint withScope(String scope)
  {
    Pinpoint newPinpoint = new Pinpoint(this);
    newPinpoint.scope = scope;
    return newPinpoint;
  }

  public Pinpoint withNestedCell(String nestedField, int nestedRow, OriginKind kind)
  {
    Pinpoint newPinpoint = new Pinpoint(this);
    newPinpoint.origin.addField(nestedField, nestedRow);
    newPinpoint.originKind = kind;
    return newPinpoint;
  }

  public void pushLocation(CallLocation location)
  {
    callLocations.push(location);
  }

  public void popLocation()
  {
    callLocations.pop();
  }

  public Reference getOrigin() {
    return origin;
  }

  @Nullable
  public String getScope()
  {
    return scope;
  }

  public String asString()
  {
    StringBuilder sb = new StringBuilder();
    if (originKind == OriginKind.REFERENCE)
    {
      sb.append(Cres.get().getString("reference").toLowerCase());
    }
    else    // then this is the expression kind of origin
    {
      int i = 0;
      for (CallLocation call : callLocations)
      {
        sb.append(Cres.get().getString("expression").toLowerCase())
            .append(" '").append(call.getCallText()).append("' ")
            .append(Cres.get().getString("structAtLine")).append(' ').append(call.getLine()).append(", ")
            .append(Cres.get().getString("column")).append(' ').append(call.getColumn())
            .append(' ').append(Cres.get().getString("structOfExpression"));
        if (++i < callLocations.size())
        {
          sb.append(' ').append(Cres.get().getString("structResidingIn"));
        }
      }
    }

    if (sb.length() > 0)
    {
      sb.append(' ').append(Cres.get().getString("structOriginatedFrom")).append(' ');
    }

    sb.append('\'').append(origin.getImage()).append('\'');
    if (scope != null && !scope.isEmpty())
    {
      sb.append(" (").append(Cres.get().getString("structScope")).append(": '").append(scope).append("')");
    }
    return sb.toString();
  }

  @Override
  public String toString()
  {
    return asString();
  }

  @Nullable
  @VisibleForTesting
  OriginKind getOriginKind()
  {
    return originKind;
  }

  @VisibleForTesting
  Deque<CallLocation> getCallLocations()
  {
    return callLocations;
  }

  @Override
  public boolean equals(Object o)
  {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;

    Pinpoint pinpoint = (Pinpoint) o;

    if (!origin.equals(pinpoint.origin))
      return false;
    if (!Objects.equals(scope, pinpoint.scope))
      return false;
    if (originKind != pinpoint.originKind)
      return false;

    Iterator<CallLocation> thisIterator = callLocations.iterator();
    for (CallLocation otherCallLocation : pinpoint.callLocations)
    {
      if (!otherCallLocation.equals(thisIterator.next()))
      {
        return false;
      }
    }
    return true;
  }

  @Override
  public int hashCode()
  {
    int result = origin.hashCode();
    result = 31 * result + (scope != null ? scope.hashCode() : 0);
    result = 31 * result + (originKind != null ? originKind.hashCode() : 0);
    result = 31 * result + callLocations.hashCode();
    return result;
  }
}
