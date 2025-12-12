package com.tibbo.aggregate.common.widget.runtime.renderers;

import java.awt.*;
import java.lang.reflect.*;

import javax.swing.*;

import org.apache.commons.beanutils.*;

import com.tibbo.aggregate.common.context.*;
import com.tibbo.aggregate.common.widget.component.*;
import com.tibbo.aggregate.common.widget.context.*;
import com.tibbo.aggregate.common.widget.runtime.*;

public abstract class AbstractSwingRenderer<C extends WAbstractComponent, T extends WAbstractContext<WComponentContext, C>, O extends JComponent, U extends RendererSupport> implements
    WidgetComponentRenderer
{
  
  // INSTANCE FIELDS
  
  private U rendererSupport;
  
  private final SwingWidgetEngine engine;
  
  private O render;
  
  /**
   * Reference to corresponding {@link WComponentContext} object represented by this Renderer
   */
  private final T componentContext;
  
  public AbstractSwingRenderer(T componentContext, SwingWidgetEngine engine)
  {
    super();
    this.engine = engine;
    this.componentContext = componentContext;
  }
  
  public abstract O createRender();
  
  @Override
  public void start()
  {
    getRendererSupport().start();
  }
  
  @Override
  public void stop()
  {
    getRendererSupport().stop();
  }
  
  @Override
  public T getComponentContext()
  {
    return componentContext;
  }
  
  @Override
  public O getRender()
  {
    if (render == null)
    {
      render = createRender();
    }
    
    return render;
  }
  
  public String getComponentName()
  {
    return getComponent().getName();
  }
  
  public void setRender(O render)
  {
    this.render = render;
    getRendererSupport().resetRender();
  }
  
  public U getRendererSupport()
  {
    return rendererSupport;
  }
  
  public void setRendererSupport(U rendererSupport)
  {
    this.rendererSupport = rendererSupport;
    rendererSupport.initView();
  }
  
  public SwingWidgetEngine getEngine()
  {
    return engine;
  }
  
  public C getComponent()
  {
    return componentContext.getComponent();
  }
  
  public static Color removeAlphaFromColor(Color color)
  {
    return new Color(color.getRed(), color.getGreen(), color.getBlue());
  }
  
  /**
   * Tries to set property value through reflection mechanism. If property aimed to represent property of some Swing Component it is recommended that names of this fields and their getters/setters
   * match in Element and represented Swing Component.
   */
  public static void synchronizeProperty(String prop, WComponent wComponent, Object jComponent) throws ContextException
  {
    try
    {
      if (PropertyUtils.isWriteable(jComponent, prop) && PropertyUtils.isReadable(wComponent, prop))
      {
        if (prop.equals(WAbstractComponent.V_BACKGROUND))
        {
          PropertyUtils.setProperty(jComponent, prop, removeAlphaFromColor((Color) PropertyUtils.getProperty(wComponent, prop)));
        }
        else
        {
          PropertyUtils.setProperty(jComponent, prop, PropertyUtils.getProperty(wComponent, prop));
        }
      }
    }
    catch (InvocationTargetException ex)
    {
      throw new ContextException("Error while setting property '" + prop + "' of component '" + wComponent.getName() + "' to its renderer: " + ex.getCause().getMessage(), ex);
    }
    catch (Exception ex1)
    {
      throw new ContextException("Error while setting property '" + prop + "' of component: " + wComponent.getName() + " to its renderer: " + ex1.getMessage(), ex1);
    }
  }
}
