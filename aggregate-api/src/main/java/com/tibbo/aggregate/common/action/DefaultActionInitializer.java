package com.tibbo.aggregate.common.action;

import java.util.*;

import com.tibbo.aggregate.common.*;
import com.tibbo.aggregate.common.context.*;
import com.tibbo.aggregate.common.datatable.*;
import com.tibbo.aggregate.common.server.*;
import com.tibbo.aggregate.common.util.*;

public class DefaultActionInitializer implements ActionInitializer
{
  @Override
  public ActionIdentifier initAction(Context context, String actionName, ServerActionInput initialParametrs, DataTable inputData, Map<String, Object> environment, ActionExecutionMode mode,
      CallerController callerController, ErrorCollector collector) throws ContextException
  {
    FunctionDefinition def = context.getFunctionDefinition(ServerContextConstants.F_INIT_ACTION);
    
    if (def == null)
    {
      throw new IllegalStateException(Cres.get().getString("conActNotAvail") + ServerContextConstants.F_INIT_ACTION + " (" + context.toDetailedString() + ")");
    }
    
    DataRecord rec = new DataRecord(def.getInputFormat());
    rec.addValue(actionName);
    rec.addValue(initialParametrs.getData());
    rec.addValue(inputData);
    if (def.getInputFormat().getFieldCount() > 3)
    {
      // Parameter introduced in AggreGate v5
      rec.addValue(mode.getCode());
    }
    
    DataTable res = context.callFunction(ServerContextConstants.F_INIT_ACTION, callerController, rec.wrap());
    
    return new ActionIdentifier(res.rec().getString(ServerContextConstants.FOF_INIT_ACTION_ACTION_ID));
  }
  
}
