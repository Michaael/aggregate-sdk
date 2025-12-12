package examples.plugin;

import java.awt.*;

import com.tibbo.aggregate.common.Log;
import com.tibbo.aggregate.common.context.AbstractContext;
import com.tibbo.aggregate.common.context.CallerController;
import com.tibbo.aggregate.common.context.Context;
import com.tibbo.aggregate.common.context.ContextException;
import com.tibbo.aggregate.common.context.ContextUtils;
import com.tibbo.aggregate.common.context.UncheckedCallerController;
import com.tibbo.aggregate.common.context.VariableDefinition;
import com.tibbo.aggregate.common.datatable.DataTable;
import com.tibbo.aggregate.common.datatable.FieldFormat;
import com.tibbo.aggregate.common.datatable.SimpleDataTable;
import com.tibbo.aggregate.common.datatable.TableFormat;
import com.tibbo.aggregate.common.expression.DefaultReferenceResolver;
import com.tibbo.aggregate.common.plugin.AbstractContextPlugin;
import com.tibbo.aggregate.common.plugin.PluginException;
import com.tibbo.aggregate.common.server.ServerContext;

/**
 * This demo plugin creates a tracker to show user's device count under every user account. The tracker uses status table to color-code different device numbers. There is a global configuration
 * setting that may be used to disable tracker creation.
 */
@SuppressWarnings("RedundantThrows")
public class DemoServerPlugin extends AbstractContextPlugin
{
  private static final String CONTEXT_TYPE_TRACKERS = "trackers";

  private static final String TRACKER_NAME = "usersDeviceCount";

  private static final String VARIABLE_CONFIG = "config";

  private static final String FIELD_CREATE_TRACKERS = "createTrackers";

  // Defining format of the global configuration variable as one-record
  private static final TableFormat FORMAT_CONFIG = new TableFormat(1, 1);
  static
  {
    // Adding boolean field
    FORMAT_CONFIG.addField(FieldFormat.create(FIELD_CREATE_TRACKERS, FieldFormat.BOOLEAN_FIELD, "Create Demo Trackers", true));
  }

  @Override
  public void globalInit(Context rootContext) throws PluginException
  {
    // Creating global config variable definition
    VariableDefinition config = new VariableDefinition(VARIABLE_CONFIG, FORMAT_CONFIG, true, true, "Demo Plugin Configuration", ContextUtils.GROUP_DEFAULT);

    // Enabling global configuration
    createGlobalConfigContext(rootContext, true, config);
  }

  @Override
  public void install(ServerContext context) throws ContextException, PluginException
  {
    // Using caller controller with disabled permission checking
    CallerController caller = new UncheckedCallerController();

    // This will be true for every user's trackers contexts, i.e. every trackers context in the system
    if (context.getType().equals(CONTEXT_TYPE_TRACKERS))
    {
      try
      {
        // Skip creation if tracker already exists
        if (context.getChild(TRACKER_NAME, caller) != null)
        {
          return;
        }

        // Getting "Create Demo Trackers" global configuration property
        // It's value of "createTrackers" field in first record of "config" variable
        boolean create = getGlobalConfigContext().getVariable(VARIABLE_CONFIG, caller).rec().getBoolean(FIELD_CREATE_TRACKERS);

        if (!create)
        {
          return;
        }

        // Parent context of the trackers context is user context, getting its name and description
        String userContextName = context.getParent().getName();
        String userContextDescription = context.getParent().getDescription();

        // Constructing tracker description
        String trackerDescription = "Device count of user '" + userContextDescription + "'";

        // Constructing tracker expression
        // It will get count of children contexts under user's devices context
        // This is actually the number of user's device accounts
        String devicesContextPath = ContextUtils.devicesContextPath(userContextName);
        String expression = "{" + devicesContextPath + ":" + AbstractContext.V_CHILDREN + "#" + DefaultReferenceResolver.RECORDS + "}";

        // Calling "create" function of trackers context to create a new tracker
        // Since trackers will be created under different user accounts, we can use the same name for all of them
        // This call will implicitly fill function input data table (first three fields: name, description and expression, others will have default values)
        context.callFunction("create", caller, TRACKER_NAME, trackerDescription, expression);

        // Getting context of newly created tracker
        Context tracker = context.getChild(TRACKER_NAME, caller);

        // Constructing statuses table to color-code different device counts
        DataTable statuses = new SimpleDataTable(tracker.getVariableDefinition("statuses").getFormat());

        // Adding status records
        statuses.addRecord("Many", "{tracker/} > 100", Color.RED);
        statuses.addRecord("Some", "{tracker/} > 10", Color.ORANGE);
        statuses.addRecord("Few", "{tracker/} > 3", Color.YELLOW);
        statuses.addRecord("Very few", "{tracker/} > 0", Color.GREEN);
        statuses.addRecord("None", "true", Color.WHITE);

        // Writing status table
        tracker.setVariable("statuses", caller, statuses);
      }
      catch (Exception ex)
      {
        // Logging the exception at Error level using "trackers" category
        Log.TRACKERS.error("Error creating demo tracker", ex);

        // Firing "info" event in the trackers context to aware system operators
        context.fireEvent(AbstractContext.E_INFO, "Error creating demo tracker" + ex.getMessage());
      }
    }
  }
}
