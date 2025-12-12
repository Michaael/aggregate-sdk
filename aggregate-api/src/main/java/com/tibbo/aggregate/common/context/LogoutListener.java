package com.tibbo.aggregate.common.context;

public interface LogoutListener
{
  default void beforeLogout(String sessionId)
  {
  };
  
  default void afterLogout(String sessionId)
  {
  };
}
