package com.tibbo.aggregate.common.widget.runtime.util;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

import com.tibbo.aggregate.common.*;
import com.tibbo.aggregate.common.context.*;
import com.tibbo.aggregate.common.data.*;
import com.tibbo.aggregate.common.datatable.*;
import com.tibbo.aggregate.common.expression.*;
import com.tibbo.aggregate.common.widget.*;
import com.tibbo.aggregate.common.widget.component.*;
import com.tibbo.aggregate.common.widget.context.*;

public class WidgetComponentApiRendererUtilities
{
  
  public static void setRendererSize(JComponent renderer, WComponent component)
  {
    int width = component.getWidth();
    int height = component.getHeight();
    if ((width <= 0 && height <= 0) || renderer == null)
    {
      return;
    }
    
    Dimension dSize = renderer.getPreferredSize();
    dSize.width = width > 0 ? width : dSize.width;
    dSize.height = height > 0 ? height : dSize.height;
    
    renderer.setPreferredSize(dSize);
    renderer.setMinimumSize(dSize);
  }
  
  public static void initBasicProperties(WAbstractContext context, WAbstractComponent component, JComponent renderedComponent, ContextManager contextManager)
  {
    if (context.getVariableDefinition(WAbstractComponent.V_ENABLED) != null)
    {
      renderedComponent.setEnabled(component.isEnabled());
    }
    
    if (context.getVariableDefinition(WAbstractComponent.V_VISIBLE) != null)
    {
      renderedComponent.setVisible(component.isVisible());
    }
    
    if (context.getVariableDefinition(WAbstractComponent.V_FOCUSABLE) != null)
    {
      renderedComponent.setFocusable(component.isFocusable());
    }
    
    if (context.getVariableDefinition(WAbstractComponent.V_BACKGROUND) != null)
    {
      Color bg = component.getBackground();
      renderedComponent.setBackground(new Color(bg.getRed(), bg.getGreen(), bg.getBlue()));
    }
    
    if (context.getVariableDefinition(WAbstractComponent.V_TOOLTIP) != null)
    {
      renderedComponent.setToolTipText(component.getToolTipText());
    }
    
    if (context.getVariableDefinition(WAbstractComponent.V_FOREGROUND) != null)
    {
      renderedComponent.setForeground(component.getForeground());
    }
    
    if (context.getVariableDefinition(WAbstractComponent.V_OPAQUE) != null)
    {
      renderedComponent.setOpaque(component.isOpaque());
    }
    
    if (context.getVariableDefinition(WAbstractComponent.V_BORDER) != null)
    {
      Border b = component.getBorder();
      if (b != null)
      {
        renderedComponent.setBorder(b);
      }
    }
    
    if (context.getVariableDefinition(WAbstractComponent.V_FONT) != null)
    {
      Font f = component.getFont();
      if (f != null)
      {
        renderedComponent.setFont(f);
      }
    }
    
    if (context.getVariableDefinition(WAbstractComponent.V_CURSOR) != null)
    {
      Integer c = component.getCursor();
      if (c != null)
      {
        renderedComponent.setCursor(Cursor.getPredefinedCursor(c));
      }
    }
    
    if (context.getVariableDefinition(WAbstractComponent.V_POPUP_MENU) != null)
    {
      setComponentPopupMenu(context, renderedComponent, context.getPath(), contextManager);
    }
  }
  
  public static void setComponentPopupMenu(final WComponentContext context, JComponent renderedComponent, String contextName, ContextManager contextManager)
  {
    if (!(context.getComponent() instanceof WAbstractComponent))
    {
      return;
    }
    
    final WAbstractComponent component = (WAbstractComponent) context.getComponent();
    final DataTable popupMenuTable = component.getPopupMenu();
    
    if (popupMenuTable != null)
    {
      final JPopupMenu popupMenu = createPopupMenu(context, popupMenuTable, contextName, contextManager);
      
      if (popupMenu.getSubElements() != null && popupMenu.getSubElements().length > 0)
        renderedComponent.setComponentPopupMenu(popupMenu);
    }
  }
  
  public static void setComponentPopupMenu(final WComponentContext context, JComponent renderedComponent, ContextManager contextManager)
  {
    setComponentPopupMenu(context, renderedComponent, context.getPath(), contextManager);
  }
  
  public static JPopupMenu createPopupMenu(final WComponentContext context, String contextName)
  {
    WAbstractComponent component = (WAbstractComponent) context.getComponent();
    DataTable popupMenuTable = component.getPopupMenu();
    if (popupMenuTable != null)
    {
      return createPopupMenu(context, popupMenuTable, contextName, (ContextManager) null);
    }
    return null;
  }
  
  public static JPopupMenu createPopupMenu(final WComponentContext context, DataTable popupMenuTable, String contextName, ContextManager contextManager)
  {
    return createPopupMenu(context, popupMenuTable, new JPopupMenu(), contextName, true, contextManager);
  }
  
  public static JPopupMenu createPopupMenu(final WComponentContext context, DataTable popupMenuTable, String contextName, Boolean useDefaulListener)
  {
    return createPopupMenu(context, popupMenuTable, new JPopupMenu(), contextName, useDefaulListener, null);
  }
  
  public static JPopupMenu createPopupMenu(final Context context, DataTable popupMenuTable, JPopupMenu popupMenu, String contextName, Boolean useDefaulListener, ContextManager contextManager)
  {
    if (popupMenu == null)
      popupMenu = new JPopupMenu();
    
    LinkedList<JMenuItem> items = createMenuItems(context, popupMenuTable, contextName, useDefaulListener);
    for (JMenuItem item : items)
      popupMenu.add(item);
    
    if (contextManager != null)
    {
      popupMenu.addPopupMenuListener(new PopupMenuListener()
      {
        @Override
        public void popupMenuWillBecomeVisible(PopupMenuEvent e)
        {
          processMenuItemsConditions(context, popupMenuTable, items, contextManager);
        }
        
        @Override
        public void popupMenuWillBecomeInvisible(PopupMenuEvent e)
        {
        }
        
        @Override
        public void popupMenuCanceled(PopupMenuEvent e)
        {
        }
      });
    }
    
    return popupMenu;
  }
  
