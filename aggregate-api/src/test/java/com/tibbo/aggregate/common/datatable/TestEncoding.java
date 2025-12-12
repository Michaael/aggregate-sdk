package com.tibbo.aggregate.common.datatable;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import com.tibbo.aggregate.common.action.command.EditProperties;
import com.tibbo.aggregate.common.context.ContextException;
import com.tibbo.aggregate.common.data.Data;
import com.tibbo.aggregate.common.datatable.encoding.ClassicEncodingSettings;
import com.tibbo.aggregate.common.datatable.encoding.KnownFormatCollector;
import com.tibbo.aggregate.common.datatable.encoding.LocalFormatCache;
import com.tibbo.aggregate.common.datatable.encoding.TransferEncodingHelper;
import com.tibbo.aggregate.common.protocol.AggreGateCommand;
import com.tibbo.aggregate.common.protocol.ProtocolCommandBuilder;
import com.tibbo.aggregate.common.tests.CommonsTestCase;

public class TestEncoding extends CommonsTestCase
{

  public void testUtfEncoding() throws ContextException
  {
    String s = "\uFFFF\u0000\u0123";
    
    DataTable st = new SimpleDataTable(new TableFormat(1, 1, FieldFormat.create("f", FieldFormat.STRING_FIELD)), s);
    
    String enc = st.encode();
    
    DataTable dt = new SimpleDataTable(enc);
    
    String d = dt.rec().getString("f");
    
    assertEquals(s, d);
  }
  
  public void testSpecialCharacterEncoding() throws ContextException
  {
    String s = String.valueOf(DataTableUtils.ELEMENT_START) + DataTableUtils.ELEMENT_END + DataTableUtils.ELEMENT_NAME_VALUE_SEPARATOR;
    
    s += String.valueOf(TransferEncodingHelper.ESCAPE_CHAR) + TransferEncodingHelper.SEPARATOR_CHAR;
    
    s += ProtocolCommandBuilder.CLIENT_COMMAND_SEPARATOR;
    
    s += String.valueOf((char) AggreGateCommand.START_CHAR) + (char) AggreGateCommand.END_CHAR;
    
    DataTable st = new SimpleDataTable(new TableFormat(1, 1, FieldFormat.create("f", FieldFormat.STRING_FIELD)), s);
    
    String enc = st.encode();
    
    DataTable dt = new SimpleDataTable(enc);
    
    String d = dt.rec().getString("f");
    
    assertEquals(s, d);
  }
  
  public void testNestedTableEncoding() throws Exception
  {
    String strdata = "test % %% %%% test";
    
    TableFormat tf = new TableFormat();
    
    FieldFormat ff = FieldFormat.create("strfield", FieldFormat.STRING_FIELD);
    
    ff.setDefault(strdata);
    
    tf.addField(ff);
    
    DataTable table = new SimpleDataTable(tf, strdata + "value");
    
    DataTable wrapped = table;
    
    for (int i = 0; i < 2; i++)
    {
      TableFormat wtf = new TableFormat();
      
      FieldFormat wff = FieldFormat.create("dtfield", FieldFormat.DATATABLE_FIELD);
      
      wff.setDefault(wrapped);
      
      wtf.addField(wff);
      
      wrapped = new SimpleDataTable(wtf, wrapped);
    }
    
    String encoded = wrapped.encode();
    
    DataTable restored = new SimpleDataTable(encoded);
    
    assertEquals(wrapped, restored);
  }
  
  public void testHardNestedTableEncoding() throws Exception
  {
    String strdata = "test % %% %%% test";
    
    TableFormat tf = FieldFormat.create("STRING_FIELD", FieldFormat.STRING_FIELD).setDefault("test % %% %%% test").wrap();
    
    tf.addField(FieldFormat.create("BOOLEAN_FIELD", FieldFormat.BOOLEAN_FIELD).setDefault(false));
    Data dataF = new Data();
    dataF.setData("test data % %% %%%".getBytes());
    tf.addField(FieldFormat.create("DATA_FIELD", FieldFormat.DATA_FIELD).setDefault(dataF));
    tf.addField(FieldFormat.create("DATE_FIELD", FieldFormat.DATE_FIELD).setDefault(new Date()));
    
    DataTable wrapped = new SimpleDataTable(tf);
    
    for (int i = 0; i < 1; i++)
    {
      TableFormat wtf = new TableFormat();
      
      FieldFormat wff = FieldFormat.create("dtfield", FieldFormat.DATATABLE_FIELD);
      
      wff.setDefault(wrapped);
      
      wtf.addField(wff);
      
      wrapped = new SimpleDataTable(wtf, wrapped);
    }
    
    String encoded = wrapped.encode();
    
    DataTable restored = new SimpleDataTable(encoded);
    
    assertEquals(wrapped, restored);
  }
  
