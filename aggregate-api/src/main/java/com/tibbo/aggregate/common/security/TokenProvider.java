package com.tibbo.aggregate.common.security;

import com.tibbo.aggregate.common.*;

public interface TokenProvider
{
  byte[] getEncodedToken() throws AggreGateException;

  String getUsername() throws AggreGateException;
}