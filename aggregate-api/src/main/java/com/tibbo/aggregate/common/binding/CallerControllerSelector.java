package com.tibbo.aggregate.common.binding;

import com.tibbo.aggregate.common.context.*;

public interface CallerControllerSelector
{
  CallerController select(Context context, String entity, int entityType);
}
