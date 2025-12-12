package com.tibbo.aggregate.common.action;

public interface ActionCommand extends Cloneable
{
  boolean isResponseValid(ActionResponse actionRequest);
  
  RequestIdentifier getRequestId();
  
  void setBatchEntry(boolean batchEntry);
  
  boolean isBatchEntry();
  
  ActionCommand clone();
  
  boolean isInteractive();
}