  public void testTest() throws Exception
  {
    TableFormat tf = new TableFormat("<<int><I>><<string><S>>", new ClassicEncodingSettings(true));
    DataTable table = new DataRecord(tf).wrap();
    
    String encode = table.encode(true);
    assertEquals("<F=<<int><I><A=0>><<string><S><A=>>><R=<0><>>", encode);
  }
  
  public void testInnerTable() throws Exception
  {
    EditProperties editData = new EditProperties("t", "c", Arrays.asList("p1", "p2"));
    
    DataTable expectedTable = editData.getParameters();
    
    ClassicEncodingSettings settings = new ClassicEncodingSettings(true);
    String encode = expectedTable.encode(settings);
    DataTable decodedTable = new SimpleDataTable(encode, settings, false);
    
    assertEquals(expectedTable.toString(), decodedTable.toString());
  }
  
  public void testInnerDynamicFormatTable() throws Exception
  {
    TableFormat format = new TableFormat(1, 1, FieldFormat.create("table", 'T'));
    DataRecord dataRecord = new DataRecord(format);
    dataRecord.setValue(0, new DataRecord(new TableFormat(FieldFormat.create("int", 'I', "Int", 777))).wrap());
    DataTable table = dataRecord.wrap();
    
    ClassicEncodingSettings settings = new ClassicEncodingSettings(true);
    settings.setEncodeFormat(false);
    String encodedTable = table.encode(settings);
    
    assertEquals("<R=<<F=<<int><I><A=777><D=Int>>><R=<777>>>>", encodedTable);
  }
  
  public void testFirstEncoding() throws ContextException
  {
    TableFormat tf = new TableFormat(1, 1);
    tf.addField(FieldFormat.FLOAT_FIELD, "value");
    tf.addField(FieldFormat.INTEGER_FIELD, "quality");
    
    DataTable nested = new SimpleDataTable(tf, 12345.67f, 123);
    
    ClassicEncodingSettings ces = new ClassicEncodingSettings(true);
    ces.setFormatCache(new LocalFormatCache("test"));
    ces.setKnownFormatCollector(new KnownFormatCollector());

    TableFormat tableFormat = createTableFormat();
    DataTable table = new SimpleDataTable(tableFormat, "test", nested, null);
    
    String encodedTable = table.encode(ces);
    String expected = "<F=<<variable><S><A=>><<value><T><A=<F=>>><<user><S><F=N><A=<NULL>>>"
        + "<M=1><X=1>><D=0><R=<test>"
        + "<<F=<<value><F><A=0.0>>"
        + "<<quality><I><A=0>><M=1><X=1>><D=1>"
        + "<R=<12345.67><123>>><<NULL>>>";
    
    assertEquals(expected, encodedTable);
  }

  public void testCachedEncoding() throws ContextException
  {
    TableFormat tf = new TableFormat(1, 1);
    tf.addField(FieldFormat.FLOAT_FIELD, "value");
    tf.addField(FieldFormat.INTEGER_FIELD, "quality");

    DataTable nested = new SimpleDataTable(tf, 12345.67f, 123);

    ClassicEncodingSettings ces = new ClassicEncodingSettings(true);
    ces.setFormatCache(new LocalFormatCache("test"));

    ces.setKnownFormatCollector(new KnownFormatCollector());

    DataTable table = new SimpleDataTable(createTableFormat(), "test", nested, null);

    String firstEncodingResult  = table.encode(ces);
    String secondEncodingResult = table.encode(ces);
    System.out.printf("First  encoding result: %s\nSecond encoding result: %s\n", firstEncodingResult, secondEncodingResult);
    String expected = "<D=0><R=<test><<D=1><R=<12345.67><123>>><<NULL>>>";
    assertEquals(expected, secondEncodingResult);
  }

  public void testTimestampAndQualityEncoding() throws ContextException
  {
    TableFormat tf = new TableFormat(1, 1);
    tf.addField(FieldFormat.FLOAT_FIELD, "value");

    DataTable nested = new SimpleDataTable(tf, 12345.67f);
    Calendar calendar = getCalendar();
    calendar.set(2000, Calendar.FEBRUARY, 1);
    nested.setTimestamp(calendar.getTime());
    nested.setQuality(198);

    ClassicEncodingSettings ces = new ClassicEncodingSettings(true);
    ces.setEncodeFormat(false);

    nested.encode(ces);
    String encodedTable = nested.encode(ces);

    String expected = "<R=<12345.67>><Q=198><T=2000-01-31 21:00:00.000>";
    assertEquals(expected, encodedTable);
  }

  /**
   * @return fresh instance of table format (as opposed to static instance which can keep {@code formatId} inside and
   * thus share an undesired state between tests)
   */
  private TableFormat createTableFormat()
  {
    return new TableFormat(1, 1)
    {
      {
        addField("<variable><S>");
        addField("<value><T>");
        addField("<user><S><F=N>");
      }
    };
  }
}
