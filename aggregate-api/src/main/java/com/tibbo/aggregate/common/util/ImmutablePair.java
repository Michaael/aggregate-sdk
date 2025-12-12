package com.tibbo.aggregate.common.util;

import java.util.*;

public final class ImmutablePair<F, S>
{
  private final F first;
  private final S second;
  
  public ImmutablePair(F first, S second)
  {
    this.first = first;
    this.second = second;
  }
  
  public F getFirst()
  {
    return first;
  }
  
  public S getSecond()
  {
    return second;
  }
  
  @Override
  public String toString()
  {
    return "[" + first + " & " + second + "]";
  }
  
  @Override
  public boolean equals(Object o)
  {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    ImmutablePair<?, ?> that = (ImmutablePair<?, ?>) o;
    return Objects.equals(first, that.first) &&
        Objects.equals(second, that.second);
  }
  
  @Override
  public int hashCode()
  {
    return Objects.hash(first, second);
  }
}
