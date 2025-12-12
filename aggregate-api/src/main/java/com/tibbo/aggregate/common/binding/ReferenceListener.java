package com.tibbo.aggregate.common.binding;

import java.util.*;

import com.tibbo.aggregate.common.expression.*;

public interface ReferenceListener<T>
{
  void referenceChanged(Reference cause, Map<String, Object> environment, ChangeCache cache) throws BindingException;
  
  void referenceChanged(Reference cause, Map<String, Object> environment, ChangeCache cache, boolean asynchronousProcessing) throws BindingException;
  
  BindingProcessor getBindingProcessor();
  
  Binding getBinding();
  
  EvaluationOptions getEvaluationOptions();
  
  void setContent(T content);
  
  T getContent();
}
