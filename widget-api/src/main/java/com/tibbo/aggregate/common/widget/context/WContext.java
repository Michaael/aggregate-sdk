package com.tibbo.aggregate.common.widget.context;

import java.beans.PropertyChangeEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import org.apache.commons.beanutils.PropertyUtils;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.Log;
import com.tibbo.aggregate.common.context.AbstractContext;
import com.tibbo.aggregate.common.context.CallerController;
import com.tibbo.aggregate.common.context.ContextException;
import com.tibbo.aggregate.common.context.ContextUtils;
import com.tibbo.aggregate.common.context.DefaultRequestController;
import com.tibbo.aggregate.common.context.RequestController;
import com.tibbo.aggregate.common.context.VariableDefinition;
import com.tibbo.aggregate.common.context.VariableGetter;
import com.tibbo.aggregate.common.context.VariableSetter;
import com.tibbo.aggregate.common.datatable.AggreGateBean;
import com.tibbo.aggregate.common.datatable.DataRecord;
import com.tibbo.aggregate.common.datatable.DataTable;
import com.tibbo.aggregate.common.datatable.DataTableConversion;
import com.tibbo.aggregate.common.datatable.DataTableException;
import com.tibbo.aggregate.common.datatable.DataTableReplication;
import com.tibbo.aggregate.common.datatable.FieldFormat;
import com.tibbo.aggregate.common.datatable.SimpleDataTable;
import com.tibbo.aggregate.common.datatable.TableFormat;
import com.tibbo.aggregate.common.datatable.converter.FormatConverter;
import com.tibbo.aggregate.common.expression.Reference;
import com.tibbo.aggregate.common.structure.Pinpoint;
import com.tibbo.aggregate.common.util.Util;
import com.tibbo.aggregate.common.widget.WidgetConstants;
import com.tibbo.aggregate.common.widget.component.IComponent;
import com.tibbo.aggregate.common.widget.component.WAbstractComponent;

/**
 * Description: Common Context wrapper for TemplateElement. It provides the possibility to work wit TemplateElement as with Context. For example to represent TemplateElement properties in
 * <code>PropertiesEditor</code>.
 */
public abstract class WContext<C extends WContext, T extends IComponent> extends AbstractContext<C>
{
  public static final String GROUP_CONSTRAINTS = ContextUtils.createGroup(ContextUtils.GROUP_DEFAULT, Cres.get().getString("wConstraints"));
  
  public static final TableFormat VFT_GRID_WIDTH = new TableFormat(1, 1,
      "<" + WidgetConstants.V_GRID_WIDTH + "><I><A=1><D=" + Cres.get().getString("gridWidth") + "><V=<L=1 " + Integer.MAX_VALUE + ">>");
  public static final TableFormat VFT_GRID_HEIGHT = new TableFormat(1, 1,
      "<" + WidgetConstants.V_GRID_HEIGHT + "><I><A=1><D=" + Cres.get().getString("gridHeight") + "><V=<L=1 " + Integer.MAX_VALUE + ">>");
  
  public static final TableFormat VFT_GRIDX = new TableFormat(1, 1, "<" + WidgetConstants.V_GRIDX + "><I><F=C><D=" + Cres.get().getString("column") + "><V=<L=0 " + Integer.MAX_VALUE + ">>");
  public static final TableFormat VFT_GRIDY = new TableFormat(1, 1, "<" + WidgetConstants.V_GRIDY + "><I><F=C><D=" + Cres.get().getString("row") + "><V=<L=0 " + Integer.MAX_VALUE + ">>");
  
  public static final VariableDefinition GRIDX_VD = new VariableDefinition(WidgetConstants.V_GRIDX, VFT_GRIDX, true, true, Cres.get().getString("column"), GROUP_CONSTRAINTS);
  public static final VariableDefinition GRIDY_VD = new VariableDefinition(WidgetConstants.V_GRIDY, VFT_GRIDY, true, true, Cres.get().getString("row"), GROUP_CONSTRAINTS);
  public static final VariableDefinition GRID_WIDTH_VD = new VariableDefinition(WidgetConstants.V_GRID_WIDTH, VFT_GRID_WIDTH, true, true, Cres.get().getString("wColumnSpan"), GROUP_CONSTRAINTS);
  public static final VariableDefinition GRID_HEIGHT_VD = new VariableDefinition(WidgetConstants.V_GRID_HEIGHT, VFT_GRID_HEIGHT, true, true, Cres.get().getString("wRowSpan"), GROUP_CONSTRAINTS);
  
