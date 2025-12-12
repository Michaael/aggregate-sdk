package com.tibbo.aggregate.common.widget;

/**
 * Implements {@link WConstraints} interface and describes component location in container with grid layout. Grid layout means almost the same as swing <code>GridBagLayout</code>. Read
 * <a>http://java.sun.com/javase/6/docs/api/java/awt/GridBagLayout.html</a> about <code>GridBagLayout</code>.
 * 
 * @see WContainer
 */
public class WGridConstraints implements WConstraints, Cloneable
{
  public static final int RELATIVE = -1;
  
  /**
   * Specifies that this component is the last component in its column or row.
   */
  public static final int REMAINDER = 0;
  
  /**
   * Do not resize the component.
   */
  public static final int FILL_NONE = 0;
  
  /**
   * Resize the component both horizontally and vertically.
   */
  public static final int FILL_BOTH = 1;
  
  /**
   * Resize the component horizontally but not vertically.
   */
  public static final int FILL_HORIZONTAL = 2;
  
  /**
   * Resize the component vertically but not horizontally.
   */
  public static final int FILL_VERTICAL = 3;
  
  /**
   * Put the component in the center of its display area.
   */
  public static final int ANCHOR_CENTER = 10;
  
  /**
   * Put the component at the top of its display area, centered horizontally.
   */
  public static final int ANCHOR_NORTH = 11;
  
  /**
   * Put the component at the top-right corner of its display area.
   */
  public static final int ANCHOR_NORTHEAST = 12;
  
  /**
   * Put the component on the right side of its display area, centered vertically.
   */
  public static final int ANCHOR_EAST = 13;
  
  /**
   * Put the component at the bottom-right corner of its display area.
   */
  public static final int ANCHOR_SOUTHEAST = 14;
  
  /**
   * Put the component at the bottom of its display area, centered horizontally.
   */
  public static final int ANCHOR_SOUTH = 15;
  
  /**
   * Put the component at the bottom-left corner of its display area.
   */
  public static final int ANCHOR_SOUTHWEST = 16;
  
  /**
   * Put the component on the left side of its display area, centered vertically.
   */
  public static final int ANCHOR_WEST = 17;
  
  /**
   * Put the component at the top-left corner of its display area.
   */
  public static final int ANCHOR_NORTHWEST = 18;
  
  private int gridx = 0;
  private int gridy = 0;
  private int gridheight = 1;
  private int gridwidth = 1;
  private int anchor = ANCHOR_CENTER;
  private int insetsTop = 0;
  private int insetsBottom = 0;
  private int insetsLeft = 0;
  private int insetsRight = 0;
  private double weightx = 1;
  private double weighty = 1;
  private int fill = FILL_NONE;
  
  public WGridConstraints()
  {
    
  }
  
  public WGridConstraints(int gridx, int gridy)
  {
    this.gridx = gridx;
    this.gridy = gridy;
  }
  
  public WGridConstraints(int gridx, int gridy, boolean fillHorizontal, boolean fillVertical)
  {
    this(gridx, gridy);
    if (fillHorizontal && fillVertical)
    {
      fill = FILL_BOTH;
    }
    else if (fillHorizontal)
    {
      fill = FILL_HORIZONTAL;
    }
    else if (fillVertical)
    {
      fill = FILL_VERTICAL;
    }
    else
    {
      fill = FILL_NONE;
    }
  }
  
  public int getGridx()
  {
    return gridx;
  }
  
  public void setGridx(int gridx)
  {
    this.gridx = gridx;
  }
  
  public int getGridy()
  {
    return gridy;
  }
  
  public void setGridy(int gridy)
  {
    this.gridy = gridy;
  }
  
  public int getGridheight()
  {
    return gridheight;
  }
  
  public void setGridheight(int gridheight)
  {
    this.gridheight = gridheight;
  }
  
  public int getGridwidth()
  {
    return gridwidth;
  }
  
  public void setGridwidth(int gridwidth)
  {
    this.gridwidth = gridwidth;
  }
  
  public int getAnchor()
  {
    return anchor;
  }
  
  public void setAnchor(int anchor)
  {
    this.anchor = anchor;
  }
  
  public int getInsetsTop()
  {
    return insetsTop;
  }
  
  public void setInsetsTop(int insetsTop)
  {
    this.insetsTop = insetsTop;
  }
  
