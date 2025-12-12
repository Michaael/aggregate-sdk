package com.tibbo.aggregate.common.widget.context;

import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.beans.PropertyChangeEvent;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.event.CaretEvent;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.context.AbstractContext;
import com.tibbo.aggregate.common.context.CallerController;
import com.tibbo.aggregate.common.context.Context;
import com.tibbo.aggregate.common.context.ContextException;
import com.tibbo.aggregate.common.context.ContextUtils;
import com.tibbo.aggregate.common.context.DefaultContextManager;
import com.tibbo.aggregate.common.context.EventDefinition;
import com.tibbo.aggregate.common.context.RequestController;
import com.tibbo.aggregate.common.context.VariableDefinition;
import com.tibbo.aggregate.common.context.VariableSetter;
import com.tibbo.aggregate.common.data.Event;
import com.tibbo.aggregate.common.datatable.DataRecord;
import com.tibbo.aggregate.common.datatable.DataTable;
import com.tibbo.aggregate.common.datatable.DataTableBindingProvider;
import com.tibbo.aggregate.common.datatable.FieldFormat;
import com.tibbo.aggregate.common.datatable.TableFormat;
import com.tibbo.aggregate.common.datatable.validator.TableKeyFieldsValidator;
import com.tibbo.aggregate.common.util.Docs;
import com.tibbo.aggregate.common.widget.Res;
import com.tibbo.aggregate.common.widget.WidgetConstants;
import com.tibbo.aggregate.common.widget.WidgetTemplate;
import com.tibbo.aggregate.common.widget.chart.converters.FCBorder;
import com.tibbo.aggregate.common.widget.chart.converters.FCFont;
import com.tibbo.aggregate.common.widget.component.WAbstractComponent;
import com.tibbo.aggregate.common.widget.engine.ComponentPinPoint;

public class WAbstractContext<C extends WComponentContext, T extends WAbstractComponent> extends WComponentContext<C, T>
{
  public static final int ONSCREEN_KEYBOARD_NONE = 0;
  public static final int ONSCREEN_KEYBOARD_SINGLE_CLICK = 1;
  public static final int ONSCREEN_KEYBOARD_DOUBLE_CLICK = 2;
  
  public static final String V_ONSCREEN_KEYBOARD = "onscreenKeyboard";
  
  // EVENT NAMES
  
  public static final String E_HIDDEN = "hidden";
  public static final String E_SHOWN = "shown";
  public static final String E_RESIZED = "resized";
  public static final String E_MOVED = "moved";
  
  public static final String E_MOUSE_CLICKED = "mouseClicked";
  public static final String E_MOUSE_DOUBLE_CLICKED = "mouseDoubleClicked";
  public static final String E_MOUSE_PRESSED = "mousePressed";
  public static final String E_MOUSE_RELEASED = "mouseReleased";
  public static final String E_MOUSE_ENTERED = "mouseEntered";
  public static final String E_MOUSE_EXITED = "mouseExited";
  public static final String E_MOUSE_OPEN_CHANGE = "mouseOpenChange";
  
  public static final String E_MOUSE_MOVED = "mouseMoved";
  
  public static final String E_MOUSE_WHEEL_MOVED = "mouseWheelMoved";
  
  public static final String E_KEY_TYPED = "keyTyped";
  public static final String E_KEY_PRESSED = "keyPressed";
  public static final String E_KEY_RELEASED = "keyReleased";
  
  public static final String E_FOCUS_GAINED = "focusGained";
  public static final String E_FOCUS_LOST = "focusLost";
  
  public static final String E_CARET_UPDATE = "caretUpdate";
  
  public static final String E_MENU_POPUP = "menuPopup";
  
  // FIELD NAMES
  
  public static final String EF_ID = "id";
  
  public static final String EF_X_ON_SCREEN = "xOnScreen";
  public static final String EF_Y_ON_SCREEN = "yOnScreen";
  public static final String EF_ALT_DOWN = "altDown";
  public static final String EF_ALT_GRAPH_DOWN = "altGraphDown";
  public static final String EF_BUTTON = "button";
  public static final String EF_CLICK_COUNT = "clickCount";
  public static final String EF_CONTROL_DOWN = "controlDown";
  public static final String EF_META_DOWN = "metaDown";
  public static final String EF_MODIFIERS = "modifiers";
  public static final String EF_MODIFIERS_EX = "modifiersEx";
  public static final String EF_POPUP_TRIGGER = "popupTrigger";
  public static final String EF_SHIFT_DOWN = "shiftDown";
  public static final String EF_WHEN = "when";
  public static final String EF_X = "x";
  public static final String EF_Y = "y";
  public static final String EF_WIDTH = "width";
  public static final String EF_HEIGHT = "height";
  
