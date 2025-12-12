package com.tibbo.aggregate.common.widget;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.tibbo.aggregate.common.binding.Binding;
import com.tibbo.aggregate.common.binding.BindingUtils;
import com.tibbo.aggregate.common.binding.EvaluationOptions;
import com.tibbo.aggregate.common.binding.ExtendedBinding;
import com.tibbo.aggregate.common.context.ContextUtils;
import com.tibbo.aggregate.common.expression.Reference;
import com.tibbo.aggregate.common.util.StringUtils;
import com.tibbo.aggregate.common.util.SyntaxErrorException;
import com.tibbo.aggregate.common.util.Util;
import com.tibbo.aggregate.common.widget.component.WComponent;

public class WBindingUtils
{
  public static final String SUBMIT_EVENT = "submit";
  public static final String LOGOUT_EVENT = "logout";
  
  /**
   * @return all bindings where component with given name is used as reference, part of expression or activator
   */
  public static List<ExtendedBinding> getBindingsRelatedToComponent(List<ExtendedBinding> bindings, String compName) throws SyntaxErrorException
  {
    return getBindingsRelatedToEntity(bindings, compName, null);
  }
  
  public static List<ExtendedBinding> getBindingsRelatedToEntity(List<ExtendedBinding> bindings,
                                                                 String componentName,
                                                                 String entity) throws SyntaxErrorException
  {
    List<ExtendedBinding> result = getBindingsWithComponent(bindings, componentName, entity);
    final boolean acceptAllEntities = StringUtils.isEmpty(entity);
    
    for (ExtendedBinding binding : bindings)
    {
      final Reference activator = binding.getEvaluationOptions().getActivator();
      if (activator != null)
      {
        final boolean acceptEntity = acceptAllEntities || Util.equals(activator.getEntity(), entity);
        
        if (Util.equals(activator.getContext(), componentName) && acceptEntity)
        {
          addIfNotExist(result, binding);
        }
      }
    }
    return result;
  }
  
  protected static void addIfNotExist(List<ExtendedBinding> result, ExtendedBinding binding)
  {
    if (!result.contains(binding))
      result.add(binding);
  }
  
  /**
   * Returns map of bindings related to passed component name.
   * Returns only those bindings where component take part in expression or reference.
   * Activators are not processed.
   */
  public static List<ExtendedBinding> getBindingsWithComponent(List<ExtendedBinding> bindings, final String compName, final String entity) throws SyntaxErrorException
  {
    List<ExtendedBinding> result = new LinkedList<>();
    final boolean acceptAllEntities = StringUtils.isEmpty(entity);
    
    for (ExtendedBinding binding : bindings)
    {
      final Collection<Reference> componentEntityReferences = new ArrayList<>();
      for (Reference ref : binding.getAllReferences())
      {
        final boolean entityAccepted = acceptAllEntities || Util.equals(ref.getEntity(), entity);
        if (entityAccepted && Util.equals(ref.getContext(), compName))
        {
          componentEntityReferences.add(ref);
        }
      }
      
      if (!componentEntityReferences.isEmpty())
        addIfNotExist(result, binding);
      
    }
    return result;
  }
  
  /**
   * @return binding with given id
   */
  public static ExtendedBinding findBindingById(long id, List<ExtendedBinding> bindings)
  {
    for (ExtendedBinding bg : bindings)
    {
      if (bg.getBinding().getId().equals(id))
        return bg;
    }
    return null;
  }
  
  /**
   * @return component names list related to provided binding
   */
  public static Set<String> getComponentsRelatedToBinding(ExtendedBinding binding)
  {
    Set<String> list = new HashSet<>();
    
    try
    {
      for (Reference ref : binding.getAllReferences())
      {
        if (Reference.SCHEMA_FORM.equals(ref.getSchema()) && ref.getContext() != null)
        {
          list.add(ref.getContext());
        }
      }
    }
    catch (SyntaxErrorException ex)
    {
      throw new IllegalArgumentException("" + ex.getMessage(), ex);
    }
    
    return list;
  }
  
