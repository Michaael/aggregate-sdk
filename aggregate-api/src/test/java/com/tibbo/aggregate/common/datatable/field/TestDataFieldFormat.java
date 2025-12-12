package com.tibbo.aggregate.common.datatable.field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.tibbo.aggregate.common.data.*;
import com.tibbo.aggregate.common.datatable.*;
import com.tibbo.aggregate.common.datatable.encoding.*;
import com.tibbo.aggregate.common.tests.*;
import com.tibbo.aggregate.common.util.*;

public class TestDataFieldFormat extends CommonsTestCase
{
  public void testValueToAndFromString() throws Exception
  {
    FieldFormat ff = FieldFormat.create("test", 'A');
    
    Data data = new Data();
    
    Data nd = (Data) ff.valueFromString(ff.valueToString(data));
    
    assertNull(nd.getId());
    assertNull(nd.getName());
    assertNull(nd.getPreview());
    assertNull(nd.getData());
    
    data = new Data();
    
    String preview = "/preview//";
    String originalData = "/data";
    
    for (char i = 0; i < 250; i++)
    {
      originalData += new String(new char[] { i });
    }
    
    data.setId((long) 123);
    data.setName("name");
    data.setPreview(preview.getBytes(StringUtils.UTF8_CHARSET));
    data.setData(originalData.getBytes(StringUtils.UTF8_CHARSET));
    
    nd = (Data) ff.valueFromEncodedString(ff.valueToEncodedString(data, new ClassicEncodingSettings(false)), new ClassicEncodingSettings(false), true);
    
    assertTrue((long) 123 == nd.getId());
    assertEquals("name", nd.getName());
    assertEquals(preview, new String(nd.getPreview(), StringUtils.UTF8_CHARSET));
    
    assertEquals(originalData, new String(nd.getData(), StringUtils.UTF8_CHARSET));
  }
  
}
