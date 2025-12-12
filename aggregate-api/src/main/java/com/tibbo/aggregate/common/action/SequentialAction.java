package com.tibbo.aggregate.common.action;

import com.tibbo.aggregate.common.context.*;
import com.tibbo.aggregate.common.device.*;


public abstract class SequentialAction<I extends InitialRequest, C extends ActionCommand, R extends ActionResponse> implements Action<I, C, R>
{
  /**
   * Override this method to provide action functionality.
   */
  protected abstract ActionResult invoke(I parameters) throws ContextException;
  
  /**
   * Action Implementations should override this method to provide command sending functionality to client.
   * 
   * @exception java.lang.IllegalStateException
   *              If the method is called not in actionRequest processing phase
   * @exception java.lang.NullPointerException
   *              If actionCommand is null
   */
  protected abstract R send(C actionCommand) throws DisconnectionException;
  
  /**
   * Action Implementations should override this method to convert request-response style processing to a continuous instruction flow in execute method.
   * 
   * @exception java.lang.IllegalArgumentException
   *              if actionRequest doesn't match previous actionCommand or first actionRequest received isn't instance of ActionInitParameters
   */
  public abstract C service(R actionRequest) throws IllegalArgumentException;
}
