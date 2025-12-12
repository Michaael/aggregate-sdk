package com.tibbo.aggregate.common.datatable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.tibbo.aggregate.common.context.*;
import com.tibbo.aggregate.common.datatable.encoding.*;
import com.tibbo.aggregate.common.datatable.validator.*;
import com.tibbo.aggregate.common.tests.*;
import org.junit.jupiter.api.Test;

public class TestFieldFormat extends CommonsTestCase
{
  @Test
  public void testFieldFormat() throws ContextException
  {
    FieldFormat ff1 = FieldFormat.create("<s1><S><F=N><D=Test>");
    FieldFormat ff2 = FieldFormat.create("<s1><S><D=Test>");
    assertTrue(ff1.extend(ff2), ff1.extendMessage(ff2));
  }
  
  @Test
  public void testDefaultValue() throws ContextException
  {
    FieldFormat ff1 = FieldFormat.create("<s1><F><A=123.456>");
    assertTrue(Math.abs(123.456f - (Float) ff1.getDefaultValue()) < 0.0000000000001f);
  }
  
  @Test
  public void testClone() throws ContextException
  {
    String format = "<s1><S><F=N><A=default><D=Test><S=<desc=default><desc2=val2>><V=<L=1 10>>";
    FieldFormat ff = FieldFormat.create(format);
    FieldFormat cl = ff.clone();
    assertEquals(format, cl.encode(new ClassicEncodingSettings(true)));
  }
  
  @Test
  public void testDefaultDescription() throws ContextException
  {
    FieldFormat ff = FieldFormat.create("<theBigValue><S>");
    
    assertEquals("The Big Value", ff.getDescription());
  }
  
  @Test
  public void testFloatStorage() throws ContextException
  {
    FieldFormat ff = FieldFormat.create("<s1><F>");
    float f = 12345678901234567890.12345678901234567890F;
    
    assertEquals(f, ff.valueFromString(ff.valueToString(f)));
  }
  
  @Test
  public void testDoubleStorage() throws ContextException
  {
    FieldFormat ff = FieldFormat.create("<s1><E>");
    double d = 12345678901234567890.12345678901234567890D;
    
    assertEquals(d, ff.valueFromString(ff.valueToString(d)));
    
  }
  

  @Test
  public void testHashCodesAreEqual()
  {
      FieldFormat ff1 = FieldFormat.create("<value><E><A=0.0>");
      FieldFormat ff2 = FieldFormat.create("<value><E><A=0.0>");

      assertEquals(ff1.getDefaultValue().hashCode(), ff2.getDefaultValue().hashCode());


      assertEquals(ff1.hashCode(), ff2.hashCode());
  }

  
  @Test
  public void testHashCodesDiffer() throws Exception
  {
    final FieldFormat ff1 = FieldFormat.create("<value><E><A=0.0>");
    
    final FieldFormat ff2 = FieldFormat.create("<value><I><A=0>");
    
    assertFalse(ff1.hashCode() == ff2.hashCode());
  }
  
  @Test
  public void testEquals() throws Exception
  {
    FieldFormat<Object> ff1 = FieldFormat.create("<value><I><A=0>");
    ff1.addValidator(new LimitsValidator(5, 10));
    
    final TableFormat tf1 = ff1.wrap();
    tf1.addRecordValidator(new KeyFieldsValidator());
    tf1.addTableValidator(new TableKeyFieldsValidator());
    
    FieldFormat<Object> ff2 = FieldFormat.create("<value><I><A=0>");
    ff2.addValidator(new LimitsValidator(5, 10));
    
    final TableFormat tf2 = ff2.wrap();
    tf2.addRecordValidator(new KeyFieldsValidator());
    tf2.addTableValidator(new TableKeyFieldsValidator());
    
    assertEquals(tf1, tf2);
    assertEquals(tf1.hashCode(), tf2.hashCode());
  }
}
