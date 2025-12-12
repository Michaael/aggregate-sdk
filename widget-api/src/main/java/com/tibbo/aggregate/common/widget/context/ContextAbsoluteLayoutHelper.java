package com.tibbo.aggregate.common.widget.context;

import java.util.*;

import com.tibbo.aggregate.common.context.*;
import com.tibbo.aggregate.common.datatable.*;
import com.tibbo.aggregate.common.widget.*;
import com.tibbo.aggregate.common.widget.component.*;

public class ContextAbsoluteLayoutHelper implements ContainerContextLayoutHelper
{
  private static final List<String> CONSTRAINT_PROPERTIES = new LinkedList<String>();
  
  static
  {
    CONSTRAINT_PROPERTIES.add(WidgetConstants.V_ZORDER);
    CONSTRAINT_PROPERTIES.add(WidgetConstants.V_XCOORDINATE);
    CONSTRAINT_PROPERTIES.add(WidgetConstants.V_YCOORDINATE);
  }
  
  @Override
  public void createConstraintsVariables(WComponentContext context, final WidgetTemplate widget)
  {
    VariableDefinition xCoordVD = WComponentContext.XCOORDINATE_VD.clone();
    xCoordVD.setSetter(new VariableSetter()
    {
      @Override
      public boolean set(Context con, VariableDefinition def, CallerController caller, RequestController request, DataTable value) throws ContextException
      {
        WComponent component = ((WComponentContext) con).getComponent();
        
        WAbsoluteConstraints oldCs = getConstraints(component, widget);
        WAbsoluteConstraints newCs = oldCs.clone();
        newCs.setX(value.rec().getInt(def.getName()));
        WComponentContext.setNewConstraints(newCs, def.getName(), oldCs, component, widget);
        return true;
      }
    });
    xCoordVD.setGetter(new VariableGetter()
    {
      @Override
      public DataTable get(Context con, VariableDefinition def, CallerController caller, RequestController request) throws ContextException
      {
        WComponent component = ((WComponentContext) con).getComponent();
        WAbsoluteConstraints cs = getConstraints(component, widget);
        return WComponentContext.getSingleFieldDT(def, def.getName(), cs.getX());
      }
    });
    context.addVariableDefinition(xCoordVD);
    
    VariableDefinition yCoordVD = WComponentContext.YCOORDINATE_VD.clone();
    yCoordVD.setSetter(new VariableSetter()
    {
      @Override
      public boolean set(Context con, VariableDefinition def, CallerController caller, RequestController request, DataTable value) throws ContextException
      {
        WComponent component = ((WComponentContext) con).getComponent();
        WAbsoluteConstraints oldCs = getConstraints(component, widget);
        WAbsoluteConstraints newCs = oldCs.clone();
        newCs.setY(value.rec().getInt(def.getName()));
        WComponentContext.setNewConstraints(newCs, def.getName(), oldCs, component, widget);
        return true;
      }
    });
    yCoordVD.setGetter(new VariableGetter()
    {
      @Override
      public DataTable get(Context con, VariableDefinition def, CallerController caller, RequestController request) throws ContextException
      {
        WComponent component = ((WComponentContext) con).getComponent();
        WAbsoluteConstraints cs = getConstraints(component, widget);
        return WComponentContext.getSingleFieldDT(def, def.getName(), cs.getY());
      }
    });
    context.addVariableDefinition(yCoordVD);
    
    VariableDefinition zVD = WComponentContext.ZORDER_VD.clone();
    zVD.setSetter(new VariableSetter()
    {
      @Override
      public boolean set(Context con, VariableDefinition def, CallerController caller, RequestController request, DataTable value) throws ContextException
      {
        WComponent component = ((WComponentContext) con).getComponent();
        WAbsoluteConstraints oldCs = getConstraints(component, widget);
        WAbsoluteConstraints newCs = oldCs.clone();
        newCs.setZOrder(value.rec().getInt(def.getName()));
        WComponentContext.setNewConstraints(newCs, def.getName(), oldCs, component, widget);
        return true;
      }
    });
    zVD.setGetter(new VariableGetter()
    {
      @Override
      public DataTable get(Context con, VariableDefinition def, CallerController caller, RequestController request) throws ContextException
      {
        WComponent component = ((WComponentContext) con).getComponent();
        WAbsoluteConstraints cs = getConstraints(component, widget);
        return WComponentContext.getSingleFieldDT(def, def.getName(), cs.getZOrder());
      }
    });
    context.addVariableDefinition(zVD);
  }
  
  private static WAbsoluteConstraints getConstraints(WComponent component, WidgetTemplate widget)
  {
    WContainer parent = WidgetApiUtils.getComponentParent(component, widget);
    return (WAbsoluteConstraints) parent.getChildConstraints(component);
  }
  
  @Override
  public List<String> getConstraintsPropertiesForLayout()
  {
    return CONSTRAINT_PROPERTIES;
  }
}
