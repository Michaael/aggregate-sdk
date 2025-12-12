package com.tibbo.aggregate.common.util;

import java.util.*;

import com.tibbo.aggregate.common.tests.*;

public class TestChangeProcessor extends CommonsTestCase
{
  public void testGauge()
  {
    ChangeProcessor cp = new ChangeProcessor(ChangeProcessor.GAUGE, ChangeProcessor.OUT_OF_RANGE_IGNORE, 0.0, 1.0);
    
    GregorianCalendar gc = new GregorianCalendar();
    
    gc.set(2000, 1, 1, 12, 0, 0);
    Date d1 = gc.getTime();
    
    gc.set(2000, 1, 1, 12, 0, 30);
    Date d2 = gc.getTime();
    
    gc.set(2000, 1, 1, 12, 1, 0);
    Date d3 = gc.getTime();
    
    assertEquals(0.0, cp.process(d1.getTime(), 0.0), 0.000001);
    
    assertEquals(-123456789.1234556789, cp.process(d2.getTime(), -123456789.1234556789), 0.000001);
    
    assertEquals(1E12, cp.process(d3.getTime(), 1E12), 0.000001);
    
  }
  
  public void testCounter()
  {
    ChangeProcessor cp = new ChangeProcessor(ChangeProcessor.COUNTER, ChangeProcessor.OUT_OF_RANGE_IGNORE, 0.0, 1.0);
    
    GregorianCalendar gc = new GregorianCalendar();
    
    gc.set(2000, 1, 1, 12, 0, 0);
    Date d1 = gc.getTime();
    
    gc.set(2000, 1, 1, 12, 0, 30);
    Date d2 = gc.getTime();
    
    gc.set(2000, 1, 1, 12, 1, 0);
    Date d3 = gc.getTime();
    
    gc.set(2000, 1, 1, 12, 11, 00);
    Date d4 = gc.getTime();
    
    gc.set(2000, 1, 1, 12, 12, 00);
    Date d5 = gc.getTime();
    
    assertNull(cp.process(d1.getTime(), 0.0));
    
    assertEquals(2000000.0, cp.process(d2.getTime(), 60000000.0), 0.000001);
    
    assertEquals((ChangeProcessor.MAX_32_BIT - 60000000.0 + 10000000.0) / 30, cp.process(d3.getTime(), 10000000.0), 0.000001);
    
    assertEquals(4000000000.0 / 600, cp.process(d4.getTime(), 4010000000.0), 0.000001);
    
    assertEquals((ChangeProcessor.MAX_32_BIT - 4010000000.0 + 1000000000.0) / 60, cp.process(d5.getTime(), 1000000000.0), 0.000001);
  }
  
  public void testDerive()
  {
    ChangeProcessor cp = new ChangeProcessor(ChangeProcessor.DERIVE, ChangeProcessor.OUT_OF_RANGE_IGNORE, 0.0, 1.0);
    
    GregorianCalendar gc = new GregorianCalendar();
    
    gc.set(2000, 1, 1, 12, 0, 0);
    Date d1 = gc.getTime();
    
    gc.set(2000, 1, 1, 12, 0, 30);
    Date d2 = gc.getTime();
    
    gc.set(2000, 1, 1, 12, 1, 0);
    Date d3 = gc.getTime();
    
    assertNull(cp.process(d1.getTime(), 0.0));
    
    assertEquals(2000000.0, cp.process(d2.getTime(), 60000000.0), 0.000001);
    
    assertEquals(-1000000.0, cp.process(d3.getTime(), 30000000.0), 0.000001);
  }
  
  public void testAbsolute()
  {
    ChangeProcessor cp = new ChangeProcessor(ChangeProcessor.ABSOLUTE, ChangeProcessor.OUT_OF_RANGE_IGNORE, 0.0, 1.0);
    
    GregorianCalendar gc = new GregorianCalendar();
    
    gc.set(2000, 1, 1, 12, 0, 0);
    Date d1 = gc.getTime();
    
    gc.set(2000, 1, 1, 12, 0, 30);
    Date d2 = gc.getTime();
    
    gc.set(2000, 1, 1, 12, 1, 0);
    Date d3 = gc.getTime();
    
    assertEquals(0.0, cp.process(d1.getTime(), 0.0), 0.000001);
    
    assertEquals(2000000.0, cp.process(d2.getTime(), 60000000.0), 0.000001);
    
    assertEquals(1000000.0, cp.process(d3.getTime(), 30000000.0), 0.000001);
  }
  
