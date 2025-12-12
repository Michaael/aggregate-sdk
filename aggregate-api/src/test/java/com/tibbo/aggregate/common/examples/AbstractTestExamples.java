package com.tibbo.aggregate.common.examples;

import com.tibbo.aggregate.common.context.*;
import com.tibbo.aggregate.common.protocol.*;
import com.tibbo.aggregate.common.tests.*;

public abstract class AbstractTestExamples extends CommonsTestCase
{
  public static final String USERNAME = "admin";
  
  protected RemoteServerController rlc;
  
  @Override
  protected void setUp() throws Exception
  {
    super.setUp();
    prepareClientConnection();
  }
  
  protected void prepareClientConnection() throws Exception
  {
    RemoteServer rls = new RemoteServer("localhost", RemoteServer.DEFAULT_PORT, USERNAME, "admin");
    rlc = new RemoteServerController(rls, true);
    rlc.connect();
    rlc.login();
  }
  
  @Override
  protected void tearDown() throws Exception
  {
    super.tearDown();
    if (rlc != null)
      rlc.disconnect();
  }
  
  public Context getContext(String path) throws Exception
  {
    return rlc.getContextManager().get(path);
  }
  
}