  public static HashMap<String, Set<String>> getPropertiesRelatedToBinding(ExtendedBinding b) throws SyntaxErrorException
  {
    HashMap<String, Set<String>> result = new HashMap<>();
    
    for (Reference ref : b.getAllReferences())
    {
      final boolean componentPropertyRef = Reference.SCHEMA_FORM.equals(ref.getSchema()) && ref.getEntity() != null && ContextUtils.ENTITY_VARIABLE == ref.getEntityType();
      if (componentPropertyRef)
      {
        if (!result.containsKey(ref.getContext()))
          result.put(ref.getContext(), new HashSet<>());
        
        result.get(ref.getContext()).add(ref.getEntity());
      }
    }
    
    return result;
  }
  
  /**
   * Compares two instances of EvaluationOptions. Returns true if they are equal. Otherwise returns false. Pay attantion that if both are null method returns true.
   * 
   * @param one
   *          EvaluationOptions
   * @param another
   *          EvaluationOptions
   * @return boolean
   */
  public static boolean compareEOptions(EvaluationOptions one, EvaluationOptions another)
  {
    if (one == null && another == null)
    {
      return true;
    }
    if (one == null || another == null)
    {
      return false;
    }

    if (!compareReferences(one.getActivator(), another.getActivator()) || one.getPattern() != another.getPattern() || one.getPeriod() != another.getPeriod())
    {
      return false;
    }
    return true;
  }
  
  /**
   * Compares two References. If both of them are null or their images are equal then returns true. Otherwise returns false
   * 
   * @param one
   *          Reference
   * @param another
   *          Reference
   * @return boolean
   */
  public static boolean compareReferences(Reference one, Reference another)
  {
    final boolean bothAreNulls = one == null && another == null;
    final boolean bothAreNotNulls = one != null && another != null;
    return bothAreNulls || bothAreNotNulls && one.getImage().equals(another.getImage());
  }
  
  /**
   * Returns list of all bindings related to function defined in passed Reference <code>functionRef</code>
   * 
   * @param functionRef
   *          Reference
   * @return List
   */
  public static List<ExtendedBinding> getFunctionBindings(Reference functionRef, List<ExtendedBinding> bindings) throws SyntaxErrorException
  {
    List<ExtendedBinding> list = new LinkedList<>();
    for (ExtendedBinding bg : bindings)
    {
      if (BindingUtils.isFunctionBinding(bg.getBinding(), functionRef) != null)
      {
        addIfNotExist(list, bg);
      }
    }
    return list;
  }
  
  /**
   * @return reference object that refers to certain widget component property
   */
  public static Reference getReferenceForComponentProperty(WComponent comp, String property)
  {
    Reference ref = new Reference();
    ref.setSchema(Reference.SCHEMA_FORM);
    ref.setContext(comp.getName());
    ref.setEntity(property);
    return ref;
  }
  
  /**
   * @return reference to the submit event of widget root panel
   */
  public static Reference getSubmitReference()
  {
    Reference ref = new Reference();
    ref.setSchema(Reference.SCHEMA_FORM);
    ref.setContext(WidgetConstants.ROOT_RESOURCE_ID);
    ref.setEntity(SUBMIT_EVENT);
    ref.setEntityType(ContextUtils.ENTITY_EVENT);
    return ref;
  }
  
  /**
   * @return reference to the reset event of widget root panel
   */
  public static Reference getResetReference()
  {
    Reference ref = new Reference();
    ref.setSchema(Reference.SCHEMA_FORM);
    ref.setContext(WidgetConstants.ROOT_RESOURCE_ID);
    ref.setEntity(LOGOUT_EVENT);
    ref.setEntityType(ContextUtils.ENTITY_EVENT);
    return ref;
  }
  
  /**
   * @return all found bindings that executes provided script
   */
  public static List<ExtendedBinding> findScriptExecuteBindings(String script, List<ExtendedBinding> bindings)
  {
    List<ExtendedBinding> bs = new LinkedList<>();
    for (ExtendedBinding b : bindings)
    {
      if (Reference.SCHEMA_FORM.equals(b.getBinding().getTarget().getSchema()) && b.getBinding().getTarget().getEntityType() == ContextUtils.ENTITY_FUNCTION
          && b.getBinding().getTarget().getEntity().equals(script))
      {
        addIfNotExist(bs, b);
      }
    }
    
    return bs;
  }
  
  public static ExtendedBinding getDuplicateIDBinding(List<ExtendedBinding> bindings, Binding bg)
  {
    for (ExtendedBinding b : bindings)
    {
      if (b.getBinding().getId().equals(bg.getId()))
      {
        return b;
      }
    }
    return null;
  }
}
