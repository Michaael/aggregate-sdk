package com.tibbo.aggregate.client.guibuilder.layout;

import java.awt.*;

import com.tibbo.aggregate.common.context.*;
import com.tibbo.aggregate.common.widget.*;
import com.tibbo.aggregate.common.widget.component.*;
import com.tibbo.aggregate.common.widget.context.*;

public interface BasicLayoutHelper
{
  public void changeContainerDefaultSize(WComponent component);
  
  public void resizeComponentInParent(WComponentContext context, Rectangle rectangle) throws ContextException;
  
  public void setZOrderForChildComponent(WComponentContext parCtx, WComponent childComponent, WConstraints newCs);
}
