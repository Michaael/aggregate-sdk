package com.tibbo.aggregate.common.server;

public class WebContextConstants
{

  /**
   * Websocket session is being closed due to user logging out (either explicitly or automatically).
   * Should not be reopened until user comes back.
   */
  public static final String LOGOUT_REASON = "logout";

  /**
   * Websocket session is being closed because its accessToken (used at the handshake) is expired.
   * Can be reopened by the frontend without user interaction.
   */
  public static final String TOKEN_EXPIRED_REASON = "tokenExpired";

  /**
   * Websocket session is being closed due to a long period of user inactivity.
   * Should not be reopened until user comes back.
   */
  public static final String SESSION_TIMEOUT_REASON = "sessionTimeout";

  public static final String ID = "com.tibbo.linkserver.plugin.context.web";
  
  public static final String APPS_PREFIX = "apps";
  
}