  public static final String EF_SCROLL_AMOUNT = "scrollAmount";
  public static final String EF_SCROLL_TYPE = "scrollType";
  public static final String EF_WHEEL_ROTATION = "wheelRotation";
  
  public static final String EF_ACTION_KEY = "actionKey";
  public static final String EF_KEY_CHAR = "keyChar";
  public static final String EF_KEY_CODE = "keyCode";
  public static final String EF_KEY_LOCATION = "keyLocation";
  
  public static final String EF_TEMPORARY = "temporary";
  
  public static final String EF_DOT = "dot";
  public static final String EF_MARK = "mark";
  
  public static final String EF_ITEM = "item";
  public static final String EF_CONTEXT = "context";
  
  // FORMATS
  
  public static final TableFormat VFT_ENABLED = new TableFormat(1, 1, "<" + WAbstractComponent.V_ENABLED + "><B><A=1>");
  public static final TableFormat VFT_VISIBLE = new TableFormat(1, 1, "<" + WAbstractComponent.V_VISIBLE + "><B><A=1>");
  public static final TableFormat VFT_FOREGROUND = new TableFormat(1, 1, "<" + WAbstractComponent.V_FOREGROUND + "><C><D=" + Cres.get().getString("wForeground") + "><A=" + Color.BLACK.getRGB() + ">");
  public static final TableFormat VFT_BACKGROUND = new TableFormat(1, 1,
      "<" + WAbstractComponent.V_BACKGROUND + "><C><D=" + Cres.get().getString("background") + "><A=" + Color.WHITE.getRGB() + "><F=N>");
  public static final TableFormat VFT_TOOLTIP = new TableFormat(1, 1, "<" + WAbstractComponent.V_TOOLTIP + "><S><F=N>");
  public static final TableFormat VFT_OPAQUE = new TableFormat(1, 1, "<" + WAbstractComponent.V_OPAQUE + "><B>");
  
  static
  {
    String ref = WAbstractComponent.V_BACKGROUND + "$" + WAbstractComponent.V_BACKGROUND + "#" + DataTableBindingProvider.PROPERTY_ENABLED;
    String exp = "{" + WAbstractComponent.V_OPAQUE + "}";
    VFT_OPAQUE.addBinding(ref, exp);
  }
  
  public static final TableFormat VFT_CURSOR = new TableFormat(1, 1, FieldFormat.<Integer> create("<" + WAbstractComponent.V_CURSOR + "><I><F=N>").setSelectionValues(cursorSelectionValues()));
  
  public static final TableFormat VFT_ONSCREEN_KEYBOARD = new TableFormat(1, 1, FieldFormat.<Integer> create("<" + V_ONSCREEN_KEYBOARD + "><I>").setSelectionValues(onscreenKeyboardSelectionValues()));
  public static final TableFormat VFT_FOCUSABLE = new TableFormat(1, 1, "<" + WAbstractComponent.V_FOCUSABLE + "><B><A=true>");
  
  public static final TableFormat EFT_EVENT = new TableFormat(1, 1);
  
  static
  {
    EFT_EVENT.addField('I', EF_ID, Cres.get().getString("id"));
  }
  
  public static final TableFormat EFT_COMPONENT = EFT_EVENT.clone();
  
  public static final TableFormat EFT_RESIZED = EFT_COMPONENT.clone();
  
  static
  {
    EFT_RESIZED.addField('I', EF_WIDTH, Cres.get().getString("width"));
    EFT_RESIZED.addField('I', EF_HEIGHT, Cres.get().getString("height"));
  }
  
  public static final TableFormat EFT_INPUT_EVENT = EFT_EVENT.clone();
  
  static
  {
    EFT_INPUT_EVENT.addField('D', EF_WHEN, Res.get().getString("wWhen"));
    EFT_INPUT_EVENT.addField('I', EF_MODIFIERS, Res.get().getString("wModifiers"));
    EFT_INPUT_EVENT.addField('B', EF_ALT_DOWN, Res.get().getString("wAltDown"));
    EFT_INPUT_EVENT.addField('B', EF_ALT_GRAPH_DOWN, Res.get().getString("wAltGraphDown"));
    EFT_INPUT_EVENT.addField('B', EF_CONTROL_DOWN, Res.get().getString("wControlDown"));
    EFT_INPUT_EVENT.addField('B', EF_SHIFT_DOWN, Res.get().getString("wShiftDown"));
    EFT_INPUT_EVENT.addField('B', EF_META_DOWN, Res.get().getString("wMetaDown"));
  }
  
