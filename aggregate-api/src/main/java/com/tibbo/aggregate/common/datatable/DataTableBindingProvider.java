package com.tibbo.aggregate.common.datatable;

import java.util.LinkedHashMap;
import java.util.Map;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.Log;
import com.tibbo.aggregate.common.binding.Binding;
import com.tibbo.aggregate.common.binding.BindingException;
import com.tibbo.aggregate.common.binding.ChangeCache;
import com.tibbo.aggregate.common.binding.EvaluationOptions;
import com.tibbo.aggregate.common.binding.ReferenceListener;
import com.tibbo.aggregate.common.binding.ReferenceWriter;
import com.tibbo.aggregate.common.expression.Reference;
import com.tibbo.aggregate.common.structure.Pinpoint;
import com.tibbo.aggregate.common.util.ErrorCollector;

public class DataTableBindingProvider extends AbstractDataTableBindingProvider
{
  public static final String PROPERTY_ENABLED = "enabled";
  public static final String PROPERTY_HIDDEN = "hidden";
  public static final String PROPERTY_CHOICES = "choices";
  public static final String PROPERTY_OPTIONS = "options";
  
  private static final EvaluationOptions EVALUATION_OPTIONS = new EvaluationOptions(true, false, 0);
  
  private final DataTable table;
  
  private boolean headless;
  
  public DataTableBindingProvider(DataTable table)
  {
    this.table = table;
  }
  
  public DataTableBindingProvider(DataTable table, ErrorCollector errorCollector)
  {
    super(errorCollector);
    this.headless = true;
    this.table = table;
  }
  
  @Override
  public Map<Binding, EvaluationOptions> createBindings()
  {
    Map<Binding, EvaluationOptions> bm = new LinkedHashMap();
    
    for (Binding b : table.getFormat().getBindings())
    {
      bm.put(b, EVALUATION_OPTIONS);
    }
    
    return bm;
  }
  
  @Override
  protected void callReferenceChanged(Reference cause, int method, ReferenceListener listener, boolean asynchronousProcessing)
  {
    try
    {
      listener.referenceChanged(cause, null, null, asynchronousProcessing);
    }
    catch (BindingException ex)
    {
      processError(listener.getBinding(), method, cause, ex);
    }
  }
  
  @Override
  protected ReferenceWriter getExternalReferenceWriter()
  {
    return null;
  }
  
  @Override
  protected void setCellValue(Object value, int row, String field)
  {
    table.getRecord(row).setValue(field, value);
  }
  
  @Override
  protected void setEnabled(Object value, int row, String field)
  {
  }
  
  @Override
  protected void setHidden(Object value, int row, String field)
  {
    if (headless)
    {
      return; // Format will be immutable in headless mode
    }
    
    final boolean hidden = (Boolean) value;
    
    try
    {
      FieldFormat ff = getFieldFormat(row, field);
      if (ff != null)
      {
        ff.setHidden(hidden);
      }
    }
    catch (BindingException ex)
    {
      Log.BINDINGS.error("Error hidden status setting field " + field + " in row " + row + " to " + hidden, ex);
    }
  }
  
  @Override
  protected void setOptions(Object value, int row, String field)
  {
  }
  
  @Override
  protected void setSelectionValues(Object value, int row, String field)
  {
  }
  
  @Override
  protected void setEditorEnabled(boolean enabled)
  {
  }
  
  @Override
  public void start()
  {
    for (int i = 0; i < table.getRecordCount(); i++)
    {
      for (FieldFormat ff : table.getFormat())
      {
        processBindings(ff.getName(), i, true, true);
      }
    }
  }
  
  @Override
  public void stop()
  {
  }
  
  @Override
  public void writeReference(int method, Binding binding, Reference cause, Object value, ChangeCache cache,
      Pinpoint pinpoint) throws BindingException
  {
    Reference ref = binding.getTarget();
    Integer row = ref.getRow() != null ? ref.getRow() : (cause != null && cause.getRow() != null) ? cause.getRow() : null;
    Reference clone = ref.clone();
    clone.setRow(row);
    writeReference(clone, value);
  }
  
  protected FieldFormat getFieldFormat(int row, String field) throws BindingException
  {
    if (row >= table.getRecordCount())
    {
      throw new BindingException(Cres.get().getString("dtRecordNotAvail") + row);
    }
    
    FieldFormat ff = table.getRecord(row).getFormat().getField(field);
    
    if (ff == null)
    {
      throw new BindingException(Cres.get().getString("dtFieldNotAvail") + field);
    }
    
    return ff;
  }
}
