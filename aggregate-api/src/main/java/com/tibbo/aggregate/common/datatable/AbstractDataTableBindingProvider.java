package com.tibbo.aggregate.common.datatable;

import com.tibbo.aggregate.common.*;
import com.tibbo.aggregate.common.binding.*;
import com.tibbo.aggregate.common.expression.*;
import com.tibbo.aggregate.common.util.*;

import java.util.*;
import java.util.concurrent.locks.*;

public abstract class AbstractDataTableBindingProvider extends AbstractBindingProvider
{
  protected final Map<ReferenceListener, Reference> listeners = new LinkedHashMap();
  
  protected final ReentrantReadWriteLock listenersLock = new ReentrantReadWriteLock();
  
  public AbstractDataTableBindingProvider()
  {
    super();
  }
  
  public AbstractDataTableBindingProvider(ErrorCollector errorCollector)
  {
    super(errorCollector);
  }
  
  public void writeReference(Reference ref, final Object value) throws BindingException
  {
    if (isLocalReference(ref) && ref.getServer() == null)
    {
      int row = ref.getRow() != null ? ref.getRow() : 0;
      String field = ref.getField();
      String property = ref.getProperty();
      
      if (field == null || field.length() == 0)
      {
        writeToEditor(property, value);
      }
      else
      {
        writeToField(row, field, property, value);
      }
    }
    else
    {
      ReferenceWriter externalReferenceWriter = getExternalReferenceWriter();
      if (externalReferenceWriter != null)
      {
        externalReferenceWriter.writeReference(ref, value);
      }
      else
      {
        Log.BINDINGS.debug("Unable to write value referenced by '" + ref + "': no external reference writer is defined");
      }
    }
  }
  
  private void writeToEditor(String property, Object value)
  {
    if (DataTableBindingProvider.PROPERTY_ENABLED.equals(property))
    {
      setEditorEnabled(Util.convertToBoolean(value, true, false));
    }
  }
  
  private void writeToField(int row, String field, String property, Object value) throws BindingException
  {
    if (DataTableBindingProvider.PROPERTY_ENABLED.equals(property))
    {
      setEnabled(value, row, field);
    }
    else if (DataTableBindingProvider.PROPERTY_HIDDEN.equals(property))
    {
      setHidden(value, row, field);
    }
    else if (DataTableBindingProvider.PROPERTY_CHOICES.equals(property))
    {
      setSelectionValues(value, row, field);
    }
    else if (DataTableBindingProvider.PROPERTY_OPTIONS.equals(property))
    {
      setOptions(value, row, field);
    }
    else if (property == null)
    {
      setCellValue(value, row, field);
    }
    else
    {
      throw new IllegalStateException("Unknown property: '" + property + "'");
    }
  }
  
  public int getListenerCount()
  {
    return listeners.size();
  }
  
  public Map<ReferenceListener, Reference> getListeners()
  {
    return listeners;
  }
  
  public ReentrantReadWriteLock getListenersLock()
  {
    return listenersLock;
  }
  
  public void addReferenceListener(Reference ref, ReferenceListener listener) throws BindingException
  {
    listenersLock.writeLock().lock();
    try
    {
      listeners.put(listener, ref);
    }
    finally
    {
      listenersLock.writeLock().unlock();
    }
  }
  
  public void removeReferenceListener(ReferenceListener listener)
  {
    listenersLock.writeLock().lock();
    try
    {
      listeners.remove(listener);
    }
    finally
    {
      listenersLock.writeLock().unlock();
    }
  }
  
  public boolean isLocalReference(Reference ref)
  {
    return ref.getSchema() == null && ref.getContext() == null && ref.getEntity() == null;
  }
  
  protected void processBindings(String field, int record, boolean startup, boolean asynchronousProcessing)
  {
    
    listenersLock.readLock().lock();
    try
    {
      for (Map.Entry<ReferenceListener, Reference> entry : listeners.entrySet())
      {
        final Reference ref = entry.getValue();
        final ReferenceListener listener = entry.getKey();
        
        if (startup && Util.equals(listener.getBinding().getTarget().getSchema(), Reference.SCHEMA_TABLE))
        {
          continue; // Bindings that change whole table should not be processed on startup
        }
        
        String rfield = ref.getField();
        
        boolean nonLocal = !isLocalReference(ref);
        
        String listenerField = listener.getBinding().getTarget().getField();
        boolean targetPointsToCurrentField = listenerField != null && listenerField.equals(field);
        
        if ((startup && nonLocal && targetPointsToCurrentField) || (rfield != null && isLocalReference(ref) && rfield.equals(field)))
        {
          if (ref.getRow() != null && ref.getRow() == record)
          {
            callReferenceChanged(ref, startup ? EvaluationOptions.STARTUP : EvaluationOptions.EVENT, listener, asynchronousProcessing);
          }
          
          if (ref.getRow() == null)
          {
            Reference clone = ref.clone();
            clone.setRow(ref.getSchema() == null ? record : null); // Substituting row to current one if reference uses default schema
            callReferenceChanged(clone, startup ? EvaluationOptions.STARTUP : EvaluationOptions.EVENT, listener, asynchronousProcessing);
          }
        }
      }
    }
    finally
    {
      listenersLock.readLock().unlock();
    }
  }
  
  protected abstract ReferenceWriter getExternalReferenceWriter();
  
  protected abstract void callReferenceChanged(final Reference cause, int method, final ReferenceListener listener, boolean asynchronousProcessing);
  
  protected abstract void setEnabled(Object value, int row, String field) throws BindingException;
  
  protected abstract void setCellValue(Object value, int row, String field) throws BindingException;
  
  protected abstract void setOptions(Object value, int row, String field) throws BindingException;
  
  protected abstract void setSelectionValues(Object value, int row, String field) throws BindingException;
  
  protected abstract void setHidden(Object value, final int row, final String field) throws BindingException;
  
  protected abstract void setEditorEnabled(boolean enabled);
}
