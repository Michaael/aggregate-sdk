package com.tibbo.aggregate.common.datatable.encoding;

import com.tibbo.aggregate.common.datatable.TableFormat;
import com.tibbo.aggregate.common.tests.CommonsTestCase;

public class TestFormatCache extends CommonsTestCase
{
  private final String format1 = "<<value><I><F=><A=0>><M=1><X=1>";
  private final String format2 = "<<value><I><F=><A=0><D=>><M=1><X=1>";
  
  public void testServerFormatCache() throws Exception
  {
    TableFormat f1 = new TableFormat(format1, new ClassicEncodingSettings(true));
    
    TableFormat f2 = new TableFormat(format2, new ClassicEncodingSettings(true));
  
    FormatCache fc = new LocalFormatCache("test");
    
    int id = fc.addIfNotExists(f1);
    
    assertEquals(0, id);
    
    id = fc.addIfNotExists(f2);
    
    assertEquals(1, id);
    
    TableFormat res = fc.get(0);
    
    assertSame(f1, res);
    
    res = fc.get(1);
    
    assertSame(f2, res);
    
    TableFormat newf1 = new TableFormat(format1, new ClassicEncodingSettings(true));
    
    res = fc.getCachedVersion(newf1);
    
    assertSame(f1, res);
    
    TableFormat newf2 = new TableFormat(format2, new ClassicEncodingSettings(true));
    
    res = fc.getCachedVersion(newf2);
    
    assertSame(f2, res);
    
  }
  
  public void testClientFormatCache() throws Exception
  {
    TableFormat f1 = new TableFormat(format1, new ClassicEncodingSettings(true));
    
    TableFormat f2 = new TableFormat(format2, new ClassicEncodingSettings(true));
  
    AbstractFormatCache fc = new RemoteFormatCache(null,"test");
    
    fc.put(123, f1);
    
    TableFormat res = fc.get(123);
    
    assertSame(f1, res);
    
    int id = fc.obtainId(f1);
    
    assertEquals(123, id);
    
    fc.put(456, f1);
    fc.put(456, f2);
    
    res = fc.get(456);
    
    assertSame(f2, res);
    
  }
  
  public void testStrangerFormatCache()
  {
    TableFormat format = new TableFormat(format1, new ClassicEncodingSettings(true));

    AbstractFormatCache alisaCache = new LocalFormatCache("alisa");
    AbstractFormatCache bobCache = new LocalFormatCache("bob");
    
    format.applyCachedFormat(alisaCache, f -> {});
    
    assertNotNull(alisaCache.obtainId(format));
    
    format.applyCachedFormat(bobCache, f -> fail());
  
    assertNull(bobCache.obtainId(format));
  }
}
