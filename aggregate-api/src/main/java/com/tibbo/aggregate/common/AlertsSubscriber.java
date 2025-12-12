package com.tibbo.aggregate.common;

import com.tibbo.aggregate.common.context.*;
import com.tibbo.aggregate.common.server.*;
import com.tibbo.aggregate.common.datatable.*;
import com.tibbo.aggregate.common.event.*;
import com.tibbo.aggregate.common.protocol.*;

public class AlertsSubscriber implements Runnable
{
  private ContextEventListener listener;
  private ContextManager manager;
  private CallerController controller;
  private String username;
  
  public AlertsSubscriber(ContextEventListener listener, ContextManager manager, CallerController controller, String username) {
    this.username = username;
    this.listener = listener;
    this.manager = manager;
    this.controller = controller;
  }
  public void subscribe() {
    run();
  }
  @Override
  public void run()
  {
    try
    {
      int popupMode = AlertConstants.POPUP_MODE_OWN;
      
      Context userContext = manager.get(ContextUtils.userContextPath(username), controller);
      
      if (userContext != null && userContext.getVariableDefinition(AlertConstants.V_ALERTS_CONFIG, controller) != null)
      {
        DataTable alertsConfig = userContext.getVariable(AlertConstants.V_ALERTS_CONFIG, controller);
        popupMode = alertsConfig.rec().getInt(AlertConstants.VF_ALERTS_CONFIG_POPUP_MODE);
      }
      
      String mask;
      
      switch (popupMode)
      {
        case AlertConstants.POPUP_MODE_OWN:
          mask = ContextUtils.alertContextPath(username, ContextUtils.CONTEXT_GROUP_MASK);
          break;
        
        case AlertConstants.POPUP_MODE_ALL:
          mask = ContextUtils.alertContextPath(ContextUtils.CONTEXT_GROUP_MASK, ContextUtils.CONTEXT_GROUP_MASK);
          break;
        
        default:
          throw new IllegalStateException("Unknown alert popup mode: " + popupMode);
      }
      
      // Relying to automatic server-side listener cleanup upon client disconnection
      manager.addMaskEventListener(mask, AlertContextConstants.E_ALERTNOTIFY, listener);
    }
    catch (Exception ex)
    {
      Log.CLIENTS.error("Error subscribing to alert notifications for " + username, ex);
    }
  }
 
}