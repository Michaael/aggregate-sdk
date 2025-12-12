package com.tibbo.aggregate.common.event;

import com.tibbo.aggregate.common.*;
import com.tibbo.aggregate.common.datatable.*;
import com.tibbo.aggregate.common.datatable.field.*;
import com.tibbo.aggregate.common.datatable.validator.*;
import com.tibbo.aggregate.common.expression.*;

public class EventEnrichmentRule extends AggreGateBean
{
  private static final String FIELD_NAME = "name";
  private static final String FIELD_EXPRESSION = "expression";
  
  public static final TableFormat FORMAT = new TableFormat();
  static
  {
    FORMAT.addTableValidator(new TableKeyFieldsValidator());
    
    FieldFormat ff = FieldFormat.create("<" + FIELD_NAME + "><S><F=K><D=" + Cres.get().getString("name") + ">");
    ff.addValidator(ValidatorHelper.NAME_LENGTH_VALIDATOR);
    ff.addValidator(ValidatorHelper.NAME_SYNTAX_VALIDATOR);
    FORMAT.addField(ff);
    
    FORMAT.addField(FieldFormat.create("<" + FIELD_EXPRESSION + "><S><D=" + Cres.get().getString("expression") + "><E=" + StringFieldFormat.EDITOR_EXPRESSION + ">"));
    
    FORMAT.setNamingExpression("print({}, \"{" + FIELD_NAME + "}\", \", \")");
  }
  
  private String name;
  private String expression;
  
  // State
  private Expression enrichmentExpression;
  
  public EventEnrichmentRule()
  {
    super(FORMAT);
    
  }
  
  public EventEnrichmentRule(DataRecord data)
  {
    super(FORMAT, data);
  }
  
  public String getName()
  {
    return name;
  }
  
  public void setName(String name)
  {
    this.name = name;
  }
  
  public String getExpression()
  {
    return expression;
  }
  
  public void setExpression(String expression)
  {
    this.expression = expression;
    
    enrichmentExpression = null;
  }
  
  public Expression getEnrichmentExpression()
  {
    if (enrichmentExpression == null)
    {
      enrichmentExpression = (expression != null && expression.length() > 0) ? new Expression(expression) : null;
    }
    
    return enrichmentExpression;
  }
  
}
