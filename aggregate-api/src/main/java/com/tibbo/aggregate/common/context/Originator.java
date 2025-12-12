package com.tibbo.aggregate.common.context;

public interface Originator
{
  int TYPE_UNDEFINED = 0;
  int TYPE_BINDING_PROVIDER = 1;
  
  Object getOriginatorObject();
  
  void setOriginatorObject(Object originatorObject);
  
  int getOriginatorType();
  
  void setOriginatorType(int originatorType);
}
