package com.tibbo.aggregate.common.widget.runtime.renderers;

import javax.swing.*;

public interface RendererSupport<O extends JComponent>
{
  public void resetRender();
  
  public O getProduction();
  
  public void initView();
  
  public void componentPropertyChanged(String property);
  
  public boolean isInteractive();
  
  public void stop();
  
  public void start();
}
