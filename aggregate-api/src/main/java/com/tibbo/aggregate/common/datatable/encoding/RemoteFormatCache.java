package com.tibbo.aggregate.common.datatable.encoding;

import com.tibbo.aggregate.common.Log;
import com.tibbo.aggregate.common.context.Context;
import com.tibbo.aggregate.common.context.Contexts;
import com.tibbo.aggregate.common.datatable.DataRecord;
import com.tibbo.aggregate.common.datatable.DataTable;
import com.tibbo.aggregate.common.datatable.TableFormat;
import com.tibbo.aggregate.common.protocol.AbstractAggreGateDeviceController;
import com.tibbo.aggregate.common.protocol.RemoteContextManager;
import com.tibbo.aggregate.common.server.CommonServerFormats;
import com.tibbo.aggregate.common.server.RootContextConstants;


/*
 * Format cache that contains format from another server. Formats should already have their indexes. Adding formats without indexes defined is not supported.
 *
 **/
public class RemoteFormatCache extends AbstractFormatCache
{
  private final AbstractAggreGateDeviceController controller;
  
  public RemoteFormatCache(AbstractAggreGateDeviceController controller, String name)
  {
    super(name);
    this.controller = controller;
  }
  
  public TableFormat get(int id)
  {
    TableFormat result = super.get(id);
    if (result != null)
    {
      return result;
    }
    if (controller == null)
    {
      throw new IllegalStateException("Format requesting is not available since controller is null");
    }
    else
    {
      try
      {
        if (Log.PROTOCOL_CACHING.isDebugEnabled())
        {
          Log.PROTOCOL_CACHING.debug("Requesting remote format #" + id);
        }
        
        final DataTable output;
        
        RemoteContextManager cm = controller.getContextManager();
        
        Context rootContext = cm != null ? cm.get(Contexts.CTX_ROOT, cm.getCallerController()) : null;

        if (rootContext != null && rootContext.getFunctionDefinition(RootContextConstants.F_GET_FORMAT) != null)
        {
          output = rootContext.callFunction(RootContextConstants.F_GET_FORMAT, id);
        }
        else
        {
          final DataTable input = new DataRecord(CommonServerFormats.FIFT_GET_FORMAT, id).wrap();
          output = controller.callRemoteFunction(Contexts.CTX_ROOT, RootContextConstants.F_GET_FORMAT, CommonServerFormats.FOFT_GET_FORMAT, input, null);
        }
        
        String formatData = output.rec().getString(RootContextConstants.FOF_GET_FORMAT_DATA);
        result = new TableFormat(formatData, new ClassicEncodingSettings(false));
        
        if (Log.PROTOCOL_CACHING.isDebugEnabled())
        {
          Log.PROTOCOL_CACHING.debug("Received explicitly requested remote format #" + id + ": " + result);
        }
        
        cacheLock.writeLock().lock();
        try
        {
          addImpl(result, id);
        }
        finally
        {
          cacheLock.writeLock().unlock();
        }
      }
      catch (Exception ex)
      {
        throw new IllegalStateException("Error obtaining format #" + id, ex);
      }
    }
    return result;
  }
  
  @Override
  protected Integer add(TableFormat tableFormat)
  {
    throw new UnsupportedOperationException("Cannot add format: '" + tableFormat + "' without defined index to the remote cache");
  }
}