  public static final TableFormat EFT_MOUSE = EFT_INPUT_EVENT.clone();
  
  static
  {
    EFT_MOUSE.addField('I', EF_X, Res.get().getString("wX"));
    EFT_MOUSE.addField('I', EF_Y, Res.get().getString("wY"));
    EFT_MOUSE.addField('I', EF_X_ON_SCREEN, Res.get().getString("wXOnScreen"));
    EFT_MOUSE.addField('I', EF_Y_ON_SCREEN, Res.get().getString("wYOnScreen"));
    EFT_MOUSE.addField('I', EF_BUTTON, Res.get().getString("wButton"));
    EFT_MOUSE.addField('I', EF_CLICK_COUNT, Res.get().getString("wClickCount"));
    EFT_MOUSE.addField('I', EF_MODIFIERS_EX, Res.get().getString("wModifiersEx"));
    EFT_MOUSE.addField('B', EF_POPUP_TRIGGER, Res.get().getString("wPopupTrigger"));
  }
  
  public static final TableFormat EFT_MOUSE_WHEEL = EFT_MOUSE.clone();
  
  static
  {
    EFT_MOUSE_WHEEL.addField('I', EF_SCROLL_AMOUNT, Res.get().getString("wScrollAmount"));
    EFT_MOUSE_WHEEL.addField('I', EF_SCROLL_TYPE, Res.get().getString("wScrollType"));
    EFT_MOUSE_WHEEL.addField('I', EF_WHEEL_ROTATION, Res.get().getString("wWheelRotation"));
  }
  
  public static final TableFormat EFT_KEY = EFT_INPUT_EVENT.clone();
  
  static
  {
    EFT_KEY.addField('B', EF_ACTION_KEY, Res.get().getString("wActionKey"));
    EFT_KEY.addField('S', EF_KEY_CHAR, Res.get().getString("wKeyChar"));
    EFT_KEY.addField('I', EF_KEY_CODE, Res.get().getString("wKeyCode"));
    EFT_KEY.addField('I', EF_KEY_LOCATION, Res.get().getString("wKeyLocation"));
  }
  
  public static final TableFormat EFT_FOCUS = EFT_EVENT.clone();
  
  static
  {
    EFT_FOCUS.addField('B', EF_TEMPORARY, Res.get().getString("wTemporary"));
  }
  
  public static final TableFormat EFT_CARET_UPDATE = new TableFormat(1, 1);
  
  static
  {
    EFT_CARET_UPDATE.addField('I', EF_DOT, Res.get().getString("wDot"));
    EFT_CARET_UPDATE.addField('I', EF_MARK, Res.get().getString("wMark"));
  }
  
  public static final TableFormat EFT_MENU_POPUP = new TableFormat(1, 1);
  
  static
  {
    EFT_MENU_POPUP.addField('S', EF_ITEM, Res.get().getString("wItem"));
    EFT_MENU_POPUP.addField('S', EF_CONTEXT, Res.get().getString("wContext"), "", true);
  }
  
  // DEFINITIONS
  
  public static VariableDefinition ENABLED_VD = new VariableDefinition(WAbstractComponent.V_ENABLED, VFT_ENABLED, true, true, Cres.get().getString("enabled"), ContextUtils.GROUP_DEFAULT);
  public static VariableDefinition VISIBLE_VD = new VariableDefinition(WAbstractComponent.V_VISIBLE, VFT_VISIBLE, true, true, Cres.get().getString("visible"), ContextUtils.GROUP_DEFAULT);
  public static VariableDefinition FOREGROUND_VD = new VariableDefinition(WAbstractComponent.V_FOREGROUND, VFT_FOREGROUND, true, true, Cres.get().getString("wForeground"), ContextUtils.GROUP_DEFAULT);
  public static VariableDefinition BACKGROUND_VD = new VariableDefinition(WAbstractComponent.V_BACKGROUND, VFT_BACKGROUND, true, true, Cres.get().getString("background"), ContextUtils.GROUP_DEFAULT);
  public static VariableDefinition BORDER_VD = new VariableDefinition(WAbstractComponent.V_BORDER, FCBorder.VFT_BORDER, true, true, Cres.get().getString("border"), ContextUtils.GROUP_DEFAULT);
  public static VariableDefinition OPAQUE_VD = new VariableDefinition(WAbstractComponent.V_OPAQUE, VFT_OPAQUE, true, true, Cres.get().getString("opaque"), ContextUtils.GROUP_DEFAULT);
  
