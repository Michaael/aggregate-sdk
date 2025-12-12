package com.tibbo.aggregate.common.widget.context;

import java.awt.*;
import java.util.*;

import com.tibbo.aggregate.common.*;
import com.tibbo.aggregate.common.binding.*;
import com.tibbo.aggregate.common.context.*;
import com.tibbo.aggregate.common.datatable.*;
import com.tibbo.aggregate.common.expression.*;
import com.tibbo.aggregate.common.widget.*;
import com.tibbo.aggregate.common.widget.component.*;

public class WContainerContext<C extends WComponentContext, U extends WContainer> extends WAbstractContext<C, U>
{
  public static final int CONTAINER_DEFAULT_ABSOLUTE_WIDTH = 100;
  public static final int CONTAINER_DEFAULT_ABSOLUTE_HEIGHT = 60;
  
  public static final int SC_NONE = 0;
  public static final int SC_VERTICAL = 1;
  public static final int SC_HORIZONTAL = 2;
  public static final int SC_BOTH = 3;
  
  public static ContainerContextLayoutHelper ABSOLUTE_LAYOUT_HELPER = new ContextAbsoluteLayoutHelper();
  public static ContainerContextLayoutHelper GRID_LAYOUT_HELPER = new ContextGridLayoutHelper();
  
  public static final String GROUP_GRID = ContextUtils.createGroup(ContextUtils.GROUP_DEFAULT, Res.get().getString("wSnapping"));
  
  public static final String V_SCROLLING = "scrolling";
  public static final String V_USE_CUSTOM_SCROLL_BARS = "useCustomScrollBars";
  public static final String V_SCROLL_BAR_PROPERTIES = "scrollBarProperties";
  public static final String V_SCROLLING_AUTO = "scrollingAuto";
  public static final String V_LAYOUT = "layout";
  public static final String V_SHOW_GRID = "showGrid";
  public static final String V_SNAP_TO_GRID = "snapToGrid";
  public static final String V_GRID_STEP = "gridStep";
  public static final String V_SCALING = "scaling";
  public static final String V_MAXIMIZE_SCALABLE = "maximizeScalable";
  
  public static final String V_BUTTON_FOREGROUND = "buttonForeground";
  public static final String V_BUTTON_BACKGROUND = "buttonBackground";
  public static final String V_SLIDER_FOREGROUND = "sliderForeground";
  public static final String V_SLIDER_BACKGROUND = "sliderBackground";
  public static final String V_SCROLL_WIDTH = "scrollWidth";
  
  private static final TableFormat VFT_SHOW_GRID = new TableFormat(1, 1, "<" + V_SHOW_GRID + "><B><A=1>");
  private static final TableFormat VFT_SNAP_TO_GRID = new TableFormat(1, 1, "<" + V_SNAP_TO_GRID + "><B>");
  private static final TableFormat VFT_GRID_STEP = new TableFormat(1, 1,
      FieldFormat.create("<" + V_GRID_STEP + "><I><A=10><V=<L=1 " + Integer.MAX_VALUE + ">>").setExtendableSelectionValues(true).setSelectionValues(gridStepSelectionValues()));
  public static final TableFormat VFT_SCROLLING_AUTO = new TableFormat(1, 1, "<" + V_SCROLLING_AUTO + "><B>");
  private static final TableFormat VFT_SCROLLING = new TableFormat(1, 1);
  private static final TableFormat VFT_USE_CUSTOM_SCROLL_BARS = new TableFormat(1, 1);
  
