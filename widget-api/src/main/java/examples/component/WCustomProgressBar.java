package examples.component;

import javax.swing.*;

import com.tibbo.aggregate.common.widget.WidgetConstants.*;
import com.tibbo.aggregate.common.widget.component.*;

public class WCustomProgressBar extends WAbstractComponent
{
  // Current progress bar value
  private int value = 35;
  
  // Current progress bar minimum value
  private int minimum = 0;
  
  // Current progress bar maximum value
  private int maximum = 100;
  
  // Current progress bar orientation
  private int orientation = JProgressBar.HORIZONTAL;
  
  public WCustomProgressBar(String name)
  {
    super(name,
        "CustomProgressBar",
        WCustomProgressBarContext.class,
        "Custom Progress Bar",
        "gb_custom_progress_bar",
        new ImageIcon(WCustomProgressBarContext.class.getResource("gb_custom_progress_bar.png")),
        null);
  }
  
  public int getValue()
  {
    return value;
  }
  
  public void setValue(int value)
  {
    // Saving old variable value
    int oldValue = this.value;
    
    // Setting new variable value
    this.value = value;
    
    // Firing property change event
    firePropertyChange(WCustomProgressBarContext.V_VALUE, oldValue, value);
  }
  
  public int getOrientation()
  {
    return orientation;
  }
  
  public void setOrientation(int orientation)
  {
    int oldOrientation = this.value;
    this.orientation = orientation;
    firePropertyChange(WCustomProgressBarContext.V_ORIENTATION, oldOrientation, orientation);
  }
  
  public int getMinimum()
  {
    return minimum;
  }
  
  public void setMinimum(int minimum)
  {
    int oldMinimum = this.minimum;
    this.minimum = minimum;
    firePropertyChange(WCustomProgressBarContext.V_MINIMUM, oldMinimum, minimum);
  }
  
  public int getMaximum()
  {
    return maximum;
  }
  
  public void setMaximum(int maximum)
  {
    int oldMaximum = this.maximum;
    this.maximum = maximum;
    firePropertyChange(WCustomProgressBarContext.V_MAXIMUM, oldMaximum, maximum);
  }
  
  @Override
  public WCustomProgressBar clone()
  {
    return (WCustomProgressBar) super.clone();
  }
  
  @Override
  public ComponentGroup getComponentGroup()
  {
    // Group for the current component
    return ComponentGroup.CUSTOM;
  }
}
