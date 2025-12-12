package com.tibbo.aggregate.common.util;

import java.util.*;

public class ElementList extends ArrayList<Element>
{
  public Element getElement(String name)
  {
    for (Element el : this)
    {
      if (el.getName() != null && el.getName().equals(name))
      {
        return el;
      }
    }

    return null;
  }
}
