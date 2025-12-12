package com.tibbo.aggregate.common.binding;

import static java.lang.String.format;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.Log;
import com.tibbo.aggregate.common.datatable.DataRecord;
import com.tibbo.aggregate.common.datatable.DataTable;
import com.tibbo.aggregate.common.datatable.DataTableBindingProvider;
import com.tibbo.aggregate.common.datatable.FieldFormat;
import com.tibbo.aggregate.common.datatable.SimpleDataTable;
import com.tibbo.aggregate.common.datatable.TableFormat;
import com.tibbo.aggregate.common.datatable.field.LongFieldFormat;
import com.tibbo.aggregate.common.datatable.field.StringFieldFormat;
import com.tibbo.aggregate.common.datatable.validator.LimitsValidator;
import com.tibbo.aggregate.common.datatable.validator.RegexValidator;
import com.tibbo.aggregate.common.expression.Expression;
import com.tibbo.aggregate.common.expression.ExpressionUtils;
import com.tibbo.aggregate.common.expression.Reference;
import com.tibbo.aggregate.common.expression.function.DefaultFunctions;
import com.tibbo.aggregate.common.structure.Pinpoint;
import com.tibbo.aggregate.common.util.StringUtils;
import com.tibbo.aggregate.common.util.SyntaxErrorException;
import com.tibbo.aggregate.common.util.TimeHelper;

public class Bindings
{
  public static final long DEFAULT_EVALUATION_PERIOD = TimeHelper.MINUTE_IN_MS;
  
  public static final String FIELD_BINDING_ID = "bindingId";
  public static final String FIELD_TARGET = "target";
  public static final String FIELD_EXPRESSION = "expression";
  public static final String FIELD_ACTIVATOR = "activator";
  public static final String FIELD_CONDITION = "condition";
  public static final String FIELD_ONSTARTUP = "onstartup";
  public static final String FIELD_ONEVENT = "onevent";
  public static final String FIELD_PERIODICALLY = "periodically";
  public static final String FIELD_PERIOD = "period";
  public static final String FIELD_QUEUE = "queue";
  
