package com.tibbo.aggregate.common.action;

import java.text.*;
import java.util.*;

import com.tibbo.aggregate.common.*;
import com.tibbo.aggregate.common.action.command.*;
import com.tibbo.aggregate.common.context.*;
import com.tibbo.aggregate.common.datatable.*;
import com.tibbo.aggregate.common.device.*;
import com.tibbo.aggregate.common.event.*;
import com.tibbo.aggregate.common.server.*;
import com.tibbo.aggregate.common.util.*;

public abstract class ServerAction extends SingleThreadAction<ServerActionInput, GenericActionCommand, GenericActionResponse>
{
  
  private final ServerActionCommandProcessor processor = new ServerActionCommandProcessor(this);
  
  @Override
  public GenericActionResponse send(GenericActionCommand actionCommand) throws DisconnectionException
  {
    return super.send(actionCommand);
  }
  
  public ServerActionCommandProcessor getProcessor()
  {
    return processor;
  }
  
  @Override
  protected ServerActionContext getActionContext()
  {
    return (ServerActionContext) super.getActionContext();
  }
  
  protected ActionDefinition getActionDefinition()
  {
    return getActionContext().getActionDefinition();
  }
  
  public ServerContext getDefiningContext()
  {
    return getActionContext().getDefiningContext();
  }
  
  public DataTable getParameter(ServerActionInput input, String name)
  {
    RequestCache cache = getActionContext() != null ? getActionContext().getRequestCache() : null;
    
    if (cache != null)
    {
      ActionResponse acmd = cache.getRequest(new RequestIdentifier(name));
      if (acmd != null && acmd instanceof GenericActionCommand)
      {
        GenericActionCommand gac = (GenericActionCommand) acmd;
        return gac.getParameters();
      }
    }
    
    return ServerActionCommandProcessor.getExecutionParameter(input.getData(), name);
  }
  
  public CallerController getCallerController()
  {
    return getActionContext().getCallerController();
  }

  public ActionResult invoke(ServerActionInput parameters) throws ContextException
  {
    if (getActionContext() != null && getActionContext().isExecutedAsDefault() && getActionContext().getActionExecutionMode().getCode() != ActionExecutionMode.REDIRECT)
    {
      if (executeDefaultSubstitutors(parameters))
      {
        return null;
      }
    }

    return execute(parameters);
  }

  protected abstract ActionResult execute(ServerActionInput parameters) throws ContextException;

  protected boolean executeDefaultSubstitutors(ServerActionInput parameters) throws ContextException
  {
    ServerContext con = getDefiningContext();

    boolean found = false;

    List<ActionDefinition> actions = con.getActionDefinitions(getCallerController());
    for (ActionDefinition ad : actions)
    {
      if (ad instanceof LaunchDashboardAction)
      {
        LaunchDashboardAction oda = (LaunchDashboardAction) ad;

        if (!oda.isDefaultActionSubstitutor(con, getCallerController()))
        {
          continue;
        }

        ActionResult res = redirect(con, ad.getName(), parameters);

        if (res == null || res.isSuccessful())
        {
          found = true;
        }
      }
    }

    return found;
  }


  public ActionResult redirect(ServerContext ctx, String action, ServerActionInput input) throws ContextException
  {
    return redirect(ctx, ctx.getActionDefinition(action), input);
  }
  
  public ActionResult redirect(ServerContext ctx, ActionDefinition def, ServerActionInput input) throws ContextException
  {
    ServerActionContext actionContext = getActionContext();
    ServerContext prevCtx = actionContext.getDefiningContext();
    actionContext.setDefiningContext(ctx);
    try
    {
      return super.redirect(def, input);
    }
    catch (DisconnectionException ex)
    {
      throw new ContextException(ex);
    }
    finally
    {
      actionContext.setDefiningContext(prevCtx);
    }
  }
  
  public ActionResult call(ServerContext devicesContext, ActionDefinition def, ServerActionInput serverActionInput)
  {
    return null;
  }
  
  public void redirectToGroupedAction(ServerContext ctx, List<Context> contexts, String action, CallerController caller, DataTable executionParameters) throws ContextException, DisconnectionException
  {
    List<ActionHolder> actions = new LinkedList();
    
    for (Context con : contexts)
    {
      ActionHolder holder = new ActionHolder();
      holder.setContext((ServerContext) con);
      holder.setActionName(action);
      holder.setInitialParameters(executionParameters);
      actions.add(holder);
    }
    
    ActionIdentifier actionId = ServerActionHelper.initActions(caller, actions, ctx);
    
    redirectById(actionId);
  }
  
  protected void executeInteractively(String operationName, ServerContext ctx, List<Context> contexts, String action, DataTable executionParameters) throws ContextException, DisconnectionException
  {
    ActionDefinition ad = null;
    
    for (Iterator<Context> iter = contexts.iterator(); iter.hasNext();)
    {
      ServerContext lsc = (ServerContext) iter.next();
      
      ad = lsc.getActionDefinition(action);
      
      if (ad == null || !ad.isEnabled() || !ctx.checkPermissions(ad.getPermissions(), getCallerController(), lsc, ad))
      {
        iter.remove();
        
        RequestIdentifier requestId = new RequestIdentifier("actionNotAvailable" + Math.abs(hashCode()));
        String msg = MessageFormat.format(Cres.get().getString("conActNotAvailExt"), action, lsc.toDetailedString());
        
        ShowMessage message = new ShowMessage(operationName, msg, EventLevel.INFO);
        message.setRequestId(requestId);
        message.setBatchEntry(true);
        
        getProcessor().send(message);
      }
    }
    
    if (contexts.size() == 0)
    {
      return;
    }
    else if (contexts.size() == 1)
    {
      ServerContext lsc = (ServerContext) contexts.get(0);
      redirect(lsc, ad, ActionUtils.createActionInput(executionParameters));
    }
    else
    {
      redirectToGroupedAction(ctx, contexts, action, getCallerController(), ActionUtils.createActionInput(executionParameters).getData());
    }
  }
  
  @Override
  protected void processError(Throwable ex)
  {
    if (Util.getRootCause(ex) instanceof DisconnectionException)
    {
      Log.CONTEXT_ACTIONS.info("Action interrupted: " + toString() + " (caller: " + getCallerController() + ")");
    }
    else
    {
      getProcessor().showError(Cres.get().getString("error"), EventLevel.ERROR, ex.getMessage(), ex);
    }
  }
}
