package com.tibbo.aggregate.common.protocol;

import java.util.concurrent.*;

public interface CommandTask<V> extends Callable<V>
{
}
