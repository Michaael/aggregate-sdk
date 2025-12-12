package com.tibbo.aggregate.common.context.loader;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.tibbo.aggregate.common.context.ContextUtils;

/**
 * Validator which will accept the context if it matches the context mask
 *
 * @author Alexander Sidorov
 * @since 01.04.2023
 * @see <a href="https://tibbotech.atlassian.net/browse/AGG-14058">AGG-14058</a>
 */
@Immutable
public class MaskContextValidator implements ContextValidator
{
  private final String contextMask;
  
  public MaskContextValidator(@Nonnull String contextMask)
  {
    this.contextMask = contextMask;
  }
  
  @Override
  public boolean validate(String contextPath)
  {
    if (contextMask.isEmpty())
    {
      return true;
    }
    return ContextUtils.matchesToMask(contextMask, contextPath, true, false);
  }
}
