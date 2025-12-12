package com.tibbo.aggregate.common.action.command;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.action.ActionUtils;
import com.tibbo.aggregate.common.action.GenericActionCommand;

public class EditExpression extends GenericActionCommand
{
  public EditExpression()
  {
    super(ActionUtils.CMD_EDIT_EXPRESSION, Cres.get().getString("editExpression"));
  }
}
