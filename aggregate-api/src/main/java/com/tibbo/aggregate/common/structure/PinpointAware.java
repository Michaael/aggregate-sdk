package com.tibbo.aggregate.common.structure;

import static java.lang.String.format;

import java.util.Optional;

/**
 * The interface to denote objects capable of holding a {@link Pinpoint}. The way of storing the pinpoint itself is
 * implementation specific, so the only thing this interface knows about is that the pinpoint can be {@code null}.
 *
 * @implNote Implementations should ensure that the pinpoint returned by {@link #obtainPinpoint()} is exactly the same
 * one that was last set with {@link #assignPinpoint} (and ideally prevent callers from re-assigning).
 */
public interface PinpointAware
{
  /**
   * Assigns this object with the given pinpoint
   *
   * @implNote If pinpoint is a must-have attribute of the implementation class (i.e. its {@code private final}
   * field), then there should be a constructor parameter of {@link Pinpoint} type while this method should
   * unconditionally throw {@link IllegalStateException} explaining why this call is prohibited in this case.
   * @param pinpoint a pinpoint this object should be associated with
   * @throws IllegalStateException in case pinpoint cannot be assigned after object construction
   */
  void assignPinpoint(Pinpoint pinpoint) throws IllegalStateException;

  @SuppressWarnings("OptionalUsedAsFieldOrParameterType")       // optional is acceptable here due to locality of usage
  default void assignPinpoint(Optional<Pinpoint> pinpointOpt) {
    assignPinpoint(pinpointOpt.orElse(null));
  }

  Optional<Pinpoint> obtainPinpoint();

  default void removePinpoint() {
    throw new UnsupportedOperationException(format("This '%s' does not support pinpoint removal", this));
  }
}
