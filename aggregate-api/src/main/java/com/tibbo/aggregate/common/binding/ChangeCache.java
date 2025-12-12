package com.tibbo.aggregate.common.binding;

import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CountDownLatch;

import com.tibbo.aggregate.common.Log;
import com.tibbo.aggregate.common.context.CallerController;
import com.tibbo.aggregate.common.context.Context;
import com.tibbo.aggregate.common.context.ContextException;
import com.tibbo.aggregate.common.context.ContextUtils;
import com.tibbo.aggregate.common.context.DefaultRequestController;
import com.tibbo.aggregate.common.context.RequestController;
import com.tibbo.aggregate.common.datatable.DataTable;

public class ChangeCache
{
  private final Map<Context, Map<String, DataTable>> variableChanges = new Hashtable();
  /**
   * Almost the same as {@code variableChanges} but for RequestController to pick the right one for observability
   * upon committing
   */
  private final Map<Context, Map<String, RequestController>> requests = new HashMap<>();
  private CountDownLatch cdl;
  
  public ChangeCache(int lockCounter)
  {
    cdl = new CountDownLatch(lockCounter);
    Log.BINDINGS.debug("ChangeCache created with counter " + lockCounter);
  }
  
  public ChangeCache()
  {
  }
  
  public void setVariableField(Context context, String variable, String field, int record, Object value, CallerController cc,
      DefaultRequestController request) throws ContextException
  {
    Map<String, DataTable> changes = variableChanges.computeIfAbsent(context, k -> new Hashtable());

    DataTable table = changes.get(variable);
    
    if (table == null)
    {
      table = context.getVariableClone(variable, cc);
      changes.put(variable, table);
    }
    
    table.getRecord(record).setValue(field, value);
    table.setTimestamp(new Date());

    requests.computeIfAbsent(context, k -> new HashMap<>())
                      .put(variable, request);
  }
  
  public void commit(BindingProvider provider, CallerControllerSelector selector)
  {
    for (Entry<Context, Map<String, DataTable>> conEntry : variableChanges.entrySet())
    {
      for (Entry<String, DataTable> varEntry : conEntry.getValue().entrySet())
      {
        Context context = conEntry.getKey();
        String variableName = varEntry.getKey();
        try
        {
          CallerController cc = selector.select(context, variableName, ContextUtils.ENTITY_VARIABLE);

          RequestController request = requests.get(context)
                                              .get(variableName);

          DataTable variableValue = varEntry.getValue();
          context.setVariable(variableName, cc, request, variableValue);
        }
        catch (Exception ex)
        {
          provider.processError(null, EvaluationOptions.EVENT, null, ex);
        }
      }
    }
  }
  
  public void await()
  {
    try
    {
      if (cdl != null)
      {
        Log.BINDINGS.debug("ChangeCache await for lock counter");
        cdl.await();
      }
    }
    catch (InterruptedException ex)
    {
      Log.BINDINGS.warn(ex.getMessage(), ex);
    }
  }
  
  public void countDown()
  {
    if (cdl != null)
    {
      Log.BINDINGS.debug("ChangeCache count down");
      cdl.countDown();
    }
  }
}
