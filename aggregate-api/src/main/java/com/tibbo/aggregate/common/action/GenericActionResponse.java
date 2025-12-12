package com.tibbo.aggregate.common.action;

import com.tibbo.aggregate.common.datatable.*;

public class GenericActionResponse implements ActionResponse
{
  private DataTable parameters;
  private boolean remember;
  private RequestIdentifier requestId;
  
  public GenericActionResponse(DataTable parameters)
  {
    this(parameters, false, null);
  }
  
  public GenericActionResponse(DataTable parameters, boolean remember, RequestIdentifier requestId)
  {
    this.parameters = parameters;
    this.remember = remember;
    this.requestId = requestId;
  }
  
  public DataTable getParameters()
  {
    return parameters;
  }
  
  public boolean shouldRemember()
  {
    return remember;
  }
  
  public RequestIdentifier getRequestId()
  {
    return requestId;
  }
  
  public void setParameters(DataTable parameters)
  {
    this.parameters = parameters;
  }
  
  public void setRemember(boolean remember)
  {
    this.remember = remember;
  }
  
  public void setRequestId(RequestIdentifier requestId)
  {
    this.requestId = requestId;
  }
}
