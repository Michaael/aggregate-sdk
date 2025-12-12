package com.tibbo.aggregate.common.util;

import java.util.*;

import com.tibbo.aggregate.common.action.command.*;
import com.tibbo.aggregate.common.binding.*;
import com.tibbo.aggregate.common.datatable.*;
import com.tibbo.aggregate.common.tests.*;

public class TestCloneUtils extends CommonsTestCase
{
  public void testDataTableClone() throws DataTableException
  {
    TableFormat tf = new TableFormat();
    
    tf.addField(FieldFormat.DATATABLE_FIELD, "nested");
    
    TableFormat nf = new TableFormat(FieldFormat.create("str", FieldFormat.STRING_FIELD));
    
    DataTable nested = new SimpleDataTable(nf, "xxx");
    
    DataTable table = new SimpleDataTable(tf, nested);
    
    DataTable clone = table.clone();
    
    assertNotSame(table, clone);
    
    assertNotSame(table.get(), clone.get());
  }
  
  public void testGenericClone() throws DataTableException
  {
    HashMap<String, String> expect = new HashMap<>();
    expect.put("aaa", "bbb");
    expect.put("ccc", "bbb");
    assertNotSame(expect, CloneUtils.genericClone(expect));
    assertEquals(expect, CloneUtils.genericClone(expect));
    
    TableFormat tf = new TableFormat();
    tf.addField(FieldFormat.DATATABLE_FIELD, "nested");
    
    LaunchWidget lw = new LaunchWidget();
    assertNotSame(lw, CloneUtils.genericClone(lw));
    assertEquals(lw, CloneUtils.genericClone(lw));
  }
  
  public void testDeepCloneOneDimensionArray() throws DataTableException
  {
    int[] ints = new int[] { 1, 2, 3 };
    assertNotSame(ints, CloneUtils.deepClone(ints));
    assertTrue(Arrays.equals(ints, (int[]) (CloneUtils.deepClone(ints))));
  }

  public void testDeepCloneTwoDimensionArray() throws DataTableException
  {
    int[][] ints = new int[][] {{1, 2, 3}, {4, 5, 6}};
    assertNotSame(ints, CloneUtils.deepClone(ints));
    assertTrue(Arrays.deepEquals(ints, (int[][]) (CloneUtils.deepClone(ints))));
  }

  public void testDeepCloneMap() throws DataTableException
  {
    Map<Binding, Binding> bindings = new LinkedHashMap<>();
    Binding b = new Binding("ref","target");
    bindings.put(b,b);
    b = new Binding("ref2","target2");
    bindings.put(b,b);
    assertNotSame(bindings, CloneUtils.deepClone(bindings));
    assertEquals(bindings, CloneUtils.deepClone(bindings));
  }
}
