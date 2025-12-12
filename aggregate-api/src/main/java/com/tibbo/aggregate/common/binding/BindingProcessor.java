package com.tibbo.aggregate.common.binding;

import java.util.Timer;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

/**
 * BindingProcessor is tightly related to {@link BindingProvider}. Its implementation is an engine that executes bindings provider by a BindingProvider.
 */
public interface BindingProcessor
{
  /**
   * Starts the binding provider.
   * @return true if startup was successful, false otherwise
   */
  boolean start();

  /**
   * Stops the binding processor.
   */
  void stop();
  
  /**
   * Checks whether the binding processor is stopped.
   * @return true if stopped, false if running
   */
  boolean isStopped();
  
  /**
   * Enables (resumes) or disables (pauses) a running processor.
   * @param enabled the new state
   */
  void setEnabled(boolean enabled);

  /**
   * Submits a task to processor's thread pool. Can be used to add "external" work for the processor.
   * @param task The task to execute
   */
  void submit(Callable task);

  /**
   * Returns processor's thread pool.
   * @return The thread pool
   */
  ExecutorService getExecutorService();

  /**
   * Returns processor's timer
   * @return The timer
   */
  Timer getTimer();
}