  public static VariableDefinition TOOLTIP_VD = new VariableDefinition(WAbstractComponent.V_TOOLTIP, VFT_TOOLTIP, true, true, Cres.get().getString("tooltip"), ContextUtils.GROUP_DEFAULT);
  public static VariableDefinition CURSOR_VD = new VariableDefinition(WAbstractComponent.V_CURSOR, VFT_CURSOR, true, true, Cres.get().getString("cursor"), ContextUtils.GROUP_DEFAULT);
  public static VariableDefinition FONT_VD = new VariableDefinition(WAbstractComponent.V_FONT, FCFont.FORMAT, true, true, Cres.get().getString("font"), ContextUtils.GROUP_DEFAULT);
  public static VariableDefinition POPUP_MENU_VD = new VariableDefinition(WAbstractComponent.V_POPUP_MENU, WAbstractComponent.VFT_POPUP_MENU, true, true, Res.get().getString("wPopupMenu"),
      ContextUtils.GROUP_DEFAULT);
  public static final VariableDefinition ONSCREEN_KEYBOARD_VD = new VariableDefinition(V_ONSCREEN_KEYBOARD, VFT_ONSCREEN_KEYBOARD, true, true, Res.get().getString("wOnscreenKeyboard"),
      ContextUtils.GROUP_DEFAULT);
  public static VariableDefinition FOCUSABLE_VD = new VariableDefinition(WAbstractComponent.V_FOCUSABLE, VFT_FOCUSABLE, true, true, Res.get().getString("wFocusable"), ContextUtils.GROUP_DEFAULT);
  public static VariableDefinition PIN_POINTS_VD = new VariableDefinition(WAbstractComponent.V_PIN_POINTS, createListFormat(ComponentPinPoint.VFT_PIN), true, true, Res.get().getString("wPinPoints"),
      ContextUtils.GROUP_DEFAULT);
  
  static
  {
    BORDER_VD.setHelpId(Docs.LS_WIDGETS_BORDER);
    FONT_VD.setHelpId(Docs.LS_WIDGETS_FONT);
    
    PIN_POINTS_VD.setValueClass(ComponentPinPoint.class);
  }
  
  public static final EventDefinition ED_HIDDEN = new EventDefinition(E_HIDDEN, EFT_COMPONENT, Res.get().getString("wHidden"));
  public static final EventDefinition ED_SHOWN = new EventDefinition(E_SHOWN, EFT_COMPONENT, Res.get().getString("wShown"));
  public static final EventDefinition ED_MOVED = new EventDefinition(E_MOVED, EFT_COMPONENT, Res.get().getString("wMoved"));
  public static final EventDefinition ED_RESIZED = new EventDefinition(E_RESIZED, EFT_RESIZED, Res.get().getString("wResized"));
  
  public static final EventDefinition ED_MOUSE_CLICKED = new EventDefinition(E_MOUSE_CLICKED, EFT_MOUSE, Res.get().getString("wMouseClicked"));
  public static final EventDefinition ED_MOUSE_DOUBLE_CLICKED = new EventDefinition(E_MOUSE_DOUBLE_CLICKED, EFT_MOUSE, Res.get().getString("wMouseDoubleClicked"));
  public static final EventDefinition ED_MOUSE_PRESSED = new EventDefinition(E_MOUSE_PRESSED, EFT_MOUSE, Res.get().getString("wMousePressed"));
  public static final EventDefinition ED_MOUSE_RELEASED = new EventDefinition(E_MOUSE_RELEASED, EFT_MOUSE, Res.get().getString("wMouseReleased"));
  public static final EventDefinition ED_MOUSE_ENTERED = new EventDefinition(E_MOUSE_ENTERED, EFT_MOUSE, Res.get().getString("wMouseEntered"));
  public static final EventDefinition ED_MOUSE_EXITED = new EventDefinition(E_MOUSE_EXITED, EFT_MOUSE, Res.get().getString("wMouseExited"));
  
  public static final EventDefinition ED_MOUSE_MOVED = new EventDefinition(E_MOUSE_MOVED, EFT_MOUSE, Res.get().getString("wMouseMoved"));
  
