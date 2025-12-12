package com.tibbo.aggregate.client.guibuilder.renderers;

import java.awt.*;

import javax.swing.*;

/**
 * Description: Extends JPanel and overrides it <code>paint</code> method. It has <code>ph</code> field that stores PaintHelper object used to draw DnD suggestion.
 */
public class PanelWithPaintHelper extends JPanel
{
  /**
   * PaintHelper instance used to draw DnD suggestion.
   * 
   * @see #paint
   */
  private PaintHelper ph;
  
  /**
   * Constructor receives PaintHelper object
   */
  public void setPaintHelper(PaintHelper ph)
  {
    this.ph = ph;
  }
  
  /**
   * Overrides super method to let <code>ph</code> to draw dnd suggestion every time PanelWithPaintHelper being painted
   */
  @Override
  public void paint(Graphics g)
  {
    super.paint(g);
    
    if (ph != null)
    {
      ph.paint(g);
    }
  }
  
  /**
   * <p>
   * Description: Provides all necessary functionality to draw DnD suggestion.
   * </p>
   */
  public interface PaintHelper
  {
    /**
     * Uses <code>graphics</code> object to draw suggested variant.
     */
    public void paint(Graphics g);
  }
}
