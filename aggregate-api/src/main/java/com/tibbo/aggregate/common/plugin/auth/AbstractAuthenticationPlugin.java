package com.tibbo.aggregate.common.plugin.auth;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.tibbo.aggregate.common.context.ContextException;
import com.tibbo.aggregate.common.plugin.BasePlugin;

public abstract class AbstractAuthenticationPlugin extends BasePlugin implements AuthenticationPlugin
{
  public AbstractAuthenticationPlugin()
  {
    super();
  }
  
  public AbstractAuthenticationPlugin(String name)
  {
    super(name);
  }
  
  @Override
  public boolean isAuthenticationByPassword()
  {
    return false;
  }

  @Override
  public void logout(Map<AuthenticationParams, Object> params) throws ContextException
  {
  }

  @Override
  public AuthenticationResult refreshToken(ImmutableMap<AuthenticationParams, Object> params) throws ContextException
  {
    return BaseAuthenticationResult.ofFailed();
  }
}