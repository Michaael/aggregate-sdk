package com.tibbo.aggregate.common.expression;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.binding.Binding;
import com.tibbo.aggregate.common.context.Context;
import com.tibbo.aggregate.common.context.ContextException;
import com.tibbo.aggregate.common.context.ContextUtils;
import com.tibbo.aggregate.common.context.DefaultRequestController;
import com.tibbo.aggregate.common.context.FunctionDefinition;
import com.tibbo.aggregate.common.context.VariableDefinition;
import com.tibbo.aggregate.common.datatable.DataRecord;
import com.tibbo.aggregate.common.datatable.DataTable;
import com.tibbo.aggregate.common.datatable.DataTableBindingProvider;
import com.tibbo.aggregate.common.datatable.DataTableConstruction;
import com.tibbo.aggregate.common.datatable.FieldFormat;
import com.tibbo.aggregate.common.datatable.TableFormat;
import com.tibbo.aggregate.common.resource.ResourceManager;
import com.tibbo.aggregate.common.util.SyntaxErrorException;

public class DefaultReferenceResolver extends AbstractReferenceResolver
{
  // Various properties
  public static final String ROW = "row";
  public static final String DESCRIPTION = "description";
  
  // Context properties
  public static final String NAME = "name";
  public static final String ICON = "icon";
  public static final String TYPE = "type";
  
  // Properties of variable definition
  public static final String READABLE = "readable";
  public static final String WRITABLE = "writable";
  
  // Properties of table
  public static final String RECORDS = "records";
  public static final String QUALITY = "quality";
  public static final String TIMESTAMP = "timestamp";
  
  // Properties of table field
  public static final String FORMAT = "format";
  public static final String HELP = "help";
  public static final String OPTIONS = "options";
  public static final String SELECTION_VALUE_DESCRIPTION = "svdesc";
  
  public DefaultReferenceResolver()
  {
    super();
  }
  
  public DefaultReferenceResolver(DataTable defaultTable)
  {
    this();
    setDefaultTable(defaultTable);
  }
  
