package examples.webapp;

import org.apache.log4j.Logger;

import com.tibbo.aggregate.common.context.Context;
import com.tibbo.aggregate.common.plugin.AbstractContextPlugin;
import com.tibbo.aggregate.common.plugin.PluginException;
import com.tibbo.linkserver.plugin.context.webserver.WebServerContextPlugin;

public class DemoWebAppContextPlugin extends AbstractContextPlugin
{
  public static final String LOG = "ag.demo.webapp";
  public static final Logger LOGGER = Logger.getLogger(LOG);
  
  public DemoWebAppContextPlugin()
  {
    super("Demo Web Application");
  }
  
  @Override
  public void globalInit(Context rootContext) throws PluginException
  {
    // Adding Web Application to the web server
    WebServerContextPlugin.addAdditionalWebApplication(new DemoWebApplication());
  }
}