  public void testOutOfBoundValueProcessingNormalize()
  {
    ChangeProcessor cp = new ChangeProcessor(ChangeProcessor.DERIVE, ChangeProcessor.OUT_OF_RANGE_NORMALIZE, -3.0, 10.0);
    
    GregorianCalendar gc = new GregorianCalendar();
    
    gc.set(2000, 1, 1, 12, 0, 0);
    Date d1 = gc.getTime();
    
    gc.set(2000, 1, 1, 12, 0, 30);
    Date d2 = gc.getTime();
    
    gc.set(2000, 1, 1, 12, 1, 0);
    Date d3 = gc.getTime();
    
    gc.set(2000, 1, 1, 12, 1, 30);
    Date d4 = gc.getTime();
    
    gc.set(2000, 1, 1, 12, 2, 0);
    Date d5 = gc.getTime();
    
    gc.set(2000, 1, 1, 12, 2, 30);
    Date d6 = gc.getTime();
    
    assertNull(cp.process(d1.getTime(), 0.0));
    
    assertEquals(1.0, cp.process(d2.getTime(), 30.0), 0.000001);
    
    assertEquals(10.0, cp.process(d3.getTime(), 330.0), 0.000001);
    
    assertEquals(10.0, cp.process(d4.getTime(), 930.0), 0.000001);
    
    assertEquals(-3.0, cp.process(d5.getTime(), 330.0), 0.000001);
    
    assertEquals(-1.0, cp.process(d6.getTime(), 300.0), 0.000001);
  }
  
  public void testOutOfBoundValueProcessingDiscard()
  {
    ChangeProcessor cp = new ChangeProcessor(ChangeProcessor.DERIVE, ChangeProcessor.OUT_OF_RANGE_DISCARD, -3.0, 10.0);
    
    GregorianCalendar gc = new GregorianCalendar();
    
    gc.set(2000, 1, 1, 12, 0, 0);
    Date d1 = gc.getTime();
    
    gc.set(2000, 1, 1, 12, 0, 30);
    Date d2 = gc.getTime();
    
    gc.set(2000, 1, 1, 12, 1, 0);
    Date d3 = gc.getTime();
    
    gc.set(2000, 1, 1, 12, 1, 30);
    Date d4 = gc.getTime();
    
    gc.set(2000, 1, 1, 12, 2, 0);
    Date d5 = gc.getTime();
    
    gc.set(2000, 1, 1, 12, 2, 30);
    Date d6 = gc.getTime();
    
    assertNull(cp.process(d1.getTime(), 0.0));
    
    assertEquals(1.0, cp.process(d2.getTime(), 30.0), 0.000001);
    
    assertEquals(10.0, cp.process(d3.getTime(), 330.0), 0.000001);
    
    assertNull(cp.process(d4.getTime(), 930.0));
    
    assertNull(cp.process(d5.getTime(), 330.0));
    
    assertEquals(-1.0, cp.process(d6.getTime(), 300.0), 0.000001);
  }
  
  public void testNullProcessing()
  {
    ChangeProcessor cp = new ChangeProcessor(ChangeProcessor.COUNTER, ChangeProcessor.OUT_OF_RANGE_DISCARD, -3.0, 10.0);
    
    GregorianCalendar gc = new GregorianCalendar();
    
    gc.set(2000, 1, 1, 12, 0, 0);
    Date d1 = gc.getTime();
    
    gc.set(2000, 1, 1, 12, 1, 0);
    Date d2 = gc.getTime();
    
    gc.set(2000, 1, 1, 12, 2, 0);
    Date d3 = gc.getTime();
    
    gc.set(2000, 1, 1, 12, 3, 0);
    Date d4 = gc.getTime();
    
    gc.set(2000, 1, 1, 12, 4, 0);
    Date d5 = gc.getTime();
    
    gc.set(2000, 1, 1, 12, 5, 0);
    Date d6 = gc.getTime();
    
    assertNull(cp.process(d1.getTime(), null));
    
    assertNull(cp.process(d2.getTime(), 0.0));
    
    assertEquals(1.0, cp.process(d3.getTime(), 60.0), 0.000001);
    
    assertNull(cp.process(d4.getTime(), null));
    
    assertEquals(0.5, cp.process(d5.getTime(), 120.0), 0.000001);
    
    assertEquals(1.0, cp.process(d6.getTime(), 180.0), 0.000001);
  }
}
