package com.tibbo.aggregate.common.datatable;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Formatter;

import org.junit.jupiter.api.Test;

import com.tibbo.aggregate.common.datatable.encoding.ClassicEncodingSettings;
import com.tibbo.aggregate.common.datatable.encoding.KnownFormatCollector;
import com.tibbo.aggregate.common.datatable.encoding.LocalFormatCache;

public class DataTableEncodingTests
{

  @Test
  public void testEncoding() throws Exception
  {
    TableFormat tf = new TableFormat(1, 1);
    tf.addField(FieldFormat.FLOAT_FIELD, "value");
    tf.addField(FieldFormat.INTEGER_FIELD, "quality");
    
    DataTable nested = new SimpleDataTable(tf, 12345.67f, 123);
    
    // ClassicEncodingSettings ces = new ClassicEncodingSettings(true);
    ClassicEncodingSettings ces = new ClassicEncodingSettings(false);
    
    DataTable table = new SimpleDataTable(createTableFormat(), "test", nested, null);
    
    String encodedTable = table.encode(ces);
    
    // String expected = "<F=<<variable><S><A=>><<value><T><A=<F=>>><<user><S><F=N><A=<NULL>>>"
    // + "<M=1><X=1>><R=<test>"
    // + "<<F=<<value><F><A=0.0>>"
    // + "<<quality><I><A=0>><M=1><X=1>>"
    // + "<R=<12345.67><123>>><<NULL>>>";
    String expected = "\\u001c\\u0046\\u001e\\u001c\\u001c\\u0076\\u0061\\u0072\\u0069\\u0061\\u0062\\u006c\\u0065\\u001d\\u001c\\u0053\\u001d\\u001c\\u0041\\u001e\\u001d\\u001d\\u001c\\u001c\\u0076\\u0061\\u006c\\u0075\\u0065\\u001d\\u001c\\u0054\\u001d\\u001c\\u0041\\u001e\\u001c\\u0046\\u001e\\u001d\\u001d\\u001d\\u001c\\u001c\\u0075\\u0073\\u0065\\u0072\\u001d\\u001c\\u0053\\u001d\\u001c\\u0046\\u001e\\u004e\\u001d\\u001c\\u0041\\u001e\\u001a\\u001d\\u001d\\u001c\\u004d\\u001e\\u0031\\u001d\\u001c\\u0058\\u001e\\u0031\\u001d\\u001d\\u001c\\u0052\\u001e\\u001c\\u0074\\u0065\\u0073\\u0074\\u001d\\u001c\\u001c\\u0046\\u001e\\u001c\\u001c\\u0076\\u0061\\u006c\\u0075\\u0065\\u001d\\u001c\\u0046\\u001d\\u001c\\u0041\\u001e\\u0030\\u002e\\u0030\\u001d\\u001d\\u001c\\u001c\\u0071\\u0075\\u0061\\u006c\\u0069\\u0074\\u0079\\u001d\\u001c\\u0049\\u001d\\u001c\\u0041\\u001e\\u0030\\u001d\\u001d\\u001c\\u004d\\u001e\\u0031\\u001d\\u001c\\u0058\\u001e\\u0031\\u001d\\u001d\\u001c\\u0052\\u001e\\u001c\\u0031\\u0032\\u0033\\u0034\\u0035\\u002e\\u0036\\u0037\\u001d\\u001c\\u0031\\u0032\\u0033\\u001d\\u001d\\u001d\\u001c\\u001a\\u001d\\u001d";
    
    // DataTableUtils.ELEMENT_START
    assertEquals(encodedTable.charAt(0), (DataTableUtils.ELEMENT_START));
    
    StringBuilder b = new StringBuilder(encodedTable.length());
    Formatter f = new Formatter(b);
    for (char c : encodedTable.toCharArray())
    {
      f.format("\\u%04x", (int) c);
    }
    
    assertEquals(expected, b.toString());
    
    f.close();
  }
  
  @Test
  public void testFirstAndCachedEncoding() throws Exception
  {
    TableFormat tableFormat = createTableFormat();
    testFirstEncoding(tableFormat);
    testCachedEncoding(tableFormat);
  }
  
  private void testFirstEncoding(TableFormat tableFormat) throws Exception
  {
    TableFormat tf = new TableFormat(1, 1);
    tf.addField(FieldFormat.FLOAT_FIELD, "value");
    tf.addField(FieldFormat.INTEGER_FIELD, "quality");
    
    DataTable nested = new SimpleDataTable(tf, 12345.67f, 123);
    
    ClassicEncodingSettings ces = new ClassicEncodingSettings(true);
    ces.setFormatCache(new LocalFormatCache("test"));
    ces.setKnownFormatCollector(new KnownFormatCollector());
    
    DataTable table = new SimpleDataTable(tableFormat, "test", nested, null);
    
    String encodedTable = table.encode(ces);
    
    String expected = "<F=<<variable><S><A=>><<value><T><A=<F=>>><<user><S><F=N><A=<NULL>>>"
        + "<M=1><X=1>><D=0><R=<test>"
        + "<<F=<<value><F><A=0.0>>"
        + "<<quality><I><A=0>><M=1><X=1>><D=1>"
        + "<R=<12345.67><123>>><<NULL>>>";
    
    assertEquals(expected, encodedTable);
  }
  
  private void testCachedEncoding(TableFormat tableFormat) throws Exception
  {
    TableFormat tf = new TableFormat(1, 1);
    tf.addField(FieldFormat.FLOAT_FIELD, "value");
    tf.addField(FieldFormat.INTEGER_FIELD, "quality");
    
    DataTable nested = new SimpleDataTable(tf, 12345.67f, 123);
    
    ClassicEncodingSettings ces = new ClassicEncodingSettings(true);
    ces.setFormatCache(new LocalFormatCache("test"));
    
    ces.setKnownFormatCollector(new KnownFormatCollector());
    
    DataTable table = new SimpleDataTable(tableFormat, "test", nested, null);
    
    table.encode(ces);
    String encodedTable = table.encode(ces);
    String expected = "<D=0><R=<test><<D=0><R=<12345.67><123>>><<NULL>>>";
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