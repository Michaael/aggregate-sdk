package com.tibbo.aggregate.common.device;

import com.tibbo.aggregate.common.context.*;
import com.tibbo.aggregate.common.datatable.*;

/**
 * Simple way to define service functions.
 * For example may be used as
 *   Map<String, ServiceFunction> functionsByName; 
 */
public interface ServiceFunction
{
  FunctionDefinition getFunctionDefinition();

  DataTable execute(DataTable parametersDataTable) throws DeviceException;
}
