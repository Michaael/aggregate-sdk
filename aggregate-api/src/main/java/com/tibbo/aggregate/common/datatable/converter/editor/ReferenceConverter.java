package com.tibbo.aggregate.common.datatable.converter.editor;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.Log;
import com.tibbo.aggregate.common.context.CallerController;
import com.tibbo.aggregate.common.context.Context;
import com.tibbo.aggregate.common.context.ContextManager;
import com.tibbo.aggregate.common.context.ContextUtils;
import com.tibbo.aggregate.common.context.Contexts;
import com.tibbo.aggregate.common.datatable.DataRecord;
import com.tibbo.aggregate.common.datatable.DataTable;
import com.tibbo.aggregate.common.datatable.DataTableBindingProvider;
import com.tibbo.aggregate.common.datatable.DataTableException;
import com.tibbo.aggregate.common.datatable.DataTableUtils;
import com.tibbo.aggregate.common.datatable.FieldFormat;
import com.tibbo.aggregate.common.datatable.SimpleDataTable;
import com.tibbo.aggregate.common.datatable.TableFormat;
import com.tibbo.aggregate.common.datatable.field.StringFieldFormat;
import com.tibbo.aggregate.common.expression.EvaluationException;
import com.tibbo.aggregate.common.expression.Evaluator;
import com.tibbo.aggregate.common.expression.Expression;
import com.tibbo.aggregate.common.expression.Reference;
import com.tibbo.aggregate.common.expression.function.DefaultFunctions;
import com.tibbo.aggregate.common.server.UtilitiesContextConstants;
import com.tibbo.aggregate.common.util.SyntaxErrorException;

public class ReferenceConverter extends AbstractEditorOptionsConverter
{
  public static final String FIELD_APPEARANCE = "appearance";
  public static final String FIELD_REFERENCE_TYPE = "referenceType";
  public static final String FIELD_CONTEXT_TYPE = "contextType";
  public static final String FIELD_CONTEXT = "context";
  public static final String FIELD_CONTEXT_EXPRESSION = "contextExpression";
  public static final String FIELD_ENTITY_TYPE = "entityType";
  public static final String FIELD_ENTITY = "entity";
  public static final String FIELD_ENTITY_EXPRESSION = "entityExpression";
  public static final String FIELD_ENTITY_PARAMETERS = "entityParameters";
  public static final String FIELD_ICON = "icon";
  
  public static final String FIELD_ENTITY_PARAMETERS_VALUE = "value";
  
  public static final String STATIC = "static";
  public static final String DYNAMIC = "dynamic";
  
  public static final String FIELD_COLUMNS_NAME = "name";
  public static final String FIELD_COLUMNS_VISIBLE = "visible";
  
  public static final TableFormat VFT_ENTITY_PARAMETERS = new TableFormat(0, Integer.MAX_VALUE);
  
  static
  {
    VFT_ENTITY_PARAMETERS.addField(FieldFormat.create(FIELD_ENTITY_PARAMETERS_VALUE, FieldFormat.STRING_FIELD, Cres.get().getString("value")));
  }
  
  public static final TableFormat FORMAT = new TableFormat(1, 1);
  
