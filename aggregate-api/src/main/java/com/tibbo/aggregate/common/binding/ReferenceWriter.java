package com.tibbo.aggregate.common.binding;

import com.tibbo.aggregate.common.expression.*;

public interface ReferenceWriter
{
  void writeReference(Reference ref, Object value) throws BindingException;
}