  @Override
  public Object resolveReference(Reference ref, EvaluationEnvironment environment) throws SyntaxErrorException, EvaluationException, ContextException
  {
    Evaluator.EvaluationStatistics.onReferenceProcessed();
    if (ref.getProperty() != null && ROW.equals(ref.getProperty()))
    {
      return getRow(ref, environment);
    }
    
    Context con = getContext(ref);
    
    DataTable table = getDefaultTable();
    
    String field = ref.getField();
    
    if (ref.getEntity() != null)
    {
      if (con != null)
      {
        if (field == null)
        {
          if (DESCRIPTION.equals(ref.getProperty()))
          {
            return resolveEntityDescription(ref, con);
          }
          if (ref.getEntityType() == ContextUtils.ENTITY_VARIABLE)
          {
            VariableDefinition vd = con.getVariableDefinition(ref.getEntity());
            
            if (vd == null)
            {
              throw new IllegalStateException(MessageFormat.format(Cres.get().getString("conVarNotAvailExt"), ref.getEntity(), con.getPath()));
            }
            
            if (READABLE.equals(ref.getProperty()))
            {
              return vd.isReadable();
            }
            
            if (WRITABLE.equals(ref.getProperty()))
            {
              return vd.isWritable();
            }
            
            if (ICON.equals(ref.getProperty()))
            {
              return ResourceManager.getImageIcon(vd.getIconId());
            }
          }
        }
        else
        {
          TableFormat rf;
          
          if (ref.getEntityType() == ContextUtils.ENTITY_VARIABLE)
          {
            VariableDefinition vd = con.getVariableDefinition(ref.getEntity());
            
            if (vd == null)
            {
              throw new IllegalStateException(MessageFormat.format(Cres.get().getString("conVarNotAvailExt"), ref.getEntity(), con.getPath()));
            }
            
            rf = vd.getFormat();
          }
          else if (ref.getEntityType() == ContextUtils.ENTITY_FUNCTION)
          {
            FunctionDefinition fd = con.getFunctionDefinition(ref.getEntity());
            
            if (fd == null)
            {
              throw new IllegalStateException(MessageFormat.format(Cres.get().getString("conFuncNotAvailExt"), ref.getEntity(), con.getPath()));
            }
            
            rf = fd.getOutputFormat();
          }
          else
          {
            throw new IllegalStateException("Illegal entity type: " + ref.getEntityType());
          }
          
          FieldFormat ff = null;
          
          if (rf != null)
          {
            ff = rf.getField(ref.getField());
          }
          
          if (ff != null)
          {
            if (FORMAT.equals(ref.getProperty()))
            {
              return ff;
            }
            
            if (DESCRIPTION.equals(ref.getProperty()))
            {
              return ff.getDescription();
            }
            
            if (HELP.equals(ref.getProperty()))
            {
              return ff.getHelp();
            }
            
          }
        }
        
        table = resolveEntity(ref, con, environment);
      }
      else
      {
        throw new IllegalStateException(MessageFormat.format(Cres.get().getString("exprDefConNotDefined"), ref.getEntity()));
      }
    }
    else
    {
      if (con != null)
      {
        if (ref.getProperty() != null && ref.getContext() != null)
        {
          if (NAME.equals(ref.getProperty()))
          {
            return con.getName();
          }
          
          if (DESCRIPTION.equals(ref.getProperty()))
          {
            return con.getDescription();
          }
          
          if (ICON.equals(ref.getProperty()))
          {
            return ResourceManager.getImageIcon(con.getIconId());
          }
          
          if (TYPE.equals(ref.getProperty()))
          {
            return con.getType();
          }
        }
        
        if (ref.getContext() != null)
        {
          return con.getPath();
        }
      }
    }
    
    if (table == null)
    {
      throw new IllegalStateException(MessageFormat.format(Cres.get().getString("exprDefDataTableNotDefined"), field));
    }
    
    if (field == null && ref.getProperty() != null)
    {
      if (RECORDS.equals(ref.getProperty()))
      {
        return table.getRecordCount();
      }
      
      if (QUALITY.equals(ref.getProperty()))
      {
        return table.getQuality();
      }
      
      if (TIMESTAMP.equals(ref.getProperty()))
      {
        return table.getTimestamp();
      }
    }
    
    if (field == null)
    {
      return getDefaultTableAggregate(table);
    }
    
    FieldFormat ff = table.getFormat().getField(ref.getField());
    
    if (ref.getProperty() != null)
    {
      if (FORMAT.equals(ref.getProperty()))
      {
        return ff;
      }
      
      if (DESCRIPTION.equals(ref.getProperty()))
      {
        return ff.getDescription();
      }
      
      if (HELP.equals(ref.getProperty()))
      {
        return ff.getHelp();
      }
      
      if (OPTIONS.equals(ref.getProperty()))
      {
        return ff.getSelectionValues();
      }
    }
    
    Integer row = getRow(ref, environment);
    
    if (table.getRecordCount() != null && table.getRecordCount() <= row)
    {
      throw new IllegalStateException(MessageFormat.format(Cres.get().getString("exprNonExistentRow"), row, table.getRecordCount()) + ": " + table.dataAsString());
    }
    
    DataRecord record = table.getRecord(row);
    
    Object value = record.getValue(field);
    
    if (ref.getProperty() != null)
    {
      if (SELECTION_VALUE_DESCRIPTION.equals(ref.getProperty()))
      {
        List<Binding> bindings = table.getFormat().getBindings();
        for (int i = bindings.size() - 1; i >= 0; i--)
        {
          Binding curBinding = bindings.get(i);
          if (curBinding.getTarget() != null && ref.getField() != null && ref.getField().equals(curBinding.getTarget().getField())
              && DataTableBindingProvider.PROPERTY_CHOICES.equals(curBinding.getTarget().getProperty()))
          {
            DataTable curBindingExpressionResult = (DataTable) getEvaluator().evaluate(curBinding.getExpression(), environment);
            
            for (DataRecord rec : curBindingExpressionResult)
            {
              if (Objects.equals(value, ff.valueFromString(rec.getValueAsString(0))))
              {
                final Object valueDesc = rec.getValue(1);
                return valueDesc == null ? value : valueDesc;
              }
            }
            
            return value;
          }
        }
        
        if (ff.hasSelectionValues())
        {
          final Object valueDesc = ff.getSelectionValues().get(value);
          return valueDesc == null ? value : valueDesc;
        }
      }
      
      boolean notNullDataTable = (value instanceof DataTable);
      
      if (notNullDataTable)
      {
        DataTable tbl = (DataTable) value;
        
        if (RECORDS.equals(ref.getProperty()))
        {
          return tbl.getRecordCount();
        }
        if (QUALITY.equals(ref.getProperty()))
        {
          return tbl.getQuality();
        }
        if (TIMESTAMP.equals(ref.getProperty()))
        {
          return tbl.getTimestamp();
        }
      }
    }
    
    return new DefaultAttributedObject(value, table.getTimestamp(), table.getQuality());
  }
  
  protected Object getDefaultTableAggregate(DataTable table)
  {
    return table;
  }
  
