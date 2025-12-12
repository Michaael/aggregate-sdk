package com.tibbo.aggregate.common.action;

import com.tibbo.aggregate.common.datatable.*;

public abstract class ProtocolHandler
{
  public static final String FIELD_ACTION_RESPONSE_PARAMETERS = "parameters";
  private static final String FIELD_ACTION_RESPONSE_REMEMBER = "remember";
  private static final String FIELD_ACTION_RESPONSE_REQUEST_ID = "requestId";
  
  private static final String FIELD_ACTION_COMMAND_TYPE = "type";
  private static final String FIELD_ACTION_COMMAND_TITLE = "title";
  private static final String FIELD_ACTION_COMMAND_PARAMETERS = "parameters";
  private static final String FIELD_ACTION_COMMAND_LAST = "last";
  private static final String FIELD_ACTION_COMMAND_BATCH_MEMBER = "batchMember";
  private static final String FIELD_ACTION_COMMAND_REQUEST_ID = "requestId";
  
  private static final String FIELD_ACTION_ID_ACTION_ID = "actionId";
  
  private static final TableFormat FORMAT_ACTION_ID_FORMAT = new TableFormat(1, 1, "<" + FIELD_ACTION_ID_ACTION_ID + "><S>");
  
  private static final TableFormat FORMAT_ACTION_COMMAND = new TableFormat(1, 1);
  static
  {
    FORMAT_ACTION_COMMAND.addField("<" + FIELD_ACTION_COMMAND_TYPE + "><S>");
    FORMAT_ACTION_COMMAND.addField("<" + FIELD_ACTION_COMMAND_TITLE + "><S><F=N>");
    FORMAT_ACTION_COMMAND.addField("<" + FIELD_ACTION_COMMAND_PARAMETERS + "><T><F=N>");
    FORMAT_ACTION_COMMAND.addField("<" + FIELD_ACTION_COMMAND_LAST + "><B>");
    FORMAT_ACTION_COMMAND.addField("<" + FIELD_ACTION_COMMAND_BATCH_MEMBER + "><B>");
    FORMAT_ACTION_COMMAND.addField("<" + FIELD_ACTION_COMMAND_REQUEST_ID + "><S><F=N>");
  }
  
  private static final TableFormat FORMAT_ACTION_RESPONSE = new TableFormat(1, 1);
  static
  {
    FORMAT_ACTION_RESPONSE.addField("<" + FIELD_ACTION_RESPONSE_PARAMETERS + "><T><F=N>");
    FORMAT_ACTION_RESPONSE.addField("<" + FIELD_ACTION_RESPONSE_REMEMBER + "><B>");
    FORMAT_ACTION_RESPONSE.addField("<" + FIELD_ACTION_RESPONSE_REQUEST_ID + "><S><F=N>");
  }
  
  public static DataTable actionIdToDataTable(ActionIdentifier id)
  {
    String stringId = id != null ? id.toString() : null;
    DataTable dataTable = new SimpleDataTable(FORMAT_ACTION_ID_FORMAT);
    dataTable.addRecord().addString(stringId);
    return dataTable;
  }
  
  public static ActionIdentifier actionIdFromDataTable(DataTable table)
  {
    if (table == null)
    {
      return null;
    }
    
    if (!table.getFormat().extend(FORMAT_ACTION_ID_FORMAT))
    {
      throw new IllegalArgumentException("Illegal action id table format: " + table.getFormat());
    }
  
    return new ActionIdentifier(table.rec().getString(FIELD_ACTION_ID_ACTION_ID));
  }
  
  public static DataTable actionCommandToDataTable(GenericActionCommand cmd)
  {
    DataTable table = new SimpleDataTable(FORMAT_ACTION_COMMAND);
    
    if (cmd == null)
    {
      return table;
    }
    
    DataRecord rec = table.addRecord();
    
    rec.setValue(FIELD_ACTION_COMMAND_TYPE, cmd.getType());
    rec.setValue(FIELD_ACTION_COMMAND_TITLE, cmd.getTitle());
    rec.setValue(FIELD_ACTION_COMMAND_PARAMETERS, cmd.getParameters());
    rec.setValue(FIELD_ACTION_COMMAND_LAST, cmd.isLast());
    rec.setValue(FIELD_ACTION_COMMAND_BATCH_MEMBER, cmd.isBatchEntry());
    rec.setValue(FIELD_ACTION_COMMAND_REQUEST_ID, cmd.getRequestId() != null ? cmd.getRequestId().toString() : null);
    
    return table;
  }
  
  public static GenericActionCommand actionCommandFromDataTable(DataTable table)
  {
    if (table == null)
    {
      return null;
    }
    
    if (table.getRecordCount() == 0)
    {
      return null;
    }
    
    String type = table.rec().getString(FIELD_ACTION_COMMAND_TYPE);
    
    GenericActionCommand actionCmd = new GenericActionCommand(type, table.rec().getString(FIELD_ACTION_COMMAND_TITLE));
    
    actionCmd.setParameters(table.rec().getDataTable(FIELD_ACTION_COMMAND_PARAMETERS));
    actionCmd.setLast(table.rec().getBoolean(FIELD_ACTION_COMMAND_LAST));
    actionCmd.setBatchEntry(table.rec().getBoolean(FIELD_ACTION_COMMAND_BATCH_MEMBER));
    
    String requestIdString = table.rec().getString(FIELD_ACTION_COMMAND_REQUEST_ID);
    actionCmd.setRequestId((requestIdString != null && requestIdString.length() > 0) ? new RequestIdentifier(requestIdString) : null);
    
    return actionCmd;
  }
  
  public static DataTable actionResponseToDataTable(GenericActionResponse response)
  {
    DataTable table = new SimpleDataTable(FORMAT_ACTION_RESPONSE);
    
    if (response == null)
    {
      return table;
    }
    
    DataRecord rec = table.addRecord();
    
    rec.setValue(FIELD_ACTION_RESPONSE_PARAMETERS, response.getParameters());
    rec.setValue(FIELD_ACTION_RESPONSE_REMEMBER, response.shouldRemember());
    rec.setValue(FIELD_ACTION_RESPONSE_REQUEST_ID, response.getRequestId() != null ? response.getRequestId().toString() : null);
    
    return table;
  }
  
  public static GenericActionResponse actionResponseFromDataTable(DataTable table)
  {
    if (table == null || table.getRecordCount() == 0)
    {
      return new GenericActionResponse(null);
    }
    
    if (!table.getFormat().extend(FORMAT_ACTION_RESPONSE))
    {
      throw new IllegalArgumentException("Illegal action response table format: " + table.getFormat());
    }
    
    String requestIdString = table.rec().getString(FIELD_ACTION_RESPONSE_REQUEST_ID);
    RequestIdentifier requestId = null;
    if (requestIdString != null && requestIdString.length() > 0)
    {
      requestId = new RequestIdentifier(requestIdString);
    }
    GenericActionResponse actionResp = new GenericActionResponse(table.rec().getDataTable(FIELD_ACTION_RESPONSE_PARAMETERS), table.rec().getBoolean(FIELD_ACTION_RESPONSE_REMEMBER), requestId);
    
    return actionResp;
  }
}
