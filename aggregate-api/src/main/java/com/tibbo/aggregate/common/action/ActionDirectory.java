package com.tibbo.aggregate.common.action;

public interface ActionDirectory<L extends ActionLocator>
{
  ActionDefinition getActionDefinition(L locator);
}
