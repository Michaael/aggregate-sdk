package com.tibbo.aggregate.common.tests;

import java.util.Locale;
import java.util.ResourceBundle;

import org.junit.jupiter.api.Test;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.util.ResourceAccessor;
import com.tibbo.aggregate.common.util.TimeHelper;

import junit.framework.TestCase;

public class TestTimeHelper extends TestCase
{
  @Test
  public void testGetForSeconds()
  {
    ResourceBundle bundleRu = ResourceAccessor.fetch(Cres.class, new Locale("ru"));
    ResourceBundle bundleEn = ResourceAccessor.fetch(Cres.class, new Locale("en"));
    
    assertEquals("1 секунду", TimeHelper.getForSeconds(1_000L, bundleRu));
    assertEquals("2 секунды", TimeHelper.getForSeconds(2_000L, bundleRu));
    assertEquals("3 секунды", TimeHelper.getForSeconds(3_000L, bundleRu));
    assertEquals("4 секунды", TimeHelper.getForSeconds(4_000L, bundleRu));
    assertEquals("5 секунд", TimeHelper.getForSeconds(5_000L, bundleRu));
    assertEquals("11 секунд", TimeHelper.getForSeconds(11_000L, bundleRu));
    assertEquals("12 секунд", TimeHelper.getForSeconds(12_000L, bundleRu));
    assertEquals("13 секунд", TimeHelper.getForSeconds(13_000L, bundleRu));
    assertEquals("14 секунд", TimeHelper.getForSeconds(14_000L, bundleRu));
    assertEquals("21 секунду", TimeHelper.getForSeconds(21_000L, bundleRu));
    assertEquals("22 секунды", TimeHelper.getForSeconds(22_000L, bundleRu));
    assertEquals("102 секунды", TimeHelper.getForSeconds(102_000L, bundleRu));
    assertEquals("103 секунды", TimeHelper.getForSeconds(103_000L, bundleRu));
    assertEquals("104 секунды", TimeHelper.getForSeconds(104_000L, bundleRu));
    assertEquals("111 секунд", TimeHelper.getForSeconds(111_000L, bundleRu));
    
    assertEquals("1 second", TimeHelper.getForSeconds(1_000L, bundleEn));
    assertEquals("2 seconds", TimeHelper.getForSeconds(2_000L, bundleEn));
    assertEquals("5 seconds", TimeHelper.getForSeconds(5_000L, bundleEn));
    assertEquals("11 seconds", TimeHelper.getForSeconds(11_000L, bundleEn));
    assertEquals("21 seconds", TimeHelper.getForSeconds(21_000L, bundleEn));
    
  }
}