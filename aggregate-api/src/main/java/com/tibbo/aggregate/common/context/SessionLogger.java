package com.tibbo.aggregate.common.context;

import java.util.*;
import java.util.concurrent.*;

import com.tibbo.aggregate.common.datatable.*;

public interface SessionLogger
{
  Future<Object> logAsync(CallerController caller, Context context, DataRecord sessionLoggerData,
      Map<ContextSessionLoggerParams, Object> params) throws ContextException;
}
