package com.tibbo.aggregate.common.widget;

import com.tibbo.aggregate.common.util.*;

/**
 * Widget component constraints interface. It signifies location of child component in container component. Container can have different layout. Therefore realizations of this interface also can be
 * different.
 *
 * @see WContainer
 */
public interface WConstraints extends Cloneable, PublicCloneable
{
  WConstraints clone();

  boolean isAbsolute();
}