  protected final MutablePropertyChangeListener propertyListener;
  private final T component;
  
  private String defaultPropertyName;
  
  private VariableGetter customPropsGetter;
  private VariableSetter customPropsSetter;
  
  /**
   * Constructor receives element object to be wrapped
   *
   * @param component
   *          Component
   */
  public WContext(T component)
  {
    super(component.getName());
    this.component = component;
    setValueCheckingEnabled(false);
    propertyListener = createPropertyListener();
    component.addPropertyChangeListener(propertyListener);
  }
  
  protected MutablePropertyChangeListener createPropertyListener()
  {
    return new ComponentPropertyChangeListener(this);
  }

  
  public T getComponent()
  {
    return component;
  }
  
  /**
   * Components can be renamed. So we need ability to set new context name.
   *
   * @param name
   *          String
   */
  @Override
  public void setName(String name)
  {
    super.setName(name);
    if (getComponent() != null)
    {
      getComponent().setName(name);
    }
  }
  
  @Override
  public void stop()
  {
    super.stop();
    component.removePropertyChangeListener(propertyListener);
  }
  
  /**
   * Rename context only without renaming a component as setName(name) does.
   *
   * @param name
   *          Name
   */
  public void setContextName(String name)
  {
    super.setName(name);
  }
  
  public void addDefaultVariableDefinition(VariableDefinition vd)
  {
    addVariableDefinition(vd);
    defaultPropertyName = vd.getName();
  }
  
  protected String getPropertyNameByReference(Reference destination)
  {
    return destination.getEntity() != null ? destination.getEntity() : getDefaultPropertyName();
  }
  
  public String getDefaultPropertyName()
  {
    return defaultPropertyName;
  }
  
  public void setDefaultPropertyName(String defaultPropertyName)
  {
    this.defaultPropertyName = defaultPropertyName;
  }
  
  public void writeReference(Reference destination, Object value, @Nullable Pinpoint pinpoint) throws ContextException
  {
    String property = getPropertyNameByReference(destination);
    
    try
    {
      T currentComponent = getComponent();
      Class propertyClass = getPropertyType(currentComponent, property);
      if (destination.getRow() == null
          && destination.getProperty() == null
          && propertyClass != null
          && value != null
          && propertyClass.isAssignableFrom(value.getClass()))
      {
        if (value instanceof DataTable)
        {
          // In case of datatable the value must be cloned to prevent "leaking out" through the binding(s)
          DataTable dataTableValue = ((DataTable) value).clone();
          setProperty(currentComponent, property, dataTableValue);
        }
        else
        {
          setProperty(currentComponent, property, value);
        }
      }
      else
      {
        // Mute notifications for the property to prevent double context variable update events by WComponent
        propertyListener.muteUpdateEvents(property);
        try
        {
          setVariable(property, destination.getField(), destination.getRow(), value, pinpoint);
        }
        finally
        {
          propertyListener.allowUpdateEvents(property);
        }
      }
    }
    catch (Exception ex)
    {
      throw new ContextException(ex);
    }
  }
  
  public void setVariable(String variable, Object value) throws ContextException
  {
    setVariable(variable, null, null, value);
  }

  /**
   * Sets value for property specified by <code>varName</code> parameter. Does some necessary prolegomenous operations over provided variable value.
   *
   * @param variable
   *          Variable name
   * @param field
   *          Field name
   * @param row
   *          Row
   * @param value
   *          Value
   * @throws ContextException
   *           Exception
   */
  public void setVariable(String variable, String field, Integer row, Object value) throws ContextException
  {
    setVariable(variable, field, row, value, null);
  }

