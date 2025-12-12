package com.tibbo.aggregate.common.protocol;

import com.tibbo.aggregate.common.AggreGateException;
import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.security.TokenProvider;

public class RemoteServer extends AggreGateNetworkDevice
{
  public final static int DEFAULT_PORT = 6460;
  public final static int DEFAULT_NON_SECURE_PORT = 6461;
  public final static String DEFAULT_USERNAME = "admin";
  public final static String DEFAULT_PASSWORD = "admin";
  
  private String username;
  private String password;
  private String provider;
  private String code;
  private String effectiveUsername;
  private String login;
  
  private TokenProvider authTokenProvider;
  
  // settings for working with certificates
  private boolean trustAll = true;
  private boolean preferCrls = true;
  private boolean noFallback = true;
  private boolean softFail = true;
  private boolean onlyEndEntity = false;
  
  private Boolean countAttempts;
  
  public RemoteServer()
  {
    this(DEFAULT_ADDRESS, DEFAULT_PORT, DEFAULT_USERNAME, DEFAULT_PASSWORD);
  }
  
  public RemoteServer(String address, int port, String username, String password)
  {
    super("server", Cres.get().getString("server"), address, port);
    this.username = username;
    this.password = password;
  }
  
  public RemoteServer(String address, int port, TokenProvider authTokenProvider) throws AggreGateException
  {
    super("server", Cres.get().getString("server"), address, port);
    this.username = authTokenProvider.getUsername();
    this.authTokenProvider = authTokenProvider;
  }
  
  public String getPassword()
  {
    return password;
  }
  
  public String getUsername()
  {
    return username;
  }
  
  public void setUsername(String username)
  {
    this.username = username;
  }
  
  public void setPassword(String password)
  {
    this.password = password;
  }
  
  public String getProvider()
  {
    return provider;
  }
  
  public void setProvider(String provider)
  {
    this.provider = provider;
  }
  
  public String getCode()
  {
    return code;
  }
  
  public void setCode(String code)
  {
    this.code = code;
  }
  
  public String getEffectiveUsername()
  {
    return effectiveUsername;
  }
  
  public void setEffectiveUsername(String effectiveUsername)
  {
    this.effectiveUsername = effectiveUsername;
  }
  
  public String getLogin()
  {
    return login;
  }
  
  public void setLogin(String login)
  {
    this.login = login;
  }
  
  public TokenProvider getAuthTokenProvider()
  {
    return authTokenProvider;
  }
  
  @Override
  public String getInfo()
  {
    return super.getInfo() + (username != null ? ", " + username : "");
  }
  
  public Boolean isCountAttempts()
  {
    return countAttempts;
  }
  
  public void setCountAttempts(Boolean countAttempts)
  {
    this.countAttempts = countAttempts;
  }
  
  public boolean isTrustAll()
  {
    return trustAll;
  }
  
  public void setTrustAll(boolean trustAll)
  {
    this.trustAll = trustAll;
  }
  
  public boolean isPreferCrls()
  {
    return preferCrls;
  }
  
  public void setPreferCrls(boolean preferCrls)
  {
    this.preferCrls = preferCrls;
  }
  
  public boolean isNoFallback()
  {
    return noFallback;
  }
  
  public void setNoFallback(boolean noFallback)
  {
    this.noFallback = noFallback;
  }
  
  public boolean isSoftFail()
  {
    return softFail;
  }
  
  public void setSoftFail(boolean softFail)
  {
    this.softFail = softFail;
  }
  
  public boolean isOnlyEndEntity()
  {
    return onlyEndEntity;
  }
  
  public void setOnlyEndEntity(boolean onlyEndEntity)
  {
    this.onlyEndEntity = onlyEndEntity;
  }
  
  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((password == null) ? 0 : password.hashCode());
    result = prime * result + ((username == null) ? 0 : username.hashCode());
    return result;
  }
  
  @Override
  public boolean equals(Object obj)
  {
    if (this == obj)
    {
      return true;
    }
    if (!super.equals(obj))
    {
      return false;
    }
    if (getClass() != obj.getClass())
    {
      return false;
    }
    RemoteServer other = (RemoteServer) obj;
    if (password == null)
    {
      if (other.password != null)
      {
        return false;
      }
    }
    else if (!password.equals(other.password))
    {
      return false;
    }
    if (username == null)
    {
      if (other.username != null)
      {
        return false;
      }
    }
    else if (!username.equals(other.username))
    {
      return false;
    }
    return true;
  }
}
