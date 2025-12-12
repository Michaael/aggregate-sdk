package com.tibbo.aggregate.common.server;

import java.util.List;
import java.util.Map;

import com.tibbo.aggregate.common.VariableValidator;
import com.tibbo.aggregate.common.action.EntityRelatedActionDescriptor;
import com.tibbo.aggregate.common.context.ContextManager;
import com.tibbo.aggregate.common.context.SessionLogger;
import com.tibbo.aggregate.common.dao.DaoFactory;
import com.tibbo.aggregate.common.datatable.DataTable;
import com.tibbo.aggregate.common.security.PermissionChecker;

public interface ServerContextManager<T extends ServerContext> extends ContextManager<T>
{
  void addVariableAction(EntityRelatedActionDescriptor action);
  
  void addEventAction(EntityRelatedActionDescriptor action);
  
  List<EntityRelatedActionDescriptor> getVariableActions();
  
  List<EntityRelatedActionDescriptor> getEventActions();
  
  PermissionChecker getPermissionChecker();
  
  DaoFactory getDaoFactory();
  
  SessionLogger getSessionLogger();
  
  Map<String, VariableValidator> getVariableValidators();
  
  void updateVariableValidators(DataTable value);
}
