package com.tibbo.aggregate.common.action;

import java.util.*;

/**
 * <p>
 * Actions that implement this interface have static list of commands and can't send commands other than on the list. These actions may be preconfigured and run in non-interactive mode.
 * </p>
 * <p>
 * Action should not send the same command twice except for the case when received data isn't valid. If an action sends the request twice or more the container may resend it to client if possible or
 * just stop action execution with an error.
 * </p>
 * <p>
 * The list should be independed on initial configuration by initialRequest. Only ActionDefinition settings may be used.
 * <p>
 * <p>
 * If the overriden action can't provide such list it should return null.
 * <p>
 */
public interface ActionCommandList
{
  /**
   * List of ActionCommands that can be requested by the source action. It is not required for the action to send all these commands but it should never send commands that aren't on the list.
   */
  List<ActionCommand> getCommands();
}
