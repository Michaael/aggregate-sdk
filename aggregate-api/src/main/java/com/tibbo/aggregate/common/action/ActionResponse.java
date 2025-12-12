package com.tibbo.aggregate.common.action;

public interface ActionResponse
{
  boolean shouldRemember();
  
  RequestIdentifier getRequestId();
}
