package examples.api;

import com.tibbo.aggregate.common.*;
import com.tibbo.aggregate.common.action.*;
import com.tibbo.aggregate.common.action.command.*;
import com.tibbo.aggregate.common.context.*;
import com.tibbo.aggregate.common.datatable.*;
import com.tibbo.aggregate.common.protocol.*;

/**
 * This advanced example shows how to execute a server context action by simulating operator input.
 */
public class ExecuteAction
{
  public static void main(String[] args)
  {
    try
    {
      // Enabling logging
      Log.start();
      
      // Provide correct server address/port and name/password of server user to log in as
      // This user must have "admin" permission level for the "users" context and its children for the successful execution of this example
      RemoteServer rls = new RemoteServer("localhost", RemoteServer.DEFAULT_PORT, "admin", "admin");
      
      // Creating server controller
      RemoteServerController rlc = new RemoteServerController(rls, true);
      
      // Connecting to the server
      rlc.connect();
      
      // Authentication/authorization
      rlc.login();
      
      // Getting context manager
      ContextManager cm = rlc.getContextManager();
      
      // Execute the "Network Devices Discovery" action
      executeDiscoveryAction(cm);
      
      // Disconnecting from the server
      rlc.disconnect();
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
  }
  
  private static void executeDiscoveryAction(ContextManager cm) throws ContextException
  {
    // Retrieving context to execute action from
    Context devicesContext = cm.get(ContextUtils.devicesContextPath("admin"));
    
    // Initializing "discovery" action (no initial parameters are specified)
    ActionIdentifier actionId = getActionIdentifier(devicesContext);
    
    // Initial response sent at first step should be NULL
    GenericActionResponse actionResponse = null;
    
    // Looping until we get NULL or "last" response
    while (true)
    {
      // Performing action step and getting next UI procedure to execute (i.e. emulate)
      GenericActionCommand cmd = getGenericActionCommand(devicesContext, actionId, actionResponse);
      
      if (cmd == null)
      {
        break; // End of action
      }
      
      // Emulating UI procedure execution
      actionResponse = processCommand(cmd);
      
      if (cmd.isLast())
      {
        break; // End of action
      }
      
      // Replicating request ID to the reply
      actionResponse.setRequestId(cmd.getRequestId());
    }
  }
  
  /**
   * This method emulates execution of UI procedures (action commands) by a human. It must "understand" the action it executes and know all action commands that an action may issue. The normal flow of
   * "discovery" action issues only "Edit Data" commands, so we just fail if command of any other type was received.
   * 
   * When received an "Edit Data" command, we reply by sending the same data. This emulated a simple click on "OK" button, i.e. when data was not edited. However, some requests with "known" IDs are
   * filled with additional data.
   * 
   * @param cmd
   *          UI procedure to process
   * @return Action response
   */
  public static GenericActionResponse processCommand(GenericActionCommand cmd)
  {
    // Logging all action commands to analyze action flow
    Log.CORE.info("Received action command: " + cmd);
    
    // Extracting parameters
    DataTable parameters = cmd.getParameters();
    
    // Edit Data command received - process it
    if (cmd.getType().equals(ActionUtils.CMD_EDIT_DATA))
    {
      // The data to be edited by a human
      DataTable data = parameters.rec().getDataTable(EditData.CF_DATA);
      
      // This is a "Specify IP ranges" command
      if (cmd.getRequestId().getId().equals("editRangesCommand"))
      {
        // Adding an IP subnet to discover
        DataRecord rec = data.addRecord();
        
        // Filling "Start Address" and leaving other field at their defaults (Mask-type range, the mask is 255.255.255.0)
        rec.setValue("startAddress", "192.168.1.1");
      }
      
      // Returning original or modified data
      return new GenericActionResponse(data);
    }
    
    // Confirm command received
    if (cmd.getType().equals(ActionUtils.CMD_CONFIRM))
    {
      // Simply reply "No"
      return new GenericActionResponse(new DataRecord(Confirm.RFT_CONFIRM, ActionUtils.NO_OPTION).wrap());
    }
    
    throw new IllegalArgumentException("Unexpected action command: " + cmd);
  }
  
  public static GenericActionCommand getGenericActionCommand(Context devicesContext, ActionIdentifier actionId, GenericActionResponse actionResponse) throws ContextException
  {
    return ActionUtils.stepAction(devicesContext, actionId, actionResponse, null);
  }
  
  public static ActionIdentifier getActionIdentifier(Context devicesContext) throws ContextException
  {
    return ActionUtils.initAction(devicesContext, "discovery", new ServerActionInput(), null, new ActionExecutionMode(ActionExecutionMode.HEADLESS), null);
  }
}