  static
  {
    VFT_USE_CUSTOM_SCROLL_BARS.addField(FieldFormat.create(V_USE_CUSTOM_SCROLL_BARS, FieldFormat.BOOLEAN_FIELD, Cres.get().getString("wUseCustomScrollBars")).setDefault(false));
    
    String ref = V_SCROLL_BAR_PROPERTIES + "$" + V_BUTTON_FOREGROUND + "#" + DataTableBindingProvider.PROPERTY_ENABLED;
    String exp = "{" + V_USE_CUSTOM_SCROLL_BARS + "}";
    VFT_USE_CUSTOM_SCROLL_BARS.addBinding(ref, exp);
    
    ref = V_SCROLL_BAR_PROPERTIES + "$" + V_BUTTON_BACKGROUND + "#" + DataTableBindingProvider.PROPERTY_ENABLED;
    exp = "{" + V_USE_CUSTOM_SCROLL_BARS + "}";
    VFT_USE_CUSTOM_SCROLL_BARS.addBinding(ref, exp);
    
    ref = V_SCROLL_BAR_PROPERTIES + "$" + V_SLIDER_FOREGROUND + "#" + DataTableBindingProvider.PROPERTY_ENABLED;
    exp = "{" + V_USE_CUSTOM_SCROLL_BARS + "}";
    VFT_USE_CUSTOM_SCROLL_BARS.addBinding(ref, exp);
    
    ref = V_SCROLL_BAR_PROPERTIES + "$" + V_SLIDER_BACKGROUND + "#" + DataTableBindingProvider.PROPERTY_ENABLED;
    exp = "{" + V_USE_CUSTOM_SCROLL_BARS + "}";
    VFT_USE_CUSTOM_SCROLL_BARS.addBinding(ref, exp);
    
    ref = V_SCROLL_BAR_PROPERTIES + "$" + V_SCROLL_WIDTH + "#" + DataTableBindingProvider.PROPERTY_ENABLED;
    exp = "{" + V_USE_CUSTOM_SCROLL_BARS + "}";
    VFT_USE_CUSTOM_SCROLL_BARS.addBinding(ref, exp);
  }
  
  public static final TableFormat VFT_SCROLL_BAR_PROPERTIES = new TableFormat(1, 1);
  
  static
  {
    VFT_SCROLL_BAR_PROPERTIES.addField(FieldFormat.create(V_BUTTON_FOREGROUND, FieldFormat.COLOR_FIELD, Cres.get().getString("wButtonForeground")).setDefault(new Color(160, 160, 160)));
    VFT_SCROLL_BAR_PROPERTIES.addField(FieldFormat.create(V_BUTTON_BACKGROUND, FieldFormat.COLOR_FIELD, Cres.get().getString("wButtonBackground")).setDefault(new Color(105, 105, 105)));
    VFT_SCROLL_BAR_PROPERTIES.addField(FieldFormat.create(V_SLIDER_FOREGROUND, FieldFormat.COLOR_FIELD, Cres.get().getString("wSliderForeground")).setDefault(Color.GRAY));
    VFT_SCROLL_BAR_PROPERTIES.addField(FieldFormat.create(V_SLIDER_BACKGROUND, FieldFormat.COLOR_FIELD, Cres.get().getString("wSliderBackground")).setDefault(Color.WHITE));
    VFT_SCROLL_BAR_PROPERTIES.addField(FieldFormat.create(V_SCROLL_WIDTH, FieldFormat.INTEGER_FIELD, Cres.get().getString("width")).setNullable(true).setDefault(null));
  }
  
