package com.tibbo.aggregate.common.widget.context;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.text.MessageFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.Log;
import com.tibbo.aggregate.common.binding.Bindings;
import com.tibbo.aggregate.common.binding.ExtendedBinding;
import com.tibbo.aggregate.common.context.ContextException;
import com.tibbo.aggregate.common.context.ContextUtils;
import com.tibbo.aggregate.common.context.EventData;
import com.tibbo.aggregate.common.context.EventDefinition;
import com.tibbo.aggregate.common.context.FunctionDefinition;
import com.tibbo.aggregate.common.context.VariableDefinition;
import com.tibbo.aggregate.common.context.VariableStatus;
import com.tibbo.aggregate.common.datatable.DataRecord;
import com.tibbo.aggregate.common.datatable.DataTable;
import com.tibbo.aggregate.common.datatable.DataTableConversion;
import com.tibbo.aggregate.common.datatable.FieldFormat;
import com.tibbo.aggregate.common.datatable.SimpleDataTable;
import com.tibbo.aggregate.common.datatable.TableFormat;
import com.tibbo.aggregate.common.datatable.field.StringFieldFormat;
import com.tibbo.aggregate.common.server.WidgetContextConstants;
import com.tibbo.aggregate.common.util.Docs;
import com.tibbo.aggregate.common.util.Icons;
import com.tibbo.aggregate.common.util.StringUtils;
import com.tibbo.aggregate.common.util.SyntaxErrorException;
import com.tibbo.aggregate.common.util.ThreadUtils;
import com.tibbo.aggregate.common.widget.Res;
import com.tibbo.aggregate.common.widget.WBindingUtils;
import com.tibbo.aggregate.common.widget.WConstraints;
import com.tibbo.aggregate.common.widget.WGridConstraints;
import com.tibbo.aggregate.common.widget.WidgetApiUtils;
import com.tibbo.aggregate.common.widget.WidgetConstants;
import com.tibbo.aggregate.common.widget.WidgetTemplate;
import com.tibbo.aggregate.common.widget.component.WComponent;
import com.tibbo.aggregate.common.widget.component.WContainer;

/**
 * Description: Common Context wrapper for TemplateElement. It provides the possibility to work wit TemplateElement as with Context. For example to represent TemplateElement properties in
 * <code>PropertiesEditor</code>.
 */
public class WComponentContext<C extends WComponentContext, T extends WComponent> extends WContext<C, T>
{
  public static final VariableStatus STATUS_BOUND = new VariableStatus(Icons.VS_BOUND, Res.get().getString("gbBound"));
  public static final VariableStatus STATUS_EMPTY = new VariableStatus(null, null);
  
  protected static String GROUP_CONSTRAINTS = ContextUtils.createGroup(ContextUtils.GROUP_DEFAULT, Cres.get().getString("wConstraints"));
  protected static String GROUP_BINDINGS = ContextUtils.createGroup(ContextUtils.GROUP_DEFAULT, Cres.get().getString("wBindings"));
  
  public static final String E_COMPONENT_ERROR = "componentError";
  
  public static final String EF_COMPONENT = "component";
  public static final String EF_ERROR = "error";
  public static final String EF_ERROR_STACK = "errorStack";
  
  public static final TableFormat VFT_WEIGHTY = new TableFormat(1, 1, "<" + WidgetConstants.V_WEIGHTY + "><F><A=" + WidgetConstants.WEIGHTY_DEFAULT_VALUE + ">");
  public static final TableFormat VFT_WEIGHTX = new TableFormat(1, 1, "<" + WidgetConstants.V_WEIGHTX + "><F><A=" + WidgetConstants.WEIGHTX_DEFAULT_VALUE + ">");
  
  public static final TableFormat VFT_FILL = new TableFormat(1, 1);
  static
  {
    FieldFormat<Object> ff = FieldFormat.create("<" + WidgetConstants.V_FILL + "><I>");
    ff.setSelectionValues(createFillSelectionValues());
    VFT_FILL.addField(ff);
  }
  
