package com.tibbo.aggregate.common.widget.runtime.renderers;

import com.tibbo.aggregate.common.widget.context.*;

public interface WidgetComponentRenderer<T extends WComponentContext, U, O extends U>
{
  /**
   * Returns representation of component that contains the renderer component returned by {@link #getRender()} and may be optionally decorated (e.g. with borders) in editor.
   */
  public U getProduction();
  
  /**
   * Returns component context.
   * 
   * @return Component context
   */
  public T getComponentContext();
  
  /**
   * This method is called by widget engine when component property is changed (e.g. by a binding).
   * 
   * @param property
   *          Name of changed property
   */
  public void componentPropertyChanged(String property);
  
  /**
   * Returns the rendered component. For example for Label component in Swing renderer it will be JLabel object.
   */
  public O getRender();
  
  /**
   * This method is called in the end of widget engine startup
   */
  public void start();
  
  /**
   * This method is called in the end of widget engine shutdown
   */
  public void stop();
}
