package com.tibbo.aggregate.common.plugin.auth;

import java.util.Arrays;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.tibbo.aggregate.common.context.Context;
import com.tibbo.aggregate.common.context.ContextException;
import com.tibbo.aggregate.common.context.VariableDefinition;
import com.tibbo.aggregate.common.plugin.PluginException;

public class AuthenticationPluginDecorator implements AuthenticationPlugin
{
  
  private final AuthenticationPlugin delegate;
  
  public AuthenticationPluginDecorator(AuthenticationPlugin authenticationPlugin)
  {
    this.delegate = authenticationPlugin;
  }
  
  @Override
  public String getId()
  {
    return delegate.getId();
  }
  
  @Override
  public String getShortId()
  {
    return delegate.getShortId();
  }
  
  @Override
  public String getDescription()
  {
    return delegate.getDescription();
  }
  
  @Override
  public int getSortIndex()
  {
    return delegate.getSortIndex();
  }
  
  @Override
  public void globalInit(Context rootContext) throws PluginException
  {
    delegate.globalInit(rootContext);
  }
  
  @Override
  public void userInit(Context userContext) throws PluginException
  {
    delegate.userInit(userContext);
  }
  
  @Override
  public void globalDeinit(Context rootContext) throws PluginException
  {
    delegate.globalDeinit(rootContext);
  }
  
  @Override
  public void userDeinit(Context userContext) throws PluginException
  {
    delegate.userDeinit(userContext);
  }
  
  @Override
  public void globalStart() throws PluginException
  {
    delegate.globalStart();
  }
  
  @Override
  public void globalStop() throws PluginException
  {
    delegate.globalStop();
  }
  
  @Override
  public Context createGlobalConfigContext(Context rootContext, boolean requestReboot, VariableDefinition... properties)
  {
    return delegate.createGlobalConfigContext(rootContext, requestReboot, properties);
  }
  
  @Override
  public Context createUserConfigContext(Context userContext, boolean requestReboot, VariableDefinition... properties)
  {
    return delegate.createUserConfigContext(userContext, requestReboot, properties);
  }
  
  @Override
  public Context getGlobalConfigContext()
  {
    return delegate.getGlobalConfigContext();
  }
  
  @Override
  public Context getUserConfigContext(String username)
  {
    return delegate.getUserConfigContext(username);
  }
  
  @Override
  public AuthenticationResult authenticate(Map<AuthenticationParams, Object> params) throws PluginException, ContextException
  {
    try
    {
      return delegate.authenticate(params);
    }
    finally
    {
      if (params.get(AuthenticationParams.PASSWORD) != null)
      {
        Arrays.fill((char[]) params.get(AuthenticationParams.PASSWORD), '\u0000'); // clear sensitive data
      }
    }
  }
  
  @Override
  public boolean isAuthenticationByPassword()
  {
    return delegate.isAuthenticationByPassword();
  }
  
  @Override
  public AuthenticationResult refreshToken(ImmutableMap<AuthenticationParams, Object> params) throws ContextException
  {
    return delegate.refreshToken(params);
  }
  
  @Override
  public void logout(Map<AuthenticationParams, Object> params) throws ContextException
  {
    delegate.logout(params);
  }
}
