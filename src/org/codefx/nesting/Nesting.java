package org.codefx.nesting;

import javafx.beans.Observable;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.value.ObservableValue;

/**
 * A nesting encapsulates a hierarchy of nested {@link ObservableValue ObservableValues}; its {@link #innerObservable()}
 * always contains the current innermost {@link Observable} in that hierarchy. If some observable or its value were
 * null, {@code innerObservable} contains null as well.
 * <p>
 * Nestings will usually be implemented such that they eagerly evaluate the nested observables.
 *
 * @param <O>
 *            the hierarchy's innermost type of {@link Observable}
 */
public interface Nesting<O extends Observable> {

	/**
	 * The current innermost observable in the hierarchy. If some observable or its value were null, this contains null
	 * as well.
	 *
	 * @return the innermost {@link ObservableValue}
	 */
	ReadOnlyProperty<O> innerObservable();

}