  private static final TableFormat VFT_LAYOUT = new TableFormat(1, 1);
  private static final TableFormat VFT_SCALING = new TableFormat(1, 1);
  static
  {
    FieldFormat ff = FieldFormat.create("<" + V_SCROLLING + "><I>");
    Map scValues = new LinkedHashMap<Object, String>();
    scValues.put(SC_NONE, Cres.get().getString("none"));
    scValues.put(SC_VERTICAL, Cres.get().getString("wVertical"));
    scValues.put(SC_HORIZONTAL, Cres.get().getString("wHorizontal"));
    scValues.put(SC_BOTH, Cres.get().getString("wBothHorVert"));
    ff.setSelectionValues(scValues);
    VFT_SCROLLING.addField(ff);
    
    String ref = V_SCROLLING_AUTO + "$" + V_SCROLLING_AUTO + "#" + DataTableBindingProvider.PROPERTY_ENABLED;
    String exp = "{" + V_SCROLLING + "} != " + SC_NONE;
    VFT_SCROLLING.addBinding(ref, exp);
    
    ff = FieldFormat.create("<" + V_LAYOUT + "><I><A=" + WidgetConstants.GRID_LAYOUT + ">");
    Map layouts = new LinkedHashMap<Integer, String>();
    layouts.put(WidgetConstants.GRID_LAYOUT, Cres.get().getString("wGridLayout"));
    layouts.put(WidgetConstants.ABSOLUTE_LAYOUT, Cres.get().getString("wAbsoluteLayout"));
    ff.setSelectionValues(layouts);
    VFT_LAYOUT.addField(ff);
    
    Reference reference1 = new Reference();
    reference1.setEntity(V_SCROLLING);
    reference1.setField(V_SCROLLING);
    reference1.setProperty(DataTableBindingProvider.PROPERTY_ENABLED);
    Reference reference2 = new Reference();
    reference2.setEntity(V_SCROLLING_AUTO);
    reference2.setField(V_SCROLLING_AUTO);
    reference2.setProperty(DataTableBindingProvider.PROPERTY_ENABLED);
    
    Expression expression = new Expression("{" + V_LAYOUT + "}==" + WidgetConstants.GRID_LAYOUT + "?true:false");
    VFT_LAYOUT.addBinding(new Binding(reference1, expression));
    VFT_LAYOUT.addBinding(new Binding(reference2, expression));
    
    ref = V_SHOW_GRID + "$#" + DataTableBindingProvider.PROPERTY_ENABLED;
    exp = "{" + V_LAYOUT + "} == " + WidgetConstants.ABSOLUTE_LAYOUT;
    VFT_LAYOUT.addBinding(ref, exp);
    
    ref = V_GRID_STEP + "$#" + DataTableBindingProvider.PROPERTY_ENABLED;
    VFT_LAYOUT.addBinding(ref, exp);
    
    ref = V_SNAP_TO_GRID + "$#" + DataTableBindingProvider.PROPERTY_ENABLED;
    VFT_LAYOUT.addBinding(ref, exp);
    
    ff = FieldFormat.create("<" + V_SCALING + "><I><A=" + SC_NONE + ">");
    ff.setSelectionValues(scValues);
    VFT_SCALING.addField(ff);
    
    ref = V_SCALING + "$#" + DataTableBindingProvider.PROPERTY_ENABLED;
    exp = "{" + V_LAYOUT + "} == " + WidgetConstants.ABSOLUTE_LAYOUT;
    VFT_LAYOUT.addBinding(ref, exp);
    
    ref = V_MAXIMIZE_SCALABLE + "$#" + DataTableBindingProvider.PROPERTY_ENABLED;
    exp = "{" + V_LAYOUT + "} == " + WidgetConstants.ABSOLUTE_LAYOUT;
    VFT_LAYOUT.addBinding(ref, exp);
    
  }
  
  private static final TableFormat VFT_MAXIMIZE_SCALABLE = new TableFormat(1, 1, "<" + V_MAXIMIZE_SCALABLE + "><B><A=0>");
  
  public static final VariableDefinition SHOW_GRID_VD = new VariableDefinition(V_SHOW_GRID, VFT_SHOW_GRID, true, true, Res.get().getString("wShowGrid"), GROUP_GRID);
  public static final VariableDefinition SNAP_TO_GRID_VD = new VariableDefinition(V_SNAP_TO_GRID, VFT_SNAP_TO_GRID, true, true, Res.get().getString("wSnapToGrid"), GROUP_GRID);
  public static final VariableDefinition GRID_STEP_VD = new VariableDefinition(V_GRID_STEP, VFT_GRID_STEP, true, true, Res.get().getString("wGridStep"), GROUP_GRID);
  public static final VariableDefinition SCROLLING_VD = new VariableDefinition(V_SCROLLING, VFT_SCROLLING, true, true, Cres.get().getString("wScrolling"), ContextUtils.GROUP_DEFAULT);
  public static final VariableDefinition LAYOUT_VD = new VariableDefinition(V_LAYOUT, VFT_LAYOUT, true, true, Cres.get().getString("wLayout"), ContextUtils.GROUP_DEFAULT);
  private static final VariableDefinition SCROLLING_AUTO_VD = new VariableDefinition(V_SCROLLING_AUTO, VFT_SCROLLING_AUTO, true, true, Cres.get().getString("wScrollingAuto"),
      ContextUtils.GROUP_DEFAULT);
  public static final VariableDefinition MAXIMIZE_SCALABLE_VD = new VariableDefinition(V_MAXIMIZE_SCALABLE, VFT_MAXIMIZE_SCALABLE, true, true, Cres.get().getString("wMaximizeScalable"),
      ContextUtils.GROUP_DEFAULT);
  public static final VariableDefinition SCALING_VD = new VariableDefinition(V_SCALING, VFT_SCALING, true, true, Cres.get().getString("wScaling"), ContextUtils.GROUP_DEFAULT);
  static
  {
    LAYOUT_VD.setIndex(INDEX_HIGHEST);
  }
  