  protected Integer getRow(Reference ref, EvaluationEnvironment environment)
  {
    if (ref.getRow() != null)
    {
      return ref.getRow();
    }
    else
    {
      if (environment != null && environment.getCause() != null && environment.getCause().getRow() != null)
      {
        return environment.getCause().getRow();
      }
      else
      {
        return getDefaultRow() != null ? getDefaultRow() : 0;
      }
    }
  }
  
  private String resolveEntityDescription(Reference ref, Context con) throws IllegalStateException
  {
    if (ref.getEntityType() == ContextUtils.ENTITY_VARIABLE)
    {
      VariableDefinition vd = con.getVariableDefinition(ref.getEntity());
      
      if (vd == null)
      {
        throw new IllegalStateException(MessageFormat.format(Cres.get().getString("conVarNotAvailExt"), ref.getEntity(), con.getPath()));
      }
      
      return vd.getDescription();
    }
    else if (ref.getEntityType() == ContextUtils.ENTITY_FUNCTION)
    {
      FunctionDefinition fd = con.getFunctionDefinition(ref.getEntity());
      
      if (fd == null)
      {
        throw new IllegalStateException(MessageFormat.format(Cres.get().getString("conFuncNotAvailExt"), ref.getEntity(), con.getPath()));
      }
      
      return fd.getDescription();
    }
    else
    {
      throw new IllegalStateException("Illegal entity type: " + ref.getEntityType());
    }
  }
  
  public List<Context> getContexts(Reference ref) throws ContextException
  {
    if (ref.getContext() != null && ContextUtils.isMask(ref.getContext()))
    {
      return ContextUtils.expandMaskToContexts(ref.getContext(), getContextManager(), getCallerController());
    }
    else
    {
      Context con = getContext(ref);
      return con != null ? Collections.singletonList(con) : Collections.emptyList();
    }
  }
  
  public Context getContext(Reference ref) throws ContextException
  {
    Context con = getDefaultContext();
    
    if (ref.getContext() != null)
    {
      if (con != null)
      {
        con = con.get(ref.getContext(), getCallerController());
      }
      else if (getContextManager() != null)
      {
        con = getContextManager().get(ref.getContext(), getCallerController());
      }
      else
      {
        //noinspection DataFlowIssue    // the assignment is reduntant but left for clarity
        con = null;
      }
      
      if (con == null)
      {
        throw new ContextException(Cres.get().getString("conNotAvail") + ref.getContext());
      }
    }
    return con;
  }
  
  protected DataTable resolveEntity(Reference ref, Context con, EvaluationEnvironment environment) throws ContextException, SyntaxErrorException, EvaluationException
  {
    if (ref.getEntityType() == ContextUtils.ENTITY_VARIABLE)
    {
      VariableDefinition vd = con.getVariableDefinition(ref.getEntity());
      
      if (vd == null)
      {
        throw new IllegalStateException(MessageFormat.format(Cres.get().getString("conVarNotAvailExt"), ref.getEntity(), con.getPath()));
      }
      
      DefaultRequestController requestController = new DefaultRequestController(getEvaluator());
      requestController.assignPinpoint(environment.obtainPinpoint());

      return con.getVariable(ref.getEntity(), getCallerController(), requestController);
    }
    else if (ref.getEntityType() == ContextUtils.ENTITY_FUNCTION)
    {
      FunctionDefinition fd = con.getFunctionDefinition(ref.getEntity());
      
      if (fd == null)
      {
        throw new IllegalStateException(MessageFormat.format(Cres.get().getString("conFuncNotAvailExt"), ref.getEntity(), con.getPath()));
      }
      
      DataTable parameters = DataTableConstruction.constructTable(ref.getParameters(), fd.getInputFormat(), getEvaluator(), environment);
      
      DefaultRequestController requestController = new DefaultRequestController(getEvaluator());
      requestController.assignPinpoint(environment.obtainPinpoint());

      return con.callFunction(ref.getEntity(), getCallerController(), requestController, parameters);
    }
    else
    {
      throw new IllegalStateException("Illegal entity type: " + ref.getEntityType());
    }
  }
  
  @Override
  public String toString()
  {
    StringBuilder sb = new StringBuilder(getClass().getSimpleName()).append("(");
    if (getDefaultContext() != null)
    {
      String contextPath = getDefaultContext().getPath().equals("")
              ? "(root)"
              : getDefaultContext().getPath();
      sb.append(" defaultContext='").append(contextPath).append('\'');
    }
    if (getDefaultTable() != null)
    {
      sb.append(" defaultTable='").append(getDefaultTable()).append('\'');
    }
    if (getDefaultRow() != null)
    {
      sb.append(" defaultRow=").append(getDefaultRow());
    }
    sb.append(" )");
    return sb.toString();
  }
}
