package examples.webapp;

import com.tibbo.aggregate.common.AggreGateException;
import com.tibbo.linkserver.plugin.context.webserver.WebServerContextPlugin;
import com.tibbo.linkserver.web.WebApplication;

public class DemoWebApplication implements WebApplication
{
  private static final String WEB_APPLICATION_CONTEXT_PATH = "/demo-web-app";
  private static final String WEB_APPLICATION_FILE_NAME = "demo-web-app.war";
  
  private org.apache.catalina.Context context;
  
  @Override
  public void init() throws AggreGateException
  {
    
  }
  
  @Override
  public String getContextPath()
  {
    return WEB_APPLICATION_CONTEXT_PATH;
  }
  
  @Override
  public void deploy() throws AggreGateException
  {
    // Web application registration
    context = WebServerContextPlugin.registerWebApplication(WEB_APPLICATION_CONTEXT_PATH, WEB_APPLICATION_FILE_NAME);
  }
  
  @Override
  public void stop() throws AggreGateException
  {
    // Web application unregistration
    // Previously saved context is used to unregister the web application
    WebServerContextPlugin.unregisterWebApplication(context);
  }
}
