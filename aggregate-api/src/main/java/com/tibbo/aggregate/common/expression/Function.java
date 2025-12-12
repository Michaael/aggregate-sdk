package com.tibbo.aggregate.common.expression;

import com.tibbo.aggregate.common.Cres;

public interface Function
{
  
  String GROUP_CONTEXT_RELATED = Cres.get().getString("fContextOperations");
  String GROUP_DATA_TABLE_PROCESSING = Cres.get().getString("fDataTableProcessing");
  String GROUP_NUMBER_PROCESSING = Cres.get().getString("fNumberProcessing");
  String GROUP_TYPE_CONVERSION = Cres.get().getString("fTypeConversion");
  String GROUP_DATE_TIME_PROCESSING = Cres.get().getString("fDateTimeProcessing");
  String GROUP_SYSTEM = Cres.get().getString("fSystem");
  String GROUP_COLOR_PROCESSING = Cres.get().getString("fColorProcessing");
  String GROUP_STRING_PROCESSING = Cres.get().getString("fStringProcessing");
  String GROUP_MATH = Cres.get().getString("fMath");
  String GROUP_OTHER = Cres.get().getString("fOther");

  
  // Custom Function names
  String ABSOLUTE = "absolute";
  String HAS_RESOLVER = "hasResolver";
  
  String EXPRESSION_EDITOR_OPTIONS = "expressionEditorOptions";

  String getName();

  String getCategory();
  
  String getReturnValue();
  
  String getParametersFootprint();
  
  String getDescription();
  
  AttributedObject executeAttributed(Evaluator evaluator, EvaluationEnvironment environment, AttributedObject... parameters) throws EvaluationException;
  
  Object execute(Evaluator evaluator, EvaluationEnvironment environment, Object... parameters) throws EvaluationException;
}
