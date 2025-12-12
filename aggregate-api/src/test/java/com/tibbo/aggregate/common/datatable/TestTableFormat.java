package com.tibbo.aggregate.common.datatable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.tibbo.aggregate.common.context.*;
import com.tibbo.aggregate.common.datatable.encoding.*;
import com.tibbo.aggregate.common.datatable.validator.*;
import com.tibbo.aggregate.common.tests.*;
import com.tibbo.aggregate.common.util.*;
import org.junit.jupiter.api.Test;

public class TestTableFormat extends CommonsTestCase
{
  private TableFormat createFormat(String format)
  {
    return new TableFormat(format, new ClassicEncodingSettings(true));
  }
  
  @Test
  public void testTableFormat() throws ContextException
  {
    TableFormat rf1 = createFormat("<<s1><S>> <<s2><S><i1><I>> <<i2><I>>");
    TableFormat rf2 = createFormat("<<s1><S>> <<s2><S><i1><I>> <<i2><I>>");
    assertTrue(rf1.extend(rf2));
    
    rf1 = createFormat("<<s1><S>> <<l1><L>> <<s2><S>> <<i1><I>> <<i2><I>>");
    rf2 = createFormat("<<s1><S>> <<s2><S>> <<i1><I>> <<i2><I>> <<b1><B><F=O>>");
    assertTrue(rf1.extend(rf2));
    
    rf1 = createFormat("<<s1><S>> <<l1><L>> <<s2><S>> <<i1><I>> <<i2><I>>");
    rf2 = createFormat("<<s1><S>> <<s2><S>> <<i1><I><F=N>> <<i2><I>> <<b1><B><F=O>>");
    assertFalse(rf1.extend(rf2));
    
    rf1 = createFormat("<<s1><S>> <<l1><L>> <<s2><S><i1><I>> <<i2><I>>");
    rf2 = createFormat("<<s1><S>> <<s2><S>> <<i2><I>> <<b1><B><F=O>> <<b2><B>>");
    assertFalse(rf1.extend(rf2));
    
    rf1 = createFormat("<<one><S>>");
    rf2 = createFormat("<<one><I>>");
    assertFalse(rf1.extend(rf2));
    
    rf1 = createFormat("<<0><S><A=test><D=test>>");
    
    assertFalse(rf1.getField(0).isNullable());
    
    assertEquals("test", rf1.getField(0).getDefaultValue());
  }
  
  @Test
  public void testEmptyFieldLookup() throws Exception
  {
    final TableFormat format = createFormat("<<one><S>>");
    // Set 'fieldLookup' to 'null' as it happens when import legacy properties
    ReflectUtils.setPrivateField(format, "fieldLookup", null);
    assertEquals(format.getFieldIndex("one"), 0);
  }
  
  @Test
  public void testEqualsHashCode() throws Exception
  {
    TableFormat tf1 = createTestTableFormat();
    TableFormat tf2 = createTestTableFormat();
    assertEquals(tf1, tf2);
    assertEquals(tf1.hashCode(), tf2.hashCode());
    
  }
  
  protected TableFormat createTestTableFormat()
  {
    TableFormat format = new TableFormat(1, 1);
    format.addField(FieldFormat.create("name", FieldFormat.STRING_FIELD, "name", "default name").addValidator(ValidatorHelper.NAME_SYNTAX_VALIDATOR));
    format.addField(FieldFormat.create("dt", FieldFormat.DATATABLE_FIELD, "dt", new SimpleDataTable()));
    format.addField(FieldFormat.create("float", FieldFormat.FLOAT_FIELD, "float", 1.5f));
    format.setReorderable(true);
    return format;
  }
  
}
