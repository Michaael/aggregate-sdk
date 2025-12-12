package com.tibbo.aggregate.client.guibuilder.renderers;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.border.*;

import com.tibbo.aggregate.client.guibuilder.*;
import com.tibbo.aggregate.client.guibuilder.editableComponent.*;
import com.tibbo.aggregate.common.*;
import com.tibbo.aggregate.common.binding.*;
import com.tibbo.aggregate.common.context.*;
import com.tibbo.aggregate.common.expression.*;
import com.tibbo.aggregate.common.util.*;
import com.tibbo.aggregate.common.widget.*;
import com.tibbo.aggregate.common.widget.component.*;
import com.tibbo.aggregate.common.widget.context.*;
import com.tibbo.aggregate.common.widget.runtime.renderers.*;
import com.tibbo.aggregate.common.widget.runtime.util.*;
import com.tibbo.aggregate.component.entityselector.*;

public abstract class EditorRendererSupport<C extends WAbstractComponent, T extends WAbstractContext<WComponentContext, C>, O extends JComponent, U extends EditableComponent>
    extends SwingRendererSupport<C, T, O>
{
  private U editableComponent;
  private GridBagConstraints constraints;
  private JPanel panelWithHelper;
  
  public EditorRendererSupport(AbstractSwingRenderer<C, T, O, SwingRendererSupport> renderer)
  {
    super(renderer);
  }
  
  /**
   * Sets etched border to itself to outline container cell occupied by represented element. Adds editor mode border to editable component.
   */
  public void addEditorModeDecorations()
  {
    if (!getRenderer().getComponentContext().getName().equals(WidgetConstants.ROOT_RESOURCE_ID))
    {
      getProduction().setBorder(BorderFactory.createEtchedBorder());
    }
    
    Border b = getEditableComponent().getEditorModeBorder();
    
    if (!WidgetComponentApiRendererUtilities.hasCustomBorder(getRenderer().getComponentContext().getComponent()) && b != null)
    {
      getEditableComponent().setBorder(b);
    }
    
    initView();
  }
  
  /**
   * Removes etched border that outlines container cell occupied by represented element. Removes editor mode border from editable component.
   */
  public void removeEditorModeDecorations()
  {
    if (!getRenderer().getComponentContext().getName().equals(WidgetConstants.ROOT_RESOURCE_ID))
    {
      getProduction().setBorder(null);
    }
    
    Border b = getEditableComponent().getEditorModeBorder();
    if (!WidgetComponentApiRendererUtilities.hasCustomBorder(getRenderer().getComponentContext().getComponent()) && b != null)
    {
      getEditableComponent().setBorder(null);
    }
    getProduction().revalidate();
  }
  
  public U getEditableComponent()
  {
    return editableComponent;
  }
  
  protected abstract U createEditableComponent();
  
  public void recreateEditableComponent()
  {
    @SuppressWarnings("unlikely-arg-type")
    boolean reset = editableComponent != null && getGuiBuilder().getWorkPane().getSelectedNodes().contains(getRenderer().getComponentContext());
    // Previous editable component mouse listeners should be removed
    if (editableComponent != null)
    {
      editableComponent.removeMouseListener((MouseListener) getGuiBuilder().getWorkPane().getSizeInsetsEditingListener());
      editableComponent.removeMouseMotionListener((MouseMotionListener) getGuiBuilder().getWorkPane().getSizeInsetsEditingListener());
      editableComponent.stop();
    }
    
    editableComponent = createEditableComponent();
    
    initView();
    
    if (getGuiBuilder().getViewer() != null && getGuiBuilder().getViewer().isDecoratedMode() && getGuiBuilder().getDataModel().getResourceByID(getRenderer().getComponentContext().getName()) != null)
    {
      addEditorModeDecorations();
    }
    getEditableComponent().addMouseListener((MouseListener) getGuiBuilder().getWorkPane().getSizeInsetsEditingListener());
    getEditableComponent().addMouseMotionListener((MouseMotionListener) getGuiBuilder().getWorkPane().getSizeInsetsEditingListener());
    if (reset)
    {
      getGuiBuilder().getWorkPane().repaint();
    }
  }
  
  public AggreGateIDE getGuiBuilder()
  {
    return (AggreGateIDE) getRenderer().getEngine();
  }
  
  protected PanelWithPaintHelper createPanelWithHelper()
  {
    return new PanelWithPaintHelper();
  }
  
  @Override
  public void initView()
  {
    if (panelWithHelper == null)
    {
      panelWithHelper = createPanelWithHelper();
    }
    
    // Initializing back that represent container cell occupied by element
    panelWithHelper.setLayout(new GridBagLayout());
    panelWithHelper.setOpaque(false);
    
    if (constraints == null)
    {
      constraints = new GridBagConstraints();
    }
    constraints.gridx = 0;
    constraints.gridy = 0;
    constraints.gridwidth = 1;
    constraints.gridheight = 1;
    constraints.weightx = 1;
    constraints.weighty = 1;
    
    if (getEditableComponent() == null)
      recreateEditableComponent();
    
    reinitConstraints();
    
    C component = getRenderer().getComponent();
    O render = getRenderer().getRender();
    WidgetComponentApiRendererUtilities.initBasicProperties(getRenderer().getComponentContext(), component, render, getRenderer().getEngine().getServerContextManager());
    WidgetComponentApiRendererUtilities.setRendererSize(render, component);
    
    getEditableComponent().reviewRender();
    getEditableComponent().setToolTipText(getRenderer().getComponentContext().getName() + " (" + component.getDescription() + ")");
  }
  
  public void reinitConstraints()
  {
    if (!getRenderer().getComponentContext().getName().equals(WidgetConstants.ROOT_RESOURCE_ID))
    {
      refreshInternalConstraints();
    }
    
    Component editableComponent = (Component) getEditableComponent();
    
    if (editableComponent.getParent() != panelWithHelper)
    {
      panelWithHelper.removeAll();
      panelWithHelper.add(editableComponent, constraints);
    }
    else
    {
      GridBagLayout layout = (GridBagLayout) panelWithHelper.getLayout();
      layout.setConstraints(editableComponent, constraints);
      layout.layoutContainer(panelWithHelper);
    }
    
    // Hack for VectorDrawing components be properly resized
    // Because of AbstractJSVGComponent repaints only on getting ComponentEvent.COMPONENT_RESIZED,
    // but this event is drown by panelWithHelper.removeAll();
    if (WidgetConstants.COMPONENT_VECTOR_DRAWING.equals(getRenderer().getComponent().getKey()))
      getRenderer().getRender().dispatchEvent(new ComponentEvent((Component) getEditableComponent(), ComponentEvent.COMPONENT_RESIZED));
    
    // TODO: see earlier commits for another version of this method that was never working but may be used someday
  }
  
  private void refreshInternalConstraints()
  {
    WContainer parent = WidgetApiUtils.getComponentParent(getRenderer().getComponent(), getRenderer().getComponentContext().getWidget());
    
    if (parent == null)
    {
      return;
    }
    
    if (parent.getLayout() == WidgetConstants.GRID_LAYOUT)
    {
      WGridConstraints cs = null;
      if (parent.getChildren().contains(getRenderer().getComponent()))
      {
        cs = (WGridConstraints) parent.getChildConstraints(getRenderer().getComponent());
      }
      else
      {
        Log.GUIBUILDER.debug("Unable to determine GridConstraints for " + getRenderer().getComponent().getName() + "; Default constraints will be used");
        
        cs = (WGridConstraints) WidgetApiUtils.getComponentDefaultConstraints(parent, parent.getLayout());
      }
      
      constraints.anchor = cs.getAnchor();
      constraints.fill = cs.getFill();
      constraints.insets.bottom = cs.getInsetsBottom();
      constraints.insets.left = cs.getInsetsLeft();
      constraints.insets.right = cs.getInsetsRight();
      constraints.insets.top = cs.getInsetsTop();
    }
    else if (parent.getLayout() == WidgetConstants.ABSOLUTE_LAYOUT)
    {
      constraints.insets.set(0, 0, 0, 0);
      constraints.anchor = GridBagConstraints.NORTHWEST;
    }
    
  }
  
  @Override
  public JPanel getProduction()
  {
    return panelWithHelper;
  }
  
  @Override
  public void resetRender()
  {
    recreateEditableComponent();
    
    /* Widget-API: the block below is moved from SwingRendererSupport instead of super.resetRender();
     * It helps to avoid using AbstractSwingViewer in Widget-API.
     */
    
    initView();
    
    WContainerContext parent = getRenderer().getEngine().getViewer().getParentContext(getRenderer().getComponentContext());
    
    // getProduction() of parent will force its recomposition
    ((WidgetComponentRenderer) getRenderer().getEngine().getViewer().getComponentRenderer(parent)).getProduction();
  }
  
  public void resetWidth()
  {
    int newWidth;
    if (getRenderer().getComponent().getWidth() == 0)
    {
      newWidth = getRenderer().getRender().getPreferredSize().width;
    }
    else
    {
      newWidth = getRenderer().getComponentContext().getComponent().getWidth();
    }
    
    Dimension d = getRenderer().getRender().getPreferredSize();
    d.width = newWidth;
    resizeEditableComponent(d);
    reinitConstraints();
  }
  
  public void resetHeight()
  {
    int newHeight;
    if (getRenderer().getComponent().getHeight() == 0)
    {
      getRenderer().setRender(null);
      
      newHeight = getRenderer().getRender().getPreferredSize().height;
      initView();
    }
    else
    {
      newHeight = getRenderer().getComponent().getHeight();
    }
    Dimension d = getRenderer().getRender().getPreferredSize();
    d.height = newHeight;
    
    resizeEditableComponent(d);
    reinitConstraints();
  }
  
  @SuppressWarnings("unlikely-arg-type")
  private void resizeEditableComponent(Dimension d)
  {
    getRenderer().getRender().setPreferredSize(d);
    if (getGuiBuilder().getWorkPane().getSelectedNodes().contains(getRenderer().getComponentContext()))
    {
      getGuiBuilder().getWorkPane().repaint();
    }
  }
  
  /**
   * Returns map of bindings that will be created when Reference <code>ref</code> imported to component <code>component</code>
   */
  public List<ExtendedBinding> getCreatedBindings(Reference ref, EntityNode en) throws SyntaxErrorException
  {
    List<ExtendedBinding> bs = new LinkedList<ExtendedBinding>();
    Reference elementRef = WBindingUtils.getReferenceForComponentProperty(getRenderer().getComponent(), getRenderer().getComponentContext().getDefaultPropertyName());
    
    if (ref.getEntityType() == ContextUtils.ENTITY_FUNCTION)
    {
      addFunctionBindings(ref, en, bs, elementRef);
    }
    else if (ref.getEntityType() == ContextUtils.ENTITY_EVENT)
    {
      EvaluationOptions eOpts = new EvaluationOptions(EvaluationOptions.EVENT);
      eOpts.setActivator(ref);
      bs.add(new ExtendedBinding(new Binding(elementRef, new Expression(ref)), eOpts));
    }
    else if (ref.getContext() != null)
    {
      // Creating ref -> elementRef submit binding
      if (!en.isReadOnly())
      {
        EvaluationOptions eOpts = new EvaluationOptions(EvaluationOptions.EVENT);
        eOpts.setActivator(WBindingUtils.getSubmitReference());
        bs.add(new ExtendedBinding(new Binding(ref, new Expression(elementRef)), eOpts));
      }
      
      // Creating ref -> elementRef reset and startup binding
      EvaluationOptions eOpts = new EvaluationOptions(EvaluationOptions.EVENT | EvaluationOptions.STARTUP);
      bs.add(new ExtendedBinding(new Binding(elementRef, new Expression(ref)), eOpts));
    }
    
    return bs;
  }
  
  private void addFunctionBindings(Reference ref, EntityNode en, List<ExtendedBinding> bs, Reference elementRef) throws SyntaxErrorException
  {
    if (ref.getField() == null)
    {
      Reference activator = new Reference();
      activator.setSchema(Reference.SCHEMA_FORM);
      activator.setContext(elementRef.getContext());
      activator.setEntityType(ContextUtils.ENTITY_EVENT);
      activator.setEntity(WAbstractContext.E_MOUSE_CLICKED);
      
      for (ExtendedBinding fbg : WBindingUtils.getFunctionBindings(ref, getGuiBuilder().getWidget().getBindings()))
      {
        EvaluationOptions eo = fbg.getEvaluationOptions().clone();
        eo.setActivator(activator);
        bs.add(new ExtendedBinding(fbg.getBinding(), eo));
      }
      // Adding call function binding
      Binding callBg = new Binding(ref, null);
      EvaluationOptions eo = new EvaluationOptions(false, true, 0);
      eo.setActivator(activator);
      bs.add(new ExtendedBinding(callBg, eo));
    }
    else if (en != null && en.getEntityObject().isInputField())
    {
      // If input field of function
      EvaluationOptions eOpts = new EvaluationOptions(EvaluationOptions.EVENT);
      inheritFunctionActivator(ref, eOpts);
      bs.add(new ExtendedBinding(new Binding(ref, new Expression(elementRef)), eOpts));
    }
    else if (en != null && !en.getEntityObject().isInputField())
    {
      // If output field of function
      EvaluationOptions eOpts = new EvaluationOptions(EvaluationOptions.EVENT);
      inheritFunctionActivator(ref, eOpts);
      bs.add(new ExtendedBinding(new Binding(elementRef, new Expression(ref)), eOpts));
    }
  }
  
  /**
   * Tries to inherit activator from existing function bindings
   */
  private void inheritFunctionActivator(Reference ref, EvaluationOptions eOpts) throws SyntaxErrorException
  {
    List<ExtendedBinding> bgs = WBindingUtils.getFunctionBindings(ref, getGuiBuilder().getWidget().getBindings());
    if (bgs.size() == 0)
    {
      return;
    }
    ExtendedBinding fb = bgs.get(0);
    if (fb != null)
    {
      eOpts.setActivator(fb.getEvaluationOptions().getActivator());
    }
  }
  
  public CustomTransferHandler getCustomTransferHandler(DataFlavor flavor)
  {
    return null;
  }
  
  public void refreshParentWidth()
  {
    WContainerContext parent = getGuiBuilder().getViewer().getParentContext(getRenderer().getComponentContext());
    if (parent != null && getGuiBuilder().getTemplateChangesManager() != null)
    {
      getGuiBuilder().getTemplateChangesManager().deferComponentSizeRevalidation(parent.getComponent());
    }
  }
  
  /**
   * Implements necessary operations to force renderer represent property changes in relevant element. If no any special handler for property found method <code>synchronizeProperty</code> invoked. It
   * implements automatic invocation of corresponding getter in element and setter in representation object. If automatical reaction is not suitable for some property corresponding handler section
   * must be present obviously.
   */
  @SuppressWarnings("unlikely-arg-type")
  @Override
  public void componentPropertyChanged(String prop)
  {
    if (WidgetConstants.CONSTRAINTS_PROPERTIES_LIST.contains(prop))
    {
      reinitConstraints();
      getEditableComponent().refreshHandlers();
    }
    else if (prop.equals(WComponent.V_WIDTH))
    {
      resetWidth();
    }
    else if (prop.equals(WComponent.V_HEIGHT))
    {
      resetHeight();
    }
    else if (prop.equals(WAbstractComponent.V_BORDER))
    {
      getRenderer().getRender().setBorder(getRenderer().getComponent().getBorder());
      if (getGuiBuilder().getViewer().isDecoratedMode())
      {
        addEditorModeDecorations();
      }
      
      resetWidth();
      resetHeight();
      refreshParentWidth();
    }
    else if (prop.equals(WAbstractComponent.V_FONT))
    {
      if (getRenderer().getComponent().getFont() != null)
      {
        getRenderer().getRender().setFont(getRenderer().getComponent().getFont());
      }
      resetWidth();
      resetHeight();
      refreshParentWidth();
    }
    else if (prop.equals(WAbstractComponent.V_CURSOR))
    {
      if (getRenderer().getComponent().getCursor() != null)
      {
        getRenderer().getRender().setCursor(Cursor.getPredefinedCursor(getRenderer().getComponent().getCursor()));
        resetWidth();
      }
    }
    else if (prop.equals(WAbstractComponent.V_POPUP_MENU))
    {
      WidgetComponentApiRendererUtilities.setComponentPopupMenu(getRenderer().getComponentContext(), getRenderer().getRender(), getRenderer().getEngine().getServerContextManager());
    }
    else if (prop.equals(WAbstractComponent.V_VISIBLE))
    {
      getEditableComponent().setVisible(getRenderer().getComponent().isVisible());
    }
    else if (prop.equals(WAbstractComponent.V_FOCUSABLE))
    {
      getEditableComponent().setFocusable(getRenderer().getComponent().isFocusable());
    }
    else
    {
      try
      {
        AbstractSwingRenderer.synchronizeProperty(prop, getRenderer().getComponent(), getRenderer().getRender());
      }
      catch (ContextException ex)
      {
        Log.GUIBUILDER.warn(ex.getMessage(), ex);
        return;
      }
    }
    
    // If this element is selected and some property was changed we need to recheck which buttons of GBToolBar are enabled
    if (getGuiBuilder().getWorkPane().getSelectedNodes().contains(getRenderer().getComponentContext()))
    {
      getGuiBuilder().getToolBar().buttonsEnabledForElements(getGuiBuilder().getWorkPane().getSelectedNodes());
    }
    
    renderChanged();
  }
  
  @Override
  public boolean isInteractive()
  {
    return false;
  }
  
  @Override
  public void stop()
  {
    super.stop();
    if (editableComponent != null)
    {
      editableComponent.stop();
    }
  }
  
  @Override
  public void renderChanged()
  {
    panelWithHelper.revalidate();
    editableComponent.refresh();
    getGuiBuilder().getWorkPane().repaint();
  }
}
