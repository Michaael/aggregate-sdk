package com.tibbo.aggregate.common.plugin.auth;

import java.util.Map;

import com.tibbo.aggregate.common.context.ContextException;

public interface Authenticator
{
  AuthenticationResult authenticate(Map<AuthenticationParams, Object> params) throws ContextException;
}
