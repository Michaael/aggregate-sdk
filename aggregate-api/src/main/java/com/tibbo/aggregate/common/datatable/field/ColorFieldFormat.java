package com.tibbo.aggregate.common.datatable.field;

import java.awt.*;
import java.util.*;
import java.util.List;

import com.tibbo.aggregate.common.*;
import com.tibbo.aggregate.common.datatable.*;
import com.tibbo.aggregate.common.datatable.encoding.*;
import com.tibbo.aggregate.common.util.*;

public class ColorFieldFormat extends FieldFormat<Color>
{
  public static final String EDITOR_BOX = "box";
  
  public ColorFieldFormat(String name)
  {
    super(name);
  }
  
  @Override
  public char getType()
  {
    return FieldFormat.COLOR_FIELD;
  }
  
  @Override
  public Class getFieldClass()
  {
    return Color.class;
  }
  
  @Override
  public Class getFieldWrappedClass()
  {
    return Color.class;
  }
  
  @Override
  public Color getNotNullDefault()
  {
    return Color.BLACK;
  }
  
  @Override
  public Color valueFromString(String value, ClassicEncodingSettings settings, boolean validate)
  {
    if (value.startsWith("#"))
    {
      int red = Integer.parseInt(value.substring(1, 3), 16);
      int green = Integer.parseInt(value.substring(3, 5), 16);
      int blue = Integer.parseInt(value.substring(5, 7), 16);
      int alpha = value.length() >= 9 ? Integer.parseInt(value.substring(7, 9), 16) : 255;
      return new Color(red, green, blue, alpha);
    }
    else
    {
      try
      {
        return new Color(Integer.valueOf(value));
      }
      catch (Exception ex)
      {
        Log.DATATABLEEDITOR.debug(ex.getMessage(), ex);
        return new Color(255);
      }
      
    }
  }
  
  @Override
  public String valueToString(Color value, ClassicEncodingSettings settings)
  {
    return value == null ? null : StringUtils.colorToString(value);
  }
  
  @Override
  public List<String> getSuitableEditors()
  {
    return Arrays.asList(new String[] { EDITOR_LIST, EDITOR_BOX });
  }
}