  public static final EventDefinition ED_MOUSE_WHEEL_MOVED = new EventDefinition(E_MOUSE_WHEEL_MOVED, EFT_MOUSE_WHEEL, Res.get().getString("wMouseWheelMoved"));
  
  public static final EventDefinition ED_KEY_PRESSED = new EventDefinition(E_KEY_PRESSED, EFT_KEY, Res.get().getString("wKeyPressed"));
  public static final EventDefinition ED_KEY_RELEASED = new EventDefinition(E_KEY_RELEASED, EFT_KEY, Res.get().getString("wKeyReleased"));
  public static final EventDefinition ED_KEY_TYPED = new EventDefinition(E_KEY_TYPED, EFT_KEY, Res.get().getString("wKeyTyped"));
  
  public static final EventDefinition ED_FOCUS_GAINED = new EventDefinition(E_FOCUS_GAINED, EFT_FOCUS, Res.get().getString("wFocusGained"));
  public static final EventDefinition ED_FOCUS_LOST = new EventDefinition(E_FOCUS_LOST, EFT_FOCUS, Res.get().getString("wFocusLost"));
  
  public static final EventDefinition ED_CARET_UPDATE = new EventDefinition(E_CARET_UPDATE, EFT_CARET_UPDATE, Res.get().getString("wCaretUpdate"));
  
  public static final EventDefinition ED_MENU_POPUP = new EventDefinition(E_MENU_POPUP, EFT_MENU_POPUP, Res.get().getString("wMenuPopup"));
  
  // STATIC METHODS
  
  private static Map<Integer, String> cursorSelectionValues()
  {
    final HashMap<Integer, String> m = new HashMap<Integer, String>();
    m.put(Cursor.DEFAULT_CURSOR, Cres.get().getString("wCursorDefault"));
    m.put(Cursor.CROSSHAIR_CURSOR, Cres.get().getString("wCursorCrosshair"));
    m.put(Cursor.TEXT_CURSOR, Cres.get().getString("wCursorText"));
    m.put(Cursor.WAIT_CURSOR, Cres.get().getString("wCursorWait"));
    m.put(Cursor.SW_RESIZE_CURSOR, Cres.get().getString("wCursorSWResize"));
    m.put(Cursor.SE_RESIZE_CURSOR, Cres.get().getString("wCursorSEResize"));
    m.put(Cursor.NW_RESIZE_CURSOR, Cres.get().getString("wCursorNWResize"));
    m.put(Cursor.NE_RESIZE_CURSOR, Cres.get().getString("wCursorNEResize"));
    m.put(Cursor.N_RESIZE_CURSOR, Cres.get().getString("wCursorNResize"));
    m.put(Cursor.S_RESIZE_CURSOR, Cres.get().getString("wCursorSResize"));
    m.put(Cursor.W_RESIZE_CURSOR, Cres.get().getString("wCursorWResize"));
    m.put(Cursor.E_RESIZE_CURSOR, Cres.get().getString("wCursorEResize"));
    m.put(Cursor.HAND_CURSOR, Cres.get().getString("wCursorHand"));
    m.put(Cursor.MOVE_CURSOR, Cres.get().getString("wCursorMove"));
    return m;
  }
  
  public static TableFormat createMapFormat(FieldFormat keyFormat, FieldFormat valueFormat)
  {
    TableFormat res = new TableFormat(true).addField(keyFormat).addField(valueFormat);
    res.addTableValidator(new TableKeyFieldsValidator());
    return res;
  }
  
  public static TableFormat createPlainMapFormat(FieldFormat keyFieldFormat, TableFormat beanTableFormat)
  {
    final TableFormat result = new TableFormat(true).addField(keyFieldFormat).addFields(beanTableFormat.getFields().toArray(new FieldFormat[beanTableFormat.getFieldCount()]));
    result.addTableValidator(new TableKeyFieldsValidator());
    return result;
  }
  
  public static TableFormat createListFormat(TableFormat rf)
  {
    TableFormat res = rf.clone();
    res.resetAllowedRecords();
    res.setReorderable(true);
    return res;
  }
  
  private static Map<Integer, String> onscreenKeyboardSelectionValues()
  {
    return new HashMap<Integer, String>()
    {
      {
        put(ONSCREEN_KEYBOARD_NONE, Res.get().getString("wNone"));
        put(ONSCREEN_KEYBOARD_SINGLE_CLICK, Res.get().getString("wSingleClick"));
        put(ONSCREEN_KEYBOARD_DOUBLE_CLICK, Res.get().getString("wDoubleClick"));
      }
    };
  }
  
