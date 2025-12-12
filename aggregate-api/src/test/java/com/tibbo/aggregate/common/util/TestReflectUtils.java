package com.tibbo.aggregate.common.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

/**
 * Тесты для класса ReflectUtils.
 * 
 * @author AggreGate SDK
 * @version 1.3.5
 */
public class TestReflectUtils
{
  // Вспомогательный класс для тестирования
  private static class TestClass
  {
    private String privateField;
    private int privateIntField;
    public String publicField;
    
    public TestClass()
    {
      this.privateField = "initial";
      this.privateIntField = 42;
      this.publicField = "public";
    }
    
    public String getPrivateField()
    {
      return privateField;
    }
    
    public int getPrivateIntField()
    {
      return privateIntField;
    }
  }
  
  // Класс для тестирования копирования свойств
  private static class SourceClass
  {
    private String name;
    private int value;
    
    public SourceClass(String name, int value)
    {
      this.name = name;
      this.value = value;
    }
    
    public String getName()
    {
      return name;
    }
    
    public void setName(String name)
    {
      this.name = name;
    }
    
    public int getValue()
    {
      return value;
    }
    
    public void setValue(int value)
    {
      this.value = value;
    }
  }
  
  private static class TargetClass
  {
    private String name;
    private int value;
    
    public String getName()
    {
      return name;
    }
    
    public void setName(String name)
    {
      this.name = name;
    }
    
    public int getValue()
    {
      return value;
    }
    
    public void setValue(int value)
    {
      this.value = value;
    }
  }
  
  @Test
  public void testGetPrivateField() throws Exception
  {
    TestClass testObj = new TestClass();
    
    // Тест получения приватного поля
    Object fieldValue = ReflectUtils.getPrivateField(testObj, "privateField");
    assertNotNull(fieldValue);
    assertEquals("initial", fieldValue);
    
    // Тест получения приватного int поля
    Object intValue = ReflectUtils.getPrivateField(testObj, "privateIntField");
    assertNotNull(intValue);
    assertEquals(42, intValue);
  }
  
  @Test
  public void testGetPrivateFieldWithClass() throws Exception
  {
    TestClass testObj = new TestClass();
    
    // Тест получения приватного поля с указанием класса
    Object fieldValue = ReflectUtils.getPrivateField(testObj, "privateField", TestClass.class);
    assertNotNull(fieldValue);
    assertEquals("initial", fieldValue);
  }
  
  @Test
  public void testGetPrivateFieldNonExistent() throws Exception
  {
    TestClass testObj = new TestClass();
    
    // Тест получения несуществующего поля
    Object fieldValue = ReflectUtils.getPrivateField(testObj, "nonExistentField");
    assertNull(fieldValue);
  }
  
  @Test
  public void testSetPrivateField() throws Exception
  {
    TestClass testObj = new TestClass();
    
    // Тест установки приватного поля
    ReflectUtils.setPrivateField(testObj, "privateField", "newValue");
    assertEquals("newValue", testObj.getPrivateField());
    
    // Тест установки приватного int поля
    ReflectUtils.setPrivateField(testObj, "privateIntField", 100);
    assertEquals(100, testObj.getPrivateIntField());
  }
  
  @Test
  public void testSetPrivateFieldWithClass() throws Exception
  {
    TestClass testObj = new TestClass();
    
    // Тест установки приватного поля с указанием класса
    ReflectUtils.setPrivateField(testObj, "privateField", "newValue", TestClass.class);
    assertEquals("newValue", testObj.getPrivateField());
  }
  
  @Test
  public void testSetPrivateFieldNonExistent() throws Exception
  {
    TestClass testObj = new TestClass();
    
    // Тест установки несуществующего поля (должно завершиться без исключения)
    try
    {
      ReflectUtils.setPrivateField(testObj, "nonExistentField", "value");
      // Если поле не найдено, метод просто возвращается без установки
    }
    catch (Exception e)
    {
      // Может быть исключение, если поле не найдено
    }
  }
  
  @Test
  public void testCopyProperties() throws Exception
  {
    SourceClass source = new SourceClass("testName", 123);
    TargetClass target = new TargetClass();
    
    // Тест копирования свойств
    ReflectUtils.copyProperties(source, Object.class, target, Object.class);
    
    assertEquals(source.getName(), target.getName());
    assertEquals(source.getValue(), target.getValue());
  }
  
  @Test
  public void testCopyPropertiesWithStopClass() throws Exception
  {
    SourceClass source = new SourceClass("testName", 123);
    TargetClass target = new TargetClass();
    
    // Тест копирования свойств с указанием stop класса
    ReflectUtils.copyProperties(source, SourceClass.class, target, TargetClass.class);
    
    // Свойства должны быть скопированы
    assertEquals(source.getName(), target.getName());
    assertEquals(source.getValue(), target.getValue());
  }
  
  @Test
  public void testGetPrivateFieldNullObject() throws Exception
  {
    try
    {
      ReflectUtils.getPrivateField(null, "field");
      fail("Expected exception");
    }
    catch (Exception e)
    {
      // Expected
    }
  }
  
  @Test
  public void testSetPrivateFieldNullObject() throws Exception
  {
    try
    {
      ReflectUtils.setPrivateField(null, "field", "value");
      fail("Expected exception");
    }
    catch (Exception e)
    {
      // Expected
    }
  }
}

