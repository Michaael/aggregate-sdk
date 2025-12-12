package com.tibbo.aggregate.common.action;

import com.tibbo.aggregate.common.datatable.*;

public class ServerActionInput implements InitialRequest
{
  private DataTable data = new SimpleDataTable();
  private boolean remember;
  private RequestIdentifier requestId;
  
  public ServerActionInput()
  {
  }
  
  public ServerActionInput(DataTable dataTable)
  {
    if (dataTable != null)
    {
      data = dataTable.clone();
    }
  }
  
  public ServerActionInput(GenericActionResponse request)
  {
    if (request == null)
    {
      throw new NullPointerException();
    }
    
    if (request.getParameters() != null)
    {
      data = request.getParameters().clone();
    }
  }
  
  public DataTable getData()
  {
    return data;
  }
  
  public boolean shouldRemember()
  {
    return remember;
  }
  
  public void setRemember(boolean flag)
  {
    this.remember = flag;
  }
  
  public void setRequestId(RequestIdentifier requestId)
  {
    this.requestId = requestId;
  }
  
  public RequestIdentifier getRequestId()
  {
    return requestId;
  }
}
