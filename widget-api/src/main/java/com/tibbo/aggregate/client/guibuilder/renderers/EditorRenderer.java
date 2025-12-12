package com.tibbo.aggregate.client.guibuilder.renderers;

import java.awt.*;

import javax.swing.*;

import com.tibbo.aggregate.client.guibuilder.editableComponent.*;

public interface EditorRenderer
{
  EditableComponent getEditableComponent();
  
  Component getProduction();
  
  void componentPropertyChanged(String prop);
  
  JComponent getRender();
}