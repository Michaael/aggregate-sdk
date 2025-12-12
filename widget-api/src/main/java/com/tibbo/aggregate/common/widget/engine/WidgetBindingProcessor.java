package com.tibbo.aggregate.common.widget.engine;

import java.util.*;
import java.util.concurrent.*;

import com.tibbo.aggregate.common.binding.*;
import com.tibbo.aggregate.common.expression.*;

public class WidgetBindingProcessor extends DefaultBindingProcessor implements TimerFactory
{
  /**
   * Flag that switches off initBindings on start if is true. No any Binding should act during Editing GUI Template.
   */
  private final boolean editorMode;
  
  public WidgetBindingProcessor(BindingProvider provider, Evaluator evaluator, boolean editorMode)
  {
    super(provider, evaluator);
    this.editorMode = editorMode;
  }
  
  public WidgetBindingProcessor(BindingProvider provider, Evaluator evaluator, TimerFactory timerFactory, ExecutorService executionService, boolean editorMode)
  {
    super(provider, evaluator, timerFactory, executionService);
    this.editorMode = editorMode;
  }
  
  @Override
  public boolean start()
  {
    if (editorMode)
    {
      getProvider().start();
      return true;
    }
    else
    {
      return super.start();
    }
  }
  
  @Override
  public Timer createTimer()
  {
    ensureTimer();
    
    return getTimer();
  }
}
