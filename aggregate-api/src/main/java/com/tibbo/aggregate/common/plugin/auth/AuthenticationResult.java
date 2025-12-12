package com.tibbo.aggregate.common.plugin.auth;

public interface AuthenticationResult {
    boolean isSuccessfull();

    String getUsername();

    String getLogin();

    Object getParam(AuthenticationParams accessToken);
}
