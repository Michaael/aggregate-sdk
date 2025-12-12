package com.tibbo.aggregate.common.widget.engine;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import com.tibbo.aggregate.common.datatable.AggreGateBean;
import com.tibbo.aggregate.common.datatable.DataRecord;
import com.tibbo.aggregate.common.datatable.FieldFormat;
import com.tibbo.aggregate.common.datatable.TableFormat;
import com.tibbo.aggregate.common.datatable.validator.LimitsValidator;
import com.tibbo.aggregate.common.expression.ExpressionUtils;
import com.tibbo.aggregate.common.expression.function.DefaultFunctions;
import com.tibbo.aggregate.common.widget.Res;
import com.tibbo.aggregate.common.widget.WGridConstraints;

public class ComponentPinPoint extends AggreGateBean
{
  public final static String STANDARD_NAME = "pin";
  public static final String V_PIN_NAME = "name";
  public static final String V_PIN_TYPE = "type";
  public static final String V_PIN_Y_COMPONENT_COORDINATE = "pinYCoordinate";
  public static final String V_PIN_X_COMPONENT_COORDINATE = "pinXCoordinate";
  
  public static final int NONE = -1;
  public static final int INPUT = 0;
  public static final int IN_OUT = 1;
  public static final int OUTPUT = 2;
  
  public final static float DELTA = 0.1f;
  
  public static final TableFormat VFT_PIN = new TableFormat(true);
  
  static
  {
    FieldFormat ff = FieldFormat.create(V_PIN_NAME, FieldFormat.STRING_FIELD, Res.get().getString("wPinName"));
    ff.setNullable(true);
    ff.setKeyField(true);
    VFT_PIN.addField(ff);
    ff = FieldFormat.create(V_PIN_TYPE, FieldFormat.INTEGER_FIELD, Res.get().getString("wPinType")).setDefault(NONE);
    ff.addSelectionValue(INPUT, Res.get().getString("wInput"));
    ff.addSelectionValue(OUTPUT, Res.get().getString("wOutput"));
    ff.addSelectionValue(IN_OUT, Res.get().getString("wInOut"));
    ff.addSelectionValue(NONE, Res.get().getString("wNone"));
    VFT_PIN.addField(ff);
    ff = FieldFormat.create(V_PIN_X_COMPONENT_COORDINATE, 'F', Res.get().getString("wPinXPosition"));
    ff.addValidator(new LimitsValidator(0f, 1f));
    VFT_PIN.addField(ff);
    ff = FieldFormat.create(V_PIN_Y_COMPONENT_COORDINATE, 'F', Res.get().getString("wPinYPosition"));
    ff.addValidator(new LimitsValidator(0f, 1f));
    VFT_PIN.addField(ff);
    
    String exp = "{" + V_PIN_NAME + "}==" + ExpressionUtils.NULL_PARAM + " ? " + DefaultFunctions.LONG + "(" + DefaultFunctions.RANDOM + "()*" + Long.MAX_VALUE + ") : {" + V_PIN_NAME + "}";
    VFT_PIN.addBinding(V_PIN_NAME, exp);
  }
  
  private String name;
  
  private float pinXCoordinate;
  private float pinYCoordinate;
  
  private int type;
  
  public ComponentPinPoint(float pinXCoordinate, float pinYCoordinate)
  {
    this(pinXCoordinate, pinYCoordinate, STANDARD_NAME, NONE);
  }
  
  public ComponentPinPoint()
  {
    super(VFT_PIN);
  }
  
  public ComponentPinPoint(DataRecord data)
  {
    super(VFT_PIN, data);
  }
  
  public ComponentPinPoint(float pinXCoordinate, float pinYCoordinate, String name, int type)
  {
    super(VFT_PIN);
    this.pinXCoordinate = pinXCoordinate;
    this.pinYCoordinate = pinYCoordinate;
    this.name = name;
    this.type = type;
  }
  
  public String getName()
  {
    return name;
  }
  
  public void setName(String name)
  {
    this.name = name;
  }
  
