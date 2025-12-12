package com.tibbo.aggregate.common.widget.context;

import java.util.*;

import com.tibbo.aggregate.common.context.*;
import com.tibbo.aggregate.common.datatable.*;
import com.tibbo.aggregate.common.widget.*;
import com.tibbo.aggregate.common.widget.component.*;

public class ContextGridLayoutHelper implements ContainerContextLayoutHelper
{
  private static final List<String> CONSTRAINT_PROPERTIES = new LinkedList<String>();
  
  static
  {
    CONSTRAINT_PROPERTIES.add(WidgetConstants.V_GRIDX);
    CONSTRAINT_PROPERTIES.add(WidgetConstants.V_GRIDY);
    CONSTRAINT_PROPERTIES.add(WidgetConstants.V_GRID_WIDTH);
    CONSTRAINT_PROPERTIES.add(WidgetConstants.V_GRID_HEIGHT);
    CONSTRAINT_PROPERTIES.add(WidgetConstants.V_ANCHOR);
    CONSTRAINT_PROPERTIES.add(WidgetConstants.V_INSETS_TOP);
    CONSTRAINT_PROPERTIES.add(WidgetConstants.V_INSETS_BOTTOM);
    CONSTRAINT_PROPERTIES.add(WidgetConstants.V_INSETS_RIGHT);
    CONSTRAINT_PROPERTIES.add(WidgetConstants.V_INSETS_LEFT);
    CONSTRAINT_PROPERTIES.add(WidgetConstants.V_FILL);
    CONSTRAINT_PROPERTIES.add(WidgetConstants.V_WEIGHTX);
    CONSTRAINT_PROPERTIES.add(WidgetConstants.V_WEIGHTY);
  }
  
