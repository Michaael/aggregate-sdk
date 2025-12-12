package com.tibbo.aggregate.client.guibuilder;

import java.util.*;

import javax.swing.*;

import com.tibbo.aggregate.client.guibuilder.layout.*;
import com.tibbo.aggregate.client.guibuilder.renderers.*;
import com.tibbo.aggregate.common.widget.component.*;
import com.tibbo.aggregate.common.widget.context.*;

public interface WorkFormViewer
{
  boolean isDecoratedMode();
  
  EditorRenderer getComponentRenderer(WContainer parentComponent);
  
  BasicLayoutHelper getLayoutHelper(WContainer parent);
  
  WComponentContext getComponentContextByName(String activeSubcontainer);
  
  JComponent getComponentRepresentation(WComponentContext cdc);
  
  WContainerContext<WComponentContext, WContainer> getParentContext(WComponentContext componentContext);
  
  List<WComponentContext> getChildComponentContexts(WContainerContext componentContext);
  
}
