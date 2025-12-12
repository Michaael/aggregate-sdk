package examples.api;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.tibbo.aggregate.common.action.ActionIdentifier;
import com.tibbo.aggregate.common.action.GenericActionCommand;
import com.tibbo.aggregate.common.action.GenericActionResponse;
import com.tibbo.aggregate.common.context.Context;
import com.tibbo.aggregate.common.context.ContextUtils;
import com.tibbo.aggregate.common.examples.AbstractTestExamples;

import org.junit.jupiter.api.Test;

public class ExecuteActionTest extends AbstractTestExamples
{
  @Test
  public void testDiscoveredAction() throws Exception
  {
    Context devicesContext = getContext(ContextUtils.devicesContextPath("admin"));
    assertNotNull(devicesContext);
    
    ActionIdentifier actionId = ExecuteAction.getActionIdentifier(devicesContext);
    
    GenericActionResponse actionResponse = getResponse(devicesContext, actionId);
    assertNotNull(actionResponse);
    assertNotNull(actionResponse.getRequestId());
  }
  
  private GenericActionResponse getResponse(Context devicesContext, ActionIdentifier actionId) throws Exception
  {
    GenericActionResponse actionResponse = null;
    
    while (true)
    {
      GenericActionCommand cmd = ExecuteAction.getGenericActionCommand(devicesContext, actionId, actionResponse);
      
      actionResponse = ExecuteAction.processCommand(cmd);
      
      actionResponse.setRequestId(cmd != null ? cmd.getRequestId() : null);
      
      if (actionResponse != null)
      {
        break;
      }
    }
    return actionResponse;
  }
  
}
