package com.tibbo.aggregate.common.protocol;

import com.tibbo.aggregate.common.data.*;
import com.tibbo.aggregate.common.datatable.*;
import com.tibbo.aggregate.common.datatable.encoding.*;
import com.tibbo.aggregate.common.server.*;
import com.tibbo.aggregate.common.tests.*;
import com.tibbo.aggregate.common.util.*;

public class TestCommandEncoding extends CommonsTestCase
{
  public static final String VF_TEST_STR = "str";
  public static final String VF_TEST_INT = "int";
  public static final String VF_TEST_FLOAT = "float";
  public static final String VF_TEST_DATA = "data";
  
  public static final String SAMPLE_FUNCTION_CMD_TO_STRING = "M/42/O/C//sampleFunction/<F=<<str><S><A=>><<int><I>" +
      "<A=0>><<float><F><A=0.0>><<data><A><F=N><A=<NULL>>>><R=<xxx><12345><0.5><<NULL>>>" +
      "<R=<yyy><12345><0.5><<NULL>>>";
  
  public static final String LOGIN_FUNCTION_CMD_TO_STRING = "M/42/O/C//login/<F=<<username><S><A=>><<password><S>" +
      "<F=N><A=<NULL>><E=password>><<code><S><F=N><A=<NULL>>><<state><S><F=N><A=<NULL>>><<provider>" +
      "<S><F=N><A=<NULL>>><<countAttempts><B><F=N><A=1>><<token><A><F=N><A=<NULL>>><<login><S><F=N><A=<NULL>>><M=1><X=1>>" +
      "<R=<SomeUserName><••••••••••••••••••><<NULL>><<NULL>><<NULL>><<NULL>><<NULL>><<NULL>>>";
  
  private final ProtocolCommandBuilder protocolCommandBuilder = new ProtocolCommandBuilder(false);
  
  public static final TableFormat VFT_TEST = new TableFormat();
  static
  {
    VFT_TEST.addField("<" + VF_TEST_STR + "><S>");
    VFT_TEST.addField("<" + VF_TEST_INT + "><I>");
    VFT_TEST.addField("<" + VF_TEST_FLOAT + "><F>");
    VFT_TEST.addField("<" + VF_TEST_DATA + "><A><F=N>");
  }
  
  public void testTableEncoding() throws Exception
  {
    DataTable table = new SimpleDataTable(VFT_TEST);
    
    table.addRecord("xxx", 12345L, 0.5f);
    
    table.addRecord("yyy", 12345L, 0.5f);
    
    String encoded = table.encode(false);
    
    OutgoingAggreGateCommand outgoing = protocolCommandBuilder.setVariableOperation("context", "variable", encoded, null);
    
    @SuppressWarnings("resource")
    IncomingAggreGateCommand incoming = new IncomingAggreGateCommand(outgoing.toByteArray());
    
    DataTable decoded = new SimpleDataTable(incoming.getContent());
    
    assertFalse(incoming.isJsonBody());
    assertEquals(table, decoded);
  }
  
  public void testDecodeBigCommand() throws Exception
  {
    DataTable table = new SimpleDataTable(VFT_TEST);
    
    byte[] sb = new byte[TransferEncodingHelper.LARGE_DATA_SIZE + 1];
    
    for (int i = 0; i < sb.length; i++)
    {
      sb[i] = (byte) (i % 10 + 65);
    }
    
    table.addRecord("xxx", 12345L, 0.5f, new Data(sb));
    
    String encoded = table.encode(false);
    
    OutgoingAggreGateCommand outgoing = protocolCommandBuilder.setVariableOperation("context", "variable", encoded, null);
    
    @SuppressWarnings("resource")
    IncomingAggreGateCommand incoming = new IncomingAggreGateCommand(outgoing.toByteArray());
    
    DataTable decoded = new SimpleDataTable(incoming.getParameter(incoming.getNumberOfParameters() - 1).getString());
    
    assertFalse(incoming.isJsonBody());
    assertEquals(table, decoded);
  }
  
