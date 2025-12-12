package com.tibbo.aggregate.common.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import com.tibbo.aggregate.common.datatable.DataTable;
import com.tibbo.aggregate.common.datatable.DataRecord;

import org.junit.jupiter.api.Test;

/**
 * Тесты для класса ThreadUtils.
 * 
 * @author AggreGate SDK
 * @version 1.3.5
 */
public class TestThreadUtils
{
  @Test
  public void testCreateStackTraceTable() throws Exception
  {
    // Создаем исключение для получения stack trace
    Exception testException = new Exception("Test exception");
    testException.fillInStackTrace();
    
    StackTraceElement[] elements = testException.getStackTrace();
    assertNotNull(elements);
    assertTrue(elements.length > 0);
    
    // Создаем таблицу stack trace
    DataTable stackTable = ThreadUtils.createStackTraceTable(elements);
    
    assertNotNull(stackTable);
    assertEquals(elements.length, stackTable.getRecordCount());
    
    // Проверяем, что все элементы stack trace присутствуют
    for (int i = 0; i < elements.length; i++)
    {
      StackTraceElement element = elements[i];
      DataRecord rec = stackTable.getRecord(i);
      
      assertNotNull(rec);
      assertEquals(element.getClassName(), rec.rec().getString("class"));
      assertEquals(element.getMethodName(), rec.rec().getString("method"));
      
      // fileName и lineNumber могут быть null
      if (element.getFileName() != null)
      {
        assertEquals(element.getFileName(), rec.rec().getString("file"));
      }
      if (element.getLineNumber() > 0)
      {
        assertEquals(element.getLineNumber(), rec.rec().getInt("line"));
      }
    }
  }
  
  @Test
  public void testCreateStackTraceTableWithEmptyArray() throws Exception
  {
    StackTraceElement[] emptyElements = new StackTraceElement[0];
    
    DataTable stackTable = ThreadUtils.createStackTraceTable(emptyElements);
    
    assertNotNull(stackTable);
    assertEquals(0, stackTable.getRecordCount());
  }
  
  @Test
  public void testCreateStackTraceTableWithNull() throws Exception
  {
    try
    {
      ThreadUtils.createStackTraceTable(null);
      fail("Expected NullPointerException");
    }
    catch (NullPointerException e)
    {
      // Expected
    }
  }
  
  @Test
  public void testStackTraceTableFormat() throws Exception
  {
    // Проверяем формат таблицы
    assertNotNull(ThreadUtils.FORMAT_STACK);
    assertTrue(ThreadUtils.FORMAT_STACK.getFieldCount() >= 4);
    
    // Проверяем наличие полей
    boolean hasClass = false;
    boolean hasMethod = false;
    boolean hasFile = false;
    boolean hasLine = false;
    
    for (int i = 0; i < ThreadUtils.FORMAT_STACK.getFieldCount(); i++)
    {
      String fieldName = ThreadUtils.FORMAT_STACK.getField(i).getName();
      if ("class".equals(fieldName))
      {
        hasClass = true;
      }
      else if ("method".equals(fieldName))
      {
        hasMethod = true;
      }
      else if ("file".equals(fieldName))
      {
        hasFile = true;
      }
      else if ("line".equals(fieldName))
      {
        hasLine = true;
      }
    }
    
    assertTrue(hasClass);
    assertTrue(hasMethod);
    assertTrue(hasFile);
    assertTrue(hasLine);
  }
}

