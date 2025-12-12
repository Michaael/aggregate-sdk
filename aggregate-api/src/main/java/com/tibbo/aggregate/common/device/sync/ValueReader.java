package com.tibbo.aggregate.common.device.sync;

import com.tibbo.aggregate.common.context.*;
import com.tibbo.aggregate.common.datatable.*;
import com.tibbo.aggregate.common.device.*;

public interface ValueReader
{
  DataTable read(CallerController callerController, RequestController requestController) throws ContextException, DeviceException, DisconnectionException;
}
