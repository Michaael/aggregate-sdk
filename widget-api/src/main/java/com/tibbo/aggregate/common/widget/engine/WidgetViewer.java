package com.tibbo.aggregate.common.widget.engine;

import java.util.*;

import javax.swing.*;

import com.tibbo.aggregate.common.context.*;
import com.tibbo.aggregate.common.widget.component.*;
import com.tibbo.aggregate.common.widget.context.*;

/**
 * <p>
 * Title: WidgetViewer
 * </p>
 * 
 * <p>
 * Description: Responds for visual representation of application.
 * </p>
 */
public interface WidgetViewer<U>
{
  /**
   * Method that returns JComponent representing application view.
   */
  public JComponent getMainTemlateComponent();
  
  /**
   * Returns <code>WComponentContext</code> object corresponding provided component name
   * 
   * @param name
   *          component name
   * @return corresponding <code>WComponentContext</code> object
   */
  public WComponentContext getComponentContextByName(String name);
  
  /**
   * Method that creates WComponentContext object corresponding given component.
   */
  public WComponentContext createNewComponentContext(WComponent component);
  
  /**
   * Getting list of child resources for <code>container</code>s resource. Creating a list of WContainerContext objects corresponding this resources.
   */
  public List<WComponentContext> getChildComponentContexts(WContainerContext container);
  
  /**
   * Returns whole collection of Elements in GEViewer1
   */
  public Collection<WComponentContext> getComponentContexts();
  
  /**
   * Returns JComponent Representation for this Component context.
   */
  public JComponent getComponentRepresentation(WComponentContext context);
  
  /**
   * Returns JComponent Representation for this component.
   */
  public JComponent getComponentRepresentation(WComponent component);
  
  /**
   * Returns JComponent Representation for this component.
   */
  public JComponent getComponentRepresentation(String component);
  
  /**
   * Returns renderer of a component mapped by the specified context.
   */
  public U getComponentRenderer(WComponentContext context);
  
  /**
   * Returns renderer of a component mapped by the specified component.
   */
  public U getComponentRenderer(WComponent component);
  
  /**
   * Returns renderer of a specific component.
   */
  public U getComponentRenderer(String component);
  
  /**
   * Returns context manager that controls component context tree.
   */
  DefaultContextManager<WComponentContext> getComponentContextManager();
  
  /**
   * Returns provided component context parent component context if exists.
   */
  public WContainerContext getParentContext(WComponentContext child);
  
  /**
   * Removes all Components. This step is necessary if new template created or before loading template from any source.
   */
  public void emptyContents(boolean createRoot);
  
  /**
   * Calls start method in each component renderer.
   */
  public void startRenderers();
  
  /**
   * Stops the viewer and its component context manager.
   */
  public void stop();
  
  /**
   * Calls stop method in each component renderer.
   */
  public void stopRenderers();
  
  public WComponentContext removeComponentContext(String component);
  
  public WComponentContext getRootContext();
}