  public int getInsetsBottom()
  {
    return insetsBottom;
  }
  
  public void setInsetsBottom(int insetsBottom)
  {
    this.insetsBottom = insetsBottom;
  }
  
  public int getInsetsLeft()
  {
    return insetsLeft;
  }
  
  public void setInsetsLeft(int insetsLeft)
  {
    this.insetsLeft = insetsLeft;
  }
  
  public int getInsetsRight()
  {
    return insetsRight;
  }
  
  public void setInsetsRight(int insetsRight)
  {
    this.insetsRight = insetsRight;
  }
  
  public double getWeightx()
  {
    return weightx;
  }
  
  public void setWeightx(double weightx)
  {
    this.weightx = weightx;
  }
  
  public double getWeighty()
  {
    return weighty;
  }
  
  public void setWeighty(double weighty)
  {
    this.weighty = weighty;
  }
  
  public int getFill()
  {
    return fill;
  }
  
  public void setFill(int fill)
  {
    this.fill = fill;
  }
  
  public void setInsets(int top, int left, int bottom, int right)
  {
    this.insetsTop = top;
    this.insetsLeft = left;
    this.insetsBottom = bottom;
    this.insetsRight = right;
  }

  @Override
  public int hashCode()
  {
    int result;
    long temp;
    result = gridx;
    result = 31 * result + gridy;
    result = 31 * result + gridheight;
    result = 31 * result + gridwidth;
    result = 31 * result + anchor;
    result = 31 * result + insetsTop;
    result = 31 * result + insetsBottom;
    result = 31 * result + insetsLeft;
    result = 31 * result + insetsRight;
    temp = Double.doubleToLongBits(weightx);
    result = 31 * result + (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(weighty);
    result = 31 * result + (int) (temp ^ (temp >>> 32));
    result = 31 * result + fill;
    return result;
  }

  @Override
  public boolean equals(Object obj)
  {
    if (obj instanceof WGridConstraints)
    {
      WGridConstraints another = (WGridConstraints) obj;
      boolean res = another.getFill() == getFill();
      res &= another.getGridx() == getGridx();
      res &= another.getGridy() == getGridy();
      res &= another.getGridheight() == getGridheight();
      res &= another.getGridwidth() == getGridwidth();
      res &= another.getAnchor() == getAnchor();
      res &= another.getInsetsBottom() == getInsetsBottom();
      res &= another.getInsetsLeft() == getInsetsLeft();
      res &= another.getInsetsRight() == getInsetsRight();
      res &= another.getInsetsTop() == getInsetsTop();
      res &= another.getWeightx() == getWeightx();
      res &= another.getWeighty() == getWeighty();
      return res;
    }
    return super.equals(obj);
  }

  @Override
  public String toString()
  {
    return "WGridConstraints{" + "gridx=" + gridx + ", gridy=" + gridy + ", gridheight=" + gridheight + ", gridwidth=" + gridwidth + ", anchor=" + anchor + ", insetsTop=" + insetsTop
        + ", insetsBottom=" + insetsBottom + ", insetsLeft=" + insetsLeft + ", insetsRight=" + insetsRight + ", weightx=" + weightx + ", weighty=" + weighty + ", fill=" + fill + '}';
  }

  @Override
  public WGridConstraints clone()
  {
    try
    {
      return (WGridConstraints) super.clone();
    }
    catch (CloneNotSupportedException ex)
    {
      throw new IllegalStateException(ex.getMessage(), ex);
    }
  }

  @Override
  public boolean isAbsolute()
  {
    return false;
  }

  /**
   * If provided constraints object is instance of WGridConstraints copies all constraints properties from it except such properties as gridx, gridy, gridWidth, gridHeight.
   * 
   * @param anotherCs
   */
  public void copyNoneCoordinateProperties(WConstraints anotherCs)
  {
    if (!(anotherCs instanceof WGridConstraints))
    {
      return;
    }
    
    WGridConstraints anotherGridCs = (WGridConstraints) anotherCs;
    setInsets(anotherGridCs.getInsetsTop(), anotherGridCs.getInsetsLeft(), anotherGridCs.getInsetsBottom(), anotherGridCs.getInsetsRight());
    setWeightx(anotherGridCs.getWeightx());
    setWeighty(anotherGridCs.getWeighty());
    setFill(anotherGridCs.getFill());
    setAnchor(anotherGridCs.getAnchor());
  }
}