  /**
   * Sets value for property specified by <code>varName</code> parameter. Does some necessary prolegomenous operations
   * over provided variable value.
   *
   * @param variable
   *     Variable name
   * @param field
   *     Field name
   * @param row
   *     Row
   * @param value
   *     Value
   * @param pinpoint
   *     Origin of the operation
   * @throws ContextException
   *     Exception
   */
  public void setVariable(String variable, String field, Integer row, Object value, @Nullable Pinpoint pinpoint) throws ContextException
  {
    VariableDefinition vd = getVariableDefinition(variable);
    
    if (vd == null)
    {
      throw new ContextException(Cres.get().getString("conVarNotAvail") + variable);
    }

    RequestController request = new DefaultRequestController();
    request.assignPinpoint(pinpoint);   // for observability

    if (field == null && row == null && value instanceof DataTable)
    {
      DataTable table = (DataTable) value;
      DataTable oldTable = getVariable(variable);
      
      if (!Util.equals(table, oldTable))
      {
        setVariable(variable, null, request, table);
      }
    }
    else
    {
      // TODO: commented code is old one. New code line is more correct. But let's wait abit before remove old one...
      // DataTable table = getComponentVariable(vd);
      DataTable table = getVariable(variable);
      
      int fieldIndex = field != null ? table.getFormat().getFieldIndex(field) : 0;
      
      if (fieldIndex == -1)
      {
        throw new ContextException(Cres.get().getString("dtFieldNotAvail") + field);
      }
      
      int recordIndex = row != null ? row : 0;
      
      if (table.getRecordCount() <= recordIndex)
      {
        throw new ContextException(Cres.get().getString("dtRecordNotAvail") + recordIndex);
      }
      
      Object oldValue = table.getRecord(recordIndex).getValue(fieldIndex);
      
      Object newValue = DataTableConversion.convertValueToField(table.getRecord(recordIndex).getFormat(fieldIndex), value);
      
      if (!Util.equals(oldValue, newValue))
      {
        table = table.clone();
        table.getRecord(recordIndex).setValue(fieldIndex, newValue);
        setVariable(variable, null, request, table);
      }
    }
  }
  
  /**
   * Overrides <code>AbstractContext</code> method. It calls <code>setComponentVariable</code> method.
   */
  @Override
  protected boolean setVariableImpl(VariableDefinition def, CallerController caller, RequestController rc, DataTable value) throws ContextException
  {
    return setComponentVariable(def, value);
  }
  
  /**
   * This method uses reflection to implement automatic property value setting where only corresponding component setter method invocation needed. This means that setter method name should answer
   * purposes of JavaBean specification. Also it is necessary that property and variable names match each other. This method does necessary manipulations to set new property value. Each
   * WComponentContext realization can define additional properties. But it inherits ancestor Variables. In some cases to provide custom setter to some property when you can not use setter for
   * variable definition you can override this method. But don't forget to invoke super method.
   *
   * @param vd
   *          VariableDefinition
   * @param value
   *          DataTable
   * @throws ContextException
   *           ContextException
   * @return Is variable successfuly set
   */
  abstract protected boolean setComponentVariable(VariableDefinition vd, DataTable value) throws ContextException;
  
  protected void setComponentVariableAsProperty(VariableDefinition vd, DataTable value, IComponent component) throws ContextException
  {
    try
    {
      Object val;
      
      Class propertyType = getPropertyType(component, vd.getName());
      
      if (List.class.isAssignableFrom(propertyType))
      {
        val = DataTableConversion.beansFromTable(value, vd.getValueClass(), vd.getFormat(), true);
      }
      else if (Map.class.isAssignableFrom(propertyType))
      {
        val = prepareMapValue(vd, value);
      }
      else if (AggreGateBean.class.isAssignableFrom(propertyType))
      {
        val = DataTableConversion.createAggreGateBean(value, propertyType);
      }
      else
      {
        val = prepareValue(vd, value, component, propertyType);
      }
      setProperty(component, vd.getName(), val);
    }
    catch (InvocationTargetException ex)
    {
      throw new ContextException("Error setting property '" + vd.getName() + "': " + ex.getCause().getMessage(), ex);
    }
    catch (Exception ex)
    {
      throw new ContextException("Error setting property '" + vd.getName() + "': " + ex.getMessage(), ex);
    }
  }
  
  private Object prepareValue(VariableDefinition vd, DataTable value, IComponent component, Class propertyType) throws Exception
  {
    Object result;
    FormatConverter<Object> fc = DataTableConversion.getFormatConverter(propertyType);
    
    Object original = getProperty(component, vd.getName());
    
    if (fc != null)
    {
      result = fc.convertToBean(value, original);
    }
    else
    {
      if (DataTable.class.isAssignableFrom(propertyType))
      {
        result = value.clone();
      }
      else
      {
        result = value.rec().getValue(0);
      }
    }
    return result;
  }
  