  static
  {
    FieldFormat ff = FieldFormat.create(FIELD_APPEARANCE, FieldFormat.INTEGER_FIELD, Cres.get().getString("appearance"));
    ff.addSelectionValue(Reference.APPEARANCE_LINK, Cres.get().getString("link"));
    ff.addSelectionValue(Reference.APPEARANCE_BUTTON, Cres.get().getString("wButton"));
    ff.setDefault(Reference.APPEARANCE_LINK);
    
    FORMAT.addField(ff);
    
    ff = FieldFormat.create("<" + FIELD_REFERENCE_TYPE + "><I><A=" + ContextUtils.ENTITY_ACTION + "><D=" + Cres.get().getString("referenceType") + ">");
    ff.addSelectionValue(ContextUtils.ENTITY_VARIABLE, Cres.get().getString("variable"));
    ff.addSelectionValue(ContextUtils.ENTITY_FUNCTION, Cres.get().getString("function"));
    ff.addSelectionValue(ContextUtils.ENTITY_EVENT, Cres.get().getString("event"));
    ff.addSelectionValue(ContextUtils.ENTITY_ACTION, Cres.get().getString("action"));
    ff.setDefault(ContextUtils.ENTITY_VARIABLE);
    FORMAT.addField(ff);
    
    ff = FieldFormat.create("<" + FIELD_CONTEXT_TYPE + "><S><D=" + Cres.get().getString("contextType") + ">");
    ff.addSelectionValue(STATIC, Cres.get().getString("static"));
    ff.addSelectionValue(DYNAMIC, Cres.get().getString("dynamic"));
    ff.setDefault(STATIC);
    FORMAT.addField(ff);
    
    FORMAT.addField(FieldFormat.create("<" + FIELD_CONTEXT + "><S><D=" + Cres.get().getString("context") + ">").setEditor(StringFieldFormat.EDITOR_CONTEXT));
    
    FORMAT.addField(FieldFormat.create("<" + FIELD_CONTEXT_EXPRESSION + "><S><D=" + Cres.get().getString("contextExpression") + ">").setEditor(StringFieldFormat.EDITOR_EXPRESSION));
    
    ff = FieldFormat.create("<" + FIELD_ENTITY_TYPE + "><S><D=" + Cres.get().getString("entityType") + ">");
    ff.addSelectionValue(STATIC, Cres.get().getString("static"));
    ff.addSelectionValue(DYNAMIC, Cres.get().getString("dynamic"));
    ff.setDefault(STATIC);
    FORMAT.addField(ff);
    
    FORMAT.addField(FieldFormat.create("<" + FIELD_ENTITY + "><S><D=" + Cres.get().getString("entity") + ">"));
    FORMAT.addField(FieldFormat.create("<" + FIELD_ENTITY_EXPRESSION + "><S><D=" + Cres.get().getString("entityExpression") + ">").setEditor(StringFieldFormat.EDITOR_EXPRESSION));
    
    FORMAT.addField(FieldFormat.create(FIELD_ENTITY_PARAMETERS, FieldFormat.DATATABLE_FIELD, Cres.get().getString("entityParameters")).setDefault(new SimpleDataTable(VFT_ENTITY_PARAMETERS)));
    
    FORMAT.addField(FieldFormat.create("<" + FIELD_ICON + "><S><F=N><D=" + Cres.get().getString("icon") + ">"));
    
    FORMAT.addBinding(FIELD_CONTEXT + "#" + DataTableBindingProvider.PROPERTY_HIDDEN, "{" + FIELD_CONTEXT_TYPE + "} == \'" + "dynamic" + "\'");
    FORMAT.addBinding(FIELD_CONTEXT_EXPRESSION + "#" + DataTableBindingProvider.PROPERTY_HIDDEN, "{" + FIELD_CONTEXT_TYPE + "} == \'" + "static" + "\'");
    
    FORMAT.addBinding(FIELD_ENTITY + "#" + DataTableBindingProvider.PROPERTY_HIDDEN, "{" + FIELD_ENTITY_TYPE + "} == \'" + "dynamic" + "\'");
    FORMAT.addBinding(FIELD_ENTITY_EXPRESSION + "#" + DataTableBindingProvider.PROPERTY_HIDDEN, "{" + FIELD_ENTITY_TYPE + "} == \'" + "static" + "\'");
    
    String contextTypeExpression = "(" + "{" + FIELD_CONTEXT_TYPE + "} == " + "\'static\'" + " ? {" + FIELD_CONTEXT + "} : {" + FIELD_CONTEXT_EXPRESSION + "}" + ")";// TODO kill
    contextTypeExpression = "{" + FIELD_CONTEXT_TYPE + "} == " + "\'static\'" + " ? {" + FIELD_CONTEXT + "} : evaluate({" + FIELD_CONTEXT_EXPRESSION + "})";
    String ref = FIELD_ENTITY + "#" + DataTableBindingProvider.PROPERTY_CHOICES;
    
    String exp1 = "{" + FIELD_REFERENCE_TYPE + "} == " + ContextUtils.ENTITY_VARIABLE + " ? " + DefaultFunctions.CALL_FUNCTION + "(\'" + Contexts.CTX_UTILITIES + "\',\'"
        + UtilitiesContextConstants.F_VARIABLES_BY_MASK + "\'," + contextTypeExpression + ")" + ":";
    String exp2 = "({" + FIELD_REFERENCE_TYPE + "} == " + ContextUtils.ENTITY_EVENT + " ? " + DefaultFunctions.CALL_FUNCTION + "(\'" + Contexts.CTX_UTILITIES + "\',\'"
        + UtilitiesContextConstants.F_EVENTS_BY_MASK + "\'," + contextTypeExpression + ")" + ":";
    String exp3 = "({" + FIELD_REFERENCE_TYPE + "} == " + ContextUtils.ENTITY_ACTION + " ? " + DefaultFunctions.CALL_FUNCTION + "(\'" + Contexts.CTX_UTILITIES + "\',\'"
        + UtilitiesContextConstants.F_ACTIONS_BY_MASK + "\'," + contextTypeExpression + ",true)" + ":"; // includeNonHeadless = true
    String exp4 = "({" + FIELD_REFERENCE_TYPE + "} == " + ContextUtils.ENTITY_FUNCTION + " ? " + DefaultFunctions.CALL_FUNCTION + "(\'" + Contexts.CTX_UTILITIES + "\',\'"
        + UtilitiesContextConstants.F_FUNCTIONS_BY_MASK + "\'," + contextTypeExpression + ")" + ":";
    String exp5 = DefaultFunctions.CALL_FUNCTION + "(\'" + Contexts.CTX_UTILITIES + "\',\'" + UtilitiesContextConstants.F_VARIABLES_BY_MASK + "\'," + contextTypeExpression + ")" + "" + ")))";
    
    StringBuilder sb = new StringBuilder();
    sb.append(exp1).append(exp2).append(exp3).append(exp4).append(exp5);
    
    FORMAT.addBinding(ref, sb.toString());
  }
  
