package com.tibbo.aggregate.common.widget;

import java.util.*;

import com.tibbo.aggregate.common.binding.*;
import com.tibbo.aggregate.common.context.*;
import com.tibbo.aggregate.common.widget.component.*;

public interface WidgetTemplate
{
  public static final String V_BINDINGS = "bindings";
  
  public List<ExtendedBinding> getBindings();
  
  public void changeBindingSet(Collection<ExtendedBinding> oldSet, List<ExtendedBinding> newBindings, boolean saveOrder) throws ContextException;
  
  public WContainer getRootPanel();
  
  public List<WComponent> getFullComponentList();
  
  public Map<String, String> getScripts();
  
  public void addScript(String name, String script);
  
  public void removeScript(String name);
  
  public WComponent getComponent(String name);
}
