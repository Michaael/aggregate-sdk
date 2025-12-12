package com.tibbo.aggregate.client.guibuilder;

import com.tibbo.aggregate.common.widget.component.*;

public interface TemplateChangeManager
{
  void deferComponentSizeRevalidation(WComponent component);
  
  void setStopSaves(boolean b);
  
  void performSingleChange(Change change);
  
  void rewriteLastStep();
  
  boolean isStopSaves();
}