  public static final TableFormat VFT_INSETS_RIGHT = new TableFormat(1, 1, "<" + WidgetConstants.V_INSETS_RIGHT + "><I><V=<L=0 " + Integer.MAX_VALUE + ">>");
  public static final TableFormat VFT_INSETS_LEFT = new TableFormat(1, 1, "<" + WidgetConstants.V_INSETS_LEFT + "><I><V=<L=0 " + Integer.MAX_VALUE + ">>");
  public static final TableFormat VFT_INSETS_BOTTOM = new TableFormat(1, 1, "<" + WidgetConstants.V_INSETS_BOTTOM + "><I><V=<L=0 " + Integer.MAX_VALUE + ">>");
  public static final TableFormat VFT_INSETS_TOP = new TableFormat(1, 1, "<" + WidgetConstants.V_INSETS_TOP + "><I><V=<L=0 " + Integer.MAX_VALUE + ">>");
  public static final TableFormat VFT_HEIGHT = new TableFormat(1, 1, "<" + WComponent.V_HEIGHT + "><I> <V=<L=0 " + Integer.MAX_VALUE + ">>");
  public static final TableFormat VFT_WIDTH = new TableFormat(1, 1, "<" + WComponent.V_WIDTH + "><I> <V=<L=0 " + Integer.MAX_VALUE + ">>");
  public static final TableFormat VFT_ZORDER = new TableFormat(1, 1, "<" + WidgetConstants.V_ZORDER + "><I><D=" + Cres.get().getString("wZOrder") + "><V=<L=0 " + Integer.MAX_VALUE + ">>");
  public static final TableFormat VFT_XCOORDINATE = new TableFormat(1, 1,
      "<" + WidgetConstants.V_XCOORDINATE + "><I><D=" + Cres.get().getString("wXCoordinate") + "><V=<L=0 " + Integer.MAX_VALUE + ">>");
  public static final TableFormat VFT_YCOORDINATE = new TableFormat(1, 1,
      "<" + WidgetConstants.V_YCOORDINATE + "><I><D=" + Cres.get().getString("wYCoordinate") + "><V=<L=0 " + Integer.MAX_VALUE + ">>");
  public static final TableFormat VFT_ANCHOR = new TableFormat(1, 1);
  
  public static final VariableDefinition WEIGHTY_VD = new VariableDefinition(WidgetConstants.V_WEIGHTY, VFT_WEIGHTY, true, true, Cres.get().getString("wWeightY"), GROUP_CONSTRAINTS);
  public static final VariableDefinition WEIGHTX_VD = new VariableDefinition(WidgetConstants.V_WEIGHTX, VFT_WEIGHTX, true, true, Cres.get().getString("wWeightX"), GROUP_CONSTRAINTS);
  public static final VariableDefinition ANCHOR_VD;
  public static final VariableDefinition FILL_VD = new VariableDefinition(WidgetConstants.V_FILL, VFT_FILL, true, true, Cres.get().getString("fill"), GROUP_CONSTRAINTS);
  public static final VariableDefinition INSETS_RIGHT_VD = new VariableDefinition(WidgetConstants.V_INSETS_RIGHT, VFT_INSETS_RIGHT, true, true, Cres.get().getString("wRightMargin"),
      GROUP_CONSTRAINTS);
  public static final VariableDefinition INSETS_LEFT_VD = new VariableDefinition(WidgetConstants.V_INSETS_LEFT, VFT_INSETS_LEFT, true, true, Cres.get().getString("wLeftMargin"), GROUP_CONSTRAINTS);
  public static final VariableDefinition INSETS_BOTTOM_VD = new VariableDefinition(WidgetConstants.V_INSETS_BOTTOM, VFT_INSETS_BOTTOM, true, true, Cres.get().getString("wBottomMargin"),
      GROUP_CONSTRAINTS);
  public static final VariableDefinition INSETS_TOP_VD = new VariableDefinition(WidgetConstants.V_INSETS_TOP, VFT_INSETS_TOP, true, true, Cres.get().getString("wTopMargin"), GROUP_CONSTRAINTS);
  public static final VariableDefinition HEIGHT_VD = new VariableDefinition(WComponent.V_HEIGHT, VFT_HEIGHT, true, true, Cres.get().getString("height"), GROUP_CONSTRAINTS);
  public static final VariableDefinition WIDTH_VD = new VariableDefinition(WComponent.V_WIDTH, VFT_WIDTH, true, true, Cres.get().getString("width"), GROUP_CONSTRAINTS);
  public static final VariableDefinition ZORDER_VD = new VariableDefinition(WidgetConstants.V_ZORDER, VFT_ZORDER, true, true, Cres.get().getString("wZOrder"), GROUP_CONSTRAINTS);
  public static final VariableDefinition XCOORDINATE_VD = new VariableDefinition(WidgetConstants.V_XCOORDINATE, VFT_XCOORDINATE, true, true, Cres.get().getString("wXCoordinate"), GROUP_CONSTRAINTS);
  public static final VariableDefinition YCOORDINATE_VD = new VariableDefinition(WidgetConstants.V_YCOORDINATE, VFT_YCOORDINATE, true, true, Cres.get().getString("wYCoordinate"), GROUP_CONSTRAINTS);
  
