package com.tibbo.aggregate.client.guibuilder;

import com.tibbo.aggregate.common.widget.engine.*;

public interface WidgetDataModel
{
  Object getResourceByID(String name);
  
  void addResource(TemplateResource resource);
  
  void removeResource(String id);
  
  boolean isDescendant(String id, String name);
}
