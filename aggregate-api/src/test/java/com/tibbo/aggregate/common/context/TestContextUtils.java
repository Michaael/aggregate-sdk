package com.tibbo.aggregate.common.context;

import java.util.List;

import com.tibbo.aggregate.common.tests.CommonsTestCase;

public class TestContextUtils extends CommonsTestCase
{
  public void testMatchesToMask()
  {
    assertTrue(ContextUtils.matchesToMask("users.admin.deviceservers.c1", "users.admin.deviceservers.c1"));
    assertTrue(ContextUtils.matchesToMask("users.*.deviceservers.c1", "users.admin.deviceservers.c1"));
    assertTrue(ContextUtils.matchesToMask("users.*.deviceservers.*", "users.admin.deviceservers.c1"));
    assertFalse(ContextUtils.matchesToMask("users.admin.deviceservers.c1", "users.admin.deviceservers.c1.devices.1"));
    assertFalse(ContextUtils.matchesToMask("users.*.deviceservers.*", "usersxxx.admin.deviceservers.c1"));
    assertTrue(ContextUtils.matchesToMask("users.*.deviceservers.c1", "users.admin.deviceservers.c1.devices", true, false));
    assertFalse(ContextUtils.matchesToMask("users.*.deviceservers.c1", "users.admin.deviceservers.c1.devices", false, false));
  }
  
  public void testDefaultDeviceContextName()
  {
    String username = "someUserName";
    assertEquals("users." + username + ".devices", ContextUtils.devicesContextPath(username));
  }

  public void testExpandNullMaskToPaths()
  {
    List<String> paths = ContextUtils.expandMaskToPaths(null, null, null, true);
    assertNotNull(paths);
    assertTrue(paths.isEmpty());
  }
}
