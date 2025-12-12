package examples.api;

import java.util.Date;
import java.util.List;

import com.tibbo.aggregate.common.Log;
import com.tibbo.aggregate.common.context.Context;
import com.tibbo.aggregate.common.context.ContextException;
import com.tibbo.aggregate.common.context.ContextManager;
import com.tibbo.aggregate.common.context.ContextUtils;
import com.tibbo.aggregate.common.context.Contexts;
import com.tibbo.aggregate.common.datatable.DataTable;
import com.tibbo.aggregate.common.protocol.RemoteServer;
import com.tibbo.aggregate.common.protocol.RemoteServerController;
import com.tibbo.aggregate.common.server.RootContextConstants;

/**
 * This example shows how to create/delete user accounts and manage their settings remotely using Server API.
 */
public class ManageUsers
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

      listUserAccounts(cm);

      createEditDeleteUser(cm);

      // Disconnecting from the server
      rlc.disconnect();
    }
    catch (Exception ex)
    {
      Log.TEST.error("Failed to manage users", ex);
    }
  }

  private static void createEditDeleteUser(ContextManager cm) throws ContextException
  {
    String testUserName = "testUser";
    String testUserPassword = "testUserPwd123";

    Context userContext = createTestUser(cm, testUserName, testUserPassword);

    editTestUser(userContext);

    // Deleting the user

    // Getting users container context
    Context usersContext = cm.get(Contexts.CTX_USERS);

    // Calling "delete" function and passing username of user to delete
    // The call will implicitly create and fill in function input data table
    deleteTestUser(usersContext, testUserName);
  }

  private static void listUserAccounts(ContextManager cm) throws ContextException
  {
    // Listing all available user accounts

    // Getting all contexts matching to the mask
    String mask = ContextUtils.userContextPath(ContextUtils.CONTEXT_GROUP_MASK); // Will result to "users.*"
    List<Context> userContexts = ContextUtils.expandMaskToContexts(mask, cm);

    for (Context userContext : userContexts)
    {
      // Getting user status info provided by "status" variable
      DataTable status = userContext.getVariable("status");

      // Getting account creation time ("creationtime" field of type Date)
      Date creationTime = status.rec().getDate("creationTime");

      Log.TEST.info("Found user account: " + userContext.toDetailedString() + " created on " + creationTime);
    }
  }

  public static void deleteTestUser(Context usersContext, String userName) throws ContextException
  {
    usersContext.callFunction("delete", userName);
  }

  public static Context createTestUser(ContextManager cm, String testUserName, String testUserPassword) throws ContextException
  {

    // Calling "register" function from the root context
    // This call will implicitly create function input Data Table and fill its first record with name/password of the new user account
    // Note that according to the "register" function documentation password must be specified twice ("Password" and "Repeat Password")
    cm.getRoot().callFunction(RootContextConstants.F_REGISTER, testUserName, testUserPassword, testUserPassword);

    // Getting context of the newly created user
    String userContextPath = ContextUtils.userContextPath(testUserName);
    Context userContext = cm.get(userContextPath);

    // Changing user's email
    // This call will get "childInfo" variable, change "email" field in its first record, and set new value of the variable
    // See description if "childInfo" variable in the user context for detailed information on its format
    userContext.setVariableField("childInfo", "email", "user@test.com", null);

    return userContext;
  }

  public static void editTestUser(Context userContext) throws ContextException
  {
    // To perform some more complicated changes, we need to process value of "childInfo" variable as a Data Table
    // Let's change user's first and last name
    DataTable userInfo = userContext.getVariable("childInfo");
    userInfo.rec().setValue("firstname", "John");
    userInfo.rec().setValue("lastname", "Doe");
    userContext.setVariable("childInfo", userInfo);
  }
}