  public static final TableFormat FORMAT = new TableFormat();
  static
  {
    FORMAT.setReorderable(false);
    
    FieldFormat bidF = FieldFormat.create(FIELD_BINDING_ID, FieldFormat.LONG_FIELD);
    bidF.setNullable(true);
    bidF.addValidator(new LimitsValidator(0L, Long.MAX_VALUE));
    bidF.setHidden(true);
    bidF.setNotReplicated(true);
    bidF.setKeyField(true);
    FORMAT.addField(bidF);
    
    FieldFormat targF = FieldFormat.create(FIELD_TARGET, FieldFormat.STRING_FIELD);
    targF.setDescription(Cres.get().getString("target"));
    targF.setHelp(Cres.get().getString("wHelpBindingTarget"));
    targF.setEditor(StringFieldFormat.EDITOR_TARGET);
    FORMAT.addField(targF);
    
    FieldFormat exprF = FieldFormat.create(FIELD_EXPRESSION, FieldFormat.STRING_FIELD);
    exprF.setDescription(Cres.get().getString("expression"));
    exprF.setHelp(Cres.get().getString("wHelpBindingExpression"));
    exprF.setEditor(StringFieldFormat.EDITOR_EXPRESSION);
    FORMAT.addField(exprF);
    
    FieldFormat actF = FieldFormat.create(FIELD_ACTIVATOR, FieldFormat.STRING_FIELD);
    actF.setDescription(Cres.get().getString("wActivatorDescr"));
    actF.setHelp(Cres.get().getString("wHelpBindingActivator"));
    actF.setEditor(StringFieldFormat.EDITOR_ACTIVATOR);
    FORMAT.addField(actF);
    
    FieldFormat condition = FieldFormat.create(FIELD_CONDITION, FieldFormat.STRING_FIELD);
    condition.setDescription(Cres.get().getString("condition"));
    condition.setHelp(Cres.get().getString("wHelpBindingCondition"));
    condition.setEditor(StringFieldFormat.EDITOR_EXPRESSION);
    FORMAT.addField(condition);
    
    FieldFormat<Boolean> osuF = FieldFormat.create(FIELD_ONSTARTUP, FieldFormat.BOOLEAN_FIELD);
    osuF.setDescription(Cres.get().getString("wOnStartup"));
    osuF.setHelp(Cres.get().getString("wHelpBindingOnStartup"));
    FORMAT.addField(osuF);
    
    FieldFormat<Boolean> oeF = FieldFormat.create(FIELD_ONEVENT, FieldFormat.BOOLEAN_FIELD);
    oeF.setDescription(Cres.get().getString("wOnEvent"));
    oeF.setHelp(Cres.get().getString("wHelpBindingOnEvent"));
    FORMAT.addField(oeF);
    
    FieldFormat<Boolean> perF = FieldFormat.create(FIELD_PERIODICALLY, FieldFormat.BOOLEAN_FIELD);
    perF.setDescription(Cres.get().getString("wPeriodically"));
    perF.setHelp(Cres.get().getString("wHelpBindingPeriodically"));
    perF.setDefault(false);
    FORMAT.addField(perF);
    
    FieldFormat<Long> epF = FieldFormat.create(FIELD_PERIOD, FieldFormat.LONG_FIELD);
    epF.setDefault(DEFAULT_EVALUATION_PERIOD);
    epF.setDescription(Cres.get().getString("wEvaluationPeriod"));
    epF.setHelp(Cres.get().getString("wHelpBindingPeriod"));
    epF.setEditor(LongFieldFormat.EDITOR_PERIOD);
    epF.addValidator(new LimitsValidator(1L, Long.MAX_VALUE));
    FORMAT.addField(epF);
    
    FieldFormat quF = FieldFormat.create(FIELD_QUEUE, FieldFormat.STRING_FIELD);
    quF.setAdvanced(true);
    quF.setDescription(Cres.get().getString("queue"));
    quF.setHelp(Cres.get().getString("wHelpBindingQueue"));
    quF.addValidator(new RegexValidator("\\w*", Cres.get().getString("dtInvalidName")));
    FORMAT.addField(quF);
    
    String ref = FIELD_EXPRESSION + "#" + DataTableBindingProvider.PROPERTY_OPTIONS;
    String exp = DefaultFunctions.EXPRESSION_EDITOR_OPTIONS + "({.:})";
    FORMAT.addBinding(ref, exp);
    
    ref = Bindings.FIELD_ACTIVATOR + "#" + DataTableBindingProvider.PROPERTY_ENABLED;
    exp = "{" + Bindings.FIELD_ONEVENT + "}";
    FORMAT.addBinding(ref, exp);
    
    String ref1 = Bindings.FIELD_PERIOD + "#" + DataTableBindingProvider.PROPERTY_ENABLED;
    String exp1 = "{" + Bindings.FIELD_PERIODICALLY + "}";
    FORMAT.addBinding(ref1, exp1);
    
    String exp2 = "{" + FIELD_BINDING_ID + "}==" + ExpressionUtils.NULL_PARAM + " ? " + DefaultFunctions.LONG + "(" + DefaultFunctions.RANDOM + "()*" + Long.MAX_VALUE + ") : {" + FIELD_BINDING_ID + "}";
    FORMAT.addBinding(FIELD_BINDING_ID, exp2);
  }

  public static List<ExtendedBinding> bindingsFromDataTable(DataTable bindingsTable) throws SyntaxErrorException
  {
    return bindingsFromDataTable(bindingsTable, null);
  }

