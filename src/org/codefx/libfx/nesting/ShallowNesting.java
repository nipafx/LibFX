package org.codefx.libfx.nesting;

import java.util.Objects;
import java.util.Optional;

import javafx.beans.Observable;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * An implementation of {@link Nesting} which solely consists of an outer {@link Observable}.
 *
 * @param <O>
 *            the outer type of {@link Observable}
 */
final class ShallowNesting<O extends Observable> implements Nesting<O> {

	/**
	 * The property holding the current inner observable, which is always the outer observable specified during
	 * construction.
	 */
	private final ReadOnlyProperty<Optional<O>> inner;

	/**
	 * Creates a new shallow nesting whose {@link #innerObservable} property always holds the specified outer
	 * observable.
	 *
	 * @param outerObservable
	 *            the {@link Observable} on which this nesting depends
	 */
	public ShallowNesting(O outerObservable) {
		Objects.requireNonNull(outerObservable, "The argument 'outerObservable' must not be null.");
		Optional<O> optionalInner = Optional.of(outerObservable);
		inner = new SimpleObjectProperty<>(this, "inner", optionalInner);
	}

	@Override
	public ReadOnlyProperty<Optional<O>> innerObservable() {
		return inner;
	}

}
