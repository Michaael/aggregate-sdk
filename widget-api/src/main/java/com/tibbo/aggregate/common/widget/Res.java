package com.tibbo.aggregate.common.widget;

import java.util.*;

import com.tibbo.aggregate.common.resource.*;
import com.tibbo.aggregate.common.util.*;

public class Res
{
  private static ResourceBundle BUNDLE = ResourceAccessor.fetch(Res.class, ResourceManager.getLocale());
  
  public static ResourceBundle get()
  {
    return BUNDLE;
  }
  
  public void reinit(Locale locale)
  {
    BUNDLE = ResourceAccessor.fetch(getClass(), locale);
  }
}