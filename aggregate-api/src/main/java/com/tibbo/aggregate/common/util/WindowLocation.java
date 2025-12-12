package com.tibbo.aggregate.common.util;

import java.awt.*;

import com.tibbo.aggregate.common.*;
import com.tibbo.aggregate.common.datatable.*;
import com.tibbo.aggregate.common.datatable.validator.*;
import com.tibbo.aggregate.common.expression.*;

public class WindowLocation extends AggreGateBean
{
  public static final String FIELD_STATE = "state";
  public static final String FIELD_SIDE = "side";
  public static final String FIELD_INDEX = "index";
  public static final String FIELD_WIDTH = "width";
  public static final String FIELD_HEIGHT = "height";
  public static final String FIELD_RESIZABLE = "resizable";
  public static final String FIELD_CLOSABLE = "closable";
  public static final String FIELD_MOVABLE = "movable";
  public static final String FIELD_FLOATABLE = "floatable";
  public static final String FIELD_MAXIMIZABLE = "maximizable";
  public static final String FIELD_AUTOHIDABLE = "autohidable";
  public static final String FIELD_SHOW_TITLE_BAR = "showTitleBar";
  
  public static final String FIELD_KEY = "key";
  public static final String FIELD_ELEMENT_INDEX = "elementIndex";
  
  public static final int STATE_DOCKED = 0;
  public static final int STATE_FLOATING = 1;
  public static final int STATE_SIDE_BAR = 2;
  
  public static final int SIDE_TOP = 0;
  public static final int SIDE_LEFT = 1;
  public static final int SIDE_BOTTOM = 2;
  public static final int SIDE_RIGHT = 3;
  // public static final int SIDE_CENTER = 4;
  // public static final int SIDE_UNKNOWN = 5;
  
  public static TableFormat FORMAT = new TableFormat(1, 1);
  
  static
  {
    FieldFormat ff = FieldFormat.create("<" + FIELD_STATE + "><I><A=" + STATE_DOCKED + "><D=" + Cres.get().getString("state") + ">");
    ff.addSelectionValue(STATE_DOCKED, Cres.get().getString("clStateDocked"));
    ff.addSelectionValue(STATE_FLOATING, Cres.get().getString("clStateFloating"));
    ff.addSelectionValue(STATE_SIDE_BAR, Cres.get().getString("clStateSideBar"));
    FORMAT.addField(ff);
    
    ff = FieldFormat.create("<" + FIELD_SIDE + "><I><A=" + SIDE_TOP + "><D=" + Cres.get().getString("side") + ">");
    ff.addSelectionValue(SIDE_TOP, Cres.get().getString("clSideTop"));
    ff.addSelectionValue(SIDE_LEFT, Cres.get().getString("clSideLeft"));
    ff.addSelectionValue(SIDE_BOTTOM, Cres.get().getString("clSideBottom"));
    ff.addSelectionValue(SIDE_RIGHT, Cres.get().getString("clSideRight"));
    // ff.addSelectionValue(SIDE_CENTER, Cres.get().getString("clSideCenter"));
    // ff.addSelectionValue(SIDE_UNKNOWN, Cres.get().getString("clSideUnknown"));
    FORMAT.addField(ff);
    
    FORMAT.addField(FieldFormat.create("<" + FIELD_INDEX + "><I><D=" + Cres.get().getString("index") + ">"));
    
    ff = FieldFormat.create("<" + FIELD_WIDTH + "><I><F=N><D=" + Cres.get().getString("width") + ">");
    ff.addValidator(ValidatorHelper.NON_ZERO_LENGTH_VALIDATOR);
    FORMAT.addField(ff);
    
    ff = FieldFormat.create("<" + FIELD_HEIGHT + "><I><F=N><D=" + Cres.get().getString("height") + ">");
    ff.addValidator(ValidatorHelper.NON_ZERO_LENGTH_VALIDATOR);
    FORMAT.addField(ff);
    
    FORMAT.addField(FieldFormat.create("<" + FIELD_RESIZABLE + "><B><A=1><D=" + Cres.get().getString("clWindowResizable") + ">"));
    FORMAT.addField(FieldFormat.create("<" + FIELD_CLOSABLE + "><B><A=1><D=" + Cres.get().getString("clWindowClosable") + ">"));
    FORMAT.addField(FieldFormat.create("<" + FIELD_MOVABLE + "><B><A=1><D=" + Cres.get().getString("clWindowMovable") + ">"));
    FORMAT.addField(FieldFormat.create("<" + FIELD_FLOATABLE + "><B><A=1><D=" + Cres.get().getString("clWindowFloatable") + ">"));
    FORMAT.addField(FieldFormat.create("<" + FIELD_MAXIMIZABLE + "><B><A=1><D=" + Cres.get().getString("clWindowMaximizable") + ">"));
    FORMAT.addField(FieldFormat.create("<" + FIELD_AUTOHIDABLE + "><B><A=1><D=" + Cres.get().getString("clWindowAutohidable") + ">"));
    FORMAT.addField(FieldFormat.create("<" + FIELD_SHOW_TITLE_BAR + "><B><A=1><D=" + Cres.get().getString("clWindowShowTitleBar") + ">"));
    FORMAT.addField(FieldFormat.create("<" + FIELD_KEY + "><S><F=A><D=" + Cres.get().getString("key") + ">"));
    FORMAT.addField(FieldFormat.create("<" + FIELD_ELEMENT_INDEX + "><I><F=A>").setNullable(true).setDefault(null).setHidden(true));
    
    FORMAT.addBinding(FIELD_SIDE + "#" + DataTableBindingProvider.PROPERTY_ENABLED, "{" + FIELD_STATE + "} != " + STATE_FLOATING);
    
    FORMAT.addBinding(FIELD_FLOATABLE + "#" + DataTableBindingProvider.PROPERTY_ENABLED, "{" + FIELD_MOVABLE + "} && {" + FIELD_STATE + "} != " + STATE_FLOATING);
    FORMAT.addBinding(FIELD_MAXIMIZABLE + "#" + DataTableBindingProvider.PROPERTY_ENABLED, "{" + FIELD_MOVABLE + "}");
    FORMAT.addBinding(FIELD_AUTOHIDABLE + "#" + DataTableBindingProvider.PROPERTY_ENABLED, "{" + FIELD_MOVABLE + "}");
    
    String state = "{" + FIELD_STATE + "#" + DefaultReferenceResolver.SELECTION_VALUE_DESCRIPTION + "}";
    String side = "({" + FIELD_STATE + "} != " + STATE_FLOATING + " ? (', ' + {" + FIELD_SIDE + "#" + DefaultReferenceResolver.SELECTION_VALUE_DESCRIPTION + "}) : '')";
    String index = "({" + FIELD_STATE + "} != " + STATE_FLOATING + " ? (' (' + {" + FIELD_INDEX + "} + ')') : '')";
    
    FORMAT.setNamingExpression(state + "+" + side + "+" + index);
  }
  
