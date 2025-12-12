package com.tibbo.aggregate.common.action;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.tibbo.aggregate.common.datatable.DataTable;

public class TestActionCommand
{
  
  @Test
  public void testCommands()
  {
    ActionUtils.COMMANDS.forEach((k, v) -> {
      GenericActionCommand cmd = ActionCommandRegistry.getCommand(k);
      DataTable expect = cmd.getParameters();
      if(expect == null)
        return;
      expect.addRecord();
      if (!k.equals(ActionUtils.CMD_EDIT_WORKFLOW) && !k.equals(ActionUtils.CMD_EDIT_PROCESS_CONTROL_PROGRAM))
        assertEquals(cmd.getType(), ActionUtils.createActionCommand(k, "test", expect).getType());
    });
  }
}