  public WAbstractContext(T component, WidgetTemplate widget)
  {
    super(component, widget);
    recreateCustomProperties();
  }
  
  @Override
  public void setupMyself() throws ContextException
  {
    super.setupMyself();
    
    enableVariableStatuses(true);
    
    addEventDefinition(ED_HIDDEN);
    addEventDefinition(ED_SHOWN);
    addEventDefinition(ED_MOVED);
    addEventDefinition(ED_RESIZED);
    
    addEventDefinition(ED_MOUSE_CLICKED);
    addEventDefinition(ED_MOUSE_DOUBLE_CLICKED);
    addEventDefinition(ED_MOUSE_PRESSED);
    addEventDefinition(ED_MOUSE_RELEASED);
    addEventDefinition(ED_MOUSE_ENTERED);
    addEventDefinition(ED_MOUSE_EXITED);
    
    addEventDefinition(ED_MOUSE_MOVED);
    
    addEventDefinition(ED_MOUSE_WHEEL_MOVED);
    
    addEventDefinition(ED_KEY_PRESSED);
    addEventDefinition(ED_KEY_RELEASED);
    addEventDefinition(ED_KEY_TYPED);
    
    addEventDefinition(ED_FOCUS_GAINED);
    addEventDefinition(ED_FOCUS_LOST);
    
    addEventDefinition(ED_MENU_POPUP);
  }
  
  @Override
  protected void createVariableDefinitions()
  {
    super.createVariableDefinitions();
    addVariableDefinition(ENABLED_VD);
    addVariableDefinition(VISIBLE_VD);
    addVariableDefinition(FOREGROUND_VD);
    addVariableDefinition(OPAQUE_VD);
    addVariableDefinition(BACKGROUND_VD);
    addVariableDefinition(BORDER_VD);
    addVariableDefinition(FONT_VD);
    addVariableDefinition(CURSOR_VD);
    addVariableDefinition(TOOLTIP_VD);
    addVariableDefinition(FOCUSABLE_VD);
    addVariableDefinition(PIN_POINTS_VD);
    
    POPUP_MENU_VD.setSetter(new VariableSetter()
    {
      @Override
      public boolean set(Context con, VariableDefinition def, CallerController caller, RequestController request, DataTable value) throws ContextException
      {
        // Creating empty context to check reserved names of variables
        // It allows to avoid using reserved names
        
        AbstractContext emptyContext = new AbstractContext(getRoot().getName())
        {
        };
        
        final DefaultContextManager<Context> contextManager = new DefaultContextManager<Context>(emptyContext, false);
        CallerController cc = contextManager.getCallerController();
        
        List<VariableDefinition> definitionsList = emptyContext.getVariableDefinitions(cc);
        List<String> reservedNames = new ArrayList<String>();
        
        for (VariableDefinition definition : definitionsList)
          reservedNames.add(definition.getName());
        
        for (DataRecord rec : value)
        {
          if (reservedNames.contains(rec.getString("name")))
            throw new ContextException(MessageFormat.format((Res.get().getString("wReservedNameError")), rec.getString("name"), reservedNames));
        }
        
        if (con instanceof WComponentContext)
          ((WAbstractComponent) ((WComponentContext) con).getComponent()).setPopupMenu(value);
        else
          getComponent().setPopupMenu(value);
        return true;
      }
    });
    
    addVariableDefinition(POPUP_MENU_VD);
    
    addVariableDefinition(new VariableDefinition(WAbstractComponent.V_CUSTOM_PROPERTIES, WidgetConstants.CUSTOM_PROPERTIES_FORMAT, true, true, Res.get().getString("wCustomProperties"),
        ContextUtils.GROUP_SYSTEM));
  }
  
  @Override
  public DataTable getVariableDefaultValue(VariableDefinition vd)
  {
    if (vd.getName().equals(WAbstractComponent.V_BORDER))
    {
      return FCBorder.createDefaultTable();
    }
    
    return super.getVariableDefaultValue(vd);
  }
  
  @Override
  public void componentPropertyChanged(PropertyChangeEvent evt)
  {
    super.componentPropertyChanged(evt);
    
    if (evt.getPropertyName().equals(WAbstractComponent.V_CUSTOM_PROPERTIES))
    {
      recreateCustomProperties();
    }
  }
  
