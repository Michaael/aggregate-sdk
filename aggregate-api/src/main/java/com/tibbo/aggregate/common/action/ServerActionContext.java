package com.tibbo.aggregate.common.action;

import com.tibbo.aggregate.common.context.CallerController;
import com.tibbo.aggregate.common.server.ServerContext;

public class ServerActionContext extends ActionContext
{
  private final CallerController callerController;
  
  // Context in that the definition of currently executing action resides
  private ServerContext definingContext;


  public ServerActionContext(ActionDefinition actionDefinition, ServerContext context, CallerController callerController)
  {
    super(actionDefinition, callerController.getCallerData().getActionManager());
    
    if (context == null)
    {
      throw new NullPointerException();
    }
    
    this.callerController = callerController;
    this.definingContext = context;
  }
  
  @Override
  public CallerController getCallerController()
  {
    return callerController;
  }
  
  @Override
  public ServerContext getDefiningContext()
  {
    return definingContext;
  }
  
  @Override
  public ActionDefinition getActionDefinition()
  {
    return super.getActionDefinition();
  }
  
  protected void setDefiningContext(ServerContext context)
  {
    this.definingContext = context;
  }

}
