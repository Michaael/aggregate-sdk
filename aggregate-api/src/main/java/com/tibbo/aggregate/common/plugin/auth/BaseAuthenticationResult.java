package com.tibbo.aggregate.common.plugin.auth;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

public class BaseAuthenticationResult implements AuthenticationResult
{
  
  private final Map<String, String> environment;
  private final Map<AuthenticationParams, Object> body;
  
  private final AuthenticationState state;
  
  public BaseAuthenticationResult(AuthenticationState state, Map<AuthenticationParams, Object> body, Map<String, String> environment)
  {
    this.state = state;
    this.body = ImmutableMap.copyOf(body);
    this.environment = ImmutableMap.copyOf(environment);
  }
  
  public static BaseAuthenticationResult ofSuccessful()
  {
    return BaseAuthenticationResult.ofSuccessful("", "");
  }
  
  public static BaseAuthenticationResult ofSuccessful(String username, String login)
  {
    return new BaseAuthenticationResult(AuthenticationState.SUCCESS,
        ImmutableMap.of(AuthenticationParams.USERNAME, username, AuthenticationParams.LOGIN, login),
        ImmutableMap.of());
  }

  public static BaseAuthenticationResult ofSuccessful(String username, String login, String accessToken)
  {
    return new BaseAuthenticationResult(AuthenticationState.SUCCESS,
            ImmutableMap.of(AuthenticationParams.USERNAME, username, AuthenticationParams.LOGIN, login, AuthenticationParams.TOKEN, accessToken),
            ImmutableMap.of());
  }
  
  public static BaseAuthenticationResult ofSuccessful(ImmutableMap<AuthenticationParams, Object> body, ImmutableMap<String, String> environment)
  {
    return new BaseAuthenticationResult(AuthenticationState.SUCCESS, body, environment);
  }
  
  public static BaseAuthenticationResult ofFailed()
  {
    return new BaseAuthenticationResult(AuthenticationState.FAILED, ImmutableMap.of(), ImmutableMap.of());
  }
  
  public String getEnvironmentAttribute(String key)
  {
    return environment.get(key);
  }
  
  @Override
  public boolean isSuccessfull()
  {
    return state == AuthenticationState.SUCCESS;
  }
  
  @Override
  public String getUsername()
  {
    return (String) body.get(AuthenticationParams.USERNAME);
  }
  
  @Override
  public String getLogin()
  {
    return (String) body.get(AuthenticationParams.LOGIN);
    
  }
  
  @Override
  public Object getParam(AuthenticationParams key)
  {
    return body.get(key);
  }
  
  enum AuthenticationState
  {
    SUCCESS,
    FAILED
  }
}
