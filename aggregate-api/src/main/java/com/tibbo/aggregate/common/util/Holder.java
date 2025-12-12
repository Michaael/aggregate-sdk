package com.tibbo.aggregate.common.util;

public class Holder<T>
{
  private T value;
  
  public Holder()
  {
    super();
  }
  
  public Holder(T value)
  {
    this();
    this.value = value;
  }
  
  public void put(T value)
  {
    this.value = value;
  }
  
  public T get()
  {
    return value;
  }

  public boolean isEmpty()
  {
    return (value == null);
  }
  
}