  private Map prepareMapValue(VariableDefinition vd, DataTable value) throws DataTableException
  {
    Map<Object, Object> result = new LinkedHashMap<Object, Object>();
    for (DataRecord rec : value)
    {
      Object mkey = DataTableConversion.convertValueFromField(rec.getValue(0));
      // when rec.getFieldCount() > 2 consider that bean fields are stored in plain format (at the same level with key)
      Object mvalue = (rec.getFieldCount() > 2) ? DataTableConversion.beanFromRecord(rec, vd.getValueClass(), vd.getFormat(), true)
          : DataTableConversion.convertValueFromField(rec.getValue(1), vd.getValueClass());
      
      result.put(mkey, mvalue);
    }
    return result;
  }
  
  /**
   * Overrides <code>AbstractContext</code> method. It calls <code>getComponentVariable</code> method.
   *
   * @param def
   *          VariableDefinition
   * @param caller
   *          CallerController
   * @param request
   *          RequestController
   * @return DataTable
   * @throws ContextException
   */
  @Override
  protected DataTable getVariableImpl(VariableDefinition def, CallerController caller, RequestController request) throws ContextException
  {
    DataTable dt = getComponentVariable(def);
    if (dt == null)
    {
      Log.WIDGETS.warn("Variable '" + def.getName() + "' value is null in component: " + getName());
    }
    
    return dt;
  }
  
  /**
   * This method uses reflection to implement automatic property value getting where only corresponding getter method invocation needed. This means that getter method name should answer purposes of
   * JavaBean specification. Also it is necessary that property and variable names match each other. Each component context realization can define additional properties. But it inherits ancestor
   * Variables. There is a standard method to provide custom getter and setter for property (to set a setter and getter to corresponding variable definition). But in some cases when we can not use
   * them we can override this method. Don't forget to invoke super method otherwise you'll break ancestor properties.
   *
   * @param vd
   *          VariableDefinition
   * @return DataTable
   */
  abstract protected DataTable getComponentVariable(VariableDefinition vd);
  
  protected DataTable getComponentVariableAsProperty(VariableDefinition vd, IComponent component)
  {
    String name = vd.getName();
    try
    {
      Class propertyType = getPropertyType(component, name);
      
      Object value = getProperty(component, name);
      
      FormatConverter<Object> fc = DataTableConversion.getFormatConverter(propertyType);
      
      if (fc != null)
      {
        value = fc.convertToTable(value, vd.getFormat());
      }
      
      if (value != null)
      {
        if (value instanceof AggreGateBean)
          return ((AggreGateBean) value).toDataTable();
        
        if (value instanceof List)
          return prepareTableFromList(vd, (List) value);
        
        if (value instanceof Map)
          return prepareTableFromMap(vd, (Map) value);
      }
      
      if (value instanceof DataTable)
      {
        return (DataTable) value;
      }
      return vd.getFormat() != null ? new SimpleDataTable(vd.getFormat(), value) : new SimpleDataTable();
    }
    catch (Exception ex)
    {
      throw new IllegalStateException("Error getting property '" + name + "': " + ex.getMessage(), ex);
    }
  }
  
  protected Object getProperty(IComponent component, String name) throws Exception
  {
    return PropertyUtils.getProperty(component, name);
  }
  
  protected Class getPropertyType(IComponent component, String name) throws Exception
  {
    return PropertyUtils.getPropertyType(component, name);
  }
  
  protected void setProperty(IComponent component, String name, Object value) throws Exception
  {
    PropertyUtils.setProperty(component, name, value);
  }
  
  private DataTable prepareTableFromMap(VariableDefinition vd, Map map) throws DataTableException
  {
    DataTable res = new SimpleDataTable(vd.getFormat());
    final FieldFormat keyFormat = res.getFormat(0);
    
    // when res.getFieldCount() > 2 consider that bean fields are stored in plain format (at the same record level with key)
    if (res.getFieldCount() > 2)
    {
      // skip key field during bean population
      LinkedHashSet<String> skipFields = new LinkedHashSet<String>(Arrays.asList(keyFormat.getName()));
      for (Object key : map.keySet())
      {
        final DataRecord rec = DataTableConversion.beanToRecord(map.get(key), res.getFormat(), true, false, skipFields);
        rec.setValue(0, DataTableConversion.convertValueToField(keyFormat, key));
        res.addRecord(rec);
      }
    }
    else
    {
      FieldFormat valueFormat = res.getFormat(1);
      for (Object key : map.keySet())
      {
        res.addRecord(DataTableConversion.convertValueToField(keyFormat, key), DataTableConversion.convertValueToField(valueFormat, map.get(key), vd.getValueClass()));
      }
    }
    return res;
  }
  
