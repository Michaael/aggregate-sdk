package com.tibbo.aggregate.common.widget.runtime.renderers;

import java.awt.event.*;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;

import com.tibbo.aggregate.common.binding.*;
import com.tibbo.aggregate.common.context.*;
import com.tibbo.aggregate.common.expression.*;
import com.tibbo.aggregate.common.widget.*;
import com.tibbo.aggregate.common.widget.component.*;
import com.tibbo.aggregate.common.widget.context.*;
import com.tibbo.aggregate.common.widget.runtime.*;

/**
 * Description: Class that defines some common behavior and functionality for different WidgetComponentRenderer implementations for Content Elements in Swing style. For example it has
 * <code>component</code> field that will be used in each Element Renderer. Also it does some common manipulations with representation Component that will be returned in
 * <code>getElementRendererComponent</code> method.
 */
public abstract class DefaultSwingComponentRenderer<C extends WAbstractComponent, T extends WAbstractContext<WComponentContext, C>, O extends JComponent, S extends SwingRendererSupport>
    extends AbstractSwingRenderer<C, T, O, S>
{
  interface Registrar
  {
    void addListener();
    
    void removeListener();
  }
  
  private final CaretListener caretListener = new CaretListener()
  {
    @Override
    public void caretUpdate(CaretEvent e)
    {
      getComponentContext().fireCaretUpdate(e);
    }
  };
  
  private final FocusListener focusListener = new FocusListener()
  {
    @Override
    public void focusGained(FocusEvent e)
    {
      getComponentContext().fireFocusGained(e);
    }
    
    @Override
    public void focusLost(FocusEvent e)
    {
      getComponentContext().fireFocusLost(e);
    }
  };
  
  private final KeyListener keyListener = new KeyListener()
  {
    @Override
    public void keyTyped(KeyEvent e)
    {
      getComponentContext().fireKeyTyped(e);
    }
    
    @Override
    public void keyPressed(KeyEvent e)
    {
      getComponentContext().fireKeyPressed(e);
    }
    
    @Override
    public void keyReleased(KeyEvent e)
    {
      getComponentContext().fireKeyReleased(e);
    }
  };
  
  private final MouseWheelListener mouseWheelListener = new MouseWheelListener()
  {
    @Override
    public void mouseWheelMoved(MouseWheelEvent e)
    {
      getComponentContext().fireWheelMoved(e);
    }
  };
  private final MouseMotionListener mouseMotionListener = new MouseMotionListener()
  {
    @Override
    public void mouseDragged(MouseEvent e)
    {
      // do not need this event
    }
    
    @Override
    public void mouseMoved(MouseEvent e)
    {
      getComponentContext().fireMouseMoved(e);
    }
  };
  
  private final MouseListener mouseListener = new MouseListener()
  {
    @Override
    public void mouseClicked(MouseEvent e)
    {
      if (!getComponent().isEnabled())
        return;

      if (e.getClickCount() == 2)
      {
        getComponentContext().fireMouseDoubleClicked(e);
        return;
      }
      getComponentContext().fireMouseClicked(e);
    }
    
    @Override
    public void mousePressed(MouseEvent e)
    {
      if (!getComponent().isEnabled())
        return;
      
      getComponentContext().fireMousePressed(e);
    }
    
    @Override
    public void mouseReleased(MouseEvent e)
    {
      if (!getComponent().isEnabled())
        return;
      
      getComponentContext().fireMouseReleased(e);
    }
    
    @Override
    public void mouseEntered(MouseEvent e)
    {
      getComponentContext().fireMouseEntered(e);
    }
    
    @Override
    public void mouseExited(MouseEvent e)
    {
      getComponentContext().fireMouseExited(e);
    }
  };
  
  private final ComponentListener componentListener = new ComponentListener()
  {
    @Override
    public void componentResized(ComponentEvent e)
    {
      getComponentContext().fireResized(e);
    }
    
    @Override
    public void componentMoved(ComponentEvent e)
    {
      getComponentContext().fireMoved(e);
    }
    
    @Override
    public void componentShown(ComponentEvent e)
    {
      getComponentContext().fireShown(e);
    }
    
    @Override
    public void componentHidden(ComponentEvent e)
    {
      getComponentContext().fireHidden(e);
    }
  };
  
  private final HashMap<String, Registrar> eventToRegistrar = new HashMap<String, Registrar>();
  
  public DefaultSwingComponentRenderer(T componentContext, SwingWidgetEngine engine)
  {
    super(componentContext, engine);
    initRegistrars();
  }
  
  @Override
  public void start()
  {
    super.start();
    addSwingEventListeners();
  }
  
  @Override
  public void stop()
  {
    super.stop();
    removeSwingEventListeners();
  }
  
  @Override
  public void componentPropertyChanged(String property)
  {
    if (WidgetTemplate.V_BINDINGS.equals(property))
    {
      addSwingEventListeners();
    }
    
    getRendererSupport().componentPropertyChanged(property);
  }
  
  @Override
  public JComponent getProduction()
  {
    return getRendererSupport().getProduction();
  }
  
  private void addSwingEventListeners()
  {
    if (getRender() != null)
    {
      Set<Registrar> registrars = getRegistrars();
      
      for (Registrar registrar : registrars)
      {
        registrar.addListener();
      }
    }
  }
  
  private void removeSwingEventListeners()
  {
    if (getRender() != null)
    {
      Set<Registrar> registrars = getRegistrars();
      
      for (Registrar registrar : registrars)
      {
        registrar.removeListener();
      }
    }
  }
  
  public Set<Registrar> getRegistrars()
  {
    Set<Registrar> registrars = new HashSet<Registrar>();
    for (ExtendedBinding binding : getEngine().getWidget().getBindings())
    {
      final Reference activator = binding.getEvaluationOptions().getActivator();
      if (activator != null && getComponent().getName().equals(activator.getContext()) && activator.getEntityType() == ContextUtils.ENTITY_EVENT)
      {
        final Registrar e = eventToRegistrar.get(activator.getEntity());
        if (e != null)
        {
          registrars.add(e);
        }
      }
    }
    return registrars;
  }
  
  private void initRegistrars()
  {
    Registrar componentRegistrar = new Registrar()
    {
      @Override
      public void addListener()
      {
        removeListener();
        getRender().addComponentListener(componentListener);
      }
      
      @Override
      public void removeListener()
      {
        getRender().removeComponentListener(componentListener);
      }
    };
    
    Registrar mouseRegistrar = new Registrar()
    {
      @Override
      public void addListener()
      {
        removeListener();
        getRender().addMouseListener(mouseListener);
      }
      
      @Override
      public void removeListener()
      {
        getRender().removeMouseListener(mouseListener);
      }
    };
    
    Registrar mouseMotionRegistrar = new Registrar()
    {
      @Override
      public void addListener()
      {
        removeListener();
        getRender().addMouseMotionListener(mouseMotionListener);
      }
      
      @Override
      public void removeListener()
      {
        getRender().removeMouseMotionListener(mouseMotionListener);
      }
    };
    
    Registrar mouseWheelRegistrar = new Registrar()
    {
      @Override
      public void addListener()
      {
        removeListener();
        getRender().addMouseWheelListener(mouseWheelListener);
      }
      
      @Override
      public void removeListener()
      {
        getRender().removeMouseWheelListener(mouseWheelListener);
      }
    };
    
    Registrar keyRegistrar = new Registrar()
    {
      @Override
      public void addListener()
      {
        removeListener();
        getRender().addKeyListener(keyListener);
      }
      
      @Override
      public void removeListener()
      {
        getRender().removeKeyListener(keyListener);
      }
    };
    
    Registrar focusRegistrar = new Registrar()
    {
      @Override
      public void addListener()
      {
        removeListener();
        getRender().addFocusListener(focusListener);
      }
      
      @Override
      public void removeListener()
      {
        getRender().removeFocusListener(focusListener);
      }
    };
    
    Registrar caretRegistrar = new Registrar()
    {
      @Override
      public void addListener()
      {
        removeListener();
        ((JTextComponent) getRender()).addCaretListener(caretListener);
      }
      
      @Override
      public void removeListener()
      {
        ((JTextComponent) getRender()).removeCaretListener(caretListener);
      }
    };
    
    eventToRegistrar.put(WAbstractContext.E_HIDDEN, componentRegistrar);
    eventToRegistrar.put(WAbstractContext.E_SHOWN, componentRegistrar);
    eventToRegistrar.put(WAbstractContext.E_RESIZED, componentRegistrar);
    eventToRegistrar.put(WAbstractContext.E_MOVED, componentRegistrar);
    
    eventToRegistrar.put(WAbstractContext.E_MOUSE_CLICKED, mouseRegistrar);
    eventToRegistrar.put(WAbstractContext.E_MOUSE_DOUBLE_CLICKED, mouseRegistrar);
    eventToRegistrar.put(WAbstractContext.E_MOUSE_PRESSED, mouseRegistrar);
    eventToRegistrar.put(WAbstractContext.E_MOUSE_RELEASED, mouseRegistrar);
    eventToRegistrar.put(WAbstractContext.E_MOUSE_ENTERED, mouseRegistrar);
    eventToRegistrar.put(WAbstractContext.E_MOUSE_EXITED, mouseRegistrar);
    
    eventToRegistrar.put(WAbstractContext.E_MOUSE_MOVED, mouseMotionRegistrar);
    
    eventToRegistrar.put(WAbstractContext.E_MOUSE_WHEEL_MOVED, mouseWheelRegistrar);
    
    eventToRegistrar.put(WAbstractContext.E_KEY_TYPED, keyRegistrar);
    eventToRegistrar.put(WAbstractContext.E_KEY_PRESSED, keyRegistrar);
    eventToRegistrar.put(WAbstractContext.E_KEY_RELEASED, keyRegistrar);
    
    eventToRegistrar.put(WAbstractContext.E_FOCUS_GAINED, focusRegistrar);
    eventToRegistrar.put(WAbstractContext.E_FOCUS_LOST, focusRegistrar);
    
    eventToRegistrar.put(WAbstractContext.E_CARET_UPDATE, caretRegistrar);
  }
}
