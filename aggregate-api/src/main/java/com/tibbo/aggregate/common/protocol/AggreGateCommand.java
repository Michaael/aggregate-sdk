package com.tibbo.aggregate.common.protocol;

import com.tibbo.aggregate.common.communication.*;
import com.tibbo.aggregate.common.datatable.*;
import com.tibbo.aggregate.common.datatable.encoding.*;
import com.tibbo.aggregate.common.datatable.field.*;
import com.tibbo.aggregate.common.util.*;

public abstract class AggreGateCommand extends Command
{
  public final static byte START_CHAR = 0x02;
  public final static byte END_CHAR = 0x0D;
  
  public static final char COMMAND_CODE_MESSAGE = 'M';
  public static final char COMMAND_CODE_REPLY = 'R';
  
  public static final char MESSAGE_CODE_START = 'S';
  public static final char MESSAGE_CODE_OPERATION = 'O';
  public static final char MESSAGE_CODE_EVENT = 'E';
  public static final char MESSAGE_CODE_COMPRESSION = 'C';
  
  public static final char COMMAND_OPERATION_GET_VAR = 'G';
  public static final char COMMAND_OPERATION_SET_VAR = 'S';
  public static final char COMMAND_OPERATION_CALL_FUNCTION = 'C';
  public static final char COMMAND_OPERATION_ADD_EVENT_LISTENER = 'L';
  public static final char COMMAND_OPERATION_REMOVE_EVENT_LISTENER = 'R';
  
  public static final int INDEX_COMMAND_CODE = 0;
  public static final int INDEX_ID = 1;
  
  public static final int INDEX_MESSAGE_CODE = 2;
  
  public static final int INDEX_START_PROTOCOL_VERSION = 3;
  public static final int INDEX_START_COMPRESSION = 4;
  
  public static final int INDEX_PROTOCOL_VERSION = 3;
  
  public static final int INDEX_OPERATION_CODE = 3;
  public static final int INDEX_OPERATION_CONTEXT = 4;
  public static final int INDEX_OPERATION_TARGET = 5;
  
  public static final int INDEX_OPERATION_MESSAGE_DATA_TABLE = 6;
  public static final int INDEX_OPERATION_MESSAGE_QUEUE_NAME = 7;
  public static final int INDEX_OPERATION_MESSAGE_FLAGS = 8;
  
  public static final int INDEX_OPERATION_LISTENER_CODE = 6;
  public static final int INDEX_OPERATION_FILTER = 7;
  public static final int INDEX_OPERATION_FINGERPRINT = 8;
  
  public static final int INDEX_EVENT_CONTEXT = 3;
  public static final int INDEX_EVENT_NAME = 4;
  public static final int INDEX_EVENT_LEVEL = 5;
  public static final int INDEX_EVENT_ID = 6;
  public static final int INDEX_EVENT_LISTENER = 7;
  public static final int INDEX_EVENT_DATA_TABLE = 8;
  public static final int INDEX_EVENT_TIMESTAMP = 9;
  public static final int INDEX_EVENT_SERVER_ID = 10;

  public static final int INDEX_REPLY_CODE = 2;
  public static final int INDEX_REPLY_MESSAGE = 3;
  public static final int INDEX_REPLY_DETAILS = 4;
  public static final int INDEX_DATA_TABLE_IN_REPLY = 3;
  
  public final static String CLIENT_COMMAND_SEPARATOR = String.valueOf('\u0017');
  public final static String CLIENT_COMMAND_VISIBLE_SEPARATOR = "/";
  public final static byte JSON_START_ARRAY_SEPARATOR = '[';
  
  private static int GENERATED_ID;
  
  private static final int MAX_PRINTED_LENGTH = 10000;
  
  @Override
  public String toString()
  {
    String s = super.toString();
    
    int len = s.length();
    
    s = s.substring(0, Math.min(MAX_PRINTED_LENGTH, s.length()));
    
    if (s.length() >= MAX_PRINTED_LENGTH)
    {
      s += "... (" + len + ")";
    }
    
    s = s.replaceAll(String.valueOf(AggreGateCommand.CLIENT_COMMAND_SEPARATOR), CLIENT_COMMAND_VISIBLE_SEPARATOR);
    
    s = s.replaceAll(String.valueOf(DataTableUtils.ELEMENT_START), String.valueOf(DataTableUtils.ELEMENT_VISIBLE_START));
    s = s.replaceAll(String.valueOf(DataTableUtils.ELEMENT_END), String.valueOf(DataTableUtils.ELEMENT_VISIBLE_END));
    s = s.replaceAll(String.valueOf(DataTableUtils.ELEMENT_NAME_VALUE_SEPARATOR), String.valueOf(DataTableUtils.ELEMENT_VISIBLE_NAME_VALUE_SEPARATOR));
    
    s = s.replaceAll(String.valueOf(DataTableUtils.DATA_TABLE_NULL), DataTableUtils.DATA_TABLE_VISIBLE_NULL);
    
    return s;
  }
  
  protected static synchronized String generateId()
  {
    return String.valueOf(++GENERATED_ID);
  }

  public static String checkCommandString(String commandString)
  {
    StringBuilder checkedCommandSB = new StringBuilder(commandString.length());

    for (String commandPart : commandString.split(AggreGateCommand.CLIENT_COMMAND_VISIBLE_SEPARATOR))
    {
      try
      {
        commandPart = checkCommandPart(commandPart);
      }
      catch (DataTableException ignore)
      {
      }

      if (checkedCommandSB.length() > 0)
      {
        checkedCommandSB.append(AggreGateCommand.CLIENT_COMMAND_VISIBLE_SEPARATOR);
      }
      checkedCommandSB.append(commandPart);
    }

    return checkedCommandSB.toString();
  }

  private static String checkCommandPart(String commandPart) throws DataTableException
  {
    DataTable command = new SimpleDataTable(commandPart, new ClassicEncodingSettings(true), true);
    if (command.getFormat().getFieldCount() == 0)
    {
      return commandPart;
    }
    for (FieldFormat ff : command.getFormat())
    {
      if (StringFieldFormat.EDITOR_PASSWORD.equals(ff.getEditor()))
      {
        for (DataRecord rec : command)
        {
          final String passwordString = rec.getString(ff.getName());
          if (!StringUtils.isEmpty(passwordString))
          {
            rec.setValue(ff.getName(), StringUtils.createMaskedPasswordString(passwordString.length()));
          }
        }
      }
    }
    return command.encode(true);
  }
}
