package com.tibbo.aggregate.common.protocol;

import static com.tibbo.aggregate.common.protocol.ProtocolVersion.*;

import com.tibbo.aggregate.common.datatable.encoding.*;

public class ProtocolCommandBuilder
{
  private final boolean json;
  
  public ProtocolCommandBuilder(boolean json)
  {
    this.json = json;
  }
  
  public OutgoingAggreGateCommand createCommand()
  {
    return createCommand(json);
  }
  
  public static OutgoingAggreGateCommand createCommand(boolean json)
  {
    if (json)
      return new OutgoingJsonCommand();
    else
      return new OutgoingAggreGateCommand();
  }
  
  public OutgoingAggreGateCommand startMessage()
  {
    return startMessage(V4.notation());
  }
  
  public OutgoingAggreGateCommand startMessage(String version)
  {
    OutgoingAggreGateCommand cmd = createCommand(json);
    cmd.addParam(String.valueOf(AggreGateCommand.COMMAND_CODE_MESSAGE));
    cmd.addParam(AggreGateCommand.generateId());
    cmd.addParam(String.valueOf(AggreGateCommand.MESSAGE_CODE_START));
    cmd.addParam(version);
    return cmd;
  }
  
  public OutgoingAggreGateCommand operationMessage()
  {
    OutgoingAggreGateCommand cmd = createCommand(json);
    cmd.addParam(String.valueOf(AggreGateCommand.COMMAND_CODE_MESSAGE));
    cmd.addParam(AggreGateCommand.generateId());
    cmd.addParam(String.valueOf(AggreGateCommand.MESSAGE_CODE_OPERATION));
    return cmd;
  }
  
  public OutgoingAggreGateCommand getVariableOperation(String context, String name)
  {
    OutgoingAggreGateCommand cmd = operationMessage();
    cmd.addParam(String.valueOf(AggreGateCommand.COMMAND_OPERATION_GET_VAR));
    cmd.addParam(context);
    cmd.addParam(name);
    return cmd;
  }
  
  public OutgoingAggreGateCommand setVariableOperation(String context, String name, String encodedValue, String queueName)
  {
    OutgoingAggreGateCommand cmd = operationMessage();
    cmd.addParam(String.valueOf(AggreGateCommand.COMMAND_OPERATION_SET_VAR));
    cmd.addParam(context);
    cmd.addParam(name);
    cmd.addParam(encodedValue);
    if (queueName != null)
    {
      cmd.addParam(TransferEncodingHelper.encode(queueName));
    }
    return cmd;
  }
  
  public OutgoingAggreGateCommand callFunctionOperation(String context, String name, String encodedInput, String queueName, String flags)
  {
    OutgoingAggreGateCommand cmd = operationMessage();
    cmd.addParam(String.valueOf(AggreGateCommand.COMMAND_OPERATION_CALL_FUNCTION));
    cmd.addParam(context);
    cmd.addParam(name);
    cmd.addParam(encodedInput);
    if (queueName != null || flags != null)
    {
      if (queueName == null)
      {
        queueName = "";
      }
      cmd.addParam(TransferEncodingHelper.encode(queueName));
    }
    
    if (flags != null)
    {
      cmd.addParam(TransferEncodingHelper.encode(flags));
    }
    return cmd;
  }
  
  public OutgoingAggreGateCommand addEventListenerOperation(String context, String name, Integer listenerHashCode, String filter, String fingerprint)
  {
    return eventListenerOperation(AggreGateCommand.COMMAND_OPERATION_ADD_EVENT_LISTENER, context, name, listenerHashCode, filter, fingerprint);
  }
  
  public OutgoingAggreGateCommand removeEventListenerOperation(String context, String name, Integer listenerHashCode, String filter, String fingerprint)
  {
    return eventListenerOperation(AggreGateCommand.COMMAND_OPERATION_REMOVE_EVENT_LISTENER, context, name, listenerHashCode, filter, fingerprint);
  }
  
  private OutgoingAggreGateCommand eventListenerOperation(char commandName, String context, String name, Integer listenerHashCode, String filter, String fingerprint)
  {
    OutgoingAggreGateCommand cmd = operationMessage();
    
    cmd.addParam(String.valueOf(commandName));
    cmd.addParam(context);
    cmd.addParam(name);
    cmd.addParam(listenerHashCode != null ? listenerHashCode.toString() : "");
    
    if (filter != null)
    {
      cmd.addParam(TransferEncodingHelper.encode(filter));
    }
    else if (fingerprint != null)
    {
      // add empty filter parameter to ensure fingerprint parameter order in command
      cmd.addParam("");
    }
    
    if (fingerprint != null)
    {
      cmd.addParam(TransferEncodingHelper.encode(fingerprint));
    }
    
    return cmd;
  }
  
  public final static String CLIENT_COMMAND_SEPARATOR = String.valueOf('\u0017');
  
}