  public static LinkedList<JMenuItem> createMenuItems(final Context context, DataTable popupMenuTable, final String contextName, Boolean useDefaulListener)
  {
    LinkedList<JMenuItem> menuItems = new LinkedList<JMenuItem>();
    
    for (DataRecord rec : popupMenuTable)
    {
      menuItems.add(createMenuItem(context, contextName, rec, useDefaulListener));
    }
    return menuItems;
  }
  
  public static void processMenuItemsConditions(final Context context, DataTable popupMenuTable, Collection<JMenuItem> popupMenuItems, ContextManager contextManager)
  {
    Evaluator evaluator = new Evaluator(contextManager, context, null, contextManager.getCallerController());
    
    for (DataRecord rec : popupMenuTable)
    {
      String name = rec.getString(WAbstractComponent.V_POPUP_MENU_NAME);
      for (JMenuItem item : popupMenuItems)
      {
        if (Objects.equals(name, item.getName()))
        {
          item.setVisible(checkCondition(evaluator, rec.getString(WAbstractComponent.V_POPUP_MENU_CONDITION)));
          break;
        }
      }
    }
  }
  
  private static Boolean checkCondition(Evaluator evaluator, String condition)
  {
    if (condition != null && !condition.isEmpty() && evaluator != null)
    {
      Expression conditionExpression = new Expression(condition);
      try
      {
        return evaluator.evaluateToBoolean(conditionExpression);
      }
      catch (Exception ex)
      {
        Log.WIDGETS.warn(ex.getMessage(), ex);
      }
    }
    return true;
  }
  
  public static JMenuItem createMenuItem(Context context, String contextName, DataRecord rec, Boolean useDefaulListener)
  {
    String name = rec.getString(WAbstractComponent.V_POPUP_MENU_NAME);
    String description = rec.getString(WAbstractComponent.V_POPUP_MENU_DESCRIPTION);
    Data iconData = rec.getData(WAbstractComponent.V_POPUP_MENU_ICON);
    
    Icon icon;
    try
    {
      icon = new ImageIcon(iconData.getData(), description);
    }
    catch (Exception e)
    {
      icon = null;
    }
    
    JMenuItem menuItem = new JMenuItem();
    menuItem.setName(name);
    menuItem.setText(description);
    menuItem.setIcon(icon);
    
    if (useDefaulListener)
      menuItem.addActionListener(createMenuListener(context, contextName, name, description, icon));
    return menuItem;
  }
  
  public static void updateMenuListeners(final Context context, DataTable popupMenuTable, final String contextName, Boolean useDefaulListener, Component[] components)
  {
    HashMap<String, AbstractAction> listeners = createMenuListeners(context, popupMenuTable, contextName, true);
    for (Component c : components)
    {
      if (c instanceof JMenuItem)
      {
        JMenuItem item = (JMenuItem) c;
        
        for (ActionListener listener : item.getActionListeners())
          item.removeActionListener(listener);
        item.addActionListener(listeners.get(item.getName()));
      }
    }
  }
  
  public static HashMap<String, AbstractAction> createMenuListeners(final Context context, DataTable popupMenuTable, final String contextName, Boolean useDefaulListener)
  {
    HashMap<String, AbstractAction> menuListeners = new HashMap<String, AbstractAction>();
    for (DataRecord rec : popupMenuTable)
    {
      String name = rec.getString(WAbstractComponent.V_POPUP_MENU_NAME);
      String description = rec.getString(WAbstractComponent.V_POPUP_MENU_DESCRIPTION);
      Data iconData = rec.getData(WAbstractComponent.V_POPUP_MENU_ICON);
      
      Icon icon;
      try
      {
        icon = new ImageIcon(iconData.getData(), description);
      }
      catch (Exception e)
      {
        icon = null;
      }
      
      menuListeners.put(name, createMenuListener(context, contextName, name, description, icon));
    }
    return menuListeners;
  }
  
  public static AbstractAction createMenuListener(final Context context, final String contextName, final String name, final String description, final Icon icon)
  {
    return new AbstractAction(description, icon)
    {
      @Override
      public void actionPerformed(ActionEvent e)
      {
        final DataRecord eventData = new DataRecord(WAbstractContext.EFT_MENU_POPUP, name, contextName);
        context.fireEvent(WAbstractContext.E_MENU_POPUP, eventData.wrap());
      }
    };
  }
  
  public static boolean hasCustomBorder(WComponent component)
  {
    return ((WAbstractComponent) component).getBorder() != null;
  }
  
  public static GridBagConstraints convertConstraintsToGridBag(WGridConstraints cs)
  {
    GridBagConstraints gcs = new GridBagConstraints();
    gcs.gridx = cs.getGridx();
    gcs.gridy = cs.getGridy();
    gcs.gridheight = cs.getGridheight();
    gcs.gridwidth = cs.getGridwidth();
    gcs.anchor = cs.getAnchor();
    gcs.fill = cs.getFill();
    gcs.insets = new Insets(cs.getInsetsTop(), cs.getInsetsLeft(), cs.getInsetsBottom(), cs.getInsetsRight());
    gcs.weightx = cs.getWeightx();
    gcs.weighty = cs.getWeighty();
    return gcs;
  }
}
