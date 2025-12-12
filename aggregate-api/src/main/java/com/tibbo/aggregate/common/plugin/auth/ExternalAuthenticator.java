package com.tibbo.aggregate.common.plugin.auth;

import java.util.Map;

import com.tibbo.aggregate.common.Log;
import com.tibbo.aggregate.common.context.ContextException;
import com.tibbo.aggregate.common.plugin.PluginException;
import com.tibbo.aggregate.common.protocol.AggreGateCodes;

public class ExternalAuthenticator implements Authenticator
{
  private final AuthenticationPlugin authenticationPlugin;

  public ExternalAuthenticator(AuthenticationPlugin authenticationPlugin, String login)
  {
    this.authenticationPlugin = new AuthenticationPluginDecorator(authenticationPlugin);
  }

  @Override
  public AuthenticationResult authenticate(Map<AuthenticationParams, Object> params) throws ContextException
  {
    try
    {
      return authenticationPlugin.authenticate(params);
    }
    catch (PluginException ex)
    {
      Log.SECURITY.warn(ex.getMessage(), ex);
      ContextException cex = new ContextException(ex.getMessage(), ex);
      cex.setCode(AggreGateCodes.REPLY_CODE_DENIED);
      throw cex;
    }
  }
  
}
