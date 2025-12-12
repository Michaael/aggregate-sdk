package com.tibbo.aggregate.common.event;

import com.tibbo.aggregate.common.context.*;
import com.tibbo.aggregate.common.data.*;
import com.tibbo.aggregate.common.datatable.*;
import com.tibbo.aggregate.common.util.*;

public interface EventsFilter
{
  public boolean pass(Event ev, CallerController caller, ContextManager contextManager, boolean throwErrors) throws SyntaxErrorException, DataTableException;
}