  private DataTable prepareTableFromList(VariableDefinition vd, List list) throws DataTableException
  {
    FormatConverter<Object> valueClassFC = DataTableConversion.getFormatConverter(vd.getValueClass());
    if (valueClassFC != null)
    {
      final DataTable table = new SimpleDataTable(vd.getFormat());
      for (Object bean : list)
      {
        final DataRecord rec = ((DataTable) valueClassFC.convertToTable(bean)).rec();
        try
        {
          table.addRecord(rec);
        }
        catch (Exception ex)
        {
          DataRecord nr = table.addRecord();
          DataTableReplication.copyRecord(rec, nr, true, true);
        }
      }
      return table;
    }
    else
      return DataTableConversion.beansToTable(list, vd.getFormat(), true);
  }
  
  /**
   * This method creates Variable Definitions for Elements properties. Each TemplateElement realization can define additional properties. That is why it must override this method. But it inherits
   * ancestor Variables. Therefore it must invoke super method.
   */
  protected abstract void createVariableDefinitions();
  
  public abstract void componentPropertyChanged(PropertyChangeEvent evt);
  
  protected VariableGetter getCustomPropsGetter()
  {
    if (customPropsGetter == null)
    {
      customPropsGetter = (context, variableDef, caller, request) -> getComponent().getCustomProperty(variableDef.getName());
    }
    
    return customPropsGetter;
  }
  
  protected VariableSetter getCustomPropsSetter()
  {
    if (customPropsSetter == null)
    {
      customPropsSetter = (context, variableDef, caller, request, value) -> {
        getComponent().setCustomProperty(variableDef.getName(), value);
        return true;
      };
    }
    
    return customPropsSetter;
  }
  
  public void addCustomPropertyVD(String name, String description, String varHelp, final TableFormat format, final boolean initCustomProperty)
  {
    addCustomPropertyVDToGroup(name, description, varHelp, format, initCustomProperty, WidgetConstants.GROUP_CUSTOM_PROPERTIES);
  }
  
  public void recreateCustomProperties(DataTable properties)
  {
    if (getVariableDefinition(WAbstractComponent.V_CUSTOM_PROPERTIES) == null || properties == null)
    {
      return;
    }
    for (VariableDefinition def : getVariableDefinitions())
    {
      if (WidgetConstants.GROUP_CUSTOM_PROPERTIES.equals(def.getGroup()))
      {
        removeVariableDefinition(def.getName());
      }
    }
    for (DataRecord rec : properties)
    {
      String vName = rec.getString(WidgetConstants.F_NAME);
      DataTable value = rec.getDataTable(WidgetConstants.F_VALUE);
      
      String vDescription = rec.getString(WidgetConstants.F_DESCRIPTION);
      if (vDescription == null || vDescription.isEmpty())
      {
        if (value.getRecordCount() > 0)
        {
          vDescription = value.getFormat(0).getDescription();
        }
        else
        {
          vDescription = Util.nameToDescription(vName);
        }
      }
      
      String vHelp = rec.getString(WidgetConstants.F_HELP);
      if (vHelp != null && vHelp.isEmpty())
      {
        vHelp = null;
      }
      
      addCustomPropertyVD(vName, vDescription, vHelp, value.getFormat(), false);
    }
  }
  
  public void recreateCustomProperties()
  {
    if (getVariableDefinition(WAbstractComponent.V_CUSTOM_PROPERTIES) == null)
    {
      return;
    }
    try
    {
      recreateCustomProperties(getVariable(WAbstractComponent.V_CUSTOM_PROPERTIES));
    }
    catch (ContextException e)
    {
      Log.GUIBUILDER.error("Failed to recreate custom properties of '" + getName() + "' component: " + e.getMessage(), e);
    }
  }
  
  protected void addCustomPropertyVDToGroup(String name, String description, String varHelp, TableFormat format, boolean initCustomProperty, String group)
  {
    if (getVariableDefinition(name) == null)
    {
      if (initCustomProperty)
      {
        getComponent().setOrCreateCustomProperty(name, new SimpleDataTable(format, true), description, varHelp);
      }
      
      final VariableDefinition vd = new VariableDefinition(name, format, true, true, description, group);
      
      vd.setGetter(getCustomPropsGetter());
      vd.setSetter(getCustomPropsSetter());
      vd.setHelp(varHelp);
      addVariableDefinition(vd);
    }
  }
}
