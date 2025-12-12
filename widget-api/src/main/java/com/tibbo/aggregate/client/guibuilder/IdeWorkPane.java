package com.tibbo.aggregate.client.guibuilder;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import com.tibbo.aggregate.client.guibuilder.editableComponent.*;

public interface IdeWorkPane
{
  void repaint();

  void revalidate();

  void setMousePointAndModifiers(MouseEvent e);

  int getModifiers();

  Set<TemplateResourceNode> getSelectedNodes();

  AggreGateIDE getGuiBuilder();

  Point getCurrentMousePoint();

  EventListener getSizeInsetsEditingListener();

  EditableComponent findEditableComponentWithActiveHandler();
}