  public VariableDefinition addComponentPropertyDefinition(String name, String description, TableFormat format)
  {
    return addComponentPropertyDefinition(name, description, format, null, false);
  }
  
  public VariableDefinition addComponentPropertyDefinition(String name, String description, TableFormat format, Class valueClass)
  {
    return addComponentPropertyDefinition(name, description, format, valueClass, false);
  }
  
  public VariableDefinition addComponentPropertyDefinition(String name, String description, TableFormat format, Class valueClass, boolean defaultProperty)
  {
    return addComponentPropertyDefinition(name, description, format, valueClass, defaultProperty, ContextUtils.GROUP_DEFAULT);
  }
  
  public VariableDefinition addComponentPropertyDefinition(String name, String description, TableFormat format, Class valueClass, boolean defaultProperty, String group)
  {
    VariableDefinition vd = new VariableDefinition(name, format, true, true, description, group);
    if (valueClass != null)
      vd.setValueClass(valueClass);
    if (defaultProperty)
    {
      addDefaultVariableDefinition(vd);
    }
    else
    {
      addVariableDefinition(vd);
    }
    return vd;
  }
  
  // COMPONENT EVENTS
  
  public Event fireHidden(ComponentEvent e)
  {
    return fireComponentEvent(E_HIDDEN, e);
  }
  
  public Event fireShown(ComponentEvent e)
  {
    return fireComponentEvent(E_SHOWN, e);
  }
  
  public Event fireResized(ComponentEvent e)
  {
    return fireComponentEvent(E_RESIZED, e);
  }
  
  public Event fireMoved(ComponentEvent e)
  {
    return fireComponentEvent(E_MOVED, e);
  }
  
  // MOUSE EVENTS
  
  public Event fireMouseClicked(MouseEvent e)
  {
    return fireMouseEvent(E_MOUSE_CLICKED, e);
  }
  
  public Event fireMouseDoubleClicked(MouseEvent e)
  {
    return fireMouseEvent(E_MOUSE_DOUBLE_CLICKED, e);
  }
  
  public Event fireMousePressed(MouseEvent e)
  {
    return fireMouseEvent(E_MOUSE_PRESSED, e);
  }
  
  public Event fireMouseReleased(MouseEvent e)
  {
    return fireMouseEvent(E_MOUSE_RELEASED, e);
    
  }
  
  public Event fireMouseEntered(MouseEvent e)
  {
    return fireMouseEvent(E_MOUSE_ENTERED, e);
    
  }
  
  public Event fireMouseExited(MouseEvent e)
  {
    return fireMouseEvent(E_MOUSE_EXITED, e);
    
  }
  
  // MOUSE MOTION EVENTS
  
  public Event fireMouseMoved(MouseEvent e)
  {
    return fireMouseEvent(E_MOUSE_MOVED, e);
  }
  
  // MOUSE WHEEL EVENTS
  
  public Event fireWheelMoved(MouseWheelEvent e)
  {
    return fireMouseWheelEvent(E_MOUSE_WHEEL_MOVED, e);
  }
  
  // KEY EVENTS
  
  public Event fireKeyTyped(KeyEvent e)
  {
    return fireKeyEvent(E_KEY_TYPED, e);
  }
  
  public Event fireKeyPressed(KeyEvent e)
  {
    return fireKeyEvent(E_KEY_PRESSED, e);
  }
  
  public Event fireKeyReleased(KeyEvent e)
  {
    return fireKeyEvent(E_KEY_RELEASED, e);
  }
  
  // FOCUS EVENTS
  
  public Event fireFocusGained(FocusEvent e)
  {
    final DataRecord eventData = createAWTEventData(e, EFT_FOCUS);
    fillFocusEvent(e, eventData);
    return fireEvent(E_FOCUS_GAINED, eventData.wrap());
  }
  
  // CARET EVENTS
  
  public Event fireCaretUpdate(CaretEvent e)
  {
    Event result = null;
    
    final boolean caretUpdateIsDefined = getEventDefinition(E_CARET_UPDATE) != null;
    if (caretUpdateIsDefined)
    {
      final DataRecord eventData = new DataRecord(EFT_CARET_UPDATE);
      eventData.setValue(EF_DOT, e.getDot());
      eventData.setValue(EF_MARK, e.getMark());
      
      result = fireEvent(E_CARET_UPDATE, eventData.wrap());
    }
    
    return result;
  }
  
