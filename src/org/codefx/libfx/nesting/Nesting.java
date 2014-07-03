package org.codefx.libfx.nesting;

import java.util.Optional;

import javafx.beans.Observable;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.value.ObservableValue;

/**
 * A nesting encapsulates a hierarchy of nested {@link ObservableValue ObservableValues}; its {@link #innerObservable()}
 * always contains the current innermost {@link Observable} in that hierarchy as an {@link Optional}. If some observable
 * or its value were null, {@code innerObservable} contains {@link Optional#empty()}.
 * <p>
 * Nestings will usually be implemented such that they eagerly evaluate the nested observables.
 *
 * @param <O>
 *            the hierarchy's innermost type of {@link Observable}
 */
public interface Nesting<O extends Observable> {

	/**
	 * A property holding the current innermost observable in the hierarchy. If some observable or its value were null,
	 * this contains {@link Optional#empty()}.
	 *
	 * @return the innermost {@link ObservableValue} in an {@link Optional}
	 */
	ReadOnlyProperty<Optional<O>> innerObservable();

}