  private static final VariableDefinition VD_USE_CUSTOM_SCROLL_BARS = new VariableDefinition(V_USE_CUSTOM_SCROLL_BARS, VFT_USE_CUSTOM_SCROLL_BARS,
      true, true, Cres.get().getString("wUseCustomScrollBars"), ContextUtils.GROUP_DEFAULT);
  
  private static final VariableDefinition VD_SCROLL_BAR_PROPERTIES = new VariableDefinition(V_SCROLL_BAR_PROPERTIES, VFT_SCROLL_BAR_PROPERTIES,
      true, true, Cres.get().getString("wScrollBarProperties"), ContextUtils.GROUP_DEFAULT);
  
  private static Map<Object, String> gridStepSelectionValues()
  {
    return new HashMap<Object, String>()
    {
      {
        put(5, null);
        put(10, null);
      }
    };
  }
  
  public WContainerContext(U component, WidgetTemplate widget)
  {
    super(component, widget);
  }
  
  @Override
  protected void createVariableDefinitions()
  {
    addVariableDefinition(LAYOUT_VD);
    
    super.createVariableDefinitions();
    
    removeVariableDefinition(WAbstractComponent.V_FOREGROUND);
    removeVariableDefinition(WAbstractComponent.V_FONT);
    removeVariableDefinition(WAbstractComponent.V_ENABLED);
    
    removeVariableDefinition(WAbstractComponent.V_FOCUSABLE);
    TableFormat fvf = VFT_FOCUSABLE.clone();
    fvf.getField(WAbstractComponent.V_FOCUSABLE).setDefault(false);
    addVariableDefinition(new VariableDefinition(WAbstractComponent.V_FOCUSABLE, fvf, true, true, Res.get().getString("wFocusable"), ContextUtils.GROUP_DEFAULT));
    
    addVariableDefinition(SHOW_GRID_VD);
    addVariableDefinition(GRID_STEP_VD);
    addVariableDefinition(SNAP_TO_GRID_VD);
    
    addVariableDefinition(SCROLLING_VD);
    addVariableDefinition(SCROLLING_AUTO_VD);
    addVariableDefinition(VD_USE_CUSTOM_SCROLL_BARS);
    addVariableDefinition(VD_SCROLL_BAR_PROPERTIES);
    addVariableDefinition(SCALING_VD);
    addVariableDefinition(MAXIMIZE_SCALABLE_VD);
  }
  
  /**
   * Returns true if two provided WGridConstraints objects has even one common cell. Otherwise returns false.
   */
  public static boolean isInterference(WGridConstraints childCs, WGridConstraints conCs)
  {
    int lastCol = conCs.getGridx() + conCs.getGridwidth() - 1;
    int lastRow = conCs.getGridy() + conCs.getGridheight() - 1;
    int lastCol2 = childCs.getGridx() + childCs.getGridwidth() - 1;
    int lastRow2 = childCs.getGridy() + childCs.getGridheight() - 1;
    boolean onx = childCs.getGridx() >= conCs.getGridx() && childCs.getGridx() <= lastCol;
    onx |= lastCol2 >= conCs.getGridx() && lastCol2 <= lastCol;
    onx |= childCs.getGridx() <= conCs.getGridx() && lastCol2 >= lastCol;
    boolean ony = childCs.getGridy() >= conCs.getGridy() && childCs.getGridy() <= lastRow;
    ony |= lastRow2 >= conCs.getGridy() && lastRow2 <= lastRow;
    ony |= childCs.getGridy() <= conCs.getGridy() && lastRow2 >= lastRow;
    return onx && ony;
  }
  
  @Override
  public void editAcceptedChildVariables(WComponentContext child)
  {
    super.editAcceptedChildVariables(child);
    child.resetConstraintsVD(getComponent().getLayout());
  }
  
  public static ContainerContextLayoutHelper getLayoutHelper(int layout)
  {
    if (layout == WidgetConstants.ABSOLUTE_LAYOUT)
    {
      return ABSOLUTE_LAYOUT_HELPER;
    }
    else
    {
      return GRID_LAYOUT_HELPER;
    }
  }
}
