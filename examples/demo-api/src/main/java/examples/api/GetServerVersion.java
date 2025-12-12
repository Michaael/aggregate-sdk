package examples.api;

import com.tibbo.aggregate.common.Log;
import com.tibbo.aggregate.common.context.Context;
import com.tibbo.aggregate.common.context.ContextManager;
import com.tibbo.aggregate.common.datatable.DataTable;
import com.tibbo.aggregate.common.protocol.RemoteServer;
import com.tibbo.aggregate.common.protocol.RemoteServerController;
import com.tibbo.aggregate.common.server.RootContextConstants;

/**
 * This simple example illustrates how to connect to server remotely using Server API and get version of the server.
 */
public class GetServerVersion
{
  public static void main(String[] args)
  {
    try
    {
      // Enabling logging
      Log.start();
      
      // Provide correct server address/port and name/password of server user to log in as
      RemoteServer rls = new RemoteServer("localhost", RemoteServer.DEFAULT_PORT, "admin", "admin");
      
      // Creating server controller
      RemoteServerController rlc = new RemoteServerController(rls, true);
      
      // Connecting to the server
      rlc.connect();
      
      // Authentication/authorization
      rlc.login();
      
      // Getting context manager
      ContextManager cm = rlc.getContextManager();
      
      // Getting root context
      Context rootContext = cm.getRoot();
      
      // Getting "version" variable from the root context
      DataTable versionData = rootContext.getVariable(RootContextConstants.V_VERSION);
      
      // Version string is contained in the first record and "version" field of the data table
      String serverVersion = versionData.rec().getString(RootContextConstants.VF_VERSION_VERSION);

      Log.TEST.info("Server version: " + serverVersion);
      
      // Disconnecting from the server
      rlc.disconnect();
    }
    catch (Exception ex)
    {
      Log.TEST.error("Failed to fetch server version", ex);
    }
  }
}
