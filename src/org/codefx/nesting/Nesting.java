package org.codefx.nesting;

import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.value.ObservableValue;

/**
 * A nesting encapsulates a hierarchy of nested {@link ObservableValue ObservableValues}. Its {@link #innerProperty()}
 * always contains the current innermost observable in that hierarchy. If some observable or its value were null,
 * {@code innerProperty} contains null as well.
 * <p>
 * Nestings will usually be implemented such that they eagerly evaluate the nested observable.
 *
 * @param <O>
 *            the hierarchy's innermost type of {@link ObservableValue}
 */
public interface Nesting<O extends ObservableValue<?>> {

	/**
	 * The current innermost observable in the hierarchy. If some observable or its value were null, this contains null
	 * as well.
	 *
	 * @return the innermost {@link ObservableValue}
	 */
	ReadOnlyProperty<O> innerProperty();

}