  @Override
  public void createConstraintsVariables(WComponentContext context, final WidgetTemplate widget)
  {
    VariableDefinition gridxVD = WComponentContext.GRIDX_VD.clone();
    gridxVD.setSetter(new VariableSetter()
    {
      @Override
      public boolean set(Context con, VariableDefinition def, CallerController caller, RequestController request, DataTable value) throws ContextException
      {
        WComponent component = WidgetApiUtils.getComponentByName(widget, con.getName());
        WGridConstraints oldCs = getConstraints(component, widget);
        WGridConstraints newCs = oldCs.clone();
        newCs.setGridx(value.rec().getInt(def.getName()));
        WComponentContext.setNewConstraints(newCs, def.getName(), oldCs, component, widget);
        return true;
      }
    });
    gridxVD.setGetter(new VariableGetter()
    {
      @Override
      public DataTable get(Context con, VariableDefinition def, CallerController caller, RequestController request) throws ContextException
      {
        WComponent component = WidgetApiUtils.getComponentByName(widget, con.getName());
        WGridConstraints cs = getConstraints(component, widget);
        return WComponentContext.getSingleFieldDT(def, def.getName(), cs.getGridx());
      }
    });
    context.addVariableDefinition(gridxVD);
    
    VariableDefinition gridyVD = WComponentContext.GRIDY_VD.clone();
    gridyVD.setSetter(new VariableSetter()
    {
      @Override
      public boolean set(Context con, VariableDefinition def, CallerController caller, RequestController request, DataTable value) throws ContextException
      {
        WComponent component = WidgetApiUtils.getComponentByName(widget, con.getName());
        WGridConstraints oldCs = getConstraints(component, widget);
        WGridConstraints newCs = oldCs.clone();
        newCs.setGridy(value.rec().getInt(def.getName()));
        WComponentContext.setNewConstraints(newCs, def.getName(), oldCs, component, widget);
        return true;
      }
    });
    gridyVD.setGetter(new VariableGetter()
    {
      @Override
      public DataTable get(Context con, VariableDefinition def, CallerController caller, RequestController request) throws ContextException
      {
        WComponent component = WidgetApiUtils.getComponentByName(widget, con.getName());
        WGridConstraints cs = getConstraints(component, widget);
        return WComponentContext.getSingleFieldDT(def, def.getName(), cs.getGridy());
      }
    });
    context.addVariableDefinition(gridyVD);
    
    VariableDefinition gridxWidthVD = WComponentContext.GRID_WIDTH_VD.clone();
    gridxWidthVD.setSetter(new VariableSetter()
    {
      @Override
      public boolean set(Context con, VariableDefinition def, CallerController caller, RequestController request, DataTable value) throws ContextException
      {
        WComponent component = WidgetApiUtils.getComponentByName(widget, con.getName());
        WGridConstraints oldCs = getConstraints(component, widget);
        WGridConstraints newCs = oldCs.clone();
        newCs.setGridwidth(value.rec().getInt(def.getName()));
        WComponentContext.setNewConstraints(newCs, def.getName(), oldCs, component, widget);
        return true;
      }
    });
    gridxWidthVD.setGetter(new VariableGetter()
    {
      @Override
      public DataTable get(Context con, VariableDefinition def, CallerController caller, RequestController request) throws ContextException
      {
        WComponent component = WidgetApiUtils.getComponentByName(widget, con.getName());
        WGridConstraints cs = getConstraints(component, widget);
        return WComponentContext.getSingleFieldDT(def, def.getName(), cs.getGridwidth());
      }
    });
    context.addVariableDefinition(gridxWidthVD);
    
    VariableDefinition gridxHeightVD = WComponentContext.GRID_HEIGHT_VD.clone();
    gridxHeightVD.setSetter(new VariableSetter()
    {
      @Override
      public boolean set(Context con, VariableDefinition def, CallerController caller, RequestController request, DataTable value) throws ContextException
      {
        WComponent component = WidgetApiUtils.getComponentByName(widget, con.getName());
        WGridConstraints oldCs = getConstraints(component, widget);
        WGridConstraints newCs = oldCs.clone();
        newCs.setGridheight(value.rec().getInt(def.getName()));
        WComponentContext.setNewConstraints(newCs, def.getName(), oldCs, component, widget);
        return true;
      }
    });
    gridxHeightVD.setGetter(new VariableGetter()
    {
      @Override
      public DataTable get(Context con, VariableDefinition def, CallerController caller, RequestController request) throws ContextException
      {
        WComponent component = WidgetApiUtils.getComponentByName(widget, con.getName());
        WGridConstraints cs = getConstraints(component, widget);
        return WComponentContext.getSingleFieldDT(def, def.getName(), cs.getGridheight());
      }
    });
    context.addVariableDefinition(gridxHeightVD);
    
    VariableDefinition anchorVD = WComponentContext.ANCHOR_VD.clone();
    anchorVD.setSetter(new VariableSetter()
    {
      @Override
      public boolean set(Context con, VariableDefinition def, CallerController caller, RequestController request, DataTable value) throws ContextException
      {
        WComponent component = WidgetApiUtils.getComponentByName(widget, con.getName());
        WGridConstraints oldCs = getConstraints(component, widget);
        WGridConstraints newCs = oldCs.clone();
        newCs.setAnchor(value.rec().getInt(def.getName()));
        WComponentContext.setNewConstraints(newCs, def.getName(), oldCs, component, widget);
        return true;
      }
    });
    anchorVD.setGetter(new VariableGetter()
    {
      @Override
      public DataTable get(Context con, VariableDefinition def, CallerController caller, RequestController request) throws ContextException
      {
        WComponent component = WidgetApiUtils.getComponentByName(widget, con.getName());
        WGridConstraints cs = getConstraints(component, widget);
        return WComponentContext.getSingleFieldDT(def, def.getName(), cs.getAnchor());
      }
    });
    context.addVariableDefinition(anchorVD);
    
    VariableDefinition insetsTopVD = WComponentContext.INSETS_TOP_VD.clone();
    insetsTopVD.setSetter(new VariableSetter()
    {
      @Override
      public boolean set(Context con, VariableDefinition def, CallerController caller, RequestController request, DataTable value) throws ContextException
      {
        WComponent component = WidgetApiUtils.getComponentByName(widget, con.getName());
        WGridConstraints oldCs = getConstraints(component, widget);
        WGridConstraints newCs = oldCs.clone();
        newCs.setInsetsTop(value.rec().getInt(def.getName()));
        WComponentContext.setNewConstraints(newCs, def.getName(), oldCs, component, widget);
        return true;
      }
    });
    insetsTopVD.setGetter(new VariableGetter()
    {
      @Override
      public DataTable get(Context con, VariableDefinition def, CallerController caller, RequestController request) throws ContextException
      {
        WComponent component = WidgetApiUtils.getComponentByName(widget, con.getName());
        WGridConstraints cs = getConstraints(component, widget);
        return WComponentContext.getSingleFieldDT(def, def.getName(), cs.getInsetsTop());
      }
    });
    context.addVariableDefinition(insetsTopVD);
    
    VariableDefinition insetsBottomVD = WComponentContext.INSETS_BOTTOM_VD.clone();
    insetsBottomVD.setSetter(new VariableSetter()
    {
      @Override
      public boolean set(Context con, VariableDefinition def, CallerController caller, RequestController request, DataTable value) throws ContextException
      {
        WComponent component = WidgetApiUtils.getComponentByName(widget, con.getName());
        WGridConstraints oldCs = getConstraints(component, widget);
        WGridConstraints newCs = oldCs.clone();
        newCs.setInsetsBottom(value.rec().getInt(def.getName()));
        WComponentContext.setNewConstraints(newCs, def.getName(), oldCs, component, widget);
        return true;
      }
    });
    insetsBottomVD.setGetter(new VariableGetter()
    {
      @Override
      public DataTable get(Context con, VariableDefinition def, CallerController caller, RequestController request) throws ContextException
      {
        WComponent component = WidgetApiUtils.getComponentByName(widget, con.getName());
        WGridConstraints cs = getConstraints(component, widget);
        return WComponentContext.getSingleFieldDT(def, def.getName(), cs.getInsetsBottom());
      }
    });
    context.addVariableDefinition(insetsBottomVD);
    
    VariableDefinition insetsLeftVD = WComponentContext.INSETS_LEFT_VD.clone();
    insetsLeftVD.setSetter(new VariableSetter()
    {
      @Override
      public boolean set(Context con, VariableDefinition def, CallerController caller, RequestController request, DataTable value) throws ContextException
      {
        WComponent component = WidgetApiUtils.getComponentByName(widget, con.getName());
        WGridConstraints oldCs = getConstraints(component, widget);
        WGridConstraints newCs = oldCs.clone();
        newCs.setInsetsLeft(value.rec().getInt(def.getName()));
        WComponentContext.setNewConstraints(newCs, def.getName(), oldCs, component, widget);
        return true;
      }
    });
    insetsLeftVD.setGetter(new VariableGetter()
    {
      @Override
      public DataTable get(Context con, VariableDefinition def, CallerController caller, RequestController request) throws ContextException
      {
        WComponent component = WidgetApiUtils.getComponentByName(widget, con.getName());
        WGridConstraints cs = getConstraints(component, widget);
        return WComponentContext.getSingleFieldDT(def, def.getName(), cs.getInsetsLeft());
      }
    });
    context.addVariableDefinition(insetsLeftVD);
    
    VariableDefinition insetsRightVD = WComponentContext.INSETS_RIGHT_VD.clone();
    insetsRightVD.setSetter(new VariableSetter()
    {
      @Override
      public boolean set(Context con, VariableDefinition def, CallerController caller, RequestController request, DataTable value) throws ContextException
      {
        WComponent component = WidgetApiUtils.getComponentByName(widget, con.getName());
        WGridConstraints oldCs = getConstraints(component, widget);
        WGridConstraints newCs = oldCs.clone();
        newCs.setInsetsRight(value.rec().getInt(def.getName()));
        WComponentContext.setNewConstraints(newCs, def.getName(), oldCs, component, widget);
        return true;
      }
    });
    insetsRightVD.setGetter(new VariableGetter()
    {
      @Override
      public DataTable get(Context con, VariableDefinition def, CallerController caller, RequestController request) throws ContextException
      {
        WComponent component = WidgetApiUtils.getComponentByName(widget, con.getName());
        WGridConstraints cs = getConstraints(component, widget);
        return WComponentContext.getSingleFieldDT(def, def.getName(), cs.getInsetsRight());
      }
    });
    context.addVariableDefinition(insetsRightVD);
    
    VariableDefinition fillVD = WComponentContext.FILL_VD.clone();
    fillVD.setSetter(new VariableSetter()
    {
      @Override
      public boolean set(Context con, VariableDefinition def, CallerController caller, RequestController request, DataTable value) throws ContextException
      {
        WComponent component = WidgetApiUtils.getComponentByName(widget, con.getName());
        WGridConstraints oldCs = getConstraints(component, widget);
        WGridConstraints newCs = oldCs.clone();
        newCs.setFill(value.rec().getInt(def.getName()));
        WComponentContext.setNewConstraints(newCs, def.getName(), oldCs, component, widget);
        return true;
      }
    });
    fillVD.setGetter(new VariableGetter()
    {
      @Override
      public DataTable get(Context con, VariableDefinition def, CallerController caller, RequestController request) throws ContextException
      {
        WComponent component = WidgetApiUtils.getComponentByName(widget, con.getName());
        WGridConstraints cs = getConstraints(component, widget);
        return WComponentContext.getSingleFieldDT(def, def.getName(), cs.getFill());
      }
    });
    context.addVariableDefinition(fillVD);
    
    VariableDefinition weightxVD = WComponentContext.WEIGHTX_VD.clone();
    weightxVD.setSetter(new VariableSetter()
    {
      @Override
      public boolean set(Context con, VariableDefinition def, CallerController caller, RequestController request, DataTable value) throws ContextException
      {
        WComponent component = WidgetApiUtils.getComponentByName(widget, con.getName());
        WGridConstraints oldCs = getConstraints(component, widget);
        WGridConstraints newCs = oldCs.clone();
        newCs.setWeightx(value.rec().getFloat(def.getName()));
        WComponentContext.setNewConstraints(newCs, def.getName(), oldCs, component, widget);
        return true;
      }
    });
    weightxVD.setGetter(new VariableGetter()
    {
      @Override
      public DataTable get(Context con, VariableDefinition def, CallerController caller, RequestController request) throws ContextException
      {
        WComponent component = WidgetApiUtils.getComponentByName(widget, con.getName());
        WGridConstraints cs = getConstraints(component, widget);
        return WComponentContext.getSingleFieldDT(def, def.getName(), cs.getWeightx());
      }
    });
    context.addVariableDefinition(weightxVD);
    
    VariableDefinition weightyVD = WComponentContext.WEIGHTY_VD.clone();
    weightyVD.setSetter(new VariableSetter()
    {
      @Override
      public boolean set(Context con, VariableDefinition def, CallerController caller, RequestController request, DataTable value) throws ContextException
      {
        WComponent component = WidgetApiUtils.getComponentByName(widget, con.getName());
        WGridConstraints oldCs = getConstraints(component, widget);
        WGridConstraints newCs = oldCs.clone();
        newCs.setWeighty(value.rec().getFloat(def.getName()));
        WComponentContext.setNewConstraints(newCs, def.getName(), oldCs, component, widget);
        return true;
      }
    });
    weightyVD.setGetter(new VariableGetter()
    {
      @Override
      public DataTable get(Context con, VariableDefinition def, CallerController caller, RequestController request) throws ContextException
      {
        WComponent component = WidgetApiUtils.getComponentByName(widget, con.getName());
        WGridConstraints cs = getConstraints(component, widget);
        return WComponentContext.getSingleFieldDT(def, def.getName(), cs.getWeighty());
      }
    });
    context.addVariableDefinition(weightyVD);
  }
  
  private static WGridConstraints getConstraints(WComponent component, WidgetTemplate widget)
  {
    WContainer parent = WidgetApiUtils.getComponentParent(component, widget);
    return (WGridConstraints) parent.getChildConstraints(component);
  }
  
  @Override
  public List<String> getConstraintsPropertiesForLayout()
  {
    return CONSTRAINT_PROPERTIES;
  }
}
