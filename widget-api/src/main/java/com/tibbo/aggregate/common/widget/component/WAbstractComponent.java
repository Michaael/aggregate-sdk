package com.tibbo.aggregate.common.widget.component;

import java.awt.*;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.border.*;

import com.tibbo.aggregate.common.*;
import com.tibbo.aggregate.common.datatable.*;
import com.tibbo.aggregate.common.datatable.field.*;
import com.tibbo.aggregate.common.datatable.validator.*;
import com.tibbo.aggregate.common.resource.*;
import com.tibbo.aggregate.common.widget.*;
import com.tibbo.aggregate.common.widget.chart.converters.*;
import com.tibbo.aggregate.common.widget.engine.*;

public abstract class WAbstractComponent extends WComponent
{
  public static final String V_FONT = "font";
  public static final String V_CURSOR = "cursor";
  public static final String V_BORDER = "border";
  public static final String V_OPAQUE = "opaque";
  public static final String V_BACKGROUND = "background";
  public static final String V_FOREGROUND = "foreground";
  public static final String V_TOOLTIP = "toolTipText";
  public static final String V_ENABLED = "enabled";
  public static final String V_VISIBLE = "visible";
  public static final String V_POPUP_MENU = "popupMenu";
  public static final String V_FOCUSABLE = "focusable";
  public static final String V_PIN_POINTS = "pinPoints";
  public static final String V_POPUP_MENU_NAME = "name";
  
  public static final String V_POPUP_MENU_DESCRIPTION = "description";
  public static final String V_POPUP_MENU_ICON = "icon";
  public static final String V_POPUP_MENU_CONDITION = "condition";
  
  public static final TableFormat VFT_POPUP_MENU = new TableFormat(true);
  
  static
  {
    VFT_POPUP_MENU.addField(FieldFormat.create(V_POPUP_MENU_NAME, 'S', Cres.get().getString("name")).setKeyField(true));
    VFT_POPUP_MENU.addField(FieldFormat.create(V_POPUP_MENU_DESCRIPTION, 'S', Cres.get().getString("description")));
    VFT_POPUP_MENU.addField(FieldFormat.create(V_POPUP_MENU_ICON, 'A', Cres.get().getString("icon"), null, true).setEditor(DataFieldFormat.EDITOR_IMAGE));
    FieldFormat<Object> ff = FieldFormat.create(V_POPUP_MENU_CONDITION, 'S', Cres.get().getString("condition"), null, true).setEditor(StringFieldFormat.EDITOR_EXPRESSION);
    VFT_POPUP_MENU.addField(ff);
    VFT_POPUP_MENU.addTableValidator(new TableKeyFieldsValidator());
  }
  
  private boolean enabled;
  
  private boolean visible;
  
  private String toolTipText;
  
  private Color foreground = Color.BLACK;
  
  private Color background = Color.WHITE;
  
  private boolean opaque;
  
  private Border border;
  
  private Font font;
  
  private Integer cursor;
  
  private DataTable popupMenu;
  
  private boolean focusable;
  
  private List<ComponentPinPoint> pinPoints;
  
  public WAbstractComponent(String name, String key, Class contextType, String description, String iconId, String helpId)
  {
    super(name, key, contextType, description, iconId, helpId);
    
    this.enabled = true;
    this.visible = true;
    this.focusable = true;
    setBackground(Color.WHITE);
    setBorder(null);
    popupMenu = new SimpleDataTable(VFT_POPUP_MENU);
    pinPoints = new ArrayList<>();
  }
  
  public WAbstractComponent(String name, String key, Class contextType, String description, String iconId, ImageIcon icon, String helpId)
  {
    this(name, key, contextType, description, iconId, helpId);
    ResourceManager.putIconToCache(iconId + "." + ResourceManager.ICON_FILE_EXTENSION, icon);
  }
  
