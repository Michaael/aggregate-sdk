package com.tibbo.aggregate.common.binding;

import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import com.tibbo.aggregate.common.expression.Reference;
import com.tibbo.aggregate.common.structure.Pinpoint;

/**
 * BindingProvider interacts with {@link BindingProcessor} by providing available bindings and then reacting to their executions.
 */
public interface BindingProvider<T>
{
  /**
   * Returns bindings provided by the Provider.
   * 
   * @return Map of bindings and their {@link EvaluationOptions}
   */
  Map<Binding, EvaluationOptions> createBindings();
  
  /**
   * Method called by {@link BindingProcessor} upon its startup. Should implement initialization logic.
   */
  void start();
  
  /**
   * Method called by {@link BindingProcessor} upon its termination. Should implement cleanup logic.
   */
  void stop();
  
  /**
   * Returns list of references of a particular binding.
   * 
   * @return List of references found in a binding.
   */
  List<Reference> getReferences(Binding binding) throws BindingException;
  
  /**
   * Method is called once a binding was executed, its expression evaluated to some result and the provider needs to
   * write the result into binding target.
   *
   * @param method
   *     Binding execution method: EvaluationOptions.STARTUP, EvaluationOptions.EVENT or EvaluationOptions.PERIODIC
   * @param binding
   *     The {@link Binding} itself
   * @param cause
   *     Execution cause
   * @param value
   *     Value to write to the target
   * @param cache
   *     Change cache
   * @param pinpoint
   *     Origin of the writing
   * @throws BindingException
   *     if writing was not successful
   */
  void writeReference(int method, Binding binding, Reference cause, Object value, @Nullable ChangeCache cache,
      @Nullable Pinpoint pinpoint) throws BindingException;
  
  /**
   * Method called by the binding processor once the provider should start listening for certain {@link Reference} changes.
   * 
   * @param ref
   *          Reference to listen
   * @param listener
   *          Listener that should be called if the reference was changed
   * @throws BindingException
   *           If a listening could not be started
   */
  void addReferenceListener(Reference ref, ReferenceListener<T> listener) throws BindingException;
  
  /**
   * Method called by the binding processor once the provider should no longer listen for changes of a {@link Reference}.
   * 
   * @param listener
   *          Listener to remove
   */
  void removeReferenceListener(ReferenceListener<T> listener);
  
  /**
   * Method used to handle execution of a binding.
   */
  void processExecution(int event, Binding binding, EvaluationOptions options, Reference cause, Object result);
  
  /**
   * Method used to handle binding processing error.
   */
  void processError(Binding binding, int method, Reference cause, Exception error);
}