  public ReferenceConverter()
  {
    editors.add(StringFieldFormat.EDITOR_REFERENCE);
    types.add(String.valueOf(FieldFormat.STRING_FIELD));
  }
  
  @Override
  public String convertToString(DataTable options)
  {
    return options.encode();
  }
  
  public static Reference createReference(Object value, FieldFormat ff, ContextManager cm, CallerController cc)
  {
    return createReference(value, ff, cm, cc, null);
  }
  
  public static Reference createReference(Object value, FieldFormat ff, ContextManager cm, CallerController cc, Context defaultContext)
  {
    boolean hasEditorOptions = ff != null && ff.getEditorOptions() != null;
    
    if (!hasEditorOptions)
    {
      if (value != null)
        return new Reference(value.toString());
      return new Reference();
    }
    
    boolean isTable = DataTableUtils.isEncodedTable(ff.getEditorOptions());
    
    if (isTable)
    {
      try
      {
        return toReference(new SimpleDataTable(ff.getEditorOptions()), cm, cc, defaultContext);
      }
      catch (DataTableException ex)
      {
        return null;
      }
    }
    else
      return new Reference(ff.getEditorOptions());
  }
  
  public static Reference toReference(DataTable options, ContextManager cm, CallerController cc)
  {
    return toReference(options, cm, cc, null);
  }
  
  public static Reference toReference(DataTable options, ContextManager cm, CallerController cc, Context defaultContext)
  {
    if (options == null)
      return new Reference();
    Reference ref = new Reference();
    for (DataRecord rec : options)
    {
      ref.setSchema("class");
      
      Evaluator evaluator = new Evaluator(cm, cc);
      evaluator.setDefaultContext(defaultContext);
      
      if (rec.getString(FIELD_CONTEXT_TYPE).equalsIgnoreCase("static"))
        ref.setContext(rec.getString(FIELD_CONTEXT));
      else
      {
        ref.setContext(evaluate(rec.getString(FIELD_CONTEXT_EXPRESSION), evaluator));
      }
      
      if (rec.getString(FIELD_ENTITY_TYPE).equalsIgnoreCase("static"))
        ref.setEntity(rec.getString(FIELD_ENTITY));
      else
      {
        ref.setEntity(evaluate(rec.getString(FIELD_ENTITY_EXPRESSION), evaluator));
      }
      
      ref.setEntityType(rec.getInt(FIELD_REFERENCE_TYPE));
      
      ref.setAppearance(rec.getInt(FIELD_APPEARANCE));
      
      if (rec.getDataTable(FIELD_ENTITY_PARAMETERS) != null)
        for (DataRecord rec2 : rec.getDataTable(FIELD_ENTITY_PARAMETERS))
          ref.addParameter(rec2.getString(FIELD_ENTITY_PARAMETERS_VALUE));
        
      if (rec.getString(ReferenceConverter.FIELD_ICON) != null)
        ref.setField(rec.getString(FIELD_ICON));
    }
    return ref;
  }
  
  private static String evaluate(String expression, Evaluator evaluator)
  {
    try
    {
      return (String) evaluator.evaluate(new Expression(expression));
    }
    catch (SyntaxErrorException | EvaluationException ex)
    {
      Log.DATATABLEEDITOR.debug(ex.getMessage(), ex);
      return expression;
    }
  }
  
  @Override
  public TableFormat getFormat()
  {
    return FORMAT;
  }
  
}
