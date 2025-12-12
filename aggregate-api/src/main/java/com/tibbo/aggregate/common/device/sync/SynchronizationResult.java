package com.tibbo.aggregate.common.device.sync;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.context.AbstractEntityDefinition;
import com.tibbo.aggregate.common.context.VariableDefinition;

public class SynchronizationResult
{
  public static final int CODE_OK = 0;
  public static final int CODE_SUSPENDED = 10;
  public static final int CODE_DISABLED_BY_DEPENDENCY = 20;
  public static final int CODE_DECLINED_BY_DRIVER = 30;
  public static final int CODE_CONNECTION_FAILED = 40;
  public static final int CODE_INTERRUPTED = 50;
  public static final int CODE_DISCONNECTED = 60;
  public static final int CODE_ERROR = 70;
  public static final int CODE_MAINTENANCE = 80;

  private static Map<Integer, String> CODES = new HashMap();
  static
  {
    CODES.put(CODE_OK, Cres.get().getString("devSyncResOk"));
    CODES.put(CODE_SUSPENDED, Cres.get().getString("devSyncResSuspended"));
    CODES.put(CODE_DISABLED_BY_DEPENDENCY, Cres.get().getString("devSyncResDisabledByDependency"));
    CODES.put(CODE_DECLINED_BY_DRIVER, Cres.get().getString("devSyncResDeclinedByDriver"));
    CODES.put(CODE_CONNECTION_FAILED, Cres.get().getString("devSyncResConnectionFailed"));
    CODES.put(CODE_INTERRUPTED, Cres.get().getString("devSyncResInterrupted"));
    CODES.put(CODE_DISCONNECTED, Cres.get().getString("devSyncResDisconnected"));
    CODES.put(CODE_ERROR, Cres.get().getString("devSyncResError"));
    CODES.put(CODE_MAINTENANCE, Cres.get().getString("devSyncResMaintenance"));
  }
  
  private final int code;
  
  private String details;
  
  private Set<String> postponedVariables;
  
  public SynchronizationResult(int code)
  {
    if (!CODES.containsKey(code))
    {
      throw new IllegalArgumentException("Unknown synchronization result code: " + code);
    }
    
    this.code = code;
  }
  
  public SynchronizationResult(int code, String details)
  {
    this(code);
    this.details = details;
  }
  
  public SynchronizationResult(int code, Set<String> postponedVariables)
  {
    this(code);
    this.postponedVariables = postponedVariables;
  }

  public boolean isSuccessful()
  {
    return code == CODE_OK;
  }
  
  public boolean isSuspended()
  {
    return code == CODE_SUSPENDED;
  }
  
  public boolean isMaintenance()
  {
    return code == CODE_MAINTENANCE;
  }
  
  public int getCode()
  {
    return code;
  }
  
  @Override
  public String toString()
  {
    return CODES.get(code) + (details != null ? ": " + details : "");
  }
  
  public Set<String> getPostponedVariables()
  {
    return postponedVariables;
  }
}
