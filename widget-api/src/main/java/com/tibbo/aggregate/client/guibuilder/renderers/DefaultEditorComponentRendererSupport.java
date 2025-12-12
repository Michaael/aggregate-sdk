package com.tibbo.aggregate.client.guibuilder.renderers;

import javax.swing.*;

import com.tibbo.aggregate.client.guibuilder.editableComponent.*;
import com.tibbo.aggregate.common.widget.component.*;
import com.tibbo.aggregate.common.widget.context.*;
import com.tibbo.aggregate.common.widget.runtime.renderers.*;

public class DefaultEditorComponentRendererSupport<C extends WAbstractComponent, T extends WAbstractContext<WComponentContext, C>, O extends JComponent> extends
    EditorRendererSupport<C, T, O, EditableComponent>
{
  public DefaultEditorComponentRendererSupport(AbstractSwingRenderer<C, T, O, SwingRendererSupport> renderer)
  {
    super(renderer);
  }
  
  @Override
  protected EditableComponent createEditableComponent()
  {
    return getGuiBuilder().createEditableComponent(getRenderer().getComponentContext(), getGuiBuilder().getWorkPane(), getRenderer().getRender());
  }
}
