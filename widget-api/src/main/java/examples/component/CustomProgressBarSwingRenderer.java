package examples.component;

import java.awt.*;

import javax.swing.*;
import javax.swing.event.*;

import com.tibbo.aggregate.common.widget.runtime.*;
import com.tibbo.aggregate.common.widget.runtime.renderers.*;

public class CustomProgressBarSwingRenderer extends DefaultSwingComponentRenderer<WCustomProgressBar, WCustomProgressBarContext, JProgressBar, SwingRendererSupport>
{
  public CustomProgressBarSwingRenderer(WCustomProgressBarContext progressBarContext, SwingWidgetEngine engine)
  {
    super(progressBarContext, engine);
  }
  
  @Override
  public JProgressBar createRender()
  {
    // Creating JProgressBar
    final JProgressBar progressBar = new JProgressBar();
    
    // Setting progress bar orientation
    progressBar.setOrientation(getComponent().getOrientation());
    
    // Setting progress bar minimum value
    progressBar.setMinimum(getComponent().getMinimum());
    
    // Setting progress bar maximum value
    progressBar.setMaximum(getComponent().getMaximum());
    
    // Setting progress bar value
    progressBar.setValue(getComponent().getValue());
    
    // Enabling progress string
    progressBar.setStringPainted(true);
    
    // Setting progress bar color
    progressBar.setForeground(getColor((float) progressBar.getPercentComplete()));
    
    if (getRendererSupport().isInteractive())
    {
      // Adding change listener
      progressBar.addChangeListener(new ChangeListener()
      {
        @Override
        public void stateChanged(ChangeEvent e)
        {
          // Setting component value if state is changed
          getComponent().setValue(progressBar.getValue());
        }
      });
    }
    
    return progressBar;
  }
  
  @Override
  public void componentPropertyChanged(String property)
  {
    // React if component property is changed
    super.componentPropertyChanged(property);
    
    // Execute additional actions if changed property is "value"
    if (property.equals(WCustomProgressBarContext.V_VALUE))
    {
      // Updating progress bar color according current value
      getRender().setForeground(getColor());
      
      // Updating renderer after changes
      getRendererSupport().renderChanged();
    }
  }
  
  public Color getColor()
  {
    float percentComplete = (float) getRender().getPercentComplete();
    return getColor(percentComplete);
  }
  
  public Color getColor(float power)
  {
    float Hue = power * 0.4f; // 0.4 = Green
    float Saturation = 1;
    float Brightness = 1;
    return Color.getHSBColor(Hue, Saturation, Brightness);
  }
}
