package com.tibbo.aggregate.common.widget.context;

import java.util.*;

import com.tibbo.aggregate.common.widget.*;

public interface ContainerContextLayoutHelper
{
  void createConstraintsVariables(WComponentContext context, WidgetTemplate widget);
  
  List<String> getConstraintsPropertiesForLayout();
}
