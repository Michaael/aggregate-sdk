package com.tibbo.aggregate.common.expression;

import com.tibbo.aggregate.common.context.*;
import com.tibbo.aggregate.common.datatable.*;
import com.tibbo.aggregate.common.util.*;

public interface ReferenceResolver
{
  Object resolveReference(Reference ref, EvaluationEnvironment environment) throws SyntaxErrorException, EvaluationException, ContextException;
  
  void setContextManager(ContextManager cm);
  
  ContextManager getContextManager();
  
  void setDefaultTable(DataTable defaultTable);
  
  DataTable getDefaultTable();
  
  void setDefaultContext(Context defaultContext);
  
  Context getDefaultContext();
  
  void setCallerController(CallerController callerController);
  
  CallerController getCallerController();
  
  void setEvaluator(Evaluator evaluator);
  
  Evaluator getEvaluator();
  
  void setDefaultRow(Integer defaultRow);
  
  Integer getDefaultRow();
}
