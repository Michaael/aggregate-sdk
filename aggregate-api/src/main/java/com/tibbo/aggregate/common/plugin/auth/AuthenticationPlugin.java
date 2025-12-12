package com.tibbo.aggregate.common.plugin.auth;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.tibbo.aggregate.common.context.ContextException;
import com.tibbo.aggregate.common.plugin.AggreGatePlugin;
import com.tibbo.aggregate.common.plugin.PluginException;

public interface AuthenticationPlugin extends AggreGatePlugin
{
  /**
   * Tries to authenticate a user. The external authentication plugin must check validity of user credentials and return an internal username of the authenticated user.
   *
   * @param params
   *          Authentication params
   * @return Authentication result
   */
  AuthenticationResult authenticate(Map<AuthenticationParams, Object> params) throws PluginException, ContextException;
  
  boolean isAuthenticationByPassword();

  /**
   * Logout external session
   *
   * @param params
   * @return
   * @throws ContextException
   */
  void logout(Map<AuthenticationParams, Object> params) throws ContextException;
  
  AuthenticationResult refreshToken(ImmutableMap<AuthenticationParams, Object> params) throws ContextException;
}
