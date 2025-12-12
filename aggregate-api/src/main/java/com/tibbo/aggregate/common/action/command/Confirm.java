package com.tibbo.aggregate.common.action.command;

import java.util.*;

import com.tibbo.aggregate.common.*;
import com.tibbo.aggregate.common.action.*;
import com.tibbo.aggregate.common.datatable.*;

public class Confirm extends GenericActionCommand
{
  public static final String CF_MESSAGE = "message";
  public static final String CF_OPTION_TYPE = "optionType";
  public static final String CF_MESSAGE_TYPE = "messageType";
  
  public static final String RF_OPTION = "option";
  
  public static final TableFormat CFT_CONFIRM = new TableFormat(1, 1);
  static
  {
    CFT_CONFIRM.addField("<" + Confirm.CF_MESSAGE + "><S><D="+ Cres.get().getString("message")+">");
    CFT_CONFIRM.addField("<" + Confirm.CF_OPTION_TYPE + "><I><D="+ Cres.get().getString("acOptionType")+">");
    CFT_CONFIRM.addField("<" + Confirm.CF_MESSAGE_TYPE + "><I><D="+ Cres.get().getString("acMessageType")+">");
  }
  
  public static final TableFormat RFT_CONFIRM = new TableFormat(1, 1, "<" + RF_OPTION + "><I><D=" + Cres.get().getString("option") + ">");
  
  private String message;
  private int optionType;
  private int messageType;
  
  public Confirm()
  {
    super(ActionUtils.CMD_CONFIRM, CFT_CONFIRM, RFT_CONFIRM);
  }
  
  public Confirm(String message)
  {
    this(Cres.get().getString("confirmation"), message, ActionUtils.YES_NO_OPTION, ActionUtils.QUESTION_MESSAGE);
  }
  
  public Confirm(String title, String message, int optionType, int messageType)
  {
    super(ActionUtils.CMD_CONFIRM, title);
    this.message = message;
    this.optionType = optionType;
    this.messageType = messageType;
  }
  
  public Confirm(String title, DataTable parameters)
  {
    super(ActionUtils.CMD_CONFIRM, title, parameters, CFT_CONFIRM);
  }
  
  @Override
  public DataTable constructParameters()
  {
    return new DataRecord(CFT_CONFIRM).addString(message).addInt(optionType).addInt(messageType).wrap();
  }
  
  @Override
  public GenericActionResponse createDefaultResponse()
  {
    
    TableFormat responseFormat = RFT_CONFIRM.clone();
    
    Map<Object, String> selectionValues = new LinkedHashMap();
    
    int optionType = getParameters().rec().getInt(Confirm.CF_OPTION_TYPE);
    if (ActionUtils.YES_NO_OPTION == optionType)
    {
      selectionValues.put(ActionUtils.YES_OPTION, Cres.get().getString("yes"));
      selectionValues.put(ActionUtils.NO_OPTION, Cres.get().getString("no"));
    }
    else if (ActionUtils.OK_CANCEL_OPTION == optionType)
    {
      selectionValues.put(ActionUtils.OK_OPTION, Cres.get().getString("ok"));
      selectionValues.put(ActionUtils.CANCEL_OPTION, Cres.get().getString("cancel"));
    }
    else if (ActionUtils.YES_NO_CANCEL_OPTION == optionType)
    {
      selectionValues.put(ActionUtils.YES_OPTION, Cres.get().getString("yes"));
      selectionValues.put(ActionUtils.NO_OPTION, Cres.get().getString("no"));
      selectionValues.put(ActionUtils.CANCEL_OPTION, Cres.get().getString("cancel"));
    }
    else
    {
      throw new IllegalStateException("Unsupported option type: " + optionType);
    }
    selectionValues.put(ActionUtils.CLOSED_OPTION, Cres.get().getString("close"));
    
    responseFormat.getField(RF_OPTION).setSelectionValues(selectionValues);
    
    return new GenericActionResponse(new SimpleDataTable(responseFormat, true));
  }
  
  public static int parseConfirm(GenericActionResponse resp)
  {
    DataTable t = resp != null ? resp.getParameters() : null;
    
    if (t == null || t.getRecordCount() == 0)
    {
      return ActionUtils.CANCEL_OPTION;
    }
    
    if (!t.getFormat().hasField(RF_OPTION))
    {
      throw new IllegalArgumentException("Malformed response");
    }
    
    int option = t.rec().getInt(RF_OPTION);
    switch (option)
    {
      case ActionUtils.YES_OPTION:
      case ActionUtils.NO_OPTION:
      case ActionUtils.CANCEL_OPTION:
      case ActionUtils.CLOSED_OPTION:
        break;
      default:
        throw new IllegalArgumentException("Illegal response option: " + option);
    }
    
    return option;
  }
  
  public String getMessage()
  {
    return message;
  }
  
  public int getOptionType()
  {
    return optionType;
  }
  
  public int getMessageType()
  {
    return messageType;
  }
  
  public void setMessage(String message)
  {
    this.message = message;
  }
  
  public void setOptionType(int optionType)
  {
    this.optionType = optionType;
  }
  
  public void setMessageType(int messageType)
  {
    this.messageType = messageType;
  }
}
