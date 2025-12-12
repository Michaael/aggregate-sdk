package com.tibbo.aggregate.common.context;

public class DefaultRequestOriginator implements Originator
{
  private Object originatorObject;
  private int originatorType = TYPE_UNDEFINED;
  
  public DefaultRequestOriginator()
  {
  }
  
  public DefaultRequestOriginator(Object originatorObject)
  {
    this.originatorObject = originatorObject;
  }
  
  public DefaultRequestOriginator(int originatorType)
  {
    this.originatorType = originatorType;
  }
  
  public DefaultRequestOriginator(Object originatorObject, int originatorType)
  {
    this.originatorObject = originatorObject;
    this.originatorType = originatorType;
  }
  
  @Override
  public Object getOriginatorObject()
  {
    return originatorObject;
  }
  
  @Override
  public void setOriginatorObject(Object originatorObject)
  {
    this.originatorObject = originatorObject;
  }
  
  @Override
  public int getOriginatorType()
  {
    return originatorType;
  }
  
  @Override
  public void setOriginatorType(int originatorType)
  {
    this.originatorType = originatorType;
  }
  
}