  public Event fireFocusLost(FocusEvent e)
  {
    final DataRecord eventData = createAWTEventData(e, EFT_FOCUS);
    fillFocusEvent(e, eventData);
    return fireEvent(E_FOCUS_LOST, eventData.wrap());
  }
  
  protected DataRecord createAWTEventData(AWTEvent e, TableFormat eventDataFormat)
  {
    final DataRecord eventData = new DataRecord(eventDataFormat);
    eventData.setValue(EF_ID, e.getID());
    return eventData;
  }
  
  protected void fillMouseEvent(MouseEvent e, DataRecord eventData)
  {
    fillInputEvent(e, eventData);
    
    eventData.setValue(EF_X, e.getX());
    eventData.setValue(EF_Y, e.getY());
    eventData.setValue(EF_X_ON_SCREEN, e.getXOnScreen());
    eventData.setValue(EF_Y_ON_SCREEN, e.getYOnScreen());
    eventData.setValue(EF_BUTTON, e.getButton());
    eventData.setValue(EF_CLICK_COUNT, e.getClickCount());
    eventData.setValue(EF_MODIFIERS_EX, e.getModifiersEx());
    eventData.setValue(EF_POPUP_TRIGGER, e.isPopupTrigger());
  }
  
  protected void fillInputEvent(InputEvent e, DataRecord eventData)
  {
    eventData.setValue(EF_WHEN, new Date(e.getWhen()));
    eventData.setValue(EF_MODIFIERS, e.getModifiers());
    eventData.setValue(EF_ALT_DOWN, e.isAltDown());
    eventData.setValue(EF_ALT_GRAPH_DOWN, e.isAltGraphDown());
    eventData.setValue(EF_CONTROL_DOWN, e.isControlDown());
    eventData.setValue(EF_SHIFT_DOWN, e.isShiftDown());
    eventData.setValue(EF_META_DOWN, e.isMetaDown());
  }
  
  private void fillFocusEvent(FocusEvent e, DataRecord eventData)
  {
    eventData.setValue(EF_TEMPORARY, e.isTemporary());
  }
  
  private Event fireComponentEvent(String name, ComponentEvent e)
  {
    final DataRecord eventData = createAWTEventData(e, EFT_RESIZED);
    
    if (e.getSource() instanceof Component)
    {
      final Rectangle bounds = ((Component) e.getSource()).getBounds();
      eventData.setValue(EF_WIDTH, bounds.width);
      eventData.setValue(EF_HEIGHT, bounds.height);
    }
    
    return fireEvent(name, eventData.wrap());
  }
  
  protected Event fireMouseEvent(String name, MouseEvent e)
  {
    final DataRecord eventData = createAWTEventData(e, EFT_MOUSE);
    
    fillMouseEvent(e, eventData);
    
    return fireEvent(name, eventData.wrap());
  }
  
  private Event fireMouseWheelEvent(String name, MouseWheelEvent e)
  {
    final DataRecord eventData = createAWTEventData(e, EFT_MOUSE_WHEEL);
    
    fillMouseEvent(e, eventData);
    
    eventData.setValue(EF_SCROLL_AMOUNT, e.getScrollAmount());
    eventData.setValue(EF_SCROLL_TYPE, e.getScrollType());
    eventData.setValue(EF_WHEEL_ROTATION, e.getWheelRotation());
    
    return fireEvent(name, eventData.wrap());
  }
  
  private Event fireKeyEvent(String name, KeyEvent e)
  {
    final DataRecord eventData = createAWTEventData(e, EFT_KEY);
    
    fillInputEvent(e, eventData);
    
    eventData.setValue(EF_ACTION_KEY, e.isActionKey());
    eventData.setValue(EF_KEY_CHAR, String.valueOf(e.getKeyChar()));
    eventData.setValue(EF_KEY_CODE, e.getKeyCode());
    eventData.setValue(EF_KEY_LOCATION, e.getKeyLocation());
    
    return fireEvent(name, eventData.wrap());
  }
  
  public Point getComponentPosition()
  {
    Point coordinate;
    try
    {
      DataTable xCoordinate = this.getVariable(WidgetConstants.V_XCOORDINATE);
      DataTable yCoordinate = this.getVariable(WidgetConstants.V_YCOORDINATE);
      coordinate = new Point(xCoordinate.rec().getInt(WidgetConstants.V_XCOORDINATE), yCoordinate.rec().getInt(WidgetConstants.V_YCOORDINATE));
    }
    catch (ContextException ex)
    {
      coordinate = new Point();
    }
    return coordinate;
  }
}
