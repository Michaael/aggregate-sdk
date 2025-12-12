package com.tibbo.aggregate.common.expression.function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.awt.*;

import com.tibbo.aggregate.common.expression.function.color.*;
import com.tibbo.aggregate.common.tests.*;
import org.junit.jupiter.api.Test;

public class TestColorFunctions extends CommonsTestCase
{
  @Test
  public void testBlueColorFunction() throws Exception
  {
    int blueColor = (int) new BlueFunction().execute(null, null, Color.BLUE);
    
    assertEquals(Color.BLUE.getBlue(), blueColor);
  }
  
  @Test
  public void testGreenColorFunction() throws Exception
  {
    int greenColor = (int) new GreenFunction().execute(null, null, Color.GREEN);
    
    assertEquals(Color.GREEN.getGreen(), greenColor);
  }
  
  @Test
  public void testReadColorFunction() throws Exception
  {
    int redColor = (int) new RedFunction().execute(null, null, Color.RED);
    
    assertEquals(Color.RED.getRed(), redColor);
  }
  
  @Test
  public void testColorFunction() throws Exception
  {
    int r = Color.ORANGE.getRed();
    int g = Color.YELLOW.getGreen();
    int b = Color.MAGENTA.getBlue();
    
    Color color = (Color) new ColorFunction().execute(null, null, r, g, b);
    
    assertNotNull(color);
    assertEquals(color.getRed(), Color.ORANGE.getRed());
    assertEquals(color.getGreen(), Color.YELLOW.getGreen());
    assertEquals(color.getBlue(), Color.MAGENTA.getBlue());
  }
  {
    int r = Color.ORANGE.getRed();
    int g = Color.YELLOW.getGreen();
    int b = Color.MAGENTA.getBlue();
    
    Color color = (Color) new ColorFunction().execute(null, null, r, g, b);
    
    assertNotNull(color);
    assertEquals(color.getRed(), Color.ORANGE.getRed());
    assertEquals(color.getGreen(), Color.YELLOW.getGreen());
    assertEquals(color.getBlue(), Color.MAGENTA.getBlue());
  }
}
