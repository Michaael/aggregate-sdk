package com.tibbo.aggregate.common.server;

import com.tibbo.aggregate.common.context.ContextException;

public interface GroupContext<C extends ServerContext> extends ServerContext<C>, GroupContextConstants
{
  boolean isHidesMembers();
  
  void addMember(String path) throws ContextException;
  
  void removeMember(String path, boolean failIfDynamic) throws ContextException;
  
  void renameMember(String oldPath, String newPath) throws ContextException;
  
  void memberAdded(ServerContext recursiveMember) throws ContextException;
  
  void memberRemoved(ServerContext recursiveMember) throws ContextException;

}