  /**
   * Converts given bindings table into a list of extended binding instances assuming that the table has
   * {@linkplain Bindings#FORMAT appropriate format}.
   * @param bindingsTable source table to read bindings data from
   * @param pinpoint an optional pointer to the place where the bindings belong to (e.g. a context's variable)
   * @return a list of instantiated bindings and their options
   * @throws SyntaxErrorException if some of the bindings contain a malformed expression
   */
  public static List<ExtendedBinding> bindingsFromDataTable(DataTable bindingsTable, @Nullable Pinpoint pinpoint)
      throws SyntaxErrorException
  {
    Map<Long, ExtendedBinding> bindingsById = new LinkedHashMap<>();    // <binding_id, binding>
    int bindingIndex = 0;
    for (DataRecord bindingRecord : bindingsTable)
    {
      String ri = bindingRecord.getString(FIELD_TARGET);
      if (ri == null)
      {
        ri = "";
      }
      Reference bRef = new Reference(ri);
      
      Expression bExp = new Expression(bindingRecord.getString(FIELD_EXPRESSION));
      
      ExpressionUtils.validateSyntax(bExp, true);
      
      Reference activator = null;
      if (bindingRecord.hasField(FIELD_ACTIVATOR))
      {
        String act = bindingRecord.getString(FIELD_ACTIVATOR);
        if (act != null && !act.isEmpty())
        {
          activator = new Reference(act);
        }
      }

      Expression condition = null;
      if (bindingRecord.hasField(FIELD_CONDITION))
      {
        String cond = bindingRecord.getString(FIELD_CONDITION);
        if (cond != null && !cond.isEmpty())
        {
          condition = new Expression(cond);
        }
      }
      
      int pattern = 0;
            if (bindingRecord.hasField(FIELD_ONSTARTUP) && bindingRecord.getBoolean(FIELD_ONSTARTUP))
      {
        pattern = pattern | EvaluationOptions.STARTUP;
      }
            if (bindingRecord.hasField(FIELD_ONEVENT) && bindingRecord.getBoolean(FIELD_ONEVENT))
      {
        pattern = pattern | EvaluationOptions.EVENT;
      }
            if (bindingRecord.hasField(FIELD_PERIODICALLY) && bindingRecord.getBoolean(FIELD_PERIODICALLY))
      {
        pattern = pattern | EvaluationOptions.PERIODIC;
      }
      
      EvaluationOptions evalOpts = new EvaluationOptions(pattern);
      evalOpts.setActivator(activator);
      evalOpts.setCondition(condition);
      if (pinpoint != null)
      {
        evalOpts.assignPinpoint(pinpoint.withOriginRow(bindingIndex++));
      }

      long period = 0;
      if (bindingRecord.hasField(FIELD_PERIOD))
      {
        if (evalOpts.isProcessPeriodically())
        {
          period = bindingRecord.getLong(FIELD_PERIOD);
        }
        
        if (period > 0)
        {
          evalOpts.setPeriod(period);
        }
      }
      
      Binding bg = new Binding(bRef, bExp);
      
      Long id;
      if (bindingRecord.hasField(FIELD_BINDING_ID))
      {
        id = bindingRecord.getLong(FIELD_BINDING_ID);
      }
      else
      {
        id = Double.valueOf(Math.random() * Long.MAX_VALUE).longValue();
      }
      bg.setId(id);
      
      if (bindingRecord.hasField(FIELD_QUEUE))
      {
        String queue = bindingRecord.getString(FIELD_QUEUE);
        if (!StringUtils.isEmpty(queue))
        {
          bg.setQueue(queue);
        }
      }

      // get rid of duplicates
      ExtendedBinding previousBinding = bindingsById.get(bg.getId());
      if (previousBinding != null)
      {
        long newId = ExpressionUtils.generateBindingId();
        Log.BINDINGS.debug(format("Duplicate binding detected for '%s'. New binding '%s' will have bindingId=%d",
                previousBinding, bg, newId));
        bg.setId(newId);
      }

      // finally enter the binding into the map
      bindingsById.put(bg.getId(), new ExtendedBinding(bg, evalOpts));
    }

    return new ArrayList<>(bindingsById.values());
  }
  
  public static DataTable bindingsToDataTable(Collection<ExtendedBinding> bindings)
  {
    return bindingsToDataTable(bindings, FORMAT);
  }
  
  public static DataTable bindingsToDataTable(Collection<ExtendedBinding> bindings, TableFormat format)
  {
    DataTable dt = new SimpleDataTable(format);
    for (ExtendedBinding bg : bindings)
    {
      dt.addRecord(bindingToDataRecord(bg, format));
    }
    
    return dt;
  }
  
  public static DataRecord bindingToDataRecord(ExtendedBinding ebg, TableFormat format)
  {
    Binding bg = ebg.getBinding();
    
    DataRecord dr = new DataRecord(format);
    if (bg.getTarget() != null)
    {
      dr.setValue(FIELD_TARGET, bg.getTarget().getImage());
    }
    dr.setValue(FIELD_BINDING_ID, bg.getId());
    dr.setId(String.valueOf(bg.getId()));
    String text = "";
    if (bg.getExpression() != null)
    {
      text = bg.getExpression().getText();
    }
    dr.setValue(FIELD_EXPRESSION, text);
    
    EvaluationOptions eOpts = ebg.getEvaluationOptions();
    dr.setValue(FIELD_ONSTARTUP, eOpts.isProcessOnStartup());
    dr.setValue(FIELD_ONEVENT, eOpts.isProcessOnEvent());
    dr.setValue(FIELD_PERIODICALLY, eOpts.isProcessPeriodically());
    
    if (eOpts.getActivator() != null)
    {
      dr.setValue(FIELD_ACTIVATOR, eOpts.getActivator().getImage());
    }
    
    if (eOpts.getCondition() != null)
    {
      dr.setValue(FIELD_CONDITION, eOpts.getCondition().getText());
    }
    
    if (eOpts.isProcessPeriodically())
    {
      dr.setValue(FIELD_PERIOD, eOpts.getPeriod());
    }
    else
    {
      dr.setValue(FIELD_PERIOD, DEFAULT_EVALUATION_PERIOD);
    }
    if (format.hasField(FIELD_QUEUE))
    {
      dr.setValue(FIELD_QUEUE, bg.getQueue() != null ? bg.getQueue() : "");
    }
    
    return dr;
  }
}