  static
  {
    FieldFormat<Integer> ff = FieldFormat.create("<" + WidgetConstants.V_ANCHOR + "><I>");
    ff.setSelectionValues(createAnchorSelectionValues());
    VFT_ANCHOR.addField(ff);
  }
  
  public static final TableFormat VFT_ALL_BINDINGS = Bindings.FORMAT.clone();
  static
  {
    VFT_ALL_BINDINGS.setReorderable(true);
  }
  
  static
  {
    GRIDX_VD.setIndex(INDEX_HIGH);
    GRIDY_VD.setIndex(INDEX_HIGH);
    GRID_WIDTH_VD.setIndex(INDEX_HIGH);
    GRID_HEIGHT_VD.setIndex(INDEX_HIGH);
    
    ANCHOR_VD = new VariableDefinition(WidgetConstants.V_ANCHOR, VFT_ANCHOR, true, true, Cres.get().getString("wAnchor"), GROUP_CONSTRAINTS);
    ANCHOR_VD.setIndex(INDEX_HIGH);
    
    INSETS_TOP_VD.setIndex(INDEX_HIGH);
    INSETS_LEFT_VD.setIndex(INDEX_HIGH);
    INSETS_BOTTOM_VD.setIndex(INDEX_HIGH);
    INSETS_RIGHT_VD.setIndex(INDEX_HIGH);
    
    FILL_VD.setIndex(INDEX_HIGH);
    
    WEIGHTX_VD.setIndex(INDEX_HIGH);
    WEIGHTY_VD.setIndex(INDEX_HIGH);
    
    XCOORDINATE_VD.setIndex(INDEX_HIGH);
    YCOORDINATE_VD.setIndex(INDEX_HIGH);
    ZORDER_VD.setIndex(INDEX_HIGH);
    
  }
  
  public static VariableDefinition BINDINGS_VD = new VariableDefinition(WidgetTemplate.V_BINDINGS, Bindings.FORMAT, true, true, Cres.get().getString("bindings"), GROUP_BINDINGS);
  static
  {
    BINDINGS_VD.setHelpId(Docs.LS_WIDGETS_BINDINGS);
  }
  
  public static final TableFormat EFT_COMPONENT_ERROR = new TableFormat(1, 1);
  
  static
  {
    EFT_COMPONENT_ERROR.addField(FieldFormat.create("<" + EF_COMPONENT + "><S><D=" + Cres.get().getString("component") + ">"));
    EFT_COMPONENT_ERROR.addField(FieldFormat.create("<" + EF_ERROR + "><S><D=" + Cres.get().getString("error") + "><E=" + StringFieldFormat.EDITOR_TEXT_AREA + ">"));
    EFT_COMPONENT_ERROR.addField(FieldFormat.create("<" + EF_ERROR_STACK + "><T><D=" + Cres.get().getString("stack") + ">"));
  }
  
