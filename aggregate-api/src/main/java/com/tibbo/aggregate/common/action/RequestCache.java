package com.tibbo.aggregate.common.action;

import java.util.*;

public class RequestCache
{
  private Map<RequestIdentifier, ActionResponse> requests = new HashMap();
  
  public RequestCache()
  {
    super();
  }
  
  public Map<RequestIdentifier, ActionResponse> getRequests()
  {
    return requests;
  }
  
  public ActionResponse getRequest(RequestIdentifier requestId)
  {
    return requests.get(requestId);
  }
  
  protected void addRequest(RequestIdentifier requestId, ActionResponse actionRequest)
  {
    requests.put(requestId, actionRequest);
  }
  
  protected void removeRequest(RequestIdentifier requestId)
  {
    requests.remove(requestId);
  }
  
  protected void clear()
  {
    requests.clear();
  }
}
