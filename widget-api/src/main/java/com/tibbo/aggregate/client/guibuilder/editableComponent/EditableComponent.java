package com.tibbo.aggregate.client.guibuilder.editableComponent;

import java.awt.*;
import java.awt.event.*;

import javax.swing.border.*;

import com.tibbo.aggregate.common.widget.component.*;
import com.tibbo.aggregate.common.widget.context.*;

public interface EditableComponent
{
  public abstract Border getEditorModeBorder();
  
  public void setBorder(Border border);
  
  public Container getParent();
  
  public abstract Rectangle getBounds();
  
  public abstract WComponentContext getComponentContext();
  
  public int gridShift(int x, WContainer parentComponent);
  
  public void removeMouseListener(MouseListener l);
  
  public void removeMouseMotionListener(MouseMotionListener l);
  
  void stop();
  
  void addMouseListener(MouseListener l);
  
  void addMouseMotionListener(MouseMotionListener l);
  
  void reviewRender();
  
  void setToolTipText(String text);
  
  void refreshHandlers();
  
  void setVisible(boolean aFlag);
  
  void setFocusable(boolean focusable);
  
  public void refresh();
  
  public abstract void setPreferredSize(Dimension preferredSize);
  
  public abstract void setMinimumSize(Dimension preferredSize);
  
  public abstract void validate();
  
  public abstract void setOpaque(boolean b);
  
}
