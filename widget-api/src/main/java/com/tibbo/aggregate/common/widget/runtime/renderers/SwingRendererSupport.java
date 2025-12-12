package com.tibbo.aggregate.common.widget.runtime.renderers;

import java.awt.*;

import javax.swing.*;

import com.tibbo.aggregate.common.context.*;
import com.tibbo.aggregate.common.widget.*;
import com.tibbo.aggregate.common.widget.component.*;
import com.tibbo.aggregate.common.widget.context.*;
import com.tibbo.aggregate.common.widget.runtime.util.*;

public class SwingRendererSupport<C extends WAbstractComponent, T extends WAbstractContext<WComponentContext, C>, O extends JComponent> implements RendererSupport<JComponent>
{
  private final AbstractSwingRenderer<C, T, O, SwingRendererSupport> renderer;
  
  public SwingRendererSupport(AbstractSwingRenderer<C, T, O, SwingRendererSupport> renderer)
  {
    super();
    this.renderer = renderer;
  }
  
  public AbstractSwingRenderer<C, T, O, SwingRendererSupport> getRenderer()
  {
    return renderer;
  }
  
  @Override
  public void initView()
  {
    WidgetComponentApiRendererUtilities.initBasicProperties(renderer.getComponentContext(), renderer.getComponent(), renderer.getRender(), getRenderer().getEngine().getServerContextManager());
    
    WidgetComponentApiRendererUtilities.setRendererSize(getProduction(), renderer.getComponent());
  }
  
  @Override
  public JComponent getProduction()
  {
    return renderer.getRender();
  }
  
  @Override
  public void resetRender()
  {
    /* Widget-API: the block below is moved to EditorRenderSupport. It helps to avoid using AbstractSwingViewer in Widget-API.
    
    initView();
    AbstractSwingViewer viewer = getRenderer().getEngine().getViewer();
    WContainerContext parent = viewer.getParentContext(getRenderer().getComponentContext());
    // getProduction() of parent will force its recomposition
    viewer.getComponentRenderer(parent).getProduction();
    */
  }
  
  @Override
  public void componentPropertyChanged(String property)
  {
    WAbstractComponent comp = getRenderer().getComponentContext().getComponent();
    if (property.equals(WComponent.V_WIDTH))
    {
      if (comp.getWidth() > 0)
      {
        Dimension d = getProduction().getPreferredSize();
        d.width = comp.getWidth();
        getProduction().setPreferredSize(d);
      }
    }
    else if (property.equals(WComponent.V_HEIGHT))
    {
      if (comp.getHeight() > 0)
      {
        Dimension d = getProduction().getPreferredSize();
        d.height = comp.getHeight();
        getProduction().setPreferredSize(d);
      }
    }
    else if (property.equals(WAbstractComponent.V_BORDER))
    {
      getRenderer().getRender().setBorder(getRenderer().getComponent().getBorder());
    }
    else if (property.equals(WAbstractComponent.V_FONT))
    {
      getRenderer().getRender().setFont(getRenderer().getComponent().getFont());
    }
    else if (property.equals(WAbstractComponent.V_FOCUSABLE))
    {
      getRenderer().getRender().setFocusable(getRenderer().getComponent().isFocusable());
    }
    else if (property.equals(WAbstractComponent.V_CURSOR))
    {
      if (getRenderer().getComponent().getCursor() != null)
      {
        getRenderer().getRender().setCursor(Cursor.getPredefinedCursor(getRenderer().getComponent().getCursor()));
      }
    }
    else if (property.equals(WAbstractComponent.V_POPUP_MENU))
    {
      WidgetComponentApiRendererUtilities.setComponentPopupMenu(getRenderer().getComponentContext(), getRenderer().getRender(), getRenderer().getComponentContext().getPath(),
          getRenderer().getEngine().getServerContextManager());
    }
    else
    {
      try
      {
        AbstractSwingRenderer.synchronizeProperty(property, getRenderer().getComponent(), getRenderer().getRender());
      }
      catch (ContextException ex)
      {
        getRenderer().getComponentContext().fireExceptionEvent(Res.get().getString("errSettingNewValue") + ": " + ex.getMessage(), ex);
        return;
      }
    }
    revalidateAndRepaint();
  }
  
  public void revalidateAndRepaint()
  {
    getProduction().revalidate();
    getProduction().repaint();
  }
  
  @Override
  public boolean isInteractive()
  {
    return true;
  }
  
  @Override
  public void stop()
  {
  }
  
  @Override
  public void start()
  {
  }
  
  public void renderChanged()
  {
    // TODO: think how to call this method more correctly. Bad variants: "renderStructureChanged", "renderSeriouslyChanged"
  }
}

