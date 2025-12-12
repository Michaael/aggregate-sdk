package com.tibbo.aggregate.common.context.loader;

import javax.annotation.concurrent.Immutable;

@Immutable
public class DecoratedContextValidator implements ContextValidator
{
  private final ContextValidator[] validators;
  
  public DecoratedContextValidator(ContextValidator... validators)
  {
    this.validators = validators;
  }
  
  @Override
  public boolean validate(String contextPath)
  {
    for (ContextValidator validator : validators)
    {
      if (!validator.validate(contextPath))
      {
        return false;
      }
    }
    return true;
  }
}