  public int getType()
  {
    return type;
  }
  
  public void setType(int type)
  {
    this.type = type;
  }
  
  public Point getAbsolutePosition(Point componentCoordinate, int width, int height)
  {
    AffineTransform at = new AffineTransform();
    at.translate(componentCoordinate.x, componentCoordinate.y);
    at.scale(width, height);
    Point2D res = at.transform(new Point2D.Float(pinXCoordinate, pinYCoordinate), null);
    return new Point((int) res.getX(), (int) res.getY());
  }
  
  public void setPositionFromAbsolute(Point absolutePosition, int width, int heght)
  {
    Point2D.Float p = new Point2D.Float(absolutePosition.x / (float) width, absolutePosition.y / (float) heght);
    setPinXCoordinate(p.x);
    setPinYCoordinate(p.y);
  }
  
  public int getAnchorLocation()
  {
    double x = pinXCoordinate;
    double y = pinYCoordinate;
    
    if (x >= DELTA && x <= 1.0f - DELTA && y <= DELTA)
      return WGridConstraints.ANCHOR_NORTH;
    if (x < DELTA && x > y)
      return WGridConstraints.ANCHOR_NORTH;
    if (x > 1.0f - DELTA && 1.0f - x < y && y < DELTA)
      return WGridConstraints.ANCHOR_NORTH;
    
    if (x >= DELTA && x <= 1.0f - DELTA && y >= 1.0f - DELTA)
      return WGridConstraints.ANCHOR_SOUTH;
    if (x < DELTA && x > 1.0f - y)
      return WGridConstraints.ANCHOR_SOUTH;
    if (x > 1.0f - DELTA && x < y)
      return WGridConstraints.ANCHOR_SOUTH;
    
    if (y >= DELTA && y <= 1.0f - DELTA && x <= DELTA)
      return WGridConstraints.ANCHOR_WEST;
    if (y < DELTA && x <= y)
      return WGridConstraints.ANCHOR_WEST;
    if (y > 1.0f - DELTA && 1.0f - y <= x)
      return WGridConstraints.ANCHOR_WEST;
    
    if (y >= DELTA && y <= 1.0f - DELTA && x >= 1.0f - DELTA)
      return WGridConstraints.ANCHOR_EAST;
    if (y < DELTA && 1.0f - x <= y)
      return WGridConstraints.ANCHOR_EAST;
    if (y > 100 - DELTA && y <= x)
      return WGridConstraints.ANCHOR_EAST;
    
    return WGridConstraints.ANCHOR_CENTER;
  }
  
  @Override
  public String toString()
  {
    return "ComponentPinPoint{" +
        "name='" + name + '\'' +
        ", pinXCoordinate=" + pinXCoordinate +
        ", pinYCoordinate=" + pinYCoordinate +
        ", type=" + type +
        '}';
  }
  
  @Override
  public boolean equals(Object o)
  {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    
    ComponentPinPoint that = (ComponentPinPoint) o;
    
    if (Float.compare(that.pinXCoordinate, pinXCoordinate) != 0)
      return false;
    if (Float.compare(that.pinYCoordinate, pinYCoordinate) != 0)
      return false;
    if (type != that.type)
      return false;
    return name.equals(that.name);
  }
  
  @Override
  public int hashCode()
  {
    int result = name.hashCode();
    result = 31 * result + (pinXCoordinate != +0.0f ? Float.floatToIntBits(pinXCoordinate) : 0);
    result = 31 * result + (pinYCoordinate != +0.0f ? Float.floatToIntBits(pinYCoordinate) : 0);
    result = 31 * result + type;
    return result;
  }
  
  public float getPinYCoordinate()
  {
    return pinYCoordinate;
  }
  
  public void setPinYCoordinate(float pinYCoordinate)
  {
    this.pinYCoordinate = pinYCoordinate;
  }
  
  public float getPinXCoordinate()
  {
    return pinXCoordinate;
  }
  
  public void setPinXCoordinate(float pinXCoordinate)
  {
    this.pinXCoordinate = pinXCoordinate;
  }
}