  private final WidgetTemplate widget;
  
  /**
   * Constructor receives element object to be wrapped
   * 
   * @param component
   *          Component
   * @param widget
   *          Widget
   */
  public WComponentContext(T component, WidgetTemplate widget)
  {
    super(component);
    this.widget = widget;
    // Empty components have no variable definitions
    if (!component.getKey().equals(WidgetConstants.COMPONENT_EMPTY))
    {
      createVariableDefinitions();
    }
  }
  
  @Override
  public T getComponent()
  {
    return super.getComponent();
  }
  
  public String resizeTo(Rectangle resizeRectangle) throws ContextException
  {
    final int width = resizeRectangle.width;
    final int height = resizeRectangle.height;
    final StringBuilder sb = new StringBuilder();
    
    if (getComponent().getWidth() != width && getVariableDefinition(WComponent.V_WIDTH) != null)
    {
      setVariable(WComponent.V_WIDTH, width);
      sb.append(WComponent.V_WIDTH);
    }
    
    if (getComponent().getHeight() != height && getVariableDefinition(WComponent.V_HEIGHT) != null)
    {
      setVariable(WComponent.V_HEIGHT, height);
      sb.append(sb.length() == 0 ? "" : ", ").append(WComponent.V_HEIGHT);
    }
    
    return sb.toString();
  }
  
  public String resizeTo(Rectangle resizeRectangle, Boolean ignoreTurning) throws ContextException
  {
    return resizeTo(resizeRectangle);
  }
  
  @Override
  public void componentPropertyChanged(PropertyChangeEvent evt)
  {
    final WContainer parentContainer = getParentContainer();
    if (parentContainer != null)
      parentContainer.childPropertyChanged(evt);
  }
  
  public WidgetTemplate getWidget()
  {
    return widget;
  }
  
  /**
   * Overrides standard <code>AbstractContext</code> method. It invokes <code>createVariableDefinitions</code> method of Element wrapped by this <code>ElementContextWrapper</code> instance. Then it
   * gets VariableDefinitions list from Element and add each Definition to the Context represented by this wrapper.
   * 
   * @throws ContextException
   */
  @Override
  public void setupMyself() throws ContextException
  {
    setPermissionCheckingEnabled(false);
    
    super.setupMyself();
  }
  
  public static void setNewConstraints(WConstraints cs, String prop, WConstraints oldcs, WComponent component, WidgetTemplate widget) throws ContextException
  {
    if (cs.equals(oldcs))
    {
      return;
    }
    WContainer container = WidgetApiUtils.getComponentParent(component, widget);
    try
    {
      container.add(component, cs);
    }
    catch (Exception ex)
    {
      container.add(component, oldcs);
      throw new ContextException(ex);
    }
    component.firePropertyChange(prop, cs, oldcs);
  }
  
  @Override
  protected boolean setComponentVariable(VariableDefinition vd, DataTable value) throws ContextException
  {
    if (vd.getName().equals(WidgetTemplate.V_BINDINGS))
    {
      try
      {
        List<ExtendedBinding> bs = Bindings.bindingsFromDataTable(value);
        if (WBindingUtils.getBindingsRelatedToComponent(bs, getComponent().getName()).size() < bs.size())
        {
          throw new ContextException(MessageFormat.format(Res.get().getString("wErrorNotRelatedToComponent"), getComponent().getName()));
        }
        widget.changeBindingSet(WBindingUtils.getBindingsRelatedToComponent(widget.getBindings(), getComponent().getName()), bs, true);
      }
      catch (SyntaxErrorException ex)
      {
        throw new ContextException(
            MessageFormat.format(Res.get().getString("wPropertyUpdateFailure"), getComponent().getName(), WidgetTemplate.V_BINDINGS, getComponent().getName()) + ":" + ex.getMessage(),
            ex);
      }
    }
    else
    {
      setComponentVariableAsProperty(vd, value, getComponent());
    }
    
    return true;
  }
  