  public void testJsonCommand() throws Exception
  {
    DataTable table = new SimpleDataTable(VFT_TEST);
    
    table.addRecord("xxx", 12345L, 0.5f);
    
    table.addRecord("yyy", 12345L, 0.5f);
    
    String encoded = table.encode(false);
    
    @SuppressWarnings("resource")
    OutgoingAggreGateCommand outgoing = new OutgoingJsonCommand();
    outgoing.addParam("P1");
    outgoing.addParam("P2");
    outgoing.addParam(encoded);
    outgoing.complete();
    
    @SuppressWarnings("resource")
    IncomingAggreGateCommand incoming = new IncomingAggreGateCommand(outgoing.toByteArray());
    DataTable decoded = new SimpleDataTable(incoming.getParameter(incoming.getNumberOfParameters() - 1).getString());
    
    assertTrue(incoming.isJsonBody());
    assertEquals(table, decoded);
  }
  
  public void testLoginCommandString() throws SyntaxErrorException
  {
    
    assertCommandToString(LOGIN_FUNCTION_CMD_TO_STRING, String.valueOf(AggreGateCommand.COMMAND_CODE_MESSAGE),
        String.valueOf(42),
        String.valueOf(AggreGateCommand.MESSAGE_CODE_OPERATION),
        String.valueOf(AggreGateCommand.COMMAND_OPERATION_CALL_FUNCTION),
        RootContextConstants.F_LOGIN, prepareLoginData());
    assertCommandToString(SAMPLE_FUNCTION_CMD_TO_STRING, String.valueOf(AggreGateCommand.COMMAND_CODE_MESSAGE),
        String.valueOf(42),
        String.valueOf(AggreGateCommand.MESSAGE_CODE_OPERATION),
        String.valueOf(AggreGateCommand.COMMAND_OPERATION_CALL_FUNCTION),
        "sampleFunction", prepareSampleData());
  }
  
  private void assertCommandToString(String expected, String commandCode, String id, String messageCode,
      String operationCode, String target, DataTable parameters) throws SyntaxErrorException
  {
    IncomingAggreGateCommand incoming = new IncomingAggreGateCommand(
        prepareCommand(commandCode, id, messageCode, operationCode, target, parameters));
    incoming.parse();
    assertEquals(expected, AggreGateCommand.checkCommandString(incoming.toString()));
    
  }
  
  private OutgoingAggreGateCommand prepareCommand(String commandCode, String id, String messageCode,
      String operationCode, String target, DataTable parameters)
  {
    String encodedParameters = parameters.encode(false);
    OutgoingAggreGateCommand outgoing = new OutgoingAggreGateCommand();
    outgoing.addParam(commandCode);
    outgoing.addParam(id);
    outgoing.addParam(messageCode);
    outgoing.addParam(operationCode);
    outgoing.addParam("");
    outgoing.addParam(target);
    outgoing.addParam(encodedParameters);
    outgoing.complete();
    return outgoing;
  }
  
  private DataTable prepareLoginData()
  {
    final DataRecord loginInput = new DataRecord(CommonServerFormats.FIFT_LOGIN);
    loginInput.setValue(RootContextConstants.FIF_LOGIN_USERNAME, "SomeUserName");
    loginInput.setValue(RootContextConstants.FIF_LOGIN_PASSWORD, "SecurePassword123$");
    loginInput.setValue(RootContextConstants.FIF_LOGIN_CODE, null);
    loginInput.setValue(RootContextConstants.FIF_LOGIN_STATE, null);
    loginInput.setValue(RootContextConstants.FIF_LOGIN_PROVIDER, null);
    loginInput.setValue(RootContextConstants.FIF_LOGIN_COUNT_ATTEMPTS, null);
    return loginInput.wrap();
  }
  
  private DataTable prepareSampleData()
  {
    DataTable table = new SimpleDataTable(VFT_TEST);
    
    table.addRecord("xxx", 12345L, 0.5f);
    
    table.addRecord("yyy", 12345L, 0.5f);
    return table;
  }
}
