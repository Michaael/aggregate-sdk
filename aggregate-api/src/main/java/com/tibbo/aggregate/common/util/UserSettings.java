package com.tibbo.aggregate.common.util;

import java.text.*;
import java.util.*;

import com.tibbo.aggregate.common.*;
import com.tibbo.aggregate.common.action.*;
import com.tibbo.aggregate.common.context.*;
import com.tibbo.aggregate.common.data.*;
import com.tibbo.aggregate.common.datatable.*;
import com.tibbo.aggregate.common.device.*;
import com.tibbo.aggregate.common.server.*;

public class UserSettings implements Cloneable
{
  private String datePattern;
  private String timePattern;
  private String timeZone;
  private int weekStartDay = Calendar.MONDAY;
  
  private List<EntityRelatedActionDescriptor> variableActions;
  private List<EntityRelatedActionDescriptor> eventActions;
  
  public UserSettings()
  {
  }
  
  public UserSettings(ContextManager cm, CallerController callerController) throws ContextException, RemoteDeviceErrorException
  {
    this();
    fill(cm, callerController);
  }
  
  public SimpleDateFormat createDateFormatter()
  {
    return createDateFormatter(getDateTimePattern(), null);
  }
  
  public SimpleDateFormat createDateFormatter(String pattern, String timezone)
  {
    SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.ENGLISH);
    
    String tz = timezone != null ? timezone : getTimeZone();
    if (tz != null)
    {
      sdf.setTimeZone(TimeZone.getTimeZone(tz));
    }
    
    return sdf;
  }
  
  public String getDatePattern()
  {
    return datePattern != null ? datePattern : DateUtils.DEFAULT_DATE_PATTERN;
  }
  
  public void setDatePattern(String datePattern)
  {
    this.datePattern = datePattern;
  }
  
  public String getTimePattern()
  {
    return timePattern != null ? timePattern : DateUtils.DEFAULT_TIME_PATTERN;
  }
  
  public void setTimePattern(String timePattern)
  {
    this.timePattern = timePattern;
  }
  
  public String getDateTimePattern()
  {
    return DateUtils.getDateTimePattern(getDatePattern(), getTimePattern());
  }
  
  public String getTimeZone()
  {
    return timeZone;
  }
  
  public void setTimeZone(String timeZone)
  {
    this.timeZone = timeZone;
  }
  
  public int getWeekStartDay()
  {
    return weekStartDay;
  }
  
  public void setWeekStartDay(int weekStartDay)
  {
    this.weekStartDay = weekStartDay;
  }
  
  public List<EntityRelatedActionDescriptor> getVariableActions()
  {
    return variableActions;
  }
  
  public void setVariableActions(List<EntityRelatedActionDescriptor> variableActions)
  {
    this.variableActions = variableActions;
  }
  
  public List<EntityRelatedActionDescriptor> getEventActions()
  {
    return eventActions;
  }
  
  public void setEventActions(List<EntityRelatedActionDescriptor> eventActions)
  {
    this.eventActions = eventActions;
  }
  
  @Override
  public UserSettings clone()
  {
    try
    {
      final UserSettings clone = (UserSettings) super.clone();
      
      if (variableActions != null)
        clone.variableActions = new ArrayList<EntityRelatedActionDescriptor>(variableActions);
      
      if (eventActions != null)
        clone.eventActions = new ArrayList<EntityRelatedActionDescriptor>(eventActions);
      
      return clone;
    }
    catch (CloneNotSupportedException ex)
    {
      throw new IllegalStateException(ex);
    }
  }
  
  public void fill(ContextManager cm, CallerController callerController) throws ContextException, RemoteDeviceErrorException
  {
    fillBasicProperties(cm, callerController);
    fillActions(cm, callerController);
  }
  
  public void fillBasicProperties(ContextManager cm, CallerController callerController) throws ContextException, RemoteDeviceErrorException
  {
    if (callerController == null)
    {
      return;
    }
    
    // Distributed: ok, getting info from directly connected server
    Context userContext = cm.get(ContextUtils.userContextPath(callerController.getUsername()), callerController);
    
    if (userContext == null || userContext.getVariableDefinition(EditableChildContextConstants.V_CHILD_INFO) == null)
    {
      return;
    }
    
    DataRecord userInfo = userContext.getVariable(EditableChildContextConstants.V_CHILD_INFO, callerController).rec();
    
    setDatePattern(userInfo.getString(User.FIELD_DATEPATTERN));
    setTimePattern(userInfo.getString(User.FIELD_TIMEPATTERN));
    setTimeZone(userInfo.getString(User.FIELD_TIMEZONE));
    
    if (userInfo.getFormat().hasField(User.FIELD_WEEK_START))
    {
      setWeekStartDay(userInfo.getInt(User.FIELD_WEEK_START));
    }
  }
  
  public void fillActions(ContextManager cm, CallerController callerController)
  {
    try
    {
      // Distributed: ok, getting data from directly connected server
      Context utilities = cm.get(Contexts.CTX_UTILITIES, callerController);
      
      if (utilities != null)
      {
        DataTable variableActions = utilities.callFunction(UtilitiesContextConstants.F_VARIABLE_ACTIONS, callerController);
        setVariableActions(DataTableConversion.beansFromTable(variableActions, EntityRelatedActionDescriptor.class, EntityRelatedActionDescriptor.FORMAT, false));
        
        DataTable eventActions = utilities.callFunction(UtilitiesContextConstants.F_EVENT_ACTIONS, callerController);
        setEventActions(DataTableConversion.beansFromTable(eventActions, EntityRelatedActionDescriptor.class, EntityRelatedActionDescriptor.FORMAT, false));
      }
    }
    catch (Exception ex)
    {
      Log.CLIENTS.error("Error retrieving entity-related actions", ex);
    }
  }
  
}