  @Override
  protected DataTable getComponentVariable(VariableDefinition vd)
  {
    if (vd == null)
    {
      return null;
    }
    
    WContainer container = getParentContainer();
    if (container == null && WidgetConstants.CONSTRAINTS_PROPERTIES_LIST.contains(vd.getName()))
    {
      throw new IllegalStateException("Component '" + getName() + "' has no parent container. Property '" + vd.getName() + "' should not be requested before component get its parent container.");
    }
    
    if (vd.getName().equals(WidgetTemplate.V_BINDINGS))
    {
      try
      {
        return Bindings.bindingsToDataTable(WBindingUtils.getBindingsRelatedToComponent(widget.getBindings(), getComponent().getName()));
      }
      catch (SyntaxErrorException ex1)
      {
        Log.WIDGETS.error("Failed to get property '" + WidgetTemplate.V_BINDINGS + " of component: " + getComponent().getName(), ex1);
        return null;
      }
    }
    else
    {
      return getComponentVariableAsProperty(vd, getComponent());
    }
  }
  
  public void removeAllVariableDefinitions()
  {
    for (VariableDefinition vd : getVariableDefinitions())
    {
      removeVariableDefinition(vd.getName());
    }
  }
  
  /**
   * This method creates Variable Definitions for Elements properties. Each TemplateElement realization can define additional properties. That is why it must override this method. But it inherits
   * ancestor Variables. Therefore it must invoke super method.
   */
  @Override
  protected void createVariableDefinitions()
  {
    addVariableDefinition(WIDTH_VD);
    addVariableDefinition(HEIGHT_VD);
    // Root panel has no constraints properties
    if (!getComponent().getKey().equals(WidgetContextConstants.COMPONENT_ROOT_PANEL))
    {
      WContainer parent = getParentContainer();
      int layout = WidgetConstants.GRID_LAYOUT;
      if (parent != null)
      {
        layout = parent.getLayout();
      }
      resetConstraintsVD(layout);
    }
    addVariableDefinition(BINDINGS_VD);
    
    addEventDefinition(new EventDefinition(E_COMPONENT_ERROR, EFT_COMPONENT_ERROR, Res.get().getString("wComponentError"), ContextUtils.GROUP_DEFAULT));
  }
  
  /**
   * Sets constraints variable definitions depending on parent container 'layout' property.
   */
  public void resetConstraintsVD(int layout)
  {
    for (VariableDefinition vd : getVariableDefinitions())
    {
      if (WidgetConstants.CONSTRAINTS_PROPERTIES_LIST.contains(vd.getName()))
      {
        removeVariableDefinition(vd.getName());
      }
    }
    WContainerContext.getLayoutHelper(layout).createConstraintsVariables(this, widget);
  }
  
  /**
   * Creates map of values for anchor property
   * 
   * @return Map
   */
  private static Map<Integer, String> createAnchorSelectionValues()
  {
    Map<Integer, String> map = new LinkedHashMap<Integer, String>();
    map.put(WGridConstraints.ANCHOR_CENTER, WidgetConstants.ANCHOR_CENTER_TITLE);
    map.put(WGridConstraints.ANCHOR_NORTH, WidgetConstants.ANCHOR_NORTH_TITLE);
    map.put(WGridConstraints.ANCHOR_EAST, WidgetConstants.ANCHOR_EAST_TITLE);
    map.put(WGridConstraints.ANCHOR_SOUTH, WidgetConstants.ANCHOR_SOUTH_TITLE);
    map.put(WGridConstraints.ANCHOR_WEST, WidgetConstants.ANCHOR_WEST_TITLE);
    map.put(WGridConstraints.ANCHOR_NORTHWEST, WidgetConstants.ANCHOR_NORTHWEST_TITLE);
    map.put(WGridConstraints.ANCHOR_NORTHEAST, WidgetConstants.ANCHOR_NORTHEAST_TITLE);
    map.put(WGridConstraints.ANCHOR_SOUTHEAST, WidgetConstants.ANCHOR_SOUTHEAST_TITLE);
    map.put(WGridConstraints.ANCHOR_SOUTHWEST, WidgetConstants.ANCHOR_SOUTHWEST_TITLE);
    return map;
  }
  