  @Override
  public WAbstractComponent clone()
  {
    WAbstractComponent clone = (WAbstractComponent) super.clone();
    
    if (border != null)
    {
      clone.border = new FCBorder().clone(border, true);
    }
    
    if (font != null)
    {
      clone.font = new FCFont().clone(font, true);
    }
    
    return clone;
  }
  
  @Override
  public boolean shouldSkipPropertyEncoding(String name)
  {
    return getCustomProperty(name) != null || name.equals(WidgetTemplate.V_BINDINGS);
  }
  
  public Color getBackground()
  {
    return background;
  }
  
  public Border getBorder()
  {
    return border;
  }
  
  public boolean isEnabled()
  {
    return enabled;
  }
  
  public Color getForeground()
  {
    return foreground;
  }
  
  public boolean isOpaque()
  {
    return opaque;
  }
  
  public String getToolTipText()
  {
    return toolTipText;
  }
  
  public Font getFont()
  {
    return font;
  }
  
  public Integer getCursor()
  {
    return cursor;
  }
  
  public void setCursor(Integer cursor)
  {
    Integer o = this.cursor;
    this.cursor = cursor;
    firePropertyChange(V_CURSOR, o, cursor);
  }
  
  public void setToolTipText(String toolTipText)
  {
    String oldTT = this.toolTipText;
    this.toolTipText = toolTipText;
    firePropertyChange(V_TOOLTIP, oldTT, toolTipText);
  }
  
  public void setOpaque(boolean opaque)
  {
    boolean oldOpaque = this.opaque;
    this.opaque = opaque;
    firePropertyChange(V_OPAQUE, oldOpaque, opaque);
  }
  
  public void setForeground(Color foreground)
  {
    Color oldForeground = this.foreground;
    this.foreground = foreground;
    firePropertyChange(V_FOREGROUND, oldForeground, foreground);
  }
  
  public void setEnabled(boolean enabled)
  {
    boolean oldEnabled = this.enabled;
    this.enabled = enabled;
    firePropertyChange(V_ENABLED, oldEnabled, enabled);
  }
  
  public void setBorder(Border border)
  {
    Border oldBorder = this.border;
    this.border = border;
    firePropertyChange(V_BORDER, oldBorder, border);
  }
  
  public void setBackground(Color background)
  {
    Color oldBackground = this.background;
    this.background = background;
    firePropertyChange(V_BACKGROUND, oldBackground, background);
  }
  
  public void setFont(Font font)
  {
    Font oldFont = this.font;
    this.font = font;
    firePropertyChange(V_FONT, oldFont, font);
  }
  
  public boolean isVisible()
  {
    return visible;
  }
  
  public void setVisible(boolean visible)
  {
    boolean oldVisible = this.visible;
    this.visible = visible;
    firePropertyChange(V_VISIBLE, oldVisible, visible);
  }
  
  public DataTable getPopupMenu()
  {
    return popupMenu;
  }
  
  public void setPopupMenu(DataTable popupMenu)
  {
    Object o = this.popupMenu;
    this.popupMenu = popupMenu;
    firePropertyChange(V_POPUP_MENU, o, popupMenu);
  }
  
  public boolean isFocusable()
  {
    return focusable;
  }
  
  public void setFocusable(boolean newValue)
  {
    boolean oldValue = focusable;
    focusable = newValue;
    firePropertyChange(V_FOCUSABLE, oldValue, newValue);
  }
  
  public List<ComponentPinPoint> getPinPoints()
  {
    return pinPoints;
  }
  
  public void setPinPoints(List<ComponentPinPoint> pinPoints)
  {
    Object o = this.pinPoints;
    this.pinPoints = pinPoints;
    firePropertyChange(V_PIN_POINTS, o, pinPoints);
  }
  
  public ComponentPinPoint getComponentPinPoint(String name)
  {
    for (ComponentPinPoint pin : getPinPoints())
    {
      if (pin.getName().equals(name))
        return pin;
    }
    return null;
  }
  
}
