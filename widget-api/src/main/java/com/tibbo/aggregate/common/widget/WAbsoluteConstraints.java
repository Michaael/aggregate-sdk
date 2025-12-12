package com.tibbo.aggregate.common.widget;

import com.tibbo.aggregate.common.widget.component.*;

/**
 * Implements {@link WConstraints} interface and describes component location in container with absolute layout. In absolute layout container child components placed in some space from containers left
 * top corner. This space is set by <code>x</code> and <code>y</code> properties exactly. <code>x</code> means horizontal space and <code>y</code> - vertical. When some child component overlaps
 * another layout wants to know which one will be on top. <code>zOrder</code> property used to make this decision. Layout will show component with greater <code>zOrder</code> on top of component with
 * lower <code>zOrder</code>.
 * 
 * @see WContainer
 */
public class WAbsoluteConstraints implements WConstraints
{
  private int x = 0;
  private int y = 0;
  private int zOrder = 0;
  
  public WAbsoluteConstraints()
  {
    super();
  }
  
  public WAbsoluteConstraints(int x, int y, int zOrder)
  {
    super();
    this.x = x;
    this.y = y;
    this.zOrder = zOrder;
  }
  
  /**
   * Returns components horizontal space from its containers left corner
   * 
   * @return
   */
  public int getX()
  {
    return x;
  }
  
  /**
   * Sets components horizontal space from its containers left corner
   * 
   * @param x
   */
  public void setX(int x)
  {
    this.x = x;
  }
  
  /**
   * Returns components vertical space from its containers top corner
   * 
   * @return
   */
  public int getY()
  {
    return y;
  }
  
  /**
   * Sets components vertical space from its containers top corner
   * 
   * @return
   */
  public void setY(int y)
  {
    this.y = y;
  }
  
  /**
   * Returns zOrder property of containers child component. Layout will show component with greater <code>zOrder</code> on top of component with lower <code>zOrder</code>.
   * 
   * @return
   */
  public int getZOrder()
  {
    return zOrder;
  }
  
  /**
   * Sets zOrder property of containers child component. Layout will show component with greater <code>zOrder</code> on top of component with lower <code>zOrder</code>.
   * 
   * @return
   */
  public void setZOrder(int order)
  {
    zOrder = order;
  }

  @Override
  public int hashCode()
  {
    int result = x;
    result = 31 * result + y;
    result = 31 * result + zOrder;
    return result;
  }

  @Override
  public String toString()
  {
    return "WAbsoluteConstraints{" + "x=" + x + ", y=" + y + ", zOrder=" + zOrder + '}';
  }

  public boolean equals(Object obj)
  {
    if (obj instanceof WAbsoluteConstraints)
    {
      WAbsoluteConstraints another = (WAbsoluteConstraints) obj;
      boolean res = another.getX() == getX();
      res &= another.getY() == getY();
      res &= another.getZOrder() == getZOrder();
      return res;
    }
    return super.equals(obj);
  }
  
  public WAbsoluteConstraints clone()
  {
    try
    {
      return (WAbsoluteConstraints) super.clone();
    }
    catch (CloneNotSupportedException ex)
    {
      throw new IllegalStateException(ex);
    }
  }

  @Override
  public boolean isAbsolute()
  {
    return true;
  }
}
