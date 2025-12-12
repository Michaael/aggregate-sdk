package examples.webapp;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;

import com.tibbo.aggregate.common.context.*;
import com.tibbo.aggregate.common.datatable.*;
import com.tibbo.aggregate.common.server.*;
import com.tibbo.linkserver.*;
import com.tibbo.linkserver.context.*;

public class DemoWebAppServlet extends HttpServlet
{
  
  @Override
  public void init() throws ServletException
  {
    
  }
  
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
  {
    // Server context manager allows to get access to AggreGate Server and its contexts
    ContextManager cm = Server.getContextManager();
    
    // Getting root context
    Context root = cm.getRoot();
    
    try
    {
      // Getting variable "version" from the root context
      // Caller controller is not used (applicable in rare cases)
      DataTable version = root.getVariable(RootContextConstants.V_VERSION);
      
      // Writing variable "version" as a string to the response body
      response.getWriter().println(version.dataAsString() + "\n");
      
      // This caller controller is used by context manager for internal operations
      // It is unsafe since it doesn't perform any permission checking
      CallerController uncheckedCallerController = new UncheckedCallerController();
      
      // Getting variable "license" from the root context
      // Using caller controller with disabled permission checking
      DataTable license = root.getVariable(RootContextConstants.V_LICENSE, uncheckedCallerController);
      
      // Writing variable "license" as a string to the response body
      response.getWriter().println(license.dataAsString() + "\n");
      
      // Getting caller controller with permissions of a specific user
      // Creating default caller controller
      CallerController caller = new DefaultCallerController(new ServerCallerData());
      
      // Setting caller controller permissions by calling the "login" function for a specific user
      root.callFunction(RootContextConstants.F_LOGIN, caller, "admin", "admin");
      
      // Getting variable "status" from the root context
      // Using caller controller with permissions of the specific user
      DataTable status = root.getVariable(RootContextConstants.V_STATUS, caller);
      
      // Writing variable "status" as a string to the response body
      response.getWriter().println(status.dataAsString());
    }
    catch (ContextException ex)
    {
      DemoWebAppContextPlugin.LOGGER.warn(ex.getMessage(), ex);
    }
  }
  
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
  {
    // For POST request do the same as for GET
    doGet(request, response);
  }
  
  @Override
  public void destroy()
  {
    
  }
}