  private static Map<Object, String> createFillSelectionValues()
  {
    Map<Object, String> map = new LinkedHashMap<Object, String>();
    map.put(WGridConstraints.FILL_NONE, Cres.get().getString("none"));
    map.put(WGridConstraints.FILL_VERTICAL, Cres.get().getString("wVertical"));
    map.put(WGridConstraints.FILL_HORIZONTAL, Cres.get().getString("wHorizontal"));
    map.put(WGridConstraints.FILL_BOTH, Cres.get().getString("wBothHorVert"));
    return map;
  }
  
  public static DataTable getSingleFieldDT(VariableDefinition vd, String field, Object value)
  {
    DataTable dt = new SimpleDataTable(vd.getFormat());
    DataRecord dr = dt.addRecord();
    Object converted = DataTableConversion.convertValueToField(dr.getFormat(field), value);
    dr.setValue(field, converted);
    return dt;
  }
  
  /**
   * Override this method for those variables which default value you can not define via TableFormat
   * 
   * @param vd
   *          VariableDefinition
   * @return DataTable
   */
  public DataTable getVariableDefaultValue(VariableDefinition vd)
  {
    return new SimpleDataTable(vd.getFormat());
  }
  
  public WContainer getParentContainer()
  {
    return WidgetApiUtils.getComponentParent(getComponent(), getWidget());
  }
  
  /**
   * Some components change variable definitions of their children.
   */
  public void editAcceptedChildVariables(WComponentContext child)
  {
    
  }
  
  public boolean shouldInformRendererAboutChange(PropertyChangeEvent event, List<WComponentContext> childContexts)
  {
    VariableDefinition vd = getVariableDefinition(event.getPropertyName());
    return vd != null && !ContextUtils.GROUP_SYSTEM.equals(vd.getGroup());
  }
  
  public void fireExceptionEvent(String message, Exception exception)
  {
    Log.WIDGETS.warn(message, exception);
    
    DataRecord data = new DataRecord(EFT_COMPONENT_ERROR);
    
    data.setValue(EF_COMPONENT, getName());
    
    if (!StringUtils.isEmpty(message))
      data.setValue(EF_ERROR, message);
    if (exception != null)
      data.setValue(EF_ERROR_STACK, ThreadUtils.createStackTraceTable(exception.getStackTrace()));
    
    fireEvent(E_COMPONENT_ERROR, data.wrap());
  }
  
  public boolean saveHiddenVariables()
  {
    return false;
  }
  
  @Override
  protected void fireVariableAdded(VariableDefinition def)
  {
    EventData ed = getEventData(E_VARIABLE_ADDED);
    if (ed != null && ed.hasListeners())
    {
      fireEvent(ed.getDefinition().getName(), new SimpleDataTable(varDefToDataRecord(def, null)));
    }
  }
  
  @Override
  protected void fireFunctionAdded(FunctionDefinition def)
  {
    EventData ed = getEventData(E_FUNCTION_ADDED);
    if (ed != null && ed.hasListeners())
    {
      fireEvent(ed.getDefinition().getName(), new SimpleDataTable(funcDefToDataRecord(def, null)));
    }
  }
  
  @Override
  protected void fireEventAdded(EventDefinition def)
  {
    EventData ed = getEventData(E_EVENT_ADDED);
    if (ed != null && ed.hasListeners())
    {
      fireEvent(ed.getDefinition().getName(), new SimpleDataTable(evtDefToDataRecord(def, null)));
    }
  }
}
