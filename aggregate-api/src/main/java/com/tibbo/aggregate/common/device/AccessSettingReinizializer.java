package com.tibbo.aggregate.common.device;

import com.tibbo.aggregate.common.context.CallerController;
import com.tibbo.aggregate.common.context.ContextException;
import com.tibbo.aggregate.common.context.VariableDefinition;
import com.tibbo.aggregate.common.datatable.DataTable;
import com.tibbo.aggregate.common.device.sync.SynchronizationParameters;

public interface AccessSettingReinizializer
{
  /**
   * This method is called when the value of the device access setting variable is changed.
   * 
   * @return True if reconnection to the device and its resynchronization is necessary, false otherwise
   */
  SynchronizationParameters reinitialize(VariableDefinition def, CallerController caller, DataTable value) throws ContextException;
}
