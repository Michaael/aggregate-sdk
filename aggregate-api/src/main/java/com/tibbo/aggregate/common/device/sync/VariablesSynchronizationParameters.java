package com.tibbo.aggregate.common.device.sync;

import java.util.Set;

import com.tibbo.aggregate.common.device.DeviceContext;

public class VariablesSynchronizationParameters extends AbstractSynchronizationParameters
{
  
  public VariablesSynchronizationParameters(String variable, int direction)
  {
    super(null, false, false, false, direction);
    variables.add(variable);
  }
  
  public VariablesSynchronizationParameters(String variable)
  {
    this(variable, DeviceContext.DIRECTION_AUTO);
  }

  public VariablesSynchronizationParameters(Set<String> vars, int direction)
  {
    super(null, false, false, false, direction);
    variables.addAll(vars);
  }
}
