package com.tibbo.aggregate.client.guibuilder;

import java.beans.*;

import javax.swing.*;

import com.tibbo.aggregate.client.guibuilder.editableComponent.*;
import com.tibbo.aggregate.client.guibuilder.renderers.*;
import com.tibbo.aggregate.common.context.*;
import com.tibbo.aggregate.common.widget.*;
import com.tibbo.aggregate.common.widget.component.*;
import com.tibbo.aggregate.common.widget.context.*;
import com.tibbo.aggregate.common.widget.engine.*;

public interface AggreGateIDE
{
  IdeWorkPane getWorkPane();
  
  WidgetDataModel getDataModel();
  
  WorkFormViewer getViewer();
  
  WidgetTemplate getWidget();
  
  IdeToolBar getToolBar();
  
  TemplateChangeManager getTemplateChangesManager();
  
  EditorRenderer getComponentRenderer(String id);
  
  EditorRenderer getComponentRenderer(WComponent component);
  
  ContextManager getServerContextManager();
  
  WidgetBindingProcessor getBindingProcessor();
  
  TransferHandler getContainerCellTransferHandler();
  
  void containerLayoutChanged(WContainer component, PropertyChangeEvent evt);
  
  void elementPropertyChanged(WComponentContext componentContext, String prop);
  
  EditableComponent createEditableComponent(WComponentContext componentContext, IdeWorkPane workPane, JComponent render);
  
  WComponent getWComponent(String key);
}