  private int state;
  private int side;
  private int index;
  private Integer width;
  private Integer height;
  private boolean resizable;
  private boolean closable;
  private boolean movable;
  private boolean floatable;
  private boolean maximizable;
  private boolean autohidable;
  private boolean showTitleBar;
  private String key;
  private Integer elementIndex;
  
  public WindowLocation()
  {
    super(FORMAT);
  }
  
  public WindowLocation(int state)
  {
    this();
    this.state = state;
  }
  
  public WindowLocation(Dimension size)
  {
    this();
    this.state = STATE_FLOATING;
    this.width = size.width;
    this.height = size.height;
  }
  
  public WindowLocation(int side, int index)
  {
    this();
    this.state = STATE_DOCKED;
    this.side = side;
    this.index = index;
  }
  
  public WindowLocation(int side, int index, Dimension size)
  {
    this(STATE_DOCKED, side, index, size);
  }
  
  public WindowLocation(int column, Dimension size)
  {
    this(STATE_DOCKED, SIDE_TOP, column, size);
  }
  
  public WindowLocation(int state, int side, int index)
  {
    this();
    this.state = state;
    this.side = side;
    this.index = index;
  }
  
  public WindowLocation(int state, int side, int index, Dimension size)
  {
    this();
    this.state = state;
    this.side = side;
    this.index = index;
    if (size != null)
    {
      this.width = size.width;
      this.height = size.height;
    }
  }
  
  public WindowLocation(DataRecord data)
  {
    super(FORMAT, data);
  }
  
  public void applyDefaultSize(Dimension defaultSize)
  {
    if (getWidth() == null || getWidth() == 0)
    {
      setWidth(defaultSize.width);
    }
    if (getHeight() == null || getHeight() == 0)
    {
      setHeight(defaultSize.height);
    }
  }
  
  public int getState()
  {
    return state;
  }
  
  public void setState(int state)
  {
    this.state = state;
  }
  
  public int getSide()
  {
    return side;
  }
  
  public void setSide(int side)
  {
    this.side = side;
  }
  
  public int getIndex()
  {
    return index;
  }
  
  public void setIndex(int index)
  {
    this.index = index;
  }
  
  public Integer getWidth()
  {
    return width;
  }
  
  public void setWidth(Integer width)
  {
    this.width = width;
  }
  
  public Integer getHeight()
  {
    return height;
  }
  
  public void setHeight(Integer height)
  {
    this.height = height;
  }
  
  public boolean isResizable()
  {
    return resizable;
  }
  
  public void setResizable(boolean resizable)
  {
    this.resizable = resizable;
  }
  
  public boolean isClosable()
  {
    return closable;
  }
  
  public void setClosable(boolean closable)
  {
    this.closable = closable;
  }
  
  public boolean isMovable()
  {
    return movable;
  }
  
  public void setMovable(boolean movable)
  {
    this.movable = movable;
  }
  
  public boolean isFloatable()
  {
    return floatable;
  }
  
  public void setFloatable(boolean floatable)
  {
    this.floatable = floatable;
  }
  
  public boolean isMaximizable()
  {
    return maximizable;
  }
  
  public void setMaximizable(boolean maximizable)
  {
    this.maximizable = maximizable;
  }
  
  public boolean isAutohidable()
  {
    return autohidable;
  }
  
  public void setAutohidable(boolean autohidable)
  {
    this.autohidable = autohidable;
  }
  
  public boolean isShowTitleBar()
  {
    return showTitleBar;
  }
  
  public void setShowTitleBar(boolean showTitleBar)
  {
    this.showTitleBar = showTitleBar;
  }
  
  public String getKey()
  {
    return key;
  }
  
  public void setKey(String key)
  {
    this.key = key;
  }
  
  public Integer getElementIndex()
  {
    return elementIndex;
  }
  
  public void setElementIndex(Integer elementIndex)
  {
    this.elementIndex = elementIndex;
  }
  
  @Override
  public String toString()
  {
    return "Window Location [state=" + state + ", side=" + side + ", index=" + index + ", width=" + width + ", height=" + height + "]";
  }
  
}
