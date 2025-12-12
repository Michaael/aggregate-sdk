package com.tibbo.aggregate.common.plugin.auth;

public enum AuthenticationParams
{
  /**
   * Username of the user in the external authentication facility, e.g. an LDAP login name
   */
  USERNAME,

  /**
   * Login of the user in the external authentication facility, e.g. an LDAP login name
   */
  LOGIN,
  /**
   * Password of the user in the external authentication facility
   */
  PASSWORD,
  /**
   * Authentication Code
   */
  CODE,
  /**
   * Authentication State
   */
  STATE,
  /**
   * Authentication Provider
   */
  PROVIDER,
  /**
   * Authentication Token
   */
  TOKEN,
  
  /**
   * Client Ip address
   */
  CLIENT_IP,
  
  /**
   * Access Token
   */
  ACCESS_TOKEN,
  
  /**
   * Refresh Token
   */
  REFRESH_TOKEN
  
}
